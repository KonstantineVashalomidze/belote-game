package com.github.konstantinevashalomidze.belotegame.integracia.config;

import com.github.konstantinevashalomidze.belotegame.tamashi.Dasta;
import com.github.konstantinevashalomidze.belotegame.tamashi.DastisMomwodebeli;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class ProeqtisConfigi {
    @Bean
    public DastisMomwodebeli achexvadiDastisMomwodebeli() {
        return () -> {
            Dasta dasta = new Dasta();
            dasta.achexe();
            return dasta;
        };
    }
}



