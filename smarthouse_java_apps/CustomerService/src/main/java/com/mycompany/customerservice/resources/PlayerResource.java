/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.customerservice.resources;

import entities.User;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import playbackmessage.PlaybackDeviceMessage;
import playbackmessage.PlaylistMessage;
/**
 *
 * @author LT
 */
@Path("player")
@Stateless
public class PlayerResource {
    @PersistenceContext
    EntityManager em;
    
    
    
    public class RequestSender extends Thread {
        List<String> result=null;
        int userId;
  
        public RequestSender(int userId) {
            this.userId = userId;
         
        }
        
        @Override
        public void run(){
            try {
                javax.naming.Context context = new InitialContext();
                ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
                Queue queue = (Queue) context.lookup("PlaybackDeviceQueue");
           // Queue playlistQueue = (Queue) context.lookup("PlaylistQueue");

                JMSContext jmscontext=connectionFactory.createContext();
                JMSProducer producer = jmscontext.createProducer();
                producer.setDeliveryDelay(0);
           // JMSConsumer jmsconsumer = jmscontext.createConsumer(playlistQueue);
                        
                PlaybackDeviceMessage pdMessage=new PlaybackDeviceMessage(PlaybackDeviceMessage.RequestType.PLAYLIST, null,userId);
               // Posiljka posiljka2=new Posiljka(TipArtikla.values()[tip], (int) (Math.random()*6+5));
                ObjectMessage objMessage=jmscontext.createObjectMessage(pdMessage);
                producer.send(queue, objMessage);
                
                jmscontext.commit();
                
            } catch (NamingException ex) {
                Logger.getLogger(PlayerResource.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    @GET
    public Response play(@Context HttpHeaders httpHeaders,@QueryParam("song")String song){
        
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        int userId=-1;
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();
            //String password = stringTokenizer.nextToken();
            
            User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
            
            if(user==null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            
            userId=user.getId();
        }

        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("smartHouseConnectionFactory");
            Queue queue = (Queue) context.lookup("PlaybackDeviceQueue");
            if (song == null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            JMSContext jmscontext=connectionFactory.createContext();
            JMSProducer producer = jmscontext.createProducer();
            
            
            PlaybackDeviceMessage pdMessage=new PlaybackDeviceMessage(PlaybackDeviceMessage.RequestType.SEARCH_AND_PLAY, song,userId);
               // Posiljka posiljka2=new Posiljka(TipArtikla.values()[tip], (int) (Math.random()*6+5));
            ObjectMessage objMessage=jmscontext.createObjectMessage(pdMessage);
            producer.send(queue, objMessage);
            
            //producer.send(queue,song + ":" + userId);
            return Response
                .ok().entity("Song is added to user id="+userId+" playlist.")
                .build();
        } catch (NamingException ex) {
            Logger.getLogger(PlayerResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }
    
    
    @GET
    @Path("playlist")
    public Response getPlaylist(@Context HttpHeaders httpHeaders){
        
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        int userId=-1;
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();
            //String password = stringTokenizer.nextToken();
            
            User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
            
            if(user==null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            
            userId=user.getId();
        }

        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
            Queue queue = (Queue) context.lookup("PlaybackDeviceQueue");
           // Queue playlistQueue = (Queue) context.lookup("PlaylistQueue");

            JMSContext jmscontext=connectionFactory.createContext();
            JMSProducer producer = jmscontext.createProducer();

           // JMSConsumer jmsconsumer = jmscontext.createConsumer(playlistQueue);
                        
            PlaybackDeviceMessage pdMessage=new PlaybackDeviceMessage(PlaybackDeviceMessage.RequestType.PLAYLIST, null,userId);
               // Posiljka posiljka2=new Posiljka(TipArtikla.values()[tip], (int) (Math.random()*6+5));
            ObjectMessage objMessage=jmscontext.createObjectMessage(pdMessage);
            producer.send(queue, objMessage);
                

            
           /* 
            System.out.println("Message sent");

 
            ObjectMessage msg = (ObjectMessage)jmsconsumer.receive();             
            PlaylistMessage pl=(PlaylistMessage) msg.getObject();*/
            //List<String> playlist=getPlaylistResponse();
            
            return Response.status(Response.Status.OK).build();    
        }  catch (NamingException ex) {
            Logger.getLogger(PlayerResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    @GET
    @Path("playlist/response")
    public Response getPlaylistResponse(@Context HttpHeaders httpHeaders){
        
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        int userId=-1;
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();
            //String password = stringTokenizer.nextToken();
            
            User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
            
            if(user==null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            
            userId=user.getId();
        }

        //List<String> playlist=null;
        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
            Topic playlistTopic = (Topic) context.lookup("testPD");
            
            JMSContext jmscontext=connectionFactory.createContext();
            JMSConsumer jmsconsumer = jmscontext.createSharedDurableConsumer(playlistTopic, "subPD"+userId, "userId="+userId);
            
            ObjectMessage msg = (ObjectMessage)jmsconsumer.receive();
            PlaylistMessage pl=(PlaylistMessage) msg.getObject();
            //playlist=pl.getSongs();
            return Response.status(Response.Status.OK).entity(pl).build(); 
        } catch (NamingException ex) {
            Logger.getLogger(PlayerResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(PlayerResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
            
        //return Response.status(Response.Status.OK).entity(new GenericEntity<List<String>>(playlist){}).build();    
        return Response.status(Response.Status.BAD_REQUEST).build();    
        
    }
    
    
     @GET
    @Path("clean")
    public Response clean(@Context HttpHeaders httpHeaders){
        
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        int userId=-1;
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();
            //String password = stringTokenizer.nextToken();
            
            User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
            
            if(user==null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            
            userId=user.getId();
        }

        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
            Queue queue = (Queue) context.lookup("PlaybackDeviceQueue");
            Queue playlistQueue = (Queue) context.lookup("PlaylistQueue");

            JMSContext jmscontext=connectionFactory.createContext();
            JMSProducer producer = jmscontext.createProducer();
            JMSConsumer jmsconsumer = jmscontext.createConsumer(playlistQueue);
                        
            PlaybackDeviceMessage pdMessage=new PlaybackDeviceMessage(PlaybackDeviceMessage.RequestType.PLAYLIST, null,userId);
               // Posiljka posiljka2=new Posiljka(TipArtikla.values()[tip], (int) (Math.random()*6+5));
            ObjectMessage objMessage=jmscontext.createObjectMessage(pdMessage);
            //producer.send(queue, objMessage);
            
            System.out.println("Message sent");

 
            ObjectMessage msg = (ObjectMessage)jmsconsumer.receive();             
            PlaylistMessage pl=(PlaylistMessage) msg.getObject();
            List<String> playlist=pl.getSongs();
            
            return Response.status(Response.Status.OK).entity(new GenericEntity<List<String>>(playlist){}).build();    
        } catch (NamingException ex) {
            Logger.getLogger(PlayerResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JMSException ex) {
            Logger.getLogger(PlayerResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    
    @GET
    @Path("testiranje")
    public Response getPlaylistTest(@Context HttpHeaders httpHeaders){
        
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        int userId=-1;
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();
            //String password = stringTokenizer.nextToken();
            
            User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
            
            if(user==null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            
            userId=user.getId();
        }

        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("anotherConnectionFactory");
            Queue queue = (Queue) context.lookup("PlaybackDeviceQueue");
            Queue playlistQueue = (Queue) context.lookup("PlaylistQueue");

            JMSContext jmscontext=connectionFactory.createContext();
            JMSProducer producer = jmscontext.createProducer();

           
                        
            PlaybackDeviceMessage pdMessage=new PlaybackDeviceMessage(PlaybackDeviceMessage.RequestType.PLAYLIST, null,userId);
               // Posiljka posiljka2=new Posiljka(TipArtikla.values()[tip], (int) (Math.random()*6+5));
            ObjectMessage objMessage=jmscontext.createObjectMessage(pdMessage);
            producer.send(queue, objMessage);
                
            System.out.println("Message sent");
            
            ConnectionFactory connectionFactory2 = (ConnectionFactory) context.lookup("onemoreConnectionFactory");
            JMSContext jmscontext2=connectionFactory2.createContext();

            Topic playlistTopic = (Topic) context.lookup("playlistTopic");
            JMSConsumer jmsconsumer = jmscontext2.createConsumer(playlistTopic,"userId="+userId, false);

            List<String> playlist=null;
            
            ObjectMessage msg = (ObjectMessage)jmsconsumer.receive();             
            PlaylistMessage pl=(PlaylistMessage) msg.getObject();
            playlist=pl.getSongs();

            return Response.status(Response.Status.OK).entity(new GenericEntity<List<String>>(playlist){}).build();    
        }  catch (NamingException ex) {
            Logger.getLogger(PlayerResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(PlayerResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    public List<String> getPlaylistResponse(){
        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
            Queue playlistQueue = (Queue) context.lookup("PlaylistQueue");
            
            JMSContext jmscontext=connectionFactory.createContext();
            JMSConsumer jmsconsumer = jmscontext.createConsumer(playlistQueue);
            
            ObjectMessage msg = (ObjectMessage)jmsconsumer.receive();
            PlaylistMessage pl=(PlaylistMessage) msg.getObject();
            List<String> playlist=pl.getSongs();
            return playlist;
        } catch (NamingException ex) {
            Logger.getLogger(PlayerResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(PlayerResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
