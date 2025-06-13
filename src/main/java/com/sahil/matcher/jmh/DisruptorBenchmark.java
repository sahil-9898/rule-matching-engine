package com.sahil.matcher.jmh;

import com.fasterxml.jackson.databind.JsonNode;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.sahil.matcher.alert.DisruptorEvent;
import com.sahil.matcher.alert.consumer.MessageProcessorConsumer;
import com.sahil.matcher.alert.consumer.RuleMatcherConsumer;
import com.sahil.matcher.message.MessageProcessor;
import com.sahil.matcher.message.json.JsonMessageProcessor;
import com.sahil.matcher.rules.RuleSet;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
public class DisruptorBenchmark {
    private Disruptor<DisruptorEvent<String>> disruptor;
    private static final int BUFFER_SIZE = 1024;
    private static final int CONSUMER_COUNT = 2;
    private static final String MSG = "{}";

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }


    @Setup(Level.Trial)
    public void setup() {
        final RuleSet ruleSet = new RuleSet(List.of());
        final MessageProcessor<String, JsonNode> messageProcessor = new JsonMessageProcessor();
        final MessageProcessorConsumer<String, JsonNode> messageProcessorConsumer = new MessageProcessorConsumer<>(messageProcessor, ruleSet);
        final RuleMatcherConsumer<String>[] ruleMatcherConsumers = new RuleMatcherConsumer[CONSUMER_COUNT];

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            ruleMatcherConsumers[i] = new RuleMatcherConsumer<>(ruleSet, i, CONSUMER_COUNT);
        }
        disruptor = new Disruptor<>(DisruptorEvent::new, BUFFER_SIZE, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new BlockingWaitStrategy());
        disruptor.handleEventsWith(messageProcessorConsumer).then(ruleMatcherConsumers);
        disruptor.start();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        disruptor.shutdown();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void disruptorBenchmark() {
        disruptor.publishEvent((event, sequence) -> event.message = MSG);
    }
}
