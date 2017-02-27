package webBaseChessGameDesigner.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.io.PrintWriter;
import java.io.StringReader;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class SpecificationReceiver extends HttpServlet 
{
	static final Calendar cal = Calendar.getInstance();
	static final long serialVersionUID = 0;
	static final String xmlHeader = "<?xml version='1.0' encoding='utf-8'?>"; 
	static String specStorageLoc = ""; 
	
	HttpSession session;
	
	// Whenever server init, locate the current filepath on server that for storing design specification
	public void init() throws ServletException
	{			
		ServerConfiguration.loadConfiguration(getInitParameter("serverConfigLoc"));
		specStorageLoc = ServerConfiguration.getStringParameter("WebappsLocation");
		specStorageLoc +=ServerConfiguration.getStringParameter("SpecificationStorageLocation");
		// Run if and only if location and file exists
		File fileDetector = new File(specStorageLoc);
		if(!(fileDetector.exists() && fileDetector.isDirectory()))
			throw new ServletException("Path to store chess design specification doesn't exist.");			
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		/**  Sample XML content from Flash client's HTTP request 
			<designInterfaceRequest user="ferry" password="subai">
			<chess name="Animal Chess Emulation" playerNum="2" turnCheck="catGameTurn">
			<map type="grid"><param>7</param><param>7</param></map>
			<regionType name="water" />
			<regionType name="cave" />
			<region name="p1cave" type="cave" owner="#PLAYER_1#">
				<tile name="R0C3" />
			</region>
			<region name="p2cave" type="cave" owner="#PLAYER_2#">
				<tile name="R6C3" />
			</region>
			<region name="river" type="water" owner="#PLAYER_NETURAL#">
				<tile name="R3C2" /><tile name="R3C3" /><tile name="R3C4" />
			</region>
			<objectAttributeType name="power" type="number" />
			<objectType name="mice" preMove="catPreMove" postMove="catPostMove">
				<objectAttribute type="power" value="1" />
			</objectType>
			<objectType name="cat" preMove="catPreMove" postMove="catPostMove">
				<objectAttribute type="power" value="2" />
			</objectType>
			<objectType name="dog" preMove="catPreMove" postMove="catPostMove">
				<objectAttribute type="power" value="3" />
			</objectType>
			<objectType name="fox" preMove="catPreMove" postMove="catPostMove">
				<objectAttribute type="power" value="4" />
			</objectType>
			<objectType name="wolf" preMove="catPreMove" postMove="catPostMove">
				<objectAttribute type="power" value="5" />
			</objectType>
			<objectType name="tiger" preMove="catPreMove" postMove="catPostMove">
				<objectAttribute type="power" value="6" />
			</objectType>
			<objectType name="lion" preMove="catPreMove" postMove="catPostMove">
				<objectAttribute type="power" value="7" />
			</objectType>
			<objectType name="elephant" preMove="catPreMove" postMove="catPostMove">
				<objectAttribute type="power" value="8" />
			</objectType>
			<object type="cat" name="p1cat" location="R1C0" owner="#PLAYER_1#" />
			<object type="dog" name="p1dog" location="R1C1" owner="#PLAYER_1#" />
			<object type="mice" name="p1mice" location="R1C2" owner="#PLAYER_1#" /
			><object type="tiger" name="p1tiger" location="R1C3" owner="#PLAYER_1#" />
			<object type="wolf" name="p1wolf" location="R1C4" owner="#PLAYER_1#" />
			<object type="fox" name="p1fox" location="R1C5" owner="#PLAYER_1#" />
			<object type="elephant" name="p1elephant" location="R1C6" owner="#PLAYER_1#" />
			<object type="lion" name="p1lion" location="R2C3" owner="#PLAYER_1#" />
			<object type="cat" name="p2cat" location="R5C0" owner="#PLAYER_2#" />
			<object type="dog" name="p2dog" location="R5C1" owner="#PLAYER_2#" />
			<object type="mice" name="p2mice" location="R5C2" owner="#PLAYER_2#" />
			<object type="tiger" name="p2tiger" location="R5C3" owner="#PLAYER_2#" />
			<object type="wolf" name="p2wolf" location="R5C4" owner="#PLAYER_2#" />
			<object type="fox" name="p2fox" location="R5C5" owner="#PLAYER_2#" />
			<object type="elephant" name="p2elephant" location="R5C6" owner="#PLAYER_2#" />
			<object type="lion" name="p2lion" location="R4C3" owner="#PLAYER_2#" />
			<rule name="catGameTurn">
				<ruleComponent>
					<condition name="1"><param>cave</param></condition>
					<action name="2" />
				</ruleComponent>
			</rule>
			<rule name="catPreMove">
				<ruleComponent>
					<action name="5" />
				</ruleComponent>
			</rule>
			<rule name="catPostMove">
				<ruleComponent>
					<action name="1" />
				</ruleComponent>
			</rule>
			</chess>
			<description>testing haha 1</description>
			</designInterfaceRequest>
		 */
		try
		{
			// Setup Servlet I/O
			BufferedReader br = req.getReader();
			PrintWriter pw = res.getWriter();
			
			// Get all content from request header
			String i_str;
			String p_str = "";
			while( (i_str = br.readLine()) != null ){	
				p_str += i_str;
			}
			
			try
			{
				// Extract required string value
				String fullDocumentStr = p_str;
				String specDocumentStr = p_str.substring(p_str.indexOf("<chess"),p_str.lastIndexOf("</chess>")+8);				
					
				// Create document objects from extracted strings
				XMLLoader xmlLoader = XMLLoader.getLoader();
				Document fullDoc = xmlLoader.getXMLDocumentFromString(fullDocumentStr);
				Document specDoc = xmlLoader.getXMLDocumentFromString(specDocumentStr);

				// Get username and password
				Element docElement = fullDoc.getDocumentElement();
				String userStr = docElement.getAttribute("user");
				String passStr = docElement.getAttribute("password");
				// Get chess description
				NodeList nList = docElement.getElementsByTagName("description");
				Element descNode = (Element)nList.item(0);
				String chessDescStr = descNode.getTextContent();
				
				// Get chess name
				NodeList nList2 = docElement.getElementsByTagName("chess");
				Element chessNode = (Element)nList2.item(0);
				String chessNameStr = chessNode.getAttribute("name");		

				// Process login
				DatabaseManager dbm = DatabaseManager.getManager();
				boolean loginSucc = dbm.validateLogin(userStr, passStr);
				
				if(loginSucc)
				{
					// If chess name already exist, no need to do further operation... 
					boolean isChessNameExist = dbm.checkChessNameExist(userStr, chessNameStr);
					if(!isChessNameExist)
					{
						// Validate input docuement, store it only if it is validated
						boolean validated = xmlLoader.validateXMLDocument(xmlLoader.getSpecificationValidator(), specDoc);
						if(validated)
						{
							try
							{
								// Construct an unique filename
								cal.setTime(new Date());
								String dateStr = cal.get(Calendar.YEAR)+"_"+(cal.get(Calendar.MONTH)+1)+"_"+cal.get(Calendar.DAY_OF_MONTH);
								String timeStr = cal.get(Calendar.HOUR_OF_DAY)+"_"+cal.get(Calendar.MINUTE)+"_"+cal.get(Calendar.SECOND);
								String fileName = userStr+"_"+dateStr+"_"+timeStr+".xml";
								
								// Code to store the design specification on server
								File file = new File(specStorageLoc+fileName);
								FileWriter fw = new FileWriter(file);
								PrintWriter pr = new PrintWriter(fw);
								pr.print(specDocumentStr);
								pr.close();
								fw.close();
								
								// Update database entry after file is created
								boolean dbUpdateSucc = dbm.addChessDefinition(userStr, chessNameStr, chessDescStr, fileName);
								if(dbUpdateSucc)
								{
									// Done.
									pw.print(makeOutputStr(true, ""));
									pw.close();
									return;
								}
							}
							catch(IOException ioe){ioe.printStackTrace();}	
						}
						else
						{
							// Document not validated
							pw.print(makeOutputStr(false, "Specification document is invalid."));
							pw.close();
							return;
						}
					}
					else
					{
						// Document not validated
						pw.print(makeOutputStr(false, "Chess name already exists."));
						pw.close();
						return;						
					}
				}
				else
				{
					pw.print(makeOutputStr(false, "Login failed."));
					pw.close();
					return;	
				}
			}
			catch(NullPointerException npe)
			{
				//Stack trace just for debug purpose.
				//npe.printStackTrace();
				//pw.print(makeOutputStr(false, "Full document is invalid. "+ makeStackTrace(npe)));
				pw.print(makeOutputStr(false, "Full document is invalid."));
				pw.close();
				return;			
			}
			catch(Exception e)
			{
				//Stack trace just for debug purpose.
				//e.printStackTrace();
				//pw.print(makeOutputStr(false, "Full document is invalid. "+ makeStackTrace(e)));
				pw.print(makeOutputStr(false, "Full document is invalid."));
				pw.close();
				return;		
			}
		}
		catch(IOException ioe)
		{
			// Maybe getWriter() or getReader() throws IOException
			ioe.printStackTrace();
			return;	
		}

	}
	
	public String makeStackTrace(Exception e)
	{
		StackTraceElement[] ste = e.getStackTrace();
		String errMsg = e.getMessage()+"\n";
		for(int i=0; i<ste.length; i++)
		{
			String mstr = ste[i].getMethodName();
			String cstr = ste[i].getClassName();
			int lnum = ste[i].getLineNumber();
			String fstr = ste[i].getFileName();
			errMsg += (cstr+"."+mstr+"("+fstr+":"+lnum+")\n");
		}
		return errMsg;
	}
	
	public String makeOutputStr(boolean succ, String msg)
	{
		String outputStr = xmlHeader+"<result>";
		outputStr += "<status>"+succ+"</status>";
		if(!succ)
			outputStr += "<message>"+msg+"</message>";
		outputStr+="</result>";
		return outputStr;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		doPost(req,res);
	}
	
	public void branchTask(HttpServletRequest req, HttpServletResponse res) throws Exception
	{
		/*
		BufferedReader br = req.getReader();
		PrintWriter pw = res.getWriter();
		pw.println("req.getContentLength():"+req.getContentLength());
		pw.println("req.getContentType():"+req.getContentType());
		pw.println("req.getContextPath():"+req.getContextPath());
		pw.println("req.getAuthType():"+req.getAuthType());
		
		pw.println("character encoding:"+req.getCharacterEncoding());
		pw.println("input:");
		String i_str;
		while( (i_str = br.readLine()) != null )
			pw.println(i_str);
		
		Enumeration e = req.getParameterNames();
		String p_str = "";
		String t_str = ""; 
		
		while(e.hasMoreElements())
		{
			t_str = e.nextElement().toString();
			p_str += "\n"+ t_str;
			p_str += "\t"+req.getParameter(t_str);
		}
		pw.println("Parameters:\n"+p_str+"\n");
		
		Enumeration e2 = req.getAttributeNames();
		String p_str2 = "";
		String t_str2 = ""; 
		
		while(e2.hasMoreElements())
		{
			t_str2 = e2.nextElement().toString();
			p_str2 += "\n"+ t_str2;
			p_str2 += "\t"+req.getParameter(t_str2);
		}
		pw.println("Attributes:\n"+p_str2+"\n");
		*/
		testXML2(req,res);
		/*
		if(req.getParameter("cmd") == null)
		{
			
			handleBadRequest(pw,1);
		}
		else
		{
			int cmd = Integer.parseInt(req.getParameter("cmd"));
			switch(cmd)
			{
				case 0: processLogin(req,res); break; 
				
				default: handleBadRequest(pw,2); break;
			}
		}*/
	}
	
	
	public void processLogin(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			String user = req.getParameter("user");
			String pass = req.getParameter("pass");
						
			DatabaseManager dbm = DatabaseManager.getManager();
			boolean success = dbm.validateLogin(user, pass);
			dbm.closeDBC();
			
			PrintWriter pw = res.getWriter();
			//res.setContentType("application/x-www-form-urlencoded");
			
			if(success)
			{
				pw.println("resultx=true&xx=3");
			}
			else
				pw.println("resultx=false&xx=str");
		}
		catch(IOException ioe){ ioe.printStackTrace(); }
	}
	
	public void handleBadRequest(PrintWriter pw, int type)
	{
		try
		{
			switch(type)
			{
				case 1: pw.println("Bad Request Format"); break;
				case 2: pw.println("No Such Command"); break;
				default: pw.println("Unknown Bad Request Error"); break;
			}
		}
		catch(Exception e){e.printStackTrace();}
	}
	
	public void storeChessDesignSpec(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		/**
		 * Once login check OK
		 * Need to check inputted stuff is vaild XML or not
		 */
		
		session = req.getSession();
		session.setMaxInactiveInterval(60*30); // Set session expire in 30 mins
		//session
		
		Enumeration e = req.getParameterNames();
		String p_str = "";
		String t_str = ""; 
		
		while(e.hasMoreElements())
		{
			t_str = e.nextElement().toString();
			p_str += "\n"+ t_str;
			p_str += "\t"+req.getParameter(t_str);
		}
		
		/**
		 * If vaild, start parsing code here?
		 */
		
		try
		{
			String loc = specStorageLoc;
			
			File fileDetector = new File(loc);
			
			if(fileDetector.exists() && fileDetector.isDirectory())
			{
				// Code to store the design specification on server
				// If and only if location and file exists
				File file = new File(loc+"test.xml");
				FileWriter fw = new FileWriter(file);
				PrintWriter pr = new PrintWriter(fw);
				pr.print(p_str);
				pr.close();
				fw.close();
				
				// Output back a XML response to Flash client
				// Should use JAXP to create real XML object to pass back
				res.setContentType("text/xml");
				PrintWriter pw = res.getWriter();
				//pw.print("result=");
				pw.print("<?xml version='1.0' encoding='utf-8'?>");
				pw.print("<result>true</result>");
				pw.close();
			}
			else
			{
				throw new ServletException("Path to store chess design specification doesn't exist.");
			}
		}
		catch(IOException ioe){ioe.printStackTrace();}		
	}
	
	public void testXML(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		try
		{
			PrintWriter pw = res.getWriter();
			pw.println("shit!");
			try
			{
				// Check file exist
				File file = new File(specStorageLoc+"test2.xml");
				if(!file.exists()){throw new ServletException("file not found");}
				
				// Start parse XML
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(file);

				// Get the root element of the inputted XML.
				Element root = doc.getDocumentElement();
				
				pw.println("Doc element name:"+root.getNodeName());
				NodeList nodeList = root.getChildNodes();
				for (int i=0; i<nodeList.getLength(); i++)
				{
					Node node = nodeList.item(i);
					if(node.getNodeName() == "bar1")
					{
						NamedNodeMap map = node.getAttributes();
						Node node_tmp = map.getNamedItem("name");
						pw.println("bar1 attribute [name]:"+node_tmp.getNodeValue());
					}
					pw.println("other nodes name:"+node.getNodeName());
				}
			}
			catch(Exception e){throw new ServletException(e.getMessage());}
			/*
			catch(ParserConfigurationException pce){pce.printStackTrace();}
			catch(SAXException saxe){saxe.printStackTrace();}
			catch(IOException ioe){ioe.printStackTrace();}		
			*/
		}
		catch (IOException ioe){ioe.printStackTrace();}
	}
	

	
	public void testXML3(HttpServletRequest req, HttpServletResponse res) throws Exception
	{
		String xmlDoc = req.getParameter("xml");
		// Start parse XML
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		// IMPORTANT! This is the way how Flash communicate with Servlet by XML! 
		Document doc = builder.parse(new InputSource(new StringReader(xmlDoc)));
		Element root = doc.getDocumentElement();
		traverseXML(root, res.getWriter(), "");
		
	}
	
	public void testXML2(HttpServletRequest req, HttpServletResponse res)
	{
		try{		
			
			// Setup reader and writer for I/O
			PrintWriter pw = res.getWriter();
			BufferedReader br = req.getReader();
			
			// Start parse XML
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			// IMPORTANT! This is the way how Flash communicate with Servlet by XML! 
			Document doc = builder.parse(new InputSource(br));

			// Get the root element of the inputted XML.
			Element root = doc.getDocumentElement();
		
			traverseXML(root, pw, "");
			
			pw.close();
			}
		
			catch(Exception e)
			{
				try
				{
					res.getWriter().println("Bad Request!");
					e.printStackTrace(res.getWriter());
				}
				catch(Exception ee){ee.printStackTrace();}
			}
	}
	// For debug purpose
	public void traverseXML(Node node, PrintWriter pw, String indent)
	{
		switch(node.getNodeType())
		{
			case Node.ELEMENT_NODE:
				pw.print(indent+"<"+node.getNodeName()+" ");
				if(node.hasAttributes())
				{
					NamedNodeMap attributes = node.getAttributes();	
					for(int i=0; i<attributes.getLength(); i++)
					{
						Node attr = attributes.item(i);
						pw.print(attr.getNodeName()+"="+attr.getNodeValue()+" ");
					}
				}
				if(node.hasChildNodes())
				{
					pw.println(">");
					NodeList nodes = node.getChildNodes();
					for(int i=0; i<nodes.getLength(); i++)
					{
						traverseXML(nodes.item(i),pw,indent+"\t");
					}
					pw.println(indent+"</"+node.getNodeName()+">");
				}
				else
				{
					pw.println("/>");
				}
				break;
			case Node.TEXT_NODE:
				pw.print(node.getTextContent());
				break;
			default:
				break;
		}
	}

}
