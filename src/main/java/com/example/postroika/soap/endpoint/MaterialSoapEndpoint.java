package com.example.postroika.soap.endpoint;

import com.example.postroika.dto.MaterialAvailabilityDto;
import com.example.postroika.service.BuildingMaterialsService;
import com.example.postroika.soap.materials.CheckMaterialAvailabilityRequest;
import com.example.postroika.soap.materials.CheckMaterialAvailabilityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@RequiredArgsConstructor
public class MaterialSoapEndpoint {

    private static final String NAMESPACE_URI = "http://postroika.example.com/soap/materials";

    private final BuildingMaterialsService buildingMaterialsService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CheckMaterialAvailabilityRequest")
    @ResponsePayload
    public CheckMaterialAvailabilityResponse checkMaterialAvailability(
            @RequestPayload CheckMaterialAvailabilityRequest request
    ) {
        MaterialAvailabilityDto availability = buildingMaterialsService.checkAvailability(
                request.getMaterialId(),
                request.getRequestedQuantity()
        );

        CheckMaterialAvailabilityResponse response = new CheckMaterialAvailabilityResponse();

        response.setMaterialId(availability.getMaterialId());
        response.setName(availability.getName());
        response.setUnit(availability.getUnit());
        response.setRequestedQuantity(availability.getRequestedQuantity());
        response.setAvailableQuantity(availability.getAvailableQuantity());
        response.setAvailable(availability.isAvailable());
        response.setMessage(availability.getMessage());

        return response;
    }
}