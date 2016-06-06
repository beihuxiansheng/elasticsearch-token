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
name|Variables
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

begin_comment
comment|/**  * The root of all Painless trees.  Contains a series of statements.  */
end_comment

begin_class
DECL|class|SSource
specifier|public
specifier|final
class|class
name|SSource
extends|extends
name|AStatement
block|{
DECL|field|statements
specifier|final
name|List
argument_list|<
name|AStatement
argument_list|>
name|statements
decl_stmt|;
DECL|method|SSource
specifier|public
name|SSource
parameter_list|(
name|Location
name|location
parameter_list|,
name|List
argument_list|<
name|AStatement
argument_list|>
name|statements
parameter_list|)
block|{
name|super
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|statements
operator|=
name|statements
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|analyze
specifier|public
name|AStatement
name|analyze
parameter_list|(
name|Variables
name|variables
parameter_list|)
block|{
if|if
condition|(
name|statements
operator|==
literal|null
operator|||
name|statements
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot generate an empty script."
argument_list|)
argument_list|)
throw|;
block|}
name|variables
operator|.
name|incrementScope
argument_list|()
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
name|statements
operator|.
name|size
argument_list|()
condition|;
operator|++
name|index
control|)
block|{
name|AStatement
name|statement
init|=
name|statements
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
comment|// Note that we do not need to check after the last statement because
comment|// there is no statement that can be unreachable after the last.
if|if
condition|(
name|allEscape
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unreachable statement."
argument_list|)
argument_list|)
throw|;
block|}
name|statement
operator|.
name|lastSource
operator|=
name|index
operator|==
name|statements
operator|.
name|size
argument_list|()
operator|-
literal|1
expr_stmt|;
name|statements
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|statement
operator|.
name|analyze
argument_list|(
name|variables
argument_list|)
argument_list|)
expr_stmt|;
name|methodEscape
operator|=
name|statement
operator|.
name|methodEscape
expr_stmt|;
name|allEscape
operator|=
name|statement
operator|.
name|allEscape
expr_stmt|;
block|}
name|variables
operator|.
name|decrementScope
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|MethodWriter
name|writer
parameter_list|)
block|{
for|for
control|(
name|AStatement
name|statement
range|:
name|statements
control|)
block|{
name|statement
operator|.
name|write
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|methodEscape
condition|)
block|{
name|writer
operator|.
name|visitInsn
argument_list|(
name|Opcodes
operator|.
name|ACONST_NULL
argument_list|)
expr_stmt|;
name|writer
operator|.
name|returnValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

