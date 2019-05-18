package net.earthcomputer.jomloptimizer;

import org.junit.Test;

public class ModifyJomlItselfTest {

    @Test
    public void testOptimizeJoml() {
        JomlOptimizerTask task = Common.createTask();
        task.setRemoveConstants(true);
        task.setModifyJomlItself(true);
        task.doOptimize();
    }

}
