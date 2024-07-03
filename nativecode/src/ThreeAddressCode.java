public class ThreeAddressCode {
    String opcode;
    Address operandOne;
    Address operandTwo;
    Address operandThree;

    public ThreeAddressCode(String opcode, Address operandOne, Address operandTwo, Address operandThree) {
        this.opcode = opcode;
        this.operandOne = operandOne;
        this.operandTwo = operandTwo;
        this.operandThree = operandThree;
    }

    public void print() {
        switch (opcode) {
            case "proc":
                System.out.println(opcode + "\t" + operandOne.region + ",0,0");
                break;
            case "end":
            case ".code":
            case ".global":
            case ".string":
                System.out.println(opcode);
                break;
            case "LAB": System.out.println("L" + operandOne.offset + ":");
                break;
            default:
                System.out.print("\t" + opcode + "\t");
                if (operandOne != null)
                    System.out.print(operandOne.str());
                if (operandTwo != null)
                    System.out.print("," + operandTwo.str());
                if (operandThree != null)
                    System.out.print("," + operandThree.str());
                System.out.println("");
        }
    }
}
