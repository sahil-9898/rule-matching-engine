package com.sahil.matcher.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "application.alert")
public final class AlertConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlertConfig.class);

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.registerModule(new ParameterNamesModule());
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public final ImmutableMap<String, AlertGeneratorConfig> alertGenerators;

    public AlertConfig(final Map<String, AlertGeneratorConfig> alertGenerators) throws JsonProcessingException {
        this.alertGenerators = ImmutableMap.copyOf(alertGenerators);
        final String config = OBJECT_MAPPER.writeValueAsString(this.alertGenerators);
        LOGGER.info("Alert Config: {}", config);
    }
}
