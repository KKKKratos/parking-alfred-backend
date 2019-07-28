package com.alfred.parkingalfred.repository;

import com.alfred.parkingalfred.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParkingLotRepository extends JpaRepository<ParkingLot,Long> {

  @Query(value = "select count(1) from parking_lot pl where pl.capacity>pl.occupied and "
      + "pl.id in(select id from employee_parking_lot epl where epl.eid = ?1)",nativeQuery = true)
  int findALLNotFullParkingLotRowsByEmployeeId(Long employeeId);

}
