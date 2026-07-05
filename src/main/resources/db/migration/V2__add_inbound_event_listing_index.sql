CREATE INDEX idx_inbound_event_connection_received_id
    ON inbound_event ( connection_id, received_at DESC, id DESC );