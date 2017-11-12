<%@ include file="header.jsp" %>
 
<label>Checking...</label>
<h3>${user}</h3>
<h3>${counter}</h3>
<script>setTimeout(function(){window.location.href='http://localhost:8080/PeePeeSpotsApp/check_winner'},5000);</script>