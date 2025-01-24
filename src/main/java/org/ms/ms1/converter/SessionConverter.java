package org.ms.ms1.converter;

import org.ms.dto.Message;
import org.ms.ms1.model.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionConverter {
    public Message toMessage(Session session) {
        return Message.builder()
                        .sessionId(session.getSessionId())
                        .service1Timestamp(session.getService1Timestamp())
                        .service2Timestamp(session.getService2Timestamp())
                        .service3Timestamp(session.getService3Timestamp())
                        .endTimestamp(session.getEndTimestamp())
                        .build();
    }
}
