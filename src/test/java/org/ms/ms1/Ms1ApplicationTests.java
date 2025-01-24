package org.ms.ms1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.ms.ms1.controller.MS1Controller;
import org.ms.ms1.converter.SessionConverter;
import org.ms.ms1.repo.InteractionRepository;
import org.ms.ms1.repo.SesionRepository;
import org.ms.ms1.service.MS1Service;
import org.ms.ms1.websocket.MS1WebSocketHandler;
import org.ms.ms1.websocket.MS1WebSocketProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@TestPropertySource(locations = "/application-dev.properties")
class Ms1ApplicationTests {

    @Autowired
    private MS1Controller ms1Controller;

    @Autowired
    private MS1Service ms1Service;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MS1WebSocketHandler ms1WebSocketHandler;

    @Autowired
    private MS1WebSocketProducer ms1WebSocketProducer;

    @Autowired
    private InteractionRepository interactionRepository;

    @Autowired
    private SesionRepository sesionRepository;

    @Autowired
    private SessionConverter sessionConverter;

    @Test
    void contextLoads() {
        assertThat(ms1Controller, is(notNullValue()));
        assertThat(ms1Service, is(notNullValue()));
        assertThat(objectMapper, is(notNullValue()));
        assertThat(ms1WebSocketHandler, is(notNullValue()));
        assertThat(ms1WebSocketProducer, is(notNullValue()));
        assertThat(interactionRepository, is(notNullValue()));
        assertThat(sesionRepository, is(notNullValue()));
        assertThat(sessionConverter, is(notNullValue()));
    }

    @Test
    public void main() {
        Ms1Application.main(new String[] {});
    }

}
