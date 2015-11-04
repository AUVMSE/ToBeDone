DROP TABLE IF EXISTS TaskTag;
DROP TABLE IF EXISTS Task;
DROP TABLE IF EXISTS Tag;
DROP TABLE IF EXISTS AndroidUser;


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
	deadline TIMESTAMP,
	breakTime INT,
	isSolved BOOLEAN,
	elapsedTime INT,
	lastStop TIMESTAMP);

CREATE TABLE TaskTag(
  id_task INT REFERENCES Task,
  id_tag INT REFERENCES Tag);

CREATE TABLE RandomTasks(
  id SERIAL PRIMARY KEY,
  name TEXT,
  priority INT,
  id_tag INT REFERENCES Tag);