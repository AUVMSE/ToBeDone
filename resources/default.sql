INSERT INTO Task(taskname,
 username,
 description,
 priority,
 deadline,
 breakTime,
 isSolved,
 isDeleted,
 elapsedTime,
 lastStop) VALUES ('book',
'emulator', 'bla bla', 5, '2015-11-06', 3600, 'f', 'f', 1200, '2015-11-03');
INSERT INTO Task(taskname,
 username,
 description,
 priority,
 deadline,
 breakTime,
 isSolved,
 isDeleted,
 elapsedTime,
 lastStop) VALUES ('shop',
'emulator', 'bla bla', 3, '2015-12-03', 3600, 'f', 'f', 3600, '2015-11-01');
INSERT INTO Task(taskname,
 username,
 description,
 priority,
 deadline,
 breakTime,
 isSolved,
 isDeleted,
 elapsedTime,
 lastStop) VALUES ('home',
'emulator', 'bla bla', 5, '2015-11-06', 1000, 'f', 'f', 1200, '2014-07-03');




insert into tasktag(taskname,
  username,
  tag) VALUES ('book', 'emulator', 'tag1');
insert into tasktag(taskname,
  username,
  tag) VALUES ('shop', 'emulator', 'tag2');
insert into tasktag(taskname,
  username,
  tag) VALUES ('book', 'emulator', 'TAG3');
	insert into tasktag(taskname,
  username,
  tag) VALUES ('book', 'emulator', 'tag2');
	insert into tasktag(taskname,
  username,
  tag) VALUES ('home', 'emulator', 'tag3');