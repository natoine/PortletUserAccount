<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<link href="<%=request.getContextPath()%>/css/login.css" rel=stylesheet />
<h4>Nouvelle inscription</h4>
<%
if(request.getAttribute("config_error_message")!=null)
{
	%>
	<div class=error_message>
	L'admin doit d'abord configurer le portlet de login en mode EDIT !! Tentez de recharger la page !!
	</div>
	<%
}
else
{
	if(request.getAttribute("error_message")!=null)
			{
				%>
				<div class=error_message>
				<%=(String) request.getAttribute("error_message") %>
				</div>
				<%
			}
	%>
	<div class = inscription>
	<form method="post" action="<portlet:actionURL/>">
	<table>
	<caption>Veuillez remplir les champs suivants : </caption>
	<tr><td>Les champs marqués d'une étoile sont obligatoires.</td></tr>
	<tr>
		<td>Nom</td>
		<%
		if(request.getAttribute("lastname")!=null)
		{
		%>
		<td><input type=text name="lastname" value="<%=(String) request.getAttribute("lastname")%>"></td>
		<%
		}
		else
		{
		%>
		<td><input type=text name="lastname" value=""></td>
		<%
		}
		%>
		<td>*</td>
	</tr>
	<tr>
		<td>Prénom</td>
		<%
		if(request.getAttribute("firstname")!=null)
		{
		%>
		<td><input type=text name="firstname" value="<%=(String) request.getAttribute("firstname")%>"></td>
		<%
		}
		else
		{
		%>
		<td><input type=text name="firstname" value=""></td>
		<%
		}
		%>
		<td>*</td>
	</tr>
	<tr>
		<td>mail</td>
		<%
		if(request.getAttribute("mail")!=null)
		{
		%>
		<td><input type=text name="mail" value="<%=(String) request.getAttribute("mail")%>"></td>
		<%
		}
		else
		{
		%>
		<td><input type=text name="mail" value=""></td>
		<%
		}
		%>	
		<td>*</td>
	</tr>
	<tr>
		<td>pseudo</td>
		<%
		if(request.getAttribute("pseudo")!=null)
		{
		%>
		<td><input type=text name="pseudo" value="<%=(String) request.getAttribute("pseudo")%>"></td>
		<%
		}
		else
		{
		%>
		<td><input type=text name="pseudo" value=""></td>
		<%
		}
		%>	
		<td>*</td>
	</tr>
	<tr>
		<td>password</td>
		<td><input type=password name="password_1" value=""></td>
		<td>*</td>
	</tr>
	<tr>
		<td>password</td>
		<td><input type=password name="password_2" value=""></td>
		<td>*</td>
	</tr>
	<tr>
		<td>Description personnelle</td>
		<%
		if(request.getAttribute("description")!=null)
		{
		%>
		<td><textarea cols=5 rows=3 name="description"><%=(String) request.getAttribute("description")%></textarea></td>
		<%
		}
		else
		{
		%>
		<td><textarea cols=5 rows=3 name="description"></textarea></td>
		<%
		}
		%>	
	</tr>
	<tr>
		<td>URL personnelle</td>
		<%
		if(request.getAttribute("url")!=null)
		{
		%>
		<td><input type=text name="url" value="<%=(String) request.getAttribute("url")%>"></td>
		<%
		}
		else
		{
		%>
		<td><input type=text name="url" value=""></td>
		<%
		}
		%>		
	</tr>
	<tr>
		<td><input type="submit" name="op" value="Create Account" /></td>
	</tr>
	</table>
	</form>
	</div>
	<%
}
 %>