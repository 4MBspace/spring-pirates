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

    <c:if test="${pageContext.request.userPrincipal.name != null}">
        
         <h2 style="margin:50px">Welcome <span style="color:#2acf8a">${pageContext.request.userPrincipal.name}</span></h2>
         
         <table border="0">
         <tr>
         <c:if test="${pageContext.request.isUserInRole('ROLE_ADMIN')}"> 
	      
		      <td>     
		        <form id="adminForm" method="POST" action="${contextPath}/admin/serverstats">        	
		            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		            <button class="btn btn-lg btn-primary btn-block" type="submit">Admin</button>
		        </form>
		      </td>
	      
	      </c:if>
	      
		      <td>
		      <form id="userprofileForm" action="javascript:getNut()" hidden>        	
	            <!-- <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/> -->
	            <button class="btn btn-lg btn-primary btn-block" type="submit">Profile</button>
	          </form>
		      </td>
		      
		      <td>     
		        <form id="rankingForm" method="POST" action="${contextPath}/ranking/global">        	
		            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		            <button class="btn btn-lg btn-primary btn-block" type="submit">Ranking</button>
		        </form>
		      </td>	           
	      
		      <td>
		      <form id="logoutForm" method="POST" action="${contextPath}/logout">        	
	            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	            <button class="btn btn-lg btn-primary btn-block" type="submit">Log Out</button>
	        	</form>
		      </td>
	      </tr>
	     </table>  
	     
	     <table>
	     	<tr>
	     		<th>Rank</th><th>K/D</th><th>Kills</th><th>Deaths</th>
	     	</tr>
	     	<tr>
	     		<td>${rank}</td><td>${kd}</td><td>${kills}</td><td>${deaths}</td>
	     	</tr>  	
	     </table>
	     
	     <form id="playForm" method="POST" action="${contextPath}/play" class="form-signin">        	
	            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	            <button class="btn btn-lg btn-primary btn-block" type="submit">Play!</button>
	     </form>  

		<div id="nutty" style="margin-left:auto; margin-right:auto" hidden>
		<a onclick="document.forms['gamelistForm'].submit()">
			<img style="margin-top:50px;" src="${contextPath}/resources/media/nut.png"/>
		</a>
		<h2>Have a nut!</h2> 
		</div>

    </c:if>

	<script type="text/javascript">
		function getNut(){
			$('#nutty').show();
		};
	</script>
	
</div>
<!-- /container -->

</body>
</html>
