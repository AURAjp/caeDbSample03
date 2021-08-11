package rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("api")
public class WebapiConfig extends ResourceConfig {
    public WebapiConfig() {
        packages(this.getClass().getPackage().getName());
    }
}