package Parser;


import Grammar.Grammar;

import java.util.*;

public class ParsingTableBuilder {

    private final Map<String, List<List<String>>> grammar;
    private final Map<String, Set<String>> firstSets;
    private final Map<String, Set<String>> followSets;
    private final Set<String> terminals;
    private final Set<String> nonTerminals;

    public ParsingTableBuilder(Map<String, List<List<String>>> grammar,
                               Map<String, Set<String>> firstSets,
                               Map<String, Set<String>> followSets) {
        this.grammar = grammar;
        this.firstSets = firstSets;
        this.followSets = followSets;
        this.nonTerminals = grammar.keySet();
        this.terminals = computeTerminals();
    }

    private Set<String> computeTerminals() {
        Set<String> terminals = new HashSet<>();
        for (List<List<String>> rules : grammar.values()) {
            for (List<String> rule : rules) {
                for (String symbol : rule) {
                    if (!grammar.containsKey(symbol) && !symbol.equals("ε")) {
                        terminals.add(symbol);
                    }
                }
            }
        }
        terminals.add("$"); // end marker
        return terminals;
    }

    public Map<String, Map<String, List<String>>> buildParsingTable() {
        Map<String, Map<String, List<String>>> table = new HashMap<>();
        System.out.println("FIRST(Llista_expressio): " + firstSets.get("Llista_expressio"));
        for (String nonTerm : grammar.keySet()) {
            table.put(nonTerm, new HashMap<>());
            for (List<String> production : grammar.get(nonTerm)) {
                Set<String> first = firstOfProduction(production);

                for (String terminal : first) {
                    if (!terminal.equals("ε")) {
                        table.get(nonTerm).put(terminal, production);
                    }
                }

                if (first.contains("ε")) {
                    for (String followSym : followSets.get(nonTerm)) {
                        table.get(nonTerm).put(followSym, production);
                    }
                }
            }
        }

        return table;
    }

    private Set<String> firstOfProduction(List<String> production) {
        Set<String> result = new HashSet<>();
        for (String symbol : production) {
            if (terminals.contains(symbol)) {
                result.add(symbol);
                break;
            } else if (symbol.equals("ε")) {
                result.add("ε");
                break;
            } else if (nonTerminals.contains(symbol)) {
                Set<String> first = new HashSet<>(firstSets.get(symbol));
                result.addAll(first);
                if (!first.contains("ε")) break;
                else result.remove("ε");
            }
        }
        return result;
    }

}
