//package com.alfred.parkingalfred.repository;
//
//import com.alfred.parkingalfred.entity.Employee;
//import com.alfred.parkingalfred.entity.ParkingLot;
//import java.util.ArrayList;
//import java.util.List;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@DataJpaTest
//@RunWith(SpringRunner.class)
////@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
//@ActiveProfiles("test")
//public class ParkingLotRepositoryTest {
//
//  @Autowired
//  private TestEntityManager entityManager;
//
//  @Autowired
//  private ParkingLotRepository parkingLotRepository;
//
//  @Before
//  public void setUp() {
//    List<ParkingLot> parkingLots = new ArrayList<ParkingLot>() {{
//      add(new ParkingLot((long) 1, "lot1", 100, 100));
//      add(new ParkingLot((long) 2, "lot2", 100, 99));
//      add(new ParkingLot((long) 3, "lot3", 100, 98));
//    }};
//    Employee employee = new Employee();
//    employee.setId(1L);
//    entityManager.persist(parkingLots.get(0));
//    entityManager.persist(parkingLots.get(1));
//    entityManager.persist(parkingLots.get(2));
//    entityManager.flush();
//    entityManager.persist(employee);
//    entityManager.flush();
//    employee.setParkingLots(parkingLots);
//    entityManager.persist(employee);
//    entityManager.flush();
//  }
//  @Test
//  public void should_get_2_when_call_findALLNotFullParkingLotRowsByEmployeeId_with_true_param() {
//    Long employeeId = 1L;
//    Integer result = parkingLotRepository.findALLNotFullParkingLotRowsByEmployeeId(employeeId);
//    Assert.assertEquals(new Integer(2), result);
//  }
//}