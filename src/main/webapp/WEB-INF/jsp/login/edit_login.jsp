<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<link href="<%=request.getContextPath()%>/css/login.css" rel=stylesheet />
<%
	String _url_inscription ="URL de la page d'inscription";
	if(request.getAttribute("url_inscription") != null ) _url_inscription = (String) request.getAttribute("url_inscription") ;
	String application_name = null;
	if(request.getAttribute("application_name") != null ) application_name = (String) request.getAttribute("application_name") ;
	String application_description = null;
	if(request.getAttribute("application_description") != null ) application_description = (String) request.getAttribute("application_description") ;
	String application_url = null;
	if(request.getAttribute("application_url") != null ) application_url = (String) request.getAttribute("application_url") ;
	
	if(request.getAttribute("error_message")!=null)
	{
		%>
		<div class=error_message>
		<%=(String) request.getAttribute("error_message") %>
		</div>
		<%
	}
	%>
<h4>Pour éditer les propriétés du portlet</h4>
<div class=admin>
<form method="post" action="<portlet:actionURL/>">
	<table>
	<tr>
		<td>Saisir l'url de la page ou est déployé le portlet d'inscription : </td><td><input type=text name="url" value="<%=_url_inscription %>"></td>
	</tr>
	<tr>
		<td>Saisir le nom de votre application : </td>
		<%
		if(application_name == null)
		{
			%>
			<td><input type=text name="application_name"></td>
			<%
		}
		else
		{
			%>
			<td><input type=text name="application_name" value="<%=application_name%>"></td>
			<%
		}
		%>
	</tr>
	<tr>
		<td>Saisir un descriptif de l'application</td>
		<%
		if(application_description == null)
		{
			%>
			<td><textarea width=100% name="application_description"></textarea></td>
			<%
		}
		else
		{
			%>
			<td><textarea width=100% name="application_description"><%=(String) request.getAttribute("application_description")%></textarea></td>
			<%
		}
		%>
	</tr>
	<tr>
		<td>Saisir une URL de représentation de l'application (URL du portail ou autre)</td>
		<%
		if(application_url == null)
		{
			%>
			<td><input type=text name="application_url"></td>
			<%
		}
		else
		{
			%>
			<td><input type=text name="application_url" value="<%=application_url%>"></td>
			<%
		}
		%>
	</tr>
	<tr>
		<td><input type="submit" name="op" value="Update" /></td>
		<td><input type="submit" name="op" value="Cancel" /></td>
	</tr>
	</table>
</form>	
</div>