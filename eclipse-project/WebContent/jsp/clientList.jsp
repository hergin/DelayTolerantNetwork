<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>View All Clients</title>
    <link href="css/style.css" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="wrapper">
<br>
<h2>DTN Admin Panel</h2>
<jsp:include page="head.jsp" />
<h3>All Clients in DTN</h3>
<br>

<br>
<s:if test="clientList.size()!=0">
<center>
<table id="table-2">
	<thead><tr>
		<th>Client</th>
		<th>Operation</th>
	</tr></thead>
	<tbody>
<s:iterator value="clientList">
	<tr>
		<td><s:property value="id"/></td>
		<td>
			<s:a action="deleteClient.action">Delete
				<s:param name="nodeId" value="id"></s:param>
			</s:a>
		</td>
	</tr>
</s:iterator>
</tbody>
</table>
</center>
</s:if>
<s:else>
There are no clients in the system!
</s:else>
</div>
</body>
</html>
