-- Initial Schema
drop table if exists event_members;
drop table if exists event_organizers;
drop table if exists tasks;
drop table if exists events;
drop table if exists addresses;
drop table if exists authorities;
drop table if exists users;

CREATE TABLE users
(
    id                    uuid PRIMARY KEY            DEFAULT gen_random_uuid(),
    username              text                        NOT NULL UNIQUE,
    email                 text                        UNIQUE,
    password              text                        NOT NULL,
    enabled               boolean                     NOT NULL DEFAULT true,
    account_non_locked    boolean                     NOT NULL DEFAULT true,
    failed_login_attempts integer                     NOT NULL DEFAULT 0,
    last_login            TIMESTAMP WITH TIME ZONE,
    avatar                text,
    created_at            TIMESTAMP WITH TIME ZONE    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP WITH TIME ZONE    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE authorities
(
    user_id   uuid NOT NULL,
    authority text NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX ix_auth_username ON authorities (user_id, authority);

-- Create addresses table
CREATE TABLE addresses
(
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    street        text NOT NULL,
    city          text NOT NULL,
    zip_code      text,
    country       text NOT NULL,
    location_name text,
    latitude      double precision,
    longitude     double precision
);

-- Create events table
CREATE TABLE events
(
    id            uuid PRIMARY KEY         DEFAULT gen_random_uuid(),
    title         text           NOT NULL UNIQUE,
    description   text,
    date          TIMESTAMP WITH TIME ZONE NOT NULL,
    address_id    uuid REFERENCES addresses (id) ON DELETE SET NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table for event tasks
CREATE TABLE tasks
(
    id             uuid PRIMARY KEY         DEFAULT gen_random_uuid(),
    description    text           NOT NULL,
    completed      boolean        NOT NULL  DEFAULT false,
    due_date       TIMESTAMP WITH TIME ZONE,
    assigned_to_id uuid REFERENCES users (id) ON DELETE SET NULL,
    event_id       uuid REFERENCES events (id) ON DELETE CASCADE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_task_per_event UNIQUE (description, event_id)
);

-- Many-to-many relationship for organizers
CREATE TABLE event_organizers
(
    event_id uuid NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    user_id  uuid NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, user_id)
);

-- Many-to-many relationship for members
CREATE TABLE event_members
(
    event_id uuid NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    user_id  uuid NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, user_id)
);
