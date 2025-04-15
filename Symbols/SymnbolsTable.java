package Symbols;
import java.util.HashMap;
import java.util.Map;

public class SymnbolsTable {
    private Map<String, Symbol> symbolsTable;

    public SymnbolsTable() {
        this.symbolsTable = new HashMap<>();
    }

    public void addEntry(String type, String name, String value) {
            symbolsTable.put(name, new Symbol(type, name, value));
    }

    public Symbol getEntry(String name) {
        return symbolsTable.get(name);
    }


}
