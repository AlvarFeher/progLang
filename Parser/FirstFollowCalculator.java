package Parser;

import Grammar.Grammar;

import java.util.*;

public class FirstFollowCalculator {

    private final Map<String, List<List<String>>> grammar;
    private final Set<String> nonTerminals;
    private final Set<String> terminals;
    public final Map<String, Set<String>> firstSets;
    public final Map<String, Set<String>> followSets;
    private final String startSymbol = "Program"; // Start symbol

    public FirstFollowCalculator(Map<String, List<List<String>>> grammar) {
        this.grammar = grammar;
        this.nonTerminals = grammar.keySet();
        this.terminals = computeTerminals();
        this.firstSets = new HashMap<>();
        this.followSets = new HashMap<>();
        computeFirstSets();
        computeFollowSets();
        followSets.get("Llista_inst").add(";");
        followSets.get("Llista_inst").add("FI");

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
        return terminals;
    }

    private void computeFirstSets() {
        for (String symbol : grammar.keySet()) {
            firstSets.put(symbol, new HashSet<>());
        }

        boolean changed;
        do {
            changed = false;
            for (String nonTerm : grammar.keySet()) {
                for (List<String> production : grammar.get(nonTerm)) {
                    for (int i = 0; i < production.size(); i++) {
                        String sym = production.get(i);
                        if (terminals.contains(sym)) {
                            if (firstSets.get(nonTerm).add(sym)) changed = true;
                            break;
                        } else if (nonTerminals.contains(sym)) {
                            Set<String> firstSym = new HashSet<>(firstSets.get(sym));
                            if (firstSym.remove("ε")) {
                                if (firstSets.get(nonTerm).addAll(firstSym)) changed = true;
                            } else {
                                if (firstSets.get(nonTerm).addAll(firstSym)) changed = true;
                                break;
                            }
                        } else if (sym.equals("ε")) {
                            if (firstSets.get(nonTerm).add("ε")) changed = true;
                            break;
                        }
                    }
                }
            }
        } while (changed);
    }

    private void computeFollowSets() {
        for (String nonTerm : nonTerminals) {
            followSets.put(nonTerm, new HashSet<>());
        }
        followSets.get(startSymbol).add("$"); // End marker

        boolean changed;
        do {
            changed = false;
            for (Map.Entry<String, List<List<String>>> entry : grammar.entrySet()) {
                String A = entry.getKey();
                for (List<String> production : entry.getValue()) {
                    for (int i = 0; i < production.size(); i++) {
                        String B = production.get(i);
                        if (nonTerminals.contains(B)) {
                            Set<String> firstBeta = new HashSet<>();
                            boolean epsilonInAll = true;

                            for (int j = i + 1; j < production.size(); j++) {
                                String sym = production.get(j);

                                if (sym.equals("ε")) continue;

                                if (terminals.contains(sym)) {
                                    firstBeta.add(sym);
                                    epsilonInAll = false;
                                    break;
                                } else if (nonTerminals.contains(sym)) {
                                    Set<String> firstSym = new HashSet<>(firstSets.get(sym));
                                    firstBeta.addAll(firstSym);
                                    if (!firstSym.contains("ε")) {
                                        epsilonInAll = false;
                                        break;
                                    } else {
                                        firstBeta.remove("ε"); // continue checking
                                    }
                                } else {
                                    epsilonInAll = false;
                                    break;
                                }
                            }

// Apply FIRST(β) to FOLLOW(B)
                            if (followSets.get(B).addAll(firstBeta)) {
                                changed = true;
                            }

// ✅ Apply FOLLOW(A) if all after B are nullable OR B is at end
                            if (epsilonInAll || i == production.size() - 1) {
                                if (followSets.get(B).addAll(followSets.get(A))) {
                                    System.out.println("FOLLOW(" + B + ") gets FOLLOW(" + A + ") = " + followSets.get(A));
                                    changed = true;
                                }
                            }

                        }
                    }
                }
            }
        } while (changed);
    }


    public void printFirstFollow() {
        System.out.println("=== FIRST Sets ===");
        for (String nt : grammar.keySet()) {
            System.out.println(nt + " -> " + firstSets.get(nt));
        }

        System.out.println("\n=== FOLLOW Sets ===");
        for (String nt : grammar.keySet()) {
            System.out.println(nt + " -> " + followSets.get(nt));
        }
    }

    public static void main(String[] args) {
        Grammar g = new Grammar();
        FirstFollowCalculator calculator = new FirstFollowCalculator(g.getGrammar());
        calculator.printFirstFollow();
    }
}
