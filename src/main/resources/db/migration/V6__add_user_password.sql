USE barbershop;

-- Senha hash (BCrypt) - nullable inicialmente pra não quebrar registros existentes
ALTER TABLE user
    ADD COLUMN password VARCHAR(255) NULL;

-- Audit timestamps
ALTER TABLE user
    ADD COLUMN updated_at DATETIME(6) NULL;

UPDATE user SET updated_at = NOW(6) WHERE updated_at IS NULL;
