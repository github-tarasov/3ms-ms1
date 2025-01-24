package org.ms.ms1.model;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionsStatistics {
    private Date minService1Timestamp;
    private Date maxEndTimestamp;
    private Long count;
}
