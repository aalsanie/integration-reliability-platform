CREATE TABLE integration_connection (
                                        id UUID PRIMARY KEY,
                                        name VARCHAR(150) NOT NULL,
                                        provider_type VARCHAR(50) NOT NULL,
                                        status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                                        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                        CONSTRAINT uq_integration_connection_name_provider
                                            UNIQUE (name, provider_type),

                                        CONSTRAINT ck_integration_connection_status
                                            CHECK (status IN ('ACTIVE', 'DISABLED'))
);

CREATE TABLE inbound_event (
                               id UUID PRIMARY KEY,
                               connection_id UUID NOT NULL,
                               external_event_id VARCHAR(200) NOT NULL,
                               event_type VARCHAR(100) NOT NULL,
                               payload JSONB NOT NULL,
                               processing_status VARCHAR(30) NOT NULL DEFAULT 'RECEIVED',
                               received_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_inbound_event_connection
                                   FOREIGN KEY (connection_id)
                                       REFERENCES integration_connection (id)
                                       ON DELETE RESTRICT,

                               CONSTRAINT uq_inbound_event_external_identity
                                   UNIQUE (connection_id, external_event_id),

                               CONSTRAINT ck_inbound_event_processing_status
                                   CHECK (
                                       processing_status IN (
                                                             'RECEIVED',
                                                             'PROCESSING',
                                                             'COMPLETED',
                                                             'FAILED'
                                           )
                                       )
);