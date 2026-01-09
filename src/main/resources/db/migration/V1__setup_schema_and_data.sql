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
insert into users(username, email, password, avatar)
values ('user', 'user@mail.com', '{bcrypt}$2a$10$wUSCcv6UnBKPT8apoANhUeKVndElryiVMQw0t1wSJWJ/hbH8tHhH.', 'https://primefaces.org/cdn/primevue/images/avatar/ionibowcher.png')
ON CONFLICT (username) DO NOTHING;

insert into users(username, email, password, avatar)
values ('admin', 'admin@mail.com', '{bcrypt}$2a$10$wUSCcv6UnBKPT8apoANhUeKVndElryiVMQw0t1wSJWJ/hbH8tHhH.', 'https://primefaces.org/cdn/primevue/images/avatar/xuxuefeng.png')
ON CONFLICT (username) DO NOTHING;

-- Seed participants
insert into users(username, email, password, avatar)
values ('testuser', 'test@mail.com', '{bcrypt}$2a$10$wUSCcv6UnBKPT8apoANhUeKVndElryiVMQw0t1wSJWJ/hbH8tHhH.', 'https://primefaces.org/cdn/primevue/images/avatar/amyelsner.png')
ON CONFLICT (username) DO NOTHING;

insert into users(username, email, password, avatar)
values ('sven_goteborg', 'sven@mail.se', '{bcrypt}$2a$10$wUSCcv6UnBKPT8apoANhUeKVndElryiVMQw0t1wSJWJ/hbH8tHhH.', 'https://primefaces.org/cdn/primevue/images/avatar/asiyajavayant.png')
ON CONFLICT (username) DO NOTHING;

insert into users(username, email, password, avatar)
values ('karin_k', 'karin@mail.se', '{bcrypt}$2a$10$wUSCcv6UnBKPT8apoANhUeKVndElryiVMQw0t1wSJWJ/hbH8tHhH.', 'https://primefaces.org/cdn/primevue/images/avatar/annafali.png')
ON CONFLICT (username) DO NOTHING;

insert into users(username, email, password, avatar)
values ('olof_p', 'olof@mail.se', '{bcrypt}$2a$10$wUSCcv6UnBKPT8apoANhUeKVndElryiVMQw0t1wSJWJ/hbH8tHhH.', 'https://primefaces.org/cdn/primevue/images/avatar/onyamalimba.png')
ON CONFLICT (username) DO NOTHING;

-- Map authorities
insert into authorities (user_id, authority)
values ((select id from users where username = 'user'), 'ROLE_USER')
ON CONFLICT (user_id, authority) DO NOTHING;

insert into authorities (user_id, authority)
values ((select id from users where username = 'admin'), 'ROLE_ADMIN')
ON CONFLICT (user_id, authority) DO NOTHING;

insert into authorities (user_id, authority)
values ((select id from users where username = 'testuser'), 'ROLE_USER')
ON CONFLICT (user_id, authority) DO NOTHING;

insert into authorities (user_id, authority)
values ((select id from users where username = 'sven_goteborg'), 'ROLE_USER')
ON CONFLICT (user_id, authority) DO NOTHING;

insert into authorities (user_id, authority)
values ((select id from users where username = 'karin_k'), 'ROLE_USER')
ON CONFLICT (user_id, authority) DO NOTHING;

insert into authorities (user_id, authority)
values ((select id from users where username = 'olof_p'), 'ROLE_USER')
ON CONFLICT (user_id, authority) DO NOTHING;

-- Seed Addresses
insert into addresses (street, city, zip_code, country, location_name)
select 'Lindholmspiren 3', 'Göteborg', '417 56', 'Sweden', 'Lindholmen Science Park'
where not exists (select 1 from addresses where location_name = 'Lindholmen Science Park');

insert into addresses (street, city, zip_code, country, location_name)
select 'Örgrytevägen 5', 'Göteborg', '412 51', 'Sweden', 'Liseberg'
where not exists (select 1 from addresses where location_name = 'Liseberg');

insert into addresses (street, city, zip_code, country, location_name)
select 'Slottskogsvägen', 'Göteborg', '413 08', 'Sweden', 'Slottsskogen'
where not exists (select 1 from addresses where location_name = 'Slottsskogen');

insert into addresses (street, city, zip_code, country, location_name)
select 'Kungsportsavenyen 10', 'Göteborg', '411 36', 'Sweden', 'Kungsparken'
where not exists (select 1 from addresses where location_name = 'Kungsparken');

insert into addresses (street, city, zip_code, country, location_name)
select 'Ringön 4', 'Göteborg', '418 34', 'Sweden', 'Älvsborgsbron'
where not exists (select 1 from addresses where location_name = 'Älvsborgsbron');

insert into addresses (street, city, zip_code, country, location_name)
select 'Södra Vägen 50', 'Göteborg', '412 54', 'Sweden', 'Universeum'
where not exists (select 1 from addresses where location_name = 'Universeum');

-- Seed Events
insert into events (title, description, date, address_id)
select 'Gothenburg Tech Meetup', 'A gathering for tech enthusiasts in Gothenburg to discuss the latest trends in software development and AI.', '2024-06-15T18:00:00.000Z', (select id from addresses where location_name = 'Lindholmen Science Park')
where not exists (select 1 from events where title = 'Gothenburg Tech Meetup');

insert into events (title, description, date, address_id)
select 'Summer Party at Liseberg', 'Annual team celebration at Scandinavia''s largest amusement park.', '2024-07-20T14:00:00.000Z', (select id from addresses where location_name = 'Liseberg')
where not exists (select 1 from events where title = 'Summer Party at Liseberg');

insert into events (title, description, date, address_id)
select 'Midsummer Workshop', 'Strategy workshop followed by traditional midsummer celebrations.', '2024-06-21T09:00:00.000Z', (select id from addresses where location_name = 'Slottsskogen')
where not exists (select 1 from events where title = 'Midsummer Workshop');

-- Seed Event Organizers
insert into event_organizers (event_id, user_id)
select (select id from events where title = 'Gothenburg Tech Meetup'), (select id from users where username = 'testuser')
where exists (select 1 from events where title = 'Gothenburg Tech Meetup') 
  and exists (select 1 from users where username = 'testuser')
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_organizers (event_id, user_id)
select (select id from events where title = 'Summer Party at Liseberg'), (select id from users where username = 'testuser')
where exists (select 1 from events where title = 'Summer Party at Liseberg')
  and exists (select 1 from users where username = 'testuser')
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_organizers (event_id, user_id)
select (select id from events where title = 'Midsummer Workshop'), (select id from users where username = 'sven_goteborg')
where exists (select 1 from events where title = 'Midsummer Workshop')
  and exists (select 1 from users where username = 'sven_goteborg')
ON CONFLICT (event_id, user_id) DO NOTHING;

-- Seed Event Members
insert into event_members (event_id, user_id)
values ((select id from events where title = 'Gothenburg Tech Meetup'), (select id from users where username = 'sven_goteborg'))
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_members (event_id, user_id)
values ((select id from events where title = 'Gothenburg Tech Meetup'), (select id from users where username = 'karin_k'))
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_members (event_id, user_id)
values ((select id from events where title = 'Summer Party at Liseberg'), (select id from users where username = 'sven_goteborg'))
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_members (event_id, user_id)
values ((select id from events where title = 'Summer Party at Liseberg'), (select id from users where username = 'karin_k'))
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_members (event_id, user_id)
values ((select id from events where title = 'Summer Party at Liseberg'), (select id from users where username = 'olof_p'))
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_members (event_id, user_id)
values ((select id from events where title = 'Midsummer Workshop'), (select id from users where username = 'testuser'))
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_members (event_id, user_id)
values ((select id from events where title = 'Midsummer Workshop'), (select id from users where username = 'karin_k'))
ON CONFLICT (event_id, user_id) DO NOTHING;

-- Seed Tasks
insert into tasks (description, completed, due_date, assigned_to_id, event_id)
values ('Book meeting room at Lindholmen', true, '2024-06-10T10:00:00.000Z', (select id from users where username = 'testuser'), (select id from events where title = 'Gothenburg Tech Meetup'))
ON CONFLICT (description, event_id) DO NOTHING;

insert into tasks (description, completed, due_date, assigned_to_id, event_id)
values ('Prepare presentation slides', false, '2024-06-14T17:00:00.000Z', (select id from users where username = 'sven_goteborg'), (select id from events where title = 'Gothenburg Tech Meetup'))
ON CONFLICT (description, event_id) DO NOTHING;

insert into tasks (description, completed, due_date, assigned_to_id, event_id)
values ('Order Liseberg entrance tickets', true, '2024-07-01T09:00:00.000Z', (select id from users where username = 'testuser'), (select id from events where title = 'Summer Party at Liseberg'))
ON CONFLICT (description, event_id) DO NOTHING;

insert into tasks (description, completed, due_date, assigned_to_id, event_id)
values ('Rent picnic equipment', false, '2024-07-15T12:00:00.000Z', (select id from users where username = 'karin_k'), (select id from events where title = 'Summer Party at Liseberg'))
ON CONFLICT (description, event_id) DO NOTHING;

insert into tasks (description, completed, due_date, assigned_to_id, event_id)
values ('Confirm dinner reservation', false, '2024-07-18T15:00:00.000Z', (select id from users where username = 'olof_p'), (select id from events where title = 'Summer Party at Liseberg'))
ON CONFLICT (description, event_id) DO NOTHING;

insert into tasks (description, completed, due_date, assigned_to_id, event_id)
values ('Buy midsummer flowers', false, '2024-06-20T16:00:00.000Z', (select id from users where username = 'testuser'), (select id from events where title = 'Midsummer Workshop'))
ON CONFLICT (description, event_id) DO NOTHING;

-- Seed 2026 Events
insert into events (title, description, date, address_id)
select 'Gothenburg Cloud Summit 2026', 'A one-day summit focusing on cloud native architectures, Kubernetes, and observability.', '2026-03-10T09:00:00Z', (select id from addresses where location_name = 'Kungsparken')
where not exists (select 1 from events where title = 'Gothenburg Cloud Summit 2026');

insert into events (title, description, date, address_id)
select 'Summer Hackathon 2026', '48-hour hackathon for students and professionals with mentorship and prizes.', '2026-07-05T10:00:00Z', (select id from addresses where location_name = 'Universeum')
where not exists (select 1 from events where title = 'Summer Hackathon 2026');

insert into events (title, description, date, address_id)
select 'Winter AI Meetup 2026', 'Evening talks and networking on applied AI and ML in industry.', '2026-12-02T18:00:00Z', (select id from addresses where location_name = 'Älvsborgsbron')
where not exists (select 1 from events where title = 'Winter AI Meetup 2026');

insert into event_organizers (event_id, user_id)
select (select id from events where title = 'Gothenburg Cloud Summit 2026'), (select id from users where username = 'testuser')
where exists (select 1 from events where title = 'Gothenburg Cloud Summit 2026')
  and exists (select 1 from users where username = 'testuser')
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_organizers (event_id, user_id)
select (select id from events where title = 'Summer Hackathon 2026'), (select id from users where username = 'user')
where exists (select 1 from events where title = 'Summer Hackathon 2026')
  and exists (select 1 from users where username = 'user')
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_organizers (event_id, user_id)
select (select id from events where title = 'Winter AI Meetup 2026'), (select id from users where username = 'sven_goteborg')
where exists (select 1 from events where title = 'Winter AI Meetup 2026')
  and exists (select 1 from users where username = 'sven_goteborg')
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_members (event_id, user_id)
values ((select id from events where title = 'Gothenburg Cloud Summit 2026'), (select id from users where username = 'sven_goteborg'))
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_members (event_id, user_id)
values ((select id from events where title = 'Gothenburg Cloud Summit 2026'), (select id from users where username = 'karin_k'))
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_members (event_id, user_id)
values ((select id from events where title = 'Summer Hackathon 2026'), (select id from users where username = 'olof_p'))
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_members (event_id, user_id)
values ((select id from events where title = 'Summer Hackathon 2026'), (select id from users where username = 'testuser'))
ON CONFLICT (event_id, user_id) DO NOTHING;

insert into event_members (event_id, user_id)
values ((select id from events where title = 'Winter AI Meetup 2026'), (select id from users where username = 'karin_k'))
ON CONFLICT (event_id, user_id) DO NOTHING;

-- Seed Tasks for 2026 events (assigned to existing users)
insert into tasks (description, completed, due_date, assigned_to_id, event_id)
values ('Finalize speaker lineup', false, '2026-02-25T12:00:00Z', (select id from users where username = 'testuser'), (select id from events where title = 'Gothenburg Cloud Summit 2026'))
ON CONFLICT (description, event_id) DO NOTHING;

insert into tasks (description, completed, due_date, assigned_to_id, event_id)
values ('Prepare hackathon kit and swag', false, '2026-06-20T17:00:00Z', (select id from users where username = 'user'), (select id from events where title = 'Summer Hackathon 2026'))
ON CONFLICT (description, event_id) DO NOTHING;

insert into tasks (description, completed, due_date, assigned_to_id, event_id)
values ('Book venue and AV for Winter AI Meetup', false, '2026-11-15T10:00:00Z', (select id from users where username = 'sven_goteborg'), (select id from events where title = 'Winter AI Meetup 2026'))
ON CONFLICT (description, event_id) DO NOTHING;
