import java.io.PrintStream;

public class x64 {
    String opcode;
    x64loc operand1;
    x64loc operand2;

    public x64(String opcode, Object src, Object dst) {
        this.opcode = opcode;
        this.operand1 = j0.loc(src);
        this.operand2 = j0.loc(dst);
    }

    public x64(String opcode, Object operand) {
        this.opcode = opcode;
        this.operand1 = j0.loc(operand);
    }

    public x64(String opcode) {
        this.opcode = opcode;
    }

    public void print(PrintStream f) {
        if (opcode.equals("lab")) {
            if (operand1 != null)
                f.println(operand1.str() + ":");
        }
        else {
            f.print("\t" + opcode);
            if (operand1 != null)
                f.print(" " +operand1.str());
            if (operand2 != null)
                f.print("," +operand2.str());
            f.println();
        }
    }
    public void print() { print(System.out); }
}
