package org.ms.ms1.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ms.dto.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MS1WebSocketProducer {

    private final MS1WebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    @Value("${websocket.uri}")
    private String websocketUri;

    @PostConstruct
    private void construct() {
        connect();
    }

    private void connect() {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.execute(webSocketHandler, websocketUri);
    }

    /*
     * Check connection or reconnect
     * */
    private void checkConnection() {
        if (!isConnected()) {
            connect();
            for (int i = 100; i > 0 && !isConnected(); i--) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (!isConnected()) {
            throw new WebSocketException("Can't connect to websocket");
        }
    }

    private boolean isConnected() {
        return webSocketHandler.getSession() != null && webSocketHandler.getSession().isOpen();
    }

    public void sendMessage(Message message) {
        checkConnection();
        try {
            TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(message));
            webSocketHandler.getSession().sendMessage(textMessage);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new WebSocketException(e.getMessage(), e);
        }
    }

}
