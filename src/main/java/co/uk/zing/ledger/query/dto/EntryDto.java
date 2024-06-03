package co.uk.zing.ledger.query.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class EntryDto {

    private UUID id;
    private UUID accountId;
    private BigDecimal amount;
    private LocalDateTime entryTime;
    private String type;
    private String direction;

    public EntryDto(UUID id, UUID accountId, BigDecimal amount, LocalDateTime entryTime, String type, String direction) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.entryTime = entryTime;
        this.type = type;
        this.direction = direction;
    }

    // Getters and setters
}
