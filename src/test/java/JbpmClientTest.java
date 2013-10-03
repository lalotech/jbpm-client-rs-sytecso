
import com.sytecso.jbpm.client.rs.JbpmRestTemplateImpl;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Eduardo
 */
public class JbpmClientTest {
    JbpmRestTemplateImpl rs = new JbpmRestTemplateImpl();    
    
    String url = "http://192.168.1.38:8080/gwt-console-server/rs/tasks/gonzalo";
    String image = "http://192.168.1.38:8080/gwt-console-server/rs/process/definition/ValidarBecas/image";
    String elroy = "http://192.168.1.38:8080/ProgramacionObraMVC/P19_5_ValidarInformacion";
    String id = "http://192.168.1.38:8080/gwt-console-server/rs/identity/sid";
    
    @Test()
    public void test()throws Exception{        
        //rs.setJSESSIONID("90dssIfm2uWMBZEQ3vXDrL1c.undefined");
        System.out.println("************   call test");       
        System.out.println(rs.get(url));
               
        
        //System.out.println("byte size: "+rs.image(image, "juan", "juan").length);
        //System.out.println("byte size: "+rs.image(image).length);
    }
    
}
