DELETE FROM users;
DELETE FROM bookings;
DELETE FROM items;
DELETE FROM comments;
DELETE FROM requests;

ALTER TABLE users ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE bookings ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE items ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE comments ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE requests ALTER COLUMN ID RESTART WITH 1;