USE barbershop;

ALTER TABLE user
    ADD CONSTRAINT uk_user_telefone UNIQUE (telefone);
