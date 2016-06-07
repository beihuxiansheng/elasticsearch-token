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
name|FunctionRef
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
name|elasticsearch
operator|.
name|painless
operator|.
name|Variables
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
name|LAMBDA_BOOTSTRAP_HANDLE
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
name|LambdaMetafactory
import|;
end_import

begin_comment
comment|/**  * Represents a function reference.  */
end_comment

begin_class
DECL|class|EFunctionRef
specifier|public
class|class
name|EFunctionRef
extends|extends
name|AExpression
block|{
DECL|field|type
specifier|public
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|call
specifier|public
specifier|final
name|String
name|call
decl_stmt|;
DECL|field|ref
specifier|private
name|FunctionRef
name|ref
decl_stmt|;
DECL|method|EFunctionRef
specifier|public
name|EFunctionRef
parameter_list|(
name|Location
name|location
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|call
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
name|call
operator|=
name|call
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
if|if
condition|(
name|expected
operator|==
literal|null
condition|)
block|{
name|ref
operator|=
literal|null
expr_stmt|;
name|actual
operator|=
name|Definition
operator|.
name|getType
argument_list|(
literal|"String"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|ref
operator|=
operator|new
name|FunctionRef
argument_list|(
name|expected
argument_list|,
name|type
argument_list|,
name|call
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
name|createError
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|actual
operator|=
name|expected
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
parameter_list|)
block|{
if|if
condition|(
name|ref
operator|==
literal|null
condition|)
block|{
name|writer
operator|.
name|push
argument_list|(
name|type
operator|+
literal|"."
operator|+
name|call
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|writeDebugInfo
argument_list|(
name|location
argument_list|)
expr_stmt|;
comment|// currently if the interface differs, we ask for a bridge, but maybe we should do smarter checking?
comment|// either way, stuff will fail if its wrong :)
if|if
condition|(
name|ref
operator|.
name|interfaceType
operator|.
name|equals
argument_list|(
name|ref
operator|.
name|samType
argument_list|)
condition|)
block|{
name|writer
operator|.
name|invokeDynamic
argument_list|(
name|ref
operator|.
name|invokedName
argument_list|,
name|ref
operator|.
name|invokedType
operator|.
name|getDescriptor
argument_list|()
argument_list|,
name|LAMBDA_BOOTSTRAP_HANDLE
argument_list|,
name|ref
operator|.
name|samType
argument_list|,
name|ref
operator|.
name|implMethod
argument_list|,
name|ref
operator|.
name|samType
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|invokeDynamic
argument_list|(
name|ref
operator|.
name|invokedName
argument_list|,
name|ref
operator|.
name|invokedType
operator|.
name|getDescriptor
argument_list|()
argument_list|,
name|LAMBDA_BOOTSTRAP_HANDLE
argument_list|,
name|ref
operator|.
name|samType
argument_list|,
name|ref
operator|.
name|implMethod
argument_list|,
name|ref
operator|.
name|samType
argument_list|,
name|LambdaMetafactory
operator|.
name|FLAG_BRIDGES
argument_list|,
literal|1
argument_list|,
name|ref
operator|.
name|interfaceType
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

