ALTER TABLE audit_logs
ADD COLUMN entity_type VARCHAR(50),
ADD COLUMN entity_id UUID,
ADD COLUMN metadata JSONB;

CREATE INDEX idx_audit_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at);