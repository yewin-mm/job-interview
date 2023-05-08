package pers.yewin.jobinterview.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import helper.DateTimeUtil;
import helper.ResponseUtil;
import helper.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pers.yewin.jobinterview.config.PropertiesLoader;
import pers.yewin.jobinterview.model.dto.JobGenderDTO;
import pers.yewin.jobinterview.model.dto.JobSalaryDTO;
import pers.yewin.jobinterview.model.dto.JobSalaryGenderDTO;
import pers.yewin.jobinterview.model.dto.SalaryGenderDTO;
import pers.yewin.jobinterview.model.entity.SalaryInfo;
import pers.yewin.jobinterview.model.pojo.FilterTypeCount;
import pers.yewin.jobinterview.model.pojo.OrderType;
import pers.yewin.jobinterview.model.pojo.SalaryJson;
import pers.yewin.jobinterview.model.pojo.SortBy;
import pers.yewin.jobinterview.model.request.JobDataRequest;
import pers.yewin.jobinterview.model.response.JobResultResponse;
import pers.yewin.jobinterview.repository.SalaryRepository;
import pers.yewin.jobinterview.service.JobService;
import pers.yewin.jobinterview.uitl.ResourceUtil;
import pers.yewin.jobinterview.uitl.SalaryCalculator;
import pojo.ServiceResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static helper.CommonUtil.printWarn;
import static helper.ConstantUtil.*;
import static helper.ValidationUtil.isEmptyCollection;
import static pers.yewin.jobinterview.uitl.ConstantUtil.*;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.service.impl
 */

@Service
@Slf4j
public class StaticJobService implements JobService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    SalaryRepository salaryRepo;

    @Value("${app.constant.jobDataFilePath}")
    String jobDataPath;

    @Value("${app.constant.jobDataFileName}")
    String jobDataFileName;

    @Autowired
    private PropertiesLoader propertiesLoader;


    // this method is to store data into database by reading file
    @Override
    public void addJobData(SalaryInfo info) {

        // we don't use the SalaryInfo param as this method is static loading from file

        /**
         * there are two data set files under /resources/jobinfo-data/ as input.
         * those files are equal, So, I will go with json file.
         */

        String filePath = jobDataPath + File.separator + jobDataFileName;
        List<SalaryJson> salaryJsonList = Arrays.asList(
                getFromResource(filePath, SalaryJson[].class));

        if(isEmptyCollection(salaryJsonList, "file data")){
            return;
        }

        log.info("file loaded size: {}", salaryJsonList.size());

        List<SalaryInfo> salaryInfoList = migrateData(salaryJsonList);
        log.info("salaryInfo file loaded size: {}", salaryInfoList.size());


        List<SalaryInfo> salaryDBList = salaryRepo.findByAddedTypeAndDeleted(STATIC_TYPE, false);
        log.info("salaryInfo db size: {}", salaryDBList.size());

        if(isEmptyCollection(salaryDBList)){
            log.info("static job data in db is empty");
            salaryRepo.saveAll(salaryInfoList);

        }else {

            // below condition is to reduce unnecessary looping.
            // if db size is equal or higher than the file data size, I will skip to loop in below step as to increase performance.
            if (salaryDBList.size() >= salaryInfoList.size()) {
                log.info("no new data");
                return;
            }

            storeData(salaryInfoList, salaryDBList);

        }

        log.info("Finish loadSalary()");

    }

    // storing into database
    private void storeData(List<SalaryInfo> salaryInfoList, List<SalaryInfo> salaryDBList){
        List<SalaryInfo> salaryToPersist = new ArrayList<>();

        // checking process for new data
        for (SalaryInfo salaryInfo : salaryInfoList) {
            boolean newInfo = true;
            for (SalaryInfo salaryDB : salaryDBList) {
                if (salaryInfo.equals(salaryDB)) { // this is override method and I did check with timestamp as imagine, it's non-null value.
                    newInfo = false;
                    break;
                }
            }
            if (newInfo) {
                log.info("New Salary Information - {} - {} - {} is loaded.", salaryInfo.getTimestamp(), salaryInfo.getSalary(), salaryInfo.getJobTitle());
                salaryToPersist.add(salaryInfo);
            }
        }

        if (!isEmptyCollection(salaryToPersist)) {
            log.info("new data size: {}", salaryToPersist.size());
            salaryRepo.saveAll(salaryToPersist);
        }
    }


    // this method is to get job data as per user input
    @Override
    public ResponseEntity<ServiceResponse> getJobData(JobDataRequest request) {
        try {
            List<Sort.Order> orders = getSortBy(request.getSortFields()==null?new ArrayList<>():request.getSortFields());
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(orders));

            String filter = getFilter(request);

            JobResultResponse jobResult = retrieveData(filter, request, pageable);

            // here, ServiceResponse is my customize microservice response object and I embedded my library in this project.
            ServiceResponse response =  ResponseUtil.getResponseObj(SUCCESS_MESSAGE, "", jobResult, THAI_ZONE_ID);

            return ResponseEntity.ok(response);


        }catch (Exception e){
            e.printStackTrace();
            ServiceResponse response = ResponseUtil.getResponseObj(FAIL_MESSAGE, e.getMessage(),null, THAI_ZONE_ID);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }



    // this method is to get sort by
    private List<Sort.Order> getSortBy(List<String> sortFields){

        if(isEmptyCollection(sortFields))
            sortFields.add("jobTitle,desc"); // default order


        if(sortFields.size()==2){ // to encounter if only one sort type was come, list size will be 2, and we need to combine as comma
            String order = sortFields.get(1);
            String field = sortFields.get(0);
            if(order.equalsIgnoreCase(OrderType.ASC.getValue()) || order.equalsIgnoreCase(OrderType.DESC.getValue())){
                sortFields.remove(1);
                sortFields.add(0, field +","+order);
            }
        }

        return sortFields.stream().map(sortField -> {
            String[] split = sortField.split(COMMA_REGEX);
            String field = split[0];
            if(!isValidFields(field)){
                return null;
            }
            field = checkJobTitle(field);
            if (split.length == 2 && "asc".equalsIgnoreCase(split[1])) {
                return Sort.Order.asc(field);
            } else if (split.length == 2 && "desc".equalsIgnoreCase(split[1])) {
                return Sort.Order.desc(field);
            } else {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());


    }

    private String checkJobTitle(String field){
        if(field.equalsIgnoreCase(SortBy.JOB_TITLE.getValue()) || field.equalsIgnoreCase(SortBy.JOB_TITLE_FIELD.getValue())){
            return SortBy.JOB_TITLE_FIELD.getValue();
        }
        return field;
    }

    private boolean isValidFields(String value){
        if(value.equalsIgnoreCase(SortBy.JOB_TITLE.getValue()) || value.equalsIgnoreCase(SortBy.JOB_TITLE_FIELD.getValue()) )
            return true;
        else if(value.equalsIgnoreCase(SortBy.SALARY.getValue()))
            return true;
        else return value.equalsIgnoreCase(SortBy.GENDER.getValue());
    }

    // this method is to get filter list
    private String getFilter(JobDataRequest request){
        if(ValidationUtil.isEmptyString(request.getFilterFields())){
            return ALL;
        }
        String [] filterList = request.getFilterFields().split(COMMA_REGEX);
        if(filterList.length==1) {
            FilterTypeCount typeCount = new FilterTypeCount(0,0,0,0);
            getCount(request.getFilterFields(), typeCount);
            return getSingleFilter(typeCount);

        }else if(filterList.length==2){
            FilterTypeCount typeCount = new FilterTypeCount(0,0,0,0);
            for(String filter: filterList){
                getCount(filter, typeCount);
            }
            return get2FilterType(typeCount);

        }else if(filterList.length==3){
            FilterTypeCount typeCount = new FilterTypeCount(0,0,0,0);
            for(String filter: filterList){
                getCount(filter, typeCount);
            }
            return get3FilterType(typeCount);

        }else {
            FilterTypeCount typeCount = new FilterTypeCount(0,0,0,0);
            for(String filter: filterList){
                getCount(filter, typeCount);
            }
            return getManyFilterType(typeCount);
        }

    }

    // this method is to calculate how many text are repeated
    private void getCount(String filter, FilterTypeCount typeCount){

        if (filter.equalsIgnoreCase(SortBy.JOB_TITLE.getValue()) || filter.equalsIgnoreCase(SortBy.JOB_TITLE_FIELD.getValue())) {
            typeCount.setJob(typeCount.getJob() + 1);
        } else if (filter.equalsIgnoreCase(SortBy.SALARY.getValue())) {
            typeCount.setSalary(typeCount.getSalary() + 1);
        } else if (filter.equalsIgnoreCase(SortBy.GENDER.getValue())) {
            typeCount.setGender(typeCount.getGender() + 1);
        }else
            typeCount.setUnknown(typeCount.getUnknown() + 1);
    }

    private String getSingleFilter(FilterTypeCount typeCount) {
        if(typeCount.getJob() > 0)
            return JOB;
        if(typeCount.getSalary() > 0 )
            return SALARY;
        if(typeCount.getGender() > 0)
            return GENDER;

        return UNKNOWN; // if we can't recognize the filter, we will go with all fields display
    }

    private String get2FilterType(FilterTypeCount typeCount) {
        if(typeCount.getJob() == 1 && typeCount.getSalary() == 1)
            return JOB_AND_SALARY;
        if(typeCount.getJob() == 1 && typeCount.getGender() == 1)
            return JOB_AND_GENDER;
        if(typeCount.getSalary() == 1 && typeCount.getGender() == 1)
            return SALARY_AND_GENDER;

        return getSingleFilter(typeCount);
    }

    private String get3FilterType(FilterTypeCount typeCount) {
        if(typeCount.getJob() == 1 && typeCount.getSalary() == 1 && typeCount.getGender() == 1)
            return ALL;

        if(typeCount.getJob() == 1 && typeCount.getSalary() == 1 && typeCount.getGender() == 0)
            return JOB_AND_SALARY;

        if(typeCount.getJob() == 1 && typeCount.getSalary() == 0 && typeCount.getGender() == 1)
            return JOB_AND_GENDER;

        if(typeCount.getJob() == 0 && typeCount.getSalary() == 1 && typeCount.getGender() == 1)
            return SALARY_AND_GENDER;

        return getSingleFilter(typeCount);
    }

    private String getManyFilterType(FilterTypeCount typeCount) {
        if(typeCount.getJob() > 0 && typeCount.getSalary() > 0 && typeCount.getGender() > 0)
            return ALL;

        if(typeCount.getJob() > 0 && typeCount.getSalary() > 0 && typeCount.getGender() == 0)
            return JOB_AND_SALARY;

        if(typeCount.getJob() > 0 && typeCount.getSalary() == 0 && typeCount.getGender() > 0)
            return JOB_AND_GENDER;

        if(typeCount.getJob() == 0 && typeCount.getSalary() > 1 && typeCount.getGender() > 1)
            return SALARY_AND_GENDER;

        return getSingleFilter(typeCount);
    }

    // retrieving data from database as per input filer and request
    private JobResultResponse retrieveData(String filter, JobDataRequest request, Pageable pageable){
        JobResultResponse jobResult;
        switch (filter) {
            case JOB_AND_SALARY:
                Page<JobSalaryDTO> jobSalaryDTOPage = salaryRepo.findJobAndSalaryBySalary(request.getMinEqSalary(), request.getMaxEqSalary(), STATIC_TYPE, false, pageable);
                jobResult = new JobResultResponse(jobSalaryDTOPage.getContent(), jobSalaryDTOPage.getNumber(), jobSalaryDTOPage.getSize(), jobSalaryDTOPage.hasNext(), jobSalaryDTOPage.getTotalPages());
                break;
            case JOB_AND_GENDER:
                Page<JobGenderDTO> jobGenderDTOPage = salaryRepo.findJobAndGenderBySalary(request.getMinEqSalary(), request.getMaxEqSalary(), STATIC_TYPE, false, pageable);
                jobResult = new JobResultResponse(jobGenderDTOPage.getContent(), jobGenderDTOPage.getNumber(), jobGenderDTOPage.getSize(), jobGenderDTOPage.hasNext(), jobGenderDTOPage.getTotalPages());
                break;
            case SALARY_AND_GENDER:
                Page<SalaryGenderDTO> salaryGenderDTOPage = salaryRepo.findSalaryAndGenderBySalary(request.getMinEqSalary(), request.getMaxEqSalary(), STATIC_TYPE, false, pageable);
                jobResult = new JobResultResponse(salaryGenderDTOPage.getContent(), salaryGenderDTOPage.getNumber(), salaryGenderDTOPage.getSize(), salaryGenderDTOPage.hasNext(), salaryGenderDTOPage.getTotalPages());
                break;
            case JOB:
                Page<String> jobDTOPage = salaryRepo.findJobBySalary(request.getMinEqSalary(), request.getMaxEqSalary(), STATIC_TYPE, false, pageable);
                jobResult = new JobResultResponse(jobDTOPage.getContent(), jobDTOPage.getNumber(), jobDTOPage.getSize(), jobDTOPage.hasNext(), jobDTOPage.getTotalPages());
                break;
            case SALARY:
                Page<Double> salaryDTOPage = salaryRepo.findSalaryBySalary(request.getMinEqSalary(), request.getMaxEqSalary(), STATIC_TYPE, false, pageable);
                jobResult = new JobResultResponse(salaryDTOPage.getContent(), salaryDTOPage.getNumber(), salaryDTOPage.getSize(), salaryDTOPage.hasNext(), salaryDTOPage.getTotalPages());
                break;
            case GENDER:
                Page<String> genderDTOPage = salaryRepo.findGenderBySalary(request.getMinEqSalary(), request.getMaxEqSalary(), STATIC_TYPE, false, pageable);
                jobResult = new JobResultResponse(genderDTOPage.getContent(), genderDTOPage.getNumber(), genderDTOPage.getSize(), genderDTOPage.hasNext(), genderDTOPage.getTotalPages());
                break;

            default: // if we can't recognize the filter, we will go with all fields display
                Page<JobSalaryGenderDTO> jobSalaryGenderDTOPage = salaryRepo.findAllBySalary(request.getMinEqSalary(), request.getMaxEqSalary(), STATIC_TYPE, false, pageable);
                jobResult = new JobResultResponse(jobSalaryGenderDTOPage.getContent(), jobSalaryGenderDTOPage.getNumber(), jobSalaryGenderDTOPage.getSize(), jobSalaryGenderDTOPage.hasNext(), jobSalaryGenderDTOPage.getTotalPages());
        }
        return jobResult;
    }



    private List<SalaryInfo> migrateData(List<SalaryJson> salaryJsonList) {
        List<SalaryInfo> infoList = new ArrayList<>();

        salaryJsonList.forEach(s -> infoList.add(new SalaryInfo(null, s.getEmployer(), s.getLocation(),
                s.getJobTitle(), s.getYearsAtEmployer(), s.getYearsOfExperience(),
                SalaryCalculator.calculateSalary(s.getSalary(),
                propertiesLoader.getCurrencyExchange(), propertiesLoader.getCurrencyList()),
                s.getSingingBonus(), s.getAnnualBonus(), s.getAnnualStockValue(), s.getGender(),
                s.getAdditionalComment(), s.getTimestamp(),
                DateTimeUtil.getCurrentDateTime(THAI_ZONE_ID), DateTimeUtil.getCurrentDateTime(THAI_ZONE_ID), STATIC_TYPE,
                false)));

        return infoList;

    }


    // here, I used the generic type T as other logic can apply this method too.
    public <T> T getFromResource(String resourceClasspathLocation, Class<T> type) {
        try {
            Resource resource = resourceLoader.getResource(resourceClasspathLocation);
            String data = ResourceUtil.read(resource.getInputStream());
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(data, type);
        } catch (IOException e) {
            e.printStackTrace();
            printWarn(ERROR_PARAM_MESSAGE, e.getMessage());
            printWarn("Cannot Load Input Data");
        }

        return null;
    }
}
