<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
  <div>
    <input id="inputmessage" type="text" />
  </div>
  <div>
    <input type="submit" value="Broadcast message" onclick="send()" />
  </div>
  <div id="messages"></div>
  <script type="text/javascript">
    var webSocket = 
      new WebSocket('ws://localhost:8080/websocket');

    webSocket.onerror = function(event) {
      onError(event)
    };

    webSocket.onopen = function(event) {
      onOpen(event)
    };

    webSocket.onmessage = function(event) {
      onMessage(event)
    };

    function onMessage(event) {
      document.getElementById('messages').innerHTML 
        += '<br />Received message: ' + event.data;
    }

    function onOpen(event) {
      document.getElementById('messages').innerHTML 
        = 'Connection established';
    }

    function onError(event) {
      alert(event.data);
    }

    function send() {
      var txt = document.getElementById('inputmessage').value;
      webSocket.send(txt);
      return false;
    }
  </script>
</body>
</html>
