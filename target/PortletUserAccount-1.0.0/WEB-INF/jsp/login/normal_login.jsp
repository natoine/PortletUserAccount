<%@ page language="java" 
	import="fr.natoine.model_user.UserAccount"
	import="javax.portlet.*"
%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<link href="<%=request.getContextPath()%>/css/login.css" rel=stylesheet />
<%
	//Cas config non set
	if(request.getAttribute("url_inscription")==null || request.getAttribute("application_name")==null || request.getAttribute("error_init_application")!=null)
	{
	%>
		<div class=admin>
			L'administrateur doit d'abord configurer l'url d'inscription du portlet et le nom de l'application en passant par le mode edit de ce portlet. 
			Si vous êtes administrateur, accédez au mode d'édition. Si les valeurs vous conviennent cliquez simplement sur update.
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
		//cas demande de renvoi d'infos
		if(request.getAttribute("lost_password")!=null)
		{
			%>
			<div class = password_lost>
			<form method="post" action="<portlet:actionURL/>">
			<table>
				<tr>
					<td><img alt=mail src="<%=request.getContextPath()%>/images/mail_alt_24x24.png"></td>
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
				</tr>
				<tr>
				<td><input type="submit" name="op" value="Envoi email" /></td>
				<td><input type="submit" name="op" value="Retour" /></td>
				</tr>
				</table>
				</form>
			</div>
			<%
		}
		else
		{
			//cas inscription
			String _url_inscription = (String)request.getAttribute("url_inscription") ;
			//if(request.getAttribute("log_message")!=null)
			if(request.getAttribute("UserLogin")!=null)
			{
				%>
				<div class=log_message>
				Vous êtes connecté <%=((UserAccount)request.getAttribute("UserLogin")).getPseudonyme()%>
				</div>
				<div class=unlogin>
				<a href="<portlet:actionURL portletMode='view'>
	 								<portlet:param name='op' value='deco'/>
									</portlet:actionURL>" title="déconnection">
								Se déconnecter.
								</a>
				</div>
				<%
			}
			else
			{
			%>
	<div class=login>
	<form method="post" action="<portlet:actionURL/>">
		<table>
			<tr>
				<td><img alt=pseudo src="<%=request.getContextPath()%>/images/user.png"></td>
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
				<td><input type=text name="pseudo"></td>
				<%
				}
				%>
			</tr>
			<tr>
				<td><img alt=password src="<%=request.getContextPath()%>/images/password_24x24.png" /></td><td><input type=text name="password"></td>
			</tr>
			<tr>
				<td>Se connecter :</td><td><input type="submit" name="op" value="Login" /></td>
			</tr>
			<tr><td><img alt=inscription src="<%=request.getContextPath()%>/images/user_add_24x24.png" /></td><td><a href="<%=_url_inscription%>">S'inscrire</a></td></tr>
			<tr><td><img alt=envoi_mail src="<%=request.getContextPath()%>/images/password_lost_24x24.png" /></td><td><a href="<portlet:actionURL portletMode='view'>
	 								<portlet:param name='op' value='lost_password'/>
									</portlet:actionURL>" title="renvoi de données par email">
								Mot de passe perdu ?
								</a></td></tr>
		</table>
	</form>
	</div>
	<%
		}
	}
}
%>