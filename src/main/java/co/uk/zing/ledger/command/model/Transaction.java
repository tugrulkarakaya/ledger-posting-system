package co.uk.zing.ledger.command.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String type; // Forex
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(unique = true)
    private String requestId;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Entry> entries;

    public Transaction(UUID id, String type, List<Entry> entries, String requestId) {
        this.id = id;
        this.type = type;
        this.entries = entries;
        this.requestId = requestId;
        this.createdAt =  LocalDateTime.now();
    }
}