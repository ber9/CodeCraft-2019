package com.huawei.util;

import com.huawei.graph.util.Path;

import java.util.Comparator;

public class PathSpeedComparator implements Comparator<Path> {
    @Override
    public int compare(Path o1, Path o2) {
        return o2.getSpeed() - o1.getSpeed();
    }
}
