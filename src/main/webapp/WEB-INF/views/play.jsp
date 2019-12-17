<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en" ng-app="play">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<meta name="description" content="">
<meta name="author" content="">

<title>List of available Games</title>

<link href="${contextPath}/resources/css/bootstrap.min.css"
	rel="stylesheet">
<link href="${contextPath}/resources/css/common.css" rel="stylesheet">

<script src="${contextPath}/resources/js/jquery.min.js"></script>

<script src='${contextPath}/resources/js/phaser.min.js'></script>

<script src='${contextPath}/resources/js/healthbar.js'></script>



<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>

	<div class = "container" data-ng-app="dataPlay">

		<form id="quitForm" method="GET" action="${contextPath}/">			
			<button style="width: auto" class="btn btn-lg btn-primary btn-block btn-back"
				type="submit">Quit</button>
		</form>
		
		<div ng-controller="PlayCtrl">
			
			<div id="listContainer" class="listContainer">
			
				<form class="form-horizontal" ng-submit="updateGame">

					<table class="table">
						<tr>
							<th>NAME</th>
							<th>MODE</th>
							<th>PLAYERS</th>
							<th>PORT</th>
							<th></th>
							<!-- <th>PING</th> -->
							<!-- <th>SELECT</th> -->
							<th>
								<button type="submit" class="btn btn-primary" ng-click="loadList()" ng-model-instant>Refresh</button>
							</th>
						</tr>
						<tr ng-repeat="game in games">
							<td>{{game.name}}</td>
							<td>{{game.mode}}</td>
							<td>{{game.players}}</td>
							<td>{{game.port}}</td>
							<!-- <td>{{getPing(game)}}</td> -->
							<td></td>
							<td><button class="btn btn-primary" ng-click="join(game)">join!</button></td>
						</tr>
					</table>

				</form>

			</div>
			
			<script src="${contextPath}/resources/js/angular.min.js"></script>
			<script src='${contextPath}/resources/js/play.js'></script>
			
			<div class="gameContainer" id="gameContainer">
			</div>
			
		</div>

	</div>	

</body>
</html>
