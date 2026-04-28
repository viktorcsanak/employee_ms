package com.example.userservice.user.residence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlaceOfResidenceRepository
    extends JpaRepository<PlaceOfResidence, Integer>, JpaSpecificationExecutor<PlaceOfResidence> {
  Optional<PlaceOfResidence> findByCityAndPostalCode(String city, String postalCode);
}
