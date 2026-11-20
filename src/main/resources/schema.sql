CREATE TABLE IF NOT EXISTS members (
    id INTEGER PRIMARY KEY,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS access_tokens (
    token_hash TEXT NOT NULL PRIMARY KEY,
    member_id INTEGER NOT NULL REFERENCES members (id) ON DELETE CASCADE,
    expires_at INTEGER NOT NULL
);
