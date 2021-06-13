import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
	private static Socket socket = null;
	private final DataOutputStream outStream;
	private final BufferedReader inStream;
	private static final String ADDRESS = "127.0.0.1";
	private static final int PORT = 50000;
	private static final String PARSEWHITESPACE = "\\s+";
	private static final String NEWLINE = "\n";
	private static final String WHITESPACE = " ";
	private static final String EMPTYSTRING = "";
	private static final String HELO = "HELO"+NEWLINE;
	private static final String AUTH = "AUTH " + System.getProperty("user.name") + NEWLINE;
	private static final String REDY = "REDY" + NEWLINE;
	private static final String NONE = "NONE" + NEWLINE;
	private static final String QUIT = "QUIT" + NEWLINE;
	private static final String OK = "OK" + NEWLINE;
	private static final String JOBN = "JOBN";
	private static final String JCPL = "JCPL";
	private static final String DATA = "DATA";
	private static ArrayList<Server> serverInformation = new ArrayList<>();
	private static ArrayList<StaticServerList> setServerInformation = new ArrayList<>();
	private String incomingMessage = inMessage();
	private int jobCores = 0;
	private int jobMemory = 0;
	private int jobDisk = 0;
	private String jobID = EMPTYSTRING;

	public static void main(String[] args) throws IOException {
		Client client = new Client(ADDRESS, PORT);
		client.eventLoop();
	}

	private static void connect(String address, int port) {
		try {
			System.out.println("Connecting to server...");
			socket = new Socket(address, port);
			System.out.println("Connection Established");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Client(String address, int port) throws IOException {
		connect(address, port);
		outStream = new DataOutputStream(socket.getOutputStream());
		inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	private void eventLoop() throws IOException {

		// "HELO" to initiate connection
		outMessage(HELO);
		inMessage();

		// Authentication information
		outMessage(AUTH);
		inMessage();

		// Confirm to Server that Client is ready
		outMessage(REDY);

		// If there are no jobs, then quit
		if (incomingMessage.contains(NONE)) {
			outMessage(QUIT);
		}

		// Event loop to handle incoming jobs
		while (!incomingMessage.contains(NONE)) {
			if (incomingMessage.contains(JOBN)) {
				String[] splitStr = incomingMessage.split(PARSEWHITESPACE);
				jobID = splitStr[2];
				outMessage(getsCapable(incomingMessage));
			}
			if (incomingMessage.contains(DATA)) {
				String[] splitData = incomingMessage.split(PARSEWHITESPACE);
				int numServers = Integer.parseInt(splitData[1]);
				outMessage(OK);
				incomingMessage = inMessage();
				String[] splitServers = incomingMessage.split(PARSEWHITESPACE);
				parseServerInfo(numServers, splitServers);
				outMessage(OK);
			}
			if (incomingMessage.contains(OK)) {
				outMessage(REDY);
			}
			if (incomingMessage.contains(JCPL)) {
				outMessage(REDY);
			}
			if (incomingMessage.contains(".")) {
				outMessage(onlyFit(jobID, setServerInformation));
				serverInformation.clear();
			}
			incomingMessage = inMessage();
		}

		// If all jobs are completed sent quit to Server
		outMessage(QUIT);

			if (inMessage().contains(QUIT)) {
				outStream.close();
				socket.close();
			}
		System.exit(1);
	}

	private void outMessage(String str) throws IOException {
		// Client to Server messages
		byte[] byteMessage = str.getBytes();
			outStream.write(byteMessage);

		// Print Client output to screen
//		System.out.print("Client: " + str);
	}

	private String inMessage() throws IOException {
		// Server to Client messages
		String str = EMPTYSTRING;
			if (inStream != null) {
				str = inStream.readLine() + NEWLINE;
			}

		// Print Server input to screen
//		System.out.print("Server: " + str);

		return str;
	}

	private String onlyFit(String jobID, ArrayList<StaticServerList> staticServerInformation) {
		StaticServerList server;
		Server available = serverInformation.get(0);
		for(int i = 0; i < staticServerInformation.size(); i++) {
			if(jobCores == staticServerInformation.get(i).cores && staticServerInformation.get(i).memory > jobMemory) {
				server = staticServerInformation.get(i);
				return "SCHD " + jobID + WHITESPACE + server.serverType + WHITESPACE + 0 + NEWLINE;
			}
		}
		return "SCHD " + jobID + WHITESPACE + available.serverType + WHITESPACE + available.serverID + NEWLINE;
	}

	private String getsCapable(String job) {
		String[] splitStr = job.split(PARSEWHITESPACE);
		jobCores = Integer.parseInt(splitStr[4]);
		jobMemory = Integer.parseInt(splitStr[5]);
		jobDisk = Integer.parseInt(splitStr[6]);
		return "GETS Capable " + jobCores + WHITESPACE + jobMemory + WHITESPACE + jobDisk + NEWLINE;
	}

	private void parseServerInfo(int numServers, String[] splitServers) {
		for(int i = 0; i < numServers; i++) {
			String serverType = splitServers[0];
			String serverID = splitServers[1];
			String status = splitServers[2];
			int startTime = Integer.parseInt(splitServers[3]);
			int cores = Integer.parseInt(splitServers[4]);
			int memory = Integer.parseInt(splitServers[5]);
			int disk = Integer.parseInt(splitServers[6]);
			int jobsWaiting = Integer.parseInt(splitServers[7]);
			int jobsRunning = Integer.parseInt(splitServers[8]);

			Server serverData = new Server(serverType, serverID, status, startTime, cores, memory, disk, jobsWaiting, jobsRunning);
			serverInformation.add(serverData);
		}
	}
	
}
