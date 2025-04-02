package Parser;

import Grammar.Grammar;

import java.util.*;

import java.util.Map;
import java.util.stream.Collectors;

public class ParsingTable {
    private Map<String, Map<String, Production>> table;
    private Map<String, List<List<String>>> grammar;
    private LoadFirstFollow firstFollow;
    public ParsingTable(Map<String, List<List<String>>> grammar, LoadFirstFollow firstFollow) {
        this.table = new HashMap<>();
        this.grammar = grammar;
        this.firstFollow = firstFollow;
    }

    private Set<String> computeFirstSet(List<String> prod){
        Set<String> result = new HashSet<>();
        boolean allNullable = true;
        for (String symbol : prod) {
            // Si el símbolo es un no terminal (existe en la gramática)
            if (grammar.containsKey(symbol)) {
                List<String> firstList = firstFollow.getFirst(symbol);
                // Añadimos todos los terminales del FIRST de ese no terminal, excepto ε
                result.addAll(firstList.stream().filter(s -> !s.equals("ε"))
                        .collect(Collectors.toSet()));
                // Si no contiene ε, ya no es nullable y se detiene
                if (!firstList.contains("ε")) {
                    allNullable = false;
                    break;
                }
            } else {
                // Si el símbolo no está en la gramática, se asume que es terminal
                result.add(symbol);
                allNullable = false;
                break;
            }
        }
        if (allNullable) {
            result.add("ε");
        }
        return result;
    }

    public Map<String, Map<String, Production>> buildTable() {
        for(String nonTerminal : grammar.keySet()){
            List<List<String>> productions = grammar.get(nonTerminal);
            for(List<String> prod : productions){
                Production production = new Production(nonTerminal,prod.toArray(prod.toArray(new String[0])));
                System.out.println();
                Set<String> firstSet = computeFirstSet(prod);
                for (String terminal : firstSet) {
                    if (!terminal.equals("ε")) {
                        table.computeIfAbsent(nonTerminal, k -> new HashMap<>())
                                .put(terminal, production);
                    }
                }
                // Si FIRST(α) contiene ε, para cada terminal en FOLLOW(A) se añade la producción
                if (firstSet.contains("ε")) {
                    List<String> followList = firstFollow.getFollow(nonTerminal);
                    for (String terminal : followList) {
                        table.computeIfAbsent(nonTerminal, k -> new HashMap<>())
                                .put(terminal, production);
                    }
                }
            }

        }
        return null;
    }

    public void printTable() {
        for(String nonTerminal : table.keySet()){
            System.out.println(nonTerminal);
            for(String terminal : table.get(nonTerminal).keySet()){
                System.out.println("\t"+terminal);
                for(String prod: table.get(nonTerminal).keySet()){
                    System.out.println("\t\t"+table.get(nonTerminal).get(prod));
                }

            }
        }
    }



}
