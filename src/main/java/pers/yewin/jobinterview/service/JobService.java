package pers.yewin.jobinterview.service;

import org.springframework.http.ResponseEntity;
import pers.yewin.jobinterview.model.entity.SalaryInfo;
import pers.yewin.jobinterview.model.request.JobDataRequest;
import pojo.ServiceResponse;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.cotroller
 */

public interface JobService {

    void addJobData(SalaryInfo salaryInfo);
    ResponseEntity<ServiceResponse> getJobData(JobDataRequest request);

}
