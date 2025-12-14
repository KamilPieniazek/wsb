package com.example.wsb.model.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDetails {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
}
