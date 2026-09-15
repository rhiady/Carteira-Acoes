CREATE INDEX idx_usuario_email ON usuario (email);
CREATE INDEX idx_corretora_cnpj ON corretora (cnpj);
CREATE INDEX idx_acao_ticker ON acao (ticker);
CREATE INDEX idx_carteira_usuario_id ON carteira (usuario_id);
CREATE INDEX idx_carteira_acao_carteira_id ON carteira_acao (carteira_id);
CREATE INDEX idx_operacao_carteira_acao_data_hora ON operacao (carteira_id, acao_id, data_hora DESC);
