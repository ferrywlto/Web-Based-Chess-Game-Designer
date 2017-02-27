package webBaseChessGameDesigner.system;

import java.util.HashMap;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RuleClassMapper 
{
	// This class works like a dynamic class loader using Java Reflection
	// by using this class, both client and server can refer to single XML
	// file for action/condition ID mapping.
	
	static boolean ruleMapped = false;
	static final HashMap<Integer,String> conditionMap = new HashMap<Integer,String>(0);
	static final HashMap<Integer,String> actionMap = new HashMap<Integer,String>(0);
	
	protected static final RuleClassMapper mapper = new RuleClassMapper();
	
	public static void startRuleMapping()
	{
		if(!ruleMapped)
		{
			String filePath = ServerConfiguration.getStringParameter("RuleMapperFileLocation");
			String fileName = ServerConfiguration.getStringParameter("RuleMapperFileName");
			XMLLoader loader = XMLLoader.getLoader();
			Document ruleMappingDoc = loader.getXMLDocumentFromFile(filePath+fileName);
			
			boolean valid = loader.validateXMLDocument(loader.getRuleMapperValidator(), ruleMappingDoc);
			if(valid)
			{
				Element root = ruleMappingDoc.getDocumentElement();
				NodeList con_nodes = root.getElementsByTagName("condition");
				NodeList act_nodes = root.getElementsByTagName("action");
				
				Messager.printMsg(mapper,"Registering Rule Classes...");
				for(int i=0; i<con_nodes.getLength(); i++)
				{
					Element node = (Element)con_nodes.item(i);
					String id = node.getElementsByTagName("ID").item(0).getFirstChild().getTextContent();
					String name = node.getElementsByTagName("name").item(0).getFirstChild().getTextContent();
					conditionMap.put(Integer.parseInt(id), name);
					Messager.printMsg(mapper,"Condition[ID:"+id+"|name:"+name+"]");
					NodeList param_nodes = node.getElementsByTagName("param");
					for(int j=0; j<param_nodes.getLength(); j++)
					{
						String iname = ((Element)param_nodes.item(j)).getElementsByTagName("name").item(0).getFirstChild().getTextContent();
						String type = ((Element)param_nodes.item(j)).getElementsByTagName("type").item(0).getFirstChild().getTextContent();
						Messager.printMsg(mapper,"Param[name:"+iname+"|type:"+type+"]");
					}
				}
				for(int i=0; i<act_nodes.getLength(); i++)
				{
					Element node = (Element)act_nodes.item(i);
					String id = node.getElementsByTagName("ID").item(0).getFirstChild().getTextContent();
					String name = node.getElementsByTagName("name").item(0).getFirstChild().getTextContent();
					actionMap.put(Integer.parseInt(id), name);
					Messager.printMsg(mapper,"Action[ID:"+id+"|name:"+name+"]");
					NodeList param_nodes = node.getElementsByTagName("param");
					for(int j=0; j<param_nodes.getLength(); j++)
					{
						String iname = ((Element)param_nodes.item(j)).getElementsByTagName("name").item(0).getFirstChild().getTextContent();
						String type = ((Element)param_nodes.item(j)).getElementsByTagName("type").item(0).getFirstChild().getTextContent();
						Messager.printMsg(mapper,"Param[name:"+iname+"|type:"+type+"]");
					}
				}
				ruleMapped = true;
				Messager.printMsg(mapper,"Rule Classes Registration Completed.\n");
			}
			else
			{
				Messager.printMsg(mapper,"Rule Mapping Failed.");
				System.exit(0);
			}
		}
	}

	public static String getConditionClassName(int ID)
	{
		if(ruleMapped)
			return conditionMap.get(ID);
		else
			return null;
	}
	
	public static String getActionClassName(int ID)
	{
		if(ruleMapped)
			return actionMap.get(ID);
		else
			return null;
	}
}
