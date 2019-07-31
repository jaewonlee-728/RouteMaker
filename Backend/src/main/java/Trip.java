import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Trip {
	private String tripId;
	private String cityCode;
	private List<String> dayIds;
	private Calendar startDate;
	private Calendar endDate;

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public void setDayIds(List<String> dayIds) {
		this.dayIds = dayIds;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public Trip(String tripId, String cityCode, List<String> dayIds, String startDate, String endDate) throws ParseException {
		this.tripId = tripId;
		this.cityCode = cityCode;
		this.dayIds = dayIds;
		this.startDate = changeDateFormat(startDate);
		this.endDate = changeDateFormat(endDate);
	}
	
	public String getTripId() {
		return this.tripId;
	}
	
	public String getCityCode() {
		return this.cityCode;
	}
	
	public List<String> getDayIds() {
		return this.dayIds;
	}
	
	public Calendar getStartDate() {
		return this.startDate;
	}
	
	public Calendar getEndDate() {
		return this.endDate;
	}
	
	public static Calendar changeDateFormat(String date) throws ParseException {
		DateFormat d = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(d.parse(date));
		return cal;  
	}
}
