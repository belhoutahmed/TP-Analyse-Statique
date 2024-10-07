package MAIN;



import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;


class Visitor extends org.eclipse.jdt.core.dom.ASTVisitor {
  

    @Override
    public boolean visit(TypeDeclaration node) {
        main.totalClassCount++;  // Incrémente le nombre de classes
        String className = node.getName().getFullyQualifiedName();
        int methodCountInClass = node.getMethods().length;
        main.totalAttributeCount += node.getFields().length;
        main.classMethodCounts.put(className, methodCountInClass);
        
     // Count attributes
        int attributeCountInClass = node.getFields().length; // Get the number of attributes (fields)
        main.classAttributeCounts.put(className, attributeCountInClass); // Add attribute count to the map
       
        
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        main.totalMethodCount++;  // Incrémente le nombre de méthodes
     // Get the starting and ending line numbers
        int startLine = node.getStartPosition();
        int endLine = startLine;

        Block body = node.getBody();
        if (body != null) {
            endLine = body.getStartPosition() + body.getLength();
        }

        int lineCount = endLine - startLine; // Calculate lines of code in the method
        main.methodLineCounts.put(node.getName().toString(), lineCount); // Add method name and its line count
       
        String methodName = node.getName().getIdentifier(); // Get the method name
        int parameterCount = node.parameters().size(); // Get the number of parameters

        // Store the method name and the number of parameters
        main.methodParameterCounts.put(methodName, parameterCount);
        return super.visit(node);
    }
    
    @Override
    public boolean visit(PackageDeclaration node) {
    	 main.totalPackages.add(node.getName().getFullyQualifiedName());  // Incrémenter le nombre de packages
        return super.visit(node);
    }

  
}
