#!/usr/bin/python
# encoding: utf8

import psycopg2 as pg_driver
import psycopg2.pool as pool
import cherrypy
import json

MAX_THREADS = 10
CREATED_OFFLINE = -1

class Users:

    exposed = True

    def GET(self, id=None):
        db = pg_pool.getconn()
        result_str = ""
        try:
            cur = db.cursor()
            if id is None:
                cur.execute("SELECT * FROM AndroidUser")
                result = []
                for row in cur.fetchall():
                    result.append({"id":row[0], "name":row[1]})
                result_str = json.dumps(result)
            else:
                cur.execute("SELECT * FROM AndroidUser WHERE id='{0}'".format(id))
                user = cur.fetchone()
                if user is None:
                    result_str = "id={0} not found in database".format(id)
                else:
                    result_str = json.dumps({"id":user[0], "name":user[1]})
        finally:
            pg_pool.putconn(db)

        return result_str

    def POST(self, name):
        db = pg_pool.getconn()
        try:
            cur = db.cursor()
            cur.execute("INSERT INTO AndroidUser (name) VALUES ('{0}')".format(name))
            db.commit()
        finally:
            pg_pool.putconn(db)


class Tags:

    exposed = True

    def GET(self, id_task=None):
        db = pg_pool.getconn()
        result_str = ""
        try:
            cur = db.cursor()
            if id_task is None:
                cur.execute("SELECT * FROM Tag")
                result = []
                for row in cur.fetchall():
                    result.append({"id":row[0], "name":row[1]})
                result_str = json.dumps(result)
            else:
                cur.execute("SELECT Tag.id, Tag.name FROM Tag, TaskTag WHERE TaskTag.id_task='{0}' AND TaskTag.id_tag = Tag.id".format(id_task))
                result = []
                for row in cur.fetchall():
                    result.append({"id":row[0], "name":row[1]})
                if len(result) == 0:
                    result_str = "Task with id={0} has no tags".format(id_task)
                else:
                    result_str = json.dumps(result)
        finally:
            pg_pool.putconn(db)

        return result_str

class Tasks:

    exposed = True

    def __init__(self):
        self.cols = ["id", "id_user", "name", "description", "priority", "deadline", "breakTime", "isSolved", "elapsedTime", "lastStop"]

    def GET(self, name=None):
        db = pg_pool.getconn()
        result_str = ""
        try:
            cur = db.cursor()
            if name is None:
                cur.execute("SELECT * FROM Task")
                result = []
                for row in cur.fetchall():
                    result.append(dict(zip(self.cols, [str(i) for i in row])))
                result_str = json.dumps(result)
            else:
                cur.execute("SELECT id FROM AndroidUser WHERE name='{0}'".format(name))
                id_user = cur.fetchone()[0]
                cur.execute("SELECT * FROM Task WHERE id_user='{0}'".format(id_user))
                result = []
                for row in cur.fetchall():
                    result.append(dict(zip(self.cols, [str(i) for i in row])))
                result_str = json.dumps(result)
        finally:
            pg_pool.putconn(db)

        return result_str

    def POST(self, id_user, name, priority, deadline, description="", breakTime=0, isSolved=False, elapsedTime=0, tags=[], lastStop=None):
        db = pg_pool.getconn()
        try:
            cur = db.cursor()
            if lastStop != None:
                cur.execute('''
                    INSERT INTO Task (id_user, name, description, priority, deadline, breakTime, isSolved, elapsedTime, lastStop)
                    VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}', '{7}', '{8}') RETURNING id
                    '''.format(id_user, name, description, priority, deadline, breakTime, isSolved, elapsedTime, lastStop))
            else:
                cur.execute('''
                    INSERT INTO Task (id_user, name, description, priority, deadline, breakTime, isSolved, elapsedTime)
                    VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}', '{7}') RETURNING id
                    '''.format(id_user, name, description, priority, deadline, breakTime, isSolved, elapsedTime, lastStop))
            id_task = cur.fetchone()[0]
            for tag in tags:
                cur.execute("SELECT COUNT(*) FROM Tag WHERE name='{0}'".format(tag))
                t = int(cur.fetchone()[0])
                if t == 0:
                    cur.execute("INSERT INTO Tag (name) VALUES ('{0}')".format(tag))
                    db.commit()
                cur.execute("SELECT id FROM Tag WHERE name='{0}'".format(tag))
                id_tag = cur.fetchone()[0]
                cur.execute("INSERT INTO TaskTag (id_task, id_tag) VALUES ('{0}', '{1}')".format(id_task, id_tag))
            db.commit()
        finally:
            pg_pool.putconn(db)

        return json.dumps({"id":id_task})

    def PUT(self, id, id_user, name, priority, deadline, description="", breakTime=0, isSolved=False, elapsedTime=0, tags=[], lastStop=None):
        db = pg_pool.getconn()
        try:
            cur = db.cursor()
            if id == CREATED_OFFLINE:
                cur.execute('''
                INSERT INTO Task (id_user, name, description, priority, deadline, breakTime, isSolved, elapsedTime, lastStop)
                VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}', '{7}', '{8}') RETURNING id
                '''.format(id_user, name, description, priority, deadline, breakTime, isSolved, elapsedTime))
                id = cur.fetchone()[0]
            else:
                cur.execute('''
                UPDATE Task 
                SET 
                id_user         '{0}', 
                name            '{1}', 
                description     '{2}', 
                priority        '{3}', 
                deadline        '{4}', 
                breakTime       '{5}', 
                isSolved        '{6}', 
                elapsedTime     '{7}',
                lastStop        '{8}'
                WHERE
                id={9}
                '''.format(id_user, name, description, priority, deadline, breakTime, isSolved, elapsedTime, lastStop, id))
            db.commit()
        finally:
            pg_pool.putconn(db)
        return json.dumps({"id":id})

class TaskTags:

    exposed = True

    def POST(self, id_task, id_tag):
        db = pg_pool.getconn()
        try:
            cur = db.cursor()
            cur.execute("INSERT INTO TaskTag (id_task, id_tag) VALUES ('{0}', '{1}')".format(id_task, id_tag))
            db.commit()
        finally:
            pg_pool.putconn(db)

    def DELETE(self, id_task):
        db = pg_pool.getconn()
        try:
            cur = db.cursor()
            cur.execute("DELETE FROM TaskTag WHERE id_task={0}".format(id_task))
            db.commit()
        finally:
            pg_pool.putconn(db)

if __name__ == '__main__':
    pg_pool = pool.ThreadedConnectionPool(1, MAX_THREADS, user="tobedone", password="tobedone", host="localhost", dbname="tobedone")

    cherrypy.tree.mount(
        Users(), '/api/users',
        {'/':
            {'request.dispatch': cherrypy.dispatch.MethodDispatcher()}
         }
    )

    cherrypy.tree.mount(
        Tags(), '/api/tags',
        {'/':
            {'request.dispatch': cherrypy.dispatch.MethodDispatcher()}
         }
    )

    cherrypy.tree.mount(
        TaskTags(), '/api/tasktags',
        {'/':
            {'request.dispatch': cherrypy.dispatch.MethodDispatcher()}
         }
    )

    cherrypy.tree.mount(
        Tasks(), '/api/tasks',
        {'/':
            {'request.dispatch': cherrypy.dispatch.MethodDispatcher()}
         }
    )

    cherrypy.server.socket_host = '192.168.1.19'
    cherrypy.engine.start()
    cherrypy.engine.block()
