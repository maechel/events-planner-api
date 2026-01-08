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
