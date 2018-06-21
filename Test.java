import com.github.javaparser.JavaParser;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.expr.*;

public class Test {

	public static void main(String args[]) throws FileNotFoundException, ParseException {
		File folder = new File(System.getProperty("user.dir"));
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        String name = file.getName();
		        if(name.contains(".java")&&!name.contains("Test.java")){
		        	System.out.println("NEW FILE:");
		        	System.out.println(name);
		        	parseFile(name);
		        }
		    }
		}
	}

	public static void parseFile(String fileLocation)throws FileNotFoundException, ParseException {
		FileInputStream fileInputStream = new FileInputStream(fileLocation);
		CompilationUnit compilationUnit = JavaParser.parse(fileInputStream);

		List<TypeDeclaration> typeDeclarations = compilationUnit.getTypes();
	  	ClassOrInterfaceDeclaration classOrInterfaceDeclaration = new ClassOrInterfaceDeclaration();

	  	for (TypeDeclaration typeDeclaration : typeDeclarations) {
			classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) typeDeclaration;
			List<BodyDeclaration> bodyDeclarationList = classOrInterfaceDeclaration.getMembers();
			
			for (BodyDeclaration bodyDeclaration : bodyDeclarationList) {
				
				if (bodyDeclaration instanceof MethodDeclaration) {
			    	MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
			    	System.out.println(methodDeclaration);
			    	List<AnnotationExpr> methodAnno = methodDeclaration.getAnnotations();
			    	if(!methodAnno.isEmpty())
			    		printAnnotationAndType(methodAnno);
			    	List<Parameter> tpList = methodDeclaration.getParameters();
			    	/*System.out.println(tpList);*/
			    	if(tpList!= null){
			    		for(Parameter tp : tpList){
			    			List<AnnotationExpr> annotations = tp.getAnnotations();
			    			if(annotations!=null){
			    				printAnnotationAndType(annotations);
			    			}
			    		}
			    	}
			    }
			    else{
				    /*System.out.println(bodyDeclaration);*/
				    if(!bodyDeclaration.getAnnotations().isEmpty()){
						System.out.println(bodyDeclaration.getAnnotations());
						List<AnnotationExpr> annotations = bodyDeclaration.getAnnotations();
						printAnnotationAndType(annotations);
					}	
			    }	
			}
		}	
	}

	public static void printAnnotationAndType(List<AnnotationExpr> annotations){
		for(AnnotationExpr aex : annotations)
		{
			System.out.println(aex.getClass());
			if (aex.getClass().equals(SingleMemberAnnotationExpr.class)){
				SingleMemberAnnotationExpr smae = (SingleMemberAnnotationExpr) aex;
				Expression e  = smae.getMemberValue();
				System.out.println(e + " " + e.getClass());
				//As an alternative do this for every expression type
				//if(e instanceof ThisExpr)
				//	System.out.println("TRUE"); 
			}

			if (aex.getClass().equals(NormalAnnotationExpr.class)){
				NormalAnnotationExpr nae = (NormalAnnotationExpr) aex;
				List<MemberValuePair> mvp  = nae.getPairs();
				System.out.println(mvp);
			}
			
			System.out.println();
		}
	}

}

/*OUTPUT:
NEW FILE:
Anno.java
[@GuardedBy("lockA")]
class com.github.javaparser.ast.expr.SingleMemberAnnotationExpr
"lockA" class com.github.javaparser.ast.expr.StringLiteralExpr

[@GuardedBy({ "lockA", "lockB" })]
class com.github.javaparser.ast.expr.SingleMemberAnnotationExpr
{ "lockA", "lockB" } class com.github.javaparser.ast.expr.ArrayInitializerExpr

@Holding("lockB")
void myMethod() {
    synchronized (lockA) {
        // dereferences y's value without holding lock lockB
        x.toString();
    }
}
class com.github.javaparser.ast.expr.SingleMemberAnnotationExpr
"lockB" class com.github.javaparser.ast.expr.StringLiteralExpr

@EnsuresLockHeld("this")
public static void lock() {
}
class com.github.javaparser.ast.expr.SingleMemberAnnotationExpr
"this" class com.github.javaparser.ast.expr.StringLiteralExpr

NEW FILE:
AnnoWithoutQuotes.java
[@GuardedBy(lockA)]
class com.github.javaparser.ast.expr.SingleMemberAnnotationExpr
lockA class com.github.javaparser.ast.expr.NameExpr

[@GuardedBy({ lockA, lockB })]
class com.github.javaparser.ast.expr.SingleMemberAnnotationExpr
{ lockA, lockB } class com.github.javaparser.ast.expr.ArrayInitializerExpr

@Holding(lockB)
void myMethod() {
    synchronized (lockA) {
        // dereferences y's value without holding lock lockB
        x.toString();
    }
}
class com.github.javaparser.ast.expr.SingleMemberAnnotationExpr
lockB class com.github.javaparser.ast.expr.NameExpr

@EnsuresLockHeld(this)
public static void lock() {
}
class com.github.javaparser.ast.expr.SingleMemberAnnotationExpr
this class com.github.javaparser.ast.expr.ThisExpr
*/