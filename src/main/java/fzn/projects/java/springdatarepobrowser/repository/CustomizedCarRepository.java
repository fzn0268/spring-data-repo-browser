package fzn.projects.java.springdatarepobrowser.repository;

import fzn.projects.java.springdatarepobrowser.model.Car;

public interface CustomizedCarRepository {
    void deleteAll(Iterable<Car> entities);
}
