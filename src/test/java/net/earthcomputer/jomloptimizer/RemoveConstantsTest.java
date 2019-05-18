package net.earthcomputer.jomloptimizer;

import org.junit.Test;

public class RemoveConstantsTest {

    @Test
    public void testRemoveConstants() {
        JomlOptimizerTask task = Common.createTask();
        task.setRemoveConstants(true);
        task.doOptimize();
    }

}
