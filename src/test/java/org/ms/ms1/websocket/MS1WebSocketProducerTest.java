package org.ms.ms1.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ms.dto.Message;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MS1WebSocketProducerTest {

    @InjectMocks
    private MS1WebSocketProducer producer;

    @Mock
    private MS1WebSocketHandler ms1WebSocketHandler;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    public void sendMessage_Success() throws IOException {
        ReflectionTestUtils.setField(producer, "websocketUri", "ws://test");
        when(objectMapper.writeValueAsString(any(Message.class))).thenReturn("");
        WebSocketSession webSocketSession = mock(WebSocketSession.class);
        when(webSocketSession.isOpen()).thenReturn(true);
        when(ms1WebSocketHandler.getSession()).thenReturn(webSocketSession);

        producer.sendMessage(new Message());

        verify(webSocketSession, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    public void sendMessage_ReConnect() throws IOException {
        ReflectionTestUtils.setField(producer, "websocketUri", "ws://test");
        when(objectMapper.writeValueAsString(any(Message.class))).thenReturn("");
        WebSocketSession webSocketSession = mock(WebSocketSession.class);
        when(webSocketSession.isOpen()).thenReturn(false)
                                        .thenReturn(true)
                                        .thenReturn(true);
        when(ms1WebSocketHandler.getSession()).thenReturn(webSocketSession);

        producer.sendMessage(new Message());

        verify(webSocketSession, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    public void sendMessage_WebSocketException() throws JsonProcessingException {
        ReflectionTestUtils.setField(producer, "websocketUri", "ws://test");
        when(objectMapper.writeValueAsString(any(Message.class))).thenThrow(mock(JsonProcessingException.class));
        WebSocketSession webSocketSession = mock(WebSocketSession.class);
        when(webSocketSession.isOpen()).thenReturn(true);
        when(ms1WebSocketHandler.getSession()).thenReturn(webSocketSession);

        assertThrowsExactly(WebSocketException.class, () -> {
            producer.sendMessage(new Message());
        });
    }


}
