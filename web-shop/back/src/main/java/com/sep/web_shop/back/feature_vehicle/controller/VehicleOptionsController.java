package com.sep.web_shop.back.feature_vehicle.controller;

import com.sep.web_shop.back.feature_vehicle.dto.VehicleOptionsDTO;
import com.sep.web_shop.back.feature_vehicle.service.interf.VehicleOptionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicle-options")
@RequiredArgsConstructor
public class VehicleOptionsController {

    @Autowired VehicleOptionsService vehicleOptionsService;

    @GetMapping
    public ResponseEntity<VehicleOptionsDTO> getVehicleOptions() {
        return ResponseEntity.ok(vehicleOptionsService.getVehicleOptions());
    }
}
