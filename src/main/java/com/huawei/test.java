package main.java.com.huawei;

import main.java.com.huawei.entity.Car;
import main.java.com.huawei.graph.Graph;
import main.java.com.huawei.graph.ksp.LazyEppstein;
import main.java.com.huawei.graph.util.Path;
import main.java.com.huawei.util.ReadUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        String carPath = "B:\\GSM\\Documents\\CodeCraft-2019\\src\\main\\java\\com\\huawei\\car.txt",roadPath = "B:\\GSM\\Documents\\CodeCraft-2019\\src\\main\\java\\com\\huawei\\road.txt";
        try {
            kShortestPaths(carPath, roadPath);
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
        for(Car car:cars){
            graph.updateWeight(car);
            carPaths.add(kShortestPath(graph, car, K));
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
