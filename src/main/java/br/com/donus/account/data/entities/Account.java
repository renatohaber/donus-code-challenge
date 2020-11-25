package br.com.donus.account.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString
@EqualsAndHashCode
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tab_account")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @javax.persistence.Column(name = "name", nullable = false, updatable = false)
    private String name;

    @javax.persistence.Column(name = "tax_id", nullable = false)
    private String taxId;

    @javax.persistence.Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @CreatedDate
    @javax.persistence.Column(name = "dt_creation", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @LastModifiedDate
    @javax.persistence.Column(name = "dt_last_update", nullable = false)
    private LocalDateTime lastUpdate;

}
