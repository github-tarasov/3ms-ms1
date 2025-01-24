package org.ms.ms1.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import java.util.Set;

@Entity
@Comment("Группы циклов (сеансов) взаимодействия")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
public class Interaction {
    @Id
    @GeneratedValue
    @Comment("Идентификатор группы циклов (сеансов) взаимодействия")
    @Column(name="interaction_id")
    @EqualsAndHashCode.Include
    private Long interactionId;

    @OneToMany(mappedBy="interaction")
    @ToString.Exclude
    private Set<Session> sessions;
}
