package pers.yewin.jobinterview.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import helper.DateTimeUtil;
import helper.ResponseUtil;
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
import pers.yewin.jobinterview.model.pojo.SalaryJson;
import pers.yewin.jobinterview.model.request.JobDataRequest;
import pers.yewin.jobinterview.model.response.GenderResponse;
import pers.yewin.jobinterview.model.response.JobResponse;
import pers.yewin.jobinterview.model.response.JobResultResponse;
import pers.yewin.jobinterview.model.response.SalaryResponse;
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

import static helper.CommonUtil.printWarn;
import static helper.ConstantUtil.*;
import static helper.ValidationUtil.isEmptyCollection;
import static pers.yewin.jobinterview.uitl.CommonUtil.getFilter;
import static pers.yewin.jobinterview.uitl.CommonUtil.getSortBy;
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
            ServiceResponse response;
            response  = getSortBy(request.getSortFields()==null?new ArrayList<>():request.getSortFields());
            if(!response.getStatus().getStatus().equals(SUCCESS_MESSAGE)){
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            List<Sort.Order> orders = (List<Sort.Order>) response.getData();
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(orders));

            String filter = getFilter(request);

            JobResultResponse jobResult = retrieveData(filter, request, pageable);

            // here, ServiceResponse is my customize microservice response object and I embedded my library in this project.
            response =  ResponseUtil.getResponseObj(SUCCESS_MESSAGE, "", jobResult, THAI_ZONE_ID);

            return ResponseEntity.ok(response);


        }catch (Exception e){
            e.printStackTrace();
            ServiceResponse response = ResponseUtil.getResponseObj(FAIL_MESSAGE, e.getMessage(),null, THAI_ZONE_ID);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }


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
                jobResult = new JobResultResponse(new JobResponse(jobDTOPage.getContent()), jobDTOPage.getNumber(), jobDTOPage.getSize(), jobDTOPage.hasNext(), jobDTOPage.getTotalPages());
                break;
            case SALARY:
                Page<Double> salaryDTOPage = salaryRepo.findSalaryBySalary(request.getMinEqSalary(), request.getMaxEqSalary(), STATIC_TYPE, false, pageable);
                jobResult = new JobResultResponse(new SalaryResponse(salaryDTOPage.getContent()), salaryDTOPage.getNumber(), salaryDTOPage.getSize(), salaryDTOPage.hasNext(), salaryDTOPage.getTotalPages());
                break;
            case GENDER:
                Page<String> genderDTOPage = salaryRepo.findGenderBySalary(request.getMinEqSalary(), request.getMaxEqSalary(), STATIC_TYPE, false, pageable);
                jobResult = new JobResultResponse(new GenderResponse(genderDTOPage.getContent()), genderDTOPage.getNumber(), genderDTOPage.getSize(), genderDTOPage.hasNext(), genderDTOPage.getTotalPages());
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
