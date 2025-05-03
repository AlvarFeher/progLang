package IntCode;

public class Quadruple {
    private String op;
    private String arg1;
    private String arg2;
    private String result;

    public Quadruple(String op, String arg1, String arg2, String result) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }

    @Override
    public String toString() {
        if (op.equals("LABEL")) {
            return arg1 + ":";
        } else
            return "(" + op + ", " + arg1 + ", " + arg2 + ", " + result + ")";
    }
}
