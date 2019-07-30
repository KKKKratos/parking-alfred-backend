package com.alfred.parkingalfred.repository;

import com.alfred.parkingalfred.entity.Employee;
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
    employee1.setTelephone("1319");
    Employee employee2 = new Employee();
    employee2.setName("e2");
    employee2.setTelephone("1339");
    Employee employee3 = new Employee();
    employee3.setName("Q3");
    employee3.setTelephone("13919");

    for (int i = 4;i<=10;i++){
      Employee employee = new Employee();
      employee.setName("e"+i);
      employee.setTelephone("22"+i);
      testEntityManager.persistAndFlush(employee);
    }

   testEntityManager.persistAndFlush(employee1);
   testEntityManager.persistAndFlush(employee2);
   testEntityManager.persistAndFlush(employee3);
  }
  @Test
  public void should_return_employee_page_when_call_findAll_By_Pageable(){
    Page<Employee> page = employeeRepository.findAll(PageRequest.of(0,3));
    Assert.assertEquals(3,page.getContent().size());
  }

//  @Test
//  public void should_return_employee_page_when_call_findAllEmployeeByNameOrTelephone(){
//    List<Employee> employeeList = employeeRepository.findAllEmployeeByNameOrTelephone(null
//        ,null,"1","5");
//    Assert.assertEquals(2,employeeList.size());
//  }
}