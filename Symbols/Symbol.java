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

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return "Symbol {"+type + " " + name + " " + value + "}";
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
