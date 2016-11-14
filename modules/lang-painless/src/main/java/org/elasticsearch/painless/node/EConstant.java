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
name|Definition
operator|.
name|Sort
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
name|MethodWriter
import|;
end_import

begin_comment
comment|/**  * Represents a constant inserted into the tree replacing  * other constants during constant folding.  (Internal only.)  */
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
name|Location
name|location
parameter_list|,
name|Object
name|constant
parameter_list|)
block|{
name|super
argument_list|(
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
operator|new
name|IllegalStateException
argument_list|(
literal|"Illegal tree structure."
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
if|if
condition|(
name|constant
operator|instanceof
name|String
condition|)
block|{
name|actual
operator|=
name|Definition
operator|.
name|STRING_TYPE
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
name|Definition
operator|.
name|DOUBLE_TYPE
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
name|Definition
operator|.
name|FLOAT_TYPE
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
name|Definition
operator|.
name|LONG_TYPE
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
name|Definition
operator|.
name|INT_TYPE
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
name|Definition
operator|.
name|CHAR_TYPE
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
name|Definition
operator|.
name|SHORT_TYPE
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
name|Definition
operator|.
name|BYTE_TYPE
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
name|Definition
operator|.
name|BOOLEAN_TYPE
expr_stmt|;
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
name|writer
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
name|writer
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
name|writer
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
name|writer
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
name|writer
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
name|writer
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
name|writer
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
name|writer
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
name|writer
operator|.
name|push
argument_list|(
operator|(
name|boolean
operator|)
name|constant
argument_list|)
expr_stmt|;
break|break;
default|default:
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
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|c
init|=
name|constant
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|constant
operator|instanceof
name|String
condition|)
block|{
name|c
operator|=
literal|"'"
operator|+
name|c
operator|+
literal|"'"
expr_stmt|;
block|}
return|return
name|singleLineToString
argument_list|(
name|constant
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|c
argument_list|)
return|;
block|}
block|}
end_class

end_unit

