public class Address {
    public String region;
    public int offset;

    public Address(String region, int offset) {
        this.region = region;
        this.offset = offset;
    }

    public String regaddr() {
        return region.equals("method") ? "loc" : region;
    }

    public String str() {
        switch (region) {
            case "lab": return "L" + offset;
            case "loc": case "imm": case "method":
            case "global": case "class": case "strings":
                return regaddr() + ":" + offset;
        }
        return region;
    }
    public void print() { System.out.print(str()); }
}
