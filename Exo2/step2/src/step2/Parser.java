package step2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ASTVisitor;

public class Parser {
	
	public static final String projectPath = "C:\\Users\\DELL\\eclipse-workspace\\testApp";
	public static final String projectSourcePath = projectPath + "\\src";
	public static final String jrePath = "C:\\Program Files\\Java\\jre1.8.0_51\\lib\\rt.jar";
	
	// Map to store method calls: method name -> list of called methods
	private static Map<String, List<String>> callGraph = new HashMap<>();
	public static void makecallGraph() throws IOException {
		// Read Java files
				final File folder = new File(projectSourcePath);
				ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

				// Process each Java file
				for (File fileEntry : javaFiles) {
					String content = FileUtils.readFileToString(fileEntry, "UTF-8");
					CompilationUnit parse = parse(content.toCharArray());
					parse.accept(new MethodVisitor());
				}
		
	}
	public static void main(String[] args) throws IOException {
		// Read Java files
		final File folder = new File(projectSourcePath);
		ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

		// Process each Java file
		for (File fileEntry : javaFiles) {
			String content = FileUtils.readFileToString(fileEntry, "UTF-8");
			CompilationUnit parse = parse(content.toCharArray());
			parse.accept(new MethodVisitor());
		}

		// Print the call graph
		System.out.println("Call Graph:");
		for (String method : callGraph.keySet()) {
			System.out.println(method + " calls: " + callGraph.get(method));
		}
	}
	
	
	// Method to get the call graph
    public static Map<String, List<String>> getCallGraph() {
        return callGraph;
    }

	// Read all Java files from a specific folder
	public static ArrayList<File> listJavaFilesForFolder(final File folder) {
		ArrayList<File> javaFiles = new ArrayList<File>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				javaFiles.addAll(listJavaFilesForFolder(fileEntry));
			} else if (fileEntry.getName().endsWith(".java")) {
				javaFiles.add(fileEntry);
			}
		}
		return javaFiles;
	}

	// Create AST
	private static CompilationUnit parse(char[] classSource) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); // java +1.6
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		parser.setBindingsRecovery(true);
 
		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
 
		parser.setUnitName("");
 
		String[] sources = { projectSourcePath }; 
		String[] classpath = {jrePath};
 
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
		parser.setSource(classSource);
		
		return (CompilationUnit) parser.createAST(null); // Create and parse
	}
	
	// Visitor to extract method calls
	private static class MethodVisitor extends ASTVisitor {
		private String currentMethodName; // Track the current method name

		@Override
		public boolean visit(MethodDeclaration node) {
			// Get the method name
			currentMethodName = node.getName().getFullyQualifiedName();
			callGraph.putIfAbsent(currentMethodName, new ArrayList<>());
			return super.visit(node);
		}

		@Override
		public void endVisit(MethodDeclaration node) {
			// Clear the current method name when leaving the method
			currentMethodName = null;
		}

		@Override
		public boolean visit(MethodInvocation node) {
			// Get the method name being called
			String invokedMethodName = node.getName().getFullyQualifiedName();
			// Add the called method to the graph
			if (currentMethodName != null) {
				callGraph.get(currentMethodName).add(invokedMethodName);
			}
			return super.visit(node);
		}
	}
}
