/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarm;

import alarmmessages.AlarmSetMessage;
import entities.Alarm;
import entities.Songs;
import entities.User;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import yt.MyYTBrowser;
import yt.YTSearchResult;

/**
 *
 * @author LT
 */
public class AlarmNotifier {

    static EntityManager em;
    static int refreshInterval=60000;//60s in ms

    @Resource(lookup = "alarmConnectionFactory")
    static ConnectionFactory connectionFactory;

    @Resource(lookup = "AlarmQueue")
    static Queue queue;

    private JMSContext context;
    private JMSConsumer consumer;

    public AlarmNotifier() {
        em = Persistence.createEntityManagerFactory("AlarmDevicePU").createEntityManager();
        context = connectionFactory.createContext();
        consumer = context.createConsumer(queue);
        consumer.setMessageListener((message) -> {
            
            try {
                em.getTransaction().begin();
                ObjectMessage msg = (ObjectMessage) message;
                AlarmSetMessage asm = (AlarmSetMessage) msg.getObject();
                Songs song = null;
                if (!asm.getSongTitle().isEmpty()) {
                    //pretraga
                    YTSearchResult searchResult = MyYTBrowser.searchYT(asm.getSongTitle());
                    List<Songs> songsFoundList = em.createNamedQuery("Songs.findByUrl", Songs.class).setParameter("url", searchResult.getUrl()).getResultList();
                    if (songsFoundList.size() == 0) {
                        song = new Songs();
                        song.setTitle(searchResult.getSongTitle());
                        song.setUrl(searchResult.getUrl());
                        song.setPlaylistList(new ArrayList<>());
                        em.persist(song);
                        em.getTransaction().commit();
                        em.getTransaction().begin();
                    } else {
                        song = songsFoundList.get(0);
                    }
                }

                

                List<User> usersFoundList = em.createNamedQuery("User.findById", User.class).setParameter("id", asm.getUserId()).getResultList();
                
                if (usersFoundList.isEmpty()) {
                    em.getTransaction().rollback();
                    return;
                }
                User user = usersFoundList.get(0);
                    
                

                TypedQuery<Alarm> query = em.createQuery("SELECT a FROM Alarm a WHERE a.userid=:user AND a.time=:dt "
                        + "AND a.period=:interval AND a.status=:stat", Alarm.class);
                query.setParameter("user", user);
                //query.setParameter("song", song);
                query.setParameter("dt", asm.getTime());
                query.setParameter("interval", asm.getPeriod());
                //query.setParameter("repeatcounter", asm.getTotalRepetitionCount());
                query.setParameter("stat", asm.getStatus());
                List<Alarm> alarmsFound = query.getResultList();
                Alarm alarmCreated=null;
                if (!alarmsFound.isEmpty()) {
                    alarmCreated = alarmsFound.get(0);
                    alarmCreated.setRepetitioncount(0);
                    if (asm.getTotalRepetitionCount() > alarmCreated.getTotalrepetitionnumber()) {
                        alarmCreated.setTotalrepetitionnumber(asm.getTotalRepetitionCount());
                    }
                    alarmCreated.setSongid(song);
                } else {
                    alarmCreated = new Alarm();
                    alarmCreated.setTime(asm.getTime());
                    alarmCreated.setPeriod(asm.getPeriod());
                    alarmCreated.setStatus(asm.getStatus());
                    alarmCreated.setSongid(song);
                    alarmCreated.setUserid(user);
                    alarmCreated.setRepetitioncount(0);
                    alarmCreated.setTotalrepetitionnumber(asm.getTotalRepetitionCount());
                }
                em.persist(alarmCreated);
                em.getTransaction().commit();
            } catch (JMSException ex) {
                em.getTransaction().rollback();
                Logger.getLogger(AlarmNotifier.class.getName()).log(Level.SEVERE, null, ex);
            }         

        });
    }

    /*public class Notifier extends Thread {

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    //TypedQuery<Alarm> query = em.createQuery("SELECT pl FROM Playlist pl WHERE pl.userid.id=:id",Playlist.class);
                    Thread.sleep(60000);//sleep for 1 minute
                } catch (InterruptedException ex) {
                    Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }*/
    /*public void insertAlarm() {
        em.getTransaction().begin();
        Alarm a = new Alarm();
        a.setPeriod(2);
        Calendar cal = Calendar.getInstance();
        java.sql.Timestamp timestamp = new java.sql.Timestamp((cal.getTimeInMillis() / 60000) * 60000);//60000 je zbog zaokruzivanja
        a.setTime(timestamp);
        a.setTotalrepetitionnumber(2);
        a.setRepetitioncount(1);
        a.setStatus("enabled");
        em.persist(a);
        em.getTransaction().commit();
    }*/

    public void selectAlarm() {
        Calendar cal = Calendar.getInstance();
        java.sql.Timestamp timestamp = new java.sql.Timestamp((cal.getTimeInMillis() / 60000) * 60000);//60000 je zbog zaokruzivanja
        Query nq = em.createNativeQuery("SELECT * FROM smarthouse.alarm WHERE (status='enabled') AND ((DATE_SUB(now(), INTERVAL repetitioncount*period MINUTE)-time) between 0 AND 60)", Alarm.class);
        List<Alarm> alarms = nq.getResultList();
        //if(!em.getTransaction().isActive())
        em.getTransaction().begin();
        for (Alarm a : alarms) {
            int currCount = a.getRepetitioncount();
            int totalCount = a.getTotalrepetitionnumber();
            if (currCount == totalCount) {
                a.setStatus("disabled");
            } else {
                a.setRepetitioncount(a.getRepetitioncount() + 1);
            }
            em.persist(a);
        }
        em.getTransaction().commit();
        
        for(Alarm a:alarms){
            Songs ringtone=a.getSongid();
            String url_address=null;
            if(ringtone!=null){
                try {
                    url_address=ringtone.getUrl();
                    URL url = new URL(url_address);
                    Desktop desktop = Desktop.getDesktop();
                    desktop.browse(url.toURI());
                } catch (MalformedURLException ex) {
                    Logger.getLogger(AlarmNotifier.class.getName()).log(Level.SEVERE, null, ex);
                } catch (URISyntaxException ex) {
                    Logger.getLogger(AlarmNotifier.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(AlarmNotifier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else{
                try {
                    File defaultSong = new File("defaultalarm.mp3");
                    Desktop.getDesktop().browse(defaultSong.toURI());
                } catch (IOException ex) {
                    Logger.getLogger(AlarmNotifier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        // TypedQuery<Alarm> query = em.createQuery("SELECT a FROM Alarm a WHERE :dt=DATEADD(minute, a.period*a.repetitioncount, a.time)",Alarm.class);
        // query.setParameter("dt", timestamp);
        // List<Alarm> alarms = query.getResultList();
    }

    public static void main(String[] args) {
        AlarmNotifier af = new AlarmNotifier();
        //af.insertAlarm();
        //af.selectAlarm();
        
        while(true){
            
            try {
                af.selectAlarm();
                System.out.println("alarm.AlarmNotifier.main()");
                Thread.sleep(refreshInterval);
            } catch (InterruptedException ex) {
                Logger.getLogger(AlarmNotifier.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
