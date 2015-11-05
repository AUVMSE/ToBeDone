#!/usr/bin/python
# encoding: utf8

import psycopg2 as pg_driver
import psycopg2.pool as pool
import cherrypy
import json

MAX_THREADS = 10

class Tasks:

    exposed = True

    def GET(self, username):
        db = pg_pool.getconn()
        result_str = ""
        try:
            cur = db.cursor()
            cur.execute("SELECT * FROM Task WHERE username='{0}'".format(username))
            result = []
            rows = cur.fetchall()
            for row in rows:
                taskname = row[0]
                cur.execute("SELECT tag FROM TaskTag WHERE username='{0}' and taskname='{1}'".format(username, taskname))
                tags = [x[0] for x in cur.fetchall()]
                result.append({"taskname":row[0], "username":row[1], "description":row[2], "priority":row[3], "deadline":str(row[4]), "breakTime":row[5], "isSolved":row[6], "elapsedTime":row[7],"lastStop":str(row[8]),"isDeleted":str(row[9]), "tags":tags})
            result_str = json.dumps(result)
        finally:
            pg_pool.putconn(db)

        return result_str

    def POST(self, taskname, username, description, priority, deadline, breakTime=0, isSolved=False, elapsedTime=0, lastStop=None, isDeleted=false, tags=[]):
        db = pg_pool.getconn()
        try:
            cur = db.cursor()

            if lastStop == "" or lastStop == "None":
                lastStop = None

            cur.execute("DELETE FROM TaskTag WHERE username='{0}' and taskname='{1}'".format(username, taskname))
            cur.execute("DELETE FROM Task WHERE username='{0}' and taskname='{1}'".format(username, taskname))

            for tag in tags:
                cur.execute("INSERT INTO TaskTag (taskname, username, tag) VALUES ('{0}', '{1}', '{2}')".format(taskname, username, tag))
            insertQuery = "INSERT INTO Task (taskname, username, description, priority, deadline, breakTime, isSolved, elapsedTime, lastStop, isDeleted) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"            
            cur.execute(insertQuery, (taskname, username, description, priority, deadline, breakTime, isSolved, elapsedTime, lastStop, isDeleted))
            
            db.commit()
        finally:
            pg_pool.putconn(db)

        return ""

if __name__ == '__main__':
    pg_pool = pool.ThreadedConnectionPool(1, MAX_THREADS, user="tobedone", password="tobedone", host="localhost", dbname="tobedone")

    cherrypy.tree.mount(
        Tasks(), '/api/tasks',
        {'/':
            {'request.dispatch': cherrypy.dispatch.MethodDispatcher()}
         }
    )

    cherrypy.server.socket_host = '192.168.65.245'
    cherrypy.engine.start()
    cherrypy.engine.block()
