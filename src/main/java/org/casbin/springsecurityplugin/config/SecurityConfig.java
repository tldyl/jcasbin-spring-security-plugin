package org.casbin.springsecurityplugin.config;

import lombok.Cleanup;
import org.casbin.adapter.HibernateAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.main.SyncedEnforcer;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Adapter;
import org.casbin.springsecurityplugin.properties.CasbinProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

@EnableWebSecurity
@EnableConfigurationProperties(DataSourceProperties.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@ComponentScan(value = {"org.casbin.springsecurityplugin.config", "org.casbin.springsecurityplugin.properties"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CasbinProperties casbinProperties;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private CasbinSecurityMetadataSource casbinSecurityMetadataSource;

    @Autowired
    private CasbinAccessDecisionManager casbinAccessDecisionManager;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    }

    @Override
    public void configure(WebSecurity web) throws Exception {

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (casbinProperties.isEnabled()) {
            http
                    .authorizeRequests()
                    .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                        @Override
                        public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                            object.setSecurityMetadataSource(casbinSecurityMetadataSource);
                            object.setAccessDecisionManager(casbinAccessDecisionManager);
                            return object;
                        }
                    })
                    .and().csrf().disable();
        }
    }

    @Bean
    public Enforcer getEnforcer() throws NoSuchMethodException,
            IllegalAccessException,
            InstantiationException,
            IOException,
            InvocationTargetException {
        Model model = new Model();
        String modelPath = new ClassPathResource(casbinProperties.getModel()).exists()
                ? casbinProperties.getModel() : "classpath:casbin/model_request.conf";
        @Cleanup
        InputStream is = ResourceUtils.getURL(modelPath).openStream();
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[100];
        int len;
        while ((len = is.read(buf)) != -1) {
            sb.append(new String(buf, 0, len, Charset.defaultCharset()));
        }
        model.loadModelFromText(sb.toString());

        Enforcer enforcer;
        Class<? extends Enforcer> enforcerClass;

        if (casbinProperties.getSynced()) {
            enforcerClass = SyncedEnforcer.class;
        } else {
            enforcerClass = Enforcer.class;
        }
        if (dataSourceProperties != null) {
            enforcer = enforcerClass.getConstructor(Model.class, Adapter.class).newInstance(model, new HibernateAdapter(dataSourceProperties.getDriverClassName(),
                    dataSourceProperties.getUrl(),
                    dataSourceProperties.getUsername(),
                    dataSourceProperties.getPassword()));
        } else {
            enforcer = enforcerClass.newInstance();
        }

        return enforcer;
    }
}
