package com.huawei.graph.ksp.test;

import com.huawei.entity.Car;
import com.huawei.graph.Graph;
import com.huawei.graph.ksp.LazyEppstein;
import com.huawei.graph.util.Path;
import com.huawei.util.ReadUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test of the lazy version of Eppstein's algorithm for computing the K shortest paths between two nodes in a graph.
 *
 * Copyright (C) 2015  Brandon Smock (dr.brandon.smock@gmail.com, GitHub: bsmock)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by Brandon Smock on October 7, 2015.
 * Last updated by Brandon Smock on December 24, 2015.
 */
public class TestLazyEppstein {

    public static void main(String args[]) throws IOException {
        long stTime = System.currentTimeMillis();
        /* Uncomment any of these example tests */
        String roadFilename, carFilename, crossFilename;
        int K;

        roadFilename = "src/graph/ksp/test/road.txt";
        carFilename = "src/graph/ksp/test/car.txt";
        crossFilename = "src/graph/ksp/test/cross.txt";

        K = 1;

        Graph graph = new Graph(roadFilename);
        List<Car> cars = new ReadUtil().readCarFile(carFilename);
        List<List<Path>> carPaths = new ArrayList<>();
        for(Car car:cars){
            graph.updateWeight(car);
            carPaths.add(kShortestPath(graph, car, K));
        }

        //usageExample1(graphFilename,sourceNode,targetNode,K);
        long endTime = System.currentTimeMillis();
        System.out.println("时间:'"+(endTime - stTime)+"'");
    }

    public static List<Path> kShortestPath(Graph graph, Car car, int k){
        List<Path> ksp;
        long timeStart = System.currentTimeMillis();
        LazyEppstein lazyEppsteinAlgorithm = new LazyEppstein();
        ksp = lazyEppsteinAlgorithm.ksp(graph, car.getFrom(), car.getTo(), k);
        long timeFinish = System.currentTimeMillis();
        int n = 0;
        for (Path p : ksp) {
            if(n<10)
                System.out.println(++n + ") " + p);
        }
        return ksp;
    }

    public static void usageExample1(String graphFilename, String source, String target, int k) {
        /* Read graph from file */
        System.out.print("Reading data from file... ");
        Graph graph = new Graph(graphFilename);
        System.out.println("complete.");

        /* Compute the K shortest paths and record the completion time */
        System.out.print("Computing the " + k + " shortest paths from [" + source + "] to [" + target + "] ");
        System.out.print("using the lazy version of Eppstein's algorithm... ");
        List<Path> ksp;
        long timeStart = System.currentTimeMillis();
        LazyEppstein lazyEppsteinAlgorithm = new LazyEppstein();
        ksp = lazyEppsteinAlgorithm.ksp(graph, source, target, k);
        long timeFinish = System.currentTimeMillis();
        System.out.println("complete.");

        System.out.println("Operation took " + (timeFinish - timeStart) / 1000.0 + " seconds.");

        /* Output the K shortest paths */
        System.out.println("k) cost: [path]");
        int n = 0;
        for (Path p : ksp) {
            System.out.println(++n + ") " + p);
        }
    }
}
