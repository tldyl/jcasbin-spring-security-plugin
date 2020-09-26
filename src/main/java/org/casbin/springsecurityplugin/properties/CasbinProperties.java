package org.casbin.springsecurityplugin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(CasbinProperties.PREFIX)
@Component
public class CasbinProperties {
    public static final String PREFIX = "spring-security-jcasbin";

    private boolean enabled = true;

    private String model = "classpath:casbin/model_request.conf";

    private String ruleTable = "casbin_rule";

    private Boolean synced = false;
}
