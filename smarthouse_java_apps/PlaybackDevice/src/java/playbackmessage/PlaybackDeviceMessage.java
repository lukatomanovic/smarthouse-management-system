/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playbackmessage;

import java.io.Serializable;

/**
 *
 * @author LT
 */
public class PlaybackDeviceMessage implements Serializable{
    public enum RequestType{SEARCH_AND_PLAY,PLAYLIST}
    private RequestType requestType;
    private String songTitle;
    private int userId;

    public PlaybackDeviceMessage(RequestType requestType, String songTitle, int userId) {
        this.requestType = requestType;
        this.songTitle = songTitle;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    
    public PlaybackDeviceMessage() {
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }
    
    

}
