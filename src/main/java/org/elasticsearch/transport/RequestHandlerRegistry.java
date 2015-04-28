begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|IllegalStateException
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
name|Constructor
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RequestHandlerRegistry
specifier|public
class|class
name|RequestHandlerRegistry
parameter_list|<
name|Request
extends|extends
name|TransportRequest
parameter_list|>
block|{
DECL|field|action
specifier|private
specifier|final
name|String
name|action
decl_stmt|;
DECL|field|requestConstructor
specifier|private
specifier|final
name|Constructor
argument_list|<
name|Request
argument_list|>
name|requestConstructor
decl_stmt|;
DECL|field|handler
specifier|private
specifier|final
name|TransportRequestHandler
argument_list|<
name|Request
argument_list|>
name|handler
decl_stmt|;
DECL|field|forceExecution
specifier|private
specifier|final
name|boolean
name|forceExecution
decl_stmt|;
DECL|field|executor
specifier|private
specifier|final
name|String
name|executor
decl_stmt|;
DECL|method|RequestHandlerRegistry
name|RequestHandlerRegistry
parameter_list|(
name|String
name|action
parameter_list|,
name|Class
argument_list|<
name|Request
argument_list|>
name|request
parameter_list|,
name|TransportRequestHandler
argument_list|<
name|Request
argument_list|>
name|handler
parameter_list|,
name|String
name|executor
parameter_list|,
name|boolean
name|forceExecution
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
try|try
block|{
name|this
operator|.
name|requestConstructor
operator|=
name|request
operator|.
name|getDeclaredConstructor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed to create constructor (does it have a default constructor?) for request "
operator|+
name|request
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|requestConstructor
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
assert|assert
name|newRequest
argument_list|()
operator|!=
literal|null
assert|;
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|forceExecution
operator|=
name|forceExecution
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
block|}
DECL|method|getAction
specifier|public
name|String
name|getAction
parameter_list|()
block|{
return|return
name|action
return|;
block|}
DECL|method|newRequest
specifier|public
name|Request
name|newRequest
parameter_list|()
block|{
try|try
block|{
return|return
name|requestConstructor
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed to instantiate request "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getHandler
specifier|public
name|TransportRequestHandler
argument_list|<
name|Request
argument_list|>
name|getHandler
parameter_list|()
block|{
return|return
name|handler
return|;
block|}
DECL|method|isForceExecution
specifier|public
name|boolean
name|isForceExecution
parameter_list|()
block|{
return|return
name|forceExecution
return|;
block|}
DECL|method|getExecutor
specifier|public
name|String
name|getExecutor
parameter_list|()
block|{
return|return
name|executor
return|;
block|}
block|}
end_class

end_unit

