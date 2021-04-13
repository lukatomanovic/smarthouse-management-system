/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author LT
 */
@Entity
@Table(name = "alarm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Alarm.findAll", query = "SELECT a FROM Alarm a"),
    @NamedQuery(name = "Alarm.findById", query = "SELECT a FROM Alarm a WHERE a.id = :id"),
    @NamedQuery(name = "Alarm.findByTime", query = "SELECT a FROM Alarm a WHERE a.time = :time"),
    @NamedQuery(name = "Alarm.findByPeriod", query = "SELECT a FROM Alarm a WHERE a.period = :period"),
    @NamedQuery(name = "Alarm.findByStatus", query = "SELECT a FROM Alarm a WHERE a.status = :status"),
    @NamedQuery(name = "Alarm.findByRepetitioncount", query = "SELECT a FROM Alarm a WHERE a.repetitioncount = :repetitioncount"),
    @NamedQuery(name = "Alarm.findByTotalrepetitionnumber", query = "SELECT a FROM Alarm a WHERE a.totalrepetitionnumber = :totalrepetitionnumber")})
public class Alarm implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    @Column(name = "period")
    private Integer period;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "status")
    private String status;
    @Column(name = "repetitioncount")
    private Integer repetitioncount;
    @Column(name = "totalrepetitionnumber")
    private Integer totalrepetitionnumber;
    @JoinColumn(name = "songid", referencedColumnName = "id")
    @ManyToOne
    private Songs songid;
    @JoinColumn(name = "userid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User userid;
    @OneToMany(mappedBy = "alarmid")
    private List<Planner> plannerList;

    public Alarm() {
    }

    public Alarm(Integer id) {
        this.id = id;
    }

    public Alarm(Integer id, Date time, String status) {
        this.id = id;
        this.time = time;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRepetitioncount() {
        return repetitioncount;
    }

    public void setRepetitioncount(Integer repetitioncount) {
        this.repetitioncount = repetitioncount;
    }

    public Integer getTotalrepetitionnumber() {
        return totalrepetitionnumber;
    }

    public void setTotalrepetitionnumber(Integer totalrepetitionnumber) {
        this.totalrepetitionnumber = totalrepetitionnumber;
    }

    public Songs getSongid() {
        return songid;
    }

    public void setSongid(Songs songid) {
        this.songid = songid;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

    @XmlTransient
    public List<Planner> getPlannerList() {
        return plannerList;
    }

    public void setPlannerList(List<Planner> plannerList) {
        this.plannerList = plannerList;
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
        if (!(object instanceof Alarm)) {
            return false;
        }
        Alarm other = (Alarm) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Alarm[ id=" + id + " ]";
    }
    
}
