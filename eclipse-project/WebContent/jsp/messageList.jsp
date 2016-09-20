<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>View All Messages</title>
    <link href="css/style.css" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="wrapper">
<br>
<h2>DTN Admin Panel</h2>
<jsp:include page="head.jsp" />
<h3>All Message History</h3>
<br>

<br>
<s:if test="messageList.size()!=0">
<center>
<table id="table-2">
	<thead><tr>
		<th>Message ID</th>
		<th>Source</th>
		<th>Destination</th>
		<th>Status</th>
	</tr></thead>
	<tbody>
<s:iterator value="messageList">
	<tr>
		<td><a href="getMessageDetails.action?requestedMessageID=<s:property value='uniqueID'/>"><s:property value="uniqueID"/></a></td>
		<td><s:property value="source"/></td>
		<td><s:property value="dest"/></td>
		<td>
		<s:if test="history.contains(dest)"><img src="jsp/reached.png"> REACHED</s:if>
		<s:elseif test="dropped==1"><img src="jsp/x.png"> DIED of TTL</s:elseif>
		<s:elseif test="dropped==2"><img src="jsp/x.png"> DIED (Destination detached)</s:elseif>
		<s:else><img src="jsp/ontheway.gif"> ON THE WAY!</s:else>
		</td>
	</tr>
</s:iterator>
</tbody>
</table>
</center>
</s:if>
<s:else>
There are no messages in the system!
</s:else>
</div>
</body>
</html>
