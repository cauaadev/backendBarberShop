CREATE TABLE user (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      name VARCHAR(255) NOT NULL,
                      telefone VARCHAR(255),
                      role VARCHAR(20) NOT NULL,
                      date DATE,
                      PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE service (
                         service_id BIGINT NOT NULL AUTO_INCREMENT,
                         service_name VARCHAR(255) NOT NULL,
                         price DOUBLE NOT NULL,
                         description VARCHAR(255),
                         PRIMARY KEY (service_id)
) ENGINE=InnoDB;

CREATE TABLE schedule (
                          agendamento_id BIGINT NOT NULL AUTO_INCREMENT,
                          client_id BIGINT NOT NULL,
                          date DATETIME(6),
                          status VARCHAR(50),
                          PRIMARY KEY (agendamento_id),
                          CONSTRAINT uk_schedule_client UNIQUE (client_id),
                          CONSTRAINT fk_schedule_client FOREIGN KEY (client_id) REFERENCES user(id)
) ENGINE=InnoDB;

CREATE TABLE schedule_barbers (
                                  schedule_id BIGINT NOT NULL,
                                  barber_id BIGINT NOT NULL,
                                  PRIMARY KEY (schedule_id, barber_id),
                                  CONSTRAINT fk_schb_schedule FOREIGN KEY (schedule_id) REFERENCES schedule(agendamento_id),
                                  CONSTRAINT fk_schb_barber   FOREIGN KEY (barber_id)   REFERENCES user(id)
) ENGINE=InnoDB;

CREATE TABLE schedule_services (
                                   schedule_id BIGINT NOT NULL,
                                   service_id BIGINT NOT NULL,
                                   PRIMARY KEY (schedule_id, service_id),
                                   CONSTRAINT fk_schs_schedule FOREIGN KEY (schedule_id) REFERENCES schedule(agendamento_id),
                                   CONSTRAINT fk_schs_service  FOREIGN KEY (service_id)  REFERENCES service(service_id)
) ENGINE=InnoDB;