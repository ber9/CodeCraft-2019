package com.huawei.util;

import com.huawei.graph.util.Path;

import java.util.Collections;
import java.util.List;

public class ParameterOptimization {

    /**
     * 每一次同一个时间的一起出发numOfCars辆，其他的调度时间每numOfCars次加1
     *
     * @param paths
     * @param numOfCars
     */
    public static void startCarsAtOneTime(List<Path> paths, int numOfCars) {
        int i = 0, j = 0;
        Collections.sort(paths, new PathComparator());
        for (Path path : paths) {
            if (j < numOfCars) {
                path.setStartTime(path.getStartTime() + i);
            } else {
                i++;
                path.setStartTime(path.getStartTime() + i);
                j = 0;
            }
            j++;
        }
    }


}
