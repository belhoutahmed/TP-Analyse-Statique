package step2;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallGraphViewer extends JFrame {
	
	    public CallGraphViewer() {
	        // Créer un graphique JGraphX
	        mxGraph graph = new mxGraph();
	        Object parent = graph.getDefaultParent();
	        
	        // Récupérer les données du graphique d'appels
	        Map<String, List<String>> callGraph = Parser.getCallGraph();
	        
	        // Dictionnaire pour suivre les nœuds déjà ajoutés
	        Map<String, Object> methodNodes = new HashMap<>();
	        
	        // Commencer à mettre à jour le graphique
	        graph.getModel().beginUpdate();
	        try {
	            // Créer des nœuds et des arêtes
	            for (String method : callGraph.keySet()) {
	                // Vérifier si le nœud a déjà été ajouté
	                if (!methodNodes.containsKey(method)) {
	                    Object methodVertex = graph.insertVertex(parent, null, method, 100, 100, 80, 30);
	                    methodNodes.put(method, methodVertex); // Ajouter le nœud à la carte
	                    
	                    for (String calledMethod : callGraph.get(method)) {
	                        // Vérifier si le nœud appelé a déjà été ajouté
	                        if (!methodNodes.containsKey(calledMethod)) {
	                            Object calledVertex = graph.insertVertex(parent, null, calledMethod, 200, 100, 80, 30);
	                            methodNodes.put(calledMethod, calledVertex); // Ajouter le nœud appelé à la carte
	                        }
	                        // Ajouter l'arête
	                        graph.insertEdge(parent, null, "calls", methodVertex, methodNodes.get(calledMethod));
	                    }
	                }
	            }
	        } finally {
	            graph.getModel().endUpdate();
	        }

	        // Disposer le graphique en cercle
	        mxCircleLayout layout = new mxCircleLayout(graph);
	        layout.execute(parent);

	        // Créer et configurer le composant JGraphX
	        mxGraphComponent graphComponent = new mxGraphComponent(graph);
	        getContentPane().add(graphComponent);

	        // Paramètres de la fenêtre
	        setTitle("Call Graph");
	        setSize(800, 600);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setLocationRelativeTo(null);
	    }
	


    public static void main(String[] args) throws IOException {
    	Parser.makecallGraph();
        SwingUtilities.invokeLater(() -> {
            CallGraphViewer viewer = new CallGraphViewer();
            viewer.setVisible(true);
        });
    }
}
