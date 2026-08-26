package br.com.coop.votacao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "sessao_votacao")
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pauta_id", nullable = false, unique = true)
    private Pauta pauta;

    @Column(name = "abriu_em", nullable = false)
    private Instant abriuEm;

    @Column(name = "fecha_em", nullable = false)
    private Instant fechaEm;

    public SessaoVotacao(Pauta pauta, Duration duracao) {
        this.pauta = pauta;
        this.abriuEm = Instant.now();
        this.fechaEm = this.abriuEm.plus(duracao);
    }

    public boolean estaAberta() {
        return Instant.now().isBefore(fechaEm);
    }
}
