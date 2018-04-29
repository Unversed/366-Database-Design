/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg366.assignment2.debug;

import java.io.File;
import java.io.IOException;
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
            System.out.println(LHS + "->" + RHS);
            this.LHS = LHS;
            this.RHS = RHS;
        }
    }

    public static void main(String[] args) {

        /*
        try (Scanner input = new Scanner(System.in)) {
            getQ1Input(input);
            buildClosure();
            printClosure();
            
        }
         */
        Scanner input;
        try {
            input = new Scanner(new File("C:\\Users\\Liam\\Documents\\NetBeansProjects\\[366]Assignment2[debug]\\src\\pkg366\\assignment2\\debug\\Test1.txt"));
            getQ1Input(input);
            findLargestClosure();
            printCandidateKey();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void getQ1Input(Scanner input) {

        System.out.println("Enter table attributes: ");
        attributes = stringToSet(input.nextLine());
        System.out.println(attributes);

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
            System.out.println("Let's test " + candidate);
            
            buildClosure = buildClosure(candidate);

            System.out.println("Is " + buildClosure + " larger than " + largestClosure + " ?");
            if (largestClosure.size() < buildClosure.size()) {
                System.out.println("It is!");
                currentCandidate = key;
                System.out.println("The new current candidate is " + currentCandidate);
                largestClosure = new HashSet(buildClosure);
                System.out.println("The new largest closure is " + largestClosure);
            } else {
                System.out.println("nope.");
            }
        }
    }

    public static Set buildClosure(Set buildClosure) {
        boolean keepBuilding = false;

        do {
            for (FunctionalDependency fd : functionalDependencies) {
                Set checkMe = new HashSet(fd.LHS);
                System.out.println("checking " + buildClosure + " against " + checkMe +"->"+fd.RHS);
                if (buildClosure.containsAll(checkMe)) {
                    System.out.println("Successful! Adding " + fd.RHS + " to closure " + buildClosure);
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
        System.out.println("The difference between table attributes and the largest set is " + difference);
        currentCandidate.addAll(difference);
        System.out.println("One possible candidate key is (" + currentCandidate + ").");
    }
}
