<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Add a Link</title>
    <link href="css/style.css" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="wrapper">
<br>
<h2>DTN Admin Panel</h2>
<jsp:include page="head.jsp" />
<h3>Add a Link</h3><br>
<s:if test="clientList.size()>1">
<form action="addLink.action">
<center>
<table>
<s:select cssStyle="width:11em" label="Link Edge 1" list="clientList" name="link.edges[0]"></s:select>
<s:select cssStyle="width:11em" label="Link Edge 2" list="clientList" name="link.edges[1]"></s:select>
<tr><td>Link Bandwidth:</td><td> <input type="text" name="link.bandwidth" value="100" /></td></tr>
<tr><td>Link Length:</td><td> <input type="text" name="link.length" value="10" /></td></tr>
</table>
</center>
<br>
<input class="button" type="submit" name="" value="Create">
</form>
</s:if>
<s:else>
There are not enough clients in the system!
</s:else>
</div>
</body>
</html>
