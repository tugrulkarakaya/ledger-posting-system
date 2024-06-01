package co.uk.zing.ledger.command.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

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
    private LocalDateTime timestamp;
    private String status;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Entry> entries;

}