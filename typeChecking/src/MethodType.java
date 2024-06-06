public class MethodType extends TypeInfo {
    TypeInfo[] parameters;
    TypeInfo returnType;

    MethodType(TypeInfo[] parameters, TypeInfo returnType) {
        super("method");
        this.parameters = parameters;
        this.returnType = returnType;
    }
}
