package pers.yewin.jobinterview.model.pojo;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.model.pojo
 */

public enum OrderType {
    DESC("DESC"), ASC("ASC");
    private final String value;

    public String getValue() {
        return value;
    }

    OrderType(String value){
        this.value = value;
    }
}
