package fzn.projects.java.springdatarepobrowser.controller;

import fzn.projects.java.springdatarepobrowser.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping("car/compare-repos")
    public String compareRepos() {
        return carService.compareRepos();
    }
}
