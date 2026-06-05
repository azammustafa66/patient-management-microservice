package com.pm.patientservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppEnvironment {

    @Getter
    private static volatile boolean dev;

    @Value("${spring.profiles.active:dev}")
    public void setActiveProfile(String activeProfile) {
        dev = activeProfile == null
                || activeProfile.isBlank()
                || activeProfile.equalsIgnoreCase("dev")
                || activeProfile.equalsIgnoreCase("local")
                || activeProfile.equalsIgnoreCase("default");
    }

}
