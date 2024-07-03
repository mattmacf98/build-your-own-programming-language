public class x64loc {
    public String registerName;
    public Object offset;
    public int mode;

    public x64loc(String registerName) {
        this.registerName = registerName;
        this.mode = 1;
    }

    public x64loc(int offset) {
        this.offset = offset;
        this.mode = 2;
    }

    public x64loc(String registerName, int offset) {
        if (registerName.equals("imm")) {
            this.offset = offset;
            this.mode = 5;
        } else if (registerName.equals("lab")) {
            this.offset = offset;
            this.mode = 6;
        } else {
            this.registerName = registerName;
            this.offset = offset;
            this.mode = 3;
        }
    }

    public x64loc(String registerName, String offset) {
        this.registerName = registerName;
        this.offset = offset;
        this.mode = 4;
    }

    public String str() {
        switch (mode) {
            case 1: return registerName;
            case 2: return String.valueOf(offset);
            case 3: case 4: {
                if (registerName.equals("lab")) return (String)offset;
                return offset + "(" + registerName + ")";
            }
            case 5: return "$" + offset;
            case 6: return (offset instanceof Integer)? (".L"+offset) : (String)offset;
            default: System.err.println("x64loc unknown mode " + mode);
        }
        return "unknown";
    }
}
