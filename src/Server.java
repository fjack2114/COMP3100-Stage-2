public class Server {

	String serverType;
	String serverID;
	String status;
	int startTime;
	int cores;
	int memory;
	int disk;
	int jobsWaiting;
	int jobsRunning;

	public Server(String serverType, String serverID, String status, int startTime, int cores, int memory, int disk, int jobsWaiting, int jobsRunning) {
		this.serverType = serverType;
		this.serverID = serverID;
		this.status = status;
		this.startTime = startTime;
		this.cores = cores;
		this.memory = memory;
		this.disk = disk;
		this.jobsWaiting = jobsWaiting;
		this.jobsRunning = jobsRunning;
	}

    public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public String getServerID() {
		return serverID;
	}

	public void setServerID(String serverID) {
		this.serverID = serverID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getCores() {
		return cores;
	}

	public void setCores(int cores) {
		this.cores = cores;
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public int getDisk() {
		return disk;
	}

	public void setDisk(int disk) {
		this.disk = disk;
	}

	public int getJobsWaiting() {
		return jobsWaiting;
	}

	public void setJobsWaiting(int jobsWaiting) {
		this.jobsWaiting = jobsWaiting;
	}

	public int getJobsRunning() {
		return jobsRunning;
	}

	public void setJobsRunning(int jobsRunning) {
		this.jobsRunning = jobsRunning;
	}
}
