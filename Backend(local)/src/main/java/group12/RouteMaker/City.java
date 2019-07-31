package group12.RouteMaker;

public class City {

    public String cityCode;
    public String cityName;
    public String state;
    public double latitude; //default place to place the camera on in the Google Maps
    public double longitude; //default place to place the camera on in the Google Maps

    public City(String cityCode, String cityName, String state, double latitude, double longitude) {
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCityCode() {
        return this.cityCode;
    }
    public String getCityName() {
        return this.cityName;
    }
    public String getState() {
        return this.state;
    }
    public double getLatitude() {
        return this.latitude;
    }
    public double getLongitude() {
        return this.longitude;
    }
}