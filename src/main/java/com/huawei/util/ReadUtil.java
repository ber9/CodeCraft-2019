package com.huawei.util;

import com.huawei.entity.*;
import com.huawei.graph.util.Path;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ReadUtil {
    private static final int IS_DUPLEX = 1;

    /**
     * @param carFilename
     * @return
     * @throws IOException
     */
    public List<Car> readCarFile(String carFilename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(carFilename));
        String line = in.readLine();
        line = in.readLine();
        List<Car> cars = new ArrayList<>();
        while (line != null) {
            String[] carDescription = line.substring(1, line.length() - 1).split(",");
            cars.add(new Car(carDescription[0].trim(), carDescription[1].trim(), carDescription[2].trim(), Integer.parseInt(carDescription[3].trim()), Integer.parseInt(carDescription[4].trim())));
            line = in.readLine();
        }
        return cars;
    }

    public void getCarMap(Map<String, Car> carMap, String carFilename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(carFilename));
        String line = in.readLine();
        line = in.readLine();
        while (line != null) {
            String[] carDescription = line.substring(1, line.length() - 1).split(",");
            carMap.put(carDescription[0].trim(), new Car(carDescription[0].trim(), carDescription[1].trim(), carDescription[2].trim(), Integer.parseInt(carDescription[3].trim()), Integer.parseInt(carDescription[4].trim())));
            line = in.readLine();
        }
    }

    public void getCrossMap(Map<String, Cross> crossMap, String crossFilename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(crossFilename));
        String line = in.readLine();
        line = in.readLine();
        while (line != null) {
            String[] crossDescription = line.substring(1, line.length() - 1).split(",");
            crossMap.put(crossDescription[0].trim(), new Cross(crossDescription[0].trim(), crossDescription[1].trim(), crossDescription[2].trim(), crossDescription[3].trim(), crossDescription[4].trim()));
            line = in.readLine();
        }
    }

    public void initRoadCondition(Map<String, RoadCondition> roadConditionMap, String roadFilename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(roadFilename));
        String line = in.readLine();
        line = in.readLine();
        while (line != null) {
            String[] description = line.substring(1, line.length() - 1).split(",");
            Road road = new Road(description[0].trim(), Integer.parseInt(description[1].trim()), Integer.parseInt(description[2].trim()),
                    Integer.parseInt(description[3].trim()), description[4].trim(), description[5].trim(), Integer.parseInt(description[6].trim()));
            List<Channel> channels = new ArrayList<>();
            for (int i = 0; i < road.getChannel(); i++) {
                List<CarOnRoad> carOnRoads = new ArrayList<>();
                Channel channel = new Channel(road.getId(), road, road.getFrom(), road.getTo(), carOnRoads);
                channels.add(channel);
            }
            if (road.getIsDuplex() == IS_DUPLEX) {
                for (int i = 0; i < road.getChannel(); i++) {
                    List<CarOnRoad> carOnRoads = new ArrayList<>();
                    Channel channel = new Channel(road.getId(), road, road.getTo(), road.getFrom(), carOnRoads);
                    channels.add(channel);
                }
            }
            RoadCondition roadCondition = new RoadCondition(channels, road);
            roadConditionMap.put(road.getId(), roadCondition);
            line = in.readLine();
        }
    }

    public void initCarOnRoad(Map<String, CarOnRoad> carOnRoadMap, Map<String, Car> carMap, List<Path> paths) {
        //paths里面的车是不是按id排序的？？？？
        for (Path path : paths) {
            String carId = path.getCarId();
            Car car = carMap.get(carId);
            //这里顺序是否会一致？
            Queue<String> carPath = path.getRoads();
            CarOnRoad carOnRoad = new CarOnRoad(null, car, CarStatus.NOTSTART,
                    -1, path.getStartTime(), carPath);
            carOnRoadMap.put(car.getId(), carOnRoad);
        }
    }
}