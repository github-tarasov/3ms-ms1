package org.ms.ms1.websocket;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Component
@Slf4j
public class MS1WebSocketHandler implements WebSocketHandler {
    private WebSocketSession session;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("WebSocket connection established");
        this.session = session;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.debug("WebSocket handle message: {}", message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket handle transport error: {}", exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.debug("WebSocket connection closed");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}