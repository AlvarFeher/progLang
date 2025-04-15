package Symbols;

public class Symbol {
    private String type;
    private String name;
    private String value;

    public Symbol(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public String toString() {
        return "Symbol {"+type + " " + name + " " + value + "}";
    }
}
