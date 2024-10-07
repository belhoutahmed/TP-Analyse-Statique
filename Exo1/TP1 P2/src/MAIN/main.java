package MAIN;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class main {
	// Variables globales pour les statistiques
    static int totalClassCount = 0;
    static int totalMethodCount = 0;
    static int totalLineCount = 0;
    static Set<String> totalPackages = new HashSet<>();
    static int totalAttributeCount = 0; 
    static Map<String, Integer> classMethodCounts = new HashMap<>(); 
    static Map<String, Integer> classAttributeCounts = new HashMap<>();
    static Map<String, Integer> methodLineCounts = new HashMap<>();
    static Map<String, Integer> methodParameterCounts = new HashMap<>();

    public static void main(String[] args) throws IOException {
    	String projectPath = "C:\\Users\\DELL\\eclipse-workspace\\testApp"; // Chemin du projet Java
        File projectDir = new File(projectPath);

        if (!projectDir.exists() || !projectDir.isDirectory()) {
            System.out.println("Le chemin du projet est invalide.");
            return;
        }

        // Analyse du projet pour récupérer les statistiques
        Analyzer.analyzeProject(projectDir);

        System.out.println("Nombre de classes dans l'application : " + totalClassCount);
        System.out.println("Nombre de méthodes dans l'application : " + totalMethodCount);
        System.out.println("Nombre de lignes de code dans l'application : " + totalLineCount);
        System.out.println("Nombre de packages dans l'application : " + totalPackages.size());
        double averageMethodsPerClass = totalClassCount > 0 ? (double) totalMethodCount / totalClassCount : 0.0;
        System.out.printf("Nombre moyen de méthodes par classe : %.2f%n", averageMethodsPerClass);
        double averageLinesPerMethod = totalMethodCount > 0 ? (double) totalLineCount / totalMethodCount : 0;
        System.out.printf("Nombre moyen de lignes de code par méthode : %.2f%n" ,averageLinesPerMethod);
        double averageAttributesPerClass = totalClassCount > 0 ? (double) totalAttributeCount / totalClassCount : 0;
        System.out.printf("Nombre moyen d'attributs par classe : %.2f%n" ,averageAttributesPerClass);
        
        List<Map.Entry<String, Integer>> sortedClassMethodCounts = new ArrayList<>(classMethodCounts.entrySet());
        sortedClassMethodCounts.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // Sort in descending order

        int topTenPercentCount = (int) Math.ceil(sortedClassMethodCounts.size() * 0.1); // Calculate top 10%
        System.out.println("Les 10% des classes qui possèdent le plus grand nombre de méthodes :");
        for (int i = 0; i < topTenPercentCount && i < sortedClassMethodCounts.size(); i++) {
            Map.Entry<String, Integer> entry = sortedClassMethodCounts.get(i);
            System.out.println("Classe : " + entry.getKey() + ", Nombre de méthodes : " + entry.getValue());
        }
        
        List<Map.Entry<String, Integer>> sortedClassAttributeCounts = new ArrayList<>(classAttributeCounts.entrySet());
        sortedClassAttributeCounts.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // Sort in descending order

        int topTenPercentAttributeCount = (int) Math.ceil(sortedClassAttributeCounts.size() * 0.1); // Calculate top 10%
        System.out.println("Les 10% des classes qui possèdent le plus grand nombre d'attributs :");
        for (int i = 0; i < topTenPercentAttributeCount && i < sortedClassAttributeCounts.size(); i++) {
            Map.Entry<String, Integer> entry = sortedClassAttributeCounts.get(i);
            System.out.println("Classe : " + entry.getKey() + ", Nombre d'attributs : " + entry.getValue());
        }
        
        
        
       // Create sets to hold the top classes for methods and attributes
        Set<String> topMethodClassSet = new HashSet<>();
        Set<String> topAttributeClassSet = new HashSet<>();

        //Populate the sets with top classes
        for (int i = 0; i < topTenPercentCount && i < sortedClassMethodCounts.size(); i++) {
            Map.Entry<String, Integer> entry = sortedClassMethodCounts.get(i);
            topMethodClassSet.add(entry.getKey()); // Add class names to the set
        }

        for (int i = 0; i < topTenPercentAttributeCount && i < sortedClassAttributeCounts.size(); i++) {
            Map.Entry<String, Integer> entry = sortedClassAttributeCounts.get(i);
            topAttributeClassSet.add(entry.getKey()); // Add class names to the set
        }
      

        //Find the intersection of both sets (shared classes)
        topMethodClassSet.retainAll(topAttributeClassSet); 

        //Display the shared classes
        System.out.println("Les classes qui font partie en même temps des deux catégories :");
        for (String className : topMethodClassSet) {
            System.out.println("Classe : " + className);
        }
        
     // Specify the value of X 
        int X = 2; 

        // Print classes that have more than X methods
        System.out.println("Les classes qui possèdent plus de " + X + " méthodes :");
        for (Map.Entry<String, Integer> entry : sortedClassMethodCounts) {
            if (entry.getValue() > X) { // Check if the method count is greater than X
                System.out.println("Classe : " + entry.getKey() + ", Nombre de méthodes : " + entry.getValue());
            }
        }
        
     // Create a list from the methodLineCounts map for sorting
        List<Map.Entry<String, Integer>> sortedMethodLineCounts = new ArrayList<>(methodLineCounts.entrySet());
        sortedMethodLineCounts.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // Sort in descending order

        // Calculate the top 10% of methods with the most lines
        int topTenPercentMethodCount = (int) Math.ceil(sortedMethodLineCounts.size() * 0.1); // Calculate the top 10%

        // Display the methods with the highest number of lines of code
        System.out.println("Les 10% des méthodes qui possèdent le plus grand nombre de lignes de code :");
        for (int i = 0; i < topTenPercentMethodCount && i < sortedMethodLineCounts.size(); i++) {
            Map.Entry<String, Integer> entry = sortedMethodLineCounts.get(i);
            System.out.println("Méthode : " + entry.getKey() + ", Nombre de lignes : " + entry.getValue());
        }
        
        
     // Find the method with the maximum number of parameters
        Map.Entry<String, Integer> maxEntry = null;
        for (Map.Entry<String, Integer> entry : methodParameterCounts.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }

        // Display the method with the highest number of parameters
        if (maxEntry != null) {
            System.out.println("La méthode avec le plus grand nombre de paramètres est : " + maxEntry.getKey());
            System.out.println("Nombre de paramètres : " + maxEntry.getValue());
        } else {
            System.out.println("Aucune méthode trouvée.");
        }


   
    }


}
