package com.example.springbootreport.container;

import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class EmbeddedTomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.jndi-name}")
    private String jndiName;

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.setContextPath("");
        factory.setPort(8081);
        factory.addContextCustomizers(context -> {
            ContextResource resource = new ContextResource();
            resource.setName(jndiName);
            resource.setType(DataSource.class.getName());
            resource.setProperty("driverClassName", driverClassName);
            resource.setProperty("odaDriverClass", driverClassName);
            resource.setProperty("username", username);
            resource.setProperty("password", password);
            resource.setProperty("url", url);

            context.getNamingResources().addResource(resource);
        });
    }

}
