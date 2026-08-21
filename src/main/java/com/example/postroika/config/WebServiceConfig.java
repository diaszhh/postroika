package com.example.postroika.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext
    ) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);

        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "materials")
    public DefaultWsdl11Definition materialsWsdl(XsdSchema materialsSchema) {
        DefaultWsdl11Definition wsdlDefinition = new DefaultWsdl11Definition();

        wsdlDefinition.setPortTypeName("MaterialsPort");
        wsdlDefinition.setLocationUri("/ws");
        wsdlDefinition.setTargetNamespace("http://postroika.example.com/soap/materials");
        wsdlDefinition.setSchema(materialsSchema);

        return wsdlDefinition;
    }

    @Bean
    public XsdSchema materialsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/materials.xsd"));
    }
}