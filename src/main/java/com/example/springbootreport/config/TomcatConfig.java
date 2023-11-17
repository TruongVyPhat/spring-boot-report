package main.java.com.example.springbootreport.config;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
//import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
//import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
public class TomcatConfig {

    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {

        return new TomcatServletWebServerFactory() {

            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                tomcat.enableNaming();
                return super.getTomcatWebServer(tomcat);
            }

            @Override
            protected void postProcessContext(Context context)
            {
                ContextResource resource = new ContextResource();

                resource.setType(DataSource.class.getName());
                resource.setName("jdbc/AMLDS_ITP");
                resource.setProperty("factory", "org.apache.tomcat.jdbc.pool.DataSourceFactory");
                resource.setProperty("driverClassName", "org.postgresql.Driver");
                resource.setProperty("url", "jdbc:postgresql://localhost:5432/realshit?schema=public");
                resource.setProperty("username", "postgres");
                resource.setProperty("password", "postgres");

                context.getNamingResources().addResource(resource);
            }
        };
    }

    @Bean
    public DataSource jndiDataSource() throws IllegalArgumentException, NamingException
    {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:/comp/env/jdbc/AMLDS_ITP");
        bean.setProxyInterface(DataSource.class);
        bean.setLookupOnStartup(false);
        bean.afterPropertiesSet();

        return (DataSource) bean.getObject();
    }
}
