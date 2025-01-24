package org.ms.ms1.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.springframework.web.socket.CloseStatus.NORMAL;

public class MS1WebSocketHandlerTest {

    private MS1WebSocketHandler handler;
    private WebSocketSession session;

    @BeforeEach
    public void before() {
        handler = new MS1WebSocketHandler();
        session = mock(WebSocketSession.class);
    }

    @Test
    public void afterConnectionEstablishedSaveSession() throws Exception {
        handler.afterConnectionEstablished(session);

        assertThat(handler.getSession(), is(notNullValue()));
    }

    @Test
    public void handleMessage() throws Exception {
        handler.handleMessage(session, new TextMessage(""));
    }

    @Test
    public void handleTransportError() throws Exception {
        handler.handleTransportError(session, new Throwable());
    }

    @Test
    public void afterConnectionClosed() throws Exception {
        handler.afterConnectionClosed(session, NORMAL);
    }

    @Test
    public void supportsPartialMessages() {
        assertThat(handler.supportsPartialMessages(), equalTo(false));
    }

}