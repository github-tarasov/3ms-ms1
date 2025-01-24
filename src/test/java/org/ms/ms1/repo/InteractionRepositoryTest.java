package org.ms.ms1.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ms.ms1.model.Interaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@DataJpaTest
public class InteractionRepositoryTest {

    @Autowired
    private InteractionRepository interactionRepository;

    @BeforeEach
    public void setUp() {
        Interaction interaction = new Interaction();
        interactionRepository.save(interaction);
    }

    @Test
    public void find() {
        List<Interaction> interactions = interactionRepository.findAll();

        assertThat(interactions, hasSize(1));
        assertThat(interactions.get(0).getInteractionId(), is(notNullValue()));
    }

    @AfterEach
    public void tearDown() {
        interactionRepository.deleteAll();
    }

}
