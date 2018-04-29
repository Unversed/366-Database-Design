package pkg366.assignment2;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author Liam
 */
public class Assignment2 {

    static Set<FunctionalDependency> functionalDependencies = new HashSet<FunctionalDependency>();
    static Set currentClosure = new HashSet<>();
    static Set largestClosure = new HashSet<>();
    static Set currentCandidate = new HashSet<>();
    static Set attributes = new HashSet<>();

    static private class FunctionalDependency {

        Set LHS = new HashSet<>();
        Set RHS = new HashSet<>();

        public FunctionalDependency(Set LHS, Set RHS) {
            this.LHS = LHS;
            this.RHS = RHS;
        }
    }

    public static void main(String[] args) {
        
        try (Scanner input = new Scanner(System.in)) {
            getQ1Input(input);
            findLargestClosure();
            printCandidateKey();
            input.close();
        }
    }

    public static void getQ1Input(Scanner input) {

        System.out.println("Enter table attributes: ");
        attributes = stringToSet(input.nextLine());

        getFD(input);
    }

    public static void getFD(Scanner FD) {

        System.out.println("Input functional dependencies: ");
        String inputFD;

        while (FD.hasNextLine()) {
            inputFD = FD.nextLine();
            if (inputFD.equals(".")) {
                break;
            } else {
                splitFDString(inputFD);
            }
        }
    }

    public static void splitFDString(String input) {
        int position = input.indexOf("->");

        Set LHS = stringToSet(input.substring(0, position));
        Set RHS = stringToSet(input.substring(position + 2));
        for (Object o : RHS) {
            Set newRHS = new HashSet<>();
            newRHS.add(o);
            functionalDependencies.add(new FunctionalDependency(LHS, newRHS));
        }
    }

    public static Set stringToSet(String string) {

        Set results = new HashSet();
        int i = string.length();
        while (i-- > 0) {
            results.add(string.charAt(i));
        }
        return results;
    }

    public static void findLargestClosure() {
        for (FunctionalDependency f : functionalDependencies) {
            Set buildClosure = null;
            Set key = new HashSet(f.LHS);
            Set candidate = new HashSet(f.LHS);

            buildClosure = buildClosure(candidate);

            if (largestClosure.size() < buildClosure.size()) {
                currentCandidate = key;
                largestClosure = new HashSet(buildClosure);
            }
        }
    }

    public static Set buildClosure(Set buildClosure) {
        boolean keepBuilding = false;

        do {
            for (FunctionalDependency fd : functionalDependencies) {
                Set checkMe = new HashSet(fd.LHS);
                if (buildClosure.containsAll(checkMe)) {
                    keepBuilding = buildClosure.addAll(fd.RHS);
                }
            }
        } while (keepBuilding);

        return buildClosure;
    }

    public static void printCandidateKey() {

        Set difference = attributes;
        for (Object o : largestClosure) {
            difference.remove(o);
        }
        currentCandidate.addAll(difference);
        System.out.println("One possible candidate key is (" + currentCandidate + ").");
    }
}
