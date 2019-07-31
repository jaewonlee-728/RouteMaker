package group12.RouteMaker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Trip {
	public String tripId;
	public String cityCode;
	public List<String> dayIds;
	public Calendar startDate;
	public Calendar endDate;

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
