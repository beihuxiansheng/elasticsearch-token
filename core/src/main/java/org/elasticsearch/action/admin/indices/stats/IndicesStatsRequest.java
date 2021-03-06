begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|admin
operator|.
name|indices
operator|.
name|stats
operator|.
name|CommonStatsFlags
operator|.
name|Flag
import|;
end_import

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

begin_comment
comment|/**  * A request to get indices level stats. Allow to enable different stats to be returned.  *<p>  * By default, all statistics are enabled.  *<p>  * All the stats to be returned can be cleared using {@link #clear()}, at which point, specific  * stats can be enabled.  */
end_comment

begin_class
DECL|class|IndicesStatsRequest
specifier|public
class|class
name|IndicesStatsRequest
extends|extends
name|BroadcastRequest
argument_list|<
name|IndicesStatsRequest
argument_list|>
block|{
DECL|field|flags
specifier|private
name|CommonStatsFlags
name|flags
init|=
operator|new
name|CommonStatsFlags
argument_list|()
decl_stmt|;
comment|/**      * Sets all flags to return all stats.      */
DECL|method|all
specifier|public
name|IndicesStatsRequest
name|all
parameter_list|()
block|{
name|flags
operator|.
name|all
argument_list|()
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
name|flags
operator|.
name|clear
argument_list|()
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
name|flags
operator|.
name|types
argument_list|(
name|types
argument_list|)
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
name|flags
operator|.
name|types
argument_list|()
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
name|flags
operator|.
name|groups
argument_list|(
name|groups
argument_list|)
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
name|flags
operator|.
name|groups
argument_list|()
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
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Docs
argument_list|,
name|docs
argument_list|)
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
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Docs
argument_list|)
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
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Store
argument_list|,
name|store
argument_list|)
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
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Store
argument_list|)
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
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Indexing
argument_list|,
name|indexing
argument_list|)
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
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Indexing
argument_list|)
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
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Get
argument_list|,
name|get
argument_list|)
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
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Get
argument_list|)
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
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Search
argument_list|,
name|search
argument_list|)
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
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Search
argument_list|)
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
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Merge
argument_list|,
name|merge
argument_list|)
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
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Merge
argument_list|)
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
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Refresh
argument_list|,
name|refresh
argument_list|)
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
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Refresh
argument_list|)
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
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Flush
argument_list|,
name|flush
argument_list|)
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
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Flush
argument_list|)
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
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Warmer
argument_list|,
name|warmer
argument_list|)
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
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Warmer
argument_list|)
return|;
block|}
DECL|method|queryCache
specifier|public
name|IndicesStatsRequest
name|queryCache
parameter_list|(
name|boolean
name|queryCache
parameter_list|)
block|{
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|QueryCache
argument_list|,
name|queryCache
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|queryCache
specifier|public
name|boolean
name|queryCache
parameter_list|()
block|{
return|return
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|QueryCache
argument_list|)
return|;
block|}
DECL|method|fieldData
specifier|public
name|IndicesStatsRequest
name|fieldData
parameter_list|(
name|boolean
name|fieldData
parameter_list|)
block|{
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|FieldData
argument_list|,
name|fieldData
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fieldData
specifier|public
name|boolean
name|fieldData
parameter_list|()
block|{
return|return
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|FieldData
argument_list|)
return|;
block|}
DECL|method|segments
specifier|public
name|IndicesStatsRequest
name|segments
parameter_list|(
name|boolean
name|segments
parameter_list|)
block|{
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Segments
argument_list|,
name|segments
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|segments
specifier|public
name|boolean
name|segments
parameter_list|()
block|{
return|return
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Segments
argument_list|)
return|;
block|}
DECL|method|fieldDataFields
specifier|public
name|IndicesStatsRequest
name|fieldDataFields
parameter_list|(
name|String
modifier|...
name|fieldDataFields
parameter_list|)
block|{
name|flags
operator|.
name|fieldDataFields
argument_list|(
name|fieldDataFields
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fieldDataFields
specifier|public
name|String
index|[]
name|fieldDataFields
parameter_list|()
block|{
return|return
name|flags
operator|.
name|fieldDataFields
argument_list|()
return|;
block|}
DECL|method|completion
specifier|public
name|IndicesStatsRequest
name|completion
parameter_list|(
name|boolean
name|completion
parameter_list|)
block|{
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Completion
argument_list|,
name|completion
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|completion
specifier|public
name|boolean
name|completion
parameter_list|()
block|{
return|return
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Completion
argument_list|)
return|;
block|}
DECL|method|completionFields
specifier|public
name|IndicesStatsRequest
name|completionFields
parameter_list|(
name|String
modifier|...
name|completionDataFields
parameter_list|)
block|{
name|flags
operator|.
name|completionDataFields
argument_list|(
name|completionDataFields
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|completionFields
specifier|public
name|String
index|[]
name|completionFields
parameter_list|()
block|{
return|return
name|flags
operator|.
name|completionDataFields
argument_list|()
return|;
block|}
DECL|method|translog
specifier|public
name|IndicesStatsRequest
name|translog
parameter_list|(
name|boolean
name|translog
parameter_list|)
block|{
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Translog
argument_list|,
name|translog
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|translog
specifier|public
name|boolean
name|translog
parameter_list|()
block|{
return|return
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Translog
argument_list|)
return|;
block|}
DECL|method|suggest
specifier|public
name|IndicesStatsRequest
name|suggest
parameter_list|(
name|boolean
name|suggest
parameter_list|)
block|{
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Suggest
argument_list|,
name|suggest
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|suggest
specifier|public
name|boolean
name|suggest
parameter_list|()
block|{
return|return
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Suggest
argument_list|)
return|;
block|}
DECL|method|requestCache
specifier|public
name|IndicesStatsRequest
name|requestCache
parameter_list|(
name|boolean
name|requestCache
parameter_list|)
block|{
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|RequestCache
argument_list|,
name|requestCache
argument_list|)
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
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|RequestCache
argument_list|)
return|;
block|}
DECL|method|recovery
specifier|public
name|IndicesStatsRequest
name|recovery
parameter_list|(
name|boolean
name|recovery
parameter_list|)
block|{
name|flags
operator|.
name|set
argument_list|(
name|Flag
operator|.
name|Recovery
argument_list|,
name|recovery
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|recovery
specifier|public
name|boolean
name|recovery
parameter_list|()
block|{
return|return
name|flags
operator|.
name|isSet
argument_list|(
name|Flag
operator|.
name|Recovery
argument_list|)
return|;
block|}
DECL|method|includeSegmentFileSizes
specifier|public
name|boolean
name|includeSegmentFileSizes
parameter_list|()
block|{
return|return
name|flags
operator|.
name|includeSegmentFileSizes
argument_list|()
return|;
block|}
DECL|method|includeSegmentFileSizes
specifier|public
name|IndicesStatsRequest
name|includeSegmentFileSizes
parameter_list|(
name|boolean
name|includeSegmentFileSizes
parameter_list|)
block|{
name|flags
operator|.
name|includeSegmentFileSizes
argument_list|(
name|includeSegmentFileSizes
argument_list|)
expr_stmt|;
return|return
name|this
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
name|flags
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
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
name|flags
operator|=
operator|new
name|CommonStatsFlags
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

