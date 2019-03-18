package com.huawei;

import com.huawei.entity.Car;
import com.huawei.graph.Edge;
import com.huawei.graph.Graph;
import com.huawei.graph.ksp.LazyEppstein;
import com.huawei.graph.util.Path;
import com.huawei.util.ReadUtil;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);
    public static void main(String[] args)
    {
        if (args.length != 4) {
            logger.error("please input args: inputFilePath, resultFilePath");
            return;
        }

        logger.info("Start...");

        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        String answerPath = args[3];
        logger.info("carPath = " + carPath + " roadPath = " + roadPath + " crossPath = " + crossPath + " and answerPath = " + answerPath);

        // TODO:read input files
        logger.info("start read input files");
        try {
            List<List<Path>> paths = kShortestPaths(carPath, roadPath);
            List<Path> answer = new ArrayList<>();
            for(List<Path> pathList:paths){
                Path path = pathList.get(0);
                answer.add(path);
            }
            writeAnswer(answer, answerPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: calc

        // TODO: write answer.txt
        logger.info("Start write output file");

        logger.info("End...");
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
        int K = 10;
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
                path.setStartTime((int) (car.getPlanTime()+Math.random()*890));
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