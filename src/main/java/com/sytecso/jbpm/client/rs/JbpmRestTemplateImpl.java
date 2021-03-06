/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sytecso.jbpm.client.rs;

import java.util.Map;

/**
 *
 * @author Eduardo
 */
public class JbpmRestTemplateImpl extends JbpmRestTemplate{
    
    //public static String KEY_USERNAME = "j_username";
    //public static String KEY_PASSWORD = "j_password";
    //public static String URL_SECURITY = "/gwt-console-server/rs/identity/secure/j_security_check";
    
    private final static String USERNAME = "juan";
    private final static String PASSWORD = "juan";    

    @Override
    public String post(String url, Map<String, Object> parameters, boolean multipart) throws Exception{ 
        return this.post(url, parameters, multipart, USERNAME, PASSWORD);
    }

    @Override
    public String get(String url)throws Exception {
        return this.get(url,USERNAME, PASSWORD);
    } 

    @Override
    public byte[] image(String url) throws Exception {        
       return this.image(url,USERNAME,PASSWORD);
    }    

    public String getJSESSIONID() {
        return SESSION_ID;
    }

    public void setJSESSIONID(String SESSIONID) {
        this.SESSION_ID = SESSIONID;
    }   
    
}
