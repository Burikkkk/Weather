package Models.Entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "dashboard")
public class Dashboard implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "start_date_id", referencedColumnName = "id", nullable = false)
    private Day startDate;

    @ManyToOne
    @JoinColumn(name = "end_date_id", referencedColumnName = "id", nullable = false)
    private Day endDate;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
    private User user;

    // Constructors
    public Dashboard() {}

    public Dashboard(Integer id, Day startDate, Day endDate, User user) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }

    // Getters and Setters
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

