package com.danilo.roombooking.repository;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractRepositoryTest {

    @ServiceConnection
    static final MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:latest")
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpassword");

    static {
        mariaDBContainer.start();
    }
}
