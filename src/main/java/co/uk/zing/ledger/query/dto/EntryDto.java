package co.uk.zing.ledger.query.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntryDto {

    private UUID id;
    private UUID accountId;
    private BigDecimal amount;
    private LocalDateTime entryTime;
    private String type;
    private String direction;
    private LocalDateTime discardedAt;
    private String status; // Pending, Posted, Failed

}
