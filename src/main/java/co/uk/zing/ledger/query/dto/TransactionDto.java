package co.uk.zing.ledger.query.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TransactionDto {

    private UUID id;
    private String type;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String requestId;
    private List<EntryDto> entries;

    public TransactionDto(UUID id, String type, String status, LocalDateTime createdAt, LocalDateTime updatedAt, String requestId, List<EntryDto> entries) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.requestId = requestId;
        this.entries = entries;
    }

}

