package com.huawei;

import com.huawei.entity.Car;
import com.huawei.graph.Edge;
import com.huawei.graph.Graph;
import com.huawei.graph.ksp.LazyEppstein;
import com.huawei.graph.util.Path;
import com.huawei.util.CarComparator;
import com.huawei.util.ParameterOptimization;
import com.huawei.util.ReadUtil;
import com.huawei.util.ScheduleSim;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);
    //23.81 532;
    //35,160 1949
    //36,170 1902
    //37,200 1856
    private static int NUM_OF_CARS = 28;
    private static int NUM_OF_CONSIDER_CARS = 80;
    private static double MUTI_NUMBER = 1;
    private static double WEIGHT_RATIO = 1;

    private static int flag = 0;
    //:weight_ratio/channel_num

    public static void main(String[] args) {
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
            /*for (NUM_OF_CARS = 35; NUM_OF_CARS <=50; NUM_OF_CARS++)
                for (NUM_OF_CONSIDER_CARS = 180; NUM_OF_CONSIDER_CARS <= 230; NUM_OF_CONSIDER_CARS++){
                    List<List<Path>> paths = kShortestPaths(carPath, roadPath);
                    List<Path> answer = new ArrayList<>();
                    for (List<Path> pathList : paths) {
                        Path path = pathList.get(0);
                        answer.add(path);
                    }
                    writeAnswer(answer, answerPath);
                    ScheduleSim scheduleSim = new ScheduleSim();
                    scheduleSim.initMap(carPath, crossPath, roadPath);
                    int time = scheduleSim.runSchedule(answer);
                    System.out.print("最终时间:");
                    System.out.println(time);
                    recordResults(NUM_OF_CARS, NUM_OF_CONSIDER_CARS, time, MUTI_NUMBER);
                    if (time != -1){
                        flag++;
                    }
                    if (flag == 2){
                        break;
                    }
                }*/
            List<List<Path>> paths = kShortestPaths(carPath, roadPath);
            List<Path> answer = new ArrayList<>();
            for (List<Path> pathList : paths) {
                Path path = pathList.get(0);
                answer.add(path);
            }
            writeAnswer(answer, answerPath);
            ScheduleSim scheduleSim = new ScheduleSim();
            scheduleSim.initMap(carPath, crossPath, roadPath);
            int time = scheduleSim.runSchedule(answer);
            System.out.print("最终时间:");
            System.out.println(time);
            /*recordResults(NUM_OF_CARS, NUM_OF_CONSIDER_CARS, time);
            if (time != -1) {
                flag++;
            }*/


        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: calc

        // TODO: write answer.txt
        logger.info("Start write output file");

        logger.info("End...");
    }

    public static List<Path> writeAnswer(List<Path> paths, String answerPath) {
        long last = System.currentTimeMillis();
        File answer = new File(answerPath);
        List<Path> orderedPath = null;
        try {
            answer.createNewFile();
            OutputStreamWriter writer = null;
            BufferedWriter out = null;
            OutputStream os = new FileOutputStream(answer);
            writer = new OutputStreamWriter(os);
            out = new BufferedWriter(writer);
            orderedPath = ParameterOptimization.startCarsAtOneTimeOrderByPlanTimeAndSpeed(paths, NUM_OF_CARS, MUTI_NUMBER);
//            ParameterOptimization.startCarsAtOneTime(paths,NUM_OF_CARS);
            for (Path path : orderedPath) {
                String finalPath = path.getCarId();
                finalPath = finalPath + "," + path.getStartTime();
                for (String road : path.getRoads()) {
                    finalPath = finalPath + "," + road;
                }
                // \r\n即为换行
                out.write("(" + finalPath + ")\r\n");
            }
            out.flush();
            long now = System.currentTimeMillis();
            System.out.print("write time:" + (now - last));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orderedPath;
    }

    public static List<List<Path>> kShortestPaths(String carPath, String roadPath) throws IOException {
        long stTime = System.currentTimeMillis();
        int K = 1;
        Graph graph = new Graph(roadPath);
        List<Car> cars = new ReadUtil().readCarFile(carPath);
        List<List<Path>> carPaths = new ArrayList<>();
        List<List<Path>> originCarPaths = new ArrayList<>();
        HashMap<String, String> roadsId = graph.getRoadsId();
        cars.sort(new CarComparator());
        for (Car car : cars) {
//            graph.updateWeight(car);
            graph.updateWeight(car, originCarPaths, NUM_OF_CONSIDER_CARS, WEIGHT_RATIO);
            List<Path> paths = kShortestPath(graph, car, K);
            List<Path> formatPaths = new LinkedList<>();
            List<Path> originPaths = new LinkedList<>();
            String roadId = null;
            for (Path oPath : paths) {
                Path path = new Path();
                for (Edge edge : oPath.getEdges()) {
                    roadId = roadsId.get(edge.getFromNode() + "_" + edge.getToNode());
                    path.add(roadId);
                }
                path.setTotalCost(oPath.getTotalCost());
                path.setCarId(car.getId());
                path.setSpeed(car.getSpeed());
                path.setStartTime(car.getPlanTime());
                formatPaths.add(path);
                originPaths.add(oPath);
            }
            carPaths.add(formatPaths);
            originCarPaths.add(originPaths);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("kPaths时间:'" + (endTime - stTime) + "'");
        return carPaths;
    }

    public static List<Path> kShortestPath(Graph graph, Car car, int k) {
        List<Path> ksp;
        LazyEppstein lazyEppsteinAlgorithm = new LazyEppstein();
        ksp = lazyEppsteinAlgorithm.ksp(graph, car.getFrom(), car.getTo(), k);
        return ksp;
    }

    private static void recordResults(int cars, int considerCars, int times, double MUTI_NUMBER) {
        File answer = new File("/home/zxw/pi.txt");
        try {
            answer.createNewFile();
            FileWriter writer = new FileWriter(answer, true);
            BufferedWriter out = new BufferedWriter(writer);
            out.write("并发车数：" + cars + " 参考车数：" + considerCars + "nPI:"+ MUTI_NUMBER +" 调度次数：" + times + "\r\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
