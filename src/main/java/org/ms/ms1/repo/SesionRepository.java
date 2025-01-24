package org.ms.ms1.repo;

import org.ms.ms1.model.Session;
import org.ms.ms1.model.SessionsStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SesionRepository extends JpaRepository<Session, Integer> {

    @Query("""
            SELECT new org.ms.ms1.model.SessionsStatistics(
                        MIN(service1Timestamp) as minService1Timestamp,
                        MAX(endTimestamp) as maxEndTimestamp,
                        COUNT(*) as count
                  ) 
            FROM Session
            WHERE interaction.interactionId = :#{#interaction_id}
            GROUP BY interaction.interactionId
    """)
    SessionsStatistics getSessionsStatisticsByInteraction(@Param("interaction_id") Long interactionId);

}
