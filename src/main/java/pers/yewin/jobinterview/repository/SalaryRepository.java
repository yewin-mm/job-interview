package pers.yewin.jobinterview.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pers.yewin.jobinterview.model.dto.*;
import pers.yewin.jobinterview.model.entity.SalaryInfo;

import java.util.List;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.repository
 */


@Repository
public interface SalaryRepository extends JpaRepository<SalaryInfo, Long> {

    List<SalaryInfo> findByAddedTypeAndDeleted(String type, boolean deleted);

    @Query("SELECT new pers.yewin.jobinterview.model.dto.JobSalaryGenderDTO(s.jobTitle, s.salary,s.gender) FROM SalaryInfo s WHERE "
            + "s.salary >= :minEqSalary and s.salary <= :maxEqSalary "
            + "and s.addedType=:type and s.deleted=:deleted")
    Page<JobSalaryGenderDTO> findAllBySalary(@Param("minEqSalary") double minEqSalary, @Param("maxEqSalary") double maxEqSalary,
                                          @Param("type") String type, @Param("deleted") boolean deleted, Pageable pageable);


    @Query("SELECT new pers.yewin.jobinterview.model.dto.JobSalaryDTO(s.jobTitle, s.salary) FROM SalaryInfo s WHERE "
            + "s.salary >= :minEqSalary and s.salary <= :maxEqSalary "
            + "and s.addedType=:type and s.deleted=:deleted")
    Page<JobSalaryDTO> findJobAndSalaryBySalary(@Param("minEqSalary") double minEqSalary, @Param("maxEqSalary") double maxEqSalary,
                                                @Param("type") String type, @Param("deleted") boolean deleted, Pageable pageable);


    @Query("SELECT new pers.yewin.jobinterview.model.dto.JobGenderDTO(s.jobTitle, s.gender) FROM SalaryInfo s WHERE "
            + "s.salary >= :minEqSalary and s.salary <= :maxEqSalary "
            + "and s.addedType=:type and s.deleted=:deleted")
    Page<JobGenderDTO> findJobAndGenderBySalary(@Param("minEqSalary") double minEqSalary, @Param("maxEqSalary") double maxEqSalary,
                                                @Param("type") String type, @Param("deleted") boolean deleted, Pageable pageable);


    @Query("SELECT new pers.yewin.jobinterview.model.dto.SalaryGenderDTO(s.salary, s.gender) FROM SalaryInfo s WHERE "
            + "s.salary >= :minEqSalary and s.salary <= :maxEqSalary "
            + "and s.addedType=:type and s.deleted=:deleted")
    Page<SalaryGenderDTO> findSalaryAndGenderBySalary(@Param("minEqSalary") double minEqSalary, @Param("maxEqSalary") double maxEqSalary,
                                                      @Param("type") String type, @Param("deleted") boolean deleted, Pageable pageable);


    @Query("SELECT s.jobTitle FROM SalaryInfo s WHERE "
            + "s.salary >= :minEqSalary and s.salary <= :maxEqSalary "
            + "and s.addedType=:type and s.deleted=:deleted")
    Page<String> findJobBySalary(@Param("minEqSalary") double minEqSalary, @Param("maxEqSalary") double maxEqSalary,
                                       @Param("type") String type, @Param("deleted") boolean deleted, Pageable pageable);


    @Query("SELECT s.salary FROM SalaryInfo s WHERE "
            + "s.salary >= :minEqSalary and s.salary <= :maxEqSalary "
            + "and s.addedType=:type and s.deleted=:deleted")
    Page<Double> findSalaryBySalary(@Param("minEqSalary") double minEqSalary, @Param("maxEqSalary") double maxEqSalary,
                                       @Param("type") String type, @Param("deleted") boolean deleted, Pageable pageable);

    @Query("SELECT s.gender FROM SalaryInfo s WHERE "
            + "s.salary >= :minEqSalary and s.salary <= :maxEqSalary "
            + "and s.addedType=:type and s.deleted=:deleted")
    Page<String> findGenderBySalary(@Param("minEqSalary") double minEqSalary, @Param("maxEqSalary") double maxEqSalary,
                                       @Param("type") String type, @Param("deleted") boolean deleted, Pageable pageable);



}
