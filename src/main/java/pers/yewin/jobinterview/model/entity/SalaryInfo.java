package pers.yewin.jobinterview.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * author: Ye Win,,
 * project: job-interview,
 * package: pers.yewin.jobinterview.entity
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "salary_info")
public class SalaryInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String employer;
    private String location;
    private String jobTitle;
    private String yearsAtEmployer;
    private String yearsOfExperience;
    private double salary;
    private String singingBonus;
    private String annualBonus;
    private String annualStockValue;
    private String gender;

    @Lob // declare is lob column type to prevent long data
    @Column(name="additional_comment", length=512)
    private String additionalComment;
    private String timestamp;
    private String createdDate;
    private String updatedDate; // to add new date when there some field was update
    private String addedType; // to know the type is file loaded (static) or manual insert
    private boolean deleted; // to handle for soft delete

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SalaryInfo other = (SalaryInfo) obj;
        if (timestamp == null) {
            return other.timestamp == null;
        } else return timestamp.equals(other.timestamp);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
