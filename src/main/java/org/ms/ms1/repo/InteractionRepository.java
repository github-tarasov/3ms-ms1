package org.ms.ms1.repo;

import org.ms.ms1.model.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Integer> {

}
