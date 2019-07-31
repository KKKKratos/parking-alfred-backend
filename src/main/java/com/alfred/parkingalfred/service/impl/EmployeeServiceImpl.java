package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.converter.EmployeeToEmployeeVOConverter;
import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.EmployeeNotExistedException;
import com.alfred.parkingalfred.form.EmployeeForm;
import com.alfred.parkingalfred.repository.EmployeeRepository;
import com.alfred.parkingalfred.repository.EmployeeRepositoryImpl;
import com.alfred.parkingalfred.repository.ParkingLotRepository;
import com.alfred.parkingalfred.service.EmployeeService;
import com.alfred.parkingalfred.utils.EncodingUtil;
import com.alfred.parkingalfred.utils.UUIDUtil;
import com.alfred.parkingalfred.vo.EmployeeVO;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final ParkingLotRepository parkingLotRepository;

    private final ApplicationEventPublisher publisher;

    private final EmployeeRepositoryImpl employeeRepositoryImpl;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               ParkingLotRepository parkingLotRepository, ApplicationEventPublisher publisher,
                               EmployeeRepositoryImpl employeeRepositroyImpl) {
        this.employeeRepository = employeeRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.publisher = publisher;
        this.employeeRepositoryImpl = employeeRepositroyImpl;
    }

    @Override
    public Employee getEmployeeByMailAndPassword(String mail, String password) {
        String encodedPassword = EncodingUtil.encodingByMd5(password);
        Employee employee = employeeRepository.findByMailAndPassword(mail, encodedPassword);
        if (employee == null) {
            throw new EmployeeNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED);
        }
        return employee;
    }

    @Override
    public boolean doesEmployeeHasNotFullParkingLots(Long employeeId) {
        employeeRepository.findById(employeeId).orElseThrow(() ->
                new EmployeeNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
        int result = parkingLotRepository.findALLNotFullParkingLotRowsByEmployeeId(employeeId);
        return result > 0;
    }

    @Override
    public List<EmployeeVO> getAllEmployeesByPageAndSize(Integer page, Integer size, EmployeeVO employeeVO) {
        List<EmployeeVO> employeeVOList = EmployeeToEmployeeVOConverter.convert(
                employeeRepositoryImpl.getAllByQueryWithPageAndEmployeeVO(page, size, employeeVO));
        return employeeVOList;
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
    }

    @Override
    public EmployeeVO createEmployee(EmployeeForm employeeForm) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeForm, employee);
        Employee employeeEmail = new Employee();
        BeanUtils.copyProperties(employeeForm, employeeEmail);
        String password = UUIDUtil.generateUUID()
                .replace("-", "").substring(0, 8);
        employeeEmail.setPassword(password);
        publisher.publishEvent(employeeEmail);
        employee.setPassword(EncodingUtil.encodingByMd5(password));
        Employee employeeResult = employeeRepository.save(employee);
        EmployeeVO employeeVOResult = new EmployeeVO();
        BeanUtils.copyProperties(employeeResult, employeeVOResult);
        return employeeVOResult;
    }

    @Override
    public int getEmployeeCount(Integer role) {
        return employeeRepository.getEmployeeCount(role);
    }

    @Override
    @Transactional
    public Employee updateEmployeeParkingLots(Long employeeId, List<Long> parkingLotIdList) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
        List<ParkingLot> employeeParkingLots = employee.getParkingLots();
        List<ParkingLot> parkingLots = parkingLotRepository.findAllByIdIn(parkingLotIdList);
        employeeParkingLots.clear();
        employeeParkingLots.addAll(parkingLots);
        employeeRepository.saveAndFlush(employee);
        return employee;
    }

    @Override
    public Employee updateEmployee(Long id,EmployeeVO employeeVO) {
        Employee employee = employeeRepository.findById(id)
          .orElseThrow(() -> new EmployeeNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
        if (employeeVO.getRole()!=null){
         employee.setRole(employeeVO.getRole());
        }
        if (employeeVO.getName()!=null){
            employee.setName(employeeVO.getName());
        }
        if (employeeVO.getTelephone()!=null){
            employee.setTelephone(employeeVO.getTelephone());
        }
        if (employeeVO.getStatus()!=null){
            employee.setStatus(employeeVO.getStatus());
        }
        return employeeRepository.save(employee);
    }
}
