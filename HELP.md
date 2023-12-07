# Read Me First

The following was discovered as part of building this project:

* The JVM level was changed from '11' to '17', review
  the [JDK Version Range](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-Versions#jdk-version-range)
  on the wiki for more details.

# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.4/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.4/maven-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.1.4/reference/htmlsingle/index.html#using.devtools)
* [Official Documentation of BIRT's integration](https://eclipse-birt.github.io/birt-website/docs/integrating/deapi)
* [Baeldung Guild of BIRT](https://www.baeldung.com/birt-reports-spring-boot)
* [Tutorial Spring Boot Birt repository](https://github.com/eugenp/tutorials/tree/master/spring-boot-modules/spring-boot-mvc-birt)
* 
### Setup JNDI DataSource by using Embedded Tomcat Servlet

Change config dataSource in report template file (.rpt) - using odaJndiName.

Naming jndiDataSource in application.yml.

Customize EmbeddedTomcatServlet.
* [Configuration EmbeddedServletContainer](https://www.baeldung.com/embeddedservletcontainercustomizer-configurableembeddedservletcontainer-spring-boot)
* [Spring-Boot-2.0-Migration-Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Migration-Guide#embedded-containers-package-structure)

Add missing dependency
* [NoClassDefFoundError org/apache/tomcat/dbcp/dbcp2/BasicDataSource](https://github.com/apache/shardingsphere/issues/16116)






