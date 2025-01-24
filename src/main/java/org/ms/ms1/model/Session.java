package org.ms.ms1.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import java.util.Date;

@Entity
@Comment("Один цикл (сеанс) взаимодействия")
@Table(indexes = @Index(columnList = "interactionId, service1Timestamp"))
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor

public class Session {
    @Id
    @GeneratedValue
    @Comment("Идентификатор одного цикла (сеанса) взаимодействия")
    @EqualsAndHashCode.Include
    private Integer sessionId; // TODO: change to Long

    /*
     * Группа циклов (сеансов) взаимодействия
     * */
    @ManyToOne
    @JoinColumn(name="interaction_id", nullable=false)
    @Comment("Идентификатор группы циклов (сеансов) взаимодействия")
    private Interaction interaction;

    @Comment("Время прохождения сообщения через MS1 в начале")
    private Date service1Timestamp;

    @Comment("Время прохождения сообщения через MS2")
    private Date service2Timestamp;

    @Comment("Время прохождения сообщения через MS3")
    private Date service3Timestamp;

    @Comment("Время прохождения сообщения через MS1 в конце")
    private Date endTimestamp;
}
