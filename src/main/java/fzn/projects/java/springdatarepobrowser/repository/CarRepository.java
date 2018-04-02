package fzn.projects.java.springdatarepobrowser.repository;

import fzn.projects.java.springdatarepobrowser.model.Car;
import org.springframework.data.repository.CrudRepository;

public interface CarRepository extends CrudRepository<Car, String>, CustomizedCarRepository {
}
