public class ArrayType extends TypeInfo {
    TypeInfo elementType;
    public ArrayType(TypeInfo typeInfo) {
        super("array");
        this.elementType = typeInfo;
    }
}
