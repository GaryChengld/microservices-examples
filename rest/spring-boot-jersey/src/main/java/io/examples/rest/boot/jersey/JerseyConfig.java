package io.examples.rest.boot.jersey;

import javax.annotation.PostConstruct;
import javax.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gary Cheng
 */
@Slf4j
@Configuration
public class JerseyConfig extends ResourceConfig implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JerseyConfig.applicationContext = applicationContext;
    }

    @PostConstruct
    public void initResources() {
        applicationContext.getBeansWithAnnotation(Path.class).values().forEach(bean -> {
            log.debug("resource class:{}", bean.getClass());
            this.register(bean.getClass());
        });
    }
}
