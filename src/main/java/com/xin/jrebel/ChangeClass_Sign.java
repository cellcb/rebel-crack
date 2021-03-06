package com.xin.jrebel;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.ASM4;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IRETURN;

/**
 * @author linxixin@cvte.com
 */
public class ChangeClass_Sign {


    /**
     * 因为密钥验证类名几乎每次都会修改, 因此那这个类的某个变量的字符串做标示
     *
     * @param classByte
     * @return
     */
    public static boolean validateLdc(byte[] classByte) {
        ClassReader classReader = new ClassReader(classByte);
        ClassNode cn = new ClassNode();
        classReader.accept(cn, 0);
        for (int i = 0; i < cn.methods.size(); i++) {
            MethodNode method = (MethodNode) cn.methods.get(i);
            if (method.localVariables == null) {
                continue;
            }
            ListIterator iterator = method.instructions.iterator();
            while (iterator.hasNext()) {
                Object next = iterator.next();

                if (next instanceof LdcInsnNode) {
                    if ("RSADigestSigner not initialised for signature generation.".equals(((LdcInsnNode) next).cst)) {
                        System.out.println("jrebel里面的密钥文件是: " + classReader.getClassName());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int time = 0;

    public static boolean validate(byte[] classByte) {
        return validateLdc(classByte);
    }

    public static byte[] crackContextValidate(byte[] classByte) throws Exception {
        time++;
        if (time > 1) {
            System.err.println("超出一次破解了, 有点异常");
        }
        ClassReader classReader = new ClassReader(classByte);
        ClassWriter classWriter = new ClassWriter(classReader, COMPUTE_MAXS);
        classReader.accept(new ChangeClassAdapter(classWriter), ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }


    private static class ChangeClassAdapter extends ClassVisitor {
        public ChangeClassAdapter(ClassVisitor cv) {
            super(ASM4);
            this.cv = cv;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

            // 当是sayName方法是做对应的修改
            if (name.equals("a") && desc.contains("([B)Z")) {
                MethodVisitor newMv = new ChangeMethodAdapter(mv);
                return newMv;
            } else {
                return mv;
            }
        }

        private class ChangeMethodAdapter extends MethodVisitor {
            public ChangeMethodAdapter(MethodVisitor mv) {
                super(ASM4);
                this.mv = mv;
            }


            @Override
            public void visitAttribute(Attribute attr) {
            }

            @Override
            public void visitCode() {
                super.visitCode();
                mv.visitInsn(ICONST_1);
                mv.visitInsn(IRETURN);
            }

            @Override
            public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
            }

            @Override
            public void visitInsn(int opcode) {
            }

            @Override
            public void visitIntInsn(int opcode, int operand) {
            }

            @Override
            public void visitVarInsn(int opcode, int var) {
            }

            @Override
            public void visitTypeInsn(int opcode, String type) {
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            }

            @Override
            public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
            }

            @Override
            public void visitJumpInsn(int opcode, Label label) {
            }

            @Override
            public void visitLabel(Label label) {
            }

            @Override
            public void visitLdcInsn(Object cst) {
            }

            @Override
            public void visitIincInsn(int var, int increment) {
            }

            @Override
            public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
            }


            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            }


            @Override
            public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            }

            @Override
            public void visitMultiANewArrayInsn(String desc, int dims) {
            }


            @Override
            public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            }


            @Override
            public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
            }

            @Override
            public void visitLineNumber(int line, Label start) {
            }

            @Override
            public void visitMaxs(int maxStack, int maxLocals) {
            }

            @Override
            public void visitEnd() {
            }

            @Override
            public AnnotationVisitor visitAnnotationDefault() {
                return super.visitAnnotationDefault();
            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return super.visitAnnotation(desc, visible);
            }


            @Override
            public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
                return super.visitParameterAnnotation(parameter, desc, visible);
            }

        }
    }


}



