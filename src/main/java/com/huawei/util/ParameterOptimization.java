package com.huawei.util;

import com.huawei.entity.Car;
import com.huawei.graph.util.Path;

import java.io.*;
import java.util.*;

public class ParameterOptimization {

    /**
     * 每一次同一个时间的一起出发numOfCars辆，其他的调度时间每numOfCars次加1
     *
     * @param paths
     * @param numOfCars
     */
    public static void startCarsAtOneTime(List<Path> paths, int numOfCars) {
        int i = 0, j = 1;
        paths.sort(new PathComparator());
        for (Path path : paths) {
            if (j <= numOfCars) {
                path.setStartTime(path.getStartTime() + i);
            } else {
                i++;
                path.setStartTime(path.getStartTime() + i);
                j = 1;
            }
            j++;
        }
    }

    public static List<Path> startCarsAtOneTimeOrderByPlanTimeAndSpeed(List<Path> paths, int numOfCars, double MUTI_NUMBER) {
        int i = 0, j = 1;
        int preTime = 1;
        paths.sort(new PathComparator());
        List<Path> tmpList = new LinkedList<>();
        List<Path> resList = new LinkedList<>();
        Set<String> isHave = new HashSet<>();
        int len = paths.size();
        while (len > resList.size()) {//直到reslist长度微len
            if (i == 0) {
                for (int m = 0; m < paths.size() && m < numOfCars; m++) {//首先reslist加入numOfCars个路径，isHave加入carId
                    resList.add(paths.get(m));
                    isHave.add(paths.get(m).getCarId());
                }
                i++;
            } else {
                for (int k = 0; k < paths.size(); k++) {//遍历所有路径
                    Path path1 = paths.get(k);
                    if (path1.getStartTime() > preTime + 1) {//路径开始时间>当前时间就不再添加
                        break;
                    }
                    if (isHave.contains(path1.getCarId())) {//是否已经加入此车路径
                        continue;
                    }
                    if (path1.getStartTime() <= preTime) {//if（路径开始时间<现在时间）将该路径时间设为现在
                        path1.setStartTime(preTime);
                    }
                    if (path1.getStartTime() == preTime) {//时间相等则加入tmplist
                        Path tmp = path1.clone();
                        isHave.add(tmp.getCarId());
                        tmpList.add(tmp);
                    }
                }

                tmpList.sort(new PathSpeedComparator());
                Iterator<Path> li = tmpList.iterator();
                double muti = numOfCars*Math.abs(Math.cos(i/(Math.PI*MUTI_NUMBER)));
                while (li.hasNext()) {//遍历tmplist
                    Path path = li.next();
                    if (j <= muti) {//
                        path.setStartTime(path.getStartTime() + 1);//时间加一秒，加入reslist
                        resList.add(path);
                        li.remove();
                    } else {//时间加一秒，设为pretime，break
                        path.setStartTime(path.getStartTime() + 1);
                        preTime = path.getStartTime();
                        resList.add(path);
                        li.remove();
                        j = 1;
                        break;
                    }
                    j++;
                }
                for (Path path : tmpList) {
                    isHave.remove(path.getCarId());//将剩下的在isHave中剔除
                }
                tmpList.clear();
                i++;
            }
        }
        return resList;
    }

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }
}
