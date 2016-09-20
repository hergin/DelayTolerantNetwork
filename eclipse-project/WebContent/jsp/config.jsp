<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Configuration</title>
    <link href="css/style.css" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="wrapper">
<br>
<h2>DTN Admin Panel</h2>
<jsp:include page="head.jsp" />
<h3>Configuration</h3><br>
<form action="modifyProtocol.action">
<center>
<table>
<s:select label="Algorithm" cssStyle="width:15em" list="#{'1':'First Contact (with History)','2':'First Contact (without History)','3':'Epidemic'}" name="currentProtocol" value="currentProtocol"></s:select>
<tr><td>TTL</td><td><input type="text" name="currentTTL" value="<s:property value='currentTTL'/>"></td></tr>
</table>
</center>
<br>
<input class="button" type="submit" name="" value="Submit Changes">
</form>
<br><br>
<h3>Tasks</h3><br>
<center>
<a href="generateRandomNetwork.action">Generate Random Network</a>
</center>
</div>
</body>
</html>
