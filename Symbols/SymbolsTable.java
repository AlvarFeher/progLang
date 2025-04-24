package Symbols;
import java.util.HashMap;
import java.util.Map;

public class SymbolsTable {
    private Map<String, Symbol> symbolsTable;

    public SymbolsTable() {
        this.symbolsTable = new HashMap<>();
    }

    public void addEntry(String name, String type, String value) {
        System.out.println(">> Adding symbol: " + name + " | Type: " + type + " | Value: " + value);
        if (!symbolsTable.containsKey(name)) {
            symbolsTable.put(name, new Symbol(type, name, value));
        } else {
            System.out.println("Symbol '" + name + "' already exists, skipping insert.");
        }
    }

    public Symbol getEntry(String name) {
        return symbolsTable.get(name);
    }
    public void updateValue(String id, String value) {
        Symbol entry = symbolsTable.get(id);
        if (entry != null) {
            entry.setValue(value);
        }
    }

    public void printSymbolsTable() {
        System.out.println("PRINTING SYMBOLS TABLE");
        for (Map.Entry<String, Symbol> entry : symbolsTable.entrySet()) {
            Symbol symbol = entry.getValue();
            System.out.printf("%-15s %-15s %-15s\n",
                    symbol.getName(),
                    symbol.getType(),
                    symbol.getValue() != null ? symbol.getValue() : "null");
        }
    }
}
