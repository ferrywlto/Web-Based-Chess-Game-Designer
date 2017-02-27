/**
 * 
 */
package webBaseChessGameDesigner.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author    Ferry To
 * @uml.dependency   supplier="webBaseChessGameDesigner.system.Messager.printMsg"
 */
public class XMLLoader 
{
	private static SchemaFactory sFactory;
	private static DocumentBuilderFactory dFactory;
	private static DocumentBuilder builder;
	/**
	 * @uml.property   name="loader"
	 */
	private static XMLLoader loader;
	
	/**
	 * @return  the loader
	 * @uml.property  name="loader"
	 */
	public static XMLLoader getLoader()
	{
		if (loader != null)
			return loader;
		else
			return new XMLLoader();
	}
	
	/*
	 * static String schemaFileName;
			static String schemaFilePath;
			trace("Load Chess Game Design Specification Schema: "+schemaFilePath+schemaFileName);
	 */
	
	// Singleton Pattern used to ensure only one instance created.
	private XMLLoader()
	{
		try
		{
			sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			dFactory = DocumentBuilderFactory.newInstance();
			builder = dFactory.newDocumentBuilder();
		}
		catch(ParserConfigurationException pce){ pce.printStackTrace(); System.exit(0);}
	}
	
	
	public Validator getRuleMapperValidator()
	{
		String fPath = ServerConfiguration.getStringParameter("RuleMapperSchemaFileLocation");
		String fName = ServerConfiguration.getStringParameter("RuleMapperSchemaFileName");
		return getXMLValidatorFromFile(fPath+fName);
	}
	
	public Validator getSpecificationValidator()
	{
		String fPath = ServerConfiguration.getStringParameter("SpecificationSchemaFileLocation");
		String fName = ServerConfiguration.getStringParameter("SpecificationSchemaFileName");
		return getXMLValidatorFromFile(fPath+fName);
		
	}
	
	public Validator getCommandValidator()
	{
		String fPath = ServerConfiguration.getStringParameter("CommandSchemaFileLocation");
		String fName = ServerConfiguration.getStringParameter("CommandSchemaFileName");
		return getXMLValidatorFromFile(fPath+fName);
	}
	
	// IMPORTANT! This is the way how Flash communicate with Servlet by XML!
	// Especially for Servlet that getting a pure XML document form a client.
	// Usage: getXMLDocumentFromHttpRequest(reg.getReader());
	public Document getXMLDocumentFromHttpRequest(BufferedReader br)
	{
		try { return builder.parse(new InputSource(br)); }
		catch (Exception e) 
		{
			e.printStackTrace(); 
			return null;
		}
	}

	// Convert a long XML string into an XML Document Object.
	// Usage: getXMLDocumentFromString("<xml><root><foo name="bar"/></root></xml>");
	// P.S. also can use it to parse a HTTP Request parameter.
	public Document getXMLDocumentFromString(String xmlStr)
	{	
		try { return builder.parse(new InputSource(new StringReader(xmlStr))); }
		catch (SAXParseException e)
		{
			e.printStackTrace();
			Messager.printMsg(this,"[XMLLoader]: Document is not well-formed.");
			return null;
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			Messager.printMsg(this,"Document is invaild.");
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Messager.printMsg(this,"Cannot get document.");
			return null;
		}
	}

	// Get an XML Document Object by parsing an XML on the server.
	public Document getXMLDocumentFromFile(String fileName)
	{	
		try { return builder.parse(new InputSource(new FileReader(new File(fileName)))); }
		catch (Exception e) 
		{
			e.printStackTrace(); 
			return null;
		}
	}
	
	// This version is to grab XML document from an InputStream, 
	// if all three versions above does not suit for you, 
	// this should be the one you need. 
	public Document getXMLDocumentFromStream(InputStream is)
	{
		try { return builder.parse(new InputSource(is)); }
		catch (Exception e) 
		{
			e.printStackTrace(); 
			return null;
		}
	}
	
	// Get a validator object which created by the specified XML schema
	// for validate against an XML Document.
	public Validator getXMLValidatorFromFile(String fileName)
	{
		try
		{
			File validatorFile = new File(fileName);
			if(!(validatorFile.exists() && validatorFile.isFile()))
			{
				fileName = (ServerConfiguration.getStringParameter("WebappsLocation")+fileName);
				validatorFile = new File(fileName);
				// If still not found after concated the webapp location, means path have problem
				if(!(validatorFile.exists() && validatorFile.isFile()))
				{
					throw new Exception("Validator File Not Found.");
				}
			}
			Schema schema = sFactory.newSchema(validatorFile);
			return schema.newValidator();
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
			return null;
		}
	}
	
	// Just a handy function. One can get the Validator by getXMLValidatorFromFile()
	// and validate an XML by themselves.
	public boolean validateXMLDocument(Validator validator, Document doc)
	{
		try
		{
			validator.validate(new DOMSource(doc));
			return true;
		}
		catch(SAXParseException spe)
		{
			Messager.printMsg(this,"Document is invalid.");
			//spe.printStackTrace();
			return false;			
		}
		catch(SAXException se)
		{
			Messager.printMsg(this,"Document is invalid.");
			//se.printStackTrace();
			return false;
		}
		catch(IOException ioe)
		{
			Messager.printMsg(this,"I/O Problem.");
			//ioe.printStackTrace();
			return false;
		}
	}
}
