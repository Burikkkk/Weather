package Controller.File;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AnaliticsData implements Serializable {
    private String period;
    private List<Regions> regions;

    // Конструктор
    public AnaliticsData(String period, List<Regions> regions) {
        this.period = period;
        this.regions = regions;
    }
    public AnaliticsData(){}

    // Геттер для period
    public String getPeriod() {
        return period;
    }

    // Сеттер для period
    public void setPeriod(String period) {
        this.period = period;
    }

    // Геттер для regions
    public List<Regions> getRegions() {
        return regions;
    }

    // Сеттер для regions
    public void setRegions(List<Regions> regions) {
        this.regions = regions;
    }
}
