var turns = [["#", "#", "#"], ["#", "#", "#"], ["#", "#", "#"]];
var turn = "";
var pTurn = 'X';

function playerTurn(turn, id) {
    setTurn(gameId);
    if (pTurn == playerType) {
        var spotTaken = $("#" + id).text();
        if (spotTaken === "#") {
            makeAMove(playerType, id.split("_")[0], id.split("_")[1]);
        }
    }
}

function makeAMove(type, xCoordinate, yCoordinate) {
    $.ajax({
        url: url + "/game/gameplay",
        type: 'POST',
        dataType: "json",
        async: false,
        contentType: "application/json",
        data: JSON.stringify({
            "type": type,
            "coordinateX": xCoordinate,
            "coordinateY": yCoordinate,
            "gameId": gameId
        }),
        success: function (data) {
            displayResponse(data);
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function displayResponse(data) {
    let board = data.board;
    for (let i = 0; i < board.length; i++) {
        for (let j = 0; j < board[i].length; j++) {
            if (board[i][j] === 1) {
                turns[i][j] = 'X'
            } else if (board[i][j] === 2) {
                turns[i][j] = 'O';
            }
            let id = i + "_" + j;
            $("#" + id).text(turns[i][j]);
        }
    }
    if (data.winner != null) {
        alert("Winner is " + data.winner);
    } else if(data.deadHeat) {
        alert("It is draw! There is no loosers:)");
    }
}

$(".tic").click(function () {
    var slot = $(this).attr('id');
    playerTurn(turn, slot);
});

function reset() {
    turns = [["#", "#", "#"], ["#", "#", "#"], ["#", "#", "#"]];
    $(".tic").text("#");
}

$("#reset").click(function () {
    reset();
});

function setTurn(gameId) {
    $.ajax({
        url: url + "/game",
        type: 'POST',
        dataType: "json",
        async: false,
        contentType: "application/json",
        data: JSON.stringify({
            "id": gameId
        }),
        success: function (data) {
            pTurn = (data.lastStepType == "X") ? 'O' : 'X';
            console.log(pTurn)
            displayResponse(data);
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function copyTextToClipboard(copyText) {
    if (navigator && navigator.clipboard && navigator.clipboard.writeText)
        return navigator.clipboard.writeText(copyText);
    return Promise.reject('The Clipboard API is not available.');
}

function ratingTable() {
    $.ajax({
        url: url + "/rating",
        type: 'GET',
        dataType: "json",
        async: false,
        contentType: "application/json",
        success: function (data) {
            generateTable(data);
            var x = document.getElementById("table-wrapper");
            if (x.style.display === "none") {
                x.style.display = "block";
            } else {
                x.style.display = "none";
            }
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function generateTable(data) {
    var headers = new Array()
    headers.push(["#","Nickname", "Point quantity", "Victory quantity", "Loss quantity", "Game quantity"]);

    console.log(data.length);
    //Create a HTML Table element.
    var table = document.createElement("TABLE");
    table.className = "fl-table";

    //Get the count of columns.
    var columnCount = headers[0].length;

    //Add the header row.
    var row = table.insertRow(-1);
    for (var i = 0; i < columnCount; i++) {
        var headerCell = document.createElement("TH");
        headerCell.innerHTML = headers[0][i];
        row.appendChild(headerCell);
    }

    //Add the data rows.
    for (var i = 0; i < data.length; i++) {
        row = table.insertRow(-1);
        var cell = row.insertCell(-1);
        cell.innerHTML = i;
        var cell = row.insertCell(-1);
        cell.innerHTML = data[i].player.login;
        var cell = row.insertCell(-1);
        cell.innerHTML = data[i].pointQuantity;
        var cell = row.insertCell(-1);
        cell.innerHTML = data[i].victoryQuantity;
        var cell = row.insertCell(-1);
        cell.innerHTML = data[i].lossQuantity;
        var cell = row.insertCell(-1);
        cell.innerHTML = data[i].pointQuantity;
    }

    var dvTable = document.getElementById("table-wrapper");
    dvTable.innerHTML = "";
    dvTable.appendChild(table);
}