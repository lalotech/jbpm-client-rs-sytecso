/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sytecso.jbpm.client.rs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

/**
 *
 * @author Eduardo
 */
abstract class JbpmRestTemplate {
    
    Logger log = Logger.getLogger(this.getClass());

    // keep this out of the method in order to reuse the object for calling other services without losing session    
    private DefaultHttpClient httpClient = new DefaultHttpClient();
    //gwt-console-server/rs/identity/secure/j_security_check";
    public static String KEY_USERNAME = "j_username";
    public static String KEY_PASSWORD = "j_password";
    public static String URL_SECURITY = "/gwt-console-server/rs/identity/secure/j_security_check";    
    public String SESSION_ID = "";

    private String authenticate(String address, String username, String password) throws Exception {
        log.info("--- Authenticate");
        //  new NameValuePair("j_username", username)
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(KEY_USERNAME, username));
        formparams.add(new BasicNameValuePair(KEY_PASSWORD, password));

        HttpPost httpPost = new HttpPost(address);

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        //UrlEncodedFormEntity entity=new UrlEncodedFormEntity(formparams, "multipart/form-data");
        httpPost.setEntity(entity);
        /*if(!this.SESSION_ID.equals("")){
            httpPost.setHeader("Cookie", "JSESSIONID="+SESSION_ID);  
            System.out.println("Authenticate JSESSIONID BROWSER: "+SESSION_ID);
        }*/
        HttpResponse response = httpClient.execute(httpPost);
        showHeaders(response);
        return read(response.getEntity().getContent());
    }

    private String requestPost(String url, Map<String, Object> parameters, boolean multipart) throws Exception {
        log.info("--- POST");
        MultipartEntity multiPartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
        if (parameters == null) {
            parameters = new HashMap<String, Object>();
        }
        Set<String> keys = parameters.keySet();
        for (String keyString : keys) {
            String value = parameters.get(keyString).toString();
            formparams.add(new BasicNameValuePair(keyString, value));
            if (multipart) {
                try {
                    StringBody stringBody = new StringBody(value, Charset.forName("UTF-8"));
                    //System.out.println(stringBody.);
                    multiPartEntity.addPart(keyString, (ContentBody) stringBody);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        HttpPost httpPost = new HttpPost(url);
        if (multipart) {
            httpPost.setEntity(multiPartEntity);
        } else {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");// new UrlEncodedFormEntity(formparams, "multipart/form-data");
            ////
            httpPost.setEntity(entity);
        }
        /*if(!this.SESSION_ID.equals("")){
            httpPost.setHeader("Cookie", "JSESSIONID="+SESSION_ID); 
        }*/
        HttpResponse response = httpClient.execute(httpPost);
        return read(response.getEntity().getContent());       
    }

    private String requestGet(String url) throws Exception {
        log.info("--- GET");
        HttpGet httpGet = new HttpGet(url);
        /*if(!this.SESSION_ID.equals("")){
            httpGet.setHeader("Cookie", "JSESSIONID="+SESSION_ID); 
        }*/
        HttpResponse response = httpClient.execute(httpGet);
        showHeaders(response);
        return read(response.getEntity().getContent());
    }

    private byte[] requestImage(String url) throws Exception {
        log.info("--- IMAGE");
        HttpGet httpGet = new HttpGet(url);
        /*if(!this.SESSION_ID.equals("")){
            httpGet.setHeader("Cookie", "JSESSIONID="+SESSION_ID); 
        }*/
        HttpResponse response = httpClient.execute(httpGet);
        log.info("image size: " + response.getEntity().getContentLength());
        BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(response.getEntity());
        return IOUtils.toByteArray(bufferedHttpEntity.getContent());
    }

    private String getUrlAutenticate(String default_url) throws Exception {
        ///gwt-console-server/rs/identity/secure/j_security_check";        
        return "http://" + default_url.split("//")[1].split("\\/")[0] + URL_SECURITY;
    }

    private String read(InputStream is) throws Exception {
        String responseString;
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder stringBuilder = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            stringBuilder.append(line);
            line = br.readLine();
        }
        responseString = stringBuilder.toString();
        isr.close();
        br.close();
        return responseString;
    }

    public String post(String url, Map<String, Object> parameters, boolean multipart, String username, String password) throws Exception {
        checkAuthentication(url, username, password);
        return requestPost(url, parameters, multipart);
    }

    public String get(String url, String username, String password) throws Exception {
        checkAuthentication(url, username, password);
        return requestGet(url);
    }

    public byte[] image(String url, String username, String password) throws Exception {
        checkAuthentication(url, username, password);
        return requestImage(url);
    }
    private void checkAuthentication(String url,String username,String password)throws Exception{
        //if(getCookies().isEmpty()){
            //Authenticate is cookies is empty
            //log.info("no Authentication found!");
            requestGet(url);
            authenticate(getUrlAutenticate(url), username, password);
        //}
    }
    public List<Cookie> getCookies() {
        List<Cookie> cl = httpClient.getCookieStore().getCookies();
        //System.out.println("Cookies: "+cl.size());
        for (Cookie c : cl) {
            log.info(c.getName() + "=" + c.getValue());
        }
        return cl;
    }   
    public void showHeaders(HttpResponse response){
        for(Header h:response.getAllHeaders()){
            log.info(h.getName()+"="+h.getValue());
        }
    }
    public void validateJsessionId() throws Exception{
        if(this.SESSION_ID.equals("")){
            throw new Exception("JSESSION ID NOT FOUND - SYTECSO");
        }
    }

    /**
     * Use this method for general username and password impl
     *
     * @param url
     * @param parameters
     * @param multipart
     * @return
     */
    public abstract String post(String url, Map<String, Object> parameters, boolean multipart) throws Exception;

    /**
     * Use this method for general username and password impl
     *
     * @param url
     * @param parameters
     * @param multipart
     * @return
     */
    public abstract String get(String url) throws Exception;

    /**
     * Use this method for general username and password impl
     *
     * @param url
     * @return
     * @throws Exception
     */
    public abstract byte[] image(String url) throws Exception;

}
