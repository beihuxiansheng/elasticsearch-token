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
name|java
operator|.
name|util
operator|.
name|List
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
comment|/**  * Represents a map initialization shortcut.  */
end_comment

begin_class
DECL|class|EMapInit
specifier|public
class|class
name|EMapInit
extends|extends
name|AExpression
block|{
DECL|field|keys
specifier|final
name|List
argument_list|<
name|AExpression
argument_list|>
name|keys
decl_stmt|;
DECL|field|values
specifier|final
name|List
argument_list|<
name|AExpression
argument_list|>
name|values
decl_stmt|;
DECL|field|constructor
name|Method
name|constructor
init|=
literal|null
decl_stmt|;
DECL|field|method
name|Method
name|method
init|=
literal|null
decl_stmt|;
DECL|method|EMapInit
specifier|public
name|EMapInit
parameter_list|(
name|Location
name|location
parameter_list|,
name|List
argument_list|<
name|AExpression
argument_list|>
name|keys
parameter_list|,
name|List
argument_list|<
name|AExpression
argument_list|>
name|values
parameter_list|)
block|{
name|super
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|keys
operator|=
name|keys
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
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
for|for
control|(
name|AExpression
name|key
range|:
name|keys
control|)
block|{
name|key
operator|.
name|extractVariables
argument_list|(
name|variables
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AExpression
name|value
range|:
name|values
control|)
block|{
name|value
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
try|try
block|{
name|actual
operator|=
name|Definition
operator|.
name|getType
argument_list|(
literal|"HashMap"
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
name|IllegalStateException
argument_list|(
literal|"Illegal tree structure."
argument_list|)
argument_list|)
throw|;
block|}
name|constructor
operator|=
name|actual
operator|.
name|struct
operator|.
name|constructors
operator|.
name|get
argument_list|(
operator|new
name|MethodKey
argument_list|(
literal|"<init>"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|constructor
operator|==
literal|null
condition|)
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
name|method
operator|=
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
literal|"put"
argument_list|,
literal|2
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
name|IllegalStateException
argument_list|(
literal|"Illegal tree structure."
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|keys
operator|.
name|size
argument_list|()
operator|!=
name|values
operator|.
name|size
argument_list|()
condition|)
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
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|keys
operator|.
name|size
argument_list|()
condition|;
operator|++
name|index
control|)
block|{
name|AExpression
name|expression
init|=
name|keys
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|expression
operator|.
name|expected
operator|=
name|Definition
operator|.
name|DEF_TYPE
expr_stmt|;
name|expression
operator|.
name|internal
operator|=
literal|true
expr_stmt|;
name|expression
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
name|keys
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|expression
operator|.
name|cast
argument_list|(
name|locals
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|values
operator|.
name|size
argument_list|()
condition|;
operator|++
name|index
control|)
block|{
name|AExpression
name|expression
init|=
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|expression
operator|.
name|expected
operator|=
name|Definition
operator|.
name|DEF_TYPE
expr_stmt|;
name|expression
operator|.
name|internal
operator|=
literal|true
expr_stmt|;
name|expression
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
name|values
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|expression
operator|.
name|cast
argument_list|(
name|locals
argument_list|)
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
name|writeDebugInfo
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newInstance
argument_list|(
name|actual
operator|.
name|type
argument_list|)
expr_stmt|;
name|writer
operator|.
name|dup
argument_list|()
expr_stmt|;
name|writer
operator|.
name|invokeConstructor
argument_list|(
name|constructor
operator|.
name|owner
operator|.
name|type
argument_list|,
name|constructor
operator|.
name|method
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|keys
operator|.
name|size
argument_list|()
condition|;
operator|++
name|index
control|)
block|{
name|AExpression
name|key
init|=
name|keys
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|AExpression
name|value
init|=
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|writer
operator|.
name|dup
argument_list|()
expr_stmt|;
name|key
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|globals
argument_list|)
expr_stmt|;
name|value
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|globals
argument_list|)
expr_stmt|;
name|method
operator|.
name|write
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

