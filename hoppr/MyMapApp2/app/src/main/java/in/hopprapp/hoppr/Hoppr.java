package in.hopprapp.hoppr;

/**
 * Created by root on 8/8/15.
 */
public class Hoppr {
    String source;
    String destination;
    Route[] route;
    String numberPlateNumber;
    String driverName;

    public Hoppr(String source, String destination, Route[] route, String numberPlateNumber, String driverName) {
        this.source = source;
        this.destination = destination;
        this.route = route;
        this.numberPlateNumber = numberPlateNumber;
        this.driverName = driverName;
    }

    public Hoppr(String source, String destination, String numberPlateNumber) {
        this.source = source;
        this.destination = destination;
        this.numberPlateNumber = numberPlateNumber;

    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Route[] getRoute() {
        return route;
    }

    public void setRoute(Route[] route) {
        this.route = route;
    }

    public String getNumberPlateNumber() {
        return numberPlateNumber;
    }

    public void setNumberPlateNumber(String numberPlateNumber) {
        this.numberPlateNumber = numberPlateNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
