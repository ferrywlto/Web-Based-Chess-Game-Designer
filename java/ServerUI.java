import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

import webBaseChessGameDesigner.system.Messager;
import webBaseChessGameDesigner.system.RuleClassMapper;
import webBaseChessGameDesigner.system.Server;
import webBaseChessGameDesigner.system.ServerConfiguration;


public class ServerUI extends JFrame
{

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		ServerUI serverUI = new ServerUI();
		
	}
	Container container;
	JPanel upperPanel;
	JPanel btnPanel;
	JPanel lowerPanel;
	
	JLabel fileLabel;
	JLabel logLabel;
	JTextField fileText;
	JButton fileBtn;
	JButton startBtn;
	JScrollPane logPane;
	JTextArea logArea;
	JButton clearBtn;
	JButton closeBtn;
	
	Server server;
	
	public ServerUI()
	{
		super("Web-based Chess Game Designer - Chess Game Server");
		initializeComponents();
	}
	
	protected void initializeComponents()
	{
		container = getContentPane();
		upperPanel = new JPanel(new GridLayout(4,1));
		btnPanel = new JPanel(new GridLayout(1,2));
		lowerPanel = new JPanel(new GridLayout(1,2));
		
		fileLabel = new JLabel("Server Configuration File:");
		logLabel = new JLabel("Server Log:");
		fileText = new JTextField();
		fileBtn = new JButton("Browse...");
		startBtn = new JButton("Start Server");
		clearBtn = new JButton("Clear Log");
		logArea = new JTextArea();
		logPane = new JScrollPane(logArea);

		closeBtn = new JButton("Close Server");
		
		final JFrame self = this;
		
		fileBtn.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event)
					{
						JFileChooser fileDlg = new JFileChooser();
						fileDlg.setAcceptAllFileFilterUsed(false);
						fileDlg.setMultiSelectionEnabled(false);
						fileDlg.setDialogTitle("Select server configuration file:");						
						fileDlg.addChoosableFileFilter(new FileFilter()
						{
							public boolean accept(File file)
							{
								if(file.isDirectory())
									return true;
								else if(file.isFile() && file.getName().endsWith(".xml"))
									return true;
								return false;
							}
							public String getDescription()
							{
								return "Server Configuration (.xml)";
							}
						});
						fileDlg.showDialog(self,"Select");
						
						String filename = fileDlg.getName(fileDlg.getSelectedFile());
						String pathname = fileDlg.getCurrentDirectory().getAbsolutePath();
						fileText.setText(pathname+"\\"+filename);
					}
				}
			);

		clearBtn.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						logArea.setText("");
					}
				}
		);
		
		closeBtn.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						if(server != null)
						{
							server.closeServer();
							fileText.setEnabled(true);
							fileBtn.setEnabled(true);
							startBtn.setEnabled(true);
							
							closeBtn.setEnabled(false);
						}
					}
				}
		);
		
		startBtn.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						final File file = new File(fileText.getText());
						if((file.isFile() && file.canRead() && file.exists()))
						{
							logArea.setText("");
							
							fileText.setEnabled(false);
							fileBtn.setEnabled(false);
							startBtn.setEnabled(false);
							
							closeBtn.setEnabled(true);
							
							Thread thread = new Thread(
									new Runnable(){
										public void run()
										{
											Messager.setOutPane(logArea);
											ServerConfiguration.loadConfiguration(file.getAbsolutePath());
											RuleClassMapper.startRuleMapping();
											server = Server.getInstance();
											server.startServer();	
										}
									}
							);
							thread.start();
						}
						else
							return;						
					}
				}
		);
		logArea.setEditable(false);
		logPane.setAutoscrolls(true);
		
		upperPanel.add(fileLabel);
		upperPanel.add(fileText);
		upperPanel.add(btnPanel);
		upperPanel.add(logLabel);
		btnPanel.add(fileBtn);
		btnPanel.add(startBtn);
		lowerPanel.add(clearBtn);
		lowerPanel.add(closeBtn);
		
		container.add(upperPanel, BorderLayout.NORTH);
		container.add(logPane,BorderLayout.CENTER);
		container.add(lowerPanel,BorderLayout.SOUTH);
		
		fileText.setEnabled(true);
		fileBtn.setEnabled(true);
		startBtn.setEnabled(true);
		
		closeBtn.setEnabled(false);
		
		setSize(800,600);
		setLocation(200, 150);
		
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
