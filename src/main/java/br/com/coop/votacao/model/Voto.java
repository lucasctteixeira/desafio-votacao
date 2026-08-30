package br.com.coop.votacao.model;

import br.com.coop.votacao.model.enums.OpcaoVotoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "voto",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_voto_pauta_associado",
                columnNames = {"pauta_id", "associado_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cpf;

    @ManyToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Column(name = "associado_id", nullable = false)
    private String associadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OpcaoVotoEnum opcao;

    @Column(name = "registrado_em", nullable = false)
    private Instant registradoEm;

    public Voto(Pauta pauta, String associadoId, String cpf, OpcaoVotoEnum opcao) {
        this.pauta = pauta;
        this.cpf = cpf;
        this.associadoId = associadoId;
        this.opcao = opcao;
        this.registradoEm = Instant.now();
    }
}