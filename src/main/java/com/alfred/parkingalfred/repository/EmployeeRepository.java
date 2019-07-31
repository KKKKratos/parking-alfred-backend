package com.alfred.parkingalfred.repository;

import com.alfred.parkingalfred.entity.Employee;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

  Employee findByMailAndPassword(String name, String password);

  Page<Employee> findAll(Pageable pageable);

  @Query(value = "select count(1) from employee e where e.role = IFNULL(?1,e.role) ", nativeQuery = true)
  int getEmployeeCount(Integer role);

  Page<Employee> findAllByRole(Integer role, Pageable pageable);
}
