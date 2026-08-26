CREATE TABLE pauta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    criada_em TIMESTAMP NOT NULL
);

CREATE TABLE sessao_votacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pauta_id BIGINT NOT NULL UNIQUE,
    abriu_em TIMESTAMP NOT NULL,
    fecha_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_sessao_pauta FOREIGN KEY (pauta_id) REFERENCES pauta (id)
);

CREATE TABLE voto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pauta_id BIGINT NOT NULL,
    associado_id VARCHAR(255) NOT NULL,
    opcao VARCHAR(10) NOT NULL,
    registrado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_voto_pauta FOREIGN KEY (pauta_id) REFERENCES pauta (id),
    CONSTRAINT uk_voto_pauta_associado UNIQUE (pauta_id, associado_id)
);