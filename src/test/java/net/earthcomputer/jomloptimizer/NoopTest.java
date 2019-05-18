package net.earthcomputer.jomloptimizer;

import org.junit.Test;

public class NoopTest {

    @Test
    public void testNoop() {
        JomlOptimizerTask task = Common.createTask();
        task.doOptimize();
    }

}
