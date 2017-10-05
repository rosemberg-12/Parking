package co.com.ceiba.parking.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import co.com.ceiba.parking.builders.DbCarParkBuilder;
import co.com.ceiba.parking.entities.DbCarPark;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CarParkRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private CarParkRepository carParkRepository;

  @Test
  public void whenFindByVehiclePlate_thenReturnCarPark() {

    DbCarPark entity = new DbCarParkBuilder().withCar("RMS34D").build();
    entityManager.persist(entity);

    Optional<DbCarPark> foundCarPark = carParkRepository.findByVehiclePlateAndExitDateIsNull(entity.getVehicle().getPlate());

    foundCarPark.ifPresent(car -> assertThat(car.getVehicle().getPlate()).isEqualTo(entity.getVehicle().getPlate()));

  }

  @Test
  public void whenCountParkCars_thenReturnNumberCars() {
    // Arrange
    DbCarPark entity = new DbCarParkBuilder().withCar("RMS34D").build();
    entityManager.persist(entity);

    // Act
    int count = carParkRepository.countByVehicleTypeAndExitDateIsNull(entity.getVehicle().getType());

    // Assert
    assertThat(count).isEqualTo(1);
  }

  @Test
  public void whenCountParkMotorcycle_thenReturnNumberCars() {
    // Arrange
    DbCarPark entity = new DbCarParkBuilder().withMotorcycle("RMS34E").build();
    entityManager.persist(entity);

    // Act
    int count = carParkRepository.countByVehicleTypeAndExitDateIsNull(entity.getVehicle().getType());

    // Assert
    assertThat(count).isEqualTo(1);
  }

}
