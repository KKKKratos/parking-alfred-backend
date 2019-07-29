package com.alfred.parkingalfred.controller;

import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.IncorrectParameterException;
import com.alfred.parkingalfred.form.ParkingLotForm;
import com.alfred.parkingalfred.service.ParkingLotService;
import com.alfred.parkingalfred.utils.ResultVOUtil;
import com.alfred.parkingalfred.vo.ResultVO;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

}
