package org.repositoryminer.antipattern;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.repositoryminer.ast.AST;
import org.repositoryminer.ast.DeclarationType;
import org.repositoryminer.ast.TypeDeclaration;
import org.repositoryminer.ast.MethodDeclaration;
import org.repositoryminer.ast.AbstractTypeDeclaration;
import org.repositoryminer.metric.CCMetric;
import org.repositoryminer.metric.MLOCMetric;
import org.repositoryminer.metric.NOAVMetric;

public class BrainMethod implements IAntiPattern {
	
	private List<Document> methodsDoc;
	private int mlocThreshold = 65;
	private float ccMlocThreshold = 0.24f;
	private int maxNestingThreshold = 5;
	private int noavThreshold = 5;
	
	public BrainMethod() {}
	
	public BrainMethod(int mlocThreshold, float ccMlocThreshold, int maxNestingThreshold, int noavThreshold) {
		this.mlocThreshold = mlocThreshold;
		this.ccMlocThreshold = ccMlocThreshold;
		this.maxNestingThreshold = maxNestingThreshold;
		this.noavThreshold = noavThreshold;
	}

	@Override
	public void detect(AbstractTypeDeclaration type, AST ast, Document document) {
		if (type.getType() == DeclarationType.CLASS_OR_INTERFACE) {
			TypeDeclaration cls = (TypeDeclaration) type;
			
			methodsDoc = new ArrayList<Document>();

			for(MethodDeclaration method : cls.getMethods()){
				boolean brainMethod = detect(method, ast);
				methodsDoc.add(new Document("method", method.getName()).append("value", new Boolean(brainMethod)));
			}

			document.append("name", new String("Brain Method")).append("methods", methodsDoc);
		}
	}
	
	public boolean detect(MethodDeclaration method, AST ast){
		boolean brainMethod = false;
		
		MLOCMetric mlocMetric = new MLOCMetric();
		CCMetric ccMetric = new CCMetric();
		NOAVMetric noavMetric = new NOAVMetric();
		
		int cc = ccMetric.calculate(method);
		int mloc = mlocMetric.calculate(method, ast);
		int noav = noavMetric.calculate(method);
		int maxNesting = 7; //TODO implement max nesting level calculation
		
		brainMethod = (mloc>mlocThreshold) && (cc/mloc >= ccMlocThreshold) && (maxNesting >= maxNestingThreshold && noav > noavThreshold);
		
		return brainMethod;
	}

}