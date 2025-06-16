package com.sahil.matcher.alert;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.sahil.matcher.config.AlertConfig;
import com.sahil.matcher.config.AlertGeneratorConfig;
import com.sahil.matcher.message.json.JsonMessageProcessor;
import com.sahil.matcher.rules.AlertRuleIndexManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public final class AlertGenerators {
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final ImmutableMap<String, AlertGenerator<?, ?>> generators;

    public AlertGenerators(final AlertConfig alertConfig, final AlertRuleIndexManager alertRuleIndexManager) {
        final var builder = ImmutableMap.<String, AlertGenerator<?, ?>>builder();

        alertConfig.alertGenerators.forEach((name, config) -> {
            final AlertGenerator<?, ?> alertGenerator = switch (config.messageType()) {
                case JSON -> createAlertGeneratorForJsonMessage(config, alertRuleIndexManager);
            };
            builder.put(name, alertGenerator);
        });

        this.generators = builder.build();
        this.startAlertGenerators();
    }

    private AlertGenerator<String, JsonNode> createAlertGeneratorForJsonMessage(
            final AlertGeneratorConfig config, final AlertRuleIndexManager alertRuleIndexManager) {
        return new AlertGenerator<>(config, new JsonMessageProcessor(), alertRuleIndexManager);
    }

    private void startAlertGenerators() {
        generators.values().forEach(AlertGenerator::startDisruptor);
        this.generators.forEach((generatorName, generator) -> this.executorService.submit(() -> {
            try {
                generator.start();
            } catch (final Exception e) {
                throw new RuntimeException("Error while starting alert generator " + generatorName, e);
            }
        }));
    }
}
