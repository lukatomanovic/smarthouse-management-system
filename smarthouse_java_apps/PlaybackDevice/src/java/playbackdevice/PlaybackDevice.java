/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playbackdevice;

import entities.Playlist;
import entities.Songs;
import entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import playbackmessage.PlaybackDeviceMessage;
import playbackmessage.PlaylistMessage;
import yt.MyYTBrowser;
import yt.YTSearchResult;


/**
 *
 * @author LT
 */
//@Stateless
public class PlaybackDevice {

    
    // @PersistenceContext(unitName = "unit1")
    static EntityManager em;
    
   @Resource(lookup = "smartHouseConnectionFactory")
   static ConnectionFactory connectionFactory;
    
   @Resource(lookup = "PlaybackDeviceQueue")
   static Queue queue;
   
   @Resource(lookup = "PlaylistQueue")
   static Queue queueReport;
    
   @Resource(lookup = "testPD")
   static Topic playlistTopic;
   
   
   static MyYTBrowser ytBrowser;
    /**
     * @param args the command line arguments
     */
   
   
    public PlaybackDevice(){
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("PlaybackDevicePU");
        em = entityManagerFactory.createEntityManager();
    }

    
    
    private void searchAndPlay(String songTitle, int userId) {
        
        YTSearchResult ytSearchResult=ytBrowser.runBrowser(songTitle);
        Songs song=null;
        if(ytSearchResult!=null){
            // manager.getTransaction().begin();
            em.getTransaction().begin();
            List<User> users=em.createNamedQuery("User.findById", User.class).setParameter("id", userId).getResultList();
            if(users.size()==0){
                System.out.println("User does not exists!");
                return;
            }
            User user=users.get(0);
            
       
            List<Songs> songsFoundList = em.createNamedQuery("Songs.findByUrl", Songs.class).setParameter("url", ytSearchResult.getUrl()).getResultList();
            if(songsFoundList.size()==0){
                song=new Songs();
                song.setTitle(ytSearchResult.getSongTitle());
                song.setUrl(ytSearchResult.getUrl());
                song.setPlaylistList(new ArrayList<>());
                em.persist(song);
            }
            else{
                song=songsFoundList.get(0);
            }
            Playlist playlist =new Playlist();
            playlist.setUserid(user);
            playlist.setSongid(song);

            em.persist(playlist);
            //  manager.getTransaction().commit();*/
            em.getTransaction().commit();
        }
    }
    
    private List<String> getUserPlaylist(int userId){
        em.getTransaction().begin();
       /* List<User> users=em.createNamedQuery("User.findById", User.class).setParameter("id", userId).getResultList();
        if(users.size()==0){
            System.out.println("User does not exists!");
            return null;
        }
        User user=users.get(0);*/
        TypedQuery<Playlist> query = em.createQuery("SELECT pl FROM Playlist pl WHERE pl.userid.id=:id",Playlist.class);
        query.setParameter("id", userId);
        List<Playlist> playlist = query.getResultList();
        em.getTransaction().commit();
        if(playlist.isEmpty())return null;
        List<String> result=new ArrayList<>();
        for(Playlist pl:playlist){
            result.add(pl.getSongid().getTitle());
        }    
        return result;
    }
    
    public static void main(String[] args) {

        PlaybackDevice playbackDevice=new PlaybackDevice();

        
        JMSContext context = connectionFactory.createContext();
        JMSConsumer consumer = context.createConsumer(queue);
        JMSProducer producer=context.createProducer();
        
        while (true) {
            try {
                
                ObjectMessage msg = (ObjectMessage)consumer.receive();
              /*  if (!(msg instanceof PlaybackDeviceMessage)){
                    System.out.println("Request is not recognized!");
                    continue;
                }*/
                
                PlaybackDeviceMessage pd=(PlaybackDeviceMessage) msg.getObject();
                if(pd.getRequestType()==PlaybackDeviceMessage.RequestType.SEARCH_AND_PLAY)
                    playbackDevice.searchAndPlay(pd.getSongTitle(),pd.getUserId());
                else{
                    List<String> lista=playbackDevice.getUserPlaylist(pd.getUserId());
                    PlaylistMessage plm=new PlaylistMessage(lista, pd.getUserId());
                    ObjectMessage objMessage=context.createObjectMessage(plm);
                    objMessage.setIntProperty("userId", pd.getUserId());
                    producer.send(playlistTopic, objMessage);
                }
                

            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.getLocalizedMessage());
                break;
            }
        }
    }

    
}
