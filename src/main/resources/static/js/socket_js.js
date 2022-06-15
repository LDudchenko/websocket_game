const url = 'http://localhost:8080';
let stompClient;
let gameId;
let playerType;

function connectToSocket(gameId) {

    console.log("connecting to the game");
    let socket = new SockJS(url + "/gameplay");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected to the frame: " + frame);
        stompClient.subscribe("/topic/game-progress/" + gameId, function (response) {
            let data = JSON.parse(response.body);
            console.log(data);
            displayResponse(data);
            var oponentField = document.getElementById("oponentLogin");
            oponentField.innerHTML = 'Oponents: '+ data.player1.login + ' and ' + data.player2.login;
        })
    })
}

function create_game() {
    let login = document.getElementById("login").value;
    if (login == null || login === '') {
        alert("Please enter login");
    } else {
        $.ajax({
            url: url + "/game/start",
            type: 'POST',
            dataType: "json",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "login": login
            }),
            success: function (data) {
                gameId = data.gameId;
                playerType = 'X';
                reset();
                copyTextToClipboard(gameId)
                connectToSocket(gameId);
                alert("You created a game. Game id is: " + data.gameId+"\nGame id is already copied to your clipboard!\nSimply use it!");
                gameOn = true;
                var signField = document.getElementById("sign");
                signField.innerHTML = 'Your sign is \'X\'';
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}


function connectToRandom() {
    let login = document.getElementById("login").value;
    if (login == null || login === '') {
        alert("Please enter login");
    } else {
        $.ajax({
            url: url + "/game/connect/random",
            type: 'POST',
            dataType: "json",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "login": login
            }),
            success: function (data) {
                gameId = data.gameId;
                playerType = 'O';
                reset();
                connectToSocket(gameId);
                alert("Congrats you're playing with: " + data.player1.login);
                var signField = document.getElementById("sign");
                signField.innerHTML = 'Your sign is \'0\'';
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}

function connectToSpecificGame() {
    let login = document.getElementById("login").value;
    if (login == null || login === '') {
        alert("Please enter login");
    } else {
        let gameIdFromHtml = document.getElementById("game_id").value;
        if (gameIdFromHtml == null || gameIdFromHtml === '') {
            alert("Please enter game id");
        }
        $.ajax({
            url: url + "/game/connect",
            type: 'POST',
            dataType: "json",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "player": {
                    "login": login
                },
                "gameId": gameIdFromHtml
            }),
            success: function (data) {
                gameId = data.gameId;
                playerType = 'O';
                reset();
                console.log(gameId);
                connectToSocket(gameId);
                alert("Congrats you're playing with: " + data.player1.login);
                var signField = document.getElementById("sign");
                signField.innerHTML = 'Your sign is \'O\'';
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}
