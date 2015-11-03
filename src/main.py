#!/usr/bin/python
# encoding: utf8

import psycopg2 as pg_driver
import psycopg2.pool as pool
import cherrypy
import json

MAX_THREADS = 10

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

    def GET(self, id=None):
        db = pg_pool.getconn()
        result_str = ""
        try:
            cur = db.cursor()
            if id is None:
                cur.execute("SELECT * FROM Tag")
                result = []
                for row in cur.fetchall():
                    result.append({"id":row[0], "name":row[1]})
                result_str = json.dumps(result)
            else:
                cur.execute("SELECT * FROM Tag WHERE id='{0}'".format(id))
                tag = cur.fetchone()
                if tag is None:
                    result_str = "id={0} not found in database".format(id)
                else:
                    result_str = json.dumps({"id":tag[0], "name":tag[1]})
        finally:
            pg_pool.putconn(db)

        return result_str

    def POST(self, name):
        db = pg_pool.getconn()
        try:
            cur = db.cursor()
            cur.execute("INSERT INTO Tag (name) VALUES ('{0}')".format(name))
            db.commit()
        finally:
            pg_pool.putconn(db)

class Tasks:

    exposed = True
    cols = ["id", "id_user", "name", "description", "priority", "deadline", "breakTime", "isSolved", "elapsedTime"]

    def GET(self, id_user=None):
        db = pg_pool.getconn()
        result_str = ""
        try:
            cur = db.cursor()
            if id_user is None:
                cur.execute("SELECT * FROM Task")
                result = []
                for row in cur.fetchall():
                    result.append(dict(zip(cols, row)))
                result_str = json.dumps(result)
            else:
                cur.execute("SELECT * FROM Task WHERE id_user='{0}'".format(id_user))
                task = cur.fetchall()
                for row in cur.fetchall():
                    result.append(dict(zip(cols, row)))
                result_str = json.dumps(result)
        finally:
            pg_pool.putconn(db)

        return result_str

    def POST(self, id_user, name, description="", priority, deadline, breakTime=0, isSolved=False, elapsedTime=0):
        db = pg_pool.getconn()
        try:
            cur = db.cursor()
            cur.execute('''
                INSERT INTO Tag (id_user, name, description, priority, deadline, breakTime, isSolved, elapsedTime)
                VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}', '{7}')
                '''.format(id_user, name, description, priority, deadline, breakTime, isSolved, elapsedTime))
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
        Tasks(), '/api/tasks',
        {'/':
            {'request.dispatch': cherrypy.dispatch.MethodDispatcher()}
         }
    )

    cherrypy.engine.start()
    cherrypy.engine.block()
