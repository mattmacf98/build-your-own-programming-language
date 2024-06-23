import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class J0Machine {
    public static byte[] code;
    public static byte[] stack;
    public static ByteBuffer codeBuffer;
    public static ByteBuffer stackBuffer;

    public static int instructionPtr;
    public static int stackPtr;
    public static int basePointer;
    public static byte op;
    public static byte opr;
    public static long operand;

    public static void init(String filename) throws IOException {
        instructionPtr = 0;
        stackPtr = 0;

        if (!loadBytecode(filename)) {
            System.err.println("cannot open " + filename);
            System.exit(1);
        }

        instructionPtr = 16;
        instructionPtr = (int) (8 * getOperand());
        stack = new byte[800000];
        stackBuffer = ByteBuffer.wrap(stack);
    }

    public static void interpret() {
        while (true) {
            fetch();
            switch (op) {
                case Op.HALT:
                    stop("Execution complete");
                    break;
                case Op.ADD:
                    long addVal1 = stackBuffer.getLong(stackPtr--);
                    long addVal2 = stackBuffer.getLong(stackPtr--);
                    stackBuffer.putLong(stackPtr++, addVal1 + addVal2);
                    break;
                case Op.SUB:
                    long subVal1 = stackBuffer.getLong(stackPtr--);
                    long subVal2 = stackBuffer.getLong(stackPtr--);
                    stackBuffer.putLong(stackPtr++, subVal1 - subVal2);
                    break;
                case Op.MUL:
                    long mulVal1 = stackBuffer.getLong(stackPtr--);
                    long mulVal2 = stackBuffer.getLong(stackPtr--);
                    stackBuffer.putLong(stackPtr++, mulVal1 * mulVal2);
                    break;
                case Op.DIV:
                    long divVal1 = stackBuffer.getLong(stackPtr--);
                    long divVal2 = stackBuffer.getLong(stackPtr--);
                    stackBuffer.putLong(stackPtr++, divVal1 / divVal2);
                    break;
                case Op.MOD:
                    long modVal1 = stackBuffer.getLong(stackPtr--);
                    long modVal2 = stackBuffer.getLong(stackPtr--);
                    stackBuffer.putLong(stackPtr++, modVal1 % modVal2);
                    break;
                case Op.LT:
                    long ltVal1 = stackBuffer.getLong(stackPtr--);
                    long ltVal2 = stackBuffer.getLong(stackPtr--);
                    stackBuffer.putLong(stackPtr++, ltVal1 < ltVal2 ? 1 : 0);
                    break;
                case Op.LE:
                    long leVal1 = stackBuffer.getLong(stackPtr--);
                    long leVal2 = stackBuffer.getLong(stackPtr--);
                    stackBuffer.putLong(stackPtr++, leVal1 <= leVal2 ? 1 : 0);
                    break;
                case  Op.GT:
                    long gtVal1 = stackBuffer.getLong(stackPtr--);
                    long gtVal2 = stackBuffer.getLong(stackPtr--);
                    stackBuffer.putLong(stackPtr++, gtVal1 > gtVal2 ? 1 : 0);
                    break;
                case Op.GE:
                    long geVal1 = stackBuffer.getLong(stackPtr--);
                    long geVal2 = stackBuffer.getLong(stackPtr--);
                    stackBuffer.putLong(stackPtr++, geVal1 >= geVal2 ? 1 : 0);
                    break;
                case Op.EQ:
                    long eqVal1 = stackBuffer.getLong(stackPtr--);
                    long eqVal2 = stackBuffer.getLong(stackPtr--);
                    stackBuffer.putLong(stackPtr++, eqVal1 == eqVal2 ? 1 : 0);
                    break;
                case Op.NEQ:
                    long neqVal1 = stackBuffer.getLong(stackPtr--);
                    long neqVal2 = stackBuffer.getLong(stackPtr--);
                    stackBuffer.putLong(stackPtr++, neqVal1 != neqVal2 ? 1 : 0);
                    break;
                case Op.PUSH:
                    long pushVal = deref(opr, operand);
                    push(pushVal);
                    break;
                case Op.POP:
                    long popVal = pop();
                    assign(operand, popVal);
                    break;
                case Op.GOTO:
                    instructionPtr = (int) operand;
                    break;
                case Op.BIF:
                    if (pop() != 0) {
                        instructionPtr = (int) operand;
                    }
                    break;
                case Op.CALL:
                    long func = stackBuffer.getLong(stackPtr - 8 - (int)(8*operand));
                    if (func >= 0 ){
                        push(instructionPtr);
                        push(basePointer);
                        basePointer = stackPtr;
                        instructionPtr = (int) func;
                    } else if (func == -1) {
                        do_println();
                    } else {
                        stop("no CALL defined for function " + func);
                    }
                    break;
                case Op.RETURN:
                    stackPtr = basePointer;
                    basePointer = (int) pop();
                    instructionPtr = (int) pop();
                    break;
                case Op.NOOP:
                    break;
                default:
                    stop("Invalid opcode " + op);
            }
        }
    }

    private static void do_println() {
        long addr = stackBuffer.getLong(stackPtr);
        byte b = codeBuffer.get((int)addr++);
        while (b != 0) {
            System.out.print((char)b);
            b = codeBuffer.get((int)addr++);
        }
        System.out.println();
    }

    private static void push(long val) {
        stackBuffer.putLong(val);
        stackPtr += 8;
    }

    private static void assign(long ad, long val) {
        switch(opr) {
            case Op.R_ABS: {  }
            case Op.R_IMM: {  }
            case Op.R_STACK: {  }
            default: {  }
        }
    }

    private static long pop() {
        stackPtr -= 8;
        return stackBuffer.getLong(stackPtr);
    }

    private static long deref(int opr, long operand) {
        switch(opr) {
            case Op.R_ABS: { return codeBuffer.getLong((int)operand); }
            case Op.R_IMM: { return operand; }
            case Op.R_STACK: { return stackBuffer.getLong(basePointer+(int)operand); }
            default: { stop("deref region " + opr); }
        }
        return 0;
    }

    private static void stop(String s) {
        System.err.println(s);
        System.exit(1);
    }

    private static void fetch() {
        op = code[instructionPtr];
        opr = code[instructionPtr + 1];
        if (opr != 0) {
            operand = getOperand();
        }
        instructionPtr += 8;
    }

    private static long getOperand() {
        long i = 0;
        if (codeBuffer.get(instructionPtr + 7) < 0) {
            i = -1;
        }
        for (int j = 7; j > 1; j--) {
            i <<= 8;
            i |= codeBuffer.get(instructionPtr + j);
        }
        return i;
    }

    private static boolean loadBytecode(String filename)  throws IOException {
        code = Files.readAllBytes(Paths.get(filename));
        byte[] magicString = "Jzero!!\0".getBytes(StandardCharsets.US_ASCII);
        int i = find(magicString, code);
        if (i >= 0) {
            codeBuffer = ByteBuffer.wrap(code);
            return true;
        } else {
            return false;
        }
    }

    private static int find(byte[] needle, byte[] haystack) {
        for ( int i = 0; i < haystack.length - needle.length + 1; i++ ) {
            boolean found = true;
            for (int j = 0; j < needle.length; j++) {
                if (haystack[i+j] != needle[j]) {
                    found = false;
                    break;
                }
            }
            if (found) { return i; }
        }
        return -1;
    }

}
