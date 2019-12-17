var play = angular.module('play',[]);
var webSocket;
var socketId;

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

	
	
/*  $scope.load = function ()  {
    $http.get('todo').
      success(function(data, status, headers, config) {
        $scope.todos = data;
      }).
      error(function(data, status, headers, config) {
        // log error
      });
  };

  $scope.save = function ()  {
    $http.post('todo', angular.toJson($scope.todo)).success(function () { 
    	$scope.load();
    });
  };

  $scope.delete = function (id)  {
    $http.delete("todo/" + id).success(function () { 
    	$scope.load();
    });
  };
*/
	$scope.games = null;
  	
	$scope.loadList = function ()  {	
		
		//$('body').append(SquareService.square(16));
		
	    $scope.games = null;
	    DataService.getData('api/serverlist', 'GET').then(function(dataResponse) {
	        $scope.games = dataResponse.data;
	        console.log("dataResponses: " + dataResponse.data);
	        
	        for (var i = 0; i < dataResponse.data.length; i++) {
  	    	  console.log("dataResponse: '" + dataResponse.data[i].name + "'; ");  	    	
  	      	}
	        
	        console.log("gameslist: " + $scope.games);
	        
	    });   
	    
	   // $scope.games = DataService.getAjax("api/serverlist"); 
	    //console.log("gameslist: " + $scope.games);
	};
	    
	$scope.join = function (game)  {
			//if(webSocket != null)
	    	//  webSocket.close();
		    var port = game.port;
		    var ip = game.ip;
		    
		    connectWebSocket(ip, port);
		    
	        $('#pirateGame').html("<h4>" + game.name + " @ " + game.ip + " : " + game.port + "</h4>" + "<script id='pirates' src='${contextPath}/resources/js/pirates.js'></script>" + 
	        		"<br/>" + "<button style='width: auto' class='btn btn-lg btn-primary btn-block btn-back' onclick='sendTest()'>Send Test</button>");	          
		    
	       // gameData. 
		        //start gameclient and connect socket
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

});

var connectWebSocket = function(addr, port)
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
       
       webSocket = new WebSocket("ws://" + addr + ":" + port);
		
       webSocket.onopen = function()
       {
          // Web Socket is connected, send data using send()
    	   webSocket.send(JSON.stringify(testJson));
          alert("Message is sent...");
       };
		
       webSocket.onmessage = function (evt) 
       { 
          var received_msg = evt.data;
          
          if(received_msg == "ping")
    	  {
        	  webSocket.send("pong");
    	  }
          else
          {          
	          var json = JSON.parse(evt.data);
	          
	          if(json[0].hasOwnProperty('socketId'))
	    	  {
	        	  sockedId = json[0].socketId;
	        	  console.log("socketId is set to: " + sockedId);
	    	  }
	          alert("Message is received... :" + JSON.stringify(json));
          }
       };
		
       webSocket.onclose = function()
       { 
          // websocket is closed.
    	   
          alert("Connection is closed..."); 
       };
    }
    
    else
    {
       // The browser doesn't support WebSocket
       alert("WebSocket NOT supported by your Browser!");
    }
 }

var sendTest = function() {
	console.log("Trying to send test Json...");
	webSocket.send(JSON.stringify(testList));
    alert("Message is sent...");
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
