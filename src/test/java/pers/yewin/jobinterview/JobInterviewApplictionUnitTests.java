package pers.yewin.jobinterview;

import helper.ResponseUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import pers.yewin.jobinterview.cotroller.StaticJobController;
import pers.yewin.jobinterview.model.request.JobDataRequest;
import pers.yewin.jobinterview.model.response.JobResultResponse;
import pojo.ServiceResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static helper.ConstantUtil.SUCCESS_MESSAGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pers.yewin.jobinterview.uitl.ConstantUtil.THAI_ZONE_ID;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview
 */

@WebMvcTest
public class JobInterviewApplictionUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // tell spring context to static job service as mock bean
    private StaticJobController jobController;

    @Test
    public void testGetJobData() throws Exception {

        List<String> sortFields = Arrays.asList("job_title,asc");

        JobResultResponse jobResult = new JobResultResponse(null, 0, 10, true, 100);

        ResponseEntity<ServiceResponse> response = null;
        ServiceResponse serviceResponse = ResponseUtil.getResponseObj(SUCCESS_MESSAGE, "", jobResult, THAI_ZONE_ID);
        response = new ResponseEntity<>(serviceResponse, HttpStatus.OK);

        Mockito.when(jobController.getJobData("0", "10000", "job_title,salary,gender", sortFields, "0", "10")).thenReturn(response);
        mockMvc.perform(get("/staticJob/getJobData")).andExpect(status().isOk());
    }

}
