package webBaseChessGameDesigner.system;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class ServerConfiguration 
{
	static Hashtable<String, String> parameters;
	static boolean loaded = false;
	
	private ServerConfiguration(){}
	
	public static String getStringParameter(String paramName)
	{
		try
		{
			if(parameters == null) 
				throw new Exception("Configuration not initialized.");
			if(!parameters.containsKey(paramName))
				throw new Exception("Parameter not found: "+paramName);
			
			String param = parameters.get(paramName);
			if(param == null)
				throw new Exception("Null parameter encountered.");

			return param;

		}catch(Exception e){e.printStackTrace(); return "";}
	}
	
	public static int getNumericParameter(String paramName)
	{
		return Integer.parseInt(getStringParameter(paramName));
	}
	
	public static boolean loadConfiguration(String path)
	{
		try
		{
			if(!loaded)
			{
				if(parameters == null)
					parameters = new Hashtable<String, String>(0);
				else
					parameters.clear();	// Remove all parameters first
				
				File detector = new File(path);
				if(detector.exists())
				{
					XMLLoader loader = XMLLoader.getLoader();
					Messager.printMsg("ServerConfiguration","Load Server Configuration File: "+path);
					Document configDoc = loader.getXMLDocumentFromFile(path);
			
					Element root = configDoc.getDocumentElement();
					NodeList nodes = root.getChildNodes();
					Messager.printMsg("ServerConfiguration","Parsing Server Config Parameters...");
					for(int i=0; i<nodes.getLength(); i++)
					{
						Node node = nodes.item(i);
						if( (node.getNodeType() == Node.ELEMENT_NODE) && 
								(node.getFirstChild().getNodeType() == Node.TEXT_NODE))
						{
							parameters.put(node.getNodeName(), node.getFirstChild().getTextContent());
						}
					}
					listParameters();
					Messager.printMsg("ServerConfiguration","Server Configuration Loading Completed.\n");
					loaded = true;
					return true;
				}
				else
				{
					throw new Exception("Config file not found.");
				}
			}
		}
		catch(Exception e){e.printStackTrace();return false;}
		return false;
	}
	
	public static void listParameters()
	{
		// For debug purpose
		Enumeration<String> e = parameters.keys();
		
		while(e.hasMoreElements())
		{
			String id = e.nextElement();
			Messager.printMsg("ServerConfiguration",id+" = "+parameters.get(id));
			
		}
	}
	public static String listParametersStr()
	{
		// For debug purpose
		Enumeration<String> e = parameters.keys();
		String result = "";
		while(e.hasMoreElements())
		{
			String id = e.nextElement();
			result += (id+" = "+parameters.get(id)+"\n");
		}
		return result;
	}
}
