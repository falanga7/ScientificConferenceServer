package it.unisa.mobileprogramming;

import java.io.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

//Sets the path to base URL + /users
@Path("/users")
public class Test {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {
		return "Hello Users written in TEXT format!";
	}

	@GET
	@Produces(MediaType.TEXT_XML)
	public String sayXMLHello() {
		return "<?xml version=\"1.0\"?>" + "<hello> Hello Users written in XML format" + "</hello>";
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		return "<html> " + "<title>" + "Hello Users" + "</title>" + "<body><h1>" + "Hello Users written in HTML format"
				+ "</body></h1>" + "</html> ";
	}
}