package pers.yewin.jobinterview.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pers.yewin.jobinterview.model.entity.SalaryInfo;
import pers.yewin.jobinterview.service.impl.StaticJobService;

import static helper.CommonUtil.printError;
import static helper.CommonUtil.printInfo;
import static helper.ConstantUtil.ERROR_PARAM_MESSAGE;

/**
 * author: Ye Win,
 * project: config,
 * package: pers.yewin.jobinterview.config
 */

@Service
@Slf4j
public class LoadConfig {

    @Autowired
    StaticJobService jobService;

    @Autowired
    PropertiesLoader loader;

    @EventListener
    public void loadEvent(ContextRefreshedEvent event) {
        try {

            printInfo("Loading properties configs");
            loader.cache();
            printInfo("Loading properties configs was finished");

            printInfo("Loading static job configs");
            jobService.addJobData(new SalaryInfo());
            printInfo("Loading static job configs was finished");

        }catch (Exception e){
            e.printStackTrace();
            printError("!!!Error in loading configs!!!");
            printError(ERROR_PARAM_MESSAGE, e.getMessage());
        }
    }
}
