package pers.yewin.jobinterview.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.model.response
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenderResponse {
    private List<String> gender;
}
