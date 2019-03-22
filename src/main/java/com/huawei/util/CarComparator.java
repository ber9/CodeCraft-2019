package com.huawei.util;

import com.huawei.entity.Car;

import java.util.Comparator;

public class CarComparator implements Comparator<Car> {
    /**
     * 时间为第一优先级，速度为第二优先级
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(Car o1, Car o2) {
        if (o1.getPlanTime() > o2.getPlanTime()){
            return 1;
        }else if (o1.getPlanTime() < o2.getPlanTime()){
            return -1;
        }else {
            if (o1.getSpeed() > o2.getSpeed()){
                return -1;
            }else if (o1.getSpeed() < o2.getSpeed()){
                return 1;
            }else {
                return 0;
            }
        }
    }

    /*@Override
    public int compare(Car o1, Car o2) {
        return o1.getPlanTime() - o2.getPlanTime();
    }*/
}
