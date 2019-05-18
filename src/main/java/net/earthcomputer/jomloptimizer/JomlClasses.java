package net.earthcomputer.jomloptimizer;

import java.util.HashMap;
import java.util.Map;

public class JomlClasses {

    private static final String PACKAGE = "org/joml/";

    private static final Map<String, String> CONSTANT_CLASSES = new HashMap<>();

    static {
        constant("Matrix3dc", "Matrix3d");
        constant("Matrix3fc", "Matrix3f");
        constant("Matrix3x2dc", "Matrix3x2d");
        constant("Matrix3x2fc", "Matrix3x2f");
        constant("Matrix4dc", "Matrix4d");
        constant("Matrix4fc", "Matrix4f");
        constant("Matrix4x3dc", "Matrix4x3d");
        constant("Matrix4x3fc", "Matrix4x3f");
        constant("Quaterniondc", "Quaterniond");
        constant("Quaternionfc", "Quaternionf");
        constant("Vector2dc", "Vector2d");
        constant("Vector2fc", "Vector2f");
        constant("Vector2ic", "Vector2i");
        constant("Vector3dc", "Vector3d");
        constant("Vector3fc", "Vector3f");
        constant("Vector3ic", "Vector3i");
        constant("Vector4dc", "Vector4d");
        constant("Vector4fc", "Vector4f");
        constant("Vector4ic", "Vector4i");
    }

    private static void constant(String constant, String nonConstant) {
        CONSTANT_CLASSES.put(PACKAGE + constant, PACKAGE + nonConstant);
    }


    public static boolean isJomlClass(String className) {
        return className.startsWith(PACKAGE);
    }

    public static boolean isConstantClass(String className) {
        return CONSTANT_CLASSES.containsKey(className);
    }

    public static String getNonConstantClass(String className) {
        return CONSTANT_CLASSES.get(className);
    }

}
