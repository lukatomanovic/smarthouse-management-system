/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author LT
 */
@Entity
@Table(name = "planner")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Planner.findAll", query = "SELECT p FROM Planner p"),
    @NamedQuery(name = "Planner.findById", query = "SELECT p FROM Planner p WHERE p.id = :id"),
    @NamedQuery(name = "Planner.findByLocation", query = "SELECT p FROM Planner p WHERE p.location = :location"),
    @NamedQuery(name = "Planner.findByTime", query = "SELECT p FROM Planner p WHERE p.time = :time"),
    @NamedQuery(name = "Planner.findByDuration", query = "SELECT p FROM Planner p WHERE p.duration = :duration"),
    @NamedQuery(name = "Planner.findByDescription", query = "SELECT p FROM Planner p WHERE p.description = :description")})
public class Planner implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 100)
    @Column(name = "location")
    private String location;
    @Basic(optional = false)
    @NotNull
    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    @Basic(optional = false)
    @NotNull
    @Column(name = "duration")
    private int duration;
    @Size(max = 100)
    @Column(name = "description")
    private String description;
    @JoinColumn(name = "alarmid", referencedColumnName = "id")
    @ManyToOne
    private Alarm alarmid;
    @JoinColumn(name = "userid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User userid;

    public Planner() {
    }

    public Planner(Integer id) {
        this.id = id;
    }

    public Planner(Integer id, Date time, int duration) {
        this.id = id;
        this.time = time;
        this.duration = duration;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Alarm getAlarmid() {
        return alarmid;
    }

    public void setAlarmid(Alarm alarmid) {
        this.alarmid = alarmid;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Planner)) {
            return false;
        }
        Planner other = (Planner) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Planner[ id=" + id + " ]";
    }
    
}
