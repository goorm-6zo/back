package goorm.back.zo6.common.event;

public abstract class Event {
    private long timestamp;

    public Event(){
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimeStamp(){
        return timestamp;
    }
}
