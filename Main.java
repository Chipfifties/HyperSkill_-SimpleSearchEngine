package search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final List<String> people = new ArrayList<>();
    private static final Map<String, List<Integer>> invertedIndex = new HashMap<>();

    public static void main(String[] args) {
        readArgs(args);
        fillInvertedIndex();
        getMenu();
    }

    private static void readArgs(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader(args[1]))) {
            String input = reader.readLine();
            while (input != null) {
                people.add(input);
                input = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("File not found.");
        }
    }

    private static void fillInvertedIndex() {
        for (String entry : people) {
            String[] words = entry.split("\\s+");
            for (String word : words) {
                List<Integer> indices = new ArrayList<>();
                for (String name : people) {
                    if (name.contains(word) && !invertedIndex.containsKey(word)) {
                        indices.add(people.indexOf(name));
                    }
                }
                if (!invertedIndex.containsKey(word)) {
                    invertedIndex.put(word.toLowerCase(), indices);
                }
            }
        }
    }

    private static void getMenu() {
        while (true) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. Find a person");
            System.out.println("2. Print all people");
            System.out.println("0. Exit");

            try {
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        System.out.println("\nSelect a matching strategy: ALL, ANY, NONE");
                        String strategy = sc.nextLine().trim();
                        System.out.println("\nEnter a name or email to search all suitable people.");
                        List<String> result = searchStrategy(strategy, sc.nextLine().toLowerCase().trim());
                        if (result.isEmpty()) {
                            System.out.println("No matching people found.");
                        } else {
                            result.forEach(System.out::println);
                        }
                        break;
                    case 2:
                        System.out.println("\n=== List of people ===");
                        people.forEach(System.out::println);
                        break;
                    case 0:
                        System.out.println("\nBye!");
                        return;
                    default:
                        throw new NumberFormatException();
                }

            } catch (NumberFormatException e) {
                System.out.println("\nIncorrect option! Try Again.");
            }
        }
    }

    private static List<String> searchStrategy(String strategy, String searchWord) {
        List<String> result = new ArrayList<>();
        switch (strategy) {
            case "ALL":
                List<Integer> allIndices = new ArrayList<>();
                for (String w : searchWord.split("\\s+")) {
                    if (invertedIndex.containsKey(w)) {
                        if (allIndices.isEmpty()) {
                            allIndices.addAll(invertedIndex.get(w));
                        } else {
                            allIndices.retainAll(invertedIndex.get(w));
                        }
                    } else {
                        return Collections.emptyList();                    }
                }
                for (Integer index : allIndices) {
                    result.add(people.get(index));
                }
                break;
            case "ANY":
                Set<Integer> anyIndices = new HashSet<>();
                for (String w : searchWord.split("\\s+")) {
                    if (invertedIndex.containsKey(w)) {
                        anyIndices.addAll(invertedIndex.get(w));
                    }
                }
                if (anyIndices.isEmpty()) {
                    return Collections.emptyList();
                } else {
                    for (Integer index : anyIndices) {
                        result.add(people.get(index));
                    }
                }
                break;
            case "NONE":
                Set<Integer> noneIndices = new HashSet<>();
                for (String w : searchWord.split("\\s+")) {
                    noneIndices.addAll(invertedIndex.get(w));
                }

                if (noneIndices.isEmpty()) {
                    return Collections.emptyList();
                } else {
                    result.addAll(people);
                    List<String> toDeleteList = new ArrayList<>();
                    for (Integer index : noneIndices) {
                        toDeleteList.add(people.get(index));
                    }
                    result.removeAll(toDeleteList);
                }
                break;
        }
        return result;
    }
}
