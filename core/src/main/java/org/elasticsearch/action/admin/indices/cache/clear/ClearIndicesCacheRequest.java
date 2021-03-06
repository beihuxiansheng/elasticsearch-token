begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.cache.clear
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|cache
operator|.
name|clear
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|broadcast
operator|.
name|BroadcastRequest
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|ClearIndicesCacheRequest
specifier|public
class|class
name|ClearIndicesCacheRequest
extends|extends
name|BroadcastRequest
argument_list|<
name|ClearIndicesCacheRequest
argument_list|>
block|{
DECL|field|queryCache
specifier|private
name|boolean
name|queryCache
init|=
literal|false
decl_stmt|;
DECL|field|fieldDataCache
specifier|private
name|boolean
name|fieldDataCache
init|=
literal|false
decl_stmt|;
DECL|field|recycler
specifier|private
name|boolean
name|recycler
init|=
literal|false
decl_stmt|;
DECL|field|requestCache
specifier|private
name|boolean
name|requestCache
init|=
literal|false
decl_stmt|;
DECL|field|fields
specifier|private
name|String
index|[]
name|fields
init|=
literal|null
decl_stmt|;
DECL|method|ClearIndicesCacheRequest
specifier|public
name|ClearIndicesCacheRequest
parameter_list|()
block|{     }
DECL|method|ClearIndicesCacheRequest
specifier|public
name|ClearIndicesCacheRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|super
argument_list|(
name|indices
argument_list|)
expr_stmt|;
block|}
DECL|method|queryCache
specifier|public
name|boolean
name|queryCache
parameter_list|()
block|{
return|return
name|queryCache
return|;
block|}
DECL|method|queryCache
specifier|public
name|ClearIndicesCacheRequest
name|queryCache
parameter_list|(
name|boolean
name|queryCache
parameter_list|)
block|{
name|this
operator|.
name|queryCache
operator|=
name|queryCache
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|requestCache
specifier|public
name|boolean
name|requestCache
parameter_list|()
block|{
return|return
name|this
operator|.
name|requestCache
return|;
block|}
DECL|method|requestCache
specifier|public
name|ClearIndicesCacheRequest
name|requestCache
parameter_list|(
name|boolean
name|requestCache
parameter_list|)
block|{
name|this
operator|.
name|requestCache
operator|=
name|requestCache
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fieldDataCache
specifier|public
name|boolean
name|fieldDataCache
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldDataCache
return|;
block|}
DECL|method|fieldDataCache
specifier|public
name|ClearIndicesCacheRequest
name|fieldDataCache
parameter_list|(
name|boolean
name|fieldDataCache
parameter_list|)
block|{
name|this
operator|.
name|fieldDataCache
operator|=
name|fieldDataCache
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fields
specifier|public
name|ClearIndicesCacheRequest
name|fields
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fields
specifier|public
name|String
index|[]
name|fields
parameter_list|()
block|{
return|return
name|this
operator|.
name|fields
return|;
block|}
DECL|method|recycler
specifier|public
name|ClearIndicesCacheRequest
name|recycler
parameter_list|(
name|boolean
name|recycler
parameter_list|)
block|{
name|this
operator|.
name|recycler
operator|=
name|recycler
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|recycler
specifier|public
name|boolean
name|recycler
parameter_list|()
block|{
return|return
name|this
operator|.
name|recycler
return|;
block|}
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
name|queryCache
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|fieldDataCache
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|recycler
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|fields
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|requestCache
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
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
name|out
operator|.
name|writeBoolean
argument_list|(
name|queryCache
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|fieldDataCache
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|recycler
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArrayNullable
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|requestCache
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

