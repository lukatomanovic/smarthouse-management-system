/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yt;

/**
 *
 * @author LT
 */
public class YTSearchResult {
    private String songTitle;
    private String url;

    public YTSearchResult(String songTitle, String url) {
        this.songTitle = songTitle;
        this.url = url;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
