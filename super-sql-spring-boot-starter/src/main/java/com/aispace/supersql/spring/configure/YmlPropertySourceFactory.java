package com.aispace.supersql.spring.configure;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;

/**
 * @author chengjie.guo
 */
public class YmlPropertySourceFactory extends DefaultPropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        String sourceName = resource.getResource().getFilename();
        if (StrUtil.isNotBlank(sourceName) && StrUtil.endWithAny(sourceName, ".yml", ".yaml")) {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            return new PropertiesPropertySource(sourceName, factory.getObject());
        }
        return super.createPropertySource(name, resource);
    }

}