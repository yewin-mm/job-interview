package pers.yewin.jobinterview.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.yewin.jobinterview.service.impl.ManualJobService;
import pers.yewin.jobinterview.service.impl.StaticJobService;

import static pers.yewin.jobinterview.uitl.ConstantUtil.MANUAL_JOB;
import static pers.yewin.jobinterview.uitl.ConstantUtil.STATIC_JOB;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.service
 */

@Service
public class JobFactory {

    @Autowired
    private StaticJobService staticJobService;

    @Autowired
    private ManualJobService manualJobService;

    public JobService getJobType(String jobType){
        JobService jobService;

        if(jobType.equals(STATIC_JOB)){
            jobService = staticJobService;

        }else if(jobType.equals(MANUAL_JOB)){
            jobService = manualJobService;
        }
        // here you can have other job type, that is factory pattern and code are decouple.

        else
            return null;

        return jobService;

    }
}
