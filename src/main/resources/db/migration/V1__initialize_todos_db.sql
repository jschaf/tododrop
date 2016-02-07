-- DROP SCHEMA IF EXISTS tododrop CASCADE;
--
-- CREATE SCHEMA tododrop;

CREATE TABLE tododrop.todo (
  id SERIAL PRIMARY KEY,
  title TEXT,
  completed BOOLEAN,
  "order" INT,
  url TEXT
);
