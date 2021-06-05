from flask import Flask
from flask import request, send_from_directory
app = Flask(__name__)
last_h, last_t = 0, 0

roomid = {"192.168.1.1": "1", "10.0.0.1": "2"}

block = {"1": 0, "2": 0}

rule_break = {"1": 0, "2": 0}


@app.route('/api/upload2/')
def upload2():
    h = request.args.get('humidity')
    global last_h
    last_h = h
    if int(h) > 4000:
        rule_break["2"] += 1
    return f"We received sound level room 2: {h}."

@app.route('/api/upload1/')
def upload1():
    t = request.args.get('humidity')
    global last_t
    last_t = t
    if int(t) > 4000:
        rule_break["1"] += 1
    return f"We received sound level room 1: {t}."

@app.route("/api/room/")
def get_room():
    g = request.args.get("gateway")
    return roomid[g]
    

@app.route('/')
def plot():
    print(456)
    return send_from_directory('', 'index.html')

@app.route('/get/')
def get():
    return {'h': last_h, 't': last_t}

@app.route('/api/updateblock/')
def update_block():
    r = request.args.get("room")
    b = request.args.get("status")
    block[r] = int(b)
    return f"Room {r} is now {b}"

@app.route('/api/download/')
def download():
    r = request.args.get("room")
    return str(block[r])

@app.route('/api/getbreak/')
def getbreak():
    r = request.args.get("room")
    return str(rule_break[r])
