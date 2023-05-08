package pers.yewin.jobinterview.cotroller;

import helper.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.yewin.jobinterview.model.entity.SalaryInfo;
import pers.yewin.jobinterview.model.pojo.SortBy;
import pers.yewin.jobinterview.model.request.JobDataRequest;
import pers.yewin.jobinterview.service.JobFactory;
import pers.yewin.jobinterview.service.JobService;
import pojo.ServiceResponse;

import java.util.List;

import static helper.CommonUtil.printError;
import static helper.ConstantUtil.*;
import static pers.yewin.jobinterview.uitl.CommonUtil.isValidNumber;
import static pers.yewin.jobinterview.uitl.ConstantUtil.*;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.cotroller
 */

@RestController
@RequestMapping("/staticJob")
@Slf4j
public class StaticJobController {

    @Autowired
    JobFactory jobFactory;

    /**
     * This method is for manual loading file if you changed something in file.
     * This is like refresh api.
     */
    @GetMapping("/loadRefresh")
    public ResponseEntity<String> loadRefresh(){
        log.info("inside loadRefresh method");
        try {
            JobService jobService = jobFactory.getJobType(STATIC_JOB);
            if (jobService != null) {
                jobService.addJobData(new SalaryInfo());
                return ResponseEntity.ok().build();
            }
        }catch (Exception e){
            e.printStackTrace();
            printError(ERROR_PARAM_MESSAGE, e.getMessage());
        }
        return ResponseEntity.internalServerError().build();
    }


    @GetMapping("/getJobData")
    public ResponseEntity<ServiceResponse> getJobData(@RequestParam(name = "salary(gte)", defaultValue = "0", required = false) String minEqSalary,
                                                      @RequestParam(name = "salary(lte)", defaultValue = "999999999", required = false) String maxEqSalary,
                                                      @RequestParam(name = "filter_fields", defaultValue = "job_title,salary,gender", required = false) String filterFields,
                                                      @RequestParam(name = "sort", required = false) List<String> sort,
                                                      @RequestParam(name = "page", defaultValue = "0", required = false) String page,
                                                      @RequestParam(name = "size", defaultValue = "10", required = false) String size) {

        log.info("inside getJobData method");
        try {

            ServiceResponse serviceValidation = inputValidation(minEqSalary, maxEqSalary, filterFields, page, size);
            if(serviceValidation.getStatus().getStatus().equals(FAIL_MESSAGE)){
                log.warn("Error at input: {}", serviceValidation);
                return new ResponseEntity<>(serviceValidation, HttpStatus.BAD_REQUEST);
            }

            JobService jobService = jobFactory.getJobType(STATIC_JOB);
            if (jobService != null) {
                JobDataRequest dataRequest = JobDataRequest.builder()
                                                        .minEqSalary(Double.parseDouble(minEqSalary)).maxEqSalary(Double.parseDouble(maxEqSalary))
                                                        .filterFields(filterFields)
                                                        .sortFields(sort)
                                                        .page(Integer.parseInt(page)).size(Integer.parseInt(size))
                                                        .build();
                log.info("input data: {}", dataRequest);
                return jobService.getJobData(dataRequest);
            }
        }catch (Exception e){
            e.printStackTrace();
            printError(ERROR_PARAM_MESSAGE, e.getMessage());
        }
        return ResponseEntity.internalServerError().build();
    }

    private ServiceResponse inputValidation(String minEqSalary, String maxEqSalary, String filterFields, String page, String size){
        ServiceResponse serviceResponse;
        if(!isValidNumber(minEqSalary) || !isValidNumber(maxEqSalary)){
            serviceResponse = ResponseUtil.getResponseObj(FAIL_MESSAGE, SALARY_INVALID, null, THAI_ZONE_ID);
            return serviceResponse;
        }


        if(!filterFields.contains(SortBy.JOB_TITLE.getValue()) && !filterFields.contains(SortBy.JOB_TITLE_FIELD.getValue()) &&
                !filterFields.contains(SortBy.SALARY.getValue()) && !filterFields.contains(SortBy.GENDER.getValue())){
            serviceResponse = ResponseUtil.getResponseObj(FAIL_MESSAGE, FILTER_INVALID, null, THAI_ZONE_ID);
            return serviceResponse;
        }


        if(!isValidNumber(page)){
            serviceResponse = ResponseUtil.getResponseObj(FAIL_MESSAGE, PAGE_INVALID, null, THAI_ZONE_ID);
            return serviceResponse;
        }
        if(!isValidNumber(size)){
            serviceResponse = ResponseUtil.getResponseObj(FAIL_MESSAGE, SIZE_INVALID, null, THAI_ZONE_ID);
            return serviceResponse;
        }

        return ResponseUtil.getResponseObj(SUCCESS_MESSAGE, "", null, THAI_ZONE_ID);
    }



}
