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

if __name__ == '__main__':
    pg_pool = pool.ThreadedConnectionPool(1, MAX_THREADS, user="tobedone", password="tobedone", host="localhost", dbname="tobedone")

    cherrypy.tree.mount(
        Users(), '/api/users',
        {'/':
            {'request.dispatch': cherrypy.dispatch.MethodDispatcher()}
         }
    )

    cherrypy.engine.start()
    cherrypy.engine.block()
