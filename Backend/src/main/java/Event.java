import java.io.Serializable;

public class Event implements Serializable {

	private static final long serialVersionUID = -7388596554094803630L;
	private String eventId;
	private String dayId;
	private String siteId;
	private String siteName;
	private double duration;
	private double latitude;
	private double longitude;
	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getDayId() {
		return dayId;
	}

	public void setDayId(String dayId) {
		this.dayId = dayId;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Integer getStartTime() {
		return startTime;
	}

	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}

	public Integer getEndTime() {
		return endTime;
	}

	public void setEndTime(Integer endTime) {
		this.endTime = endTime;
	}

	private Integer startTime;
    private Integer endTime;

    
    
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
