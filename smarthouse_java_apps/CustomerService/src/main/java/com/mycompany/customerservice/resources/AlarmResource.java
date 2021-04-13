/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.customerservice.resources;

import alarmmessages.AlarmSetMessage;
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
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 *
 * @author LT
 */
@Path("alarm")
@Stateless
public class AlarmResource {

    @PersistenceContext
    EntityManager em;

    @POST
    public Response setAlarm(@Context HttpHeaders httpHeaders,
            @QueryParam("dateTime") String dateTime,
            @QueryParam("period") Integer period,
            @QueryParam("songTitle") String songTitle,
            @QueryParam("repetitionNumber") Integer repeatNum,
            @QueryParam("status") String alarmStatus) {

        int periodCheck=0;
        int repeatNumCheck=0;
        
        if(period!=null)
            periodCheck=period;

        if(repeatNum!=null)
            repeatNumCheck=repeatNum;
        
        
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        int userId = -1;
        if (authHeaderValues != null && authHeaderValues.size() > 0) {
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();
            //String password = stringTokenizer.nextToken();

            User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();

            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            userId = user.getId();
        }
        try {
            javax.naming.Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("alarmConnectionFactory");
            Queue queue = (Queue) context.lookup("AlarmQueue");
            
            JMSContext jmscontext = connectionFactory.createContext();
            JMSProducer producer = jmscontext.createProducer();

            AlarmSetMessage alarmSetMessage = new AlarmSetMessage();
            
            //String sDate6 = "31-Dec-1998 23:37:50";
            //SimpleDateFormat formatter6=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss"); 
            //Date date6=formatter6.parse(sDate6);  
            
            //String sDate = "31-02-1998 23:37:50";
            SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
            Date date=formatter.parse(dateTime);  
            alarmSetMessage.setTime(date);
            alarmSetMessage.setPeriod(periodCheck);
            alarmSetMessage.setStatus(alarmStatus);
            alarmSetMessage.setSongTitle(songTitle);
            alarmSetMessage.setTotalRepetitionCount(repeatNumCheck);
            alarmSetMessage.setUserId(userId);
            
            ObjectMessage objMessage = jmscontext.createObjectMessage(alarmSetMessage);
            producer.send(queue, objMessage);

            //producer.send(queue,song + ":" + userId);
            return Response
                    .ok().entity("Alarm for user id=" + userId + " is set.")
                    .build();
        } catch (NamingException ex) { 
            Logger.getLogger(AlarmResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(AlarmResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
