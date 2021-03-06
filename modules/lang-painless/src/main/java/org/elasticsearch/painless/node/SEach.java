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

begin_comment
comment|/**  * Represents a for-each loop and defers to subnodes depending on type.  */
end_comment

begin_class
DECL|class|SEach
specifier|public
class|class
name|SEach
extends|extends
name|AStatement
block|{
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
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
DECL|field|sub
specifier|private
name|AStatement
name|sub
init|=
literal|null
decl_stmt|;
DECL|method|SEach
specifier|public
name|SEach
parameter_list|(
name|Location
name|location
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|name
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
name|type
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|name
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
name|variables
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|expression
operator|.
name|extractVariables
argument_list|(
name|variables
argument_list|)
expr_stmt|;
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
name|expression
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
name|expression
operator|.
name|expected
operator|=
name|expression
operator|.
name|actual
expr_stmt|;
name|expression
operator|=
name|expression
operator|.
name|cast
argument_list|(
name|locals
argument_list|)
expr_stmt|;
specifier|final
name|Type
name|type
decl_stmt|;
try|try
block|{
name|type
operator|=
name|locals
operator|.
name|getDefinition
argument_list|()
operator|.
name|getType
argument_list|(
name|this
operator|.
name|type
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|exception
parameter_list|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not a type ["
operator|+
name|this
operator|.
name|type
operator|+
literal|"]."
argument_list|)
argument_list|)
throw|;
block|}
name|locals
operator|=
name|Locals
operator|.
name|newLocalScope
argument_list|(
name|locals
argument_list|)
expr_stmt|;
name|Variable
name|variable
init|=
name|locals
operator|.
name|addVariable
argument_list|(
name|location
argument_list|,
name|type
argument_list|,
name|name
argument_list|,
literal|true
argument_list|)
decl_stmt|;
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
name|ARRAY
condition|)
block|{
name|sub
operator|=
operator|new
name|SSubEachArray
argument_list|(
name|location
argument_list|,
name|variable
argument_list|,
name|expression
argument_list|,
name|block
argument_list|)
expr_stmt|;
block|}
elseif|else
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
operator|||
name|Iterable
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|expression
operator|.
name|actual
operator|.
name|clazz
argument_list|)
condition|)
block|{
name|sub
operator|=
operator|new
name|SSubEachIterable
argument_list|(
name|location
argument_list|,
name|variable
argument_list|,
name|expression
argument_list|,
name|block
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal for each type ["
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
name|sub
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
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
literal|"Extraneous for each loop."
argument_list|)
argument_list|)
throw|;
block|}
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
name|sub
operator|.
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
name|sub
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|globals
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|singleLineToString
argument_list|(
name|type
argument_list|,
name|name
argument_list|,
name|expression
argument_list|,
name|block
argument_list|)
return|;
block|}
block|}
end_class

end_unit

