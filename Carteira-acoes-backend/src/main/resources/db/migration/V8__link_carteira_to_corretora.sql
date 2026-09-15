ALTER TABLE carteira ADD COLUMN corretora_id BIGINT;

UPDATE carteira
SET corretora_id = (SELECT id FROM corretora WHERE cnpj = '02332886000104')
WHERE corretora_id IS NULL;

ALTER TABLE carteira ALTER COLUMN corretora_id SET NOT NULL;

ALTER TABLE carteira
    ADD CONSTRAINT fk_carteira_corretora
    FOREIGN KEY (corretora_id) REFERENCES corretora (id);

CREATE INDEX idx_carteira_corretora_id ON carteira (corretora_id);
