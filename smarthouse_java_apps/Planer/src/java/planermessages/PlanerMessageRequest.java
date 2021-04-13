/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planermessages;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author LT
 */
public class PlanerMessageRequest implements Serializable {
    private Date startTime;
    private int duration;
    private String destination;
    private boolean setAlarm;
    private String songTitle;
    private int userid;
    private String description;
    
    private int planerId;

    public PlanerMessageRequest() {
    }

    public PlanerMessageRequest(Date startTime, int duration, String destination, boolean setAlarm, String songTitle, int userid, String description, int planerId) {
        this.startTime = startTime;
        this.duration = duration;
        this.destination = destination;
        this.setAlarm = setAlarm;
        this.songTitle = songTitle;
        this.userid = userid;
        this.description = description;
        this.planerId = planerId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isSetAlarm() {
        return setAlarm;
    }

    public void setSetAlarm(boolean setAlarm) {
        this.setAlarm = setAlarm;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPlanerId() {
        return planerId;
    }

    public void setPlanerId(int planerId) {
        this.planerId = planerId;
    }
    
}
