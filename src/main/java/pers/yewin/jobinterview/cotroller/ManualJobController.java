package pers.yewin.jobinterview.cotroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.cotroller
 */

@RestController
@RequestMapping("/manualJob")
public class ManualJobController {

    /**
     * Here, you can implement if you want to store manual job data by calling api instead of reading from file and get job data.
     * You can just use FactoryClass to route ManualJobService eg. JobService jobService = jobFactory.getJobType(MANUAL_JOB);
     */

    // create post method for storing job data by manual api call
    // I've already setup for added_type in SalaryInfo entity and just add "manual" type in there if you store data.

    // create get method for retrieving job data


}
