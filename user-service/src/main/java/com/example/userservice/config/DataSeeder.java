package com.example.userservice.config;

import com.example.userservice.permissions.Role;
import com.example.userservice.residence.PlaceOfResidence;
import com.example.userservice.residence.PlaceOfResidenceRepository;
import com.example.userservice.user.User;
import com.example.userservice.user.UserRepository;
import java.time.LocalDate;
import java.util.Random;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("seed")
public class DataSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PlaceOfResidenceRepository placeRepo;
  private final PasswordEncoder passwordEncoder;

  private static final String[] CITIES = {"Novi Sad", "Belgrade", "Subotica", "Szeged", "Budapest"};
  private static final String[] GENDERS = {"MALE", "FEMALE"};
  private static final String[] POSITIONS = {"ENGINEER", "HR", "MANAGER", "INTERN"};
  private static final Random RANDOM = new Random();

  public DataSeeder(
      UserRepository userRepository,
      PlaceOfResidenceRepository placeRepo,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.placeRepo = placeRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    seedUsers(50);
  }

  private void seedUsers(int count) {

    if (userRepository.findByEmail("super@m.c").isEmpty()) {

      final PlaceOfResidence adminPlace = getOrCreatePlace("Novi Sad", "21000");

      final User admin =
          User.builder()
              .firstName("System")
              .lastName("Admin")
              .email("super@m.c")
              .password(passwordEncoder.encode("123"))
              .dateOfBirth(LocalDate.of(1990, 1, 1))
              .gender("MALE")
              .position("ADMIN")
              .startOfEmployment(LocalDate.now())
              .address("Admin Street 0")
              .placeOfResidence(adminPlace)
              .roles(Set.of(Role.ADMIN, Role.HR, Role.BASIC))
              .build();

      userRepository.save(admin);
    }

    for (int i = 0; i < count; i++) {

      final String firstName = randomFirstName();
      final String lastName = randomLastName();
      final String email = (firstName + "." + lastName + i + "@test.com").toLowerCase();

      if (userRepository.findByEmail(email).isPresent()) {
        continue;
      }

      final String city = randomCity();
      final String postal = randomPostal();
      final PlaceOfResidence place = getOrCreatePlace(city, postal);

      final User user =
          User.builder()
              .firstName(firstName)
              .lastName(lastName)
              .email(email)
              .password(passwordEncoder.encode("password123"))
              .dateOfBirth(randomDate())
              .gender(randomGender())
              .position(randomPosition())
              .startOfEmployment(randomStartDate())
              .address("Generated address " + i)
              .placeOfResidence(place)
              .roles(Set.of(Role.BASIC))
              .build();

      userRepository.save(user);
    }
  }

  private PlaceOfResidence getOrCreatePlace(String city, String postalCode) {
    return placeRepo
        .findByCityAndPostalCode(city, postalCode)
        .orElseGet(
            () ->
                placeRepo.save(
                    PlaceOfResidence.builder().city(city).postalCode(postalCode).build()));
  }

  private String randomCity() {
    return CITIES[RANDOM.nextInt(CITIES.length)];
  }

  private String randomPostal() {
    return String.valueOf(2000 + RANDOM.nextInt(8000));
  }

  private String randomGender() {
    return GENDERS[RANDOM.nextInt(GENDERS.length)];
  }

  private String randomPosition() {
    return POSITIONS[RANDOM.nextInt(POSITIONS.length)];
  }

  private String randomFirstName() {
    return "User" + RANDOM.nextInt(1000);
  }

  private String randomLastName() {
    return "Test" + RANDOM.nextInt(1000);
  }

  private LocalDate randomDate() {
    return LocalDate.of(1970 + RANDOM.nextInt(40), 1 + RANDOM.nextInt(12), 1 + RANDOM.nextInt(28));
  }

  private LocalDate randomStartDate() {
    return LocalDate.of(2015 + RANDOM.nextInt(10), 1 + RANDOM.nextInt(12), 1 + RANDOM.nextInt(28));
  }
}
