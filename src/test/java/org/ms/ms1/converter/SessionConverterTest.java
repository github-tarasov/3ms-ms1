package org.ms.ms1.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ms.dto.Message;
import org.ms.ms1.controller.MS1Controller;
import org.ms.ms1.model.Interaction;
import org.ms.ms1.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
public class SessionConverterTest {

    @Autowired
    private SessionConverter sessionConverter;

    @Test
    public void toMessage() {
        Interaction interaction = new Interaction();
        interaction.setInteractionId(10L);
        Session session = new Session();
        session.setSessionId(20);
        session.setInteraction(interaction);
        session.setService1Timestamp(new Date());
        session.setService2Timestamp(new Date());
        session.setService3Timestamp(new Date());
        session.setEndTimestamp(new Date());

        Message message = sessionConverter.toMessage(session);

        assertThat(message.getSessionId(), equalTo(session.getSessionId()));
        assertThat(message.getService1Timestamp(), equalTo(session.getService1Timestamp()));
        assertThat(message.getService2Timestamp(), equalTo(session.getService2Timestamp()));
        assertThat(message.getService3Timestamp(), equalTo(session.getService3Timestamp()));
        assertThat(message.getEndTimestamp(), equalTo(session.getEndTimestamp()));
    }

}
