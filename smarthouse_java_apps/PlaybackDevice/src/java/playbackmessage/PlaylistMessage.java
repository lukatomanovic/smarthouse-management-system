/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playbackmessage;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author LT
 */
@XmlRootElement(name="playlist")
public class PlaylistMessage implements Serializable{
    private List<String> songs;
    private int userId;

    public PlaylistMessage(List<String> songs, int userId) {
        this.songs = songs;
        this.userId = userId;
    }

    public PlaylistMessage() {
    }


    @XmlElementWrapper(name="songs")
    @XmlElement(name="song")
    public List<String> getSongs() {
        return songs;
    }

    public void setSongs(List<String> songs) {
        this.songs = songs;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    
}
