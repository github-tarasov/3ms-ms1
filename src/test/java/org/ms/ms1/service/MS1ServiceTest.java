package org.ms.ms1.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ms.dto.Message;
import org.ms.ms1.converter.SessionConverter;
import org.ms.ms1.model.Interaction;
import org.ms.ms1.model.Session;
import org.ms.ms1.model.SessionsStatistics;
import org.ms.ms1.repo.InteractionRepository;
import org.ms.ms1.repo.SesionRepository;
import org.ms.ms1.websocket.MS1WebSocketProducer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MS1ServiceTest {

    @InjectMocks
    private MS1Service ms1Service;

    @Mock
    private MS1WebSocketProducer wsService;

    @Mock
    private InteractionRepository interactionRepository;

    @Mock
    private SesionRepository sesionRepository;

    @Mock
    private SessionConverter sessionConverter;

    @Test
    public void start() {
        Interaction interaction = new Interaction();
        interaction.setInteractionId(10L);
        when(interactionRepository.save(any(Interaction.class))).thenReturn(interaction);

        Session session = new Session();
        session.setInteraction(interaction);
        session.setSessionId(20);
        when(sesionRepository.save(any(Session.class))).thenReturn(session);

        Message message = new Message();
        message.setSessionId(session.getSessionId());
        message.setService1Timestamp(new Date());
        when(sessionConverter.toMessage(any(Session.class))).thenReturn(message);

        doNothing().when(wsService).sendMessage(any(Message.class));


        ms1Service.start();


        verify(interactionRepository, times(1)).save(any(Interaction.class));

        ArgumentCaptor<Session> argumentSession = ArgumentCaptor.forClass(Session.class);
        verify(sesionRepository, times(1)).save(argumentSession.capture());
        assertThat(argumentSession.getValue().getSessionId(), is(nullValue()));
        assertThat(argumentSession.getValue().getService1Timestamp(), is(notNullValue()));

        verify(sessionConverter, times(1)).toMessage(any(Session.class));

        ArgumentCaptor<Message> argumentMessage = ArgumentCaptor.forClass(Message.class);
        verify(wsService, times(1)).sendMessage(argumentMessage.capture());
        assertThat(argumentMessage.getValue().getSessionId(), equalTo(session.getSessionId()));
        assertThat(argumentMessage.getValue().getService1Timestamp(), is(notNullValue()));
    }

    @Test
    public void storeAndStopBecauseDurationMoreThenMaxInterval() {
        Interaction interaction = new Interaction();
        interaction.setInteractionId(10L);

        Session sessionBefore = new Session();
        sessionBefore.setSessionId(20);
        sessionBefore.setInteraction(interaction);
        sessionBefore.setService1Timestamp(new Date());
        when(sesionRepository.findById(any(Integer.class))).thenReturn(Optional.of(sessionBefore));

        Session sessionAfter = new Session();
        sessionAfter.setSessionId(20);
        sessionAfter.setInteraction(interaction);
        sessionAfter.setService1Timestamp(new Date());
        sessionAfter.setService2Timestamp(new Date());
        sessionAfter.setService3Timestamp(new Date());
        sessionAfter.setEndTimestamp(new Date());
        when(sesionRepository.saveAndFlush(any(Session.class))).thenReturn(sessionAfter);

        long now = System.currentTimeMillis();
        SessionsStatistics sessionsStatistics = new SessionsStatistics(new Date(now - 2500l), new Date(now + 2500l), 1L);
        when(sesionRepository.getSessionsStatisticsByInteraction(any(Long.class))).thenReturn(sessionsStatistics);
        ReflectionTestUtils.setField(ms1Service, "interactionIntervalInSeconds", 5L);


        Message message = new Message();
        message.setSessionId(sessionBefore.getSessionId());
        message.setService1Timestamp(sessionBefore.getService1Timestamp());
        message.setService2Timestamp(new Date());
        message.setService3Timestamp(new Date());
        ms1Service.store(message);


        verify(sesionRepository, times(1)).findById(eq(20));

        ArgumentCaptor<Session> argumentSession = ArgumentCaptor.forClass(Session.class);
        verify(sesionRepository, times(1)).saveAndFlush(argumentSession.capture());
        assertThat(argumentSession.getValue().getSessionId(), equalTo(message.getSessionId()));
        assertThat(argumentSession.getValue().getService1Timestamp(), is(notNullValue()));
        assertThat(argumentSession.getValue().getService2Timestamp(), is(notNullValue()));
        assertThat(argumentSession.getValue().getService3Timestamp(), is(notNullValue()));
        assertThat(argumentSession.getValue().getEndTimestamp(), is(notNullValue()));

        verify(sesionRepository, times(1)).getSessionsStatisticsByInteraction(eq(10L));

        // Don't start again
        verify(sesionRepository, never()).save(any(Session.class));
        verify(sessionConverter, never()).toMessage(any(Session.class));
        verify(wsService, never()).sendMessage(any(Message.class));
    }

    @Test
    public void storeAndStopBecauseStoped() {
        Interaction interaction = new Interaction();
        interaction.setInteractionId(10L);

        Session sessionBefore = new Session();
        sessionBefore.setSessionId(20);
        sessionBefore.setInteraction(interaction);
        sessionBefore.setService1Timestamp(new Date());
        when(sesionRepository.findById(any(Integer.class))).thenReturn(Optional.of(sessionBefore));

        Session sessionAfter = new Session();
        sessionAfter.setSessionId(20);
        sessionAfter.setInteraction(interaction);
        sessionAfter.setService1Timestamp(new Date());
        sessionAfter.setService2Timestamp(new Date());
        sessionAfter.setService3Timestamp(new Date());
        sessionAfter.setEndTimestamp(new Date());
        when(sesionRepository.saveAndFlush(any(Session.class))).thenReturn(sessionAfter);

        long now = System.currentTimeMillis();
        SessionsStatistics sessionsStatistics = new SessionsStatistics(new Date(now - 500l), new Date(now + 500l), 1L);
        when(sesionRepository.getSessionsStatisticsByInteraction(any(Long.class))).thenReturn(sessionsStatistics);
        ReflectionTestUtils.setField(ms1Service, "interactionIntervalInSeconds", 5L);

        ms1Service.stop();
        Message message = new Message();
        message.setSessionId(sessionBefore.getSessionId());
        message.setService1Timestamp(sessionBefore.getService1Timestamp());
        message.setService2Timestamp(new Date());
        message.setService3Timestamp(new Date());
        ms1Service.store(message);


        verify(sesionRepository, times(1)).findById(eq(20));

        ArgumentCaptor<Session> argumentSession = ArgumentCaptor.forClass(Session.class);
        verify(sesionRepository, times(1)).saveAndFlush(argumentSession.capture());
        assertThat(argumentSession.getValue().getSessionId(), equalTo(message.getSessionId()));
        assertThat(argumentSession.getValue().getService1Timestamp(), is(notNullValue()));
        assertThat(argumentSession.getValue().getService2Timestamp(), is(notNullValue()));
        assertThat(argumentSession.getValue().getService3Timestamp(), is(notNullValue()));
        assertThat(argumentSession.getValue().getEndTimestamp(), is(notNullValue()));

        verify(sesionRepository, times(1)).getSessionsStatisticsByInteraction(eq(10L));

        // Don't start again
        verify(sesionRepository, never()).save(any(Session.class));
        verify(sessionConverter, never()).toMessage(any(Session.class));
        verify(wsService, never()).sendMessage(any(Message.class));
    }

    @Test
    public void storeAndRepeatOnce() {
        Interaction interaction = new Interaction();
        interaction.setInteractionId(10L);

        Session sessionBefore = new Session();
        sessionBefore.setSessionId(20);
        sessionBefore.setInteraction(interaction);
        sessionBefore.setService1Timestamp(new Date());
        when(sesionRepository.findById(any(Integer.class))).thenReturn(Optional.of(sessionBefore));

        Session sessionAfter = new Session();
        sessionAfter.setSessionId(20);
        sessionAfter.setInteraction(interaction);
        sessionAfter.setService1Timestamp(new Date());
        sessionAfter.setService2Timestamp(new Date());
        sessionAfter.setService3Timestamp(new Date());
        sessionAfter.setEndTimestamp(new Date());
        when(sesionRepository.saveAndFlush(any(Session.class))).thenReturn(sessionAfter);

        long now = System.currentTimeMillis();
        SessionsStatistics sessionsStatistics = new SessionsStatistics(new Date(now - 2000l), new Date(now + 2000l), 1L);
        when(sesionRepository.getSessionsStatisticsByInteraction(any(Long.class))).thenReturn(sessionsStatistics);
        ReflectionTestUtils.setField(ms1Service, "interactionIntervalInSeconds", 5L);

        when(sesionRepository.save(any(Session.class))).thenReturn(new Session());
        when(sessionConverter.toMessage(any(Session.class))).thenReturn(new Message());


        Message message = new Message();
        message.setSessionId(sessionBefore.getSessionId());
        message.setService1Timestamp(sessionBefore.getService1Timestamp());
        message.setService2Timestamp(new Date());
        message.setService3Timestamp(new Date());
        ms1Service.store(message);


        verify(sesionRepository, times(1)).findById(eq(20));

        ArgumentCaptor<Session> argumentSession = ArgumentCaptor.forClass(Session.class);
        verify(sesionRepository, times(1)).saveAndFlush(argumentSession.capture());
        assertThat(argumentSession.getValue().getSessionId(), equalTo(message.getSessionId()));
        assertThat(argumentSession.getValue().getService1Timestamp(), is(notNullValue()));
        assertThat(argumentSession.getValue().getService2Timestamp(), is(notNullValue()));
        assertThat(argumentSession.getValue().getService3Timestamp(), is(notNullValue()));
        assertThat(argumentSession.getValue().getEndTimestamp(), is(notNullValue()));

        verify(sesionRepository, times(1)).getSessionsStatisticsByInteraction(eq(10L));

        // start again
        verify(sesionRepository, times(1)).save(any(Session.class));
        verify(sessionConverter, times(1)).toMessage(any(Session.class));
        verify(wsService, times(1)).sendMessage(any(Message.class));
    }

    @Test
    public void store_Exception() {
        when(sesionRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        Message message = new Message();
        message.setSessionId(1);
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            ms1Service.store(message);
        });


    }

}
