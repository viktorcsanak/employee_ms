package com.example.userservice.residence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PlaceOfResidenceRepository extends JpaRepository<PlaceOfResidence, Integer>,
                                        JpaSpecificationExecutor<PlaceOfResidence>
{
    Optional<PlaceOfResidence> findByCityAndPostalCode(String city, String postalCode);                                            
}