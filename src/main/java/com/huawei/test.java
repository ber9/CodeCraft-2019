package com.huawei;

import com.huawei.entity.Car;
import com.huawei.graph.Edge;
import com.huawei.graph.Graph;
import com.huawei.graph.ksp.LazyEppstein;
import com.huawei.graph.util.Path;
import com.huawei.util.ReadUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        String carPath = "C:\\project\\ideaProject\\CodeCraft-2019\\src\\main\\java\\com\\huawei\\car.txt",
                roadPath = "C:\\project\\ideaProject\\CodeCraft-2019\\src\\main\\java\\com\\huawei\\road.txt";
        try {
            List<List<Path>> paths = kShortestPaths(carPath, roadPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeAnswer(List<Path> paths, String answerPath){
        File answer = new File(answerPath);
        try {
            answer.createNewFile();
            FileWriter writer = new FileWriter(answer);
            BufferedWriter out = new BufferedWriter(writer);
            for(Path path:paths){
                String carId = path.getCarId();
                String finalPath = carId;
                finalPath = finalPath+","+path.getStartTime();
                for(String road:path.getRoads()){
                    finalPath = finalPath+","+road;
                }
                out.write("(" + finalPath + ")\r\n"); // \r\n即为换行
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<List<Path>> kShortestPaths(String carPath, String roadPath) throws IOException {
        long stTime = System.currentTimeMillis();
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
                path.setCarId(car.getId());
                path.setStartTime(car.getPlanTime());
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
