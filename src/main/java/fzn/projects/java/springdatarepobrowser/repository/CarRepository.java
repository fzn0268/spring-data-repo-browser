package fzn.projects.java.springdatarepobrowser.repository;

import fzn.projects.java.springdatarepobrowser.model.Car;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarRepository extends CrudRepository<Car, String>, CustomizedCarRepository {
    List<Car> findByBrand(@Param("brand") String brand);
}
