package webBaseChessGameDesigner.system;

public class Main {

	public static void main(String[] args)
	{
		ServerConfiguration.loadConfiguration("server_configuration.xml");
		RuleClassMapper.startRuleMapping();
		Server server = new Server();
		server.startServer();
	}

}
