ALTER TABLE usuario ADD COLUMN papel VARCHAR(20) DEFAULT 'USER';

UPDATE usuario SET papel = 'USER' WHERE papel IS NULL;

ALTER TABLE usuario ALTER COLUMN papel SET NOT NULL;

ALTER TABLE usuario
    ADD CONSTRAINT ck_usuario_papel CHECK (papel IN ('ADMIN', 'USER'));
