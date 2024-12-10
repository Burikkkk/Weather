package Controller.File;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import java.io.Serializable;
import java.util.Map;

public class Regions implements Serializable {
    private String name;  // Добавлено поле name
    private Map<String, Object> regionData;

    // Конструктор с параметрами для инициализации name и regionData
    public Regions(String name, Map<String, Object> regionData) {
        this.name = name;
        this.regionData = regionData;
    }

    // Конструктор без параметров (пустой)
    public Regions() {}

    // Геттер для name
    public String getName() {
        return name;
    }

    // Сеттер для name
    public void setName(String name) {
        this.name = name;
    }

    // Геттер для regionData
    public Map<String, Object> getRegionData() {
        return regionData;
    }

    // Сеттер для regionData
    public void setRegionData(Map<String, Object> regionData) {
        this.regionData = regionData;
    }
}
