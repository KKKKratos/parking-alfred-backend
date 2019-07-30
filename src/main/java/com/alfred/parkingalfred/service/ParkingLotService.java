package com.alfred.parkingalfred.service;

import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.form.ParkingLotForm;
import java.util.List;

public interface ParkingLotService {

  public List<ParkingLot> getParkingLotsByParkingBoyId(Long parkingBoyId);

  public ParkingLot createParkingLot(ParkingLotForm parkingLotForm);

  public List<ParkingLot> getAllParkingLotsByPageAndSize(int page,int size);

  public int getParkingLotCount();

  public ParkingLot updateParkingLotById(Long id, ParkingLot parkingLot);
}
