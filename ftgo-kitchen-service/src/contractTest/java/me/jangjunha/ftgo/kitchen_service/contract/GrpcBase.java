package me.jangjunha.ftgo.kitchen_service.contract;

import com.asarkar.grpc.test.GrpcCleanupExtension;
import com.asarkar.grpc.test.Resources;
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;
import me.jangjunha.ftgo.kitchen_service.domain.Ticket;
import me.jangjunha.ftgo.kitchen_service.grpc.GrpcServer;
import me.jangjunha.ftgo.kitchen_service.service.KitchenService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.http.HttpVerifier;
import org.springframework.cloud.contract.verifier.http.OkHttpHttpVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(GrpcCleanupExtension.class)
@SpringBootTest(
        classes = GrpcBase.Config.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"grpc.server.port=8105", "spring.datasource.url=jdbc:h2:mem:testdb", "spring.datasource.driver-class-name=org.h2.Driver"}
)
public abstract class GrpcBase {

//    private final KitchenService kitchenService = mock(KitchenService.class);

//    @BeforeEach
//    void setUp() {
//        when(kitchenService.getTicket(any())).thenReturn(new Ticket(UUID.randomUUID(), UUID.randomUUID(), 0L, new TicketDetails(Collections.emptyList())));
//        RestAssuredMockMvc.standaloneSetup(kitchenServiceImpl);
//    }

    @Configuration
    @EnableAutoConfiguration
    static class Config {

        @Value("${grpc.server.port}")
        private int grpcServerPort;

        @Bean
        public KitchenService kitchenService() {
            KitchenService svc = mock(KitchenService.class);
            when(svc.getTicket(any())).thenReturn(new Ticket(UUID.randomUUID(), UUID.randomUUID(), 0L, new TicketDetails(Collections.emptyList())));
            return svc;
        }

//        @Bean
//        public KitchenServiceImpl kitchenServiceImpl(KitchenService kitchenService) {
//            KitchenServiceImpl controller = new KitchenServiceImpl(kitchenService);
////            RestAssuredMockMvc.standaloneSetup(controller);
//            return controller;
//        }

        @Bean
        public GrpcServer grpcServer(Resources resources, KitchenService kitchenService) {
            GrpcServer server = new GrpcServer(grpcServerPort, kitchenService);
            resources.register(server.server, Duration.ofSeconds(3));
            return server;
        }

        @Bean
        Resources resources() {
            return new Resources();
        }

        @Bean
        HttpVerifier httpOkVerifier() {
            return new OkHttpHttpVerifier("localhost:" + grpcServerPort);
        }
    }
}
