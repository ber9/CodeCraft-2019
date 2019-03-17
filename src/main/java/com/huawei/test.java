package main.java.com.huawei;

import main.java.com.huawei.entity.Car;
import main.java.com.huawei.graph.Edge;
import main.java.com.huawei.graph.Graph;
import main.java.com.huawei.graph.ksp.LazyEppstein;
import main.java.com.huawei.graph.util.Path;
import main.java.com.huawei.util.ReadUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        String carPath = null,roadPath = null;
        try {
            kShortestPaths(carPath, roadPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<List<Path>> kShortestPaths(String carPath, String roadPath) throws IOException {
        long stTime = System.currentTimeMillis();
        carPath = "C:\\project\\ideaProject\\CodeCraft-2019\\src\\main\\java\\com\\huawei\\car.txt";
        roadPath = "C:\\project\\ideaProject\\CodeCraft-2019\\src\\main\\java\\com\\huawei\\road.txt";
        int K = 100;
        Graph graph = new Graph(roadPath);
        List<Car> cars = new ReadUtil().readCarFile(carPath);
        List<List<Path>> carPaths = new ArrayList<>();
        HashMap<String,String> roadsId = graph.getRoadsId();
        for(Car car:cars){
            graph.updateWeight(car);
            List<Path> paths = kShortestPath(graph, car, K);
            List<Path> formatPaths = new LinkedList<>();
            String roadId = null;
            for(int i = 0; i<paths.size(); i++){
                Path path = new Path();
                Path oPath = paths.get(i);
                for(Edge edge:oPath.getEdges()){
                    roadId = roadsId.get(edge.getFromNode()+"_"+edge.getToNode());
                    path.add(roadId);
                }
                path.setTotalCost(oPath.getTotalCost());
                //assert oPath.getTotalCost()>100:car.toString();
                formatPaths.add(path);
            }
            carPaths.add(formatPaths);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("kPaths时间:'"+(endTime - stTime)+"'");
        return carPaths;
    }

    public static List<Path> kShortestPath(Graph graph, Car car, int k){
        List<Path> ksp;
        LazyEppstein lazyEppsteinAlgorithm = new LazyEppstein();
        ksp = lazyEppsteinAlgorithm.ksp(graph, car.getFrom(), car.getTo(), k);
        /*int n = 0;
        for (Path p : ksp) {
            if(n<10)
                System.out.println(++n + ") " + p);
        }*/
        return ksp;
    }
}
