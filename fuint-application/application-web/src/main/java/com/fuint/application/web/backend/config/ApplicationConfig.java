package com.fuint.application.web.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * 加载applicationContext.xml
 * Created by Administrator on 2017/2/22.
 */
@Configuration
@ImportResource("classpath:/applicationContext.xml")
public class ApplicationConfig {
}
