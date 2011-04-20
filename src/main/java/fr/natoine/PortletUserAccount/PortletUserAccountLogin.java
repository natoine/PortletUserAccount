/*
 * Copyright 2010 Antoine Seilles (Natoine)
 *   This file is part of PortletUserAccount.

    PortletUserAccount is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    PortletUserAccount is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with PortletUserAccount.  If not, see <http://www.gnu.org/licenses/>.

 */
package fr.natoine.PortletUserAccount;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletSession;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;
import javax.portlet.WindowState;

import fr.natoine.dao.resource.DAOResource;
import fr.natoine.dao.user.DAOUser;
import fr.natoine.model_resource.Resource;
import fr.natoine.model_resource.URI;
import fr.natoine.model_user.Application;
import fr.natoine.model_user.Person;
import fr.natoine.model_user.UserAccount;
import fr.natoine.properties.PropertiesUtils;
import fr.natoine.stringOp.StringOp;


public class PortletUserAccountLogin extends GenericPortlet implements EventPortlet
{
	private Properties applicationProps ;
	private final String saved_properties = "/properties/appProperties";
	private final String default_properties = "/properties/defaultProperties";
	
	private boolean init_application = false ;
	
	/**
	 * url de la page du portail ou est déployé le portlet d'inscription PortletUserAccountInscription.
	 */
	private static String URL_INSCRIPTION = null ;
	private static String APPLICATION_URL = null ;
	private static String APPLICATION_NAME = null ;
	private static String APPLICATION_DESCRIPTION = null ;
	private static Resource REPRESENTS_APPLICATION = null ;
	
	//private static RetrieveUser CONTROLEUR_USER = null ;
	//private static CreateApplication CREATOR_APPLICATION = null ;
	//private static RetrieveApplication RETRIEVE_APPLICATION = null ;
	private static DAOUser DAOUSER = null ;
	private static EntityManagerFactory emf_user = null ; 
	
	private static final String NORMAL_VIEW = "/WEB-INF/jsp/login/normal_login.jsp";
	private static final String MAXIMIZED_VIEW = "/WEB-INF/jsp/login/normal_login.jsp";
	private static final String HELP_VIEW = "/WEB-INF/jsp/login/help_login.jsp";
	private static final String EDIT_VIEW = "/WEB-INF/jsp/login/edit_login.jsp";

	private PortletRequestDispatcher normalView;
	private PortletRequestDispatcher maximizedView;
	private PortletRequestDispatcher helpView;
	private PortletRequestDispatcher editView;

	public void doView( RenderRequest request, RenderResponse response )
	throws PortletException, IOException 
	{
		setRenderAttributes(request);
		if( WindowState.MINIMIZED.equals( request.getWindowState() ) ) {
			return;
		}
		if ( WindowState.NORMAL.equals( request.getWindowState() ) ) {
			normalView.include( request, response );
		} else {
			maximizedView.include( request, response );
		}
	}

	protected void doHelp( RenderRequest request, RenderResponse response )
	throws PortletException, IOException 
	{
		helpView.include( request, response );
	}

	protected void doEdit( RenderRequest request, RenderResponse response )
	throws PortletException, IOException 
	{
		setRenderAttributes(request);
		editView.include( request, response );
	}

	public void init( PortletConfig config ) throws PortletException 
	{
		super.init( config );
		normalView = config.getPortletContext().getRequestDispatcher( NORMAL_VIEW );
		maximizedView = config.getPortletContext().getRequestDispatcher( MAXIMIZED_VIEW );
		helpView = config.getPortletContext().getRequestDispatcher( HELP_VIEW );
		editView = config.getPortletContext().getRequestDispatcher( EDIT_VIEW );
		//CONTROLEUR_USER = new RetrieveUser();
		//CREATOR_APPLICATION = new CreateApplication();
		//RETRIEVE_APPLICATION = new RetrieveApplication();
		emf_user = Persistence.createEntityManagerFactory("user");
		DAOUSER = new DAOUser(emf_user);
		
		// create application properties with default
		Properties defaultProps = PropertiesUtils.loadDefault(getPortletContext().getRealPath(default_properties));
		applicationProps = new Properties(defaultProps);
		//System.out.println("[PortletUserAccountLogin.init] load default values : url_inscription " + applicationProps.getProperty("url_inscription") + " application_url " + applicationProps.getProperty("application_url") + " application_description " + applicationProps.getProperty("application_description") + " application_name " + applicationProps.getProperty("application_name"));
		
		// now load properties from last invocation
		applicationProps = PropertiesUtils.loadLastState(applicationProps, getPortletContext().getRealPath(saved_properties));
		//System.out.println("[PortletUserAccountLogin.init] load last state values : url_inscription " + applicationProps.getProperty("url_inscription") + " application_url " + applicationProps.getProperty("application_url") + " application_description " + applicationProps.getProperty("application_description") + " application_name " + applicationProps.getProperty("application_name"));
		
		//sets values 
		URL_INSCRIPTION = applicationProps.getProperty("url_inscription");
		APPLICATION_URL = applicationProps.getProperty("application_url");
		APPLICATION_DESCRIPTION = applicationProps.getProperty("application_description");
		APPLICATION_NAME = applicationProps.getProperty("application_name");
		if(APPLICATION_NAME != null)
		{
			//REPRESENTS_APPLICATION = RETRIEVE_APPLICATION.retrieveApplication(APPLICATION_NAME).getRepresents() ;
			REPRESENTS_APPLICATION = DAOUSER.retrieveApplication(APPLICATION_NAME).getRepresents() ;
			if(REPRESENTS_APPLICATION != null) this.init_application = true ;
		}
		//System.out.println("[PortletUserAccountLogin.init] default values : url_inscription " + URL_INSCRIPTION + " application_url " + APPLICATION_URL + " application_description " + APPLICATION_DESCRIPTION + " application_name " + APPLICATION_NAME);
	}

	public void destroy() 
	{
		normalView = null;
		maximizedView = null;
		helpView = null;
		editView = null ;
		super.destroy();
	}

	private void setRenderAttributes(RenderRequest request) 
	{
		request.removeAttribute("error_message");
		if(request.getParameter("error_message")!=null) request.setAttribute("error_message" , request.getParameter("error_message"));
		request.removeAttribute("log_message");
		if(request.getParameter("log_message")!=null) request.setAttribute("log_message" , request.getParameter("log_message"));
		if(URL_INSCRIPTION != null) request.setAttribute("url_inscription", URL_INSCRIPTION);
		if(APPLICATION_NAME != null) 
		{
			request.setAttribute("application_name", APPLICATION_NAME);
			request.getPortletSession().setAttribute("application_name", APPLICATION_NAME, PortletSession.APPLICATION_SCOPE);
		}
		if(APPLICATION_DESCRIPTION != null) request.setAttribute("application_description", APPLICATION_DESCRIPTION);
		if(APPLICATION_URL != null) 
		{
			request.setAttribute("application_url" ,  APPLICATION_URL);
			request.getPortletSession().setAttribute("application_url", APPLICATION_URL , PortletSession.APPLICATION_SCOPE);
		}
		if(!this.init_application)
		{
			request.setAttribute("error_init_application", "");
		}
		
		if(request.getParameter("pseudo")!=null) request.setAttribute("pseudo" , request.getParameter("pseudo"));
		if(request.getParameter("mail")!=null) request.setAttribute("mail" , request.getParameter("mail"));
		if(request.getParameter("lost_password")!=null) request.setAttribute("lost_password", request.getParameter("lost_password"));
		
		if(request.getPortletSession().getAttribute("UserLogin")!=null)
		{
			UserAccount _user = ((UserAccount)request.getPortletSession().getAttribute("UserLogin"));
			//System.out.println("[PortletUserAccountLogin.setRenderAttributes] utilisateur loggé : " + _user.getLabel() );
			request.getPortletSession().setAttribute("UserLogin", _user);
			request.setAttribute("UserLogin", _user);
			request.getPortletSession().setAttribute("UserLogin", _user, request.getPortletSession().APPLICATION_SCOPE);
			
		}
	}

	public void processAction(ActionRequest request, ActionResponse response)
	throws PortletException, PortletSecurityException, IOException 
	{
		String op = request.getParameter("op");
		StringBuffer message = new StringBuffer(1024);
		if ((op != null) && (op.trim().length() > 0)) 
		{
			//Mise à jour de l'url d'inscription via le mode edit
			if (op.equalsIgnoreCase("update")) 
			{
				doUpdate(request , response , message);
				return;
			}
			//Non mise à jour de l'url d'inscription via le mode edit
			if (op.equalsIgnoreCase("cancel")) 
			{
				doCancel(response);
				return;
			}
			if(op.equalsIgnoreCase("login"))
			{
				doLogin(request, response, message);
				return;
			}
			if(op.equalsIgnoreCase("deco"))
			{
				doDeco(response , request);
				return;
			}
			if(op.equalsIgnoreCase("lost_password"))
			{
				doLostPwd(response);
				return;
			}
			if(op.equalsIgnoreCase("envoi email"))
			{
				doDataSend(response , request);
				return;
			}
			if(op.equalsIgnoreCase("Retour"))
			{
				response.setPortletMode(PortletMode.VIEW);
				return;
			}
		}
		else 
		{
			System.out.println("[PortletUserAccountLogin.processAction 3]" + op);
			message.append("Operation is null");
		}
		System.out.println("[PortletUserAccountLogin.processAction 4]" + op);
		response.setRenderParameter("message", message.toString());
		response.setPortletMode(PortletMode.VIEW);
	}
	
	private void sendEvent(String _event_type , Serializable _event_object , ActionResponse response)
	{
		System.out.println("[PortletUserAccountLogin.sendEvent] type : " + _event_type + " value : " + _event_object);
		response.setEvent(_event_type, _event_object);
		//System.out.println("[PortletUserAccountLogin.sendEvent] type : " + _event_type + " value : " + _event_object);
	}
	
	private void doUpdate(ActionRequest request, ActionResponse response , StringBuffer message) 
	throws ReadOnlyException, ValidatorException, IOException, PortletModeException
	{
		String _new_url_inscription = request.getParameter("url");
		String _new_application_name = request.getParameter("application_name");
		String _new_application_description = request.getParameter("application_description");
		String _new_application_url = request.getParameter("application_url");
		String error_message = "" ;
		boolean error = false ;
		if(StringOp.isValidURI(_new_url_inscription))
		{
			//System.out.println("[PortletUserAccountLogin.doUpdate] nouvelle url : " + _new_url_inscription);
			URL_INSCRIPTION = _new_url_inscription ;
			applicationProps.setProperty("url_inscription", _new_url_inscription);
		}
		else 
		{
			error_message = error_message.concat("URL inscription non valide. ");
			error = true ;
		}
		if(StringOp.isNull(_new_application_name)) 
		{
			//System.out.println("[PortletUserAccountLogin.doUpdate] not a valid application name");
			error_message = error_message.concat("Nom d'application nom valide. ");
			error = true ;
		}
		else
		{
			_new_application_name = StringOp.deleteBlanks(_new_application_name);
			if(StringOp.isNull(_new_application_name)) 
			{
				error_message = error_message.concat("Nom d'application non valide. ");
				error = true ;
			}
			else
			{
				APPLICATION_NAME = _new_application_name ;
				request.getPortletSession().setAttribute("application_name", APPLICATION_NAME, request.getPortletSession().APPLICATION_SCOPE);
				applicationProps.setProperty("application_name", APPLICATION_NAME);
			}
		}
		if(StringOp.isValidURI(_new_application_url))
		{
			APPLICATION_URL = _new_application_url ;
			applicationProps.setProperty("application_url", APPLICATION_URL);
		}
		else
		{
			error_message = error_message.concat("URL de l'application non valide. ");
			error = true ;
		}
		if(_new_application_description != null)
		{
			APPLICATION_DESCRIPTION = StringOp.deleteBlanks(_new_application_description);
			applicationProps.setProperty("application_description", APPLICATION_DESCRIPTION);
		}
		if(error)
		{
			response.setRenderParameter("error_message", error_message);
			response.setPortletMode(PortletMode.EDIT);
		}
		else
		{
			//retrouver l'application
			//new RetrieveUser().retrieveAgent("toto");
			//Application _app = RETRIEVE_APPLICATION.retrieveApplication(APPLICATION_NAME);
			Application _app = DAOUSER.retrieveApplication(APPLICATION_NAME);
			//System.out.println("[PortletUserAccountLogin.doUpdate] l'application : " + APPLICATION_NAME + " a l'id : " + _app.getId());
			if(_app.getId() == null) //si l'application n'existe pas
			{
				EntityManagerFactory emf_resource = Persistence.createEntityManagerFactory("resource");
				DAOResource DAOResource = new DAOResource(emf_resource);
				//System.out.println("[PortletUserAccountLogin.doUpdate] doit créer l'application : " + APPLICATION_NAME);
				//créer la Resource de représentation
				//URI _uri_represents = new CreateUri().createAndGetURI(APPLICATION_URL);
				URI _uri_represents = DAOResource.createAndGetURI(APPLICATION_URL);
				//créer la ressource de description
				//List _resources = new RetrieveResource().retrieveResource(APPLICATION_NAME, _uri_represents);
				List _resources = DAOResource.retrieveResource(APPLICATION_NAME, _uri_represents);
				if(_resources.size() > 0) REPRESENTS_APPLICATION = (Resource)_resources.get(0);
				else REPRESENTS_APPLICATION = DAOResource.createAndGetResource("[PortletUserAccountLogin.doUpdate]", APPLICATION_NAME, _uri_represents);//REPRESENTS_APPLICATION = new CreateResource().createAndGetResource("[PortletUserAccountLogin.doUpdate]", APPLICATION_NAME, _uri_represents);
				if(REPRESENTS_APPLICATION.getId() == null)
				{
					REPRESENTS_APPLICATION = null;
					response.setRenderParameter("error_message", "problème à la création de la ressource. Voir configuration BDD.");
					response.setPortletMode(PortletMode.EDIT);
				}
				//créer l'application
				else DAOUSER.createApplication(APPLICATION_NAME, APPLICATION_DESCRIPTION, REPRESENTS_APPLICATION);//CREATOR_APPLICATION.createApplication(APPLICATION_NAME, APPLICATION_DESCRIPTION, REPRESENTS_APPLICATION);
				this.init_application = true ;
			}
			this.sendEvent("newApplicationName", APPLICATION_NAME, response);
			PropertiesUtils.store(applicationProps , getPortletContext().getRealPath(saved_properties), "[PortletUserAccountLogin.doUpdate]");
			response.setPortletMode(PortletMode.VIEW);
		}
	}
	
	private void doCancel(ActionResponse response) 
	throws ReadOnlyException, ValidatorException, IOException, PortletModeException
	{
		response.setPortletMode(PortletMode.VIEW);
	}
	
	private void doDeco(ActionResponse response , ActionRequest  request) 
	throws ReadOnlyException, ValidatorException, IOException, PortletModeException
	{
		//Alerter que l'utilisateur est déconnecté
		this.sendEvent("UserUnLog", "", response);
		request.getPortletSession().removeAttribute("UserLogin");
		response.setPortletMode(PortletMode.VIEW);
	}
	
	private void doLogin(ActionRequest request, ActionResponse response , StringBuffer message) 
	throws ReadOnlyException, ValidatorException, IOException, PortletModeException
	{
		String pseudo = request.getParameter("pseudo");
		String password = request.getParameter("password");
		if(pseudo == null || pseudo.isEmpty()) 
		{
			response.setRenderParameter("error_message", "champ Pseudo obligatoire");
			response.setPortletMode(PortletMode.VIEW);
		}
		else
		{
			if(password == null || password.isEmpty())
			{
				response.setRenderParameter("error_message", "Password obligatoire");
				response.setRenderParameter("pseudo", pseudo);
				response.setPortletMode(PortletMode.VIEW);
			}
			else
			{
				//tester si l'utilisateur existe
				//UserAccount _user = CONTROLEUR_USER.retrieveUserAccount(pseudo, APPLICATION_NAME);
				UserAccount _user = DAOUSER.retrieveUserAccount(pseudo, APPLICATION_NAME);
				if(_user.getId() != null)
				{
					//System.out.println("[PortletUserAccountLogin.doLogin] l'utilisateur existe, id : " + _user.getId());
					//tester si le mot de passe correspond
					if(_user.getPassword().compareTo(password) == 0)
					{
						//logger !!!
						//response.setRenderParameter("log_message", "Bienvenue sur le site " + _user.getPseudonyme());
						request.getPortletSession().setAttribute("UserLogin", _user);
						//envoyer event UserAccount
						this.sendEvent("UserLog", _user, response);
						request.getPortletSession().setAttribute("UserLogin", _user, request.getPortletSession().APPLICATION_SCOPE);
						response.setPortletMode(PortletMode.VIEW);
						
					}
					else
					{
						//le mot de passe ne correspond pas
						response.setRenderParameter("error_message", "Données incorrectes");
						response.setRenderParameter("pseudo", pseudo);
						response.setPortletMode(PortletMode.VIEW);
					}
				}
				else
				{
					//l'utilisateur n'existe pas
					response.setRenderParameter("error_message", "Données incorrectes");
					response.setPortletMode(PortletMode.VIEW);
				}
			}
		}
	}
	
	private void doLostPwd(ActionResponse response) 
	throws ReadOnlyException, ValidatorException, IOException, PortletModeException
	{
		response.setRenderParameter("lost_password", "");
		response.setPortletMode(PortletMode.VIEW);
	}
	
	private void doDataSend(ActionResponse response , ActionRequest request) 
	throws ReadOnlyException, ValidatorException, IOException, PortletModeException
	{
		//récupérer l'email
		String mail = request.getParameter("mail");
		//vérifier l'email
		if(StringOp.isValidMail(mail))
		{
			//vérifier que l'email existe
			//Person _person = CONTROLEUR_USER.retrievePerson(mail);
			Person _person = DAOUSER.retrievePerson(mail);
			if(_person.getId() != null)
			{
				//envoi de l'email
				//TODO
				//notification et retour page de login
				response.setRenderParameter("error_message", "email envoyé");
			}
			else
			{
				//n'existe pas dans la base
				response.setRenderParameter("lost_password", "");
				response.setRenderParameter("mail" , mail);
				response.setRenderParameter("error_message", "Cet email est inconnu de l'application");
			}
		}
		else
		{
			//erreur de saisie de l'email
			//System.out.println("[PortletUserAccountLogin.doDataSend] mail non valide");
			response.setRenderParameter("lost_password", "");
			response.setRenderParameter("error_message", "Mail non valide");
		}	
		response.setPortletMode(PortletMode.VIEW);
	}
}