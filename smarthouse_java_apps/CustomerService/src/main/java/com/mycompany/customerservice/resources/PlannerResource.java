/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.customerservice.resources;

import entities.User;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import planermessages.PlanerMessageRequest;
import planermessages.PlanerMessageResponse;

/**
 *
 * @author LT
 */
@Path("planner")
@Stateless
public class PlannerResource {

    @PersistenceContext
    EntityManager em;
    
    @POST
    @Path("insert")
    public Response insertInPlanner(@Context HttpHeaders httpHeaders,
            @QueryParam("destination")String destination,
            @QueryParam("duration")int duration,
            @QueryParam("startTime") String startTime,
            @QueryParam("description") String description,
            @QueryParam("setAlarm")String alarmSet){
        
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        int userId=-1;
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();

            
            User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
            
            if(user==null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            
            userId=user.getId();
        }

        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("planerConnectionFactory");
            Queue queue = (Queue) context.lookup("planerQueue");
            JMSContext jmscontext=connectionFactory.createContext();
            JMSProducer producer = jmscontext.createProducer();
            
            
            SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
            Date dateTimeStart=formatter.parse(startTime);
            
            PlanerMessageRequest planerMessageRequest=new PlanerMessageRequest();
            planerMessageRequest.setDestination(destination);
            planerMessageRequest.setDuration(duration);
            if(alarmSet.equals("yes"))
                planerMessageRequest.setSetAlarm(true);
            else planerMessageRequest.setSetAlarm(false);
            planerMessageRequest.setStartTime(dateTimeStart);
            planerMessageRequest.setUserid(userId);
            planerMessageRequest.setDescription(description);
            
            ObjectMessage objMessage=jmscontext.createObjectMessage(planerMessageRequest);
            objMessage.setStringProperty("requesttype", "insert");
            producer.send(queue, objMessage);

            return Response
                .ok().entity("Request for planer is sent for userid="+userId)
                .build();
        } catch (NamingException ex) {
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) { 
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    @POST
    @Path("update")
    public Response updatePlanner(@Context HttpHeaders httpHeaders,
            @QueryParam("id")Integer idp,
            @QueryParam("destination")String destination,
            @QueryParam("duration")Integer duration,
            @QueryParam("startTime") String startTime,
            @QueryParam("description") String description,
            @QueryParam("setAlarm")String alarmSet){
        
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        int userId=-1;
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();

            
            User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
            
            if(user==null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            
            userId=user.getId();
        }

        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("planerConnectionFactory");
            Queue queue = (Queue) context.lookup("planerQueue");
            JMSContext jmscontext=connectionFactory.createContext();
            JMSProducer producer = jmscontext.createProducer();
            
            
            SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
            Date dateTimeStart=formatter.parse(startTime);
            
            PlanerMessageRequest planerMessageRequest=new PlanerMessageRequest();
            planerMessageRequest.setPlanerId(idp);
            planerMessageRequest.setDestination(destination);
            planerMessageRequest.setDuration(duration);
            
            if(alarmSet.equals("yes"))
                planerMessageRequest.setSetAlarm(true);
            else planerMessageRequest.setSetAlarm(false);
            
            planerMessageRequest.setStartTime(dateTimeStart);
            planerMessageRequest.setUserid(userId);
            planerMessageRequest.setDescription(description);
            
            ObjectMessage objMessage=jmscontext.createObjectMessage(planerMessageRequest);
            objMessage.setStringProperty("requesttype", "update");
            producer.send(queue, objMessage);

            return Response
                .ok().entity("Request for planer is sent for userid="+userId)
                .build();
        } catch (NamingException ex) {
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) { 
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    @DELETE
    @Path("delete")
    public Response deleteFromPlanner(@Context HttpHeaders httpHeaders,
            @QueryParam("id")Integer idp){
        
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        int userId=-1;
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();

            
            User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
            
            if(user==null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            
            userId=user.getId();
        }

        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("planerConnectionFactory");
            Queue queue = (Queue) context.lookup("planerQueue");
            JMSContext jmscontext=connectionFactory.createContext();
            JMSProducer producer = jmscontext.createProducer();
            
                  
            PlanerMessageRequest planerMessageRequest=new PlanerMessageRequest();
            planerMessageRequest.setPlanerId(idp);
            planerMessageRequest.setUserid(userId);
         
            ObjectMessage objMessage=jmscontext.createObjectMessage(planerMessageRequest);
            objMessage.setStringProperty("requesttype", "delete");
            producer.send(queue, objMessage);

            return Response
                .ok().entity("Request for planer is sent for userid="+userId)
                .build();
        } catch (NamingException ex) {
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) { 
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    @GET
    public Response selectPlannerRequest(@Context HttpHeaders httpHeaders){
        
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        int userId=-1;
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();

            
            User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
            
            if(user==null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            
            userId=user.getId();
        }

        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("planerConnectionFactory");
            Queue queue = (Queue) context.lookup("planerQueue");
            JMSContext jmscontext=connectionFactory.createContext();
            JMSProducer producer = jmscontext.createProducer();
           // Topic plannerTopic = (Topic) context.lookup("plannerTopic");

           // JMSConsumer jmsconsumer = jmscontext.createSharedDurableConsumer(plannerTopic, "sub="+userId, "userId="+userId);
            
            PlanerMessageRequest planerMessageRequest=new PlanerMessageRequest();
            planerMessageRequest.setUserid(userId);
         
            ObjectMessage objMessage=jmscontext.createObjectMessage(planerMessageRequest);
            objMessage.setStringProperty("requesttype", "select");
            producer.send(queue, objMessage);
            /* 
            ObjectMessage msg = (ObjectMessage)jmsconsumer.receive();
            PlanerMessageResponse plannerResponse=(PlanerMessageResponse) msg.getObject();*/
            return Response
                .ok().entity("Request SELECT is send for userid="+userId)
                .build();
        } catch (NamingException ex) {
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) { 
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    @GET
    @Path("result")
    public Response selectPlannerResponse(@Context HttpHeaders httpHeaders){
        
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        int userId=-1;
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();

            
            User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
            
            if(user==null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            
            userId=user.getId();
        }

        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("onemoreConnectionFactory");
            JMSContext jmscontext=connectionFactory.createContext();
            Topic plannerTopic = (Topic) context.lookup("plannerTopic");
            JMSConsumer jmsconsumer = jmscontext.createSharedDurableConsumer(plannerTopic, "subsPlanner"+userId, "userId="+userId);
            
            ObjectMessage msg = (ObjectMessage)jmsconsumer.receive();
            PlanerMessageResponse plannerResponse=(PlanerMessageResponse) msg.getObject();
            //jmsconsumer.close();
            return Response
                .ok().entity(plannerResponse)
                .build();
        } catch (NamingException ex) {
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) { 
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
}
