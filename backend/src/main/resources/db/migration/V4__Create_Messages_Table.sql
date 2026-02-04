-- Create messages table for announcement chat feature
CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    message TEXT NOT NULL,
    sender_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    announcement_id BIGINT NOT NULL REFERENCES announcements(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index for better query performance (only if they don't exist)
CREATE INDEX IF NOT EXISTS idx_messages_announcement_id ON messages(announcement_id);
CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messages(created_at);