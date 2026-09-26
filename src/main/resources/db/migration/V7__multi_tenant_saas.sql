CREATE TABLE business (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    name                  VARCHAR(120) NOT NULL,
    slug                  VARCHAR(80)  NOT NULL,
    segment               VARCHAR(30)  NOT NULL,
    phone                 VARCHAR(20),
    email                 VARCHAR(160),
    address               VARCHAR(255),
    city                  VARCHAR(120),
    description           VARCHAR(500),
    brand_color           VARCHAR(9)   NOT NULL DEFAULT '#6D5DF6',
    booking_enabled       BIT(1)       NOT NULL DEFAULT b'1',
    slot_interval_minutes INT          NOT NULL DEFAULT 30,
    min_advance_minutes   INT          NOT NULL DEFAULT 60,
    plan                  VARCHAR(20)  NOT NULL DEFAULT 'PROFISSIONAL',
    subscription_status   VARCHAR(20)  NOT NULL DEFAULT 'TRIAL',
    trial_ends_at         DATETIME(6),
    created_at            DATETIME(6)  NOT NULL,
    updated_at            DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_business_slug UNIQUE (slug)
) ENGINE = InnoDB;

CREATE TABLE business_hours (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    business_id BIGINT NOT NULL,
    day_of_week INT    NOT NULL,
    open_time   TIME,
    close_time  TIME,
    closed      BIT(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (id),
    CONSTRAINT uk_business_hours_day UNIQUE (business_id, day_of_week),
    CONSTRAINT fk_business_hours_business FOREIGN KEY (business_id) REFERENCES business (id) ON DELETE CASCADE
) ENGINE = InnoDB;

INSERT INTO business (name, slug, segment, plan, subscription_status, trial_ends_at, created_at)
SELECT 'Minha Empresa', 'minha-empresa', 'BARBEARIA', 'PROFISSIONAL', 'TRIAL', DATE_ADD(NOW(6), INTERVAL 14 DAY), NOW(6)
FROM DUAL
WHERE EXISTS (SELECT 1 FROM user);

INSERT INTO business_hours (business_id, day_of_week, open_time, close_time, closed)
SELECT b.id, d.n, '09:00:00', '19:00:00', IF(d.n = 7, b'1', b'0')
FROM business b
CROSS JOIN (SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
            UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7) d;

ALTER TABLE user
    ADD COLUMN business_id        BIGINT       NULL AFTER id,
    ADD COLUMN email              VARCHAR(160) NULL,
    ADD COLUMN bookable           BIT(1)       NOT NULL DEFAULT b'0',
    ADD COLUMN active             BIT(1)       NOT NULL DEFAULT b'1',
    ADD COLUMN commission_percent DECIMAL(5, 2) NULL,
    ADD COLUMN notes              VARCHAR(500) NULL;

UPDATE user SET business_id = (SELECT MIN(id) FROM business);
UPDATE user SET role = 'OWNER' WHERE role = 'ADM';
UPDATE user SET role = 'PROFESSIONAL', bookable = b'1' WHERE role = 'BARBER';
UPDATE user SET email = CONCAT(telefone, '@conta.local') WHERE role IN ('OWNER', 'PROFESSIONAL');

ALTER TABLE user DROP INDEX uk_user_telefone;

ALTER TABLE user
    MODIFY business_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_user_business FOREIGN KEY (business_id) REFERENCES business (id),
    ADD CONSTRAINT uk_user_business_telefone UNIQUE (business_id, telefone),
    ADD CONSTRAINT uk_user_email UNIQUE (email);

ALTER TABLE service
    ADD COLUMN business_id      BIGINT NULL AFTER service_id,
    ADD COLUMN duration_minutes INT    NOT NULL DEFAULT 30,
    ADD COLUMN active           BIT(1) NOT NULL DEFAULT b'1',
    MODIFY price DECIMAL(10, 2) NOT NULL;

UPDATE service SET business_id = (SELECT MIN(id) FROM business);

DELETE FROM schedule_services
WHERE service_id IN (SELECT service_id FROM service WHERE business_id IS NULL);

DELETE FROM service WHERE business_id IS NULL;

ALTER TABLE service
    MODIFY business_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_service_business FOREIGN KEY (business_id) REFERENCES business (id);

RENAME TABLE schedule TO appointment;
RENAME TABLE schedule_services TO appointment_services;

ALTER TABLE appointment RENAME COLUMN schedule_id TO id;
ALTER TABLE appointment RENAME COLUMN date TO start_at;
ALTER TABLE appointment_services RENAME COLUMN schedule_id TO appointment_id;

ALTER TABLE appointment
    ADD COLUMN business_id     BIGINT        NULL AFTER id,
    ADD COLUMN professional_id BIGINT        NULL AFTER client_id,
    ADD COLUMN end_at          DATETIME(6)   NULL AFTER start_at,
    ADD COLUMN total_price     DECIMAL(10, 2) NOT NULL DEFAULT 0,
    ADD COLUMN notes           VARCHAR(500)  NULL,
    ADD COLUMN source          VARCHAR(20)   NOT NULL DEFAULT 'INTERNO',
    ADD COLUMN membership_id   BIGINT        NULL;

UPDATE appointment a
SET a.business_id     = (SELECT MIN(id) FROM business),
    a.professional_id = (SELECT MIN(sb.barber_id) FROM schedule_barbers sb WHERE sb.schedule_id = a.id),
    a.end_at          = DATE_ADD(a.start_at, INTERVAL 30 MINUTE),
    a.total_price     = COALESCE((SELECT SUM(s.price)
                                  FROM appointment_services x
                                  JOIN service s ON s.service_id = x.service_id
                                  WHERE x.appointment_id = a.id), 0);

DELETE FROM appointment_services
WHERE appointment_id IN (SELECT id FROM appointment WHERE professional_id IS NULL OR start_at IS NULL);

DELETE FROM schedule_barbers
WHERE schedule_id IN (SELECT id FROM appointment WHERE professional_id IS NULL OR start_at IS NULL);

DELETE FROM appointment WHERE professional_id IS NULL OR start_at IS NULL;

DROP TABLE schedule_barbers;

ALTER TABLE appointment
    MODIFY business_id     BIGINT      NOT NULL,
    MODIFY professional_id BIGINT      NOT NULL,
    MODIFY start_at        DATETIME(6) NOT NULL,
    MODIFY end_at          DATETIME(6) NOT NULL,
    MODIFY status          VARCHAR(20) NOT NULL,
    ADD CONSTRAINT fk_appointment_business FOREIGN KEY (business_id) REFERENCES business (id),
    ADD CONSTRAINT fk_appointment_professional FOREIGN KEY (professional_id) REFERENCES user (id),
    ADD INDEX idx_appointment_business_start (business_id, start_at),
    ADD INDEX idx_appointment_professional_start (professional_id, start_at);

CREATE TABLE membership_plan (
    id              BIGINT         NOT NULL AUTO_INCREMENT,
    business_id     BIGINT         NOT NULL,
    name            VARCHAR(100)   NOT NULL,
    description     VARCHAR(500),
    price           DECIMAL(10, 2) NOT NULL,
    monthly_credits INT            NULL,
    active          BIT(1)         NOT NULL DEFAULT b'1',
    created_at      DATETIME(6)    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_membership_plan_business FOREIGN KEY (business_id) REFERENCES business (id)
) ENGINE = InnoDB;

CREATE TABLE membership_plan_services (
    plan_id    BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    PRIMARY KEY (plan_id, service_id),
    CONSTRAINT fk_mps_plan FOREIGN KEY (plan_id) REFERENCES membership_plan (id) ON DELETE CASCADE,
    CONSTRAINT fk_mps_service FOREIGN KEY (service_id) REFERENCES service (service_id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE membership (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    business_id      BIGINT      NOT NULL,
    client_id        BIGINT      NOT NULL,
    plan_id          BIGINT      NOT NULL,
    status           VARCHAR(20) NOT NULL,
    started_at       DATE        NOT NULL,
    next_charge_date DATE        NOT NULL,
    credits_used     INT         NOT NULL DEFAULT 0,
    canceled_at      DATETIME(6),
    created_at       DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_membership_business FOREIGN KEY (business_id) REFERENCES business (id),
    CONSTRAINT fk_membership_client FOREIGN KEY (client_id) REFERENCES user (id) ON DELETE CASCADE,
    CONSTRAINT fk_membership_plan FOREIGN KEY (plan_id) REFERENCES membership_plan (id),
    INDEX idx_membership_business_status (business_id, status)
) ENGINE = InnoDB;

CREATE TABLE membership_payment (
    id            BIGINT         NOT NULL AUTO_INCREMENT,
    membership_id BIGINT         NOT NULL,
    amount        DECIMAL(10, 2) NOT NULL,
    method        VARCHAR(20)    NOT NULL,
    paid_at       DATETIME(6)    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_membership_payment_membership FOREIGN KEY (membership_id) REFERENCES membership (id) ON DELETE CASCADE
) ENGINE = InnoDB;

ALTER TABLE appointment
    ADD CONSTRAINT fk_appointment_membership FOREIGN KEY (membership_id) REFERENCES membership (id) ON DELETE SET NULL;
