package com.alfred.parkingalfred.converter;

import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.vo.ParkingLotVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ParkingLotToParkingLotVOConverter {

    public static ParkingLotVO convert(ParkingLot parkingLot) {
        ParkingLotVO parkingLotVo = new ParkingLotVO();
        BeanUtils.copyProperties(parkingLot, parkingLotVo);
        return parkingLotVo;
    }
    public static List<ParkingLotVO> convert(List<ParkingLot> parkingLotList){
        return  parkingLotList.stream().map(e->convert(e)).collect(Collectors.toList());
    }
}
