public class StaticServerList {

    String serverType;
    int cores;
    int memory;
    int disk;
    int limit;
    int bootupTime;
    float hourlyRate;

    public StaticServerList(String serverType, int limit, int bootupTime, float hourlyRate, int cores, int memory, int disk) {
        this.serverType = serverType;
        this.limit = limit;
        this.bootupTime = bootupTime;
        this.hourlyRate = hourlyRate;
        this.cores = cores;
        this.memory = memory;
        this.disk = disk;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getBootupTime() {
        return bootupTime;
    }

    public void setBootupTime(int bootupTime) {
        this.bootupTime = bootupTime;
    }

    public float getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(float hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}
