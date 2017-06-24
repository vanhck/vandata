import javafx.util.Pair;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by renx on 23.06.17.
 */
public class Main {
    public static void main(String[] args) {
        Driver[] drivers = new Driver[5];
        drivers[0] = new Driver("Andre Meixner", 1.0);
        drivers[1] =  new Driver("Charlotte Pröller", 0.5);
        drivers[2] = new Driver("Jonas Kett", 0.0);
        drivers[3] = new Driver("Luise Kaufmann", 0.8);
        drivers[4] = new Driver("Marc Mayer", 0.7);

        HashMap<String, Pair<Double, Integer> > driverMap = new HashMap<>();
        for (int i = 0; i < drivers.length; i++) {
            driverMap.put(drivers[i].name, new Pair<>(0.0, 0));
        }

        String filePath = "file.txt";

        for (int i = 0; i < 400; i++) {
            int driver = ThreadLocalRandom.current().nextInt(0, drivers.length);
            Ride ride = drivers[driver].getRide();
            System.out.println(ride);
            Pair<Double, Integer> oldscore = driverMap.get(drivers[driver].name);
            Pair<Double, Integer> score = ride.getScore();
            driverMap.put(drivers[driver].name, new Pair<>(oldscore.getKey() + score.getKey(), oldscore.getValue() + score.getValue()));
            ride.writeXML(filePath);
            FileSender.sendFile(filePath);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < drivers.length; i++) {
            Pair<Double, Integer> score = driverMap.get(drivers[i].name);
            System.out.println(drivers[i].name + " has Score " + score.getKey() / score.getValue());

        }
    }
}
