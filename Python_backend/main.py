import flask
from flask import request, send_file, send_from_directory
import sqlite3
import datetime
import json
import io

DATABASE = "database.db"

app = flask.Flask(__name__)
app.config["DEBUG"] = True



@app.route('/users/id/<userID>', methods=['GET'])
def getUser(userID):
    con = sqlite3.connect(DATABASE)
    cur = con.cursor()

    cur.execute("select * from users where userID = ? limit 1", userID)
    _, last_name, middle_name, first_name = cur.fetchall()[0]

    con.close()

    return  f"{last_name} {middle_name} {first_name}"


@app.route('/api/users', methods=['POST'])
def postUser():
    con = sqlite3.connect(DATABASE)
    cur = con.cursor()

    firstname = request.form.get('firstname')
    middlename = request.form.get('middlename')
    lastname = request.form.get('lastname')

    cur.execute("insert into users (first_name, middle_name, last_name) values (?,?,?)", (firstname, middlename, lastname) )

    con.commit()
    con.close()

    return  f"{cur.lastrowid}"


@app.route('/api/dbs', methods=['POST'])
def postDb():
    con = sqlite3.connect(DATABASE)
    cur = con.cursor()

    dbFile = request.files["db_file"]
    user_id = request.form.get('user_id')
    db_name = request.form.get('name')

    # print(f"the db file is: {dbFile} with size: {dbFile.__sizeof__()}")

    date = datetime.datetime.now()

    cur.execute("insert into backup (date, userID, db_file, name) values (?,?,?,?)", (date, user_id, dbFile.read(), db_name) )

    con.commit()
    con.close()

    result = {}
    result["id"] = cur.lastrowid
    result["date"] = str(date)
    result["name"] = db_name

    return  json.dumps(result, indent=4)



@app.route('/api/dbs/list', methods=['GET'])
def getBackUpList():
    con = sqlite3.connect(DATABASE)
    cur = con.cursor()

    user_id = request.args.get('user_id')

    array_dictionary = []

    cur.execute("select * from backup where userID = ? order by date DESC", user_id)
    backupList = cur.fetchall()

    for backup in backupList:
        dictionary = {}
        id, date, usID, db, name = backup
        dictionary["id"] = id
        dictionary["date"] = date
        dictionary["name"] = name
        array_dictionary.append(dictionary)

    con.close()

    return json.dumps(array_dictionary, indent=4)


@app.route('/api/dbs', methods=['GET'])
def getBackUp():
    con = sqlite3.connect(DATABASE)
    cur = con.cursor()

    user_id = request.args.get('user_id')
    db_id = request.args.get('db_id')

    cur.execute("select * from backup where userID = ? and backID = ? limit 1", (user_id, db_id))
    backup = cur.fetchall()[0]

    ID, DATE, userID, dbFILE = backup

    con.close()

    return send_file(io.BytesIO(dbFILE), as_attachment=True, mimetype="application/x-sqlite3", download_name="BackUp.db")



app.run()
