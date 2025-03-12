package com.danilo.roombooking.config.data;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.danilo.roombooking.repository.jpa")
public class JpaConfig {
}
