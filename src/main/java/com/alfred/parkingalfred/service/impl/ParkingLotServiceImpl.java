package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.EmployeeNotExistedException;
import com.alfred.parkingalfred.form.ParkingLotForm;
import com.alfred.parkingalfred.repository.EmployeeRepository;
import com.alfred.parkingalfred.repository.ParkingLotRepository;
import com.alfred.parkingalfred.service.ParkingLotService;
import java.util.List;

import io.netty.util.internal.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {

  private final ParkingLotRepository parkingLotRepository;

  private final EmployeeRepository employeeRepository;

  public ParkingLotServiceImpl(ParkingLotRepository parkingLotRepository,
      EmployeeRepository employeeRepository) {
    this.parkingLotRepository = parkingLotRepository;
    this.employeeRepository = employeeRepository;
  }

  @Override
  public List<ParkingLot> getParkingLotsByParkingBoyId(Long parkingBoyId) {
    Employee employee = employeeRepository.findById(parkingBoyId).orElseThrow(() ->
        new EmployeeNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
    return employee.getParkingLots();
  }

  @Override
  public ParkingLot createParkingLot(ParkingLotForm parkingLotForm) {
    ParkingLot parkingLot = new ParkingLot();
    BeanUtils.copyProperties(parkingLotForm, parkingLot);
    return  parkingLotRepository.save(parkingLot);
  }

  @Override
  public List<ParkingLot> getAllParkingLotsWithFilterByPageAndSize(int page, int size, String name) {
    PageRequest pageRequest = PageRequest.of(page-1,size);
    if (StringUtil.isNullOrEmpty(name)) {
      name = StringUtil.EMPTY_STRING;
    }
    Page<ParkingLot> parkingLotPage = parkingLotRepository.findAllByNameLike("%" + name + "%", pageRequest);
    return parkingLotPage.getContent();
  }

  @Override
  public int getParkingLotCount() {
    return parkingLotRepository.getParkingLotCount();
  }
}
