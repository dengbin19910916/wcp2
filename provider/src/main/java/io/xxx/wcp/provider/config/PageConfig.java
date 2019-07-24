package io.xxx.wcp.provider.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
public class PageConfig extends WebMvcConfigurationSupport {

    private static final int PMP_MAX_PAGE_SIZE = 10_000;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setMaxPageSize(PMP_MAX_PAGE_SIZE);
        argumentResolvers.add(resolver);
        super.addArgumentResolvers(argumentResolvers);
    }
}
