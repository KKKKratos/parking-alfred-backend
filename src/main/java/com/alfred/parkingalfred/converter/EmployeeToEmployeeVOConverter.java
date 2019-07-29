package com.alfred.parkingalfred.converter;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.vo.EmployeeVO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;

/**
 * @Auther: Gio (zhangrun macmanboy@foxmail.com)
 * @Date: 2019/7/29 11:40
 * @Description:
 */
public class EmployeeToEmployeeVOConverter {
  public static EmployeeVO convert(Employee employee){
    EmployeeVO employeeVO = new EmployeeVO();
    BeanUtils.copyProperties(employee,employeeVO);
    return  employeeVO;
  }
  public static List<EmployeeVO> convert(List<Employee> employeeList){
    return  employeeList.stream().map(e->convert(e)).collect(Collectors.toList());
  }
}
