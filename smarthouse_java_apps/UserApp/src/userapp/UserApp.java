/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userapp;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author LT
 */
public class UserApp {

    private String username;
    private String password;
    private String credential;
    private static int END_OPTION = 8;

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private static Document convertStringToXMLDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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

    public static int selectMenuOption(Scanner consoleReader) {

        System.out.println("1.Pusti pesmu");
        System.out.println("2.Pogledaj plejlistu");
        System.out.println("3.Navij alarm");
        System.out.println("4.Dodaj obavezu");
        System.out.println("5.Ukloni obavezu iz planera");
        System.out.println("6.Izmeni obavezu iz planera");
        System.out.println("7.Prikazi svoje obaveze");
        System.out.println("8.Iskljuci uredjaj");

        int choice = consoleReader.nextInt();
        consoleReader.nextLine();
        System.out.println("Your choice is option " + choice);
        return choice;
    }

    /**
     * @param args the command line arguments
     */
    public boolean playSong(String songTitle) {
        try {

            OkHttpClient client = new OkHttpClient();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("song", songTitle);
            String paramsString = UserApp.getParamsString(parameters);
            String url_address = "http://localhost:8080/CustomerService/api/player?" + paramsString;

            Request request = new Request.Builder()
                    .url(url_address).header("Authorization", credential)
                    .build();
            Response response = client.newCall(request).execute();
            return response.isSuccessful();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean getPlaylist() {
        try {

            OkHttpClient client = new OkHttpClient();
            String url_address = "http://localhost:8080/CustomerService/api/player/playlist";

            Request request = new Request.Builder()
                    .url(url_address).header("Authorization", credential)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return false;
            }
            url_address = "http://localhost:8080/CustomerService/api/player/playlist/response";
            request = new Request.Builder()
                    .url(url_address).header("Authorization", credential)
                    .build();
            response = client.newCall(request).execute();
            String result = response.body().string();
            if (response.isSuccessful()) {
                //System.out.println(result);
                Document doc = UserApp.convertStringToXMLDocument(result);
                Transformer transformer = TransformerFactory.newInstance().newTransformer();

                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

                //initialize StreamResult with File object to save to file
                StreamResult sr = new StreamResult(new StringWriter());

                DOMSource source = new DOMSource(doc);

                transformer.transform(source, sr);

                String xmlStringOutput = sr.getWriter().toString();

                System.out.println(xmlStringOutput);
                return true;
            }

            return false;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean setAlarm(String startTime, Integer period, String songTitle, Integer repetitionNumber, String status) {
        try {
            OkHttpClient client = new OkHttpClient();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("dateTime", startTime);
            parameters.put("period", "" + period);
            parameters.put("songTitle", songTitle);
            parameters.put("repetitionNumber", "" + repetitionNumber);
            parameters.put("status", status);

            RequestBody formBody = new FormBody.Builder()
                    .add("dateTime", "startTime")
                    .add("period", "" + period)
                    .add("songTitle", songTitle)
                    .add("repetitionNumber", "" + repetitionNumber)
                    .add("status", status)
                    .build();
            String paramsString = UserApp.getParamsString(parameters);
            String url_address = "http://localhost:8080/CustomerService/api/alarm?" + paramsString;

            Request request = new Request.Builder()
                    .url(url_address).post(formBody).header("Authorization", credential)
                    .build();
            Response response = client.newCall(request).execute();
            return response.isSuccessful();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean insertPlanner(String destination, int duration, String startTime, String description, String alarmSet) {
        try {
            OkHttpClient client = new OkHttpClient();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("destination", destination);
            parameters.put("duration", "" + duration);
            parameters.put("startTime", startTime);
            parameters.put("description", description);
            parameters.put("setAlarm", alarmSet);

            RequestBody formBody = new FormBody.Builder()
                    .add("destination", destination)
                    .add("duration", "" + duration)
                    .add("startTime", startTime)
                    .add("description", description)
                    .add("setAlarm", alarmSet)
                    .build();
            String paramsString = UserApp.getParamsString(parameters);
            String url_address = "http://localhost:8080/CustomerService/api/planner/insert?" + paramsString;

            Request request = new Request.Builder()
                    .url(url_address).post(formBody).header("Authorization", credential)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                getUserPlanner();
            }
            return response.isSuccessful();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updatePlanner(Integer planerId, String destination, Integer duration, String startTime, String description, String alarmSet) {
        try {
            OkHttpClient client = new OkHttpClient();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("id", "" + planerId);
            parameters.put("destination", destination);
            parameters.put("duration", "" + duration);
            parameters.put("startTime", startTime);
            parameters.put("description", description);
            parameters.put("setAlarm", alarmSet);

            RequestBody formBody = new FormBody.Builder()
                    .add("id", "" + planerId)
                    .add("destination", destination)
                    .add("duration", "" + duration)
                    .add("startTime", startTime)
                    .add("description", description)
                    .add("setAlarm", alarmSet)
                    .build();
            String paramsString = UserApp.getParamsString(parameters);
            String url_address = "http://localhost:8080/CustomerService/api/planner/update?" + paramsString;

            Request request = new Request.Builder()
                    .url(url_address).post(formBody).header("Authorization", credential)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                getUserPlanner();
            }
            return response.isSuccessful();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deletePlanner(Integer planerId) {
        try {
            OkHttpClient client = new OkHttpClient();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("id", "" + planerId);
            /*RequestBody formBody = new FormBody.Builder()
                    .add("id", ""+planerId)     
                    .build();*/
            String paramsString = UserApp.getParamsString(parameters);
            String url_address = "http://localhost:8080/CustomerService/api/planner/delete?" + paramsString;

            Request request = new Request.Builder()
                    .url(url_address).delete().header("Authorization", credential)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                getUserPlanner();
            }
            return response.isSuccessful();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean getUserPlanner() {
        try {

            OkHttpClient client = new OkHttpClient();
            String url_address = "http://localhost:8080/CustomerService/api/planner";

            Request request = new Request.Builder()
                    .url(url_address).header("Authorization", credential)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return false;
            }
            url_address = "http://localhost:8080/CustomerService/api/planner/result";
            request = new Request.Builder()
                    .url(url_address).header("Authorization", credential)
                    .build();
            response = client.newCall(request).execute();
            String result = response.body().string();
            if (response.isSuccessful()) {
                //System.out.println(result);
                Document doc = UserApp.convertStringToXMLDocument(result);
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                StreamResult sr = new StreamResult(new StringWriter());
                DOMSource source = new DOMSource(doc);
                transformer.transform(source, sr);
                String xmlStringOutput = sr.getWriter().toString();
                System.out.println(xmlStringOutput);
                return true;
            }

            return false;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void main(String[] args) {
        UserApp userApp = new UserApp();
        Scanner consoleReader = new Scanner(System.in);
        boolean hasCredentials = false;
        String username = null;
        String password = null;
        String credentialSent = null;
        while (!hasCredentials) {
            try {
                System.out.println("Enter username:");
                username = consoleReader.nextLine();
                System.out.println("Enter password:");
                password = consoleReader.nextLine();

                String url_address = "http://localhost:8080/CustomerService/api/javaee8";
                OkHttpClient client = new OkHttpClient();
                credentialSent = Credentials.basic(username, password);
                Request request = new Request.Builder()
                        .url(url_address).header("Authorization", credentialSent)
                        .build();
                Response response = client.newCall(request).execute();
                hasCredentials = response.isSuccessful();
            } catch (IOException ex) {
                Logger.getLogger(UserApp.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        userApp.setUsername(username);
        userApp.setPassword(password);
        userApp.setCredential(credentialSent);
        System.out.println("Login is Successful! Welcome " + username + "!");

        int option = END_OPTION;
        do {

            option = UserApp.selectMenuOption(consoleReader);

            switch (option) {
                case 1: {
                    System.out.println("Enter song title:");
                    String songTitle = consoleReader.nextLine();
                    if (userApp.playSong(songTitle)) {
                        System.out.println("Pesma je pustena");
                    } else {
                        System.out.println("Pesmu nije moguce pronaci!");
                    }
                    break;
                }
                case 2: {
                    if (!userApp.getPlaylist()) {
                        System.out.println("Plejlistu nije moguce dohvatiti!");
                    }
                    break;
                }
                case 3: {
                    //String startTime,int period,String songTitle,int repetitionNumber, String status
                    System.out.println("Enter start time(example DD-MM-YYYY HH:MM:SS): ");
                    String startTime = consoleReader.nextLine();
                    System.out.println("Enter repetition period: ");
                    Integer period = consoleReader.nextInt();
                    consoleReader.nextLine();
                    System.out.println("Enter song title: ");
                    String songTitle = consoleReader.nextLine();
                    System.out.println("Enter repetition count: ");
                    Integer repetitionNumber = consoleReader.nextInt();
                    consoleReader.nextLine();
                    System.out.println("Enter status: ");
                    String status = consoleReader.nextLine();
                    userApp.setAlarm(startTime, period, songTitle, repetitionNumber, status);
                    break;
                }

                case 4: {
                    //String destination,int duration,String startTime,String description
                    System.out.println("Enter destination: ");
                    String destination = consoleReader.nextLine();
                    System.out.println("Enter duration: ");
                    Integer duration = consoleReader.nextInt();
                    consoleReader.nextLine();
                    System.out.println("Enter start time(example DD-MM-YYYY HH:MM:SS): ");
                    String startTime = consoleReader.nextLine();
                    System.out.println("Enter description: ");
                    String description = consoleReader.nextLine();
                    System.out.println("Set alarm [yes/no]: ");
                    String alarmSet = consoleReader.nextLine();
                    userApp.insertPlanner(destination, duration, startTime, description, alarmSet);
                    break;
                }
                case 5: {
                    System.out.println("Enter planner id: ");
                    Integer planerId = consoleReader.nextInt();
                    consoleReader.nextLine();
                    userApp.deletePlanner(planerId);
                    break;
                }
                case 6: {
                    System.out.println("Enter planner id: ");
                    Integer planerId = consoleReader.nextInt();
                    consoleReader.nextLine();
                    System.out.println("Enter destination: ");
                    String destination = consoleReader.nextLine();
                    System.out.println("Enter duration: ");
                    Integer duration = consoleReader.nextInt();
                    consoleReader.nextLine();
                    System.out.println("Enter start time(example DD-MM-YYYY HH:MM:SS): ");
                    String startTime = consoleReader.nextLine();
                    System.out.println("Enter description: ");
                    String description = consoleReader.nextLine();
                    System.out.println("Set alarm [yes/no]: ");
                    String alarmSet = consoleReader.nextLine();
                    userApp.updatePlanner(planerId, destination, duration, startTime, description, alarmSet);
                    break;
                }
                case 7: {
                    //prikaz obaveza
                    userApp.getUserPlanner();
                    break;
                }
                case 8: {
                    System.out.println("Aplikacija je zavrsila sa radom!");
                    return;
                }

            }
        } while (option != END_OPTION);

    }

}
