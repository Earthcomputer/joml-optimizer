package net.earthcomputer.jomloptimizer;

import groovy.lang.Reference;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.*;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JomlOptimizerTask extends DefaultTask {

    private File inputJar;
    private File outputJar;

    private boolean removeConstants = false;
    private boolean modifyJomlItself = false;

    private Map<String, ClassNode> cachedClasses = new HashMap<>();
    private Set<String> modifiedClasses = new HashSet<>();

    private static final Object LOCK = new Object();

    @InputFile
    public File getInputJar() {
        return inputJar;
    }

    public void setInputJar(File inputJar) {
        this.inputJar = inputJar;
    }

    @OutputFile
    public File getOutputJar() {
        return outputJar;
    }

    public void setOutputJar(File outputJar) {
        this.outputJar = outputJar;
    }

    @Input
    public boolean getRemoveConstants() {
        return removeConstants;
    }

    public void setRemoveConstants(boolean removeConstants) {
        this.removeConstants = removeConstants;
    }

    @Input
    public boolean getModifyJomlItself() {
        return modifyJomlItself;
    }

    public void setModifyJomlItself(boolean modifyJomlItself) {
        this.modifyJomlItself = modifyJomlItself;
    }

    @TaskAction
    public void doOptimize() {
        readInputsToCache();
        if (removeConstants)
            removeConstants();
        writeOutputs();
    }

    private void readInputsToCache() {
        getProject().zipTree(inputJar).visit(file -> {
            if (file.getName().endsWith(".class")) {
                try {
                    ClassReader reader = new ClassReader(file.open());
                    ClassNode node = new ClassNode();
                    reader.accept(node, 0);
                    cachedClasses.put(node.name, node);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        });
    }

    private void writeOutputs() {
        if (!outputJar.getParentFile().isDirectory())
            outputJar.getParentFile().mkdirs();

        try {
            ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputJar)));
            getProject().zipTree(inputJar).visit(file -> {
                try {
                    zipOut.putNextEntry(new ZipEntry(file.getPath()));
                    if (!file.isDirectory()) {
                        if (!file.getName().endsWith(".class") || !modifiedClasses.contains(file.getPath().substring(0, file.getPath().length() - 6))) {
                            file.copyTo(zipOut);
                        } else {
                            ClassWriter writer = new ClassWriter(0);
                            cachedClasses.get(file.getPath().substring(0, file.getPath().length() - 6)).accept(writer);
                            zipOut.write(writer.toByteArray());
                        }
                    }
                    zipOut.closeEntry();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
            zipOut.flush();
            zipOut.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    // ===== OPTIMIZATIONS ===== //

    private void removeConstants() {
        cachedClasses.values().parallelStream().forEach(clazz -> {
            if (!modifyJomlItself && JomlClasses.isJomlClass(clazz.name))
                return;
            if (JomlClasses.isConstantClass(clazz.name))
                return;
            ClassNode newClass = new ClassNode();
            Reference<Boolean> classModified = new Reference<>(Boolean.FALSE);
            clazz.accept(new ClassRemapper(newClass, new Remapper() {
                @Override
                public String map(String typeName) {
                    if (JomlClasses.isConstantClass(typeName)) {
                        classModified.set(Boolean.TRUE);
                        return JomlClasses.getNonConstantClass(typeName);
                    }
                    return typeName;
                }
            }) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    // overridden to not map interfaces
                    this.className = name;
                    cv.visit(version, access, remapper.mapType(name), remapper
                                    .mapSignature(signature, false), remapper.mapType(superName),
                            interfaces);
                }

                @Override
                protected MethodVisitor createMethodRemapper(MethodVisitor mv) {
                    return new MethodRemapper(mv, remapper) {
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                            if (JomlClasses.isJomlClass(owner)) {
                                if (!modifyJomlItself) {
                                    mv.visitMethodInsn(opcode == Opcodes.INVOKEINTERFACE ? Opcodes.INVOKEVIRTUAL : opcode, remapper.mapType(owner), name, desc, false);
                                    Type returnType = Type.getReturnType(desc);
                                    if (returnType.getSort() == Type.OBJECT && JomlClasses.isConstantClass(returnType.getInternalName())) {
                                        mv.visitTypeInsn(Opcodes.CHECKCAST, "L" + JomlClasses.getNonConstantClass(returnType.getInternalName()) + ";");
                                    }
                                } else {
                                    if (JomlClasses.isConstantClass(owner)) {
                                        String nonConstant = JomlClasses.getNonConstantClass(owner);
                                        if (canUseField(nonConstant, name, desc)) {
                                            classModified.set(Boolean.TRUE);
                                            mv.visitFieldInsn(Opcodes.GETFIELD, nonConstant, name, Type.getReturnType(desc).getDescriptor());
                                        } else {
                                            super.visitMethodInsn(opcode == Opcodes.INVOKEINTERFACE ? Opcodes.INVOKEVIRTUAL : opcode, owner, name, desc, false);
                                        }
                                    } else {
                                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                                    }
                                }
                            } else {
                                super.visitMethodInsn(opcode, owner, name, desc, itf);
                            }
                        }

                        private boolean canUseField(String owner, String name, String desc) {
                            if (!desc.startsWith("()"))
                                return false;
                            try {
                                Class<?> clazz = Class.forName(owner.replace('/', '.'));
                                Field field = clazz.getField(name);
                                Method getter = clazz.getMethod(name);
                                return field.getType() == getter.getReturnType();
                            } catch (ReflectiveOperationException e) {
                                return false;
                            }
                        }
                    };
                }
            });
            if (classModified.get()) {
                synchronized (LOCK) {
                    modifiedClasses.add(clazz.name);
                    cachedClasses.put(clazz.name, newClass);
                }
            }
        });
    }
}
