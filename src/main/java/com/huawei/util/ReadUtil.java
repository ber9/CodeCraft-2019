package main.java.com.huawei.util;

import main.java.com.huawei.entity.Car;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadUtil {
    /**
     * 
     * @param carFilename
     * @return
     * @throws IOException
     */
    public List<Car> readCarFile(String carFilename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(carFilename));
        String line = in.readLine();
        line = in.readLine();
        List<Car> cars = new ArrayList<>();
        while (line != null){
            String[] carDescription = line.substring(1, line.length() - 1).split(",");
            cars.add(new Car(carDescription[0].trim(),carDescription[1].trim(),carDescription[2].trim(),Integer.parseInt(carDescription[3].trim()),Integer.parseInt(carDescription[4].trim() )));
            line = in.readLine();
        }
        return cars;
    }
}
