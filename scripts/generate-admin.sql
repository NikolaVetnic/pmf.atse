-- creates admin user with [ admin : adMIN1234! ] credentials in PostgreSQL database
INSERT INTO "users"("user_type", "id", "password", "role", "username", "version")
VALUES ('AdminEntity', 0, '$2a$10$IqKwbfjy3mXtHQ7LlpgDQe2DX7.vVJqgz3lgU7wwkUuSu07KVjHcG', 'ADMIN', 'admin', 0);

INSERT INTO "admins"("id") VALUES (0);