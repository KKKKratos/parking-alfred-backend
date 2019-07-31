package com.alfred.parkingalfred.repository;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.vo.EmployeeVO;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EmployeeRepositoryImpl {
  @Autowired
  private  EntityManager entityManager;
  public List<Employee> getAllByQueryWithPageAndEmployeeVO(Integer page, Integer size,
      EmployeeVO employeeVO){
    StringBuffer querySql = new StringBuffer("SELECT * FROM employee e where 1=1 ");
    if (!StringUtils.isEmpty(employeeVO.getName())){
      querySql.append(" and e.name like CONCAT('%' ,"+employeeVO.getName()+", '%')");
    }
    if (!StringUtils.isEmpty(employeeVO.getTelephone())){
      querySql.append(" and e.telephone like CONCAT('%' ,'"+employeeVO.getTelephone()+"', '%')");
    }
    if (employeeVO.getRole()!=null){
      querySql.append(" and e.role like CONCAT('%' ,"+employeeVO.getRole()+", '%')");
    }
    int start = (page-1)>0?page-1:0;
    int offset = size;
    querySql.append(" limit "+start+","+offset);
    Query query = entityManager.createNativeQuery(querySql.toString() , Employee.class);
    List<Employee> list = query.getResultList();
    return list;
  }
}
