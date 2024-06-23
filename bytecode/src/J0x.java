public class J0x {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("usage: J0x <file>");
            System.exit(1);
        }

        String file = args[0];
        if (!file.endsWith(".j0")) {
            System.err.println("file must end with .j0");
            System.exit(1);
        }

        try {
            J0Machine.init(file);
        } catch (Exception e) {
            System.err.println("Can't initialize, Exiting");
            System.exit(1);
        }
        J0Machine.interpret();
    }
}
