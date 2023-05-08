package pers.yewin.jobinterview.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.model.pojo
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterTypeCount {
    private int job;
    private int salary;
    private int gender;
    private int unknown;
}
