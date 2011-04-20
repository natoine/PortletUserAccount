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
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Event;
import javax.portlet.EventPortlet;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
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

public class PortletUserAccountInscription extends GenericPortlet implements EventPortlet
{
	private Properties applicationProps ;
	private final String saved_properties = "/properties/appProperties";
	private final String default_properties = "/properties/defaultProperties";
	
	private static String APPLICATION_NAME = null ;
	
	private static Application APPLICATION = null ;
	private static String APPLICATION_URL = null ;
	private static DAOUser DAOUSER = null ;
	private static DAOResource DAORESOURCE = null ;
	private static EntityManagerFactory emf_resource = null ; // Persistence.createEntityManagerFactory("resource");
	private static EntityManagerFactory emf_user = null ; // Persistence.createEntityManagerFactory("user");
	
    private static final String NORMAL_VIEW = "/WEB-INF/jsp/inscription/normal.jsp";
    private static final String MAXIMIZED_VIEW = "/WEB-INF/jsp/inscription/normal.jsp";
    private static final String HELP_VIEW = "/WEB-INF/jsp/inscription/help.jsp";

    private PortletRequestDispatcher normalView;
    private PortletRequestDispatcher maximizedView;
    private PortletRequestDispatcher helpView;

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
        throws PortletException, IOException {
        helpView.include( request, response );
    }

    public void init( PortletConfig config ) throws PortletException {
        super.init( config );
        normalView = config.getPortletContext().getRequestDispatcher( NORMAL_VIEW );
        maximizedView = config.getPortletContext().getRequestDispatcher( MAXIMIZED_VIEW );
        helpView = config.getPortletContext().getRequestDispatcher( HELP_VIEW );
       
        emf_resource = Persistence.createEntityManagerFactory("resource");
        emf_user = Persistence.createEntityManagerFactory("user");
        DAOUSER = new DAOUser(emf_user);
        DAORESOURCE = new DAOResource(emf_resource);
        
     // create application properties with default
		Properties defaultProps = PropertiesUtils.loadDefault(getPortletContext().getRealPath(default_properties));
		applicationProps = new Properties(defaultProps);
		//System.out.println("[PortletUserAccountInscription.init] load default values : url_inscription " + applicationProps.getProperty("url_inscription") + " application_url " + applicationProps.getProperty("application_url") + " application_description " + applicationProps.getProperty("application_description") + " application_name " + applicationProps.getProperty("application_name"));
		
		// now load properties from last invocation
		applicationProps = PropertiesUtils.loadLastState(applicationProps, getPortletContext().getRealPath(saved_properties));
		//System.out.println("[PortletUserAccountInscription.init] load last state values : url_inscription " + applicationProps.getProperty("url_inscription") + " application_url " + applicationProps.getProperty("application_url") + " application_description " + applicationProps.getProperty("application_description") + " application_name " + applicationProps.getProperty("application_name"));
		
		//sets values 
		APPLICATION_NAME = applicationProps.getProperty("application_name");
		if(APPLICATION_NAME != null)
		{
			//APPLICATION = new RetrieveApplication().retrieveApplication(APPLICATION_NAME);
			APPLICATION = DAOUSER.retrieveApplication(APPLICATION_NAME);
			if(APPLICATION.getId() == null) APPLICATION = null ; 
		} 
		if(applicationProps.getProperty("application_url") != null) APPLICATION_URL = applicationProps.getProperty("application_url");
    }

    public void destroy() 
    {
        normalView = null;
        maximizedView = null;
        helpView = null;
        super.destroy();
    }

    private void setRenderAttributes(RenderRequest request) 
	{
    	if(request.getPortletSession().getAttribute("application_name", PortletSession.APPLICATION_SCOPE)!=null)
		{
			//System.out.println("[PortletUserAccountInscription.setRenderAttributes] application defined at application_scope");
			request.removeAttribute("config_error_message");
			String _new_app_name = (String)request.getPortletSession().getAttribute("application_name", PortletSession.APPLICATION_SCOPE) ;
			if(APPLICATION_NAME == null || APPLICATION_NAME.compareTo(_new_app_name)!= 0) 
				//APPLICATION = new RetrieveApplication().retrieveApplication(_new_app_name);
				APPLICATION = DAOUSER.retrieveApplication(_new_app_name);
			APPLICATION_NAME = _new_app_name ;
		}
    	if(APPLICATION_NAME != null)
    	{
    		if(APPLICATION == null || APPLICATION.getId() == null) APPLICATION = DAOUSER.retrieveApplication(APPLICATION_NAME); //APPLICATION = new RetrieveApplication().retrieveApplication(APPLICATION_NAME);
    		if(APPLICATION.getId() != null)
    		{
        		request.removeAttribute("config_error_message");	
        		request.setAttribute("application_name", APPLICATION_NAME);
    		}
    		else request.setAttribute("config_error_message", "");
    	}
    	else request.setAttribute("config_error_message", "");
       	if(request.getPortletSession().getAttribute("application_url", PortletSession.APPLICATION_SCOPE)!=null)
		{
       		APPLICATION_URL = (String) request.getPortletSession().getAttribute("application_url", PortletSession.APPLICATION_SCOPE) ;
       		request.setAttribute("application_url", APPLICATION_URL);
		}
    	
    	if(request.getParameter("error_message")!=null) request.setAttribute("error_message", request.getParameter("error_message"));
    	
		if(request.getParameter("description")!=null) request.setAttribute("description" , request.getParameter("description"));
		if(request.getParameter("url")!=null) request.setAttribute("url" , request.getParameter("url"));
		if(request.getParameter("firstname")!=null) request.setAttribute("firstname" , request.getParameter("firstname"));
		if(request.getParameter("lastname")!=null) request.setAttribute("lastname" , request.getParameter("lastname"));
		if(request.getParameter("pseudo")!=null) request.setAttribute("pseudo" , request.getParameter("pseudo"));
		if(request.getParameter("mail")!=null) request.setAttribute("mail" , request.getParameter("mail"));
	}
    
    public void processEvent(EventRequest request, EventResponse response)
	{
		Event event = request.getEvent();
		//System.out.println("[PortletUserAccountInscription.processEvent] event : " + event.getName());
		if(event.getName().equals("newApplicationName"))
		{
			Object _application_name = event.getValue();
			if(_application_name instanceof String)
			{
				APPLICATION_NAME = (String)_application_name ;
				//APPLICATION = new RetrieveApplication().retrieveApplication(APPLICATION_NAME);
				APPLICATION = DAOUSER.retrieveApplication(APPLICATION_NAME);
			}
		}
	}
    
    public void processAction(ActionRequest request, ActionResponse response)
	throws PortletException, PortletSecurityException, IOException 
	{
		String op = request.getParameter("op");
		StringBuffer message = new StringBuffer(1024);
		if ((op != null) && (op.trim().length() > 0)) 
		{
			if(op.equalsIgnoreCase("Create Account"))
			{
				doCreateAccount(response , request);
				return;
			}
		}
		else 
		{
			System.out.println("[PortletUserAccountInscription.processAction 3]" + op);
			message.append("Operation is null");
		}
		System.out.println("[PortletUserAccountInscription.processAction 4]" + op);
		response.setRenderParameter("message", message.toString());
		response.setPortletMode(PortletMode.VIEW);
	}
    
    private void doCreateAccount(ActionResponse response , ActionRequest request) 
	throws ReadOnlyException, ValidatorException, IOException, PortletModeException
	{
    	//System.out.println("[PortletUserAccountInscription.doCreateAccount]");
    	String firstname = request.getParameter("firstname");
    	String lastname = request.getParameter("lastname");	
    	String mail = request.getParameter("mail");
		String pseudo = request.getParameter("pseudo");
		String description = request.getParameter("description");
		String url = request.getParameter("url");
		String password1 = request.getParameter("password_1");
		String password2 = request.getParameter("password_2");
		String error_message = "" ;
		boolean error = false ;
		if(firstname == null || firstname.isEmpty()) 
		{
			error = true ;
			error_message = error_message.concat("Champ prénom obligatoire. ");
		}
		if(lastname == null || lastname.isEmpty()) 
		{
			error = true ;
			error_message = error_message.concat("Champ nom obligatoire. ");
		}
		if(mail == null || mail.isEmpty()) 
		{
			error = true ;
			error_message = error_message.concat("Champ mail obligatoire. ");
		}
		else if(! StringOp.isValidMail(mail))
		{
			error = true ;
			error_message = error_message.concat("Mail non valide. ");
		}
		else response.setRenderParameter("mail", mail);
		if(pseudo == null || pseudo.isEmpty()) 
		{
			error = true ;
			error_message = error_message.concat("Champ Pseudonyme obligatoire. ");
		}
		else
		{
			//tester disponibilité du pseudo
			//UserAccount _pseudo_test = RETRIEVE_USER.retrieveUserAccount(pseudo, APPLICATION_NAME);
			UserAccount _pseudo_test = DAOUSER.retrieveUserAccount(pseudo, APPLICATION_NAME);
			if(_pseudo_test.getId() != null) //pseudo non dispo
			{
				error_message = error_message.concat("Pseudonyme non disponible. ");
			}
			else
			{
				response.setRenderParameter("pseudo", pseudo);
			}
		}
		if(password1 == null || password1.isEmpty() || password2 == null || password2.isEmpty()) 
		{
			error = true ;
			error_message = error_message.concat("Champs password obligatoires. ");
		}
		else
		{
			if(password1.compareTo(password2) != 0)
			{
				error = true ;
				error_message = error_message.concat("Les deux mots de passe doivent être identiques. ");
			}
		}
		if(StringOp.isValidURI(url))
		{
			//System.out.println("[PortletUserAccountInscription.doCreateAccount] url valid : " + url);
			response.setRenderParameter("url", url);
		}
		else
		{
			if(! url.isEmpty()) error_message = error_message.concat("URL non valide. ");
		}
		if(error)
		{
			response.setRenderParameter("firstname", firstname);
			response.setRenderParameter("lastname", lastname);
			response.setRenderParameter("description", description);
			response.setRenderParameter("error_message", error_message);
			response.setPortletMode(PortletMode.VIEW);
		}
		else
		{
			if(APPLICATION != null)
			{
				//récupérer la personne
				//Person _person = RETRIEVE_USER.retrievePerson(mail);
				Person _person = DAOUSER.retrievePerson(mail);
				String _person_label = firstname + lastname ;
				_person_label = StringOp.deleteBlanks(_person_label);
				_person_label = _person_label.replaceAll(" ", "_");
				//System.out.println("[PortletUserAccountInscription.doCreateAccount] person id : " + _person.getId());
				if(_person.getId() == null) //si la personne n'existe pas
				{	
					//créer l'URI et la ressource représentant la personne
					if(url == null || url.isEmpty())
					{
						//TODO A modifier quand décidé de quelle stratégie de génération de page pour les personnes
						//générer une URL à partir de l'application
						//url = APPLICATION.getRepresents().getRepresentsResource().getEffectiveURI() + "/person#" + firstname + lastname ;
						url = APPLICATION.getRepresents().getRepresentsResource().getEffectiveURI() + "/person#" + _person_label ;
						//System.out.println("[PortletUserAccountInscription.doCreateAccount] generated url : " + url);
					}
					//CREATE_URI.createURI(url);
					//URI _uri_person = RETRIEVE_URI.retrieveURI(url);
					URI _uri_person = DAORESOURCE.createAndGetURI(url);
					//créer la Resource
					//Resource represents = CREATE_RESOURCE.createAndGetResource("[PortletUserAccountInscription.doCreateAccount] Application : " + APPLICATION_NAME,
					Resource represents = DAORESOURCE.createAndGetResource("[PortletUserAccountInscription.doCreateAccount] Application : " + APPLICATION_NAME,
							"person." + _person_label, _uri_person);
					//System.out.println("[PortletUserAccountInscription.doCreateAccount] resource to represent person created id : " + represents.getId());
					//créer la personne
					//System.out.println("[PortletUserAccountInscription.doCreateAccount] gonna creates person firstname : " + firstname + " lastname : " + lastname);
					//CONTROLEUR_USER.createPerson(firstname, lastname, mail, "représentation de la personne " + firstname + lastname, represents, APPLICATION, null);
					DAOUSER.createPerson(firstname, lastname, mail, "représentation de la personne " + firstname + lastname, represents, APPLICATION, null);
					//_person = RETRIEVE_USER.retrievePerson(mail);
					_person = DAOUSER.retrievePerson(mail);
					//System.out.println("[PortletUserAccountInscription.doCreateAccount] person created id : " + _person.getId());
				}
				//si le pseudo est libre
				//tester disponibilité du pseudo
				//UserAccount _pseudo_test = RETRIEVE_USER.retrieveUserAccount(pseudo, APPLICATION_NAME);
				UserAccount _pseudo_test = DAOUSER.retrieveUserAccount(pseudo, APPLICATION_NAME);
				if(_pseudo_test.getId() != null) //pseudo non dispo
				{
					error_message = error_message.concat("Pseudonyme non disponible. ");
					response.setRenderParameter("firstname", firstname);
					response.setRenderParameter("lastname", lastname);
					response.setRenderParameter("description", description);
					response.setRenderParameter("error_message", error_message);
					response.setPortletMode(PortletMode.VIEW);
				}
				else
				{
					//if(_person != null)
					if(_person.getId() != null)
					{
						//System.out.println("[[PortletUserAccountInscription.doCreateAccount] person resource : " + _person.getRepresents());
						//créer le compte
						//boolean _creation = CONTROLEUR_USER.createUserAccount(pseudo, password1, description, _person.getRepresents(), _person, APPLICATION, null);
						boolean _creation = DAOUSER.createUserAccount(pseudo, password1, description, _person.getRepresents(), _person, APPLICATION, null);
						if(_creation) 
						{
							String _err = "L'utilisateur " + pseudo + " vient d'être créé." ;
							if(APPLICATION_URL != null) 
							{
								_err = _err.concat("  <a href=\"" + APPLICATION_URL + "\" ><button type=\"button\">Retour à la page d'accueil</button></a>");
							}
							response.setRenderParameter("error_message", _err);
						}
						else response.setRenderParameter("error_message", "Problème lors de la création de l'utilisateur " + pseudo + ". Veuillez retenter ultérieurement.");
						response.setPortletMode(PortletMode.VIEW);
						//TODO
						//send email de confirmation
					}
					else
					{
						response.setRenderParameter("error_message", "problème à la création de l'utilisateur. Veuillez retenter.");
						response.setPortletMode(PortletMode.VIEW);
					}
				}
			}
			else
			{
				response.setRenderParameter("error_message", "problème de configuration. L'admin devrait tenter de reconfigurer le portlet login en mode EDIT.");
				response.setPortletMode(PortletMode.VIEW);
			}
		}
	}
}