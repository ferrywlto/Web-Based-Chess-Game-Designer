package webBaseChessGameDesigner.system;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import webBaseChessGameDesigner.framework.core.Chess;
import webBaseChessGameDesigner.framework.core.Region;
import webBaseChessGameDesigner.framework.core.RuleComponent;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;
import webBaseChessGameDesigner.framework.managers.MapManager;
import webBaseChessGameDesigner.framework.managers.ObjectManager;
import webBaseChessGameDesigner.framework.managers.PlayerManager;
import webBaseChessGameDesigner.framework.managers.RuleManager;
import webBaseChessGameDesigner.framework.types.AttributeDataType;

/**
 * @author  Ferry To
 */
public class ChessBuilder 
{
	static ChessBuilder builder;
	static Validator validator;

	private ChessBuilder()
	{
		// If getBuilder is called before setSchema(), 
		// schema will be loaded from default location.
		
		// Once the builder is set up, it should only follow 
		// the same schema until the server shutdown.
		if(validator == null)
		{
			XMLLoader loader = XMLLoader.getLoader();
			Messager.printMsg(this,"Chess Builder Initializing...");

			validator = loader.getSpecificationValidator();
		}
	}
	
	// Singleton Pattern used here to ensure only one instnace of ChessBuilder will be created.
	/**
	 * @return  the builder
	 * @uml.property  name="builder"
	 */
	public static ChessBuilder getBuilder()
	{
		if(builder != null) return builder;
		else 
		{
			builder = new ChessBuilder();
			return builder;
		}
	}
	
	public String getSpecificationString(String specificationFileName)
	{
		String filePath = ServerConfiguration.getStringParameter("SpecificationStorageLocation");
		String resultStr = "";
		String temp = "";
		try
		{
			FileReader fr = new FileReader(filePath+specificationFileName);
			BufferedReader br = new BufferedReader(fr);
			while( (temp=br.readLine()) != null )
				resultStr += temp;
			return resultStr;
		}
		catch(FileNotFoundException fne)
		{ 
			Messager.printMsg(this,"File not found.");
			fne.printStackTrace();
			return null;
		}
		catch(IOException ioe) 
		{ 
			Messager.printMsg(this,"File cannot read.");
			ioe.printStackTrace();
			return null;
		}
	}
	
	
	public Chess parseChessSpecification(String specificationFileName)
	{
		XMLLoader loader = XMLLoader.getLoader();
		Messager.printMsg(this,"Validate Chess Game Design Specification: "+specificationFileName);
		String specString = getSpecificationString(specificationFileName);
		
		if(specString == null) return null;
		
		Document specDoc = loader.getXMLDocumentFromString(specString);
		boolean isValid = loader.validateXMLDocument(validator, specDoc);
		if(isValid)
		{
			Messager.printMsg(this,"Specification is valid. Start parsing chess game definition...");

			Element root = specDoc.getDocumentElement();
			String chessName = root.getAttribute("name");
			String chessRule = root.getAttribute("turnCheck");
			
			Chess myChess = new Chess(chessName,specString);
			GeneralResourceManager grm = myChess.getGRM();
			buildPlayers(root,grm.getPlayerManager());
			buildMap(root,grm.getMapManager());
			buildRules(root,grm.getRuleManager());
			buildObjects(root,grm.getObjectManager());
			
			// Finally have to add the turnCheck rule back to chess object.
			myChess.setTurnRule(chessRule);
			
			Messager.printMsg(this,"Reconstruction of Chess Definition Completed.");
			return myChess;
		}
		else
		{
			Messager.printMsg(this,"Specification is invalid. Returning NULL to upper layer.");
			return null;
		}
	}
		
	protected void buildRules(Element root, RuleManager ruleM)
	{
		Messager.printMsg(this,"Start building rules...");
		NodeList ruleNodes = root.getElementsByTagName("rule");
		for(int i=0; i<ruleNodes.getLength(); i++)
		{
			Element rule = (Element)ruleNodes.item(i);
			String ruleName = rule.getAttribute("name");
			// Building Rule Components of a Rule
			NodeList componentNodes = rule.getElementsByTagName("ruleComponent");
			RuleComponent[] components = new RuleComponent[componentNodes.getLength()];
			for(int j=0; j<componentNodes.getLength(); j++)
			{
				Element component = (Element)componentNodes.item(j);
				// Building a Condition
				NodeList conditionNodes = component.getElementsByTagName("condition");
				String[] conditionIDs = new String[conditionNodes.getLength()];
				for(int k=0; k<conditionNodes.getLength(); k++)
				{
					Element condition = (Element)conditionNodes.item(k);
					int conditionID = Integer.parseInt(condition.getAttribute("name"));
					// Extract parameters for the Condition
					NodeList paramNodes = condition.getElementsByTagName("param");
					String[] params = new String[paramNodes.getLength()];
					for(int m=0; m<paramNodes.getLength(); m++)
					{
						Element param = (Element)paramNodes.item(m);
						String value = param.getFirstChild().getTextContent();
						params[m] = value;
					}
					// Add condition to the respository in rule manager
					String conKey = ruleM.addCondition(conditionID, params);
					// Construct the condition keys for building rule component
					conditionIDs[k] = conKey;
				}
				// Building an Action
				NodeList actionNodes = component.getElementsByTagName("action");
				String[] actionIDs = new String[actionNodes.getLength()];
				for(int n=0; n<actionNodes.getLength(); n++)
				{
					Element action = (Element)actionNodes.item(n);
					int actionID = Integer.parseInt(action.getAttribute("name"));
					// Extract parameters for the Action
					NodeList paramNodes = action.getElementsByTagName("param");
					String[] params = new String[paramNodes.getLength()];
					for(int p=0; p<paramNodes.getLength(); p++)
					{
						Element param = (Element)paramNodes.item(p);
						String value = param.getFirstChild().getTextContent();
						params[p] = value;
					}
					// Add action to the respository in rule manager
					String actKey = ruleM.addAction(actionID, params);
					// Construct the action keys for building rule component
					actionIDs[n] = actKey;
				}
				components[j] = ruleM.makeComponent(conditionIDs, actionIDs);
			}
			ruleM.addRule(ruleName, components);
		}
	}
	
	protected void buildObjects(Element root, ObjectManager objM)
	{
		NodeList objectAttributeTypes = root.getElementsByTagName("objectAttributeType");
		NodeList objectTypes = root.getElementsByTagName("objectType");
		NodeList objects = root.getElementsByTagName("object"); 
		
		// Build Common Object Attributes
		Messager.printMsg(this,"Start building common attributes...");
		for(int i=0; i<objectAttributeTypes.getLength(); i++)
		{
			Element oat = (Element)objectAttributeTypes.item(i);
			String oat_name = oat.getAttribute("name"); 
			String oat_type = oat.getAttribute("type");
			if(oat_type.equals("string"))
				Messager.printMsg(this,"Attribute created: "+objM.addAttributeType(oat_name, AttributeDataType.LITERAL));
			else if(oat_type.equals("number"))
				Messager.printMsg(this,"Attribute created: "+objM.addAttributeType(oat_name, AttributeDataType.NUMBER));
			Messager.printMsg(this,"Common Attribute[name:"+oat_name+"|type:"+oat_type+"]");
		}
		
		// Build Object Types
		Messager.printMsg(this,"Start building object types...");
		for(int i=0; i<objectTypes.getLength(); i++)
		{
			Element ot = (Element)objectTypes.item(i);
			String ot_name = ot.getAttribute("name");
			String ot_pre = ot.getAttribute("preMove");
			String ot_post = ot.getAttribute("postMove");
			// For common attribute types that associated with the object type
			NodeList objAttrNodes = ot.getElementsByTagName("objectAttribute");
			String[][] attrs = new String[objAttrNodes.getLength()][2];
			Messager.printMsg(this,"Object Type[name:"+ot_name+"|preM:"+ot_pre+"|postM:"+ot_post+"]");
			for(int j=0; j<objAttrNodes.getLength(); j++)
			{
				Element attr = (Element)objAttrNodes.item(j);
				attrs[j][0] = attr.getAttribute("type");
				attrs[j][1] = attr.getAttribute("value");
				Messager.printMsg(this,"Attributes[name:"+attrs[j][0]+"|value:"+attrs[j][1]+"]");
			}
			objM.addObjectType(ot_name, ot_pre, ot_post, attrs);
		}
		
		// Build Objects
		Messager.printMsg(this,"Start building objects...");
		for(int i=0; i<objects.getLength(); i++)
		{
			Element obj = (Element)objects.item(i);
			String obj_name = obj.getAttribute("name");
			String obj_type = obj.getAttribute("type");
			String obj_loc =  obj.getAttribute("location");
			String obj_owner = obj.getAttribute("owner");
			Messager.printMsg(this,"Object[name:"+obj_name+"|type:"+obj_type+"|location:"+obj_loc+"|owner:"+obj_owner+"]");
			objM.addObject(obj_name, obj_loc, obj_owner, obj_type);
		}
	}
	
	protected void buildPlayers(Element root, PlayerManager playerM)
	{
		//#PLAYER_"+(i+1)+"# Since Player number start from 1 instead of 0
		Messager.printMsg(this,"Start building players...");
		int playerNum = Integer.parseInt(root.getAttribute("playerNum"));
		for(int i=0; i<playerNum; i++)
			playerM.addPlayer("#PLAYER_"+(i+1)+"#");
		//[IMPORTANT]
		//I think no more need for this, as owner can just set to null
		//and have a mapping of rule parameters #PLAYER_NETURAL# to NULL is also OK. 
		//playerM.addPlayer("#PLAYER_NETURAL#");
	}
	
	protected void buildMap(Element root, MapManager mapM)
	{
		// Create Map
		Messager.printMsg(this,"Start building map...");
		Element 	map 			= (Element)root.getElementsByTagName("map").item(0);
		NodeList 	mapParamList 	= map.getElementsByTagName("param");
		NodeList 	regionTypes 	= root.getElementsByTagName("regionType");
		NodeList 	regions 		= root.getElementsByTagName("region");
		String 		mapType 		= map.getAttribute("type");		
		String[] 	mapParam 		= new String[mapParamList.getLength()];
		
		for(int i=0; i<mapParamList.getLength(); i++)
		{
			mapParam[i] = mapParamList.item(i).getFirstChild().getTextContent();
		}
		mapM.construct(mapType, mapParam);
		
		// Add Region Types
		Messager.printMsg(this,"Start building region types...");
		for(int i=0; i<regionTypes.getLength(); i++)
		{
			Element reg_t = (Element)regionTypes.item(i);
			String rt_name = reg_t.getAttribute("name");
			mapM.addRegionType(rt_name);
			Messager.printMsg(this,"Region Type[name:"+rt_name+"]");
		}
		
		// Add Regions
		Messager.printMsg(this,"Start building regions...");
		for(int i=0; i<regions.getLength(); i++)
		{
			Element reg = (Element)regions.item(i);
			String r_name = reg.getAttribute("name");
			String r_type = reg.getAttribute("type");
			String r_owner = reg.getAttribute("owner");
			mapM.addRegion(r_name, r_type, r_owner);
			Messager.printMsg(this,"Region[name:"+r_name+"|type:"+r_type+"|owner:"+r_owner+"]");
			NodeList tiles = reg.getElementsByTagName("tile");
			Region region = mapM.getRegion(r_name);
			String temp = "";
			for(int j=0; j<tiles.getLength(); j++)
			{
				String content = ((Element)tiles.item(j)).getAttribute("name");
				region.addTile(mapM.getTile(content));
				temp += (content+"|");
			}
			Messager.printMsg(this, "\t[tiles:"+temp+"]");
		}		
	}
	
	//static void trace(String msg){System.out.println(msg);}
	/**
<chess name="my chess" playerNum="2" turnCheck="catGameTurn">
  <map type="grid">
    <param>5</param>
    <param>5</param>
  </map>
  <regionType name="water"/>
  <regionType name="cave"/>
  <region name="p1cave" type="cave" owner="#PLAYER_1#">
    <tile name="r1c3"/>
  </region>
  <region name="p2cave" type="cave" owner="#PLAYER_2#">
    <tile name="r5c3"/>
  </region>
  <region name="river" type="water" owner="#PLAYER_NETURAL">
    <tile name="r3c2"/>
    <tile name="r3c3"/>
    <tile name="r3c4"/>
  </region>
  <objectAttributeType name="power" type="number"/>
  <objectType name="cat" preMove="catPreMove" postMove="catPostMove">
    <objectAttribute type="power" value="1"/>
  </objectType>
  <object type="cat" name="p1cat" location="r2c3"/>
  <object type="cat" name="p2cat" location="r4c3"/>
  <rule name="turnRule">
    <ruleComponent>
      <action name="5">
        <param>12345</param>
      </action>
      <action name="6"/>
    </ruleComponent>
  </rule>
  <rule name="catPreMove">
    <ruleComponent>
      <condition name="1">
        <param>p1cat</param>
        <param>p2cat</param>
      </condition>
      <condition name="2">
        <param>3</param>
      </condition>
      <action name="3">
        <param>r4c3</param>
      </action>
    </ruleComponent>
    <ruleComponent>
      <action name="5">
        <param>12345</param>
      </action>
    </ruleComponent>
  </rule>
  <rule name="catPostMove">
    <ruleComponent>
      <condition name="3">
        <param>cat</param>
      </condition>
      <condition name="5">
        <param>r3c3</param>
        <param>r4c4</param>
      </condition>
      <action name="1024">
        <param>objectA</param>
      </action>
    </ruleComponent>
  </rule>
</chess>
	 */
	
}
