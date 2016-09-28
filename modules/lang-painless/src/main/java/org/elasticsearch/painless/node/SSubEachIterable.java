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
name|AnalyzerCaster
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
name|DefBootstrap
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
name|Method
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
name|MethodKey
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
name|Locals
operator|.
name|Variable
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
name|Objects
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
name|ITERATOR_HASNEXT
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
name|ITERATOR_NEXT
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
name|ITERATOR_TYPE
import|;
end_import

begin_comment
comment|/**  * Represents a for-each loop for iterables.  */
end_comment

begin_class
DECL|class|SSubEachIterable
specifier|final
class|class
name|SSubEachIterable
extends|extends
name|AStatement
block|{
DECL|field|expression
specifier|private
name|AExpression
name|expression
decl_stmt|;
DECL|field|block
specifier|private
specifier|final
name|SBlock
name|block
decl_stmt|;
DECL|field|variable
specifier|private
specifier|final
name|Variable
name|variable
decl_stmt|;
DECL|field|cast
specifier|private
name|Cast
name|cast
init|=
literal|null
decl_stmt|;
DECL|field|iterator
specifier|private
name|Variable
name|iterator
init|=
literal|null
decl_stmt|;
DECL|field|method
specifier|private
name|Method
name|method
init|=
literal|null
decl_stmt|;
DECL|method|SSubEachIterable
specifier|public
name|SSubEachIterable
parameter_list|(
name|Location
name|location
parameter_list|,
name|Variable
name|variable
parameter_list|,
name|AExpression
name|expression
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
name|variable
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|variable
argument_list|)
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|expression
argument_list|)
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
comment|// We must store the iterator as a variable for securing a slot on the stack, and
comment|// also add the location offset to make the name unique in case of nested for each loops.
name|iterator
operator|=
name|locals
operator|.
name|addVariable
argument_list|(
name|location
argument_list|,
name|Definition
operator|.
name|getType
argument_list|(
literal|"Iterator"
argument_list|)
argument_list|,
literal|"#itr"
operator|+
name|location
operator|.
name|getOffset
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|expression
operator|.
name|actual
operator|.
name|sort
operator|==
name|Sort
operator|.
name|DEF
condition|)
block|{
name|method
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|method
operator|=
name|expression
operator|.
name|actual
operator|.
name|struct
operator|.
name|methods
operator|.
name|get
argument_list|(
operator|new
name|MethodKey
argument_list|(
literal|"iterator"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|method
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
literal|"Unable to create iterator for the type ["
operator|+
name|expression
operator|.
name|actual
operator|.
name|name
operator|+
literal|"]."
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|cast
operator|=
name|AnalyzerCaster
operator|.
name|getLegalCast
argument_list|(
name|location
argument_list|,
name|Definition
operator|.
name|DEF_TYPE
argument_list|,
name|variable
operator|.
name|type
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
name|expression
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|globals
argument_list|)
expr_stmt|;
if|if
condition|(
name|method
operator|==
literal|null
condition|)
block|{
name|Type
name|itr
init|=
name|Definition
operator|.
name|getType
argument_list|(
literal|"Iterator"
argument_list|)
decl_stmt|;
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|Type
name|methodType
init|=
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|Type
operator|.
name|getMethodType
argument_list|(
name|itr
operator|.
name|type
argument_list|,
name|Definition
operator|.
name|DEF_TYPE
operator|.
name|type
argument_list|)
decl_stmt|;
name|writer
operator|.
name|invokeDefCall
argument_list|(
literal|"iterator"
argument_list|,
name|methodType
argument_list|,
name|DefBootstrap
operator|.
name|ITERATOR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|method
operator|.
name|write
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|visitVarInsn
argument_list|(
name|iterator
operator|.
name|type
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
name|iterator
operator|.
name|getSlot
argument_list|()
argument_list|)
expr_stmt|;
name|Label
name|begin
init|=
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
name|writer
operator|.
name|mark
argument_list|(
name|begin
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visitVarInsn
argument_list|(
name|iterator
operator|.
name|type
operator|.
name|type
operator|.
name|getOpcode
argument_list|(
name|Opcodes
operator|.
name|ILOAD
argument_list|)
argument_list|,
name|iterator
operator|.
name|getSlot
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|invokeInterface
argument_list|(
name|ITERATOR_TYPE
argument_list|,
name|ITERATOR_HASNEXT
argument_list|)
expr_stmt|;
name|writer
operator|.
name|ifZCmp
argument_list|(
name|MethodWriter
operator|.
name|EQ
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visitVarInsn
argument_list|(
name|iterator
operator|.
name|type
operator|.
name|type
operator|.
name|getOpcode
argument_list|(
name|Opcodes
operator|.
name|ILOAD
argument_list|)
argument_list|,
name|iterator
operator|.
name|getSlot
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|invokeInterface
argument_list|(
name|ITERATOR_TYPE
argument_list|,
name|ITERATOR_NEXT
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeCast
argument_list|(
name|cast
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visitVarInsn
argument_list|(
name|variable
operator|.
name|type
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
name|variable
operator|.
name|getSlot
argument_list|()
argument_list|)
expr_stmt|;
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
name|writer
operator|.
name|goTo
argument_list|(
name|begin
argument_list|)
expr_stmt|;
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
