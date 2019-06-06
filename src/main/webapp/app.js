var ws;

function connect() {
    var userId = document.getElementById("userId").value;
    ws = new WebSocket("ws://" + document.location.host + "/game/" + userId);
    var gameId = document.getElementById("gameId").value;

    ws.onmessage = function (event) {
        var log = document.getElementById("log");
        console.log(event.data);
        var message = JSON.parse(event.data);
        log.innerHTML += message.gameId + " : " + message.moveString + "\n";
    };
}

function send() {
    var moveString = document.getElementById("msg").value;
    var userId = document.getElementById("userId").value;
    var gameId = document.getElementById("gameId").value;
    var json = JSON.stringify({
        "gameId": gameId,
        "userId": userId,
        "moveString": moveString

    });

    ws.send(json);
}
