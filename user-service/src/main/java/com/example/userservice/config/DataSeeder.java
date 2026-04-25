package com.example.userservice.config;

import com.example.userservice.permissions.Role;
import com.example.userservice.user.UserRepository;
import com.example.userservice.user.model.User;
import com.example.userservice.user.residence.PlaceOfResidence;
import com.example.userservice.user.residence.PlaceOfResidenceRepository;
import java.time.LocalDate;
import java.util.HashSet;
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

  private static final Random RANDOM = new Random();

  private static final String[] FIRST_NAMES = {
    "John", "Alice", "Michael", "Sara", "David", "Emma", "Chris", "Lara", "Tom", "Sophia"
  };

  private static final String[] LAST_NAMES = {
    "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Taylor", "Moore"
  };

  private static final String[] POSITIONS = {
    "Software Engineer",
    "HR Specialist",
    "Data Analyst",
    "Product Manager",
    "Designer",
    "Project Coordinator"
  };

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

  @SuppressWarnings("checkstyle:methodlength")
  private void seedUsers(int count) {
    if (userRepository.findByEmail("viktor@company.io").isEmpty()) {

      final PlaceOfResidence place = getOrCreatePlace("Szabadka", "24000");

      final User user =
          User.builder()
              .email("viktor@company.io")
              .password(passwordEncoder.encode("wildcard"))
              .firstName("Viktor")
              .lastName("Csanak")
              .dateOfBirth(LocalDate.of(2000, 6, 1))
              .startOfEmployment(LocalDate.of(2022, 7, 1))
              .gender("Male")
              .position("Medior Embedded Software Engineer")
              .address("undisclosed address")
              .placeOfResidence(place)
              .roles(Set.of(Role.ADMIN, Role.HR, Role.BASIC))
              .build();

      userRepository.save(user);
    }

    if (userRepository.findByEmail("jane.doe@company.io").isEmpty()) {

      final PlaceOfResidence place = getOrCreatePlace("Szabadka", "24000");

      final User user =
          User.builder()
              .email("jane.doe@company.io")
              .password(passwordEncoder.encode("hrmanagement"))
              .firstName("Jane")
              .lastName("Doe")
              .dateOfBirth(LocalDate.of(1989, 3, 12))
              .startOfEmployment(LocalDate.of(2015, 8, 30))
              .gender("Female")
              .position("Lead HR Manager")
              .address("undisclosed address")
              .placeOfResidence(place)
              .roles(Set.of(Role.HR, Role.BASIC))
              .build();

      userRepository.save(user);
    }

    if (userRepository.findByEmail("bob@company.io").isEmpty()) {

      final PlaceOfResidence place = getOrCreatePlace("Szabadka", "24000");

      final User user =
          User.builder()
              .email("bob@company.io")
              .password(passwordEncoder.encode("admin123"))
              .firstName("Bob")
              .lastName("Ace")
              .dateOfBirth(LocalDate.of(1994, 12, 1))
              .startOfEmployment(LocalDate.of(2020, 1, 25))
              .gender("Male")
              .position("System Administrator")
              .address("undisclosed address")
              .placeOfResidence(place)
              .roles(Set.of(Role.ADMIN, Role.BASIC))
              .build();

      userRepository.save(user);
    }

    if (userRepository.findByEmail("joe.average@company.io").isEmpty()) {

      final PlaceOfResidence place = getOrCreatePlace("Szabadka", "24000");

      final User user =
          User.builder()
              .email("joe.average@company.io")
              .password(passwordEncoder.encode("notmypet"))
              .firstName("Joe")
              .lastName("Average")
              .dateOfBirth(LocalDate.of(1994, 12, 1))
              .startOfEmployment(LocalDate.of(2020, 1, 25))
              .gender("Male")
              .position("IT technician")
              .address("undisclosed address")
              .placeOfResidence(place)
              .roles(Set.of(Role.BASIC))
              .build();

      userRepository.save(user);
    }
    for (int i = 0; i < 96; i++) {

      final String first = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
      final String last = LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
      final String email = (first + "." + last + i + "@company.io").toLowerCase();

      if (userRepository.findByEmail(email).isPresent()) {
        continue;
      }

      final boolean isAdmin = i % 20 == 0;
      final boolean hrAccess = i % 15 == 0;

      final String position = POSITIONS[RANDOM.nextInt(POSITIONS.length)];

      final Set<Role> roles = new HashSet<>();
      roles.add(Role.BASIC);
      if (isAdmin) {
        roles.add(Role.ADMIN);
      }
      if (hrAccess) {
        roles.add(Role.HR);
      }
      final PlaceOfResidence place = getOrCreatePlace("Szabadka", "24000");

      final User user =
          User.builder()
              .email(email)
              .password(passwordEncoder.encode("password123"))
              .firstName(first)
              .lastName(last)
              .dateOfBirth(
                  LocalDate.of(
                      1990 + RANDOM.nextInt(30), 1 + RANDOM.nextInt(12), 1 + RANDOM.nextInt(28)))
              .startOfEmployment(
                  LocalDate.of(
                      2020 + RANDOM.nextInt(5), 1 + RANDOM.nextInt(12), 1 + RANDOM.nextInt(28)))
              .gender("Other")
              .position(position)
              .address("undisclosed address")
              .placeOfResidence(place)
              .roles(roles)
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
}
