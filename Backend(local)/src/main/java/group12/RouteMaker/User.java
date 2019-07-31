package group12.RouteMaker;

import java.util.ArrayList;
import java.util.List;
 
public class User {
	public String email;
	public String password;
	public String firstName;
	public String lastName;
	public String ageGroup;
	
	public String currentlyEditingTripId;
	public List<String> preferenceList;
	public List<String> currentTripIds;
	public List<String> pastTripIds;
	
	public User() {
        initialize();
    }
    
    public User(String email, String firstName, String lastName, String password, String ageGroup, List<String> preferenceList) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.ageGroup = ageGroup;
        this.preferenceList = preferenceList;
    }

    public void initialize() {
        this.currentTripIds = new ArrayList<>();
        this.pastTripIds = new ArrayList<>();
    }
    
    public String getEmail() {
    	return this.email;
    }

    public String getFirstName() {
    	return this.firstName;
    }
    
    public String getLastName() {
    	return this.lastName;
    }
    
    public String getPassword() {
    	return this.password;
    }
    
    public String getAgeGroup() {
    	return this.ageGroup;
    }

    public String getCurrentlyEditingTripId() {
		return currentlyEditingTripId;
	}
   
    public List<String> getPreferenceList() {
    	return this.preferenceList;
    }
    
    public List<String> getCurrentTripIds() {
    	return this.currentTripIds;
    }
    
    public List<String> getPastTripIds() {
    	return this.pastTripIds;
    }   
       
    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

	public void setPreferenceList(List<String> preferenceList) {
		this.preferenceList = preferenceList;
	}

    public void addCurrentTrip(String tripId) {
        this.currentTripIds.add(tripId);
    }
    
    public void setCurrentTripIds(List<String> currIds) {
    	this.currentTripIds = currIds;
    }
    
    public void setPastTripIds(List<String> pastIds) {
    	this.pastTripIds = pastIds;
    }

    public void setCurrentlyEditingTripId(String currentlyEditingTripId) {
        this.currentlyEditingTripId = currentlyEditingTripId;
    }

    public void resetCurrentlyEditingTripId(){
        this.currentlyEditingTripId = "";
    }

    public void moveCurrTrip(String tripId) {
        boolean ret = this.currentTripIds.remove(tripId);
        System.out.println(ret);
        ret = this.pastTripIds.add(tripId);
        System.out.println(ret);
    }
    
    public String preferenceToString() {
    	String ret = "{";
    	for (String s : this.preferenceList) {
    		ret += s + " ";
    	}
    	ret = ret.trim();
    	ret += "}";
    	ret = ret.replaceAll(" ", ","); 
    	return ret;
    }

}