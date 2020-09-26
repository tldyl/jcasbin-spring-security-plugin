package org.casbin.springsecurityplugin.config;

import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class CasbinAccessDecisionManager implements AccessDecisionManager {
    @Autowired
    private Enforcer enforcer;

    @Override
    public void decide(Authentication auth, Object object, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ConfigAttribute> attributes = (List<ConfigAttribute>) collection;
        if (attributes.size() != 2) {
            throw new AccessDeniedException("You don't have access to " + object + "!");
        }
        String requestUrl = attributes.get(0).getAttribute();
        String requestMethod = attributes.get(1).getAttribute();
        if (enforcer.enforce(username, requestUrl, requestMethod)) {
            return;
        }
        throw new AccessDeniedException("You don't have access to " + object + "!");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
