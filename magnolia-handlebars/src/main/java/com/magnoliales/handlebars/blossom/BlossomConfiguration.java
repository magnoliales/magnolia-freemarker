package com.magnoliales.handlebars.blossom;

import com.magnoliales.handlebars.renderer.HandlebarsRenderer;
import com.magnoliales.handlebars.security.MagnoliaAuthenticationManager;
import com.magnoliales.handlebars.security.MagnoliaUserDetailsService;
import info.magnolia.module.blossom.preexecution.BlossomHandlerMapping;
import info.magnolia.module.blossom.view.TemplateViewResolver;
import info.magnolia.module.blossom.view.UuidRedirectViewResolver;
import info.magnolia.module.blossom.web.BlossomWebArgumentResolver;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.engine.RenderingEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class BlossomConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public SimpleControllerHandlerAdapter simpleControllerHandlerAdapter() {
        return new SimpleControllerHandlerAdapter();
    }

    @Bean
    @SuppressWarnings("deprecation")
    public AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter() {
        AnnotationMethodHandlerAdapter adapter = new AnnotationMethodHandlerAdapter();
        adapter.setCustomArgumentResolver(new BlossomWebArgumentResolver());
        return adapter;
    }

    @Bean
    @SuppressWarnings("deprecation")
    public DefaultAnnotationHandlerMapping defaultAnnotationHandlerMapping() {
        DefaultAnnotationHandlerMapping mapping = new DefaultAnnotationHandlerMapping();
        mapping.setUseDefaultSuffixPattern(false);
        return mapping;
    }

    @Bean
    public BeanNameUrlHandlerMapping beanNameUrlHandlerMapping() {
        return new BeanNameUrlHandlerMapping();
    }

    @Bean
    public BlossomHandlerMapping blossomHandlerMapping() {
        BlossomHandlerMapping blossomHandlerMapping = new BlossomHandlerMapping();
        AbstractUrlHandlerMapping[] targetHandlerMappings = new AbstractUrlHandlerMapping[] {
                defaultAnnotationHandlerMapping(),
                beanNameUrlHandlerMapping(),
        };
        blossomHandlerMapping.setTargetHandlerMappings(targetHandlerMappings);
        return blossomHandlerMapping;
    }

    @Bean
    public UuidRedirectViewResolver uuidRedirectViewResolver() {
        UuidRedirectViewResolver resolver = new UuidRedirectViewResolver();
        resolver.setOrder(1);
        return resolver;
    }

    @Bean
    public HandlebarsRenderer handlebarsRenderer() {
        return new HandlebarsRenderer(renderingEngine());
    }

    @Bean
    public RenderingEngine renderingEngine() {
        return Components.getComponent(RenderingEngine.class);
    }

    @Bean
    public ViewResolver templateViewResolver() {
        TemplateViewResolver resolver = new TemplateViewResolver();
        resolver.setOrder(2);
        resolver.setViewRenderer(handlebarsRenderer());
        return resolver;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return new MagnoliaAuthenticationManager();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new MagnoliaUserDetailsService();
    }
}

