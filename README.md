# ReplaceFlowExpressionParser

Command to run: 

  javac Test.java
  java Test
  
The Test.java file looks for all the .java files in the folder and identifies the type of Java Expression in annotation without the need for regex by making calls to Java Parser. There are two files inside - Anno.java and AnnoWithoutQuotes.java. Anno.java follows the rules present in the manual. AnnoWithoutQuotes.java has the same example and follows rules as well, but the expression inside the annotation is not within quotes.

On checking for the kind of expression, every expression in Anno.java evaluates to a StringLiteralExpr since it is inside quotes " " or ArrayInitializerExpr if inside { }. 
In @Annotation("myClass.field"), "myClass.field" is extracted and evaluates to StringLiteralExpr 

On removing the quotes as done in AnnoWithoutQuotes.java, ThisExpr(), SuperExpr(), FieldAccessExpr(), ArrayAccessExpr(), String/Integer/Long/NullLiteralExpr(), and MethodCallExpr() are identified correctly. 
In @Annotation(myClass.field), myClass.field is extracted evaluates to FieldAccessExpr.

(Does this imply that the way annotations are defined will have to be changed? I will see if I can look for a way around that if so.)
