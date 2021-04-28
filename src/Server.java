public class Server {

		String type; 
		int limit; 
		int bootTime;
		float hourlyRate; 
		int coreCount; 
		int memory; 
		int disk;

		public Server(String type, int limit, int bootTime, float hourlyRate, int coreCount, int memory, int disk){
			this.type = type;
			this.limit = limit;
			this.bootTime = bootTime;
            this.hourlyRate = hourlyRate;
			this.coreCount = coreCount;
			this.memory = memory;
			this.disk = disk;
		}

		public String getType() {
			return this.type;
		}

		public int getCores() {
			return this.coreCount;
		}
}
