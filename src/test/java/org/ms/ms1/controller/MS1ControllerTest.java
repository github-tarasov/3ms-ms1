package org.ms.ms1.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ms.dto.Message;
import org.ms.ms1.service.MS1Service;

import java.io.IOException;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MS1ControllerTest {
    @InjectMocks
    private MS1Controller ms1Controller;

    @Mock
    private MS1Service ms1Service;

    @Test
    public void start() {
        doNothing().when(ms1Service).start();

        ms1Controller.start();

        verify(ms1Service, times(1)).start();
    }

    @Test
    public void stop() {
        doNothing().when(ms1Service).stop();

        ms1Controller.stop();

        verify(ms1Service, times(1)).stop();
    }

    @Test
    public void store() {
        Message message = Message.builder()
                            .sessionId(123)
                            .service1Timestamp(new Date())
                            .service2Timestamp(new Date())
                            .service3Timestamp(new Date())
                            .build();
        doNothing().when(ms1Service).store(any(Message.class));

        ms1Controller.store(message);

        verify(ms1Service, times(1)).store(eq(message));
    }

}
