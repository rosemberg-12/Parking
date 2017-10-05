package co.com.ceiba.parking.services.impl;

import static co.com.ceiba.parking.enums.EVehicleType.CAR;
import static co.com.ceiba.parking.enums.EVehicleType.MOTORCYCLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import co.com.ceiba.parking.builders.CarParkBuilder;
import co.com.ceiba.parking.builders.DbCarParkBuilder;
import co.com.ceiba.parking.domain.CarPark;
import co.com.ceiba.parking.entities.DbCarPark;
import co.com.ceiba.parking.exceptions.LimitExceededException;
import co.com.ceiba.parking.exceptions.NoAvailableDayException;
import co.com.ceiba.parking.helpers.Constants;
import co.com.ceiba.parking.repositories.CarParkRepository;
import co.com.ceiba.parking.services.CarParkService;

@RunWith(SpringRunner.class)
public class CarParkServiceImplTest {

  private final static long DEFAULT_KEY = 1;

  @TestConfiguration
  static class CarParkServiceImplTestContextConfiguration {

    @Bean
    public CarParkServiceImpl carParkService() {
      return new CarParkServiceImpl();
    }
  }

  @Autowired
  private CarParkService carParkService;

  @MockBean
  private CarParkRepository carParkRepository;

  private LocalDateTime now;

  @Before
  public void setUp() {
    this.now = LocalDateTime.of(LocalDate.of(2017, Month.OCTOBER, 5), LocalTime.MAX);
    when(carParkRepository.save(any(DbCarPark.class))).thenReturn(new DbCarParkBuilder().withId(DEFAULT_KEY).build());
  }

  @Test
  public void whenPlateStartWithA_thenThrowNoAvailableDayException() {
    // Arrange
    CarPark carPark = new CarParkBuilder().withCar("ARD23D").withEntryDate(now).build();

    try {
      // Act
      carParkService.park(carPark);
      fail();
    }
    catch (NoAvailableDayException ex) {
      // Assert
      assertThat(ex.getMessage()).isEqualTo(Constants.NO_AVAILABLE_DAYS);
    }

  }

  @Test
  public void whenPlateStartWithASunday_thenSaveCarPark() {
    // Arrange
    LocalDateTime sunday = LocalDateTime.of(LocalDate.of(2017, Month.OCTOBER, 1), LocalTime.MAX);
    CarPark carPark = new CarParkBuilder().withCar("ARD23D").withEntryDate(sunday).build();

    // Act
    CarPark newCarPark = carParkService.park(carPark);

    // Assert
    assertThat(newCarPark.getId()).isEqualTo(DEFAULT_KEY);
  }

  @Test
  public void whenPlateStartWithAMonday_thenSaveCarPark() {
    // Arrange
    LocalDateTime sunday = LocalDateTime.of(LocalDate.of(2017, Month.OCTOBER, 2), LocalTime.MAX);
    CarPark carPark = new CarParkBuilder().withCar("ARD23D").withEntryDate(sunday).build();

    // Act
    CarPark newCarPark = carParkService.park(carPark);

    // Assert
    assertThat(newCarPark.getId()).isEqualTo(DEFAULT_KEY);
  }

  @Test
  public void whenThereAre21Cars_thenThrowLimitExceededException() {
    // Arrange
    when(carParkRepository.countByVehicleTypeAndExitDateIsNull(CAR)).thenReturn(21);
    CarPark carPark = new CarParkBuilder().withCar("CRD23D").withEntryDate(now).build();

    try {
      // Act
      carParkService.park(carPark);
      fail();
    }
    catch (LimitExceededException ex) {
      // Assert
      assertThat(ex.getMessage()).isEqualTo(Constants.LIMIT_EXCEEDED_CAR);
    }
  }

  @Test
  public void whenThereAre9Cars_thenThrowLimitExceededException() {
    // Arrange
    CarPark carPark = new CarParkBuilder().withCar("CRD23D").withEntryDate(now).build();

    when(carParkRepository.countByVehicleTypeAndExitDateIsNull(CAR)).thenReturn(9);

    // Act
    CarPark newCarPark = carParkService.park(carPark);

    // Assert
    assertThat(newCarPark.getId()).isEqualTo(DEFAULT_KEY);
  }

  @Test
  public void whenThereAre11Motorcycles_thenThrowLimitExceededException() {
    // Arrange
    when(carParkRepository.countByVehicleTypeAndExitDateIsNull(MOTORCYCLE)).thenReturn(11);
    CarPark carPark = new CarParkBuilder().withMotorcycle("CRD23D").withEntryDate(now).build();

    try {
      // Act
      carParkService.park(carPark);
      fail();
    }
    catch (LimitExceededException ex) {
      // Assert
      assertThat(ex.getMessage()).isEqualTo(Constants.LIMIT_EXCEEDED_MOTORCYCLE);
    }

  }

  @Test
  public void whenThereAre9Motorcycles_thenThrowLimitExceededException() {
    // Arrange
    when(carParkRepository.countByVehicleTypeAndExitDateIsNull(MOTORCYCLE)).thenReturn(9);
    CarPark carPark = new CarParkBuilder().withMotorcycle("CRD23D").withEntryDate(now).build();

    // Act
    CarPark newCarPark = carParkService.park(carPark);

    // Assert
    assertThat(newCarPark.getId()).isEqualTo(DEFAULT_KEY);
  }

}
