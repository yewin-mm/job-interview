package pers.yewin.jobinterview.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.model
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobResultResponse {
    Object jobResult;
    int pageNumber;
    int pageSize;
    boolean hasNextPage;
    int totalPages;

}
