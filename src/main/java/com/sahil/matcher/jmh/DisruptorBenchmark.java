package com.sahil.matcher.jmh;

import com.fasterxml.jackson.databind.JsonNode;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.sahil.matcher.alert.DisruptorEvent;
import com.sahil.matcher.alert.consumer.MessageProcessorConsumer;
import com.sahil.matcher.alert.consumer.RuleMatcherConsumer;
import com.sahil.matcher.message.MessageProcessor;
import com.sahil.matcher.message.json.JsonMessageProcessor;
import com.sahil.matcher.rules.FilterRuleGenerator;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
public class DisruptorBenchmark {
    private Disruptor<DisruptorEvent<String>> disruptor;
    private static final int BUFFER_SIZE = 1024;
    private static final int CONSUMER_COUNT = 4;
    private static final String MSG;

    static {
        try {
            MSG = FilterRuleGenerator.generateSampleJsonMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }


    @Setup(Level.Trial)
    public void setup() {
        final MessageProcessor<String, JsonNode> messageProcessor = new JsonMessageProcessor();
        final MessageProcessorConsumer<String, JsonNode> messageProcessorConsumer = new MessageProcessorConsumer<>(messageProcessor);
        final RuleMatcherConsumer<String>[] ruleMatcherConsumers = new RuleMatcherConsumer[CONSUMER_COUNT];

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            ruleMatcherConsumers[i] = new RuleMatcherConsumer<>(i, CONSUMER_COUNT);
        }
        disruptor = new Disruptor<>(DisruptorEvent::new, BUFFER_SIZE, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new SleepingWaitStrategy());
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
        disruptor.publishEvent((event, sequence) -> event.messageReceived(MSG, null));
    }
}
