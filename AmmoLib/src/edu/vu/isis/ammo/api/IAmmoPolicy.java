// IAmmoPolicy.java
package edu.vu.isis.ammo.api;
/**
  See docs/java/ammo-api.nw for documentation
*/ 
import javax.xml.datatype.Duration;
import java.util.Calendar;

public interface IAmmoPolicy {
  public enum NetLinkState {
     DISCONNECTED     ("Disconnected"),
     IDLE             ("Idle"),
     SCANNING         ("Scanning"),
     CONNECTING       ("Connecting"),
     AUTHENTICATING   ("Authenticating"),
     OBTAINING_IPADDR ("Obtaining IP Address"),
     FAILED           ("Failed"),
     CONNECTED        ("Connected");

     private final String text;  
     NetLinkState(String text) {
        this.text = text;
     }
     public String text()   { return this.text; }
  }

  public enum GatewayState {
     DISCONNECTED     ("Disconnected"),
     IDLE             ("Idle"),
     AUTHENTICATING   ("Authenticating"),
     FAILED           ("Failed"),
     CONNECTED        ("Connected");

     private final String text;  
     GatewayState(String text) {
        this.text = text;
     }
     public String text()   { return this.text; }
  }
     
  public interface Identity {
     public Credential[] getCredentials();
  }
  public interface Credential { }
   
  public interface NetworkController {
     public NetLink[] getNetworkLinks();
     public Gateway[] getGateways();
     public Identity getIdentity();
  }
}

