package org.repositoryminer.metric.clazz;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.repositoryminer.ast.AST;
import org.repositoryminer.ast.AbstractTypeDeclaration;
import org.repositoryminer.ast.MethodDeclaration;
import org.repositoryminer.metric.MetricId;

/**
 * <h1>Number of Parameters</h1>
 * <p>
 * PAR is defined as the number of parameters per method.
 */
public class PAR extends MethodBasedMetricTemplate {

	private List<Document> methodsDoc;
	
	@Override
	public MetricId getId() {
		return MetricId.PAR;
	}
	
	@Override
	public Document calculate(AbstractTypeDeclaration type, List<MethodDeclaration> methods, AST ast) {
		methodsDoc = new ArrayList<Document>();
		int accumulated = 0;
		
		for(MethodDeclaration method : methods){
			int par = calculate(method);
			accumulated += par;
			methodsDoc.add(new Document("method", method.getName()).append("value", new Integer(par)));
		}
	
		return new Document("name", MetricId.PAR.toString()).append("accumulated", new Integer(accumulated)).append("methods", methodsDoc);
	}
	
	public int calculate(MethodDeclaration method){
		return method.getParameters() != null ? method.getParameters().size() : 0;
	}

}