package pers.yewin.jobinterview.model.pojo;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.model.pojo
 */

public enum SortBy {
    CREATE_DATE("createdDate"), JOB_TITLE_FIELD("jobTitle"), JOB_TITLE("job_title"), SALARY("salary"), GENDER("gender");
    private final String value;

    public String getValue() {
        return value;
    }

    SortBy(String value){
        this.value = value;
    }
}
