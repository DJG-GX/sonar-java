/*
 * Sonar Java
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.java.ast.parser;

import org.sonar.java.ast.api.JavaGrammar;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.api.GenericTokenType.LITERAL;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.adjacent;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.o2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.firstOf;
import static org.sonar.java.ast.api.JavaKeyword.ABSTRACT;
import static org.sonar.java.ast.api.JavaKeyword.ASSERT;
import static org.sonar.java.ast.api.JavaKeyword.BOOLEAN;
import static org.sonar.java.ast.api.JavaKeyword.BREAK;
import static org.sonar.java.ast.api.JavaKeyword.BYTE;
import static org.sonar.java.ast.api.JavaKeyword.CASE;
import static org.sonar.java.ast.api.JavaKeyword.CATCH;
import static org.sonar.java.ast.api.JavaKeyword.CHAR;
import static org.sonar.java.ast.api.JavaKeyword.CLASS;
import static org.sonar.java.ast.api.JavaKeyword.CONTINUE;
import static org.sonar.java.ast.api.JavaKeyword.DEFAULT;
import static org.sonar.java.ast.api.JavaKeyword.DO;
import static org.sonar.java.ast.api.JavaKeyword.DOUBLE;
import static org.sonar.java.ast.api.JavaKeyword.ELSE;
import static org.sonar.java.ast.api.JavaKeyword.ENUM;
import static org.sonar.java.ast.api.JavaKeyword.EXTENDS;
import static org.sonar.java.ast.api.JavaKeyword.FALSE;
import static org.sonar.java.ast.api.JavaKeyword.FINAL;
import static org.sonar.java.ast.api.JavaKeyword.FINALLY;
import static org.sonar.java.ast.api.JavaKeyword.FLOAT;
import static org.sonar.java.ast.api.JavaKeyword.FOR;
import static org.sonar.java.ast.api.JavaKeyword.IF;
import static org.sonar.java.ast.api.JavaKeyword.IMPLEMENTS;
import static org.sonar.java.ast.api.JavaKeyword.IMPORT;
import static org.sonar.java.ast.api.JavaKeyword.INSTANCEOF;
import static org.sonar.java.ast.api.JavaKeyword.INT;
import static org.sonar.java.ast.api.JavaKeyword.INTERFACE;
import static org.sonar.java.ast.api.JavaKeyword.LONG;
import static org.sonar.java.ast.api.JavaKeyword.NATIVE;
import static org.sonar.java.ast.api.JavaKeyword.NEW;
import static org.sonar.java.ast.api.JavaKeyword.NULL;
import static org.sonar.java.ast.api.JavaKeyword.PACKAGE;
import static org.sonar.java.ast.api.JavaKeyword.PRIVATE;
import static org.sonar.java.ast.api.JavaKeyword.PROTECTED;
import static org.sonar.java.ast.api.JavaKeyword.PUBLIC;
import static org.sonar.java.ast.api.JavaKeyword.RETURN;
import static org.sonar.java.ast.api.JavaKeyword.SHORT;
import static org.sonar.java.ast.api.JavaKeyword.STATIC;
import static org.sonar.java.ast.api.JavaKeyword.STRICTFP;
import static org.sonar.java.ast.api.JavaKeyword.SUPER;
import static org.sonar.java.ast.api.JavaKeyword.SWITCH;
import static org.sonar.java.ast.api.JavaKeyword.SYNCHRONIZED;
import static org.sonar.java.ast.api.JavaKeyword.THIS;
import static org.sonar.java.ast.api.JavaKeyword.THROW;
import static org.sonar.java.ast.api.JavaKeyword.THROWS;
import static org.sonar.java.ast.api.JavaKeyword.TRANSIENT;
import static org.sonar.java.ast.api.JavaKeyword.TRUE;
import static org.sonar.java.ast.api.JavaKeyword.TRY;
import static org.sonar.java.ast.api.JavaKeyword.VOID;
import static org.sonar.java.ast.api.JavaKeyword.VOLATILE;
import static org.sonar.java.ast.api.JavaKeyword.WHILE;
import static org.sonar.java.ast.api.JavaPunctuator.AND;
import static org.sonar.java.ast.api.JavaPunctuator.ANDAND;
import static org.sonar.java.ast.api.JavaPunctuator.ANDEQU;
import static org.sonar.java.ast.api.JavaPunctuator.AT;
import static org.sonar.java.ast.api.JavaPunctuator.BANG;
import static org.sonar.java.ast.api.JavaPunctuator.COLON;
import static org.sonar.java.ast.api.JavaPunctuator.COMMA;
import static org.sonar.java.ast.api.JavaPunctuator.DEC;
import static org.sonar.java.ast.api.JavaPunctuator.DIV;
import static org.sonar.java.ast.api.JavaPunctuator.DIVEQU;
import static org.sonar.java.ast.api.JavaPunctuator.DOT;
import static org.sonar.java.ast.api.JavaPunctuator.ELLIPSIS;
import static org.sonar.java.ast.api.JavaPunctuator.EQU;
import static org.sonar.java.ast.api.JavaPunctuator.EQUAL;
import static org.sonar.java.ast.api.JavaPunctuator.GT;
import static org.sonar.java.ast.api.JavaPunctuator.HAT;
import static org.sonar.java.ast.api.JavaPunctuator.HATEQU;
import static org.sonar.java.ast.api.JavaPunctuator.INC;
import static org.sonar.java.ast.api.JavaPunctuator.LBRK;
import static org.sonar.java.ast.api.JavaPunctuator.LE;
import static org.sonar.java.ast.api.JavaPunctuator.LPAR;
import static org.sonar.java.ast.api.JavaPunctuator.LT;
import static org.sonar.java.ast.api.JavaPunctuator.LWING;
import static org.sonar.java.ast.api.JavaPunctuator.MINUS;
import static org.sonar.java.ast.api.JavaPunctuator.MINUSEQU;
import static org.sonar.java.ast.api.JavaPunctuator.MOD;
import static org.sonar.java.ast.api.JavaPunctuator.MODEQU;
import static org.sonar.java.ast.api.JavaPunctuator.NOTEQUAL;
import static org.sonar.java.ast.api.JavaPunctuator.OR;
import static org.sonar.java.ast.api.JavaPunctuator.OREQU;
import static org.sonar.java.ast.api.JavaPunctuator.OROR;
import static org.sonar.java.ast.api.JavaPunctuator.PLUS;
import static org.sonar.java.ast.api.JavaPunctuator.PLUSEQU;
import static org.sonar.java.ast.api.JavaPunctuator.QUERY;
import static org.sonar.java.ast.api.JavaPunctuator.RBRK;
import static org.sonar.java.ast.api.JavaPunctuator.RPAR;
import static org.sonar.java.ast.api.JavaPunctuator.RWING;
import static org.sonar.java.ast.api.JavaPunctuator.SEMI;
import static org.sonar.java.ast.api.JavaPunctuator.SL;
import static org.sonar.java.ast.api.JavaPunctuator.SLEQU;
import static org.sonar.java.ast.api.JavaPunctuator.STAR;
import static org.sonar.java.ast.api.JavaPunctuator.STAREQU;
import static org.sonar.java.ast.api.JavaPunctuator.TILDA;
import static org.sonar.java.ast.api.JavaTokenType.CHARACTER_LITERAL;
import static org.sonar.java.ast.api.JavaTokenType.DOUBLE_LITERAL;
import static org.sonar.java.ast.api.JavaTokenType.FLOAT_LITERAL;
import static org.sonar.java.ast.api.JavaTokenType.INTEGER_LITERAL;
import static org.sonar.java.ast.api.JavaTokenType.LONG_LITERAL;

public class JavaGrammarImpl extends JavaGrammar {

  public JavaGrammarImpl() {
    ge.is(GT, adjacent(EQU));
    sr.is(GT, adjacent(GT));
    srequ.is(GT, adjacent(GT), adjacent(EQU));
    bsr.is(GT, adjacent(GT), adjacent(GT));
    bsrequ.is(GT, adjacent(GT), adjacent(GT), adjacent(EQU));

    compilationsUnits();
    classDeclaration();
    interfaceDeclarations();
    enums();
    formalParameters();
    blocksAndStatements();
    expressions();
    types();
    annotations();
    literals();
  }

  /**
   * 3.10. Literals
   */
  private void literals() {
    literal.is(firstOf(
        FLOAT_LITERAL,
        INTEGER_LITERAL,
        DOUBLE_LITERAL,
        LONG_LITERAL,
        CHARACTER_LITERAL,
        LITERAL,
        TRUE,
        FALSE,
        NULL));
  }

  /**
   * 4. Types, Values and Variables
   */
  private void types() {
    type.is(firstOf(basicType, classType), o2n(dim));
    referenceType.is(firstOf(
        and(basicType, o2n(dim)),
        and(classType, o2n(dim))));
    classType.is(IDENTIFIER, opt(typeArguments), o2n(DOT, IDENTIFIER, opt(typeArguments)));
    classTypeList.is(classType, o2n(COMMA, classType));
    typeArguments.is(LT, typeArgument, o2n(COMMA, typeArgument), GT);
    typeArgument.is(firstOf(
        referenceType,
        and(QUERY, opt(firstOf(EXTENDS, SUPER), referenceType))));
    typeParameters.is(LT, typeParameter, o2n(COMMA, typeParameter), GT);
    typeParameter.is(IDENTIFIER, opt(EXTENDS, bound));
    bound.is(classType, o2n(AND, classType));
    modifier.is(firstOf(
        annotation,
        PUBLIC,
        PROTECTED,
        PRIVATE,
        STATIC,
        ABSTRACT,
        FINAL,
        NATIVE,
        SYNCHRONIZED,
        TRANSIENT,
        VOLATILE,
        STRICTFP));
  }

  /**
   * 7.3. Compilation Units
   */
  private void compilationsUnits() {
    compilationUnit.is(opt(packageDeclaration), o2n(importDeclaration), o2n(typeDeclaration), EOF);

    packageDeclaration.is(o2n(annotation), PACKAGE, qualifiedIdentifier, SEMI);
    importDeclaration.is(IMPORT, opt(STATIC), qualifiedIdentifier, opt(DOT, STAR), SEMI);
    typeDeclaration.is(firstOf(
        and(o2n(modifier), firstOf(classDeclaration, enumDeclaration, interfaceDeclaration, annotationTypeDeclaration)),
        SEMI));
  }

  /**
   * 8.1. Class Declaration
   */
  private void classDeclaration() {
    classDeclaration.is(CLASS, IDENTIFIER, opt(typeParameters), opt(EXTENDS, classType), opt(IMPLEMENTS, classTypeList), classBody);

    classBody.is(LWING, o2n(classBodyDeclaration), RWING);
    classBodyDeclaration.is(firstOf(
        SEMI,
        classInitDeclaration,
        and(o2n(modifier), memberDecl)));
    classInitDeclaration.is(opt(STATIC), block);
    memberDecl.is(firstOf(
        and(typeParameters, genericMethodOrConstructorRest),
        and(type, IDENTIFIER, methodDeclaratorRest),
        fieldDeclaration,
        and(VOID, IDENTIFIER, voidMethodDeclaratorRest),
        and(IDENTIFIER, constructorDeclaratorRest),
        interfaceDeclaration,
        classDeclaration,
        enumDeclaration,
        annotationTypeDeclaration));
    fieldDeclaration.is(type, variableDeclarators, SEMI);
    genericMethodOrConstructorRest.is(firstOf(
        and(firstOf(type, VOID), IDENTIFIER, methodDeclaratorRest),
        and(IDENTIFIER, constructorDeclaratorRest)));
    methodDeclaratorRest.is(formalParameters, o2n(dim), opt(THROWS, classTypeList), firstOf(methodBody, SEMI));
    voidMethodDeclaratorRest.is(formalParameters, opt(THROWS, classTypeList), firstOf(methodBody, SEMI));
    constructorDeclaratorRest.is(formalParameters, opt(THROWS, classTypeList), methodBody);
    methodBody.is(block);
  }

  /**
   * 8.9. Enums
   */
  private void enums() {
    enumDeclaration.is(ENUM, IDENTIFIER, opt(IMPLEMENTS, classTypeList), enumBody);
    enumBody.is(LWING, opt(enumConstants), opt(COMMA), opt(enumBodyDeclarations), RWING);
    enumConstants.is(enumConstant, o2n(COMMA, enumConstant));
    enumConstant.is(o2n(annotation), IDENTIFIER, opt(arguments), opt(classBody));
    enumBodyDeclarations.is(SEMI, o2n(classBodyDeclaration));
  }

  /**
   * 9.1. Interface Declarations
   */
  private void interfaceDeclarations() {
    interfaceDeclaration.is(INTERFACE, IDENTIFIER, opt(typeParameters), opt(EXTENDS, classTypeList), interfaceBody);

    interfaceBody.is(LWING, o2n(interfaceBodyDeclaration), RWING);
    interfaceBodyDeclaration.is(firstOf(
        and(o2n(modifier), interfaceMemberDecl),
        SEMI));
    interfaceMemberDecl.is(firstOf(
        interfaceMethodOrFieldDecl,
        interfaceGenericMethodDecl,
        and(VOID, IDENTIFIER, voidInterfaceMethodDeclaratorsRest),
        interfaceDeclaration,
        annotationTypeDeclaration,
        classDeclaration,
        enumDeclaration));
    interfaceMethodOrFieldDecl.is(type, IDENTIFIER, interfaceMethodOrFieldRest);
    interfaceMethodOrFieldRest.is(firstOf(
        and(constantDeclaratorsRest, SEMI),
        interfaceMethodDeclaratorRest));
    interfaceMethodDeclaratorRest.is(formalParameters, o2n(dim), opt(THROWS, classTypeList), SEMI);
    interfaceGenericMethodDecl.is(typeParameters, firstOf(type, VOID), IDENTIFIER, interfaceMethodDeclaratorRest);
    voidInterfaceMethodDeclaratorsRest.is(formalParameters, opt(THROWS, classTypeList), SEMI);
    constantDeclaratorsRest.is(constantDeclaratorRest, o2n(COMMA, constantDeclarator));
    constantDeclarator.is(IDENTIFIER, constantDeclaratorRest);
    constantDeclaratorRest.is(o2n(dim), EQU, variableInitializer);
  }

  /**
   * 8.4.1. Formal Parameters
   */
  private void formalParameters() {
    formalParameters.is(LPAR, opt(formalParameterDecls), RPAR);
    formalParameter.is(o2n(firstOf(FINAL, annotation)), type, variableDeclaratorId);
    formalParameterDecls.is(o2n(firstOf(FINAL, annotation)), type, formalParametersDeclsRest);
    formalParametersDeclsRest.is(firstOf(
        and(variableDeclaratorId, opt(COMMA, formalParameterDecls)),
        and(ELLIPSIS, variableDeclaratorId)));
    variableDeclaratorId.is(IDENTIFIER, o2n(dim));
  }

  /**
   * 9.7. Annotations
   */
  private void annotations() {
    annotationTypeDeclaration.is(AT, INTERFACE, IDENTIFIER, annotationTypeBody);
    annotationTypeBody.is(LWING, o2n(annotationTypeElementDeclaration), RWING);
    annotationTypeElementDeclaration.is(firstOf(
        and(o2n(modifier), annotationTypeElementRest),
        SEMI));
    annotationTypeElementRest.is(firstOf(
        and(type, annotationMethodOrConstantRest, SEMI),
        classDeclaration,
        enumDeclaration,
        interfaceDeclaration,
        annotationTypeDeclaration));
    annotationMethodOrConstantRest.is(firstOf(
        annotationMethodRest,
        annotationConstantRest));
    annotationMethodRest.is(IDENTIFIER, LPAR, RPAR, opt(defaultValue));
    annotationConstantRest.is(variableDeclarators);
    defaultValue.is(DEFAULT, elementValue);
    annotation.is(AT, qualifiedIdentifier, opt(annotationRest));
    annotationRest.is(firstOf(
        normalAnnotationRest,
        singleElementAnnotationRest));
    normalAnnotationRest.is(LPAR, opt(elementValuePairs), RPAR);
    elementValuePairs.is(elementValuePair, o2n(COMMA, elementValuePair));
    elementValuePair.is(IDENTIFIER, EQU, elementValue);
    elementValue.is(firstOf(
        conditionalExpression,
        annotation,
        elementValueArrayInitializer));
    elementValueArrayInitializer.is(LWING, opt(elementValues), opt(COMMA), RWING);
    elementValues.is(elementValue, o2n(COMMA, elementValue));
    singleElementAnnotationRest.is(LPAR, elementValue, RPAR);
  }

  /**
   * 14. Blocks and Statements
   */
  private void blocksAndStatements() {
    // 14.2. Blocks
    block.is(LWING, blockStatements, RWING);
    blockStatements.is(o2n(blockStatement));
    blockStatement.is(firstOf(
        localVariableDeclarationStatement,
        and(o2n(modifier), firstOf(classDeclaration, enumDeclaration)),
        statement));

    // 14.4. Local Variable Declaration Statements
    localVariableDeclarationStatement.is(variableModifiers, type, variableDeclarators, SEMI);
    variableModifiers.is(o2n(firstOf(
        annotation,
        FINAL)));
    variableDeclarators.is(variableDeclarator, o2n(COMMA, variableDeclarator));
    variableDeclarator.is(IDENTIFIER, o2n(dim), opt(EQU, variableInitializer));

    // 14.5. Statements
    statement.is(firstOf(
        block,
        assertStatement,
        ifStatement,
        forStatement,
        whileStatement,
        doStatement,
        tryStatement,
        switchStatement,
        synchronizedStatement,
        returnStatement,
        throwStatement,
        breakStatement,
        continueStatement,
        labeledStatement,
        expressionStatement,
        emptyStatement));

    // 14.6. The Empty Statement
    emptyStatement.is(SEMI);
    // 14.7. Labeled Statements
    labeledStatement.is(IDENTIFIER, COLON, statement);
    // 14.8. Expression Statements
    expressionStatement.is(statementExpression, SEMI);
    // 14.9. The if Statement
    ifStatement.is(IF, parExpression, statement, opt(ELSE, statement));
    // 14.10. The assert Statement
    assertStatement.is(ASSERT, expression, opt(COLON, expression), SEMI);

    // 14.11. The switch statement
    switchStatement.is(SWITCH, parExpression, LWING, switchBlockStatementGroups, RWING);
    switchBlockStatementGroups.is(o2n(switchBlockStatementGroup));
    switchBlockStatementGroup.is(switchLabel, blockStatements);
    switchLabel.is(firstOf(
        and(CASE, constantExpression, COLON),
        and(CASE, enumConstantName, COLON),
        and(DEFAULT, COLON)));
    enumConstantName.is(IDENTIFIER);

    // 14.12. The while Statement
    whileStatement.is(WHILE, parExpression, statement);
    // 14.13. The do Statement
    doStatement.is(DO, statement, WHILE, parExpression, SEMI);

    // 14.14. The for Statement
    forStatement.is(firstOf(
        and(FOR, LPAR, opt(forInit), SEMI, opt(expression), SEMI, opt(forUpdate), RPAR, statement),
        and(FOR, LPAR, formalParameter, COLON, expression, RPAR, statement)));
    forInit.is(firstOf(
        and(o2n(firstOf(FINAL, annotation)), type, variableDeclarators),
        and(statementExpression, o2n(COMMA, statementExpression))));
    forUpdate.is(statementExpression, o2n(COMMA, statementExpression));

    // 14.15. The break Statement
    breakStatement.is(BREAK, opt(IDENTIFIER), SEMI);
    // 14.16. The continue Statement
    continueStatement.is(CONTINUE, opt(IDENTIFIER), SEMI);
    // 14.17. The return Statement
    returnStatement.is(RETURN, opt(expression), SEMI);
    // 14.18. The throw Statement
    throwStatement.is(THROW, expression, SEMI);
    // 14.19. The synchronized Statement
    synchronizedStatement.is(SYNCHRONIZED, parExpression, block);

    // 14.20. The try Statement
    tryStatement.is(firstOf(
        and(TRY, block, firstOf(and(one2n(catchClause), opt(finally_)), finally_)),
        tryWithResourcesStatement));
    tryWithResourcesStatement.is(TRY, resourceSpecification, block, o2n(catchClause), opt(finally_));
    resourceSpecification.is(LPAR, resource, o2n(SEMI, resource), opt(SEMI), RPAR);
    resource.is(opt(variableModifiers), type, variableDeclaratorId, EQU, expression);

    catchClause.is(CATCH, LPAR, catchFormalParameter, RPAR, block);
    catchFormalParameter.is(opt(variableModifiers), catchType, variableDeclaratorId);
    catchType.is(classType, o2n(OR, classType));

    finally_.is(FINALLY, block);
  }

  /**
   * 15. Expressions
   */
  private void expressions() {
    statementExpression.is(expression);
    constantExpression.is(expression);
    expression.is(assignmentExpression);
    assignmentExpression.is(conditionalExpression, o2n(assignmentOperator, conditionalExpression)).skipIfOneChild();
    assignmentOperator.is(firstOf(
        EQU,
        PLUSEQU,
        MINUSEQU,
        STAREQU,
        DIVEQU,
        ANDEQU,
        OREQU,
        HATEQU,
        MODEQU,
        SLEQU,
        srequ,
        bsrequ));
    conditionalExpression.is(conditionalOrExpression, o2n(QUERY, expression, COLON, conditionalOrExpression)).skipIfOneChild();
    conditionalOrExpression.is(conditionalAndExpression, o2n(OROR, conditionalAndExpression)).skipIfOneChild();
    conditionalAndExpression.is(inclusiveOrExpression, o2n(ANDAND, inclusiveOrExpression)).skipIfOneChild();
    inclusiveOrExpression.is(exclusiveOrExpression, o2n(OR, exclusiveOrExpression)).skipIfOneChild();
    exclusiveOrExpression.is(andExpression, o2n(HAT, andExpression)).skipIfOneChild();
    andExpression.is(equalityExpression, o2n(AND, equalityExpression)).skipIfOneChild();
    equalityExpression.is(relationalExpression, o2n(firstOf(EQUAL, NOTEQUAL), relationalExpression)).skipIfOneChild();
    relationalExpression.is(shiftExpression, o2n(firstOf(
        and(firstOf(ge, GT, LE, LT), shiftExpression),
        and(INSTANCEOF, referenceType)))).skipIfOneChild();
    shiftExpression.is(additiveExpression, o2n(firstOf(SL, bsr, sr), additiveExpression)).skipIfOneChild();
    additiveExpression.is(multiplicativeExpression, o2n(firstOf(PLUS, MINUS), multiplicativeExpression)).skipIfOneChild();
    multiplicativeExpression.is(unaryExpression, o2n(firstOf(STAR, DIV, MOD), unaryExpression)).skipIfOneChild();
    unaryExpression.is(firstOf(
        and(prefixOp, unaryExpression),
        and(LPAR, type, RPAR, unaryExpression),
        and(primary, o2n(selector), o2n(postFixOp)))).skipIfOneChild();
    primary.is(firstOf(
        parExpression,
        and(nonWildcardTypeArguments, firstOf(explicitGenericInvocationSuffix, and(THIS, arguments))),
        and(THIS, opt(arguments)),
        and(SUPER, superSuffix),
        literal,
        and(NEW, creator),
        and(qualifiedIdentifier, opt(identifierSuffix)),
        and(basicType, o2n(dim), DOT, CLASS),
        and(VOID, DOT, CLASS)));
    identifierSuffix.is(firstOf(
        and(LBRK, firstOf(and(RBRK, o2n(dim), DOT, CLASS), and(expression, RBRK))),
        arguments,
        and(DOT, firstOf(
            CLASS,
            explicitGenericInvocation,
            THIS,
            and(SUPER, arguments),
            and(NEW, opt(nonWildcardTypeArguments), innerCreator)))));
    explicitGenericInvocation.is(nonWildcardTypeArguments, explicitGenericInvocationSuffix);
    nonWildcardTypeArguments.is(LT, referenceType, o2n(COMMA, referenceType), GT);
    explicitGenericInvocationSuffix.is(firstOf(
        and(SUPER, superSuffix),
        and(IDENTIFIER, arguments)));
    prefixOp.is(firstOf(
        INC,
        DEC,
        BANG,
        TILDA,
        PLUS,
        MINUS));
    postFixOp.is(firstOf(
        INC,
        DEC));
    selector.is(firstOf(
        and(DOT, IDENTIFIER, opt(arguments)),
        and(DOT, explicitGenericInvocation),
        and(DOT, THIS),
        and(DOT, SUPER, superSuffix),
        and(DOT, NEW, opt(nonWildcardTypeArguments), innerCreator),
        dimExpr));
    superSuffix.is(firstOf(
        arguments,
        and(DOT, IDENTIFIER, opt(arguments))));
    basicType.is(firstOf(
        BYTE,
        SHORT,
        CHAR,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN));
    arguments.is(LPAR, opt(expression, o2n(COMMA, expression)), RPAR);
    creator.is(firstOf(
        and(opt(nonWildcardTypeArguments), createdName, classCreatorRest),
        and(opt(nonWildcardTypeArguments), firstOf(classType, basicType), arrayCreatorRest)));
    createdName.is(IDENTIFIER, opt(nonWildcardTypeArguments), o2n(DOT, IDENTIFIER, opt(nonWildcardTypeArguments)));
    innerCreator.is(IDENTIFIER, classCreatorRest);
    arrayCreatorRest.is(LBRK, firstOf(
        and(RBRK, o2n(dim), arrayInitializer),
        and(expression, RBRK, o2n(dimExpr), o2n(dim))));
    classCreatorRest.is(opt(diamond), arguments, opt(classBody));
    diamond.is(LT, GT);
    arrayInitializer.is(LWING, opt(variableInitializer, o2n(COMMA, variableInitializer)), opt(COMMA), RWING);
    variableInitializer.is(firstOf(arrayInitializer, expression));
    parExpression.is(LPAR, expression, RPAR);
    qualifiedIdentifier.is(IDENTIFIER, o2n(DOT, IDENTIFIER));
    dim.is(LBRK, RBRK);
    dimExpr.is(LBRK, expression, RBRK);
  }

}
