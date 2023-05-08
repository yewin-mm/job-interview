package pers.yewin.jobinterview.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.model.pojo
 */

@Data
public class SalaryJson {
    @JsonProperty("Timestamp")
    private String timestamp;
    @JsonProperty("Employer")
    private String employer;
    @JsonProperty("Location")
    private String location;
    @JsonProperty("Job Title")
    private String jobTitle;
    @JsonProperty("Years at Employer")
    private String yearsAtEmployer;
    @JsonProperty("Years of Experience")
    private String yearsOfExperience;
    @JsonProperty("Salary")
    private String salary;
    @JsonProperty("Signing Bonus")
    private String singingBonus;
    @JsonProperty("Annual Bonus")
    private String annualBonus;
    @JsonProperty("Annual Stock Value/Bonus")
    private String annualStockValue;
    @JsonProperty("Gender")
    private String gender;
    @JsonProperty("Additional Comments")
    private String additionalComment;
}
