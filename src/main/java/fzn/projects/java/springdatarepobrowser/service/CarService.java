package fzn.projects.java.springdatarepobrowser.service;

import fzn.projects.java.springdatarepobrowser.model.Car;
import fzn.projects.java.springdatarepobrowser.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CarService {
    @Autowired
    @Qualifier("carRepositoryA")
    private CarRepository carRepositoryA;

    @Autowired
    @Qualifier("carRepositoryB")
    private CarRepository carRepositoryB;

    public String compareRepos() {
        carRepositoryA.findByBrand("A");
        carRepositoryB.findByBrand("B");
        return String.format("CarRepoA: %s, CarRepoB: %s", carRepositoryA.toString(), carRepositoryB.toString());
    }
}
