package pkg366.assignment1;

import java.util.*;

/**
 *
 * @author Liam
 */
public class Assignment1 {

    static Set<FunctionalDependency> functionalDependencies = new HashSet<FunctionalDependency>();
    static Set closure = new HashSet<>();
    static Set follows = new HashSet<>();

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
            buildClosure();
            printClosure();

            getQ2Input(input);
            buildClosure();

            if (closure.containsAll(follows)) {
                System.out.println("It is true");
            } else {
                System.out.println("It is false");
            }
        }
    }

    public static void getQ1Input(Scanner input) {
        getFD(input);

        System.out.println("Enter a set of attributes: ");
        closure = stringToSet(input.nextLine());
    }

    public static void getQ2Input(Scanner input) {
        getFD(input);

        System.out.println("Enter a single Functional Dependency: ");
        String inputFD = input.nextLine();

        closure = stringToSet(inputFD.substring(0, inputFD.indexOf("->")));
        follows = stringToSet(inputFD.substring(inputFD.indexOf("->") + 2));
        splitFDString(inputFD);
    }

    public static void getFD(Scanner FD) {

        System.out.println("Input Functional Dependencies: ");
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

    public static void buildClosure() {
        boolean keepBuilding = false;
        do {
            for (FunctionalDependency fd : functionalDependencies) {
                if (closure.containsAll(fd.LHS)) {
                    keepBuilding = closure.addAll(fd.RHS);
                }
            }
        } while (keepBuilding);
    }

    public static void printClosure() {
        StringBuilder cSet = new StringBuilder();
        String delim = "";
        for (Object o : closure) {
            cSet.append(delim).append(o);
            delim = ",";
        }
        System.out.println("The closure is {" + cSet + "}.");
    }
}
