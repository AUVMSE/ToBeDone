DROP TABLE IF EXISTS TaskTag;
DROP TABLE IF EXISTS Task;


CREATE TABLE Task (
	taskname TEXT,
	username TEXT,
	description TEXT,
	priority INT,
	deadline DATE,
	breakTime INT,
	isSolved BOOLEAN,
	elapsedTime INT,
	lastStop TIMESTAMP,
	isDeleted BOOLEAN);

CREATE TABLE TaskTag(
  taskname TEXT,
  username TEXT,
  tag TEXT);
