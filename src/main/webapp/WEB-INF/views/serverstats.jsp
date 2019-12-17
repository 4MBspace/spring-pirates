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

         <h2 style="margin:50px">GameServer Administration</h2>
         
               
	       <table>
	         <tr><th style="width: 100px">Server port</th><th style="width: 200px">Last known message</th><th style="width:auto">Players</th><th style="width:auto"> </th><th style="width:auto"> </th>
	         </tr>
	         
			    <c:forEach items="${gameServers}" var="gs">  
			    	<tr>
			          <td><c:out value="${gs.serverState.port}"/></td>
			   		  <td><c:out value="${gs.serverState.stateMessage}"/></td>
			   		  <td><c:out value="${gs.serverState.players}"/></td>
			   		  <td>
				   		<form action="${contextPath}/admin/kick" method="post">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
							<input type="hidden" name="serverPort" value="${gs.serverState.port}"/>
							<button style="width:auto" class="btn btn-lg btn-primary btn-block" type="submit">Kick All</button>
						</form>		   		  
			   		  </td>
			   		  <td>
				   		<form action="${contextPath}/admin/removeServer" method="post">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
							<input type="hidden" name="serverPort" value="${gs.serverState.port}"/>
							<button style="width:auto" class="btn btn-lg btn-primary btn-block" type="submit">Close</button>
						</form>		   		  
			   		  </td>
			   		</tr>
			   	</c:forEach>
		   	</table>
          
		    
		<table>
			<tr>			
				<td>
					<form action="${contextPath}/admin/serverstats" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<button  class="btn btn-lg btn-primary btn-block" type="submit">Update info!</button>
					</form>
				</td>
				<c:if test="${empty gameServers}">		
					<td>
						<form action="${contextPath}/admin/createserver" method="post">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
							<button class="btn btn-lg btn-primary btn-block" type="submit">Create Server!</button>
						</form>
					</td>
				</c:if>
			</tr>
		</table> 
</div>
<!-- /container -->

</body>
</html>
