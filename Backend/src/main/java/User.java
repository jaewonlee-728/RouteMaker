import java.util.ArrayList;
import java.util.List;
 
public class User {
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private String ageGroup;
	
	private String currentlyEditingTripId;
	private List<String> preferenceList;
	private List<String> currentTripIds;
	private List<String> pastTripIds;
	
    public User() {
        initialize();
    }
    
    public String getCurrentlyEditingTripId() {
		return currentlyEditingTripId;
	}

	public List<String> getCurrentTripIds() {
		return currentTripIds;
	}

	public List<String> getPastTripIds() {
		return pastTripIds;
	}

	public void setPreferenceList(List<String> preferenceList) {
		this.preferenceList = preferenceList;
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
    
    public List<String> getPreferenceList() {
    	return this.preferenceList;
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
    public void setpreferenceList(List<String> list) {
        this.preferenceList = list;
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