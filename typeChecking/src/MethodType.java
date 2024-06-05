public class MethodType extends TypeInfo {
    Parameter[] parameters;
    TypeInfo returnType;

    MethodType(Parameter[] parameters, TypeInfo returnType) {
        super("method");
        this.parameters = parameters;
        this.returnType = returnType;
    }
}
