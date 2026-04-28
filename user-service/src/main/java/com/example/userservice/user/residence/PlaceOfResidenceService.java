package com.example.userservice.user.residence;

import org.springframework.stereotype.Service;

@Service
public class PlaceOfResidenceService {

  private final PlaceOfResidenceRepository repo;

  public PlaceOfResidenceService(PlaceOfResidenceRepository repo) {
    this.repo = repo;
  }

  public PlaceOfResidence resolve(String city, String postalCode) {

    return repo.findByCityAndPostalCode(city, postalCode)
        .orElseGet(
            () -> repo.save(PlaceOfResidence.builder().city(city).postalCode(postalCode).build()));
  }
}
