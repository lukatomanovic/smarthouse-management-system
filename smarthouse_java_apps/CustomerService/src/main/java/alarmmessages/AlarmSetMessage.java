/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarmmessages;

import java.io.Serializable;
import java.util.Date;


/**
 *
 * @author LT
 */
public class AlarmSetMessage implements Serializable{
    
    private Date time;
    private int period;
    private String status;
    private String songTitle;
    private int userId;
    private int totalRepetitionCount;

    public AlarmSetMessage(Date time, int period, String status, String songTitle, int userId, int totalRepetitionCount) {
        this.time = time;
        this.period = period;
        this.status = status;
        this.songTitle = songTitle;
        this.userId = userId;
        this.totalRepetitionCount = totalRepetitionCount;
    }

    public AlarmSetMessage() {
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTotalRepetitionCount() {
        return totalRepetitionCount;
    }

    public void setTotalRepetitionCount(int totalRepetitionCount) {
        this.totalRepetitionCount = totalRepetitionCount;
    }

    

}
