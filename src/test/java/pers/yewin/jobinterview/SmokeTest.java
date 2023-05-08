package pers.yewin.jobinterview;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.yewin.jobinterview.cotroller.StaticJobController;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview
 */
@SpringBootTest
public class SmokeTest {

    @Autowired
    private StaticJobController controller;

    @Test
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
}
