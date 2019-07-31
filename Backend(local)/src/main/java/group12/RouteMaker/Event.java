package group12.RouteMaker;

import java.io.Serializable;
import java.util.Calendar;

public class Event implements Serializable {

    public String eventId;
    public String dayId;
    public String siteId;
    public String siteName;
    public double duration;
    public double latitude;
    public double longitude;
    public Integer startTime;
    public Integer endTime;

    
    
    public Event(String eventId, String dayId, String siteId, String siteName, double latitude, double longitude, Integer startTime, Integer endTime) {
        this.eventId = eventId;
        this.dayId = dayId;
        this.siteId = siteId;
        this.siteName = siteName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startTime = startTime;
        this.endTime = endTime;
        setDuration();
    }

    public void setDuration() {
        // check if endTime >= startTime, else print error
        int startHour = startTime / 100;
        int startMinute = (startHour * 60) + (startTime % 100);

        int endHour = endTime / 100;
        int endMinute = (endHour * 60) + (endTime % 100);

        this.duration = (endMinute - startMinute) / 60.0;
    }


}
