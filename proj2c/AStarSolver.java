package proj2c;

import proj2ab.ArrayHeapMinPQ;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    double totalWeight = 0;
    int numExplored = 0;
    double time;
    SolverOutcome result;
    ArrayList<Vertex> path = new ArrayList<>();
    HashMap<Vertex, Attributes> tracker = new HashMap<>();
    ArrayHeapMinPQ<Vertex> fringe = new ArrayHeapMinPQ<>();

    private class Attributes {  // store the distance and edge info in one
        double distTo;          // distance from the start
        Vertex edgeTo;          // previous vertex
        Attributes(double d, Vertex e) {
            distTo = d;
            edgeTo = e;
        }
    }
    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        Stopwatch sw = new Stopwatch();
        Vertex currentVertex;
        fringe.add(start, input.estimatedDistanceToGoal(start, end));
        tracker.put(start, new Attributes(0, null)); //
        /* Repeat until the PQ is empty, PQ.getSmallest() is the goal, or timeout is exceeded */
        while (fringe.size() != 0
                && !fringe.getSmallest().equals(end)
                && sw.elapsedTime() < timeout) {
            currentVertex = fringe.removeSmallest();             // go to next vertex
            numExplored++;
            /* relax(check and update) all edges outgoing from current vertex */
            for (WeightedEdge<Vertex> edge : input.neighbors(currentVertex)) {
                /*if exploring vertex is new vertex, add to tracker and fringe */
                if (!tracker.containsKey(edge.to())) {
                    tracker.put(edge.to(),
                            new Attributes(tracker.get(edge.from()).distTo
                                    + edge.weight(), edge.from()));
                    fringe.add(edge.to(),
                            tracker.get(edge.to()).distTo
                                    + input.estimatedDistanceToGoal(edge.to(), end));

                } else { // if revisiting a vertex
                    /* if revisit is a better route, then update distTo in tracker */
                    if (tracker.get(edge.to()).distTo
                            > tracker.get(edge.from()).distTo + edge.weight()) {
                        tracker.replace(edge.to(),
                                new Attributes(tracker.get(edge.from()).distTo
                                        + edge.weight(), edge.from()));

                        /* if fringe has the vertex, then update priority */
                        if (fringe.contains(edge.to())) {
                            fringe.changePriority(edge.to(),
                                    tracker.get(edge.to()).distTo
                                            + input.estimatedDistanceToGoal(edge.to(), end));

                        } else { // if fringe doesn't have the vertex, then add
                            fringe.add(edge.to(),
                                    tracker.get(edge.to()).distTo
                                            + input.estimatedDistanceToGoal(edge.to(), end));
                        }
                    }
                }
            }
        }

        if (fringe.size() == 0) { // unsolvable case
            result = SolverOutcome.UNSOLVABLE;
        } else if (sw.elapsedTime() > timeout) { // timeout case
            result = SolverOutcome.TIMEOUT;
        } else if (fringe.getSmallest().equals(end)) {  // solved case
            path = findPath(end, start);
            totalWeight = calWeight(end);
            time = sw.elapsedTime();
            result = SolverOutcome.SOLVED;
        }
    }

    private double calWeight(Vertex end) {
        return tracker.get(end).distTo;
    }

    /* recursively go back to the beginning to construct path */
    private ArrayList<Vertex> findPath(Vertex v, Vertex goal) {
        if (!v.equals(goal)) {
            ArrayList<Vertex> route = findPath(tracker.get(v).edgeTo, goal);
            route.add(v);
            return route;
        } else {
            return new ArrayList<>(List.of(goal));
        }
    }
    public SolverOutcome outcome() {
        return result;
    }
    public List<Vertex> solution() {
        return path;
    }
    public double solutionWeight() {
        return totalWeight;
    }
    public int numStatesExplored() {
        return numExplored;
    }
    public double explorationTime() {
        return time;
    }
}
