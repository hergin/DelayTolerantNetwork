<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>View All Links</title>
    <link href="css/style.css" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="wrapper">
<br>
<h2>DTN Admin Panel</h2>
<jsp:include page="head.jsp" />
<h3>All Links as a Graph</h3>
<br>

<img src="images/<s:property value="newImageName"/>">
<br><br>
<s:if test="linkFlag">
<center>
<table id="table-2">
	<thead><tr>
		<th>Edge 1</th>
		<th>Edge 2</th>
		<th>Bandwidth</th>
		<th>Length</th>
		<th>Operation</th>
	</tr></thead>
	<tbody>
<s:iterator value="linkList">
	<tr>
		<td><s:property value="edges[0]"/></td>
		<td><s:property value="edges[1]"/></td>
		<td><s:property value="bandwidth"/></td>
		<td><s:property value="length"/></td>
		<td>
			<s:if test="active">
				<s:a action="deactivateLink.action">Deactivate
					<s:param name="link.edges[0]" value="edges[0]"></s:param>
					<s:param name="link.edges[1]" value="edges[1]"></s:param>
				</s:a>
			</s:if>
			<s:else>
				<s:a action="activateLink.action">Activate
					<s:param name="link.edges[0]" value="edges[0]"></s:param>
					<s:param name="link.edges[1]" value="edges[1]"></s:param>
				</s:a>
			</s:else>
			|
			<s:a action="deleteLink.action">Delete
				<s:param name="link.edges[0]" value="edges[0]"></s:param>
				<s:param name="link.edges[1]" value="edges[1]"></s:param>
			</s:a>
		</td>
	</tr>
</s:iterator>
</tbody>
</table>
</center>
</s:if>
<s:else>
There are no links in the system!
</s:else>
</div>
</body>
</html>
