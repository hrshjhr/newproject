package in.hopprapp.hoppr;

/**
 * Created by root on 8/8/15.
 */
public class Route {
    double latitude;
    double longitude;

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

    public Route(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
