package com.example.pontera.home.assignment.api.testdata;

import com.example.pontera.home.assignment.dto.ExtraParams;
import com.example.pontera.home.assignment.dto.FactorDetails;
import com.example.pontera.home.assignment.dto.LoginRequest;
import com.example.pontera.home.assignment.dto.PasswordFactorDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationDataProvider {

    public LoginRequest createLoginRequest() {
        return LoginRequest.builder()
                .factorType("PASSWORD")
                .factorDetails(FactorDetails.builder()
                        .passwordFactorDetails(PasswordFactorDetails.builder()
                                .showPassword(false)
                                .email("maayan+tester1@feex.com")
                                .password("Advisor0103Buckley")
                                .build())
                        .build())
                .extraParams(ExtraParams.builder()
                        .zendesk(false)
                        .build())
                .token("")
                .build();
    }
}
