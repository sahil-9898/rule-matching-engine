package com.sahil.matcher.config;

import com.sahil.matcher.alert.DisruptorWaitStrategy;

public record DisruptorConfig(int ringBufferSize, int ruleMatcherConsumers, DisruptorWaitStrategy waitStrategy) {
}
