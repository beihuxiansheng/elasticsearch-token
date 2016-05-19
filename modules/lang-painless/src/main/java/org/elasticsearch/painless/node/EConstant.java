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
name|CompilerSettings
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
name|MethodWriter
import|;
end_import

begin_comment
comment|/**  * Respresents a constant.  Note this replaces any other expression  * node with a constant value set during a cast.  (Internal only.)  */
end_comment

begin_class
DECL|class|EConstant
specifier|final
class|class
name|EConstant
extends|extends
name|AExpression
block|{
DECL|method|EConstant
name|EConstant
parameter_list|(
specifier|final
name|int
name|line
parameter_list|,
specifier|final
name|String
name|location
parameter_list|,
specifier|final
name|Object
name|constant
parameter_list|)
block|{
name|super
argument_list|(
name|line
argument_list|,
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|constant
operator|=
name|constant
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|analyze
name|void
name|analyze
parameter_list|(
specifier|final
name|CompilerSettings
name|settings
parameter_list|,
specifier|final
name|Definition
name|definition
parameter_list|,
specifier|final
name|Variables
name|variables
parameter_list|)
block|{
if|if
condition|(
name|constant
operator|instanceof
name|String
condition|)
block|{
name|actual
operator|=
name|definition
operator|.
name|getType
argument_list|(
literal|"String"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|constant
operator|instanceof
name|Double
condition|)
block|{
name|actual
operator|=
name|definition
operator|.
name|getType
argument_list|(
literal|"double"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|constant
operator|instanceof
name|Float
condition|)
block|{
name|actual
operator|=
name|definition
operator|.
name|getType
argument_list|(
literal|"float"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|constant
operator|instanceof
name|Long
condition|)
block|{
name|actual
operator|=
name|definition
operator|.
name|getType
argument_list|(
literal|"long"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|constant
operator|instanceof
name|Integer
condition|)
block|{
name|actual
operator|=
name|definition
operator|.
name|getType
argument_list|(
literal|"int"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|constant
operator|instanceof
name|Character
condition|)
block|{
name|actual
operator|=
name|definition
operator|.
name|getType
argument_list|(
literal|"char"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|constant
operator|instanceof
name|Short
condition|)
block|{
name|actual
operator|=
name|definition
operator|.
name|getType
argument_list|(
literal|"short"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|constant
operator|instanceof
name|Byte
condition|)
block|{
name|actual
operator|=
name|definition
operator|.
name|getType
argument_list|(
literal|"byte"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|constant
operator|instanceof
name|Boolean
condition|)
block|{
name|actual
operator|=
name|definition
operator|.
name|getType
argument_list|(
literal|"boolean"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|error
argument_list|(
literal|"Illegal tree structure."
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
name|void
name|write
parameter_list|(
specifier|final
name|CompilerSettings
name|settings
parameter_list|,
specifier|final
name|Definition
name|definition
parameter_list|,
specifier|final
name|MethodWriter
name|adapter
parameter_list|)
block|{
specifier|final
name|Sort
name|sort
init|=
name|actual
operator|.
name|sort
decl_stmt|;
switch|switch
condition|(
name|sort
condition|)
block|{
case|case
name|STRING
case|:
name|adapter
operator|.
name|push
argument_list|(
operator|(
name|String
operator|)
name|constant
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|adapter
operator|.
name|push
argument_list|(
operator|(
name|double
operator|)
name|constant
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|adapter
operator|.
name|push
argument_list|(
operator|(
name|float
operator|)
name|constant
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|adapter
operator|.
name|push
argument_list|(
operator|(
name|long
operator|)
name|constant
argument_list|)
expr_stmt|;
break|break;
case|case
name|INT
case|:
name|adapter
operator|.
name|push
argument_list|(
operator|(
name|int
operator|)
name|constant
argument_list|)
expr_stmt|;
break|break;
case|case
name|CHAR
case|:
name|adapter
operator|.
name|push
argument_list|(
operator|(
name|char
operator|)
name|constant
argument_list|)
expr_stmt|;
break|break;
case|case
name|SHORT
case|:
name|adapter
operator|.
name|push
argument_list|(
operator|(
name|short
operator|)
name|constant
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTE
case|:
name|adapter
operator|.
name|push
argument_list|(
operator|(
name|byte
operator|)
name|constant
argument_list|)
expr_stmt|;
break|break;
case|case
name|BOOL
case|:
if|if
condition|(
name|tru
operator|!=
literal|null
operator|&&
operator|(
name|boolean
operator|)
name|constant
condition|)
block|{
name|adapter
operator|.
name|goTo
argument_list|(
name|tru
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fals
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|boolean
operator|)
name|constant
condition|)
block|{
name|adapter
operator|.
name|goTo
argument_list|(
name|fals
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tru
operator|==
literal|null
operator|&&
name|fals
operator|==
literal|null
condition|)
block|{
name|adapter
operator|.
name|push
argument_list|(
operator|(
name|boolean
operator|)
name|constant
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|error
argument_list|(
literal|"Illegal tree structure."
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|sort
operator|!=
name|Sort
operator|.
name|BOOL
condition|)
block|{
name|adapter
operator|.
name|writeBranch
argument_list|(
name|tru
argument_list|,
name|fals
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

