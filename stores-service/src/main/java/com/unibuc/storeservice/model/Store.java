package com.unibuc.storeservice.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "contact_phone_number")
    private String contactPhoneNumber;

    // The owner is a user managed by the auth-service. We keep only the foreign
    // key here and resolve owner details over OpenFeign when needed.
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
}
