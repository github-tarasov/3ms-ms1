package org.ms.ms1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ms.dto.Message;
import org.ms.ms1.converter.SessionConverter;
import org.ms.ms1.model.Interaction;
import org.ms.ms1.model.Session;
import org.ms.ms1.model.SessionsStatistics;
import org.ms.ms1.repo.InteractionRepository;
import org.ms.ms1.repo.SesionRepository;
import org.ms.ms1.websocket.MS1WebSocketProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class MS1Service {

    private final MS1WebSocketProducer wsService;
    private final InteractionRepository interactionRepository;
    private final SesionRepository sesionRepository;
    private final SessionConverter sessionConverter;

    @Value("${interactionIntervalInSeconds}")
    private Long interactionIntervalInSeconds;

    /*
    * if true, then don't repeat any interaction(s)
    * */
    private boolean isStop = false;

    public void start() {
        Interaction interaction = new Interaction();
        interaction = interactionRepository.save(interaction);
        log.debug("Created interaction: {}", interaction);

        isStop = false;
        startNewSession(interaction);
    }

    private void startNewSession(Interaction interaction) {
        log.debug("startNewSession for interaction: {}", interaction.getInteractionId());
        Session session = new Session();
        session.setInteraction(interaction);
        session.setService1Timestamp(new Date());
        session = sesionRepository.save(session);
        log.debug("Created session: {}", session);

        Message message = sessionConverter.toMessage(session);
        wsService.sendMessage(message);
    }

    // Внимание: работоспособно только в случае единственного экземпляра MS1Service (включая другие экземпляры сервиса ms1)
    // Иначе - переносить во внешнее хранилище.
    public void stop() {
        isStop = true;
    }

    public void store(Message message) {
        // Save session details to DB
        Session session = sesionRepository.findById(message.getSessionId())
                            .orElseThrow(() -> new IllegalArgumentException(String.format("Session %d not found", message.getSessionId())));
        session.setService2Timestamp(message.getService2Timestamp());
        session.setService3Timestamp(message.getService3Timestamp());
        session.setEndTimestamp(new Date());
        session = sesionRepository.saveAndFlush(session);

        // Check interaction duration for start new session
        SessionsStatistics sessionsStatistics = sesionRepository.getSessionsStatisticsByInteraction(
                session.getInteraction().getInteractionId()
        );
        double durationInSeconds = (sessionsStatistics.getMaxEndTimestamp().getTime() - sessionsStatistics.getMinService1Timestamp().getTime()) / 1000d;
        if (!isStop && durationInSeconds < interactionIntervalInSeconds) {
            startNewSession(session.getInteraction());
        } else {
            log.debug("Stop interaction.\nDuration: {} seconds\nCount: {}", durationInSeconds, sessionsStatistics.getCount());
        }
    }
}
