/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author dhara
 */
public class Weather {
    private double tempf;
    private double feelsf;
    private double tempc;
    private double feelsc;
    private double precip;
    private double maxtempf;

    public double getMaxtempf() {
        return maxtempf;
    }

    public void setMaxtempf(double maxtempf) {
        this.maxtempf = maxtempf;
    }

    public double getMintempf() {
        return mintempf;
    }

    public void setMintempf(double mintempf) {
        this.mintempf = mintempf;
    }

    public double getMaxtempc() {
        return maxtempc;
    }

    public void setMaxtempc(double maxtempc) {
        this.maxtempc = maxtempc;
    }

    public double getMintempc() {
        return mintempc;
    }

    public void setMintempc(double mintempc) {
        this.mintempc = mintempc;
    }
    private double mintempf;
    private double maxtempc;
    private double mintempc;
    private BufferedImage img =null;

    public double getTempf() {
        return tempf;
    }

    public void setTempf(int tempf) {
        this.tempf = tempf;
    }

    public double getFeelsf() {
        return feelsf;
    }

    public void setFeelsf(int feelsf) {
        this.feelsf = feelsf;
    }

    public double getTempc() {
        return tempc;
    }

    public void setTempc(int tempc) {
        this.tempc = tempc;
    }

    public double getFeelsc() {
        return feelsc;
    }

    public void setFeelsc(int feelsc) {
        this.feelsc = feelsc;
    }

    public double getPrecip() {
        return precip;
    }

    public void setPrecip(double precip) {
        this.precip = precip;
    }

    public BufferedImage getImg() {
        return img;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }
    
    
    JSONObject json = null;
    
    public Weather(String zip){
        this.getWeatherDetails(zip);
        
    }
    public void getWeatherDetails(String zip) {
        try {
            String urlQ = "http://api.wunderground.com/api/ad5cc3e92651695e/forecast/geolookup/conditions/q/"+zip+".json";
            
            try {
                URL url = new URL(urlQ);
                
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }
                
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()),"UTF-8"));
                
//            String line =null;
                String output;
                output = IOUtils.toString(br);
//            br.readLine();
//            System.out.println("Output from Server .... \n");
//            while ((a = br.readLine()) != null) {
//                output.concat(a);
//            }
                br.close();
                System.out.print(output);
                
                json = new JSONObject(output);
                //json =(JSONObject) JSONSerializer.toJSON(output);
                
                conn.disconnect();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            feelsc = json.getJSONObject("current_observation").getDouble("feelslike_c");
            feelsf = json.getJSONObject("current_observation").getDouble("feelslike_f");
            precip = json.getJSONObject("current_observation").getDouble("precip_today_in");
            tempc = json.getJSONObject("current_observation").getDouble("temp_c");
            tempf = json.getJSONObject("current_observation").getDouble("temp_f");
            maxtempc = json.getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday").
                    getJSONObject(0).getJSONObject("high").getDouble("celsius");
            maxtempf = json.getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday").
                    getJSONObject(0).getJSONObject("high").getDouble("fahrenheit");
            mintempc =json.getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday").
                    getJSONObject(0).getJSONObject("low").getDouble("celsius");
            mintempf =json.getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday").
                    getJSONObject(0).getJSONObject("low").getDouble("fahrenheit");
            
            try{
                URL url = new URL(json.getJSONObject("current_observation").getString("icon_url"));
                img = ImageIO.read(url);
            } catch (IOException ex) {
                Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (JSONException ex) {
            Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    

    private SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        String serverURI = "http://ws.cdyne.com/";
        SOAPEnvelope envelope = soapPart.getEnvelope();

        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("GetCityWeatherByZIP");
        QName attributeName = new QName("xmlns");
        soapBodyElem.addAttribute(attributeName, "http://ws.cdyne.com/WeatherWS/");
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("ZIP");
        soapBodyElem1.addTextNode("02215");
        soapMessage.saveChanges();
        return soapMessage;

    }

    private void getSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceContent, result);
    }

}
