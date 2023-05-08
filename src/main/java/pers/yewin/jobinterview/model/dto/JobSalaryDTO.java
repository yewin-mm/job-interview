package pers.yewin.jobinterview.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.model.response
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSalaryDTO {

    private String jobTitle;
    private double salary;
}
