package group12.RouteMaker;

public class Site {

//  private static final long serialVersionUID = 1L;
//   public String address;
//  public String neighborhood;
//  public String imageUrl;
//  public String dollarSign;
//  public String priceRange;
    public String category;
    public double duration;
    public String phoneNumber;
    public String siteId;
    public String siteName;
    public double latitude;
    public double longitude;
    public int monOpen;
    public int monClose;
    public int tueOpen;
    public int tueClose;
    public int wedOpen;
    public int wedClose;
    public int thuOpen;
    public int thuClose;
    public int friOpen;
    public int friClose;
    public int satOpen;
    public int satClose;
    public int sunOpen;
    public int sunClose;
    public String siteUrl;
    public String cityCode;

    public Site() {
       
    }

    public Site(String siteId, String siteName, double latitude, double longitude, String phoneNumber,
          String category, double duration, int monOpen, int monClose, int tueOpen,
          int tueClose, int wedOpen, int wedClose, int thuOpen, int thuClose,
          int friOpen, int friClose, int satOpen, int satClose, int sunOpen,
          int sunClose, String siteUrl, String citycode) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.category = category;
        this.duration = duration;
        this.monClose = monClose;
        this.monOpen = monOpen;
        this.tueOpen = tueOpen;
        this.tueClose = tueClose;
        this.wedOpen = wedOpen;
        this.wedClose = wedClose;
        this.thuOpen = thuOpen;
        this.thuClose = thuClose;
        this.friOpen = friOpen;
        this.friClose = friClose;
        this.satOpen = satOpen;
        this.satClose = satClose;
        this.sunOpen = sunOpen;
        this.sunClose = sunClose;
        this.siteUrl = siteUrl;
        this.cityCode = citycode;
    }
    
   public String getCategory() {
      return this.category;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public double getDuration() {
      return this.duration;
   }

   public void setDuration(double duration) {
      this.duration = duration;
   }

   public String getPhoneNumber() {
      return this.phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public String getSiteId() {
      return this.siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public String getSiteName() {
      return this.siteName;
   }

   public void setSiteName(String siteName) {
      this.siteName = siteName;
   }

   public double getLatitude() {
      return this.latitude;
   }

   public void setLatitude(double latitude) {
      this.latitude = latitude;
   }

   public double getLongitude() {
      return this.longitude;
   }

   public void setLongitude(double longitude) {
      this.longitude = longitude;
   }

   public int getMonOpen() {
      return this.monOpen;
   }

   public void setMonOpen(int monOpen) {
      this.monOpen = monOpen;
   }

   public int getMonClose() {
      return this.monClose;
   }

   public void setMonClose(int monClose) {
      this.monClose = monClose;
   }

   public int getTueOpen() {
      return this.tueOpen;
   }

   public void setTueOpen(int tueOpen) {
      this.tueOpen = tueOpen;
   }

   public int getTueClose() {
      return this.tueClose;
   }

   public void setTueClose(int tueClose) {
      this.tueClose = tueClose;
   }

   public int getWedOpen() {
      return this.wedOpen;
   }

   public void setWedOpen(int wedOpen) {
      this.wedOpen = wedOpen;
   }

   public int getWedClose() {
      return this.wedClose;
   }

   public void setWedClose(int wedClose) {
      this.wedClose = wedClose;
   }

   public int getThuOpen() {
      return this.thuOpen;
   }

   public void setThuOpen(int thuOpen) {
      this.thuOpen = thuOpen;
   }

   public int getThuClose() {
      return this.thuClose;
   }

   public void setThuClose(int thuClose) {
      this.thuClose = thuClose;
   }

   public int getFriOpen() {
      return this.friOpen;
   }

   public void setFriOpen(int friOpen) {
      this.friOpen = friOpen;
   }

   public int getFriClose() {
      return this.friClose;
   }

   public void setFriClose(int friClose) {
      this.friClose = friClose;
   }

   public int getSatOpen() {
      return this.satOpen;
   }

   public void setSatOpen(int satOpen) {
      this.satOpen = satOpen;
   }

   public int getSatClose() {
      return this.satClose;
   }

   public void setSatClose(int satClose) {
      this.satClose = satClose;
   }

   public int getSunOpen() {
      return this.sunOpen;
   }

   public void setSunOpen(int sunOpen) {
      this.sunOpen = sunOpen;
   }

   public int getSunClose() {
      return this.sunClose;
   }

   public void setSunClose(int sunClose) {
      this.sunClose = sunClose;
   }

   public String getSiteUrl() {
      return this.siteUrl;
   }

   public void setSiteUrl(String siteUrl) {
      this.siteUrl = siteUrl;
   }

   public String getCityCode() {
      return this.cityCode;
   }

   public void setCityCode(String cityCode) {
      this.cityCode = cityCode;
   }
}