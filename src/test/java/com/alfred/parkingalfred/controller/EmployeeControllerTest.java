package com.alfred.parkingalfred.controller;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.entity.Order;
import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.enums.RoleEnum;
import com.alfred.parkingalfred.exception.EmployeeNotExistedException;
import com.alfred.parkingalfred.form.EmployeeForm;
import com.alfred.parkingalfred.service.EmployeeService;
import com.alfred.parkingalfred.service.OrderService;
import com.alfred.parkingalfred.service.ParkingLotService;
import com.alfred.parkingalfred.utils.JwtUtil;

import com.alfred.parkingalfred.vo.EmployeeVO;

import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private OrderService orderService;
    @MockBean
    private ParkingLotService parkingLotService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void should_return_ok_when_login_with_correct_account() throws Exception {
        String mail = "mail";
        String password = "password";
        Employee loginEmployee = new Employee();
        loginEmployee.setMail(mail);
        loginEmployee.setPassword(password);

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setRole(RoleEnum.PARKING_BOY.getCode());
        when(employeeService.getEmployeeByMailAndPassword(anyString(), anyString()))
                .thenReturn(employee);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginEmployee)))
                .andExpect(status().isOk());
    }

    @Test
    public void should_return_404_when_login_with_invalid_account() throws Exception {
        String mail = "mail";
        String password = "password";
        Employee loginEmployee = new Employee();
        loginEmployee.setMail(mail);
        loginEmployee.setPassword(password);

        when(employeeService.getEmployeeByMailAndPassword(anyString(), anyString())).thenThrow(
                new EmployeeNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginEmployee)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_return_parkingLots_when_call_getParkingLotsByEmployeeId_API_with_true_param()
            throws Exception {
        ParkingLot parkingLot = new ParkingLot((long) 1, "test lot", 100, 100);
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        String token = JwtUtil.generateToken(employee);
        when(parkingLotService.getParkingLotsByParkingBoyId(employeeId))
                .thenReturn(Collections.singletonList(parkingLot));
        mockMvc.perform(get("/employees/{employeeId}/parking-lots/", employeeId)
                .header("Authorization", "Bearer " + token)
        ).andExpect(status().isOk());
    }
    @Test
    public void should_return_orders_when_call_getOrdersByEmployeeId_API_with_true_param()
        throws Exception {
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        String token = JwtUtil.generateToken(employee);
        Order order = new Order("1", 1,  "A", 2, employee);
        when(orderService.getOrdersByEmployeeId(anyLong()))
            .thenReturn(Collections.singletonList(order));
        mockMvc.perform(get("/employees/{employeeId}/orders/", employeeId)
            .header("Authorization", "Bearer " + token)
        ).andExpect(status().isOk());
    }

    @Test
    public void should_return_true_when_call_getStatusOfEmployeeById_API_with_true_param()
            throws Exception {
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        String token = JwtUtil.generateToken(employee);
        when(employeeService.doesEmployeeHasNotFullParkingLots(employeeId)).thenReturn(true);
        mockMvc.perform(get("/employees/{employeeId}/parking-lots/", employeeId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void should_return_employee_when_get_self_employee() throws Exception {
        Long employeeId = 1L;
        RoleEnum role = RoleEnum.PARKING_BOY;
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setRole(role.getCode());

        when(employeeService.getEmployeeById(employeeId)).thenReturn(employee);
        String token = JwtUtil.generateToken(employee);

        mockMvc.perform(get("/employee", employeeId)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void should_return_EmployeeVO_List_when_call_getEmployees_API() throws Exception {
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        String token = JwtUtil.generateToken(employee);
        List<EmployeeVO> employeeVOList = new ArrayList<>();
        employeeVOList.add(new EmployeeVO());
        when(employeeService.getAllEmployeesByPageAndSize(1, 3,new EmployeeVO())).thenReturn(employeeVOList);
        when(employeeService.getEmployeeCount(null)).thenReturn(1);
        mockMvc.perform(get("/employees", 1, 3, 1)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void should_return_EmployeeVO_when_call_create_Employees_API() throws Exception {
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        String token = JwtUtil.generateToken(employee);

        EmployeeForm employeeForm = new EmployeeForm();
        employeeForm.setPassword("123456");
        employeeForm.setMail("dsadsa@qq.com");
        employeeForm.setStatus(1);

        EmployeeVO employeeVO = new EmployeeVO();
        BeanUtils.copyProperties(employeeForm, employeeVO);
        employeeVO.setId(1L);
        when(employeeService.createEmployee(employeeForm)).thenReturn(employeeVO);

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(employeeForm))
                .header("Authorization", "Bearer " + token)
        ).andExpect(status().isOk());
    }

    @Test
    public void should_add_parking_lots_to_employee() throws Exception {
        Long id = 1L;
        Employee employee = new Employee();
        employee.setId(id);
        employee.setParkingLots(new ArrayList<>());
        List<Integer> parkingLotIdList = new ArrayList<Integer>() {{
            add(1);
            add(2);
        }};

        when(employeeService.updateEmployeeParkingLots(anyLong(), anyList())).thenReturn(employee);

        mockMvc.perform(put("/employees/{employeeId}/parking-lots", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(parkingLotIdList))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_return_employee_to_call_updateEmployee_with_true_param() throws Exception {
        Long id = 1L;
        EmployeeVO employeeVO = new EmployeeVO();
        employeeVO.setId(id);
        employeeVO.setStatus(2);
        employeeVO.setTelephone("2312");
        employeeVO.setName("21312");
        employeeVO.setRole(2);
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeVO,employee);
        when(employeeService.updateEmployee(anyLong(), any(EmployeeVO.class))).thenReturn(employee);
        mockMvc.perform(put("/employees/{employeeId}", id)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(employeeVO))
          .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
    }
}