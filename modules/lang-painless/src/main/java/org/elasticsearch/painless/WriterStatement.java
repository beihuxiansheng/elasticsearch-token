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
name|Definition
operator|.
name|Sort
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
name|Metadata
operator|.
name|ExpressionMetadata
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
name|Metadata
operator|.
name|StatementMetadata
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
name|BlockContext
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
name|StatementContext
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
name|WhileContext
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
name|WriterUtility
operator|.
name|Branch
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|Label
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|Opcodes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|commons
operator|.
name|GeneratorAdapter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|WriterConstants
operator|.
name|PAINLESS_ERROR_TYPE
import|;
end_import

begin_class
DECL|class|WriterStatement
class|class
name|WriterStatement
block|{
DECL|field|metadata
specifier|private
specifier|final
name|Metadata
name|metadata
decl_stmt|;
DECL|field|execute
specifier|private
specifier|final
name|GeneratorAdapter
name|execute
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|Writer
name|writer
decl_stmt|;
DECL|field|utility
specifier|private
specifier|final
name|WriterUtility
name|utility
decl_stmt|;
DECL|method|WriterStatement
name|WriterStatement
parameter_list|(
specifier|final
name|Metadata
name|metadata
parameter_list|,
specifier|final
name|GeneratorAdapter
name|execute
parameter_list|,
specifier|final
name|Writer
name|writer
parameter_list|,
specifier|final
name|WriterUtility
name|utility
parameter_list|)
block|{
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|execute
operator|=
name|execute
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|utility
operator|=
name|utility
expr_stmt|;
block|}
DECL|method|processSource
name|void
name|processSource
parameter_list|(
specifier|final
name|SourceContext
name|ctx
parameter_list|)
block|{
specifier|final
name|StatementMetadata
name|sourcesmd
init|=
name|metadata
operator|.
name|getStatementMetadata
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|StatementContext
name|sctx
range|:
name|ctx
operator|.
name|statement
argument_list|()
control|)
block|{
name|writer
operator|.
name|visit
argument_list|(
name|sctx
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|sourcesmd
operator|.
name|methodEscape
condition|)
block|{
name|execute
operator|.
name|visitInsn
argument_list|(
name|Opcodes
operator|.
name|ACONST_NULL
argument_list|)
expr_stmt|;
name|execute
operator|.
name|returnValue
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|processIf
name|void
name|processIf
parameter_list|(
specifier|final
name|IfContext
name|ctx
parameter_list|)
block|{
specifier|final
name|ExpressionContext
name|exprctx
init|=
name|ctx
operator|.
name|expression
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|els
init|=
name|ctx
operator|.
name|ELSE
argument_list|()
operator|!=
literal|null
decl_stmt|;
specifier|final
name|Branch
name|branch
init|=
name|utility
operator|.
name|markBranch
argument_list|(
name|ctx
argument_list|,
name|exprctx
argument_list|)
decl_stmt|;
name|branch
operator|.
name|end
operator|=
operator|new
name|Label
argument_list|()
expr_stmt|;
name|branch
operator|.
name|fals
operator|=
name|els
condition|?
operator|new
name|Label
argument_list|()
else|:
name|branch
operator|.
name|end
expr_stmt|;
name|writer
operator|.
name|visit
argument_list|(
name|exprctx
argument_list|)
expr_stmt|;
specifier|final
name|BlockContext
name|blockctx0
init|=
name|ctx
operator|.
name|block
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|StatementMetadata
name|blockmd0
init|=
name|metadata
operator|.
name|getStatementMetadata
argument_list|(
name|blockctx0
argument_list|)
decl_stmt|;
name|writer
operator|.
name|visit
argument_list|(
name|blockctx0
argument_list|)
expr_stmt|;
if|if
condition|(
name|els
condition|)
block|{
if|if
condition|(
operator|!
name|blockmd0
operator|.
name|allLast
condition|)
block|{
name|execute
operator|.
name|goTo
argument_list|(
name|branch
operator|.
name|end
argument_list|)
expr_stmt|;
block|}
name|execute
operator|.
name|mark
argument_list|(
name|branch
operator|.
name|fals
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visit
argument_list|(
name|ctx
operator|.
name|block
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|execute
operator|.
name|mark
argument_list|(
name|branch
operator|.
name|end
argument_list|)
expr_stmt|;
block|}
DECL|method|processWhile
name|void
name|processWhile
parameter_list|(
specifier|final
name|WhileContext
name|ctx
parameter_list|)
block|{
specifier|final
name|ExpressionContext
name|exprctx
init|=
name|ctx
operator|.
name|expression
argument_list|()
decl_stmt|;
specifier|final
name|Branch
name|branch
init|=
name|utility
operator|.
name|markBranch
argument_list|(
name|ctx
argument_list|,
name|exprctx
argument_list|)
decl_stmt|;
name|branch
operator|.
name|begin
operator|=
operator|new
name|Label
argument_list|()
expr_stmt|;
name|branch
operator|.
name|end
operator|=
operator|new
name|Label
argument_list|()
expr_stmt|;
name|branch
operator|.
name|fals
operator|=
name|branch
operator|.
name|end
expr_stmt|;
name|utility
operator|.
name|pushJump
argument_list|(
name|branch
argument_list|)
expr_stmt|;
name|execute
operator|.
name|mark
argument_list|(
name|branch
operator|.
name|begin
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visit
argument_list|(
name|exprctx
argument_list|)
expr_stmt|;
specifier|final
name|BlockContext
name|blockctx
init|=
name|ctx
operator|.
name|block
argument_list|()
decl_stmt|;
name|boolean
name|allLast
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|blockctx
operator|!=
literal|null
condition|)
block|{
specifier|final
name|StatementMetadata
name|blocksmd
init|=
name|metadata
operator|.
name|getStatementMetadata
argument_list|(
name|blockctx
argument_list|)
decl_stmt|;
name|allLast
operator|=
name|blocksmd
operator|.
name|allLast
expr_stmt|;
name|writeLoopCounter
argument_list|(
name|blocksmd
operator|.
name|count
operator|>
literal|0
condition|?
name|blocksmd
operator|.
name|count
else|:
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visit
argument_list|(
name|blockctx
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ctx
operator|.
name|empty
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|writeLoopCounter
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|WriterUtility
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
if|if
condition|(
operator|!
name|allLast
condition|)
block|{
name|execute
operator|.
name|goTo
argument_list|(
name|branch
operator|.
name|begin
argument_list|)
expr_stmt|;
block|}
name|execute
operator|.
name|mark
argument_list|(
name|branch
operator|.
name|end
argument_list|)
expr_stmt|;
name|utility
operator|.
name|popJump
argument_list|()
expr_stmt|;
block|}
DECL|method|processDo
name|void
name|processDo
parameter_list|(
specifier|final
name|DoContext
name|ctx
parameter_list|)
block|{
specifier|final
name|ExpressionContext
name|exprctx
init|=
name|ctx
operator|.
name|expression
argument_list|()
decl_stmt|;
specifier|final
name|Branch
name|branch
init|=
name|utility
operator|.
name|markBranch
argument_list|(
name|ctx
argument_list|,
name|exprctx
argument_list|)
decl_stmt|;
name|Label
name|start
init|=
operator|new
name|Label
argument_list|()
decl_stmt|;
name|branch
operator|.
name|begin
operator|=
operator|new
name|Label
argument_list|()
expr_stmt|;
name|branch
operator|.
name|end
operator|=
operator|new
name|Label
argument_list|()
expr_stmt|;
name|branch
operator|.
name|fals
operator|=
name|branch
operator|.
name|end
expr_stmt|;
specifier|final
name|BlockContext
name|blockctx
init|=
name|ctx
operator|.
name|block
argument_list|()
decl_stmt|;
specifier|final
name|StatementMetadata
name|blocksmd
init|=
name|metadata
operator|.
name|getStatementMetadata
argument_list|(
name|blockctx
argument_list|)
decl_stmt|;
name|utility
operator|.
name|pushJump
argument_list|(
name|branch
argument_list|)
expr_stmt|;
name|execute
operator|.
name|mark
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visit
argument_list|(
name|blockctx
argument_list|)
expr_stmt|;
name|execute
operator|.
name|mark
argument_list|(
name|branch
operator|.
name|begin
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visit
argument_list|(
name|exprctx
argument_list|)
expr_stmt|;
name|writeLoopCounter
argument_list|(
name|blocksmd
operator|.
name|count
operator|>
literal|0
condition|?
name|blocksmd
operator|.
name|count
else|:
literal|1
argument_list|)
expr_stmt|;
name|execute
operator|.
name|goTo
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|execute
operator|.
name|mark
argument_list|(
name|branch
operator|.
name|end
argument_list|)
expr_stmt|;
name|utility
operator|.
name|popJump
argument_list|()
expr_stmt|;
block|}
DECL|method|processFor
name|void
name|processFor
parameter_list|(
specifier|final
name|ForContext
name|ctx
parameter_list|)
block|{
specifier|final
name|ExpressionContext
name|exprctx
init|=
name|ctx
operator|.
name|expression
argument_list|()
decl_stmt|;
specifier|final
name|AfterthoughtContext
name|atctx
init|=
name|ctx
operator|.
name|afterthought
argument_list|()
decl_stmt|;
specifier|final
name|Branch
name|branch
init|=
name|utility
operator|.
name|markBranch
argument_list|(
name|ctx
argument_list|,
name|exprctx
argument_list|)
decl_stmt|;
specifier|final
name|Label
name|start
init|=
operator|new
name|Label
argument_list|()
decl_stmt|;
name|branch
operator|.
name|begin
operator|=
name|atctx
operator|==
literal|null
condition|?
name|start
else|:
operator|new
name|Label
argument_list|()
expr_stmt|;
name|branch
operator|.
name|end
operator|=
operator|new
name|Label
argument_list|()
expr_stmt|;
name|branch
operator|.
name|fals
operator|=
name|branch
operator|.
name|end
expr_stmt|;
name|utility
operator|.
name|pushJump
argument_list|(
name|branch
argument_list|)
expr_stmt|;
if|if
condition|(
name|ctx
operator|.
name|initializer
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|visit
argument_list|(
name|ctx
operator|.
name|initializer
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|execute
operator|.
name|mark
argument_list|(
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|exprctx
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|visit
argument_list|(
name|exprctx
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BlockContext
name|blockctx
init|=
name|ctx
operator|.
name|block
argument_list|()
decl_stmt|;
name|boolean
name|allLast
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|blockctx
operator|!=
literal|null
condition|)
block|{
name|StatementMetadata
name|blocksmd
init|=
name|metadata
operator|.
name|getStatementMetadata
argument_list|(
name|blockctx
argument_list|)
decl_stmt|;
name|allLast
operator|=
name|blocksmd
operator|.
name|allLast
expr_stmt|;
name|int
name|count
init|=
name|blocksmd
operator|.
name|count
operator|>
literal|0
condition|?
name|blocksmd
operator|.
name|count
else|:
literal|1
decl_stmt|;
if|if
condition|(
name|atctx
operator|!=
literal|null
condition|)
block|{
operator|++
name|count
expr_stmt|;
block|}
name|writeLoopCounter
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visit
argument_list|(
name|blockctx
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ctx
operator|.
name|empty
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|writeLoopCounter
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|WriterUtility
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
if|if
condition|(
name|atctx
operator|!=
literal|null
condition|)
block|{
name|execute
operator|.
name|mark
argument_list|(
name|branch
operator|.
name|begin
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visit
argument_list|(
name|atctx
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|atctx
operator|!=
literal|null
operator|||
operator|!
name|allLast
condition|)
block|{
name|execute
operator|.
name|goTo
argument_list|(
name|start
argument_list|)
expr_stmt|;
block|}
name|execute
operator|.
name|mark
argument_list|(
name|branch
operator|.
name|end
argument_list|)
expr_stmt|;
name|utility
operator|.
name|popJump
argument_list|()
expr_stmt|;
block|}
DECL|method|processDecl
name|void
name|processDecl
parameter_list|(
specifier|final
name|DeclContext
name|ctx
parameter_list|)
block|{
name|writer
operator|.
name|visit
argument_list|(
name|ctx
operator|.
name|declaration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|processContinue
name|void
name|processContinue
parameter_list|()
block|{
specifier|final
name|Branch
name|jump
init|=
name|utility
operator|.
name|peekJump
argument_list|()
decl_stmt|;
name|execute
operator|.
name|goTo
argument_list|(
name|jump
operator|.
name|begin
argument_list|)
expr_stmt|;
block|}
DECL|method|processBreak
name|void
name|processBreak
parameter_list|()
block|{
specifier|final
name|Branch
name|jump
init|=
name|utility
operator|.
name|peekJump
argument_list|()
decl_stmt|;
name|execute
operator|.
name|goTo
argument_list|(
name|jump
operator|.
name|end
argument_list|)
expr_stmt|;
block|}
DECL|method|processReturn
name|void
name|processReturn
parameter_list|(
specifier|final
name|ReturnContext
name|ctx
parameter_list|)
block|{
name|writer
operator|.
name|visit
argument_list|(
name|ctx
operator|.
name|expression
argument_list|()
argument_list|)
expr_stmt|;
name|execute
operator|.
name|returnValue
argument_list|()
expr_stmt|;
block|}
DECL|method|processTry
name|void
name|processTry
parameter_list|(
specifier|final
name|TryContext
name|ctx
parameter_list|)
block|{
specifier|final
name|TrapContext
index|[]
name|trapctxs
init|=
operator|new
name|TrapContext
index|[
name|ctx
operator|.
name|trap
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|ctx
operator|.
name|trap
argument_list|()
operator|.
name|toArray
argument_list|(
name|trapctxs
argument_list|)
expr_stmt|;
specifier|final
name|Branch
name|branch
init|=
name|utility
operator|.
name|markBranch
argument_list|(
name|ctx
argument_list|,
name|trapctxs
argument_list|)
decl_stmt|;
name|Label
name|end
init|=
operator|new
name|Label
argument_list|()
decl_stmt|;
name|branch
operator|.
name|begin
operator|=
operator|new
name|Label
argument_list|()
expr_stmt|;
name|branch
operator|.
name|end
operator|=
operator|new
name|Label
argument_list|()
expr_stmt|;
name|branch
operator|.
name|tru
operator|=
name|trapctxs
operator|.
name|length
operator|>
literal|1
condition|?
name|end
else|:
literal|null
expr_stmt|;
name|execute
operator|.
name|mark
argument_list|(
name|branch
operator|.
name|begin
argument_list|)
expr_stmt|;
specifier|final
name|BlockContext
name|blockctx
init|=
name|ctx
operator|.
name|block
argument_list|()
decl_stmt|;
specifier|final
name|StatementMetadata
name|blocksmd
init|=
name|metadata
operator|.
name|getStatementMetadata
argument_list|(
name|blockctx
argument_list|)
decl_stmt|;
name|writer
operator|.
name|visit
argument_list|(
name|blockctx
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|blocksmd
operator|.
name|allLast
condition|)
block|{
name|execute
operator|.
name|goTo
argument_list|(
name|end
argument_list|)
expr_stmt|;
block|}
name|execute
operator|.
name|mark
argument_list|(
name|branch
operator|.
name|end
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|TrapContext
name|trapctx
range|:
name|trapctxs
control|)
block|{
name|writer
operator|.
name|visit
argument_list|(
name|trapctx
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|blocksmd
operator|.
name|allLast
operator|||
name|trapctxs
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|execute
operator|.
name|mark
argument_list|(
name|end
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processThrow
name|void
name|processThrow
parameter_list|(
specifier|final
name|ThrowContext
name|ctx
parameter_list|)
block|{
name|writer
operator|.
name|visit
argument_list|(
name|ctx
operator|.
name|expression
argument_list|()
argument_list|)
expr_stmt|;
name|execute
operator|.
name|throwException
argument_list|()
expr_stmt|;
block|}
DECL|method|processExpr
name|void
name|processExpr
parameter_list|(
specifier|final
name|ExprContext
name|ctx
parameter_list|)
block|{
specifier|final
name|StatementMetadata
name|exprsmd
init|=
name|metadata
operator|.
name|getStatementMetadata
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
specifier|final
name|ExpressionContext
name|exprctx
init|=
name|ctx
operator|.
name|expression
argument_list|()
decl_stmt|;
specifier|final
name|ExpressionMetadata
name|expremd
init|=
name|metadata
operator|.
name|getExpressionMetadata
argument_list|(
name|exprctx
argument_list|)
decl_stmt|;
name|writer
operator|.
name|visit
argument_list|(
name|exprctx
argument_list|)
expr_stmt|;
if|if
condition|(
name|exprsmd
operator|.
name|methodEscape
condition|)
block|{
name|execute
operator|.
name|returnValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|utility
operator|.
name|writePop
argument_list|(
name|expremd
operator|.
name|to
operator|.
name|type
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processMultiple
name|void
name|processMultiple
parameter_list|(
specifier|final
name|MultipleContext
name|ctx
parameter_list|)
block|{
for|for
control|(
specifier|final
name|StatementContext
name|sctx
range|:
name|ctx
operator|.
name|statement
argument_list|()
control|)
block|{
name|writer
operator|.
name|visit
argument_list|(
name|sctx
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processSingle
name|void
name|processSingle
parameter_list|(
specifier|final
name|SingleContext
name|ctx
parameter_list|)
block|{
name|writer
operator|.
name|visit
argument_list|(
name|ctx
operator|.
name|statement
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|processInitializer
name|void
name|processInitializer
parameter_list|(
name|InitializerContext
name|ctx
parameter_list|)
block|{
specifier|final
name|DeclarationContext
name|declctx
init|=
name|ctx
operator|.
name|declaration
argument_list|()
decl_stmt|;
specifier|final
name|ExpressionContext
name|exprctx
init|=
name|ctx
operator|.
name|expression
argument_list|()
decl_stmt|;
if|if
condition|(
name|declctx
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|visit
argument_list|(
name|declctx
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|exprctx
operator|!=
literal|null
condition|)
block|{
specifier|final
name|ExpressionMetadata
name|expremd
init|=
name|metadata
operator|.
name|getExpressionMetadata
argument_list|(
name|exprctx
argument_list|)
decl_stmt|;
name|writer
operator|.
name|visit
argument_list|(
name|exprctx
argument_list|)
expr_stmt|;
name|utility
operator|.
name|writePop
argument_list|(
name|expremd
operator|.
name|to
operator|.
name|type
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|WriterUtility
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
block|}
DECL|method|processAfterthought
name|void
name|processAfterthought
parameter_list|(
name|AfterthoughtContext
name|ctx
parameter_list|)
block|{
specifier|final
name|ExpressionContext
name|exprctx
init|=
name|ctx
operator|.
name|expression
argument_list|()
decl_stmt|;
specifier|final
name|ExpressionMetadata
name|expremd
init|=
name|metadata
operator|.
name|getExpressionMetadata
argument_list|(
name|exprctx
argument_list|)
decl_stmt|;
name|writer
operator|.
name|visit
argument_list|(
name|ctx
operator|.
name|expression
argument_list|()
argument_list|)
expr_stmt|;
name|utility
operator|.
name|writePop
argument_list|(
name|expremd
operator|.
name|to
operator|.
name|type
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|processDeclaration
name|void
name|processDeclaration
parameter_list|(
name|DeclarationContext
name|ctx
parameter_list|)
block|{
for|for
control|(
specifier|final
name|DeclvarContext
name|declctx
range|:
name|ctx
operator|.
name|declvar
argument_list|()
control|)
block|{
name|writer
operator|.
name|visit
argument_list|(
name|declctx
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processDeclvar
name|void
name|processDeclvar
parameter_list|(
specifier|final
name|DeclvarContext
name|ctx
parameter_list|)
block|{
specifier|final
name|ExpressionMetadata
name|declvaremd
init|=
name|metadata
operator|.
name|getExpressionMetadata
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
specifier|final
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|Type
name|type
init|=
name|declvaremd
operator|.
name|to
operator|.
name|type
decl_stmt|;
specifier|final
name|Sort
name|sort
init|=
name|declvaremd
operator|.
name|to
operator|.
name|sort
decl_stmt|;
name|int
name|slot
init|=
operator|(
name|int
operator|)
name|declvaremd
operator|.
name|postConst
decl_stmt|;
specifier|final
name|ExpressionContext
name|exprctx
init|=
name|ctx
operator|.
name|expression
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|initialize
init|=
name|exprctx
operator|==
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|initialize
condition|)
block|{
name|writer
operator|.
name|visit
argument_list|(
name|exprctx
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|sort
condition|)
block|{
case|case
name|VOID
case|:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|WriterUtility
operator|.
name|error
argument_list|(
name|ctx
argument_list|)
operator|+
literal|"Unexpected state."
argument_list|)
throw|;
case|case
name|BOOL
case|:
case|case
name|BYTE
case|:
case|case
name|SHORT
case|:
case|case
name|CHAR
case|:
case|case
name|INT
case|:
if|if
condition|(
name|initialize
condition|)
name|execute
operator|.
name|push
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
if|if
condition|(
name|initialize
condition|)
name|execute
operator|.
name|push
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
if|if
condition|(
name|initialize
condition|)
name|execute
operator|.
name|push
argument_list|(
literal|0.0F
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
if|if
condition|(
name|initialize
condition|)
name|execute
operator|.
name|push
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
break|break;
default|default:
if|if
condition|(
name|initialize
condition|)
name|execute
operator|.
name|visitInsn
argument_list|(
name|Opcodes
operator|.
name|ACONST_NULL
argument_list|)
expr_stmt|;
block|}
name|execute
operator|.
name|visitVarInsn
argument_list|(
name|type
operator|.
name|getOpcode
argument_list|(
name|Opcodes
operator|.
name|ISTORE
argument_list|)
argument_list|,
name|slot
argument_list|)
expr_stmt|;
block|}
DECL|method|processTrap
name|void
name|processTrap
parameter_list|(
specifier|final
name|TrapContext
name|ctx
parameter_list|)
block|{
specifier|final
name|StatementMetadata
name|trapsmd
init|=
name|metadata
operator|.
name|getStatementMetadata
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
specifier|final
name|Branch
name|branch
init|=
name|utility
operator|.
name|getBranch
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
specifier|final
name|Label
name|jump
init|=
operator|new
name|Label
argument_list|()
decl_stmt|;
specifier|final
name|BlockContext
name|blockctx
init|=
name|ctx
operator|.
name|block
argument_list|()
decl_stmt|;
specifier|final
name|EmptyscopeContext
name|emptyctx
init|=
name|ctx
operator|.
name|emptyscope
argument_list|()
decl_stmt|;
name|execute
operator|.
name|mark
argument_list|(
name|jump
argument_list|)
expr_stmt|;
name|execute
operator|.
name|visitVarInsn
argument_list|(
name|trapsmd
operator|.
name|exception
operator|.
name|type
operator|.
name|getOpcode
argument_list|(
name|Opcodes
operator|.
name|ISTORE
argument_list|)
argument_list|,
name|trapsmd
operator|.
name|slot
argument_list|)
expr_stmt|;
if|if
condition|(
name|blockctx
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|visit
argument_list|(
name|ctx
operator|.
name|block
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|emptyctx
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|WriterUtility
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
name|execute
operator|.
name|visitTryCatchBlock
argument_list|(
name|branch
operator|.
name|begin
argument_list|,
name|branch
operator|.
name|end
argument_list|,
name|jump
argument_list|,
name|trapsmd
operator|.
name|exception
operator|.
name|type
operator|.
name|getInternalName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|branch
operator|.
name|tru
operator|!=
literal|null
operator|&&
operator|!
name|trapsmd
operator|.
name|allLast
condition|)
block|{
name|execute
operator|.
name|goTo
argument_list|(
name|branch
operator|.
name|tru
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeLoopCounter
specifier|private
name|void
name|writeLoopCounter
parameter_list|(
specifier|final
name|int
name|count
parameter_list|)
block|{
specifier|final
name|Label
name|end
init|=
operator|new
name|Label
argument_list|()
decl_stmt|;
name|execute
operator|.
name|iinc
argument_list|(
name|metadata
operator|.
name|loopCounterSlot
argument_list|,
operator|-
name|count
argument_list|)
expr_stmt|;
name|execute
operator|.
name|visitVarInsn
argument_list|(
name|Opcodes
operator|.
name|ILOAD
argument_list|,
name|metadata
operator|.
name|loopCounterSlot
argument_list|)
expr_stmt|;
name|execute
operator|.
name|push
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|execute
operator|.
name|ifICmp
argument_list|(
name|GeneratorAdapter
operator|.
name|GT
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|execute
operator|.
name|throwException
argument_list|(
name|PAINLESS_ERROR_TYPE
argument_list|,
literal|"The maximum number of statements that can be executed in a loop has been reached."
argument_list|)
expr_stmt|;
name|execute
operator|.
name|mark
argument_list|(
name|end
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

