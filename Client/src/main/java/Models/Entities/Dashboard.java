package Models.Entities;

import java.io.Serializable;


public class Dashboard implements Serializable {

    private Integer id;
    private Day startDate;
    private Day endDate;
    private User user;

    public Dashboard() {}

    public Dashboard(Integer id, Day startDate, Day endDate, User user) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }
    public Dashboard(Day startDate, Day endDate, User user) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Day getStartDate() {
        return startDate;
    }

    public void setStartDate(Day startDate) {
        this.startDate = startDate;
    }

    public Day getEndDate() {
        return endDate;
    }

    public void setEndDate(Day endDate) {
        this.endDate = endDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

