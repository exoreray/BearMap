package proj2d;

import proj2ab.KDTree;
import proj2ab.Point;
import proj2c.streetmap.StreetMapGraph;
import proj2c.streetmap.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {
    private List<Node> nodesList;
    private Map<String, List<Node>> cleanMatch = new HashMap<>();
    private TrieSET names = new TrieSET();
    private Map<Point, Node> tracker = new HashMap<>();
    private List<Point> points = new ArrayList<>();
    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        // You might find it helpful to uncomment the line below:
        List<Node> nodes = this.getNodes();
        this.nodesList = nodes;

        // construct points list
        for (Node tempNode : nodesList) {
            if (!neighbors(tempNode.id()).isEmpty()) {
                Point tempPoint = new Point(tempNode.lon(), tempNode.lat());
                points.add(tempPoint);
                tracker.put(tempPoint, tempNode);
            }
        }
        for (Node node: nodesList) {
            if (node.name() != null) {
                names.add(cleanString(node.name()));
                if (cleanMatch.containsKey(cleanString(node.name()))) {
                    List<Node> temp = cleanMatch.get(cleanString(node.name()));
                    temp.add(node);
                    cleanMatch.put(cleanString(node.name()), temp);
                } else {
                    List<Node> temp = new ArrayList<>();
                    temp.add(node);
                    cleanMatch.put(cleanString(node.name()), temp);
                }
            }
        }
    }

    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        Map<Point, Node> tracker = new HashMap<>();
        List<Point> points = new ArrayList<>();
        // construct points list
        for (Node tempNode : nodesList) {
            if (!neighbors(tempNode.id()).isEmpty()) {
                Point tempPoint = new Point(tempNode.lon(), tempNode.lat());
                points.add(tempPoint);
                tracker.put(tempPoint, tempNode);
            }
        }
        KDTree pointSet = new KDTree(points);
        return tracker.get(pointSet.nearest(lon, lat)).id();
    }


    /**
     * For Project Part III (gold points)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {

        ArrayList<String> result = new ArrayList<>();
        for (String name : names.keysWithPrefix(cleanString(prefix))) {
            for (Node node: cleanMatch.get(name)) {
                result.add(node.name());
            }
        }
        return result;
    }

    /**
     * For Project Part III (gold points)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Node node: cleanMatch.get(cleanString(locationName))) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("lat", node.lat());
            temp.put("lon", node.lon());
            temp.put("name", node.name());
            temp.put("id", node.id());
            result.add(temp);
        }
        return result;
    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

}
