DROP TABLE IF EXISTS AndroidUser;
DROP TABLE IF EXISTS Task;
DROP TABLE IF EXISTS Tag;
DROP TABLE IF EXISTS TaskTag;

CREATE TABLE AndroidUser (
	id SERIAL PRIMARY KEY,
	name TEXT UNIQUE);

CREATE TABLE Tag (
	id SERIAL PRIMARY KEY,
	name TEXT UNIQUE);

CREATE TABLE Task (
	id SERIAL PRIMARY KEY,
	id_user INT REFERENCES AndroidUser,
	name TEXT,
	description TEXT,
	priority INT,
	deadline DATE,
	breakTime INT,
	isSolved BOOLEAN,
	elapsedTime INT);

CREATE TABLE TaskTag(
  id_task INT REFERENCES Task,
  id_tag INT REFERENCES Tag);