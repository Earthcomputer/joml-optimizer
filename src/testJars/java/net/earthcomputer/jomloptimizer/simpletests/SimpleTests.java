package net.earthcomputer.jomloptimizer.simpletests;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class SimpleTests {

    private static Vector3fc createConstantVector() {
        return new Vector3f(1, 2, 3);
    }

    public static float getConstantHash() {
        Vector3fc v = createConstantVector();
        return v.x() + 31 * (v.y() + 31 * v.z());
    }

}
