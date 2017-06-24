/**
 * Created by renx on 23.06.17.
 */
public class Driver {

    public String name;
    private double badBehaviour;

    public Driver(String name, double badBehaviour) {
        this.name = name;
        this.badBehaviour = badBehaviour;
    }

    public Ride getRide() {
        return new Ride(name, badBehaviour);

    }
}
