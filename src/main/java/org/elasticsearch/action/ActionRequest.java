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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamOutput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ActionRequest
specifier|public
specifier|abstract
class|class
name|ActionRequest
parameter_list|<
name|T
extends|extends
name|ActionRequest
parameter_list|>
extends|extends
name|TransportRequest
block|{
DECL|field|listenerThreaded
specifier|private
name|boolean
name|listenerThreaded
init|=
literal|false
decl_stmt|;
DECL|method|ActionRequest
specifier|protected
name|ActionRequest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|ActionRequest
specifier|protected
name|ActionRequest
parameter_list|(
name|ActionRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// this does not set the listenerThreaded API, if needed, its up to the caller to set it
comment|// since most times, we actually want it to not be threaded...
comment|//this.listenerThreaded = request.listenerThreaded();
block|}
comment|/**      * Should the response listener be executed on a thread or not.      *<p/>      *<p>When not executing on a thread, it will either be executed on the calling thread, or      * on an expensive, IO based, thread.      */
DECL|method|listenerThreaded
specifier|public
specifier|final
name|boolean
name|listenerThreaded
parameter_list|()
block|{
return|return
name|this
operator|.
name|listenerThreaded
return|;
block|}
comment|/**      * Sets if the response listener be executed on a thread or not.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|listenerThreaded
specifier|public
specifier|final
name|T
name|listenerThreaded
parameter_list|(
name|boolean
name|listenerThreaded
parameter_list|)
block|{
name|this
operator|.
name|listenerThreaded
operator|=
name|listenerThreaded
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
DECL|method|validate
specifier|public
specifier|abstract
name|ActionRequestValidationException
name|validate
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

