begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|node
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
name|Globals
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
name|Locals
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
name|Location
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
name|MethodWriter
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Represents a for loop.  */
end_comment

begin_class
DECL|class|SFor
specifier|public
specifier|final
class|class
name|SFor
extends|extends
name|AStatement
block|{
DECL|field|initializer
specifier|private
name|ANode
name|initializer
decl_stmt|;
DECL|field|condition
specifier|private
name|AExpression
name|condition
decl_stmt|;
DECL|field|afterthought
specifier|private
name|AExpression
name|afterthought
decl_stmt|;
DECL|field|block
specifier|private
specifier|final
name|SBlock
name|block
decl_stmt|;
DECL|field|continuous
specifier|private
name|boolean
name|continuous
init|=
literal|false
decl_stmt|;
DECL|method|SFor
specifier|public
name|SFor
parameter_list|(
name|Location
name|location
parameter_list|,
name|ANode
name|initializer
parameter_list|,
name|AExpression
name|condition
parameter_list|,
name|AExpression
name|afterthought
parameter_list|,
name|SBlock
name|block
parameter_list|)
block|{
name|super
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|initializer
operator|=
name|initializer
expr_stmt|;
name|this
operator|.
name|condition
operator|=
name|condition
expr_stmt|;
name|this
operator|.
name|afterthought
operator|=
name|afterthought
expr_stmt|;
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractVariables
name|void
name|extractVariables
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|variables
parameter_list|)
block|{
if|if
condition|(
name|initializer
operator|!=
literal|null
condition|)
block|{
name|initializer
operator|.
name|extractVariables
argument_list|(
name|variables
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|condition
operator|!=
literal|null
condition|)
block|{
name|condition
operator|.
name|extractVariables
argument_list|(
name|variables
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|afterthought
operator|!=
literal|null
condition|)
block|{
name|afterthought
operator|.
name|extractVariables
argument_list|(
name|variables
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|block
operator|!=
literal|null
condition|)
block|{
name|block
operator|.
name|extractVariables
argument_list|(
name|variables
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|analyze
name|void
name|analyze
parameter_list|(
name|Locals
name|locals
parameter_list|)
block|{
name|locals
operator|=
name|Locals
operator|.
name|newLocalScope
argument_list|(
name|locals
argument_list|)
expr_stmt|;
if|if
condition|(
name|initializer
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|initializer
operator|instanceof
name|AStatement
condition|)
block|{
name|initializer
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|initializer
operator|instanceof
name|AExpression
condition|)
block|{
name|AExpression
name|initializer
init|=
operator|(
name|AExpression
operator|)
name|this
operator|.
name|initializer
decl_stmt|;
name|initializer
operator|.
name|read
operator|=
literal|false
expr_stmt|;
name|initializer
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|initializer
operator|.
name|statement
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not a statement."
argument_list|)
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"Illegal tree structure."
argument_list|)
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|condition
operator|!=
literal|null
condition|)
block|{
name|condition
operator|.
name|expected
operator|=
name|Definition
operator|.
name|BOOLEAN_TYPE
expr_stmt|;
name|condition
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
name|condition
operator|=
name|condition
operator|.
name|cast
argument_list|(
name|locals
argument_list|)
expr_stmt|;
if|if
condition|(
name|condition
operator|.
name|constant
operator|!=
literal|null
condition|)
block|{
name|continuous
operator|=
operator|(
name|boolean
operator|)
name|condition
operator|.
name|constant
expr_stmt|;
if|if
condition|(
operator|!
name|continuous
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Extraneous for loop."
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|block
operator|==
literal|null
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"For loop has no escape."
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
name|continuous
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|afterthought
operator|!=
literal|null
condition|)
block|{
name|afterthought
operator|.
name|read
operator|=
literal|false
expr_stmt|;
name|afterthought
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|afterthought
operator|.
name|statement
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not a statement."
argument_list|)
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|block
operator|!=
literal|null
condition|)
block|{
name|block
operator|.
name|beginLoop
operator|=
literal|true
expr_stmt|;
name|block
operator|.
name|inLoop
operator|=
literal|true
expr_stmt|;
name|block
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
if|if
condition|(
name|block
operator|.
name|loopEscape
operator|&&
operator|!
name|block
operator|.
name|anyContinue
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Extraneous for loop."
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|continuous
operator|&&
operator|!
name|block
operator|.
name|anyBreak
condition|)
block|{
name|methodEscape
operator|=
literal|true
expr_stmt|;
name|allEscape
operator|=
literal|true
expr_stmt|;
block|}
name|block
operator|.
name|statementCount
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|block
operator|.
name|statementCount
argument_list|)
expr_stmt|;
block|}
name|statementCount
operator|=
literal|1
expr_stmt|;
if|if
condition|(
name|locals
operator|.
name|hasVariable
argument_list|(
name|Locals
operator|.
name|LOOP
argument_list|)
condition|)
block|{
name|loopCounter
operator|=
name|locals
operator|.
name|getVariable
argument_list|(
name|location
argument_list|,
name|Locals
operator|.
name|LOOP
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
name|void
name|write
parameter_list|(
name|MethodWriter
name|writer
parameter_list|,
name|Globals
name|globals
parameter_list|)
block|{
name|writer
operator|.
name|writeStatementOffset
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|Label
name|start
init|=
operator|new
name|Label
argument_list|()
decl_stmt|;
name|Label
name|begin
init|=
name|afterthought
operator|==
literal|null
condition|?
name|start
else|:
operator|new
name|Label
argument_list|()
decl_stmt|;
name|Label
name|end
init|=
operator|new
name|Label
argument_list|()
decl_stmt|;
if|if
condition|(
name|initializer
operator|instanceof
name|SDeclBlock
condition|)
block|{
name|initializer
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|globals
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|initializer
operator|instanceof
name|AExpression
condition|)
block|{
name|AExpression
name|initializer
init|=
operator|(
name|AExpression
operator|)
name|this
operator|.
name|initializer
decl_stmt|;
name|initializer
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|globals
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writePop
argument_list|(
name|initializer
operator|.
name|expected
operator|.
name|sort
operator|.
name|size
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|mark
argument_list|(
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|condition
operator|!=
literal|null
operator|&&
operator|!
name|continuous
condition|)
block|{
name|condition
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|globals
argument_list|)
expr_stmt|;
name|writer
operator|.
name|ifZCmp
argument_list|(
name|Opcodes
operator|.
name|IFEQ
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
name|boolean
name|allEscape
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|block
operator|!=
literal|null
condition|)
block|{
name|allEscape
operator|=
name|block
operator|.
name|allEscape
expr_stmt|;
name|int
name|statementCount
init|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|block
operator|.
name|statementCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|afterthought
operator|!=
literal|null
condition|)
block|{
operator|++
name|statementCount
expr_stmt|;
block|}
if|if
condition|(
name|loopCounter
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|writeLoopCounter
argument_list|(
name|loopCounter
operator|.
name|getSlot
argument_list|()
argument_list|,
name|statementCount
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
name|block
operator|.
name|continu
operator|=
name|begin
expr_stmt|;
name|block
operator|.
name|brake
operator|=
name|end
expr_stmt|;
name|block
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|globals
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|loopCounter
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|writeLoopCounter
argument_list|(
name|loopCounter
operator|.
name|getSlot
argument_list|()
argument_list|,
literal|1
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|afterthought
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|mark
argument_list|(
name|begin
argument_list|)
expr_stmt|;
name|afterthought
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|globals
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|afterthought
operator|!=
literal|null
operator|||
operator|!
name|allEscape
condition|)
block|{
name|writer
operator|.
name|goTo
argument_list|(
name|start
argument_list|)
expr_stmt|;
block|}
name|writer
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

