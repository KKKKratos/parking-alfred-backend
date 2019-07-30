package com.alfred.parkingalfred.service;

import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.form.ParkingLotForm;
import java.util.List;

public interface ParkingLotService {

  List<ParkingLot> getParkingLotsByParkingBoyId(Long parkingBoyId);

  ParkingLot createParkingLot(ParkingLotForm parkingLotForm);

  List<ParkingLot> getAllParkingLotsWithFilterByPageAndSize(int page, int size, String name);

  int getParkingLotCount();

  ParkingLot updateParkingLotById(Long id, ParkingLot parkingLot);
}
