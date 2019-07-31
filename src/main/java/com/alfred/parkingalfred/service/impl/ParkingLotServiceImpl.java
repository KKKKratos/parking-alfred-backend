package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.enums.ParkingLotStatusEnum;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.EmployeeNotExistedException;
import com.alfred.parkingalfred.exception.OrderNotExistedException;
import com.alfred.parkingalfred.form.ParkingLotForm;
import com.alfred.parkingalfred.repository.EmployeeRepository;
import com.alfred.parkingalfred.repository.ParkingLotRepository;
import com.alfred.parkingalfred.service.ParkingLotService;
import io.netty.util.internal.StringUtil;
import java.util.List;
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
    parkingLot.setStatus(ParkingLotStatusEnum.USABLE.getCode());
    return  parkingLotRepository.save(parkingLot);
  }

  @Override
  public List<ParkingLot> getAllParkingLotsWithFilterByPageAndSize(int page, int size, String name) {
    PageRequest pageRequest = PageRequest.of(page-1,size);
    if (StringUtil.isNullOrEmpty(name)) {
      name = StringUtil.EMPTY_STRING;
    }
    Page<ParkingLot> parkingLotPage = parkingLotRepository
      .findAllByNameLike("%" + name + "%", pageRequest);
    return parkingLotPage.getContent();
  }

  @Override
  public int getParkingLotCount() {
    return parkingLotRepository.getParkingLotCount();
  }

  @Override
  public ParkingLot updateParkingLotById(Long id, ParkingLot parkingLot) {
    ParkingLot parkingLotFinded = parkingLotRepository.findById(id)
            .orElseThrow(() -> new OrderNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
    updateParkingLot(parkingLot, parkingLotFinded);
    return parkingLotRepository.save(parkingLotFinded);
  }

  private void updateParkingLot(ParkingLot parkingLot, ParkingLot parkingLotFinded) {
    if(parkingLot.getName()!= null){
      parkingLotFinded.setName(parkingLot.getName());
    }
    if (parkingLot.getCapacity() != null) {
      parkingLotFinded.setCapacity(parkingLot.getCapacity());
    }
    if (parkingLot.getOccupied() != null) {
      parkingLotFinded.setOccupied(parkingLot.getOccupied());
    }
    if (parkingLot.getStatus() != null) {
      parkingLotFinded.setStatus(parkingLot.getStatus());
    }
  }
}
