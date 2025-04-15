package Parser;

public class InputEntry {
    private String value;
    private String terminal;

    public InputEntry(String value, String terminal) {
        this.value = value;
        this.terminal = terminal;
    }

    public String getValue() {
        return value;
    }

    public String getTerminal() {
        return terminal;
    }
}
