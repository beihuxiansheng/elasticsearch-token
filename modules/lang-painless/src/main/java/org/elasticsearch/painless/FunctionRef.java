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
name|Method
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
name|Handle
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
name|Type
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
name|MethodHandle
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

begin_comment
comment|/**   * computes "everything you need" to call LambdaMetaFactory, given an expected interface,  * and reference class + method name  */
end_comment

begin_class
DECL|class|FunctionRef
specifier|public
class|class
name|FunctionRef
block|{
DECL|field|invokedName
specifier|public
specifier|final
name|String
name|invokedName
decl_stmt|;
DECL|field|invokedType
specifier|public
specifier|final
name|Type
name|invokedType
decl_stmt|;
DECL|field|invokedMethodType
specifier|public
specifier|final
name|MethodType
name|invokedMethodType
decl_stmt|;
DECL|field|implMethod
specifier|public
specifier|final
name|Handle
name|implMethod
decl_stmt|;
DECL|field|implMethodHandle
specifier|public
specifier|final
name|MethodHandle
name|implMethodHandle
decl_stmt|;
DECL|field|samType
specifier|public
specifier|final
name|Type
name|samType
decl_stmt|;
DECL|field|samMethodType
specifier|public
specifier|final
name|MethodType
name|samMethodType
decl_stmt|;
DECL|field|interfaceType
specifier|public
specifier|final
name|Type
name|interfaceType
decl_stmt|;
DECL|field|interfaceMethodType
specifier|public
specifier|final
name|MethodType
name|interfaceMethodType
decl_stmt|;
DECL|method|FunctionRef
specifier|public
name|FunctionRef
parameter_list|(
name|Definition
operator|.
name|Type
name|expected
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|call
parameter_list|)
block|{
name|boolean
name|isCtorReference
init|=
literal|"new"
operator|.
name|equals
argument_list|(
name|call
argument_list|)
decl_stmt|;
comment|// check its really a functional interface
comment|// for e.g. Comparable
name|Method
name|method
init|=
name|expected
operator|.
name|struct
operator|.
name|getFunctionalMethod
argument_list|()
decl_stmt|;
if|if
condition|(
name|method
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot convert function reference ["
operator|+
name|type
operator|+
literal|"::"
operator|+
name|call
operator|+
literal|"] "
operator|+
literal|"to ["
operator|+
name|expected
operator|.
name|name
operator|+
literal|"], not a functional interface"
argument_list|)
throw|;
block|}
comment|// e.g. compareTo
name|invokedName
operator|=
name|method
operator|.
name|name
expr_stmt|;
comment|// e.g. (Object)Comparator
name|invokedType
operator|=
name|Type
operator|.
name|getMethodType
argument_list|(
name|expected
operator|.
name|type
argument_list|)
expr_stmt|;
name|invokedMethodType
operator|=
name|MethodType
operator|.
name|methodType
argument_list|(
name|expected
operator|.
name|clazz
argument_list|)
expr_stmt|;
comment|// e.g. (Object,Object)int
name|interfaceType
operator|=
name|Type
operator|.
name|getMethodType
argument_list|(
name|method
operator|.
name|method
operator|.
name|getDescriptor
argument_list|()
argument_list|)
expr_stmt|;
name|interfaceMethodType
operator|=
name|method
operator|.
name|handle
operator|.
name|type
argument_list|()
operator|.
name|dropParameterTypes
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// lookup requested method
name|Definition
operator|.
name|Struct
name|struct
init|=
name|Definition
operator|.
name|getType
argument_list|(
name|type
argument_list|)
operator|.
name|struct
decl_stmt|;
specifier|final
name|Definition
operator|.
name|Method
name|impl
decl_stmt|;
comment|// ctor ref
if|if
condition|(
name|isCtorReference
condition|)
block|{
name|impl
operator|=
name|struct
operator|.
name|constructors
operator|.
name|get
argument_list|(
operator|new
name|Definition
operator|.
name|MethodKey
argument_list|(
literal|"<init>"
argument_list|,
name|method
operator|.
name|arguments
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// look for a static impl first
name|Definition
operator|.
name|Method
name|staticImpl
init|=
name|struct
operator|.
name|staticMethods
operator|.
name|get
argument_list|(
operator|new
name|Definition
operator|.
name|MethodKey
argument_list|(
name|call
argument_list|,
name|method
operator|.
name|arguments
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|staticImpl
operator|==
literal|null
condition|)
block|{
comment|// otherwise a virtual impl
name|impl
operator|=
name|struct
operator|.
name|methods
operator|.
name|get
argument_list|(
operator|new
name|Definition
operator|.
name|MethodKey
argument_list|(
name|call
argument_list|,
name|method
operator|.
name|arguments
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|impl
operator|=
name|staticImpl
expr_stmt|;
block|}
block|}
if|if
condition|(
name|impl
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown reference ["
operator|+
name|type
operator|+
literal|"::"
operator|+
name|call
operator|+
literal|"] matching "
operator|+
literal|"["
operator|+
name|expected
operator|+
literal|"]"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|tag
decl_stmt|;
if|if
condition|(
name|isCtorReference
condition|)
block|{
name|tag
operator|=
name|Opcodes
operator|.
name|H_NEWINVOKESPECIAL
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|impl
operator|.
name|modifiers
argument_list|)
condition|)
block|{
name|tag
operator|=
name|Opcodes
operator|.
name|H_INVOKESTATIC
expr_stmt|;
block|}
else|else
block|{
name|tag
operator|=
name|Opcodes
operator|.
name|H_INVOKEVIRTUAL
expr_stmt|;
block|}
name|implMethod
operator|=
operator|new
name|Handle
argument_list|(
name|tag
argument_list|,
name|struct
operator|.
name|type
operator|.
name|getInternalName
argument_list|()
argument_list|,
name|impl
operator|.
name|name
argument_list|,
name|impl
operator|.
name|method
operator|.
name|getDescriptor
argument_list|()
argument_list|)
expr_stmt|;
name|implMethodHandle
operator|=
name|impl
operator|.
name|handle
expr_stmt|;
if|if
condition|(
name|isCtorReference
condition|)
block|{
name|samType
operator|=
name|Type
operator|.
name|getMethodType
argument_list|(
name|interfaceType
operator|.
name|getReturnType
argument_list|()
argument_list|,
name|impl
operator|.
name|method
operator|.
name|getArgumentTypes
argument_list|()
argument_list|)
expr_stmt|;
name|samMethodType
operator|=
name|MethodType
operator|.
name|methodType
argument_list|(
name|interfaceMethodType
operator|.
name|returnType
argument_list|()
argument_list|,
name|impl
operator|.
name|handle
operator|.
name|type
argument_list|()
operator|.
name|parameterArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|impl
operator|.
name|modifiers
argument_list|)
condition|)
block|{
name|samType
operator|=
name|Type
operator|.
name|getMethodType
argument_list|(
name|impl
operator|.
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|,
name|impl
operator|.
name|method
operator|.
name|getArgumentTypes
argument_list|()
argument_list|)
expr_stmt|;
name|samMethodType
operator|=
name|impl
operator|.
name|handle
operator|.
name|type
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Type
index|[]
name|argTypes
init|=
name|impl
operator|.
name|method
operator|.
name|getArgumentTypes
argument_list|()
decl_stmt|;
name|Type
index|[]
name|params
init|=
operator|new
name|Type
index|[
name|argTypes
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|argTypes
argument_list|,
literal|0
argument_list|,
name|params
argument_list|,
literal|1
argument_list|,
name|argTypes
operator|.
name|length
argument_list|)
expr_stmt|;
name|params
index|[
literal|0
index|]
operator|=
name|struct
operator|.
name|type
expr_stmt|;
name|samType
operator|=
name|Type
operator|.
name|getMethodType
argument_list|(
name|impl
operator|.
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|samMethodType
operator|=
name|impl
operator|.
name|handle
operator|.
name|type
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

