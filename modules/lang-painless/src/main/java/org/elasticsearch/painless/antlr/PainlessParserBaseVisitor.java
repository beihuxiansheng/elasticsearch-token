begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// ANTLR GENERATED CODE: DO NOT EDIT
end_comment

begin_package
DECL|package|org.elasticsearch.painless.antlr
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|antlr
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
name|tree
operator|.
name|AbstractParseTreeVisitor
import|;
end_import

begin_comment
comment|/**  * This class provides an empty implementation of {@link PainlessParserVisitor},  * which can be extended to create a visitor which only needs to handle a subset  * of the available methods.  *  * @param<T> The return type of the visit operation. Use {@link Void} for  * operations with no return type.  */
end_comment

begin_class
DECL|class|PainlessParserBaseVisitor
class|class
name|PainlessParserBaseVisitor
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractParseTreeVisitor
argument_list|<
name|T
argument_list|>
implements|implements
name|PainlessParserVisitor
argument_list|<
name|T
argument_list|>
block|{
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitSource
annotation|@
name|Override
specifier|public
name|T
name|visitSource
parameter_list|(
name|PainlessParser
operator|.
name|SourceContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitFunction
annotation|@
name|Override
specifier|public
name|T
name|visitFunction
parameter_list|(
name|PainlessParser
operator|.
name|FunctionContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitParameters
annotation|@
name|Override
specifier|public
name|T
name|visitParameters
parameter_list|(
name|PainlessParser
operator|.
name|ParametersContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitIf
annotation|@
name|Override
specifier|public
name|T
name|visitIf
parameter_list|(
name|PainlessParser
operator|.
name|IfContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitWhile
annotation|@
name|Override
specifier|public
name|T
name|visitWhile
parameter_list|(
name|PainlessParser
operator|.
name|WhileContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitDo
annotation|@
name|Override
specifier|public
name|T
name|visitDo
parameter_list|(
name|PainlessParser
operator|.
name|DoContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitFor
annotation|@
name|Override
specifier|public
name|T
name|visitFor
parameter_list|(
name|PainlessParser
operator|.
name|ForContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitEach
annotation|@
name|Override
specifier|public
name|T
name|visitEach
parameter_list|(
name|PainlessParser
operator|.
name|EachContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitDecl
annotation|@
name|Override
specifier|public
name|T
name|visitDecl
parameter_list|(
name|PainlessParser
operator|.
name|DeclContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitContinue
annotation|@
name|Override
specifier|public
name|T
name|visitContinue
parameter_list|(
name|PainlessParser
operator|.
name|ContinueContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitBreak
annotation|@
name|Override
specifier|public
name|T
name|visitBreak
parameter_list|(
name|PainlessParser
operator|.
name|BreakContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitReturn
annotation|@
name|Override
specifier|public
name|T
name|visitReturn
parameter_list|(
name|PainlessParser
operator|.
name|ReturnContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitTry
annotation|@
name|Override
specifier|public
name|T
name|visitTry
parameter_list|(
name|PainlessParser
operator|.
name|TryContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitThrow
annotation|@
name|Override
specifier|public
name|T
name|visitThrow
parameter_list|(
name|PainlessParser
operator|.
name|ThrowContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitExpr
annotation|@
name|Override
specifier|public
name|T
name|visitExpr
parameter_list|(
name|PainlessParser
operator|.
name|ExprContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitTrailer
annotation|@
name|Override
specifier|public
name|T
name|visitTrailer
parameter_list|(
name|PainlessParser
operator|.
name|TrailerContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitBlock
annotation|@
name|Override
specifier|public
name|T
name|visitBlock
parameter_list|(
name|PainlessParser
operator|.
name|BlockContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitEmpty
annotation|@
name|Override
specifier|public
name|T
name|visitEmpty
parameter_list|(
name|PainlessParser
operator|.
name|EmptyContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitInitializer
annotation|@
name|Override
specifier|public
name|T
name|visitInitializer
parameter_list|(
name|PainlessParser
operator|.
name|InitializerContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitAfterthought
annotation|@
name|Override
specifier|public
name|T
name|visitAfterthought
parameter_list|(
name|PainlessParser
operator|.
name|AfterthoughtContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitDeclaration
annotation|@
name|Override
specifier|public
name|T
name|visitDeclaration
parameter_list|(
name|PainlessParser
operator|.
name|DeclarationContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitDecltype
annotation|@
name|Override
specifier|public
name|T
name|visitDecltype
parameter_list|(
name|PainlessParser
operator|.
name|DecltypeContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitDeclvar
annotation|@
name|Override
specifier|public
name|T
name|visitDeclvar
parameter_list|(
name|PainlessParser
operator|.
name|DeclvarContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitTrap
annotation|@
name|Override
specifier|public
name|T
name|visitTrap
parameter_list|(
name|PainlessParser
operator|.
name|TrapContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitDelimiter
annotation|@
name|Override
specifier|public
name|T
name|visitDelimiter
parameter_list|(
name|PainlessParser
operator|.
name|DelimiterContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitSingle
annotation|@
name|Override
specifier|public
name|T
name|visitSingle
parameter_list|(
name|PainlessParser
operator|.
name|SingleContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitComp
annotation|@
name|Override
specifier|public
name|T
name|visitComp
parameter_list|(
name|PainlessParser
operator|.
name|CompContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitBool
annotation|@
name|Override
specifier|public
name|T
name|visitBool
parameter_list|(
name|PainlessParser
operator|.
name|BoolContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitConditional
annotation|@
name|Override
specifier|public
name|T
name|visitConditional
parameter_list|(
name|PainlessParser
operator|.
name|ConditionalContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitAssignment
annotation|@
name|Override
specifier|public
name|T
name|visitAssignment
parameter_list|(
name|PainlessParser
operator|.
name|AssignmentContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitBinary
annotation|@
name|Override
specifier|public
name|T
name|visitBinary
parameter_list|(
name|PainlessParser
operator|.
name|BinaryContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitPre
annotation|@
name|Override
specifier|public
name|T
name|visitPre
parameter_list|(
name|PainlessParser
operator|.
name|PreContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitPost
annotation|@
name|Override
specifier|public
name|T
name|visitPost
parameter_list|(
name|PainlessParser
operator|.
name|PostContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitRead
annotation|@
name|Override
specifier|public
name|T
name|visitRead
parameter_list|(
name|PainlessParser
operator|.
name|ReadContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitNumeric
annotation|@
name|Override
specifier|public
name|T
name|visitNumeric
parameter_list|(
name|PainlessParser
operator|.
name|NumericContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitTrue
annotation|@
name|Override
specifier|public
name|T
name|visitTrue
parameter_list|(
name|PainlessParser
operator|.
name|TrueContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitFalse
annotation|@
name|Override
specifier|public
name|T
name|visitFalse
parameter_list|(
name|PainlessParser
operator|.
name|FalseContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitNull
annotation|@
name|Override
specifier|public
name|T
name|visitNull
parameter_list|(
name|PainlessParser
operator|.
name|NullContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitOperator
annotation|@
name|Override
specifier|public
name|T
name|visitOperator
parameter_list|(
name|PainlessParser
operator|.
name|OperatorContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitCast
annotation|@
name|Override
specifier|public
name|T
name|visitCast
parameter_list|(
name|PainlessParser
operator|.
name|CastContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitDynamic
annotation|@
name|Override
specifier|public
name|T
name|visitDynamic
parameter_list|(
name|PainlessParser
operator|.
name|DynamicContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitStatic
annotation|@
name|Override
specifier|public
name|T
name|visitStatic
parameter_list|(
name|PainlessParser
operator|.
name|StaticContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitNewarray
annotation|@
name|Override
specifier|public
name|T
name|visitNewarray
parameter_list|(
name|PainlessParser
operator|.
name|NewarrayContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitExprprec
annotation|@
name|Override
specifier|public
name|T
name|visitExprprec
parameter_list|(
name|PainlessParser
operator|.
name|ExprprecContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitChainprec
annotation|@
name|Override
specifier|public
name|T
name|visitChainprec
parameter_list|(
name|PainlessParser
operator|.
name|ChainprecContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitString
annotation|@
name|Override
specifier|public
name|T
name|visitString
parameter_list|(
name|PainlessParser
operator|.
name|StringContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitRegex
annotation|@
name|Override
specifier|public
name|T
name|visitRegex
parameter_list|(
name|PainlessParser
operator|.
name|RegexContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitVariable
annotation|@
name|Override
specifier|public
name|T
name|visitVariable
parameter_list|(
name|PainlessParser
operator|.
name|VariableContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitCalllocal
annotation|@
name|Override
specifier|public
name|T
name|visitCalllocal
parameter_list|(
name|PainlessParser
operator|.
name|CalllocalContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitNewobject
annotation|@
name|Override
specifier|public
name|T
name|visitNewobject
parameter_list|(
name|PainlessParser
operator|.
name|NewobjectContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitSecondary
annotation|@
name|Override
specifier|public
name|T
name|visitSecondary
parameter_list|(
name|PainlessParser
operator|.
name|SecondaryContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitCallinvoke
annotation|@
name|Override
specifier|public
name|T
name|visitCallinvoke
parameter_list|(
name|PainlessParser
operator|.
name|CallinvokeContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitFieldaccess
annotation|@
name|Override
specifier|public
name|T
name|visitFieldaccess
parameter_list|(
name|PainlessParser
operator|.
name|FieldaccessContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitBraceaccess
annotation|@
name|Override
specifier|public
name|T
name|visitBraceaccess
parameter_list|(
name|PainlessParser
operator|.
name|BraceaccessContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitArguments
annotation|@
name|Override
specifier|public
name|T
name|visitArguments
parameter_list|(
name|PainlessParser
operator|.
name|ArgumentsContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitArgument
annotation|@
name|Override
specifier|public
name|T
name|visitArgument
parameter_list|(
name|PainlessParser
operator|.
name|ArgumentContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitLambda
annotation|@
name|Override
specifier|public
name|T
name|visitLambda
parameter_list|(
name|PainlessParser
operator|.
name|LambdaContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitLamtype
annotation|@
name|Override
specifier|public
name|T
name|visitLamtype
parameter_list|(
name|PainlessParser
operator|.
name|LamtypeContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitFuncref
annotation|@
name|Override
specifier|public
name|T
name|visitFuncref
parameter_list|(
name|PainlessParser
operator|.
name|FuncrefContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitClassFuncref
annotation|@
name|Override
specifier|public
name|T
name|visitClassFuncref
parameter_list|(
name|PainlessParser
operator|.
name|ClassFuncrefContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitConstructorFuncref
annotation|@
name|Override
specifier|public
name|T
name|visitConstructorFuncref
parameter_list|(
name|PainlessParser
operator|.
name|ConstructorFuncrefContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitCapturingFuncref
annotation|@
name|Override
specifier|public
name|T
name|visitCapturingFuncref
parameter_list|(
name|PainlessParser
operator|.
name|CapturingFuncrefContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>The default implementation returns the result of calling    * {@link #visitChildren} on {@code ctx}.</p>    */
DECL|method|visitLocalFuncref
annotation|@
name|Override
specifier|public
name|T
name|visitLocalFuncref
parameter_list|(
name|PainlessParser
operator|.
name|LocalFuncrefContext
name|ctx
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|ctx
argument_list|)
return|;
block|}
block|}
end_class

end_unit

