import java.io.PrintStream;
import java.util.HashMap;

public class Bytecode {
    int opcode;
    int operandRegion;
    long operand;

    static HashMap<Short,String> ops;
    static { ops = new HashMap<>();
        ops.put(Op.HALT,"halt"); ops.put(Op.NOOP,"noop");
        ops.put(Op.ADD,"add"); ops.put(Op.SUB,"sub");
        ops.put(Op.MUL,"mul"); ops.put(Op.DIV, "div");
        ops.put(Op.MOD,"mod"); ops.put(Op.NEG, "neg");
        ops.put(Op.PUSH,"push"); ops.put(Op.POP, "pop");
        ops.put(Op.CALL, "call"); ops.put(Op.RETURN, "return");
        ops.put(Op.GOTO, "goto"); ops.put(Op.BIF, "bif");
        ops.put(Op.LT, "lt"); ops.put(Op.LE, "le");
        ops.put(Op.GT, "gt"); ops.put(Op.GE, "ge");
        ops.put(Op.EQ, "eq"); ops.put(Op.NEQ, "neq");
        ops.put(Op.LOCAL, "local"); ops.put(Op.LOAD, "load");
        ops.put(Op.STORE, "store");
    }

    public Bytecode(int opcode, Address address) {
        this.opcode = opcode;
        addr(address);
    }

    public void addr(Address address) {
        if (address == null) {
            operandRegion = Op.R_NONE;
            return;
        }
        switch (address.region) {
            case "loc":
                operandRegion = Op.R_STACK;
                operand = address.offset;
                break;
            case "glob":
            case "lab":
            case "const":
                operandRegion = Op.R_ABS;
                operand = address.offset;
                break;
            case "obj":
                operandRegion = Op.R_HEAP;
                operand = address.offset;
                break;
            case "imm":
                operandRegion = Op.R_IMM;
                operand = address.offset;
                break;
        }
    }

    public String getName() {
        return ops.get((short)opcode);
    }

    public String addrOf() {
        switch (operandRegion) {
            case Op.R_NONE:
                return "";
            case Op.R_STACK:
                return "stack: " + operand;
            case Op.R_ABS:
                return "@ " + Long.toHexString(operand);
            case Op.R_HEAP:
                return "heap " + operand;
            case Op.R_IMM:
                return String.valueOf(operand);
        }

        return operandRegion + ":" + operand;
    }

    public void print() {
        print(System.out);
    }

    public void print(PrintStream f) {
        f.println("\t" + getName() + " " + addrOf());
    }

    public void printb() {
        printb(System.out);
    }
    public void printb(PrintStream f) {
        long x = operand;
        f.print((char)opcode);
        f.print((char)operandRegion);
        for (int i = 0; i < 6; i++) {
            f.print((char)(x & 0xFF));
            x >>= 8;
        }
    }
}
