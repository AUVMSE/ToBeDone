DROP TABLE IF EXISTS TaskTag;
DROP TABLE IF EXISTS Task;


CREATE TABLE Task (
	taskname TEXT,
	username TEXT,
	description TEXT,
	priority INT,
	deadline TIMESTAMP,
	breakTime INT,
	isSolved BOOLEAN,
	elapsedTime INT,
	lastStop TIMESTAMP);

CREATE TABLE TaskTag(
  taskname TEXT,
  username TEXT,
  tag TEXT);