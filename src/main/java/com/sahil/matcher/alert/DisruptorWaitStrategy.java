package com.sahil.matcher.alert;

import com.lmax.disruptor.*;

public enum DisruptorWaitStrategy {
    BUSY_SPIN {
        @Override
        public WaitStrategy create() {
            return new BusySpinWaitStrategy();
        }
    },
    BLOCKING_WAIT {
        @Override
        public WaitStrategy create() {
            return new BlockingWaitStrategy();
        }
    },
    SLEEPING_WAIT {
        @Override
        public WaitStrategy create() {
            return new SleepingWaitStrategy();
        }
    },
    YIELDING {
        @Override
        public WaitStrategy create() {
            return new YieldingWaitStrategy();
        }
    };

    public abstract WaitStrategy create();
}
