package webBaseChessGameDesigner.system;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JTextArea;

import webBaseChessGameDesigner.system.state.ConnectionState;


public class Messager 
{
	protected static final Calendar cal = Calendar.getInstance();
	protected static JTextArea outArea;
	
	public static String getDateString()
	{	
		cal.setTime(new Date());

		String dateStr = cal.get(Calendar.YEAR)+":"+(cal.get(Calendar.MONTH)+1)+":"+cal.get(Calendar.DAY_OF_MONTH);
		String timeStr = cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);

		return "["+dateStr+"|"+timeStr+"]";
	}
	
	public static void printMsg(Connection conn, String msg)
	{	
		ConnectionState state = conn.getState();
		if(state == ConnectionState.INIT || state == ConnectionState.CLOSED)
			printMsg(getDateString()+"[Connection:"+conn.getConnID()+"]: "+msg);
		else
			printMsg(getDateString()+"[Connection:"+conn.getConnID()+":"+conn.getUsername()+"]: "+msg);
	}
	
	public static void printMsg(XMLLoader xmlLoader, String msg)
	{	
		printMsg(getDateString()+"[XMLLoader]: "+msg);
	}

	public static void printMsg(DatabaseManager dbm, String msg)
	{	
		printMsg(getDateString()+"[DatabaseManager]: "+msg);
	}
	
	public static void printMsg(Server server, String msg)
	{	
		printMsg(getDateString()+"[Server]: "+msg);
	}
	
	public static void printMsg(String sender, String msg)
	{	
		printMsg(getDateString()+"["+sender+"]: "+msg);
	}
	
	public static void printMsg(Object obj, String msg)
	{	
		printMsg(getDateString()+"["+obj.getClass().getSimpleName()+"]: "+msg);
	}
	
	public static void printMsg(String msg)
	{
		if(outArea != null)
		{
			outArea.append(msg+"\n");
			outArea.setCaretPosition(outArea.getText().length());
		}
		else
			System.out.println(msg);
	}
	
	public static void setOutPane(JTextArea pane)
	{
		outArea = pane;
	}
	
	//Just a handy function to construct an error response.
	public static String responseMsg(String type, String result)
	{
		return "<wbcgdp_response type='"+type+"'><result>"+result+"</result></wbcgdp_response>";
	}
	
	public static String errorMsg(String msg)
	{
		return "<wbcgdp_response type='error'><msg>"+msg+"</msg></wbcgdp_response>";
	}
	
	public static String infoMsg(String msg)
	{
		return "<wbcgdp_response type='info'><msg>"+msg+"</msg></wbcgdp_response>";
	}
}
