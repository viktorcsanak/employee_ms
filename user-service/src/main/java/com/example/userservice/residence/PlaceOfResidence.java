package com.example.userservice.residence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "place_of_residence",
    uniqueConstraints = @UniqueConstraint(columnNames = {"city", "postal_code"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceOfResidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String postalCode;
}
