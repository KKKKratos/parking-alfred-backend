package com.alfred.parkingalfred.repository;

import com.alfred.parkingalfred.entity.Employee;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public  class EmployeeRepositoryTest {

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private TestEntityManager testEntityManager;
  private List<Employee> employeeList;

  @Before
  public  void initData(){
    Employee employee1 = new Employee();
    employee1.setName("e1");
    Employee employee2 = new Employee();
    employee1.setName("e2");
    Employee employee3 = new Employee();
    employee1.setName("e3");

   testEntityManager.persistAndFlush(employee1);
   testEntityManager.persistAndFlush(employee2);
   testEntityManager.persistAndFlush(employee3);
  }
  @Test
  public void should_return_employee_page_when_call_findAll_By_Pageable(){
    Page<Employee> page = employeeRepository.findAll(PageRequest.of(0,3));
    Assert.assertEquals(3,page.getContent().size());
  }

}