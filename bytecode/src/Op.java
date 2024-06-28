public class Op {
    public final static short HALT = 1, NOOP = 2, ADD = 3, SUB = 4, MUL = 5, DIV = 6, MOD = 7, NEG = 8, PUSH = 9, POP = 10,
            CALL = 11, RETURN = 12, GOTO = 13, BIF = 14, BNIF = 15, LT = 16, LE = 17, GT = 18, GE = 19, EQ = 20, NEQ = 21, LOCAL = 22, LOAD = 23,
            STORE = 24;
    public final static short R_NONE = 0, R_ABS = 1, R_IMM = 2, R_STACK = 3, R_HEAP = 4;
}
