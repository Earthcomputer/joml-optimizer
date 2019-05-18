package net.earthcomputer.jomloptimizer.simpletests;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class SimpleTests {

    private static Vector3fc createConstantVector() {
        return new Vector3f(1, 2, 3);
    }

    private static Matrix4fc createConstantMatrix() {
        return new Matrix4f();
    }

    public static float getConstantHash() {
        Vector3fc v = createConstantVector();
        return v.x() + 31 * (v.y() + 31 * v.z());
    }

    public static float getLength() {
        return createConstantVector().length();
    }

    public static float sum4x4MatrixIdentity() {
        Matrix4fc mat = createConstantMatrix();
        return mat.m00() + mat.m01() + mat.m02() + mat.m03()
                + mat.m10() + mat.m11() + mat.m12() + mat.m13()
                + mat.m20() + mat.m21() + mat.m22() + mat.m23()
                + mat.m30() + mat.m31() + mat.m32() + mat.m33();
    }

}
