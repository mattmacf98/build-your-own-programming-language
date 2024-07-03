import java.util.ArrayList;

public class RegisterUse {
    public String registerName;
    public boolean loaded;
    public boolean dirty;
    public int basePointerOffset;

   public static RegisterUse[] registers = new RegisterUse[] {
            new RegisterUse("%rdi", -8),
            new RegisterUse("%rsi", -16),
            new RegisterUse("%rdx", -24),
            new RegisterUse("%rcx", -32),
            new RegisterUse("%r8", -40),
            new RegisterUse("%r9", -48),
            new RegisterUse("%r10", -56),
            new RegisterUse("%r11", -64),
            new RegisterUse("%r12", -72),
            new RegisterUse("%r13", -80),
            new RegisterUse("%r14", -88),
    };

    public RegisterUse(String registerName, int basePointerOffset) {
        this.registerName = registerName;
        this.basePointerOffset = basePointerOffset;
        this.loaded = false;
        this.dirty = false;
    }

    public ArrayList<x64> load() {
        if (loaded) {
            return null;
        }
        loaded = true;
        return j0.xgen("movq", basePointerOffset + "(%rbp)", registerName);
    }

    public ArrayList<x64> save() {
        if (!dirty) {
            return null;
        }
        dirty = false;
        return j0.xgen("movq", registerName, basePointerOffset + "(%rbp)");
    }
}
