package fr.natoine.test;


import junit.framework.TestCase;

public class PortletLoginTest extends TestCase
{
	public PortletLoginTest(String name) 
	{
	    super(name);
	}
	
	public void testRetrieveUserAccount()
	{
		/*RetrieveUser CONTROLEUR_USER = new RetrieveUser();
		CONTROLEUR_USER.retrieveUserAccount("pseudonyme", "UserControlerTestApplication.testuserAccount");
	*/
	}
	
	/*
	public void testGenerateApplication()
	{
		String APPLICATION_URL = "http://test.portletlogin.testGenerateApplication" ;
			//créer la Resource de représentation
			URI _uri_represents = new RetrieveUri().retrieveURI(APPLICATION_URL);
			if(_uri_represents.getId() == null)
			{
				new CreateUri().createURI(APPLICATION_URL);
				_uri_represents = new RetrieveUri().retrieveURI(APPLICATION_URL);
			}
			//créer la ressource de description
			Resource REPRESENTS_APPLICATION = new CreateResource().createAndGetResource("[PortletLoginTest]", 
					"testGenerateApplicationapplicationname", _uri_represents);
			if(REPRESENTS_APPLICATION.getId() == null)
			{
				REPRESENTS_APPLICATION = null;
			}
		
		//créer l'application
		new CreateApplication().createApplication("testGenerateApplicationapplicationname", "description de l'application", REPRESENTS_APPLICATION);
	}
	public void testGenerateURLFromApp()
	{
		Application _app = new RetrieveApplication().retrieveApplication("testGenerateApplicationapplicationname");
		String url = _app.getRepresents().getRepresentsResource().getEffectiveURI() + "/person#" + "firstname" + "lastname" ;
		System.out.println("[PortletLoginTest.testGenerateURLFromApp] url : " + url);
	}
	*/
}