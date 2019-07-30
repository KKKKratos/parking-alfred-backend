package com.alfred.parkingalfred.controller;

import com.alfred.parkingalfred.converter.EmployeeToEmployeeVOConverter;
import com.alfred.parkingalfred.converter.ParkingLotToParkingLotVOConverter;
import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.IncorrectParameterException;
import com.alfred.parkingalfred.form.ParkingLotForm;
import com.alfred.parkingalfred.service.ParkingLotService;
import com.alfred.parkingalfred.utils.ResultVOUtil;
import com.alfred.parkingalfred.vo.EmployeeVO;
import com.alfred.parkingalfred.vo.ParkingLotVO;
import com.alfred.parkingalfred.vo.ResultVO;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParkingLotController {
  @Autowired
  ParkingLotService parkingLotService;

  @PostMapping("/parking-lots")
  public ResultVO createParkingLot(@Valid @RequestBody ParkingLotForm parkingLotForm, BindingResult bindingResult){
    if (bindingResult.hasErrors()){
      throw  new IncorrectParameterException(ResultEnum.PARAM_ERROR);
    }
    ParkingLot parkingLot = parkingLotService.createParkingLot(parkingLotForm);
    return ResultVOUtil.success(parkingLot);
  }
  @GetMapping("/parking-lots")
  public ResultVO getAllParkingLots(@RequestParam(name = "page",defaultValue = "1")Integer page
      ,@RequestParam(name = "size",defaultValue = "10")Integer size){
    List<ParkingLot> parkingLotList = parkingLotService.getAllParkingLotsByPageAndSize(page,size);
    List<ParkingLotVO> parkingLotVOS =parkingLotList.stream()
            .map(ParkingLotToParkingLotVOConverter::convert)
            .collect(Collectors.toList());
    for(int i=0;i<parkingLotList.size();i++){
      if ( parkingLotList.get(i).getEmployees()!=null){
      List<EmployeeVO> employeeVOList = parkingLotList.get(i).getEmployees().stream()
              .map(EmployeeToEmployeeVOConverter::convert)
              .collect(Collectors.toList());
      parkingLotVOS.get(i).setEmployeeVOS(employeeVOList);
      }else {
        List<EmployeeVO> employeeVOList =null;
        parkingLotVOS.get(i).setEmployeeVOS(employeeVOList);
      }
    }
    int totoalCount = parkingLotService.getParkingLotCount();
    HashMap<String, Object> result = new HashMap<>();
    result.put("parkingLots", parkingLotVOS);
    result.put("totalCount",totoalCount);
    return ResultVOUtil.success(result);
  }

}
