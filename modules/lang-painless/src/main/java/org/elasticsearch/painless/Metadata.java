begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
package|;
end_package

begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|v4
operator|.
name|runtime
operator|.
name|ParserRuleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|v4
operator|.
name|runtime
operator|.
name|tree
operator|.
name|ParseTree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|Cast
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|PainlessParser
operator|.
name|ExpressionContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|PainlessParser
operator|.
name|PrecedenceContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Metadata is a wrapper for all the data that is collected by the {@link Analyzer}.  Each node in the ANTLR parse tree  * will have one of the types of metadata to store information used either in a different node by the analyzer  * or by the {@link Writer} during byte code generation.  Metadata also contains several objects passed into the  * {@link Analyzer} and {@link Writer} used during compilation including the {@link Definition}, the source code,  * the root of the ANTLR parse tree, and the {@link CompilerSettings}.  */
end_comment

begin_class
DECL|class|Metadata
class|class
name|Metadata
block|{
comment|/**      * StatementMetadata is used to store metadata mostly about      * control flow for ANTLR nodes related to if/else, do, while, for, etc.      */
DECL|class|StatementMetadata
specifier|static
class|class
name|StatementMetadata
block|{
comment|/**          * The source variable is the ANTLR node used to generate this metadata.          */
DECL|field|source
specifier|final
name|ParserRuleContext
name|source
decl_stmt|;
comment|/**          * The lastSource variable will be set to true when the final statement from the root ANTLR node is about          * to be visited.  This is used to determine whether or not the auto-return feature is allowed to be used,          * and if a null return value needs to be generated automatically since a return value is always required.          */
DECL|field|lastSource
name|boolean
name|lastSource
init|=
literal|false
decl_stmt|;
comment|/**          * The beginLoop variable will be set to true whenever a loop node is initially visited including inner          * loops.  This will not be propagated down the parse tree afterwards, though.  This is used to determine          * whether or not inLoop should be set further down the tree.  Note that inLoop alone is not enough          * information to determine whether we are in the last statement of a loop because we may inside of          * multiple loops, so this variable is necessary.          */
DECL|field|beginLoop
name|boolean
name|beginLoop
init|=
literal|false
decl_stmt|;
comment|/**          * The inLoop variable is set to true when inside a loop.  This will be propagated down the parse tree.  This          * is used to determine whether or not continue and break statements are legal.          */
DECL|field|inLoop
name|boolean
name|inLoop
init|=
literal|false
decl_stmt|;
comment|/**          * The lastLoop variable is set to true when the final statement of a loop is reached.  This will be          * propagated down the parse tree until another loop is reached and then will not be propagated further for          * the current loop.  This is used to determine whether or not a continue statement is superfluous.          */
DECL|field|lastLoop
name|boolean
name|lastLoop
init|=
literal|false
decl_stmt|;
comment|/**          * The methodEscape variable is set to true when a statement would cause the method to potentially exit.  This          * includes return, throw, and continuous loop statements.  Note that a catch statement may possibly          * reset this to false after a throw statement.  This will be propagated up the tree as far as necessary.          * This is used by the {@link Writer} to ensure that superfluous statements aren't unnecessarily written          * into generated bytecode.          */
DECL|field|methodEscape
name|boolean
name|methodEscape
init|=
literal|false
decl_stmt|;
comment|/**          * The loopEscape variable is set to true when a loop is going to be exited.  This may be caused by a number of          * different statements including continue, break, return, etc.  This will only be propagated as far as the          * loop node.  This is used to ensure that in certain case an infinite loop will be caught at          * compile-time rather than run-time.          */
DECL|field|loopEscape
name|boolean
name|loopEscape
init|=
literal|false
decl_stmt|;
comment|/**          * The allLast variable is set whenever a final statement in a block is reached. This includes the end of loop,          * if, else, etc.  This will be only propagated to the top of the block statement ANTLR node.          * This is used to ensure that there are no unreachable statements within the script.          */
DECL|field|allLast
name|boolean
name|allLast
init|=
literal|false
decl_stmt|;
comment|/**          * The anyContinue will be set to true when a continue statement is visited.  This will be propagated to the          * loop node it's within.  This is used to ensure that in certain case an infinite loop will be caught at          * compile-time rather than run-time.          */
DECL|field|anyContinue
name|boolean
name|anyContinue
init|=
literal|false
decl_stmt|;
comment|/**          * The anyBreak will be set to true when a break statement is visited.  This will be propagated to the          * loop node it's within.  This is used to in conjunction with methodEscape to ensure there are no unreachable          * statements within the script.          */
DECL|field|anyBreak
name|boolean
name|anyBreak
init|=
literal|false
decl_stmt|;
comment|/**          * The count variable is used as a rudimentary count of statements within a loop.  This will be used in          * the {@link Writer} to keep a count of statements that have been executed at run-time to ensure that a loop          * will exit if it runs too long.          */
DECL|field|count
name|int
name|count
init|=
literal|0
decl_stmt|;
comment|/**          * The exception variable is used to store the exception type when a throw node is visited.  This is used by          * the {@link Writer} to write the correct type of exception in the generated byte code.          */
DECL|field|exception
name|Type
name|exception
init|=
literal|null
decl_stmt|;
comment|/**          * The slot variable is used to store the place on the stack of where a thrown exception will be stored to.          * This is used by the {@link Writer}.          */
DECL|field|slot
name|int
name|slot
init|=
operator|-
literal|1
decl_stmt|;
comment|/**          * Constructor.          * @param source The associated ANTLR node.          */
DECL|method|StatementMetadata
specifier|private
name|StatementMetadata
parameter_list|(
specifier|final
name|ParserRuleContext
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
block|}
comment|/**      * ExpressionMetadata is used to store metadata mostly about constants and casting      * for ANTLR nodes related to mathematical operations.      */
DECL|class|ExpressionMetadata
specifier|static
class|class
name|ExpressionMetadata
block|{
comment|/**          * The source variable is the ANTLR node used to generate this metadata.          */
DECL|field|source
specifier|final
name|ParserRuleContext
name|source
decl_stmt|;
comment|/**          * The read variable is used to determine whether or not the value of an expression will be read from.          * This is set to false when the expression is the left-hand side of an assignment that is not chained or          * when a method call is made alone.  This will propagate down the tree as far as necessary.          * The {@link Writer} uses this to determine when a value may need to be popped from the stack          * such as when a method call returns a value that is never read.          */
DECL|field|read
name|boolean
name|read
init|=
literal|true
decl_stmt|;
comment|/**          * The statement variable is set true when an expression is a complete meaning that there is some sort          * of effect on a variable or a method call is made.  This will propagate up the tree as far as necessary.          * This prevents statements that have no effect on the output of a script from being executed.          */
DECL|field|statement
name|boolean
name|statement
init|=
literal|false
decl_stmt|;
comment|/**          * The preConst variable is set to a non-null value when a constant statement is made in a script.  This is          * used to track the constant value prior to any casts being made on an ANTLR node.          */
DECL|field|preConst
name|Object
name|preConst
init|=
literal|null
decl_stmt|;
comment|/**          * The postConst variable is set to a non-null value when a cast is made on a node where a preConst variable          * has already been set when the cast would leave the constant as a non-object value except in the case of a          * String.  This will be propagated up the tree and used to simplify constants when possible such as making          * the value of 2*2 be 4 in the * node, so that the {@link Writer} only has to push a 4 onto the stack.          */
DECL|field|postConst
name|Object
name|postConst
init|=
literal|null
decl_stmt|;
comment|/**          * The isNull variable is set to true when a null constant statement is made in the script.  This allows the          * {@link Writer} to potentially shortcut certain comparison operations.          */
DECL|field|isNull
name|boolean
name|isNull
init|=
literal|false
decl_stmt|;
comment|/**          * The to variable is used to track what an ANTLR node's value should be cast to.  This is set on every ANTLR          * node in the tree, and used by the {@link Writer} to make a run-time cast if necessary in the byte code.          * This is also used by the {@link Analyzer} to determine if a cast is legal.          */
DECL|field|to
name|Type
name|to
init|=
literal|null
decl_stmt|;
comment|/**          * The from variable is used to track what an ANTLR node's value should be cast from.  This is set on every          * ANTLR node in the tree independent of other nodes.  This is used by the {@link Analyzer} to determine if a          * cast is legal.          */
DECL|field|from
name|Type
name|from
init|=
literal|null
decl_stmt|;
comment|/**          * The explicit variable is set to true when a cast is explicitly made in the script.  This tracks whether          * or not a cast is a legal up cast.          */
DECL|field|explicit
name|boolean
name|explicit
init|=
literal|false
decl_stmt|;
comment|/**          * The typesafe variable is set to true when a dynamic type is used as part of an expression.  This propagates          * up the tree to the top of the expression.  This allows for implicit up casts throughout the expression and          * is used by the {@link Analyzer}.          */
DECL|field|typesafe
name|boolean
name|typesafe
init|=
literal|true
decl_stmt|;
comment|/**          * This is set to the combination of the to and from variables at the end of each node visit in the          * {@link Analyzer}.  This is set on every ANTLR node in the tree independent of other nodes, and is          * used by {@link Writer} to make a run-time cast if necessary in the byte code.          */
DECL|field|cast
name|Cast
name|cast
init|=
literal|null
decl_stmt|;
comment|/**          * Constructor.          * @param source The associated ANTLR node.          */
DECL|method|ExpressionMetadata
specifier|private
name|ExpressionMetadata
parameter_list|(
specifier|final
name|ParserRuleContext
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
block|}
comment|/**      * ExternalMetadata is used to store metadata about the overall state of a variable/method chain such as      * '(int)x.get(3)' where each piece of that chain is broken into it's indiviual pieces and stored in      * {@link ExtNodeMetadata}.      */
DECL|class|ExternalMetadata
specifier|static
class|class
name|ExternalMetadata
block|{
comment|/**          * The source variable is the ANTLR node used to generate this metadata.          */
DECL|field|source
specifier|final
name|ParserRuleContext
name|source
decl_stmt|;
comment|/**          * The read variable is set to true when the value of a variable/method chain is going to be read from.          * This is used by the {@link Analyzer} to determine if this variable/method chain will be in a standalone          * statement.          */
DECL|field|read
name|boolean
name|read
init|=
literal|false
decl_stmt|;
comment|/**          * The storeExpr variable is set to the right-hand side of an assignment in the variable/method chain if          * necessary.  This is used by the {@link Analyzer} to set the proper metadata for a read versus a write,          * and is used by the {@link Writer} to determine if a bytecode operation should be a load or a store.          */
DECL|field|storeExpr
name|ParserRuleContext
name|storeExpr
init|=
literal|null
decl_stmt|;
comment|/**          * The token variable is set to a constant value of the operator type (+, -, etc.) when a compound assignment          * is being visited.  This is also used by the increment and decrement operators.  This is used by both the          * {@link Analyzer} and {@link Writer} to correctly handle the compound assignment.          */
DECL|field|token
name|int
name|token
init|=
literal|0
decl_stmt|;
comment|/**          * The pre variable is set to true when pre-increment or pre-decrement is visited.  This is used by both the          * {@link Analyzer} and {@link Writer} to correctly handle any reads of the variable/method chain that are          * necessary.          */
DECL|field|pre
name|boolean
name|pre
init|=
literal|false
decl_stmt|;
comment|/**          * The post variable is set to true when post-increment or post-decrement is visited. This is used by both the          * {@link Analyzer} and {@link Writer} to correctly handle any reads of the variable/method chain that are          * necessary.          */
DECL|field|post
name|boolean
name|post
init|=
literal|false
decl_stmt|;
comment|/**          * The scope variable is incremented and decremented when a precedence node is visited as part of a          * variable/method chain.  This is used by the {@link Analyzer} to determine when the final piece of the          * variable/method chain has been reached.          */
DECL|field|scope
name|int
name|scope
init|=
literal|0
decl_stmt|;
comment|/**          * The current variable is set to whatever the current type is within the visited node of the variable/method          * chain.  This changes as the nodes for the variable/method are walked through.  This is used by the          * {@link Analyzer} to make decisions about whether or not a cast is legal, and what methods are available          * for that specific type.          */
DECL|field|current
name|Type
name|current
init|=
literal|null
decl_stmt|;
comment|/**          * The statik variable is set to true when a variable/method chain begins with static type.  This is used by          * the {@link Analyzer} to determine what methods/members are available for that specific type.          */
DECL|field|statik
name|boolean
name|statik
init|=
literal|false
decl_stmt|;
comment|/**          * The statement variable is set to true when a variable/method chain can be standalone statement.  This is          * used by the {@link Analyzer} to error out if there a variable/method chain that is not a statement.          */
DECL|field|statement
name|boolean
name|statement
init|=
literal|false
decl_stmt|;
comment|/**          * The constant variable is set when a String constant is part of the variable/method chain.  String is a          * special case because methods/members need to be able to be called on a String constant, so this can't be          * only as part of {@link ExpressionMetadata}.  This is used by the {@link Writer} to write out the String          * constant in the byte code.          */
DECL|field|constant
name|Object
name|constant
init|=
literal|null
decl_stmt|;
comment|/**          * Constructor.          * @param source The associated ANTLR node.          */
DECL|method|ExternalMetadata
specifier|private
name|ExternalMetadata
parameter_list|(
specifier|final
name|ParserRuleContext
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
block|}
DECL|class|ExtNodeMetadata
specifier|static
class|class
name|ExtNodeMetadata
block|{
comment|/**          * The parent variable is top-level ANTLR node of the variable/method chain.  This is used to retrieve the          * ExternalMetadata for the variable/method chain this ExtNodeMetadata is a piece of.          */
DECL|field|parent
specifier|final
name|ParserRuleContext
name|parent
decl_stmt|;
comment|/**          * The source variable is the ANTLR node used to generate this metadata.          */
DECL|field|source
specifier|final
name|ParserRuleContext
name|source
decl_stmt|;
comment|/**          * The target variable is set to a value based on the type of ANTLR node that is visited.  This is used by          * {@link Writer} to determine whether a cast, store, load, or method call should be written in byte code          * depending on what the target variable is.          */
DECL|field|target
name|Object
name|target
init|=
literal|null
decl_stmt|;
comment|/**          * The last variable is set to true when the last ANTLR node of the variable/method chain is visted.  This is          * used by the {@link Writer} in conjuction with the storeExpr variable to determine whether or not a store          * needs to be written as opposed to a load.          */
DECL|field|last
name|boolean
name|last
init|=
literal|false
decl_stmt|;
comment|/**          * The type variable is set to the type that a visited node ends with.  This is used by both the          * {@link Analyzer} and {@link Writer} to make decisions about compound assignments, String constants, and          * shortcuts.          */
DECL|field|type
name|Type
name|type
init|=
literal|null
decl_stmt|;
comment|/**          * The promote variable is set to the type of a promotion within a compound assignment.  Compound assignments          * may require promotion between the left-hand side variable and right-hand side value.  This is used by the          * {@link Writer} to make the correct decision about the byte code operation.          */
DECL|field|promote
name|Type
name|promote
init|=
literal|null
decl_stmt|;
comment|/**          * The castFrom variable is set during a compound assignment.  This is used by the {@link Writer} to          * cast the values to the promoted type during a compound assignment.          */
DECL|field|castFrom
name|Cast
name|castFrom
init|=
literal|null
decl_stmt|;
comment|/**          * The castTo variable is set during an explicit cast in a variable/method chain or during a compound          * assignment.  This is used by the {@link Writer} to either do an explicit cast, or cast the values          * from the promoted type back to the original type during a compound assignment.          */
DECL|field|castTo
name|Cast
name|castTo
init|=
literal|null
decl_stmt|;
comment|/**          * Constructor.          * @param parent The top-level ANTLR node for the variable/method chain.          * @param source The associated ANTLR node.          */
DECL|method|ExtNodeMetadata
specifier|private
name|ExtNodeMetadata
parameter_list|(
specifier|final
name|ParserRuleContext
name|parent
parameter_list|,
specifier|final
name|ParserRuleContext
name|source
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
block|}
comment|/**      * A utility method to output consistent error messages.      * @param ctx The ANTLR node the error occurred in.      * @return The error message with tacked on line number and character position.      */
DECL|method|error
specifier|static
name|String
name|error
parameter_list|(
specifier|final
name|ParserRuleContext
name|ctx
parameter_list|)
block|{
return|return
literal|"Error ["
operator|+
name|ctx
operator|.
name|getStart
argument_list|()
operator|.
name|getLine
argument_list|()
operator|+
literal|":"
operator|+
name|ctx
operator|.
name|getStart
argument_list|()
operator|.
name|getCharPositionInLine
argument_list|()
operator|+
literal|"]: "
return|;
block|}
comment|/**      * Acts as both the Painless API and white-list for what types and methods are allowed.      */
DECL|field|definition
specifier|final
name|Definition
name|definition
decl_stmt|;
comment|/**      * The original text of the input script.  This is used to write out the source code into      * the byte code file for debugging purposes.      */
DECL|field|source
specifier|final
name|String
name|source
decl_stmt|;
comment|/**      * Toot node of the ANTLR tree for the Painless script.      */
DECL|field|root
specifier|final
name|ParserRuleContext
name|root
decl_stmt|;
comment|/**      * Used to determine certain compile-time constraints such as whether or not numeric overflow is allowed      * and how many statements are allowed before a loop will throw an exception.      */
DECL|field|settings
specifier|final
name|CompilerSettings
name|settings
decl_stmt|;
comment|/**      * Used to determine what slot the input variable is stored in.  This is used in the {@link Writer} whenever      * the input variable is accessed.      */
DECL|field|inputValueSlot
name|int
name|inputValueSlot
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Used to determine what slot the score variable is stored in.  This is used in the {@link Writer} whenever      * the score variable is accessed.      */
DECL|field|scoreValueSlot
name|int
name|scoreValueSlot
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Used to determine what slot the loopCounter variable is stored in.  This is used n the {@link Writer} whenever      * the loop variable is accessed.      */
DECL|field|loopCounterSlot
name|int
name|loopCounterSlot
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Maps the relevant ANTLR node to its metadata.      */
DECL|field|statementMetadata
specifier|private
specifier|final
name|Map
argument_list|<
name|ParserRuleContext
argument_list|,
name|StatementMetadata
argument_list|>
name|statementMetadata
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Maps the relevant ANTLR node to its metadata.      */
DECL|field|expressionMetadata
specifier|private
specifier|final
name|Map
argument_list|<
name|ParserRuleContext
argument_list|,
name|ExpressionMetadata
argument_list|>
name|expressionMetadata
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Maps the relevant ANTLR node to its metadata.      */
DECL|field|externalMetadata
specifier|private
specifier|final
name|Map
argument_list|<
name|ParserRuleContext
argument_list|,
name|ExternalMetadata
argument_list|>
name|externalMetadata
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Maps the relevant ANTLR node to its metadata.      */
DECL|field|extNodeMetadata
specifier|private
specifier|final
name|Map
argument_list|<
name|ParserRuleContext
argument_list|,
name|ExtNodeMetadata
argument_list|>
name|extNodeMetadata
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Constructor.      * @param definition The Painless definition.      * @param source The source text for the script.      * @param root The root ANTLR node.      * @param settings The compile-time settings.      */
DECL|method|Metadata
name|Metadata
parameter_list|(
specifier|final
name|Definition
name|definition
parameter_list|,
specifier|final
name|String
name|source
parameter_list|,
specifier|final
name|ParserRuleContext
name|root
parameter_list|,
specifier|final
name|CompilerSettings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|definition
operator|=
name|definition
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
block|}
comment|/**      * Creates a new StatementMetadata and stores it in the statementMetadata map.      * @param source The ANTLR node for this metadata.      * @return The new StatementMetadata.      */
DECL|method|createStatementMetadata
name|StatementMetadata
name|createStatementMetadata
parameter_list|(
specifier|final
name|ParserRuleContext
name|source
parameter_list|)
block|{
specifier|final
name|StatementMetadata
name|sourcesmd
init|=
operator|new
name|StatementMetadata
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|statementMetadata
operator|.
name|put
argument_list|(
name|source
argument_list|,
name|sourcesmd
argument_list|)
expr_stmt|;
return|return
name|sourcesmd
return|;
block|}
comment|/**      * Retrieves StatementMetadata from the statementMetadata map.      * @param source The ANTLR node for this metadata.      * @return The retrieved StatementMetadata.      */
DECL|method|getStatementMetadata
name|StatementMetadata
name|getStatementMetadata
parameter_list|(
specifier|final
name|ParserRuleContext
name|source
parameter_list|)
block|{
specifier|final
name|StatementMetadata
name|sourcesmd
init|=
name|statementMetadata
operator|.
name|get
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourcesmd
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|error
argument_list|(
name|source
argument_list|)
operator|+
literal|"Statement metadata does not exist at"
operator|+
literal|" the parse node with text ["
operator|+
name|source
operator|.
name|getText
argument_list|()
operator|+
literal|"]."
argument_list|)
throw|;
block|}
return|return
name|sourcesmd
return|;
block|}
comment|/**      * The ANTLR parse tree is modified in one single case; a parent node needs to check a child node to see if it's      * a precedence node, and if so, it must be removed from the tree permanently. Once the ANTLR tree is built,      * precedence nodes are no longer necessary to maintain the correct ordering of the tree, so they only      * add a level of indirection where complicated decisions about metadata passing would have to be made.  This      * method removes the need for those decisions.      * @param source The child ANTLR node to check for precedence.      * @return The updated child ANTLR node.      */
DECL|method|updateExpressionTree
name|ExpressionContext
name|updateExpressionTree
parameter_list|(
name|ExpressionContext
name|source
parameter_list|)
block|{
comment|// Check to see if the ANTLR node is a precedence node.
if|if
condition|(
name|source
operator|instanceof
name|PrecedenceContext
condition|)
block|{
specifier|final
name|ParserRuleContext
name|parent
init|=
name|source
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
comment|// Mark the index of the source node within the list of child nodes from the parent.
for|for
control|(
specifier|final
name|ParseTree
name|child
range|:
name|parent
operator|.
name|children
control|)
block|{
if|if
condition|(
name|child
operator|==
name|source
condition|)
block|{
break|break;
block|}
operator|++
name|index
expr_stmt|;
block|}
comment|// If there are multiple precedence nodes in a row, remove them all.
while|while
condition|(
name|source
operator|instanceof
name|PrecedenceContext
condition|)
block|{
name|source
operator|=
operator|(
operator|(
name|PrecedenceContext
operator|)
name|source
operator|)
operator|.
name|expression
argument_list|()
expr_stmt|;
block|}
comment|// Update the parent node with the child of the precedence node.
name|parent
operator|.
name|children
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
return|return
name|source
return|;
block|}
comment|/**      * Creates a new ExpressionMetadata and stores it in the expressionMetadata map.      * @param source The ANTLR node for this metadata.      * @return The new ExpressionMetadata.      */
DECL|method|createExpressionMetadata
name|ExpressionMetadata
name|createExpressionMetadata
parameter_list|(
name|ParserRuleContext
name|source
parameter_list|)
block|{
specifier|final
name|ExpressionMetadata
name|sourceemd
init|=
operator|new
name|ExpressionMetadata
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|expressionMetadata
operator|.
name|put
argument_list|(
name|source
argument_list|,
name|sourceemd
argument_list|)
expr_stmt|;
return|return
name|sourceemd
return|;
block|}
comment|/**      * Retrieves ExpressionMetadata from the expressionMetadata map.      * @param source The ANTLR node for this metadata.      * @return The retrieved ExpressionMetadata.      */
DECL|method|getExpressionMetadata
name|ExpressionMetadata
name|getExpressionMetadata
parameter_list|(
specifier|final
name|ParserRuleContext
name|source
parameter_list|)
block|{
specifier|final
name|ExpressionMetadata
name|sourceemd
init|=
name|expressionMetadata
operator|.
name|get
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceemd
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|error
argument_list|(
name|source
argument_list|)
operator|+
literal|"Expression metadata does not exist at"
operator|+
literal|" the parse node with text ["
operator|+
name|source
operator|.
name|getText
argument_list|()
operator|+
literal|"]."
argument_list|)
throw|;
block|}
return|return
name|sourceemd
return|;
block|}
comment|/**      * Creates a new ExternalMetadata and stores it in the externalMetadata map.      * @param source The ANTLR node for this metadata.      * @return The new ExternalMetadata.      */
DECL|method|createExternalMetadata
name|ExternalMetadata
name|createExternalMetadata
parameter_list|(
specifier|final
name|ParserRuleContext
name|source
parameter_list|)
block|{
specifier|final
name|ExternalMetadata
name|sourceemd
init|=
operator|new
name|ExternalMetadata
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|externalMetadata
operator|.
name|put
argument_list|(
name|source
argument_list|,
name|sourceemd
argument_list|)
expr_stmt|;
return|return
name|sourceemd
return|;
block|}
comment|/**      * Retrieves ExternalMetadata from the externalMetadata map.      * @param source The ANTLR node for this metadata.      * @return The retrieved ExternalMetadata.      */
DECL|method|getExternalMetadata
name|ExternalMetadata
name|getExternalMetadata
parameter_list|(
specifier|final
name|ParserRuleContext
name|source
parameter_list|)
block|{
specifier|final
name|ExternalMetadata
name|sourceemd
init|=
name|externalMetadata
operator|.
name|get
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceemd
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|error
argument_list|(
name|source
argument_list|)
operator|+
literal|"External metadata does not exist at"
operator|+
literal|" the parse node with text ["
operator|+
name|source
operator|.
name|getText
argument_list|()
operator|+
literal|"]."
argument_list|)
throw|;
block|}
return|return
name|sourceemd
return|;
block|}
comment|/**      * Creates a new ExtNodeMetadata and stores it in the extNodeMetadata map.      * @param source The ANTLR node for this metadata.      * @return The new ExtNodeMetadata.      */
DECL|method|createExtNodeMetadata
name|ExtNodeMetadata
name|createExtNodeMetadata
parameter_list|(
specifier|final
name|ParserRuleContext
name|parent
parameter_list|,
specifier|final
name|ParserRuleContext
name|source
parameter_list|)
block|{
specifier|final
name|ExtNodeMetadata
name|sourceemd
init|=
operator|new
name|ExtNodeMetadata
argument_list|(
name|parent
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|extNodeMetadata
operator|.
name|put
argument_list|(
name|source
argument_list|,
name|sourceemd
argument_list|)
expr_stmt|;
return|return
name|sourceemd
return|;
block|}
comment|/**      * Retrieves ExtNodeMetadata from the extNodeMetadata map.      * @param source The ANTLR node for this metadata.      * @return The retrieved ExtNodeMetadata.      */
DECL|method|getExtNodeMetadata
name|ExtNodeMetadata
name|getExtNodeMetadata
parameter_list|(
specifier|final
name|ParserRuleContext
name|source
parameter_list|)
block|{
specifier|final
name|ExtNodeMetadata
name|sourceemd
init|=
name|extNodeMetadata
operator|.
name|get
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceemd
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|error
argument_list|(
name|source
argument_list|)
operator|+
literal|"External metadata does not exist at"
operator|+
literal|" the parse node with text ["
operator|+
name|source
operator|.
name|getText
argument_list|()
operator|+
literal|"]."
argument_list|)
throw|;
block|}
return|return
name|sourceemd
return|;
block|}
block|}
end_class

end_unit
