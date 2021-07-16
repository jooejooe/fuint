package com.fuint.application.web.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * 加载 applicationContext.xml
 * Created by FSQ
 * Contact wx fsq_better
 */
@Configuration
@ImportResource("classpath:/applicationContext.xml")
public class ApplicationConfig {
}
