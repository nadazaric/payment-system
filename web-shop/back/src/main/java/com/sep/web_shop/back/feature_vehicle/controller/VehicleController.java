package com.sep.web_shop.back.feature_vehicle.controller;

import com.sep.web_shop.back.feature_vehicle.dto.CreateVehicleDTO;
import com.sep.web_shop.back.feature_vehicle.dto.VehicleDTO;
import com.sep.web_shop.back.feature_vehicle.service.interf.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    @Autowired VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getAll() {
        return new ResponseEntity<>(vehicleService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> getById(@PathVariable Long id) {
        return vehicleService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VehicleDTO> createVehicle(@RequestPart("vehicle") CreateVehicleDTO createVehicleDTO, @RequestPart("image") MultipartFile image) {
        Optional<VehicleDTO> vehicleDTO = vehicleService.createVehicle(
                createVehicleDTO,
                image
        );

        return vehicleDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

}
