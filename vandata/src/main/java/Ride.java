import javafx.util.Pair;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by renx on 23.06.17.
 */
public class Ride {
    private static double optimalFuelConsumption = 7.0;
    private double fuelConsumption;
    private int hardAccelerationCount;
    private int km;
    private int hardStopCount;
    private double constantVelocityKm;
    private double sidewaysAccelerationS;
    private int stops;
    private String name;

    public Ride(String name, double badBehaviour) {
        this.km = ThreadLocalRandom.current().nextInt(5, 150 + 1);
        this.name = name;
        this.hardStopCount = (int) nextGaussianMinMax(0, 4 * badBehaviour * badBehaviour * (km / 100.0) + 1);
        this.hardAccelerationCount = (int) (ThreadLocalRandom.current().nextInt(0, 10) * (km / 100.0) * (badBehaviour / 2 + 0.5));
        this.fuelConsumption = ((optimalFuelConsumption * ThreadLocalRandom.current().nextDouble(badBehaviour / 5.0 + 0.7 + this.hardAccelerationCount / 50.0, badBehaviour / 5.0 + 1.2  + this.hardAccelerationCount / 50.0))/ 100.0) * km;
        this.constantVelocityKm = nextGaussian(km / 2.0 - badBehaviour * (km / 6.0) - this.hardAccelerationCount, km / 3.0);
        this.sidewaysAccelerationS = 0.0;
        while (ThreadLocalRandom.current().nextInt(0, (int) (30 * (badBehaviour + 0.3))) < 4) {
            this.sidewaysAccelerationS += ThreadLocalRandom.current().nextDouble(1.0, 1.0 + 10.0  * km / 100);
        }
        this.stops = ThreadLocalRandom.current().nextInt((int)(2 * km - badBehaviour * km),(int)(2 * km + (badBehaviour + 0.2) * km)); // TODO
    }

    private double nextGaussianMinMax(double min, double max) {
        double g = -1.0;
        while (g < 0) g = Math.abs(ThreadLocalRandom.current().nextGaussian()) / 3.0;

        double gauss = g * (max - min) + min;
        if (gauss < min) {
            gauss = 2*min - gauss;
        }
        return gauss;
    }
    private double nextGaussian(double mean, double range) {
        double g = -1.0;
        while (g < 0) g = Math.abs(ThreadLocalRandom.current().nextGaussian()) / 3.0;

        double gauss = g * range + mean;
        return gauss;
    }

    public Pair<Double, Integer> getScore() {
        double a_fuelConsumption = 1.0;
        double w_fuelConsumption = a_fuelConsumption * 5 * Math.pow(10, 2 - ((fuelConsumption / km) * 100) / optimalFuelConsumption) * km;
        double a_hardStopCount = 0.5;
        double w_hardStopCount = a_hardStopCount * -50 * hardStopCount;
        double a_hardAccelerationCount = 0.5;
        double w_hardAccelerationCount = a_hardAccelerationCount * -5 * Math.exp(hardAccelerationCount / 2.0);
        double a_sidewaysAccelerationS = 0.7;
        double w_sidewaysAccelerationS = a_sidewaysAccelerationS * -100 * sidewaysAccelerationS;
        double a_constantVelocityKm = 0.5;
        double w_constantVelocityKm = a_constantVelocityKm * 80 * (constantVelocityKm - 0.4);
        double sum_efficiency = w_fuelConsumption + w_hardStopCount + w_hardAccelerationCount + w_sidewaysAccelerationS + w_constantVelocityKm;

        double e_stops = 2;
        double w_stops = 10 * (1 / (Math.abs(1 - stops / (e_stops * km)) + 0.01));

        return new Pair<>(0.6 * sum_efficiency + 0.4 * w_stops, km);
    }

    public String toString() {
        return name + " has " + String.valueOf(Math.round((fuelConsumption / km) * 100)) + "l/km in " + km + "km with " + hardStopCount + " hardstops" + " and " + hardAccelerationCount + " hard accelerations - constantvelo " + constantVelocityKm / km + " - sideway " + sidewaysAccelerationS;
    }

    public void writeXML(String filePath) {
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("drivingresult");
            doc.appendChild(rootElement);

            // general elements
            Element general = doc.createElement("general");
            rootElement.appendChild(general);

            Element fin = doc.createElement("fin");
            rootElement.appendChild(fin);
            fin.setTextContent(name);

            Element user = doc.createElement("user");
            rootElement.appendChild(user);
            user.setTextContent(name);

            Element drivenKm = doc.createElement("drivenKm");
            rootElement.appendChild(drivenKm);
            drivenKm.setTextContent(String.valueOf(km));

            addKeyValue(rootElement,doc,"fuelConsumption", String.valueOf(fuelConsumption));
            addKeyValue(rootElement,doc,"hardAccelerationCount", String.valueOf(hardAccelerationCount));
            addKeyValue(rootElement,doc,"hardStopCount", String.valueOf(hardStopCount));
            addKeyValue(rootElement,doc,"constantVelocityKm", String.valueOf(constantVelocityKm));
            addKeyValue(rootElement,doc,"sidewaysAccelerationS", String.valueOf(sidewaysAccelerationS));
            addKeyValue(rootElement,doc,"stops", String.valueOf(stops));

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            File file = new File("file.xml");
            StreamResult result = new StreamResult(new File(filePath));

            transformer.transform(source, result);

            System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public void addKeyValue(Element rootElement, Document doc, String key, String value) {
        Element keyvalue = doc.createElement("keyvalue");
        rootElement.appendChild(keyvalue);

        Element name = doc.createElement("name");
        keyvalue.appendChild(name);
        name.setTextContent(String.valueOf(key));

        Element valueE = doc.createElement("value");
        keyvalue.appendChild(valueE);
        valueE.setTextContent(String.valueOf(value));

    }
}
