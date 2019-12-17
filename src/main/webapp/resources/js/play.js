var play = angular.module('play',[]);
var webSocket;
var socketId;
var mobile;

$(document).ready(function(){
	if (/Android|webOS|iPhone|iPad|BlackBerry|Windows Phone|Opera Mini|IEMobile|Mobile/i.test(navigator.userAgent))
		mobile = true;
	else mobile = false;
});

play.service('DataService', function($http) {
    delete $http.defaults.headers.common['X-Requested-With'];
    this.getData = function(addr, meth) {
        // $http() returns a $promise that we can add handlers with .then()
        return $http({
            method: meth,
            url: addr,
            contentType: "application/json",
            dataType: "json",
            params: 'limit=10, sort_by=created:desc',
            headers: {'Authorization': 'Token token=black'}
         });
     }
    
    this.getAjax = function(addr) {
    	return privateAjax(addr, "GET");
    }
    
    this.postAjax = function(addr) {
    	return privateAjax(addr, "POST");
    }
    
    var privateAjax = function(addr, meth) {
    	$.ajax({
    	    type: meth,
    	    contentType: "application/json",
    	    dataType: "json",
    	    url: addr,
    	    success: function(data) {
    	      for (var i = 0; i < data.length; i++) {
    	    	  console.log("success: '" + data[i].name + "'; ");
    	      }
    	      return data;
    	    },
    	    error: function(data){
    	      console.log("error:" + data);
    	      return null;
    	    }
    	  });
    }
});

play.service('SquareService', function(){
	   this.square = function(a) {
	      return a*a;
	   }
	});

play.controller("PlayCtrl", function($scope, $http, DataService) {

	$scope.games = null;
  	
	$scope.loadList = function ()  {	
		getServerList();		
	};
	
	var getServerList = function() {		
	    $scope.games = null;
	    DataService.getData('api/serverlist', 'GET').then(function(dataResponse) {
	        $scope.games = dataResponse.data;
	        console.log("dataResponses: " + dataResponse.data);
	        
	        for (var i = 0; i < dataResponse.data.length; i++) {
  	    	  console.log("dataResponse: '" + dataResponse.data[i].name + "'; ");  	    	
  	      	}
	        
	        console.log("gameslist: " + $scope.games);
	        
	    });  
	};
	    
	$scope.join = function (game)  {
			//if(webSocket != null)
	    	//  webSocket.close();
		    var port = game.port;
		    var ip = game.ip;
		    
		    //connect socket
		    connectWebSocket(ip, port, game);
		    //console.log(JSON.stringify(game.map));  
	}; 
	
	//$scope.sendTest = function() {
	//	console.log("Trying to send test Json...");
	//	webSocket.send(JSON.stringify(testJson));
     //   alert("Message is sent...");
	//}; 
	
	$scope.getPing = function (game) {
		
		ping(game.ip).then(function(delta) {
            return (String(delta));
        }).catch(function(error) {
            return (String(error));
        });
	};	

	getServerList();
	
});        

var connectWebSocket = function(addr, port, game)
{
    if ("WebSocket" in window)
    {
       alert("WebSocket is supported by your Browser!");
       
       // Let us open a web socket
      
      // if(webSocket != null)
	  // {
	//	   webSocket.close();
	//	   webSocket = null;
	  // }
       

	   console.log("Connecting to websocket on: " + addr + ":" +port);
	   
       window.webSocket = new WebSocket("ws://" + addr + ":" + port);
       //test socket:
       //window.webSocket = new WebSocket("ws://" + addr + ":8080" + "/websocket");
		
       window.webSocket.onopen = function()
       {
          // Web Socket is connected, send data using send()
    	  window.webSocket.send(JSON.stringify(testJson));
          console.log("onopen message is sent...");
       };
		
       window.webSocket.onmessage = function (evt) 
       { 
          var received_msg = evt.data;
          //console.log("NEW MESSAGE!");

          if(received_msg == "ping")
    	  {
        	  webSocket.send("pong");
    	  }
          else
          {          
	          var json = JSON.parse(evt.data);
              //console.log("Message received: " + JSON.stringify(json[0]));
	          
              
	          if(json[0].hasOwnProperty('gameMessage'))
	    	  {	        	  
	        	  //console.log("parsing gameState from websocket json data: " + JSON.stringify(json[0]));
                  parseGameState(json);
	    	  }//request to remove idle enemy from local game
	          else if(json[0].hasOwnProperty('killId'))
	    	  {
	        	  console.log("remove idle local enemy: " + json[0].killId);
                  //start gameclient
	        	  if(window.gameState.playerStates.has(json[0].killId)){
	                  window.gameState.playerStates.delete(json[0].killId);
	              }
	    	  }
	        //first message after serversocket connection:
	          else if(json[0].hasOwnProperty('socketId'))
	    	  {
	        	  window.sockedId = json[0].socketId;
	        	  console.log("socketId is set to: " + window.sockedId);
                  //start gameclient
                  setGameData(game, json[0].socketId);
	    	  }
	          //alert("Message is received... :" + JSON.stringify(json));
          }
       };
		
       window.webSocket.onclose = function()
       { 
          // websocket is closed.
    	   $('#gameContainer').empty();
           $('#gameContainer').hide();
           $('#listContainer').show();
           $('#testButton').hide();
          alert("Connection is closed..."); 
       };
    }
    
    else
    {
       // The browser doesn't support WebSocket
       alert("WebSocket NOT supported by your Browser!");
    }
 }

var gameData;

var setGameData = function(game, id)
{   
    window.gameData = {
            tilesX : game.map.length,
            tilesY : game.map[0].length,
            tileSize : 128,
            map : game.map,
             
            worldW : function() {
                return this.tileSize * this.tilesX;
            },

            worldH : function() {
                return this.tileSize * this.tilesY;
            },

            socketId : id,

            //islandArray = [],

            //TODO: check what type of island tile needs to be pushed
            islandArray : []
            //TODO: palmtrees!
        }
        $('#gameContainer').show();
        $('#gameContainer').html("<h4>" + game.name + " @ " + game.ip + " : " + game.port + "</h4>" + "<div id='pirates'><script src='/resources/js/pirates.js'></script></div>" + 
                    "<br/>" + "<button id='testButton' style='width: auto' class='btn btn-lg btn-primary btn-block btn-back' onclick='sendTest()'>Send Test</button>");           
            
        $('#testButton').hide();
        $('#listContainer').hide();
} 

var sendTest = function() {
	console.log("Trying to send test Json...");
	window.webSocket.send(JSON.stringify(testList));
    alert("Message is sent...");
}

var parseGameState = function(json){
	var ps;

    for(var i = 0; i < json.length; i++)
    {
        if(json[i].hasOwnProperty('playerState'))
        {
            if(!window.gameState.playerStates.has(json[i].id)){
                window.gameState.playerStates.set(json[i].id, new PlayerState(json[i].posX, json[i].posY, json[i].health, json[i].rotation, json[i].velocity, "RED", "player" + json[i].id, json[i].id));
            }
            else
            {
                ps = window.gameState.playerStates.get(json[i].id);
                ps.posX = json[i].posX;
                ps.posY = json[i].posY;
                ps.health = json[i].health;
                ps.rotation = json[i].rotation;
                ps.velocity.x = json[i].velocity.x;
                ps.velocity.y = json[i].velocity.y;
            }
        }
        if(json[i].hasOwnProperty('cannonBallState'))
        {
            //console.log("Pushing new cannonball into cannonBallStates");
            window.gameState.cannonBallStates.push({ posX : json[i].posX, posY : json[i].posY, velX : json[i].velocity.x, velY : json[i].velocity.y, id : json[i].id });
        }
    }
}

function PlayerState(posX, posY, health, rotation, velocity, color, name, id) {
    this.playerState = true;
    this.gameMessage = true;
    this.posX = posX;
    this.posY = posY;
    this.health = health;
    this.rotation = rotation;
    this.velocity = velocity;
    this.name = name;
    this.id = id;
}

function CannonBallState(posX, posY, velocity, id) {
    this.cannonBallState = true;
    this.gameMessage = true;
    this.posX = posX;
    this.posY = posY;
    this.velocity = velocity;
    this.id = id;
}

var mapToHtmlTable = function(map) {
		
	result = "<table>";
	
	for(var j = 0; j < map.length; j++)
	{
		result += "<tr>";
		for(var i = 0; i < map[j].length; i++)
		{
			result += "<td>";
			result += map[j][i];
			result += "<td>";
		}
		result += "</tr>"
	}
	
	result += "</table>";
	console.log(result);
	return result;
}

var testJson = {
		id : 1,
		name : "game1",
		mode : "MODE_TDM",
		ip : "10.0.0.127",
		port : 8080,
		players : 0
	};

var testList = [
	{
		"id" : 1,
		"name" : "game1",
		"mode" : "MODE_TDM",
		"ip" : "",
		"port" : 8080,
		"players" : 0
	},
	{
		"id" : 2,
		"name" : "game2",
		"mode" : "MODE_KOTH",
		"ip" : "https://google.com/",
		"port" : 9002,
		"players" : 0
	},
	{
		"id" : 3,
		"name" : "game3",
		"mode" : "MODE_CTF",
		"ip" : "216.58.194.131",
		"port" : 9003,
		"players" : 0
	}
];
