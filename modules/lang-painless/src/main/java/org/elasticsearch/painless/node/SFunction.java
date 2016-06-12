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
name|Def
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
name|Parameter
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
name|FunctionReserved
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
name|ClassWriter
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
name|lang
operator|.
name|invoke
operator|.
name|MethodType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  * Represents a user-defined function.  */
end_comment

begin_class
DECL|class|SFunction
specifier|public
class|class
name|SFunction
extends|extends
name|AStatement
block|{
DECL|field|reserved
specifier|final
name|FunctionReserved
name|reserved
decl_stmt|;
DECL|field|rtnTypeStr
specifier|final
name|String
name|rtnTypeStr
decl_stmt|;
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|paramTypeStrs
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|paramTypeStrs
decl_stmt|;
DECL|field|paramNameStrs
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|paramNameStrs
decl_stmt|;
DECL|field|statements
specifier|final
name|List
argument_list|<
name|AStatement
argument_list|>
name|statements
decl_stmt|;
DECL|field|rtnType
name|Type
name|rtnType
init|=
literal|null
decl_stmt|;
DECL|field|parameters
name|List
argument_list|<
name|Parameter
argument_list|>
name|parameters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|method
name|Method
name|method
init|=
literal|null
decl_stmt|;
DECL|field|locals
name|Locals
name|locals
init|=
literal|null
decl_stmt|;
DECL|method|SFunction
specifier|public
name|SFunction
parameter_list|(
name|FunctionReserved
name|reserved
parameter_list|,
name|Location
name|location
parameter_list|,
name|String
name|rtnType
parameter_list|,
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paramTypes
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paramNames
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
name|reserved
operator|=
name|reserved
expr_stmt|;
name|this
operator|.
name|rtnTypeStr
operator|=
name|rtnType
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|paramTypeStrs
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|paramTypes
argument_list|)
expr_stmt|;
name|this
operator|.
name|paramNameStrs
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|paramNames
argument_list|)
expr_stmt|;
name|this
operator|.
name|statements
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|statements
argument_list|)
expr_stmt|;
block|}
DECL|method|generate
name|void
name|generate
parameter_list|()
block|{
try|try
block|{
name|rtnType
operator|=
name|Definition
operator|.
name|getType
argument_list|(
name|rtnTypeStr
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
literal|"Illegal return type ["
operator|+
name|rtnTypeStr
operator|+
literal|"] for function ["
operator|+
name|name
operator|+
literal|"]."
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|paramTypeStrs
operator|.
name|size
argument_list|()
operator|!=
name|paramNameStrs
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
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|paramClasses
init|=
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[
name|this
operator|.
name|paramTypeStrs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|List
argument_list|<
name|Type
argument_list|>
name|paramTypes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|param
init|=
literal|0
init|;
name|param
operator|<
name|this
operator|.
name|paramTypeStrs
operator|.
name|size
argument_list|()
condition|;
operator|++
name|param
control|)
block|{
try|try
block|{
name|Type
name|paramType
init|=
name|Definition
operator|.
name|getType
argument_list|(
name|this
operator|.
name|paramTypeStrs
operator|.
name|get
argument_list|(
name|param
argument_list|)
argument_list|)
decl_stmt|;
name|paramClasses
index|[
name|param
index|]
operator|=
name|paramType
operator|.
name|clazz
expr_stmt|;
name|paramTypes
operator|.
name|add
argument_list|(
name|paramType
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|add
argument_list|(
operator|new
name|Parameter
argument_list|(
name|location
argument_list|,
name|paramNameStrs
operator|.
name|get
argument_list|(
name|param
argument_list|)
argument_list|,
name|paramType
argument_list|)
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
literal|"Illegal parameter type ["
operator|+
name|this
operator|.
name|paramTypeStrs
operator|.
name|get
argument_list|(
name|param
argument_list|)
operator|+
literal|"] for function ["
operator|+
name|name
operator|+
literal|"]."
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|commons
operator|.
name|Method
name|method
init|=
operator|new
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|commons
operator|.
name|Method
argument_list|(
name|name
argument_list|,
name|MethodType
operator|.
name|methodType
argument_list|(
name|rtnType
operator|.
name|clazz
argument_list|,
name|paramClasses
argument_list|)
operator|.
name|toMethodDescriptorString
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|method
operator|=
operator|new
name|Method
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|rtnType
argument_list|,
name|paramTypes
argument_list|,
name|method
argument_list|,
name|Modifier
operator|.
name|STATIC
operator||
name|Modifier
operator|.
name|PRIVATE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
literal|"Cannot generate an empty function ["
operator|+
name|name
operator|+
literal|"]."
argument_list|)
argument_list|)
throw|;
block|}
name|this
operator|.
name|locals
operator|=
operator|new
name|Locals
argument_list|(
name|reserved
argument_list|,
name|locals
argument_list|,
name|rtnType
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
name|locals
operator|=
name|this
operator|.
name|locals
expr_stmt|;
name|locals
operator|.
name|incrementScope
argument_list|()
expr_stmt|;
name|AStatement
name|last
init|=
name|statements
operator|.
name|get
argument_list|(
name|statements
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|AStatement
name|statement
range|:
name|statements
control|)
block|{
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
name|statement
operator|==
name|last
expr_stmt|;
name|statement
operator|.
name|analyze
argument_list|(
name|locals
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
if|if
condition|(
operator|!
name|methodEscape
operator|&&
name|rtnType
operator|.
name|sort
operator|!=
name|Sort
operator|.
name|VOID
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not all paths provide a return value for method ["
operator|+
name|name
operator|+
literal|"]."
argument_list|)
argument_list|)
throw|;
block|}
name|locals
operator|.
name|decrementScope
argument_list|()
expr_stmt|;
block|}
comment|/** Writes the function to given ClassWriter. */
DECL|method|write
name|void
name|write
parameter_list|(
name|ClassWriter
name|writer
parameter_list|,
name|BitSet
name|statements
parameter_list|)
block|{
specifier|final
name|MethodWriter
name|function
init|=
operator|new
name|MethodWriter
argument_list|(
name|Opcodes
operator|.
name|ACC_PRIVATE
operator||
name|Opcodes
operator|.
name|ACC_STATIC
argument_list|,
name|method
operator|.
name|method
argument_list|,
name|writer
argument_list|,
name|statements
argument_list|)
decl_stmt|;
name|write
argument_list|(
name|function
argument_list|)
expr_stmt|;
name|function
operator|.
name|endMethod
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
name|void
name|write
parameter_list|(
name|MethodWriter
name|function
parameter_list|)
block|{
if|if
condition|(
name|reserved
operator|.
name|getMaxLoopCounter
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// if there is infinite loop protection, we do this once:
comment|// int #loop = settings.getMaxLoopCounter()
name|Variable
name|loop
init|=
name|locals
operator|.
name|getVariable
argument_list|(
literal|null
argument_list|,
name|FunctionReserved
operator|.
name|LOOP
argument_list|)
decl_stmt|;
name|function
operator|.
name|push
argument_list|(
name|reserved
operator|.
name|getMaxLoopCounter
argument_list|()
argument_list|)
expr_stmt|;
name|function
operator|.
name|visitVarInsn
argument_list|(
name|Opcodes
operator|.
name|ISTORE
argument_list|,
name|loop
operator|.
name|slot
argument_list|)
expr_stmt|;
block|}
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
name|function
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|methodEscape
condition|)
block|{
if|if
condition|(
name|rtnType
operator|.
name|sort
operator|==
name|Sort
operator|.
name|VOID
condition|)
block|{
name|function
operator|.
name|returnValue
argument_list|()
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
block|}
DECL|method|getStaticHandleFieldName
name|String
name|getStaticHandleFieldName
parameter_list|()
block|{
return|return
name|Def
operator|.
name|getUserFunctionHandleFieldName
argument_list|(
name|name
argument_list|,
name|parameters
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

