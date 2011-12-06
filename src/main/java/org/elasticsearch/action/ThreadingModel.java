begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_enum
DECL|enum|ThreadingModel
specifier|public
enum|enum
name|ThreadingModel
block|{
DECL|enum constant|NONE
name|NONE
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
DECL|enum constant|OPERATION
name|OPERATION
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|,
DECL|enum constant|LISTENER
name|LISTENER
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
block|,
DECL|enum constant|OPERATION_LISTENER
name|OPERATION_LISTENER
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|)
block|;
DECL|field|id
specifier|private
name|byte
name|id
decl_stmt|;
DECL|method|ThreadingModel
name|ThreadingModel
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
comment|/**      *<tt>true</tt> if the actual operation the action represents will be executed      * on a different thread than the calling thread (assuming it will be executed      * on the same node).      */
DECL|method|threadedOperation
specifier|public
name|boolean
name|threadedOperation
parameter_list|()
block|{
return|return
name|this
operator|==
name|OPERATION
operator|||
name|this
operator|==
name|OPERATION_LISTENER
return|;
block|}
comment|/**      *<tt>true</tt> if the invocation of the action result listener will be executed      * on a different thread (than the calling thread or an "expensive" thread, like the      * IO thread).      */
DECL|method|threadedListener
specifier|public
name|boolean
name|threadedListener
parameter_list|()
block|{
return|return
name|this
operator|==
name|LISTENER
operator|||
name|this
operator|==
name|OPERATION_LISTENER
return|;
block|}
DECL|method|addListener
specifier|public
name|ThreadingModel
name|addListener
parameter_list|()
block|{
if|if
condition|(
name|this
operator|==
name|NONE
condition|)
block|{
return|return
name|LISTENER
return|;
block|}
if|if
condition|(
name|this
operator|==
name|OPERATION
condition|)
block|{
return|return
name|OPERATION_LISTENER
return|;
block|}
return|return
name|this
return|;
block|}
DECL|method|removeListener
specifier|public
name|ThreadingModel
name|removeListener
parameter_list|()
block|{
if|if
condition|(
name|this
operator|==
name|LISTENER
condition|)
block|{
return|return
name|NONE
return|;
block|}
if|if
condition|(
name|this
operator|==
name|OPERATION_LISTENER
condition|)
block|{
return|return
name|OPERATION
return|;
block|}
return|return
name|this
return|;
block|}
DECL|method|addOperation
specifier|public
name|ThreadingModel
name|addOperation
parameter_list|()
block|{
if|if
condition|(
name|this
operator|==
name|NONE
condition|)
block|{
return|return
name|OPERATION
return|;
block|}
if|if
condition|(
name|this
operator|==
name|LISTENER
condition|)
block|{
return|return
name|OPERATION_LISTENER
return|;
block|}
return|return
name|this
return|;
block|}
DECL|method|removeOperation
specifier|public
name|ThreadingModel
name|removeOperation
parameter_list|()
block|{
if|if
condition|(
name|this
operator|==
name|OPERATION
condition|)
block|{
return|return
name|NONE
return|;
block|}
if|if
condition|(
name|this
operator|==
name|OPERATION_LISTENER
condition|)
block|{
return|return
name|LISTENER
return|;
block|}
return|return
name|this
return|;
block|}
DECL|method|fromId
specifier|public
specifier|static
name|ThreadingModel
name|fromId
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|0
condition|)
block|{
return|return
name|NONE
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|1
condition|)
block|{
return|return
name|OPERATION
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|2
condition|)
block|{
return|return
name|LISTENER
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|3
condition|)
block|{
return|return
name|OPERATION_LISTENER
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No threading model for ["
operator|+
name|id
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_enum

end_unit

