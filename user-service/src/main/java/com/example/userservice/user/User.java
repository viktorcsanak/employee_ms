package com.example.userservice.user;

import com.example.userservice.auth.Session;
import com.example.userservice.permissions.Role;
import com.example.userservice.residence.PlaceOfResidence;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer id;

  @Column(nullable = false)
  private String firstName;

  @Column private String middleName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private LocalDate dateOfBirth;

  @Column(nullable = false)
  private String gender;

  @Column(nullable = false, unique = true)
  private String email;

  @Column private String cellularPhone;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String position;

  @Column(nullable = false)
  private LocalDate startOfEmployment;

  @ManyToOne
  @JoinColumn(name = "place_of_residence_id")
  private PlaceOfResidence placeOfResidence;

  @Column(nullable = false)
  private String address;

  @Builder.Default
  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "role")
  private Set<Role> roles = new HashSet<>();

  @Builder.Default
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Session> sessions = new HashSet<>();
}
