package webBaseChessGameDesigner.system;

/**
 * COMP315 Homework 1: Enhanced Chat Client
 * Lecturer:	Louis Sham
 * Student: 	Ferry To Wai Lun
 * Class: 		BScIMT 42077 Year4
 * Date:		2006/09/23
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class WBCDClient 
{
	public static int port = 3333;
	public static void main(String[] args) throws IOException 
	{
		String nickname = null;
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		//If user not inputted two parameters, system will not run.
		if(args.length != 2)
		{
			System.out.println("Two parameters are required.");
			System.exit(1);
		}
		else
		{
			//Getting the nickname from command line arguments
			nickname = args[1];
		}
		
		try 
		{
			socket = new Socket(args[0], port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} 
		catch (UnknownHostException e) 
		{
			System.out.println("Don't know about host.");
			e.printStackTrace();
			System.exit(1);
		}
		catch (IOException e) 
		{
			System.out.println("Couldn't get I/O");
			e.printStackTrace();
			System.exit(1);
		}

		Thread t = new Thread(new MessageReceiver(in));
		t.start();

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		String userInput;
		
		//Heres comes the client send out its nickname
		out.println(nickname);
		
		while ((userInput = stdIn.readLine()) != null) 
		{
			if(userInput.startsWith("/whisper"))
			{
				userInput = "__WHISPER__"+ userInput.substring(8);
			}
			out.println(userInput);
		}

		out.close();
		in.close();
		stdIn.close();
		socket.close();
	}
}

class MessageReceiver implements Runnable 
{
	BufferedReader in = null;
	public MessageReceiver(BufferedReader br)
	{
		in = br;
	}
	
	public void run()
	{
		try 
		{
			String message;
			while (true) 
			{
				message = in.readLine();
				if (message == null) break;
				System.out.println(message);
			}
			System.exit(1);
		} 
		catch (Exception e)
		{
			//Handle the case of disconnected from server			
			System.out.println("Disconnected from server.");
			System.exit(1);
		}
	}
}
