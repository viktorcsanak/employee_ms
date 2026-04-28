package com.example.userservice.user.residence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceOfResidenceServiceTest {

  @Mock private PlaceOfResidenceRepository repo;

  @InjectMocks private PlaceOfResidenceService service;

  @Test
  void resolve_existingResidence_returnsExisting() {
    PlaceOfResidence existing =
        PlaceOfResidence.builder().city("Novi Sad").postalCode("21000").build();

    when(repo.findByCityAndPostalCode("Novi Sad", "21000")).thenReturn(Optional.of(existing));

    PlaceOfResidence result = service.resolve("Novi Sad", "21000");

    assertEquals(existing, result);
    verify(repo, never()).save(any());
  }

  @Test
  void resolve_missingResidence_createsAndSaves() {
    when(repo.findByCityAndPostalCode("Novi Sad", "21000")).thenReturn(Optional.empty());

    PlaceOfResidence saved =
        PlaceOfResidence.builder().city("Novi Sad").postalCode("21000").build();

    when(repo.save(any(PlaceOfResidence.class))).thenReturn(saved);

    PlaceOfResidence result = service.resolve("Novi Sad", "21000");

    assertEquals("Novi Sad", result.getCity());
    assertEquals("21000", result.getPostalCode());

    verify(repo).save(any(PlaceOfResidence.class));
  }
}
