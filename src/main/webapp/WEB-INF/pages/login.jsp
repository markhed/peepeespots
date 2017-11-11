<%@ include file="header.jsp" %>
  
<h3>Write in your name</h3> 

<form action="/CounterWebApp/welcome" method="get">
    User name: <input type="text" name="user"/>
    <input type="submit" value="Go"/>
</form>