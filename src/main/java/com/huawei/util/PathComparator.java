package com.huawei.util;

import com.huawei.graph.util.Path;

import java.util.Comparator;

public class PathComparator implements Comparator<Path> {
    /**
     * 时间为第一优先级，速度为第二优先级
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(Path o1, Path o2) {
        if (o1.getStartTime() > o2.getStartTime()){
            return 1;
        }else if (o1.getStartTime() < o2.getStartTime()){
            return -1;
        }else {
            if (o1.getSpeed() > o1.getSpeed()){
                return -1;
            }else if (o1.getSpeed() < o1.getSpeed()){
                return 1;
            }else {
                return 0;
            }
        }
    }

    /*@Override
    public int compare(Path o1, Path o2){
        return o1.getStartTime() - o2.getStartTime();
    }*/
}
