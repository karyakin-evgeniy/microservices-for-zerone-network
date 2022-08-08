package org.proteam24.zeroneapplication.integration;

import com.corundumstudio.socketio.SocketIOServer;
import org.junit.jupiter.api.BeforeAll;
import org.proteam24.zeroneapplication.integration.annotation.IT;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;

@IT
@Sql({
        "classpath:sql/data.sql"
})
public abstract class IntegrationTestBase {

    @MockBean
    private SocketIOServer socketIOServer;

    private static final MySQLContainer<?> container = new MySQLContainer<>("mysql:8");

    @BeforeAll
    static void runContainer() {
        container.start();
    }

    @DynamicPropertySource
    static void mySqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
    }
}
