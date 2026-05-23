USE barbershop;

-- 1. Cria índice não-único em client_id pra a FK ter onde se apoiar
ALTER TABLE schedule ADD INDEX idx_schedule_client (client_id);

-- 2. Agora pode dropar o unique (FK migra pro idx_schedule_client automaticamente)
ALTER TABLE schedule DROP INDEX uk_schedule_client;

-- 3. Audit timestamps
ALTER TABLE schedule
    ADD COLUMN created_at DATETIME(6) NULL,
    ADD COLUMN updated_at DATETIME(6) NULL;

-- Preenche audit pros registros existentes
UPDATE schedule SET created_at = NOW(6), updated_at = NOW(6) WHERE created_at IS NULL;
