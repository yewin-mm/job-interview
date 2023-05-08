package pers.yewin.jobinterview.model.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.model.request
 */

@Data
@Builder
public class JobDataRequest {
    private double minEqSalary;
    private double maxEqSalary;
    private String filterFields;
    private List<String> sortFields;
    private int page;
    private int size;
}
