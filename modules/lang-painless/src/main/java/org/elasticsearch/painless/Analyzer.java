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
name|elasticsearch
operator|.
name|painless
operator|.
name|PainlessParser
operator|.
name|AfterthoughtContext
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
name|ArgumentsContext
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
name|AssignmentContext
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
name|BinaryContext
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
name|BoolContext
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
name|BreakContext
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
name|CastContext
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
name|CompContext
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
name|ConditionalContext
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
name|ContinueContext
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
name|DeclContext
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
name|DeclarationContext
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
name|DecltypeContext
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
name|DeclvarContext
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
name|DoContext
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
name|EmptyContext
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
name|EmptyscopeContext
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
name|ExprContext
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
name|ExtbraceContext
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
name|ExtcallContext
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
name|ExtcastContext
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
name|ExtdotContext
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
name|ExternalContext
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
name|ExtfieldContext
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
name|ExtnewContext
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
name|ExtprecContext
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
name|ExtstartContext
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
name|ExtstringContext
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
name|ExtvarContext
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
name|FalseContext
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
name|ForContext
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
name|GenericContext
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
name|IdentifierContext
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
name|IfContext
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
name|IncrementContext
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
name|InitializerContext
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
name|MultipleContext
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
name|NullContext
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
name|NumericContext
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
name|PostincContext
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
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|PainlessParser
operator|.
name|PreincContext
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
name|ReturnContext
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
name|SingleContext
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
name|SourceContext
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
name|ThrowContext
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
name|TrapContext
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
name|TrueContext
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
name|TryContext
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
name|UnaryContext
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
name|WhileContext
import|;
end_import

begin_class
DECL|class|Analyzer
class|class
name|Analyzer
extends|extends
name|PainlessParserBaseVisitor
argument_list|<
name|Void
argument_list|>
block|{
DECL|method|analyze
specifier|static
name|void
name|analyze
parameter_list|(
specifier|final
name|Metadata
name|metadata
parameter_list|)
block|{
operator|new
name|Analyzer
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
block|}
DECL|field|statement
specifier|private
specifier|final
name|AnalyzerStatement
name|statement
decl_stmt|;
DECL|field|expression
specifier|private
specifier|final
name|AnalyzerExpression
name|expression
decl_stmt|;
DECL|field|external
specifier|private
specifier|final
name|AnalyzerExternal
name|external
decl_stmt|;
DECL|method|Analyzer
specifier|private
name|Analyzer
parameter_list|(
specifier|final
name|Metadata
name|metadata
parameter_list|)
block|{
specifier|final
name|Definition
name|definition
init|=
name|metadata
operator|.
name|definition
decl_stmt|;
specifier|final
name|AnalyzerUtility
name|utility
init|=
operator|new
name|AnalyzerUtility
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
specifier|final
name|AnalyzerCaster
name|caster
init|=
operator|new
name|AnalyzerCaster
argument_list|(
name|definition
argument_list|)
decl_stmt|;
specifier|final
name|AnalyzerPromoter
name|promoter
init|=
operator|new
name|AnalyzerPromoter
argument_list|(
name|definition
argument_list|)
decl_stmt|;
name|statement
operator|=
operator|new
name|AnalyzerStatement
argument_list|(
name|metadata
argument_list|,
name|this
argument_list|,
name|utility
argument_list|,
name|caster
argument_list|)
expr_stmt|;
name|expression
operator|=
operator|new
name|AnalyzerExpression
argument_list|(
name|metadata
argument_list|,
name|this
argument_list|,
name|caster
argument_list|,
name|promoter
argument_list|)
expr_stmt|;
name|external
operator|=
operator|new
name|AnalyzerExternal
argument_list|(
name|metadata
argument_list|,
name|this
argument_list|,
name|utility
argument_list|,
name|caster
argument_list|,
name|promoter
argument_list|)
expr_stmt|;
name|utility
operator|.
name|incrementScope
argument_list|()
expr_stmt|;
name|utility
operator|.
name|addVariable
argument_list|(
literal|null
argument_list|,
literal|"#this"
argument_list|,
name|definition
operator|.
name|execType
argument_list|)
expr_stmt|;
comment|//
comment|// reserved words parameters.
comment|//
comment|// input map of variables passed to the script. TODO: rename to 'params' since that will be its use
name|metadata
operator|.
name|inputValueSlot
operator|=
name|utility
operator|.
name|addVariable
argument_list|(
literal|null
argument_list|,
literal|"input"
argument_list|,
name|definition
operator|.
name|smapType
argument_list|)
operator|.
name|slot
expr_stmt|;
comment|// scorer parameter passed to the script. internal use only.
name|metadata
operator|.
name|scorerValueSlot
operator|=
name|utility
operator|.
name|addVariable
argument_list|(
literal|null
argument_list|,
literal|"#scorer"
argument_list|,
name|definition
operator|.
name|objectType
argument_list|)
operator|.
name|slot
expr_stmt|;
comment|// doc parameter passed to the script.
comment|// TODO: currently working as a Map<String,Def>, we can do better?
name|metadata
operator|.
name|docValueSlot
operator|=
name|utility
operator|.
name|addVariable
argument_list|(
literal|null
argument_list|,
literal|"doc"
argument_list|,
name|definition
operator|.
name|smapType
argument_list|)
operator|.
name|slot
expr_stmt|;
comment|//
comment|// reserved words implemented as local variables
comment|//
comment|// loop counter to catch runaway scripts. internal use only.
name|metadata
operator|.
name|loopCounterSlot
operator|=
name|utility
operator|.
name|addVariable
argument_list|(
literal|null
argument_list|,
literal|"#loop"
argument_list|,
name|definition
operator|.
name|intType
argument_list|)
operator|.
name|slot
expr_stmt|;
comment|// document's score as a read-only float.
name|metadata
operator|.
name|scoreValueSlot
operator|=
name|utility
operator|.
name|addVariable
argument_list|(
literal|null
argument_list|,
literal|"_score"
argument_list|,
name|definition
operator|.
name|floatType
argument_list|)
operator|.
name|slot
expr_stmt|;
comment|// ctx map set by executable scripts as a read-only map.
name|metadata
operator|.
name|ctxValueSlot
operator|=
name|utility
operator|.
name|addVariable
argument_list|(
literal|null
argument_list|,
literal|"ctx"
argument_list|,
name|definition
operator|.
name|smapType
argument_list|)
operator|.
name|slot
expr_stmt|;
name|metadata
operator|.
name|createStatementMetadata
argument_list|(
name|metadata
operator|.
name|root
argument_list|)
expr_stmt|;
name|visit
argument_list|(
name|metadata
operator|.
name|root
argument_list|)
expr_stmt|;
name|utility
operator|.
name|decrementScope
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visitSource
specifier|public
name|Void
name|visitSource
parameter_list|(
specifier|final
name|SourceContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processSource
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitIf
specifier|public
name|Void
name|visitIf
parameter_list|(
specifier|final
name|IfContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processIf
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitWhile
specifier|public
name|Void
name|visitWhile
parameter_list|(
specifier|final
name|WhileContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processWhile
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitDo
specifier|public
name|Void
name|visitDo
parameter_list|(
specifier|final
name|DoContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processDo
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitFor
specifier|public
name|Void
name|visitFor
parameter_list|(
specifier|final
name|ForContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processFor
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitDecl
specifier|public
name|Void
name|visitDecl
parameter_list|(
specifier|final
name|DeclContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processDecl
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitContinue
specifier|public
name|Void
name|visitContinue
parameter_list|(
specifier|final
name|ContinueContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processContinue
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitBreak
specifier|public
name|Void
name|visitBreak
parameter_list|(
specifier|final
name|BreakContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processBreak
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitReturn
specifier|public
name|Void
name|visitReturn
parameter_list|(
specifier|final
name|ReturnContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processReturn
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitTry
specifier|public
name|Void
name|visitTry
parameter_list|(
specifier|final
name|TryContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processTry
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitThrow
specifier|public
name|Void
name|visitThrow
parameter_list|(
specifier|final
name|ThrowContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processThrow
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitExpr
specifier|public
name|Void
name|visitExpr
parameter_list|(
specifier|final
name|ExprContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processExpr
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitMultiple
specifier|public
name|Void
name|visitMultiple
parameter_list|(
specifier|final
name|MultipleContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processMultiple
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitSingle
specifier|public
name|Void
name|visitSingle
parameter_list|(
specifier|final
name|SingleContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processSingle
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitEmpty
specifier|public
name|Void
name|visitEmpty
parameter_list|(
specifier|final
name|EmptyContext
name|ctx
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|AnalyzerUtility
operator|.
name|error
argument_list|(
name|ctx
argument_list|)
operator|+
literal|"Unexpected state."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|visitEmptyscope
specifier|public
name|Void
name|visitEmptyscope
parameter_list|(
specifier|final
name|EmptyscopeContext
name|ctx
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|AnalyzerUtility
operator|.
name|error
argument_list|(
name|ctx
argument_list|)
operator|+
literal|"Unexpected state."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|visitInitializer
specifier|public
name|Void
name|visitInitializer
parameter_list|(
specifier|final
name|InitializerContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processInitializer
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitAfterthought
specifier|public
name|Void
name|visitAfterthought
parameter_list|(
specifier|final
name|AfterthoughtContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processAfterthought
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitDeclaration
specifier|public
name|Void
name|visitDeclaration
parameter_list|(
specifier|final
name|DeclarationContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processDeclaration
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitDecltype
specifier|public
name|Void
name|visitDecltype
parameter_list|(
specifier|final
name|DecltypeContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processDecltype
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitDeclvar
specifier|public
name|Void
name|visitDeclvar
parameter_list|(
specifier|final
name|DeclvarContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processDeclvar
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitTrap
specifier|public
name|Void
name|visitTrap
parameter_list|(
specifier|final
name|TrapContext
name|ctx
parameter_list|)
block|{
name|statement
operator|.
name|processTrap
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitIdentifier
specifier|public
name|Void
name|visitIdentifier
parameter_list|(
name|IdentifierContext
name|ctx
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|AnalyzerUtility
operator|.
name|error
argument_list|(
name|ctx
argument_list|)
operator|+
literal|"Unexpected state."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|visitGeneric
specifier|public
name|Void
name|visitGeneric
parameter_list|(
name|GenericContext
name|ctx
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|AnalyzerUtility
operator|.
name|error
argument_list|(
name|ctx
argument_list|)
operator|+
literal|"Unexpected state."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|visitPrecedence
specifier|public
name|Void
name|visitPrecedence
parameter_list|(
specifier|final
name|PrecedenceContext
name|ctx
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|AnalyzerUtility
operator|.
name|error
argument_list|(
name|ctx
argument_list|)
operator|+
literal|"Unexpected state."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|visitNumeric
specifier|public
name|Void
name|visitNumeric
parameter_list|(
specifier|final
name|NumericContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processNumeric
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitTrue
specifier|public
name|Void
name|visitTrue
parameter_list|(
specifier|final
name|TrueContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processTrue
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitFalse
specifier|public
name|Void
name|visitFalse
parameter_list|(
specifier|final
name|FalseContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processFalse
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitNull
specifier|public
name|Void
name|visitNull
parameter_list|(
specifier|final
name|NullContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processNull
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitExternal
specifier|public
name|Void
name|visitExternal
parameter_list|(
specifier|final
name|ExternalContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processExternal
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitPostinc
specifier|public
name|Void
name|visitPostinc
parameter_list|(
specifier|final
name|PostincContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processPostinc
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitPreinc
specifier|public
name|Void
name|visitPreinc
parameter_list|(
specifier|final
name|PreincContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processPreinc
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitUnary
specifier|public
name|Void
name|visitUnary
parameter_list|(
specifier|final
name|UnaryContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processUnary
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitCast
specifier|public
name|Void
name|visitCast
parameter_list|(
specifier|final
name|CastContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processCast
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitBinary
specifier|public
name|Void
name|visitBinary
parameter_list|(
specifier|final
name|BinaryContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processBinary
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitComp
specifier|public
name|Void
name|visitComp
parameter_list|(
specifier|final
name|CompContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processComp
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitBool
specifier|public
name|Void
name|visitBool
parameter_list|(
specifier|final
name|BoolContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processBool
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitConditional
specifier|public
name|Void
name|visitConditional
parameter_list|(
specifier|final
name|ConditionalContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processConditional
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitAssignment
specifier|public
name|Void
name|visitAssignment
parameter_list|(
specifier|final
name|AssignmentContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processAssignment
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitExtstart
specifier|public
name|Void
name|visitExtstart
parameter_list|(
specifier|final
name|ExtstartContext
name|ctx
parameter_list|)
block|{
name|external
operator|.
name|processExtstart
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitExtprec
specifier|public
name|Void
name|visitExtprec
parameter_list|(
specifier|final
name|ExtprecContext
name|ctx
parameter_list|)
block|{
name|external
operator|.
name|processExtprec
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitExtcast
specifier|public
name|Void
name|visitExtcast
parameter_list|(
specifier|final
name|ExtcastContext
name|ctx
parameter_list|)
block|{
name|external
operator|.
name|processExtcast
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitExtbrace
specifier|public
name|Void
name|visitExtbrace
parameter_list|(
specifier|final
name|ExtbraceContext
name|ctx
parameter_list|)
block|{
name|external
operator|.
name|processExtbrace
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitExtdot
specifier|public
name|Void
name|visitExtdot
parameter_list|(
specifier|final
name|ExtdotContext
name|ctx
parameter_list|)
block|{
name|external
operator|.
name|processExtdot
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitExtcall
specifier|public
name|Void
name|visitExtcall
parameter_list|(
specifier|final
name|ExtcallContext
name|ctx
parameter_list|)
block|{
name|external
operator|.
name|processExtcall
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitExtvar
specifier|public
name|Void
name|visitExtvar
parameter_list|(
specifier|final
name|ExtvarContext
name|ctx
parameter_list|)
block|{
name|external
operator|.
name|processExtvar
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitExtfield
specifier|public
name|Void
name|visitExtfield
parameter_list|(
specifier|final
name|ExtfieldContext
name|ctx
parameter_list|)
block|{
name|external
operator|.
name|processExtfield
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitExtnew
specifier|public
name|Void
name|visitExtnew
parameter_list|(
specifier|final
name|ExtnewContext
name|ctx
parameter_list|)
block|{
name|external
operator|.
name|processExtnew
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitExtstring
specifier|public
name|Void
name|visitExtstring
parameter_list|(
specifier|final
name|ExtstringContext
name|ctx
parameter_list|)
block|{
name|external
operator|.
name|processExtstring
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|visitArguments
specifier|public
name|Void
name|visitArguments
parameter_list|(
specifier|final
name|ArgumentsContext
name|ctx
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|AnalyzerUtility
operator|.
name|error
argument_list|(
name|ctx
argument_list|)
operator|+
literal|"Unexpected state."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|visitIncrement
specifier|public
name|Void
name|visitIncrement
parameter_list|(
specifier|final
name|IncrementContext
name|ctx
parameter_list|)
block|{
name|expression
operator|.
name|processIncrement
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

