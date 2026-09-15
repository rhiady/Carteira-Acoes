ALTER TABLE historico_cotacao DROP CONSTRAINT fk_historico_cotacao_acao;

ALTER TABLE historico_cotacao
    ADD CONSTRAINT fk_historico_cotacao_acao
    FOREIGN KEY (acao_id) REFERENCES acao (id) ON DELETE CASCADE;
