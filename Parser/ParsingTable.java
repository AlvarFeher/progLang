package Parser;

import Grammar.Grammar;

import java.util.*;

public class ParsingTable {
    private Map<String, Map<String, String>> table;
   private Map<String, List<List<String>>> grammar;
   private LoadFirstFollow firstFollow;

    public ParsingTable(Map<String, List<List<String>>> grammar, LoadFirstFollow firstFollow) {
        this.grammar = grammar;
        this.firstFollow = firstFollow;
        this.table = new HashMap<>();
        buildTable();
    }

    private void buildTable() {
        // Recorremos cada no terminal de la gramática
        for (String nonTerminal : grammar.keySet()) {
            table.put(nonTerminal, new HashMap<>());
            List<List<String>> productions = grammar.get(nonTerminal);
            for (List<String> production : productions) {
                // Calculamos FIRST(α) para la producción A → α
                Set<String> firstSet = computeFirstForProduction(production);
                // Para cada terminal en FIRST(α) (excepto ε)
                for (String terminal : firstSet) {
                    if (!terminal.equals("ε")) {
                        table.get(nonTerminal).put(terminal, String.valueOf(production));
                    }
                }
                // Si FIRST(α) contiene ε, entonces para cada terminal en FOLLOW(A)
                if (firstSet.contains("ε")) {
                    List<String> followSet = firstFollow.getFollow(nonTerminal);
                    for (String terminal : followSet) {
                        table.get(nonTerminal).put(terminal, String.valueOf(production));
                    }
                }
            }
        }
    }

    // Método auxiliar para calcular FIRST de una secuencia de símbolos (producción)
    private Set<String> computeFirstForProduction(List<String> production) {
        Set<String> firstSet = new HashSet<>();
        boolean allHaveEpsilon = true;
        for (String symbol : production) {
            Set<String> symbolFirst = new HashSet<>();
            if (isTerminal(symbol)) {
                // Si es terminal, FIRST(symbol) es él mismo
                symbolFirst.add(symbol);
            } else {
                // Si es no terminal, usamos el conjunto FIRST ya calculado
                symbolFirst.addAll(firstFollow.getFirst(symbol));
            }
            // Agregamos todos los símbolos excepto ε
            for (String s : symbolFirst) {
                if (!s.equals("ε")) {
                    firstSet.add(s);
                }
            }
            // Si el símbolo actual no tiene ε en su FIRST, la cadena deja de producir ε
            if (!symbolFirst.contains("ε")) {
                allHaveEpsilon = false;
                break;
            }
        }
        if (allHaveEpsilon) {
            firstSet.add("ε");
        }
        return firstSet;
    }

    // Determina si un símbolo es terminal (suponiendo que todos los no terminales están en la gramática)
    private boolean isTerminal(String symbol) {
        return !grammar.containsKey(symbol);
    }

    // Método de consulta: dado un no terminal y un terminal, retorna la producción (como cadena) a aplicar
    public String getProduction(String nonTerminal, String terminal) {
        if (table.containsKey(nonTerminal)) {
            Map<String, String> row = table.get(nonTerminal);
            if (row.containsKey(terminal)) {
                List<String> production = Collections.singletonList(row.get(terminal));
                return String.join(" ", production);
            }
        }
        return null;
    }

    // Para visualizar o depurar la tabla
    public void printTable() {
        for (String nonTerminal : table.keySet()) {
            System.out.println("NonTerminal: " + nonTerminal);
            Map<String, String> row = table.get(nonTerminal);
            for (String terminal : row.keySet()) {
                System.out.println("\tTerminal: " + terminal + " -> " + String.join(" ", row.get(terminal)));
            }
        }
    }

}
