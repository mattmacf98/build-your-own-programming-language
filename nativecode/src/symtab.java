import java.util.ArrayList;
import java.util.HashMap;

public class symtab {
    String scope;
    symtab parent;
    int count;
    HashMap<String, symtabEntry> table;
    ArrayList<String> L;

    symtab(String sc) {
        this.scope = sc;
        this.table = new HashMap<String,symtabEntry>();
        L = new ArrayList<String>();
    }

    public symtab(String scope, symtab parent) {
        this.scope = scope;
        this.parent = parent;
        this.table = new HashMap<String, symtabEntry>();
        L = new ArrayList<String>();
    }

    public symtabEntry lookup(String sym) {
        symtabEntry result;
        result = table.get(sym);
        if (result != null) {
            return result;
        } else if (parent != null) {
            return parent.lookup(sym);
        } else  {
            return null;
        }
    }

    public void insert(String s, boolean isConst, symtab sub, TypeInfo typeInfo) {
        if (table.containsKey(s)) {
            j0.semerror("redeclaration of " + s);
        } else {
            if (sub != null) {
                sub.parent = this;
            }
            table.put(s, new symtabEntry(s, this, sub, isConst, typeInfo, new Address(scope, count)));
            L.add(s);
            count += 8;
        }
    }

    public Address generateLocal() {
        String tempVariable = "__local$" + count;
        insert(tempVariable, false, null, new TypeInfo("int"));
        return table.get(tempVariable).address;
    }

    public void print() {
        print(0);
    }

    public void print(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print(" ");
        }
        System.out.println(scope +  " - " + table.size() + " symbols");
        for (symtabEntry entry : table.values()) {
            entry.print(level + 1);
        }
    }
}
