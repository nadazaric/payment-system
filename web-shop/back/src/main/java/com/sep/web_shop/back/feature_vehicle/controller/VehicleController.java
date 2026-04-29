package com.sep.web_shop.back.feature_vehicle.controller;

import com.sep.web_shop.back.feature_vehicle.dto.VehicleDTO;
import com.sep.web_shop.back.feature_vehicle.service.interf.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    @Autowired VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getAll() {
        return new ResponseEntity<>(vehicleService.getAll(), HttpStatus.OK);
    }

}
