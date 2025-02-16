#!/bin/bash
set -e

psql -U postgres <<EOF
\connect postgres
CREATE DATABASE crud;
ALTER DATABASE crud OWNER TO postgres;
\c crud
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255),
    firstname TEXT,
    isActive BOOLEAN NOT NULL DEFAULT FALSE
);
EOF
