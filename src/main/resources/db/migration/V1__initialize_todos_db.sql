-- DROP SCHEMA IF EXISTS tododrop CASCADE;
--
-- CREATE SCHEMA tododrop;

CREATE TABLE todo (
  id SERIAL PRIMARY KEY,
  title TEXT,
  completed BOOLEAN,
  "order" INT,
  url TEXT
);
