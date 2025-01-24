package org.ms.ms1.websocket;

import org.junit.jupiter.api.Test;

public class WebSocketExceptionTest {

    @Test
    public void constructor1() {
        new WebSocketException("Message");
    }

    @Test
    public void constructor2() {
        new WebSocketException("Message", new Throwable());
    }

}
