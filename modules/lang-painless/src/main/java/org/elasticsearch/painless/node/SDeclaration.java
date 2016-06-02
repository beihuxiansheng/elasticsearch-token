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
name|Variables
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
name|Variables
operator|.
name|Variable
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
name|MethodWriter
import|;
end_import

begin_comment
comment|/**  * Represents a single variable declaration.  */
end_comment

begin_class
DECL|class|SDeclaration
specifier|public
specifier|final
class|class
name|SDeclaration
extends|extends
name|AStatement
block|{
DECL|field|type
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|expression
name|AExpression
name|expression
decl_stmt|;
DECL|field|variable
name|Variable
name|variable
decl_stmt|;
DECL|method|SDeclaration
specifier|public
name|SDeclaration
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
name|type
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|analyze
name|void
name|analyze
parameter_list|(
name|Variables
name|variables
parameter_list|)
block|{
specifier|final
name|Type
name|type
decl_stmt|;
try|try
block|{
name|type
operator|=
name|Definition
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
if|if
condition|(
name|expression
operator|!=
literal|null
condition|)
block|{
name|expression
operator|.
name|expected
operator|=
name|type
expr_stmt|;
name|expression
operator|.
name|analyze
argument_list|(
name|variables
argument_list|)
expr_stmt|;
name|expression
operator|=
name|expression
operator|.
name|cast
argument_list|(
name|variables
argument_list|)
expr_stmt|;
block|}
name|variable
operator|=
name|variables
operator|.
name|addVariable
argument_list|(
name|location
argument_list|,
name|type
argument_list|,
name|name
argument_list|,
literal|false
argument_list|,
literal|false
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
parameter_list|)
block|{
name|writer
operator|.
name|writeStatementOffset
argument_list|(
name|location
argument_list|)
expr_stmt|;
if|if
condition|(
name|expression
operator|==
literal|null
condition|)
block|{
switch|switch
condition|(
name|variable
operator|.
name|type
operator|.
name|sort
condition|)
block|{
case|case
name|VOID
case|:
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
name|writer
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
name|writer
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
name|writer
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
name|writer
operator|.
name|push
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
break|break;
default|default:
name|writer
operator|.
name|visitInsn
argument_list|(
name|Opcodes
operator|.
name|ACONST_NULL
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|expression
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
name|slot
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

