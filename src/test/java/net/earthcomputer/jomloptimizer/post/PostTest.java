package net.earthcomputer.jomloptimizer.post;

import net.earthcomputer.jomloptimizer.simpletests.SimpleTests;
import org.junit.Test;
import static org.junit.Assert.*;

public class PostTest {

    @Test
    public void postTest() {
        assertEquals(2946, SimpleTests.getConstantHash(), 0);
    }

}
