begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.stats
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
name|stats
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
name|BroadcastOperationRequest
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

begin_comment
comment|/**  * A request to get indices level stats. Allow to enable different stats to be returned.  *<p/>  *<p>By default, the {@link #docs(boolean)}, {@link #store(boolean)}, {@link #indexing(boolean)}  * are enabled. Other stats can be enabled as well.  *<p/>  *<p>All the stats to be returned can be cleared using {@link #clear()}, at which point, specific  * stats can be enabled.  */
end_comment

begin_class
DECL|class|IndicesStatsRequest
specifier|public
class|class
name|IndicesStatsRequest
extends|extends
name|BroadcastOperationRequest
argument_list|<
name|IndicesStatsRequest
argument_list|>
block|{
DECL|field|docs
specifier|private
name|boolean
name|docs
init|=
literal|true
decl_stmt|;
DECL|field|store
specifier|private
name|boolean
name|store
init|=
literal|true
decl_stmt|;
DECL|field|indexing
specifier|private
name|boolean
name|indexing
init|=
literal|true
decl_stmt|;
DECL|field|get
specifier|private
name|boolean
name|get
init|=
literal|true
decl_stmt|;
DECL|field|search
specifier|private
name|boolean
name|search
init|=
literal|true
decl_stmt|;
DECL|field|merge
specifier|private
name|boolean
name|merge
init|=
literal|false
decl_stmt|;
DECL|field|refresh
specifier|private
name|boolean
name|refresh
init|=
literal|false
decl_stmt|;
DECL|field|flush
specifier|private
name|boolean
name|flush
init|=
literal|false
decl_stmt|;
DECL|field|warmer
specifier|private
name|boolean
name|warmer
init|=
literal|false
decl_stmt|;
DECL|field|filterCache
specifier|private
name|boolean
name|filterCache
init|=
literal|false
decl_stmt|;
DECL|field|idCache
specifier|private
name|boolean
name|idCache
init|=
literal|false
decl_stmt|;
DECL|field|types
specifier|private
name|String
index|[]
name|types
init|=
literal|null
decl_stmt|;
DECL|field|groups
specifier|private
name|String
index|[]
name|groups
init|=
literal|null
decl_stmt|;
comment|/**      * Sets all flags to return all stats.      */
DECL|method|all
specifier|public
name|IndicesStatsRequest
name|all
parameter_list|()
block|{
name|docs
operator|=
literal|true
expr_stmt|;
name|store
operator|=
literal|true
expr_stmt|;
name|get
operator|=
literal|true
expr_stmt|;
name|indexing
operator|=
literal|true
expr_stmt|;
name|search
operator|=
literal|true
expr_stmt|;
name|merge
operator|=
literal|true
expr_stmt|;
name|refresh
operator|=
literal|true
expr_stmt|;
name|flush
operator|=
literal|true
expr_stmt|;
name|warmer
operator|=
literal|true
expr_stmt|;
name|filterCache
operator|=
literal|true
expr_stmt|;
name|idCache
operator|=
literal|true
expr_stmt|;
name|types
operator|=
literal|null
expr_stmt|;
name|groups
operator|=
literal|null
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Clears all stats.      */
DECL|method|clear
specifier|public
name|IndicesStatsRequest
name|clear
parameter_list|()
block|{
name|docs
operator|=
literal|false
expr_stmt|;
name|store
operator|=
literal|false
expr_stmt|;
name|get
operator|=
literal|false
expr_stmt|;
name|indexing
operator|=
literal|false
expr_stmt|;
name|search
operator|=
literal|false
expr_stmt|;
name|merge
operator|=
literal|false
expr_stmt|;
name|refresh
operator|=
literal|false
expr_stmt|;
name|flush
operator|=
literal|false
expr_stmt|;
name|warmer
operator|=
literal|false
expr_stmt|;
name|filterCache
operator|=
literal|false
expr_stmt|;
name|idCache
operator|=
literal|false
expr_stmt|;
name|types
operator|=
literal|null
expr_stmt|;
name|groups
operator|=
literal|null
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Document types to return stats for. Mainly affects {@link #indexing(boolean)} when      * enabled, returning specific indexing stats for those types.      */
DECL|method|types
specifier|public
name|IndicesStatsRequest
name|types
parameter_list|(
name|String
modifier|...
name|types
parameter_list|)
block|{
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Document types to return stats for. Mainly affects {@link #indexing(boolean)} when      * enabled, returning specific indexing stats for those types.      */
DECL|method|types
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
name|this
operator|.
name|types
return|;
block|}
comment|/**      * Sets specific search group stats to retrieve the stats for. Mainly affects search      * when enabled.      */
DECL|method|groups
specifier|public
name|IndicesStatsRequest
name|groups
parameter_list|(
name|String
modifier|...
name|groups
parameter_list|)
block|{
name|this
operator|.
name|groups
operator|=
name|groups
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|groups
specifier|public
name|String
index|[]
name|groups
parameter_list|()
block|{
return|return
name|this
operator|.
name|groups
return|;
block|}
DECL|method|docs
specifier|public
name|IndicesStatsRequest
name|docs
parameter_list|(
name|boolean
name|docs
parameter_list|)
block|{
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|docs
specifier|public
name|boolean
name|docs
parameter_list|()
block|{
return|return
name|this
operator|.
name|docs
return|;
block|}
DECL|method|store
specifier|public
name|IndicesStatsRequest
name|store
parameter_list|(
name|boolean
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|store
specifier|public
name|boolean
name|store
parameter_list|()
block|{
return|return
name|this
operator|.
name|store
return|;
block|}
DECL|method|indexing
specifier|public
name|IndicesStatsRequest
name|indexing
parameter_list|(
name|boolean
name|indexing
parameter_list|)
block|{
name|this
operator|.
name|indexing
operator|=
name|indexing
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|indexing
specifier|public
name|boolean
name|indexing
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexing
return|;
block|}
DECL|method|get
specifier|public
name|IndicesStatsRequest
name|get
parameter_list|(
name|boolean
name|get
parameter_list|)
block|{
name|this
operator|.
name|get
operator|=
name|get
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|()
block|{
return|return
name|this
operator|.
name|get
return|;
block|}
DECL|method|search
specifier|public
name|IndicesStatsRequest
name|search
parameter_list|(
name|boolean
name|search
parameter_list|)
block|{
name|this
operator|.
name|search
operator|=
name|search
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|search
specifier|public
name|boolean
name|search
parameter_list|()
block|{
return|return
name|this
operator|.
name|search
return|;
block|}
DECL|method|merge
specifier|public
name|IndicesStatsRequest
name|merge
parameter_list|(
name|boolean
name|merge
parameter_list|)
block|{
name|this
operator|.
name|merge
operator|=
name|merge
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|merge
specifier|public
name|boolean
name|merge
parameter_list|()
block|{
return|return
name|this
operator|.
name|merge
return|;
block|}
DECL|method|refresh
specifier|public
name|IndicesStatsRequest
name|refresh
parameter_list|(
name|boolean
name|refresh
parameter_list|)
block|{
name|this
operator|.
name|refresh
operator|=
name|refresh
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|refresh
specifier|public
name|boolean
name|refresh
parameter_list|()
block|{
return|return
name|this
operator|.
name|refresh
return|;
block|}
DECL|method|flush
specifier|public
name|IndicesStatsRequest
name|flush
parameter_list|(
name|boolean
name|flush
parameter_list|)
block|{
name|this
operator|.
name|flush
operator|=
name|flush
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|flush
specifier|public
name|boolean
name|flush
parameter_list|()
block|{
return|return
name|this
operator|.
name|flush
return|;
block|}
DECL|method|warmer
specifier|public
name|IndicesStatsRequest
name|warmer
parameter_list|(
name|boolean
name|warmer
parameter_list|)
block|{
name|this
operator|.
name|warmer
operator|=
name|warmer
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|warmer
specifier|public
name|boolean
name|warmer
parameter_list|()
block|{
return|return
name|this
operator|.
name|warmer
return|;
block|}
DECL|method|filterCache
specifier|public
name|IndicesStatsRequest
name|filterCache
parameter_list|(
name|boolean
name|filterCache
parameter_list|)
block|{
name|this
operator|.
name|filterCache
operator|=
name|filterCache
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|filterCache
specifier|public
name|boolean
name|filterCache
parameter_list|()
block|{
return|return
name|this
operator|.
name|filterCache
return|;
block|}
DECL|method|idCache
specifier|public
name|IndicesStatsRequest
name|idCache
parameter_list|(
name|boolean
name|idCache
parameter_list|)
block|{
name|this
operator|.
name|idCache
operator|=
name|idCache
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|idCache
specifier|public
name|boolean
name|idCache
parameter_list|()
block|{
return|return
name|this
operator|.
name|idCache
return|;
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
name|docs
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|indexing
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|get
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|search
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|merge
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|flush
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|refresh
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|warmer
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|filterCache
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|idCache
argument_list|)
expr_stmt|;
if|if
condition|(
name|types
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|types
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|type
range|:
name|types
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|groups
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|groups
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|group
range|:
name|groups
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
block|}
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
name|docs
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|store
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|indexing
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|get
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|search
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|merge
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|flush
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|refresh
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|warmer
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|filterCache
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|idCache
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|types
operator|=
operator|new
name|String
index|[
name|size
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|types
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
block|}
name|size
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|groups
operator|=
operator|new
name|String
index|[
name|size
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|groups
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

