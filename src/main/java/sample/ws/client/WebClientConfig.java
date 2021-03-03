package sample.ws.client;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.common.spi.GeneratedClassClassLoaderCapture;
import org.apache.cxf.common.spi.GeneratedNamespaceClassLoader;
import org.apache.cxf.common.spi.NamespaceClassCreator;
import org.apache.cxf.endpoint.dynamic.ExceptionClassCreator;
import org.apache.cxf.endpoint.dynamic.ExceptionClassLoader;
import org.apache.cxf.jaxb.FactoryClassCreator;
import org.apache.cxf.jaxb.FactoryClassLoader;
import org.apache.cxf.jaxb.WrapperHelperClassLoader;
import org.apache.cxf.jaxb.WrapperHelperCreator;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.spi.WrapperClassCreator;
import org.apache.cxf.jaxws.spi.WrapperClassLoader;
import org.apache.cxf.wsdl.ExtensionClassCreator;
import org.apache.cxf.wsdl.ExtensionClassLoader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import sample.ws.service.Hello;

@Configuration
public class WebClientConfig {
    @Bean
    @Profile("!capture")
    public Bus cxf() {
        final Bus bus = new SpringBus();
        
        bus.setExtension(new WrapperHelperClassLoader(bus), WrapperHelperCreator.class);
        bus.setExtension(new ExtensionClassLoader(bus), ExtensionClassCreator.class);
        bus.setExtension(new ExceptionClassLoader(bus), ExceptionClassCreator.class);
        bus.setExtension(new WrapperClassLoader(bus), WrapperClassCreator.class);
        bus.setExtension(new FactoryClassLoader(bus), FactoryClassCreator.class);
        bus.setExtension(new GeneratedNamespaceClassLoader(bus), NamespaceClassCreator.class);
        
        return bus;
    }
    
    @Configuration
    @Profile("capture")
    public static class CaptureConfig {
        @Bean
        public Bus cxf(DumpingClassLoaderCapturer capturer) {
            final Bus bus = new SpringBus();
            
            bus.setExtension(capturer, GeneratedClassClassLoaderCapture.class);
            
            return bus;
        }
        
        @Bean
        public DumpingClassLoaderCapturer capturer() {
            return new DumpingClassLoaderCapturer();
        }
    }
    
    @Bean
    public Hello helloWorldClient(Bus bus){
        final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setBus(bus);
        factory.setServiceClass(Hello.class);
        factory.setAddress("http://localhost:8080/cxf/Hello");
        return factory.create(Hello.class);
    }

}
