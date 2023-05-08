package pers.yewin.jobinterview.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pers.yewin.jobinterview.model.entity.SalaryInfo;
import pers.yewin.jobinterview.model.request.JobDataRequest;
import pers.yewin.jobinterview.service.JobService;
import pojo.ServiceResponse;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.service.impl
 */

@Service
public class ManualJobService implements JobService {

    /**
     * Here, you can implement if you want to store manual job data by calling api instead of reading from file and get job data.
     */

    @Override
    public void addJobData(SalaryInfo salaryInfo) {

    }

    @Override
    public ResponseEntity<ServiceResponse> getJobData(JobDataRequest request) {
        return null;
    }


}
