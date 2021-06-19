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
    private static final String HELO = "HELO" + NEWLINE;
    private static final String AUTH = "AUTH " + System.getProperty("user.name") + NEWLINE;
    private static final String REDY = "REDY" + NEWLINE;
    private static final String NONE = "NONE" + NEWLINE;
    private static final String QUIT = "QUIT" + NEWLINE;
    private static final String OK = "OK" + NEWLINE;
    private static final String JOBN = "JOBN";
    private static final String JCPL = "JCPL";
    private static final String DATA = "DATA";
    private static final String SCHD = "SCHD ";
    private static ArrayList<Server> serverInformation = new ArrayList<>();
    private static ArrayList<StaticServerList> setServerInformation = new ArrayList<>();
    private String incomingMessage = inMessage();
    private int numServers = 0;
    private int jobCores = 0;
    private int jobMemory = 0;
    private int jobDisk = 0;
    private String jobID = EMPTYSTRING;

    public static void main(String[] args) throws IOException {
        Client client = new Client(ADDRESS, PORT);
        client.eventLoop();
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

        // XML cases parses the static server information provided by the ds-system.xml file that is created
        // when a configuration file is run
        setServerInformation = XML.parse("ds-system.xml");

        // Event loop to handle incoming jobs
        while (!incomingMessage.contains(NONE)) {
            if (incomingMessage.contains(JOBN)) {
                String[] splitStr = incomingMessage.split(PARSEWHITESPACE);
                jobID = splitStr[2];
                outMessage(getsAvailable(incomingMessage));
            }
            // The Server sends the dynamic Server information after the GETS Avail command.  After the Server sends
            // "DATA" the dynamic server information is contained delimited by a newline. This "if" statement parses
            // the number of severs that are currently available and then the "for" statement sends the details of the
			// servers to the parseServerInfo method
            if (incomingMessage.contains(DATA)) {
                String[] splitData = incomingMessage.split(PARSEWHITESPACE);
                numServers = Integer.parseInt(splitData[1]);
                outMessage(OK);

                for (int i = 0; i < numServers; i++) {
                    incomingMessage = inMessage();
                    String[] splitServers = incomingMessage.split(PARSEWHITESPACE);
                    parseServerInfo(splitServers);

                }
                outMessage(OK);
            }
            if (incomingMessage.contains(OK)) {
                outMessage(REDY);
            }
            if (incomingMessage.contains(JCPL)) {
                outMessage(REDY);
            }
            // A period is the message that is send after the server information has been relayed. Server is
            // now waiting on a job command to be sent
            if (incomingMessage.contains(".")) {
                // If there at least one dynamic server it will try the algorithm so see if the job fits if not it
                // will default to the static server list
                if (numServers > 0) {
                    outMessage(terminateServers());
                    outMessage(closeFit(jobID));
                }
                // If GETS Avail returns no servers then fall back on scheduling with the static server list
                else if (numServers == 0) {
                    outMessage(processJob(jobID));
                }
                // Clears dynamic server information after a scheduling decision has been made
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

    // Method establishes a connection between the Client and Server
    private static void connect(String address, int port) {
        try {
            System.out.println("Connecting to Server...");
            socket = new Socket(address, port);
            System.out.println("Connection Established");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method that assigned the input and output for the socket
    public Client(String address, int port) throws IOException {
        connect(address, port);
        outStream = new DataOutputStream(socket.getOutputStream());
        inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // Handles the messages coming from the Server to the Client
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

    // Method for sending messages from the Client to the Server
    private void outMessage(String str) throws IOException {
        // Client to Server messages
        byte[] byteMessage = str.getBytes();
        outStream.write(byteMessage);

        // Print Client output to screen
//		System.out.print("Client: " + str);
    }

    // Algorithm used for scheduling jobs, can be modified to lower turnaround time if the comparison between
    // serverInformation.get(h).cores - jobCores <= "number" if number increases
    // algorithm can also be modified to lower cost if number decreases
    private String closeFit(String jobID) {
        for (int h = 0; h < serverInformation.size(); h++) {
            if (serverInformation.get(h).cores >= jobCores && serverInformation.get(h).cores - jobCores <= 7 &&
                    serverInformation.get(h).memory >= jobMemory && serverInformation.get(h).disk >= jobDisk) {
                return SCHD + jobID + WHITESPACE + serverInformation.get(h).serverType +
                        WHITESPACE + serverInformation.get(h).serverID + NEWLINE;
            }
        }
        return processJob(jobID);
    }

    // This method is used when there are no "available" servers to process the current job, the method iterates
    // through and selects a server large enough to handle the current job and the serverID is a randomized number of
    // possible servers to balance the distribution of jobs
    private String processJob(String jobID) {
        for (int i = 0; i < setServerInformation.size(); i++) {
            if (setServerInformation.get(i).cores >= jobCores && setServerInformation.get(i).memory > jobMemory &&
                    setServerInformation.get(i).disk > jobDisk) {
                return SCHD + jobID + WHITESPACE + setServerInformation.get(i).serverType + WHITESPACE +
                        ((int) (Math.random() * (setServerInformation.get(i).limit))) + NEWLINE;
            }
        }
        return EMPTYSTRING;
    }

    // Iterates through the current GETS Avail servers and sends a TERM command for any servers that are idle
    private String terminateServers() {
        for (int i = 1; i < numServers; i++) {
            if (serverInformation.get(i).status.equals("idle")) {
                return "TERM " + serverInformation.get(i).serverType + WHITESPACE + serverInformation.get(i).serverID + NEWLINE;
            }
        }
        return EMPTYSTRING;
    }

    // Sends command to retrieve servers that are available to schedule the current job to
    private String getsAvailable(String job) {
        String[] splitStr = job.split(PARSEWHITESPACE);
        jobCores = Integer.parseInt(splitStr[4]);
        jobMemory = Integer.parseInt(splitStr[5]);
        jobDisk = Integer.parseInt(splitStr[6]);
        return "GETS Avail " + jobCores + WHITESPACE + jobMemory + WHITESPACE + jobDisk + NEWLINE;
    }

    // Parses the data about the current GETS Avail servers into an arraylist to be used by the algorithm
    private void parseServerInfo(String[] splitServers) {
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
