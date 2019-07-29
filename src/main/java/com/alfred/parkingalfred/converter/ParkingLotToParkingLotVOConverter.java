package com.alfred.parkingalfred.converter;

import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.vo.ParkingLotVo;
import org.springframework.beans.BeanUtils;

public class ParkingLotToParkingLotVOConverter {

    public static ParkingLotVo convert(ParkingLot parkingLot) {
        ParkingLotVo parkingLotVo = new ParkingLotVo();
        BeanUtils.copyProperties(parkingLot, parkingLotVo);
        return parkingLotVo;
    }
}
