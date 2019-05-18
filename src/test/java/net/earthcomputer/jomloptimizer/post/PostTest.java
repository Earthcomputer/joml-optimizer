package net.earthcomputer.jomloptimizer.post;

import net.earthcomputer.jomloptimizer.simpletests.SimpleTests;
import org.junit.Test;
import static org.junit.Assert.*;

public class PostTest {

    @Test
    public void testHash() {
        assertEquals(2946, SimpleTests.getConstantHash(), 0);
    }

    @Test
    public void testLength() {
        assertEquals(Math.sqrt(14), SimpleTests.getLength(), 1e-5);
    }

    @Test
    public void test4x4MatrixSum() {
        assertEquals(4, SimpleTests.sum4x4MatrixIdentity(), 0);
    }

}
