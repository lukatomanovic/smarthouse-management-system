/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planer;

import entities.Alarm;
import entities.Planner;
import entities.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import planermessages.PlanerMessageRequest;
import org.apache.commons.lang3.time.DateUtils;
import planermessages.PlanerMessageResponse;

/**
 *
 * @author LT
 */
public class Planer {

    static EntityManager em;

    @Resource(lookup = "planerConnectionFactory")
    static ConnectionFactory connectionFactory;

    @Resource(lookup = "planerQueue")
    static Queue queue;
    
    @Resource(lookup = "plannerTopic")
    static Topic plannerTopic;
    /**
     * @param args the command line arguments
     */
    public Planer() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("PlanerPU");
        em = entityManagerFactory.createEntityManager();
    }

    /**
     * @param args the command line arguments
     */
    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    public static Double[] getCoords(String location_adress) {
        try {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("q", location_adress);
            parameters.put("apiKey", "BSEZlkYr0nsOtvgKDvwv1FYvPm4kvaxmb9ZgpFxU01U");

            String paramsString = Planer.getParamsString(parameters);
            String url_adress = "https://geocode.search.hereapi.com/v1/geocode?" + paramsString;
            System.out.println(url_adress);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url_adress)
                    .build(); // defaults to GET
            Response response = client.newCall(request).execute();

            JSONObject jsonResponse = new JSONObject(response.body().string());
            JSONArray itemsArray = jsonResponse.getJSONArray("items");
            JSONObject firstItem = (JSONObject) itemsArray.getJSONObject(0);
            JSONObject position = firstItem.getJSONObject("position");
            Double width = position.getDouble("lat");
            Double height = position.getDouble("lng");
            System.out.println("Sirina je " + width);
            System.out.println("Visina je " + height);

            return new Double[]{width, height};
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Planer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Planer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(Planer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Integer getTravelDuration(Double[] startLocation, Double[] endLocation) {
        try {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("transportMode", "car");
            parameters.put("origin", startLocation[0] + "," + startLocation[1]);
            parameters.put("destination", endLocation[0] + "," + endLocation[1]);
            parameters.put("return", "summary");
            parameters.put("apiKey", "BSEZlkYr0nsOtvgKDvwv1FYvPm4kvaxmb9ZgpFxU01U");

            String paramsString = Planer.getParamsString(parameters);
            String url_adress = "https://router.hereapi.com/v8/routes?" + paramsString;
            System.out.println(url_adress);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url_adress)
                    .build(); // defaults to GET
            Response response = client.newCall(request).execute();

            String travelReport = response.body().string();
            System.out.println(travelReport);
            JSONObject jsonResponse = new JSONObject(travelReport);
            JSONArray routesArray = jsonResponse.getJSONArray("routes");
            JSONObject routeObject = (JSONObject) routesArray.getJSONObject(0);
            JSONArray selectionArray = routeObject.getJSONArray("sections");
            JSONObject selectionObject = (JSONObject) selectionArray.getJSONObject(0);

            JSONObject summary = selectionObject.getJSONObject("summary");

            Double duration = summary.getDouble("duration");
            Double lenght = summary.getDouble("length");
            System.out.println("Vreme trajanja putovanja u sekundama " + duration);
            System.out.println("Duzina puta u km " + lenght);

            int durationInteger = (int) Math.round(duration);
            int travelDuration = durationInteger / 60;
            if (durationInteger % 60 != 0) {
                travelDuration++;
            }
            return travelDuration;

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Planer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Planer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(Planer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean insertObligation(PlanerMessageRequest pmr) {
        User user = em.createNamedQuery("User.findById", User.class).setParameter("id", pmr.getUserid()).getResultList().get(0);
        if (pmr.getDestination() == null) {
            pmr.setDestination(user.getAddress());
        }
        List<Planner> plannerList = em.createQuery("SELECT p FROM Planner p WHERE p.userid.id=:uid ORDER BY p.time", Planner.class).setParameter("uid", pmr.getUserid()).getResultList();
        Date alarmTime = pmr.getStartTime();
        int alarmDuration = 0;
        Planner prev = null;
        Planner next = null;
        Alarm alarmNext = null;
        if (!plannerList.isEmpty()) {

            for (Planner p : plannerList) {
                /*Return Value: The function gives three return values specified below:

                    It returns the value 0 if the argument Date is equal to this Date.
                    It returns a value less than 0 if this Date is before the Date argument.
                    It returns a value greater than 0 if this Date is after the Date argument.*/
                int resComp = p.getTime().compareTo(pmr.getStartTime());
                if (resComp == 0) {
                    return false;
                }
                if (resComp > 0) {
                    next = p;
                    break;
                }
                prev = p;
            }
            if (prev != null) {
                Date timePrevStart = prev.getTime();
                Date timePrevEnd = DateUtils.addMinutes(timePrevStart, prev.getDuration());

                int travelDuration = Planer.getTravelDuration(Planer.getCoords(prev.getLocation()), Planer.getCoords(pmr.getDestination()));

                Date nextFreeSlot = DateUtils.addMinutes(timePrevEnd, travelDuration);

                if (nextFreeSlot.compareTo(pmr.getStartTime()) > 0) {
                    return false;
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(pmr.getStartTime());
                cal.add(Calendar.MINUTE, (-1) * travelDuration);
                alarmTime = cal.getTime();
                alarmDuration = travelDuration;
            }
            if (next != null) {
                int travelDuration = Planer.getTravelDuration(Planer.getCoords(pmr.getDestination()), Planer.getCoords(next.getLocation()));
                Date startObligationTime = pmr.getStartTime();
                Date endObligationTime = DateUtils.addMinutes(startObligationTime, pmr.getDuration());
                Date nextObligationTime = DateUtils.addMinutes(endObligationTime, travelDuration);
                if (nextObligationTime.compareTo(next.getTime()) > 0) {
                    return false;
                }

                //passed
                alarmNext = next.getAlarmid();
                if (alarmNext != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(next.getTime());
                    cal.add(Calendar.MINUTE, (-1) * travelDuration);
                    alarmNext.setTime(cal.getTime());
                }
            }
        } else {
            int travelDuration = Planer.getTravelDuration(Planer.getCoords(user.getAddress()), Planer.getCoords(pmr.getDestination()));
            Calendar cal = Calendar.getInstance();
            cal.setTime(pmr.getStartTime());
            cal.add(Calendar.MINUTE, (-1) * travelDuration);
            alarmTime = cal.getTime();
            alarmDuration = travelDuration;
        }
        //we can insert obligation
        Planner newPlan = new Planner();
        Alarm alarm = null;
        if (pmr.isSetAlarm()) {
            //int count=alarmDuration/5;
            alarm = new Alarm();
            //alarm.setPeriod(5);

            alarm.setPeriod(0);
            alarm.setPlannerList(new ArrayList<>());
            alarm.setRepetitioncount(0);
            alarm.setSongid(null);
            alarm.setStatus("enabled");
            alarm.setTime(alarmTime);
            //alarm.setTotalrepetitionnumber(count);
            alarm.setTotalrepetitionnumber(0);
            alarm.setUserid(user);
            em.getTransaction().begin();
            em.persist(alarm);
            em.getTransaction().commit();
        }

        newPlan.setAlarmid(alarm);
        newPlan.setDuration(pmr.getDuration());
        newPlan.setLocation(pmr.getDestination());
        newPlan.setTime(pmr.getStartTime());
        newPlan.setUserid(user);
        newPlan.setDescription(pmr.getDescription());
        em.getTransaction().begin();
        em.persist(newPlan);
        if ((next != null) && (alarmNext != null)) {
            em.persist(alarmNext);
        }
        em.getTransaction().commit();
        return true;
    }

    public boolean updateObligation(PlanerMessageRequest pmr) {
        List<Planner> planerFound = em.createNamedQuery("Planner.findById", Planner.class).setParameter("id", pmr.getPlanerId()).getResultList();
        if (planerFound.isEmpty()) {
            return false;
        }
        Planner planner = planerFound.get(0);
        User user = planner.getUserid();
        if (pmr.getDestination() == null) {
            pmr.setDestination(user.getAddress());
        }
        List<Planner> plannerList = em.createQuery("SELECT p FROM Planner p WHERE p.userid.id=:uid ORDER BY p.time", Planner.class)
                .setParameter("uid", pmr.getUserid()).getResultList();
        Date alarmTime = pmr.getStartTime();
        int alarmDuration = 0;
        Planner prev = null;
        Planner next = null;
        Planner oldPrev = null;
        Planner oldNext = null;
        boolean saveOldNext = false;
        boolean savePrev = false;
        Planner prevSaved = null;
        Alarm alarmNext = null;
        Alarm alarmDelete = null;
        if (!plannerList.isEmpty()) {

            for (Planner p : plannerList) {
                /*Return Value: The function gives three return values specified below:

                    It returns the value 0 if the argument Date is equal to this Date.
                    It returns a value less than 0 if this Date is before the Date argument.
                    It returns a value greater than 0 if this Date is after the Date argument.*/

                if (p.getId() == planner.getId()) {
                    oldPrev = prev;
                    saveOldNext = true;
                    continue;
                }

                int resComp = p.getTime().compareTo(pmr.getStartTime());
                if (resComp == 0) {
                    return false;
                }

                if (resComp > 0 && next == null) {
                    next = p;
                    savePrev = true;
                    prevSaved = prev;
                }

                if (saveOldNext) {
                    oldNext = p;
                    saveOldNext = false;
                }

                prev = p;
            }
            if (savePrev) {
                prev = prevSaved;
            }
            if (prev != null) {
                Date timePrevStart = prev.getTime();
                Date timePrevEnd = DateUtils.addMinutes(timePrevStart, prev.getDuration());

                int travelDuration = Planer.getTravelDuration(Planer.getCoords(prev.getLocation()), Planer.getCoords(pmr.getDestination()));

                Date nextFreeSlot = DateUtils.addMinutes(timePrevEnd, travelDuration);

                if (nextFreeSlot.compareTo(pmr.getStartTime()) > 0) {
                    return false;
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(pmr.getStartTime());
                cal.add(Calendar.MINUTE, (-1) * travelDuration);
                alarmTime = cal.getTime();
                alarmDuration = travelDuration;
            }
            if (next != null) {
                int travelDuration = Planer.getTravelDuration(Planer.getCoords(pmr.getDestination()), Planer.getCoords(next.getLocation()));
                Date startObligationTime = pmr.getStartTime();
                Date endObligationTime = DateUtils.addMinutes(startObligationTime, pmr.getDuration());
                Date nextObligationTime = DateUtils.addMinutes(endObligationTime, travelDuration);
                if (nextObligationTime.compareTo(next.getTime()) > 0) {
                    return false;
                }

                //passed
                alarmNext = next.getAlarmid();
                if (alarmNext != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(next.getTime());
                    cal.add(Calendar.MINUTE, (-1) * travelDuration);
                    alarmNext.setTime(cal.getTime());
                }
            }
        } else {
            int travelDuration = Planer.getTravelDuration(Planer.getCoords(user.getAddress()), Planer.getCoords(pmr.getDestination()));
            Calendar cal = Calendar.getInstance();
            cal.setTime(pmr.getStartTime());
            cal.add(Calendar.MINUTE, (-1) * travelDuration);
            alarmTime = cal.getTime();
            alarmDuration = travelDuration;
        }
        //we can edit obligation

        Alarm alarmOld = planner.getAlarmid();

        Alarm alarm = alarmOld;
        boolean commitAlarm = false;
        if (pmr.isSetAlarm() && (alarmOld == null || alarmOld.getTime().compareTo(alarmTime) != 0)) {
            //int count=alarmDuration/5;
            if (alarmOld != null) {
                alarm = alarmOld;
                alarm.setTime(alarmTime);
                alarm.setRepetitioncount(0);
            } else {
                alarm = new Alarm();
                //alarm.setPeriod(5);

                alarm.setPeriod(0);
                alarm.setPlannerList(new ArrayList<>());
                alarm.setRepetitioncount(0);
                alarm.setSongid(null);
                alarm.setStatus("enabled");
                alarm.setTime(alarmTime);
                //alarm.setTotalrepetitionnumber(count);
                alarm.setTotalrepetitionnumber(0);
                alarm.setUserid(user);
            }
            /*em.getTransaction().begin();
            em.persist(alarm);
            em.getTransaction().commit();*/
            commitAlarm = true;
        } else if (!pmr.isSetAlarm() && alarmOld != null) {
            alarmDelete = alarmOld;
            alarm=null;
            //planner.setAlarmid(null);-alarm==null
            //alarmChanged=true;
        }

        planner.setAlarmid(alarm);
        planner.setDuration(pmr.getDuration());
        planner.setLocation(pmr.getDestination());
        planner.setTime(pmr.getStartTime());
        planner.setUserid(user);
        planner.setDescription(pmr.getDescription());

        boolean nextChanged = false;
        if (oldNext != null && oldNext != next && oldNext.getAlarmid() != null) {
            String startLoc = null;
            if (oldPrev != null) {
                startLoc = oldPrev.getLocation();
            } else {
                startLoc = user.getAddress();
            }
            int travelDuration = Planer.getTravelDuration(Planer.getCoords(startLoc), Planer.getCoords(oldNext.getLocation()));
            Calendar cal = Calendar.getInstance();
            cal.setTime(oldNext.getTime());
            cal.add(Calendar.MINUTE, (-1) * travelDuration);
            oldNext.getAlarmid().setTime(cal.getTime());
            nextChanged = true;
        }

        em.getTransaction().begin();
        if (commitAlarm) {
            em.persist(alarm);
        }
        em.persist(planner);
        if (alarmDelete != null) {
            em.remove(alarmDelete);
        }
        if (nextChanged) {
            em.persist(oldNext);
        }
        em.getTransaction().commit();
        return true;
    }

    public boolean deleteObligation(PlanerMessageRequest pmr) {
        List<Planner> planerFound = em.createNamedQuery("Planner.findById", Planner.class).setParameter("id", pmr.getPlanerId()).getResultList();
        if (planerFound.isEmpty()) {
            return false;
        }
        Planner planner = planerFound.get(0);
        User user = planner.getUserid();

        List<Planner> plannerList = em.createQuery("SELECT p FROM Planner p WHERE p.userid.id=:uid ORDER BY p.time", Planner.class)
                .setParameter("uid", pmr.getUserid()).getResultList();

        Planner prev = null;
        Planner next = null;
        Planner oldPrev = null;
        Planner oldNext = null;
        boolean saveOldNext = false;

        Alarm alarmDelete = null;
        if (!plannerList.isEmpty()) {

            for (Planner p : plannerList) {
                /*Return Value: The function gives three return values specified below:

                    It returns the value 0 if the argument Date is equal to this Date.
                    It returns a value less than 0 if this Date is before the Date argument.
                    It returns a value greater than 0 if this Date is after the Date argument.*/

                if (p.getId() == planner.getId()) {
                    oldPrev = prev;
                    saveOldNext = true;
                    continue;
                }

                if (saveOldNext) {
                    oldNext = p;
                    saveOldNext = false;
                    break;
                }

                prev = p;
            }

        } else {
            return false;
        }
        //we can edit obligation

        boolean nextChanged = false;
        if (oldNext != null && oldNext != next && oldNext.getAlarmid() != null) {
            String startLoc = null;
            if (oldPrev != null) {
                startLoc = oldPrev.getLocation();
            } else {
                startLoc = user.getAddress();
            }
            int travelDuration = Planer.getTravelDuration(Planer.getCoords(startLoc), Planer.getCoords(oldNext.getLocation()));
            Calendar cal = Calendar.getInstance();
            cal.setTime(oldNext.getTime());
            cal.add(Calendar.MINUTE, (-1) * travelDuration);
            oldNext.getAlarmid().setTime(cal.getTime());
            nextChanged = true;
        }

        if (planner.getAlarmid() != null) {
            alarmDelete = planner.getAlarmid();
            planner.setAlarmid(null);
        }

        em.getTransaction().begin();

        em.remove(planner);

        if (nextChanged) {
            em.persist(oldNext);
        }

        if (alarmDelete != null) {
            em.remove(alarmDelete);
        }
        em.getTransaction().commit();
        return true;
    }

    public List<Planner> getPlaner(PlanerMessageRequest pmr) {
        List<Planner> plannerList = em.createQuery("SELECT p FROM Planner p WHERE p.userid.id=:uid ORDER BY p.time", Planner.class)
                .setParameter("uid", pmr.getUserid()).getResultList();
        return plannerList;
    }

    public static void main(String[] args) {

        /*String startLoc="Beograd";
        String endLoc="Nis";
        Double[] coordsStart = Planer.getCoords(startLoc);
        System.out.println(startLoc+"   lat>"+coordsStart[0]+" lng>"+coordsStart[1]);
        Double[] coordsEnd = Planer.getCoords(endLoc);
        System.out.println(endLoc+"   lat>"+coordsEnd[0]+" lng>"+coordsEnd[1]);
        
        Double travelDuration = Planer.getTravelDuration(coordsStart, coordsEnd);
        System.out.println("Travel duration in seconds "+travelDuration);*/
        Planer planer = new Planer();
        //playbackDevice.selectUser();
        //ytBrowser=new MyYTBrowser();

        JMSContext context = connectionFactory.createContext();
        JMSConsumer consumer = context.createConsumer(queue);
        JMSProducer producer=context.createProducer();

        while (true) {
            try {
                ObjectMessage msg = (ObjectMessage) consumer.receive();

                PlanerMessageRequest pmr = (PlanerMessageRequest) msg.getObject();
                String msgType = msg.getStringProperty("requesttype");

                if (msgType.equals("insert")) {
                    planer.insertObligation(pmr);
                } else if (msgType.equals("update")) {
                    planer.updateObligation(pmr);
                } else if (msgType.equals("delete")) {
                    planer.deleteObligation(pmr);
                }else{
                    List<Planner> planerList = planer.getPlaner(pmr);
                    PlanerMessageResponse plm=new PlanerMessageResponse(planerList);
                    ObjectMessage objMessage=context.createObjectMessage(plm);
                    objMessage.setIntProperty("userId", pmr.getUserid());
                    producer.send(plannerTopic, objMessage);
                }

            } catch (JMSException ex) {
                Logger.getLogger(Planer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
