<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Message Details</title>
    <link href="css/style.css" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="wrapper">
<br>
<h2>DTN Admin Panel</h2>
<jsp:include page="head.jsp" />
<h3>
<s:if test="selectedMessage.history.contains(selectedMessage.dest)">Message REACHED to destination!</s:if>
<s:elseif test="selectedMessage.dropped==1">Message was DROPPED because of TTL!</s:elseif>
<s:elseif test="selectedMessage.dropped==1">Message was DROPPED because DESTINATION detached!</s:elseif>
<s:else>Message is on the WAY!</s:else>
</h3>
<br>

<img src="images/<s:property value="messageStatusPath"/>">
<br><br>

<center>
<table id="table-2">
	<thead><tr>
		<th>Source</th>
		<th>Destination</th>
		<th>Current</th>
		<th>Content</th>
	</tr></thead>
	<tbody>
	<tr>
		<td><s:property value="selectedMessage.source"/></td>
		<td><s:property value="selectedMessage.dest"/></td>
		<td><s:property value="selectedMessage.current"/></td>
		<td><s:property value="selectedMessage.msg"/></td>
	</tr>
</tbody>
</table>
<br><br>
<img src="jsp/legend.png">
</center>

</div>
</body>
</html>
