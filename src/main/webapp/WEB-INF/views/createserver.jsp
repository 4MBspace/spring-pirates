<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Welcome</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
    
    <script src="${contextPath}/resources/js/jquery.min.js"></script>
     <script src="${contextPath}/resources/js/jcanvas.min.js"></script>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>

<div class="container">

	<form action="${contextPath}/" method="get">
		<button style="width: auto" class="btn btn-lg btn-primary btn-block" type="submit">Back</button>
	</form>

   <h2 style="margin:50px">Create New GameServer</h2>     		    
					
	<form id="mapForm" action="${contextPath}/admin/startserver" method="post">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		<input id="mapGrid" type="hidden" name="mapGrid" value='' />
		
			<table>						
				<tr> 
					<td><b>Name:</b> </td>				
					<td>											
						<input type="text" id="mapName" name="mapName" value="mapname"/>
					</td>
				</tr>				
					<td><b>Tree spawnrate:</b> </td>				
					<td>											
						<input type="number" id="treeSpawn" name="treeSpawn" value="1"/>
					</td>
				</tr>				
				<tr> 
					<td><b>Booty spawnrate:</b> </td>				
					<td>											
						<input type="number" id="bootySpawn" name="bootySpawn" value="1"/>
					</td>
				</tr>
				<tr> 
					<td><b>Crate spawnrate:</b> </td>				
					<td>											
						<input type="number" id="crateSpawn" name="crateSpawn" value="1"/>
					</td>
				</tr>	
				<tr> 
					<td><b>Bottle spawnrate:</b> </td>				
					<td>											
						<input type="number" id="bottleSpawn" name="bottleSpawn" value="1"/>
					</td>
				</tr>
				<tr> 
					<td><b>Map Size:</b> </td>				
					<td>											
						<select id="mapSize" name="mapSize">
						  <option value="16">mini</option>
						  <option value="32">small</option>
						  <option value="64">medium</option>
						  <option value="128">large</option>
						 <!-- <option value="256">huge</option> deprecated: too large! Needs better map/islands datastructure-->
						</select>
					</td>
				</tr>
				<!--  <tr> 
					<td><b>Islands:</b> </td>				
					<td>											
						<input type="number" id="islands" name="islands" value="1"/>
					</td>
				</tr>				
				<tr> -->
				<tr> 
					<td><b>Island size:</b> </td>				
					<td>											
						<select id="islandSize" name="islandSize">
						  <option value="2">mini</option>
						  <option value="4">small</option>
						  <option value="8">medium</option>
						  <option value="16">large</option>
						  <option value="32">huge</option>
						</select>
					</td>
				</tr> 				
				<tr> 	
			</table>
			<table>			
				<tr>
					<td>
						<button id="resetButton" class="btn btn-lg btn-primary btn-block" type="button">Reset Map</button>
					</td>
					<td>
						<button id="submitButton" class="btn btn-lg btn-primary btn-block" type="submit">Create Server!</button>
					</td>
				</tr>
			</table> 
	</form>
	
	<div id="canvasContainer">
		<canvas id="mapCanvas" width="256" height="256">
		
		</canvas>
		
		<div id="canvasText"></div>
	</div>
	
	<div id = "mapTableContainer">
		<table id = "mapTable">
		</table>
	</div>
			
		
</div>
<!-- /container -->
<script src="${contextPath}/resources/js/generate.js"></script>

</body>
</html>
