package org.ms.ms1.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ms.ms1.model.Interaction;
import org.ms.ms1.model.Session;
import org.ms.ms1.model.SessionsStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class SessionRepositoryTest {

    @Autowired
    private SesionRepository sesionRepository;

    @Autowired
    private InteractionRepository interactionRepository;

    private Interaction interaction;

    @BeforeEach
    public void setUp() {
        interaction = new Interaction();
        interaction = interactionRepository.save(interaction);
    }

    @Test
    public void getSessionsStatisticsByInteraction() {
        long now = System.currentTimeMillis();

        Session session1 = new Session();
        session1.setInteraction(interaction);
        session1.setService1Timestamp(new Date(now - 1000l));
        session1.setService2Timestamp(new Date(now - 900l));
        session1.setService3Timestamp(new Date(now - 800l));
        session1.setEndTimestamp(new Date(now - 700l));
        sesionRepository.save(session1);

        Session session2 = new Session();
        session2.setInteraction(interaction);
        session2.setService1Timestamp(new Date(now + 700l));
        session2.setService2Timestamp(new Date(now + 800l));
        session2.setService3Timestamp(new Date(now + 900l));
        session2.setEndTimestamp(new Date(now + 1000l));
        sesionRepository.save(session2);

        SessionsStatistics sessionsStatistics = sesionRepository.getSessionsStatisticsByInteraction(interaction.getInteractionId());

        assertThat(sessionsStatistics.getMinService1Timestamp().getTime(), equalTo(now - 1000l));
        assertThat(sessionsStatistics.getMaxEndTimestamp().getTime(), equalTo(now + 1000l));
        assertThat(sessionsStatistics.getCount(), equalTo(2L));
    }

    @AfterEach
    public void tearDown() {
        sesionRepository.deleteAll();
        interactionRepository.deleteAll();
    }

}
