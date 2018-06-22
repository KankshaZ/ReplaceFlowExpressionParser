# ReplaceFlowExpressionParser: 
Currently, the CheckerFramework uses Regex to parse for any expressions/arguments given to Annotations. However, to make it more generalizable and robust, an alternative using JavaParser is demonstrated here. 
  
### Structure of repo: (regarding issue [CheckerFramework#1614](https://github.com/typetools/checker-framework/issues/1614))
- Test.java : looks for all the .java files in the folder and identifies the type of Java Expression in annotation without the need for regex by making calls to Java Parser.
- Anno.java: Lock checker example adapted from the CheckerFramework Manual.
- AnnoWithoutQuotes.java: Same above example but the expressions inside the annotation is without quotes ( " ) .

### Working : 

On checking for the kind of expression, every expression in Anno.java evaluates to a StringLiteralExpr since it is inside quotes " " or ArrayInitializerExpr if inside { }. 
In @Annotation("myClass.field"), "myClass.field" is extracted and evaluates to StringLiteralExpr 

On removing the quotes as done in AnnoWithoutQuotes.java, ThisExpr(), SuperExpr(), FieldAccessExpr(), ArrayAccessExpr(), String/Integer/Long/NullLiteralExpr(), and MethodCallExpr() are identified correctly. 
In @Annotation(myClass.field), myClass.field is extracted evaluates to FieldAccessExpr.


### Run: 

    javac Test.java
    java Test
    
### Example : ( Anno.java )

    import org.checkerframework.checker.lock.qual.*;

    import java.util.concurrent.locks.ReentrantLock;

    class Anno {
      final Object lockA = new Object();
      final Object lockB = new Object();
      @GuardedBy("lockA") Object x = new Object();
      @GuardedBy({"lockA", "lockB"}) Object y = new Object();
      @Holding("lockB")
      void myMethod() {
          synchronized(lockA) {
            x.toString();  // dereferences y's value without holding lock lockB
          }
      }

      @EnsuresLockHeld("this")
      public static void lock(){

      }
    }


### Output for above example : 
    
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

### Inference: 
The isXYZExpr() function ( present in the current Regex version of the checker framework source code for [FlowExpressionParseUtil](https://github.com/typetools/checker-framework/blob/8896e76f32d2a55acf8b08be952f26b12cdaf7a5/framework/src/main/java/org/checkerframework/framework/util/FlowExpressionParseUtil.java#L123)) is easy to replace by the JavaParser - the getClass method or checking using ‘instance of’ works. 

__For each argument given to the Annotation, the above code will return the type of expression it is__

### Pending & Concerns: 

- Does this imply that the way annotations are defined will have to be changed? I will see if I can look for a way around that if so.
- How to fit the input to the parseXYZExpr() function on using Java Parser has to be worked on.


<!--
The Test.java file looks for all the .java files in the folder and identifies the type of Java Expression in annotation without the need for regex by making calls to Java Parser. There are two files inside - Anno.java and AnnoWithoutQuotes.java. Anno.java follows the rules present in the manual. AnnoWithoutQuotes.java has the same example and follows rules as well, but the expression inside the annotation is not within quotes.
\
On checking for the kind of expression, every expression in Anno.java evaluates to a StringLiteralExpr since it is inside quotes " " or ArrayInitializerExpr if inside { }. 
In @Annotation("myClass.field"), "myClass.field" is extracted and evaluates to StringLiteralExpr 
\
On removing the quotes as done in AnnoWithoutQuotes.java, ThisExpr(), SuperExpr(), FieldAccessExpr(), ArrayAccessExpr(), String/Integer/Long/NullLiteralExpr(), and MethodCallExpr() are identified correctly. 
In @Annotation(myClass.field), myClass.field is extracted evaluates to FieldAccessExpr.
\
(Does this imply that the way annotations are defined will have to be changed? I will see if I can look for a way around that if so.)
-->
