package com.huawei.graph.util;

import com.huawei.entity.Road;
import com.huawei.graph.*;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * The Path class implements a path in a weighted, directed graph as a sequence of Edges.
 *
 * Created by Brandon Smock on 6/18/15.
 */
public class Path implements Cloneable, Comparable<Path> {
    private LinkedList<Edge> edges;
    private LinkedList<String> roads;
    private LinkedList<Road> roadEntity;
    private String carId;
    private int speed;
    private int startTime;
    private double totalCost;
    private int fitness;

    public Path() {
        edges = new LinkedList<Edge>();
        roads = new LinkedList<String>();
        roadEntity = new LinkedList<Road>();
        totalCost = 0;
    }

    public Path(double totalCost) {
        edges = new LinkedList<Edge>();
        roads = new LinkedList<String>();

        this.totalCost = totalCost;
    }

    public Path(LinkedList<Edge> edges) {
        this.edges = edges;
        totalCost = 0;
        for (Edge edge : edges) {
            totalCost += edge.getWeight();
        }
    }

    public Path(LinkedList<Edge> edges,LinkedList<String> roads,String carId,int speed,int startTime,int fitness) {
        this.edges = edges;
        this.roads = roads;
        this.carId = carId;
        this.speed = speed;
        this.startTime = startTime;
        this.fitness = fitness;
        totalCost = 0;
        for (Edge edge : edges) {
            totalCost += edge.getWeight();
        }
    }

    public Path(LinkedList<Edge> edges, double totalCost) {
        this.edges = edges;
        this.totalCost = totalCost;
    }

    public LinkedList<Road> getRoadEntity() {
        return roadEntity;
    }

    public void setRoadEntity(LinkedList<Road> roadEntity) {
        this.roadEntity = roadEntity;
    }

    public void addRoadEntity(Road road){
        roadEntity.add(road);
    }

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    public LinkedList<String> getRoads() {
        return roads;
    }

    public void setRoads(LinkedList<String> roads) {
        this.roads = roads;
    }

    public void setEdges(LinkedList<Edge> edges) {
        this.edges = edges;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public List<String> getNodes() {
        LinkedList<String> nodes = new LinkedList<String>();

        for (Edge edge : edges) {
            nodes.add(edge.getFromNode());
        }

        Edge lastEdge = edges.getLast();
        if (lastEdge != null) {
            nodes.add(lastEdge.getToNode());
        }

        return nodes;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void addFirstNode(String nodeLabel) {
        String firstNode = edges.getFirst().getFromNode();
        edges.addFirst(new Edge(nodeLabel, firstNode,0));
    }

    public void addFirst(Edge edge) {
        edges.addFirst(edge);
        totalCost += edge.getWeight();
    }

    public void add(Edge edge) {
        edges.add(edge);
        totalCost += edge.getWeight();
    }

    public void add(String road) {
        roads.add(road);
    }

    public void addLastNode(String nodeLabel) {
        String lastNode = edges.getLast().getToNode();
        edges.addLast(new Edge(lastNode, nodeLabel,0));
    }

    public int size() {
        return edges.size();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int numEdges = edges.size();
        sb.append(totalCost);
        sb.append(": [");
        if (numEdges > 0) {
            for (int i = 0; i < edges.size(); i++) {
                sb.append(edges.get(i).getFromNode().toString());
                sb.append("-");
            }

            sb.append(edges.getLast().getToNode().toString());
        }
        sb.append("]");
        return sb.toString();
    }

/*    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path path = (Path) o;

        if (Double.compare(path.totalCost, totalCost) != 0) return false;
        if (!edges.equals(path.edges)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = edges.hashCode();
        temp = Double.doubleToLongBits(totalCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }*/

    public boolean equals(Path path2) {
        if (path2 == null)
            return false;

        LinkedList<Edge> edges2 = path2.getEdges();

        int numEdges1 = edges.size();
        int numEdges2 = edges2.size();

        if (numEdges1 != numEdges2) {
            return false;
        }

        for (int i = 0; i < numEdges1; i++) {
            Edge edge1 = edges.get(i);
            Edge edge2 = edges2.get(i);
            if (!edge1.getFromNode().equals(edge2.getFromNode()))
                return false;
            if (!edge1.getToNode().equals(edge2.getToNode()))
                return false;
        }

        return true;
    }

    public int compareTo(Path path2) {
        double path2Cost = path2.getTotalCost();
        if (totalCost == path2Cost)
            return 0;
        if (totalCost > path2Cost)
            return 1;
        return -1;
    }

    public Path clone() {
        LinkedList<Edge> edges = new LinkedList<Edge>();
        LinkedList<String> roads = new LinkedList<>();
        String carId = this.carId;
        int speed = this.speed;
        int startTime = this.startTime;
        int fitness = this.fitness;

        for (Edge edge : this.edges) {
            edges.add(edge.clone());
        }
        for(String road:this.roads){
            roads.add(road);
        }
        return new Path(edges, roads,carId,speed,startTime,fitness);
    }

    public Path shallowClone() {
        LinkedList<Edge> edges = new LinkedList<Edge>();

        for (Edge edge : this.edges) {
            edges.add(edge);
        }

        return new Path(edges,this.totalCost);
    }

    public Path cloneTo(int i) {
        LinkedList<Edge> edges = new LinkedList<Edge>();
        int l = this.edges.size();
        if (i > l)
            i = l;

        //for (Edge edge : this.edges.subList(0,i)) {
        for (int j = 0; j < i; j++) {
            edges.add(this.edges.get(j).clone());
        }

        return new Path(edges);
    }

    public Path cloneFrom(int i) {
        LinkedList<Edge> edges = new LinkedList<Edge>();

        for (Edge edge : this.edges.subList(i,this.edges.size())) {
            edges.add(edge.clone());
        }

        return new Path(edges);
    }

    public void addPath(Path p2) {

        this.edges.addAll(p2.getEdges());
        this.totalCost += p2.getTotalCost();
    }
}