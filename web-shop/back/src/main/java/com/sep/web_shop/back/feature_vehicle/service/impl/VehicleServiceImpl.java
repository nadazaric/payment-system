package com.sep.web_shop.back.feature_vehicle.service.impl;

import com.sep.web_shop.back.feature_vehicle.dto.CreateVehicleDTO;
import com.sep.web_shop.back.feature_vehicle.dto.VehicleDTO;
import com.sep.web_shop.back.feature_vehicle.mapper.VehicleMapper;
import com.sep.web_shop.back.feature_vehicle.model.Vehicle;
import com.sep.web_shop.back.feature_vehicle.repository.VehicleRepository;
import com.sep.web_shop.back.feature_vehicle.service.interf.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    @Autowired VehicleRepository vehicleRepository;
    @Autowired VehicleMapper vehicleMapper;

    @Value("${app.images.folder}")
    private String vehiclesFolder;


    @Override
    public List<VehicleDTO> getAll() {
        return vehicleMapper.toDtoList(vehicleRepository.findAll());
    }

    @Override
    public Optional<VehicleDTO> getById(Long id) {
        return vehicleRepository.findById(id)
                .map(vehicleMapper::toDto);
    }

    @Override
    public Optional<VehicleDTO> createVehicle(
            CreateVehicleDTO createVehicleDTO,
            MultipartFile image
    ) {
        if (image == null || image.isEmpty()) {
            return Optional.empty();
        }

        try {
            Vehicle vehicle = vehicleMapper.toEntity(createVehicleDTO);
            vehicle.setImagePath("");

            Vehicle savedVehicle = vehicleRepository.save(vehicle);

            String extension = getFileExtension(image.getOriginalFilename());
            String fileName = savedVehicle.getId() + extension;

            Path uploadPath = Path.of(vehiclesFolder);
            Files.createDirectories(uploadPath);

            Path imagePath = uploadPath.resolve(fileName);
            image.transferTo(imagePath.toFile());

            savedVehicle.setImagePath("/images/vehicles/" + fileName);

            Vehicle updatedVehicle = vehicleRepository.save(savedVehicle);

            return Optional.of(vehicleMapper.toDto(updatedVehicle));
        } catch (IOException exception) {
            return Optional.empty();
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".png";
        }

        return fileName.substring(fileName.lastIndexOf("."));
    }

}
