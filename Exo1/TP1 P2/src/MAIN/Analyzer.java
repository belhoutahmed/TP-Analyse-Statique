package MAIN;

import org.eclipse.jdt.core.dom.AST;

import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class Analyzer {
	
	// Fonction pour analyser le projet
    public static void analyzeProject(File directory) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                analyzeProject(file); // Analyser récursivement les sous-répertoires
            } else if (file.getName().endsWith(".java")) {
                analyzeFile(file); // Analyser le fichier Java
            }
        }
    }

    // Analyse d'un fichier Java pour récupérer les statistiques
    public static void analyzeFile(File file) throws IOException {
        // AST Parsing
        ASTParser parser = ASTParser.newParser(AST.JLS4);
        parser.setSource(FileUtils.readFileToString(file, "UTF-8").toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        // Visiteur combiné pour compter les classes et méthodes
        Visitor visitor = new Visitor();
        cu.accept(visitor);

        // Compter les lignes de code
        main.totalLineCount += countLinesInFile(file);
    }
    
 // Compte les lignes de code dans un fichier
    public static int countLinesInFile(File file) throws IOException {
        // Lecture du contenu du fichier
        String content = FileUtils.readFileToString(file, "UTF-8");

        // Compte des lignes non vides
        String[] lines = content.split("\n");
        int nonEmptyLineCount = 0;
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                nonEmptyLineCount++;
            }
        }

        return nonEmptyLineCount;
    }


}
