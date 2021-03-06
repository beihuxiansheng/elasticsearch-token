begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.fetch
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
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
name|search
operator|.
name|SearchPhaseResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchShardTarget
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|query
operator|.
name|QuerySearchResult
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|QueryFetchSearchResult
operator|.
name|readQueryFetchSearchResult
import|;
end_import

begin_class
DECL|class|ScrollQueryFetchSearchResult
specifier|public
specifier|final
class|class
name|ScrollQueryFetchSearchResult
extends|extends
name|SearchPhaseResult
block|{
DECL|field|result
specifier|private
name|QueryFetchSearchResult
name|result
decl_stmt|;
DECL|method|ScrollQueryFetchSearchResult
specifier|public
name|ScrollQueryFetchSearchResult
parameter_list|()
block|{     }
DECL|method|ScrollQueryFetchSearchResult
specifier|public
name|ScrollQueryFetchSearchResult
parameter_list|(
name|QueryFetchSearchResult
name|result
parameter_list|,
name|SearchShardTarget
name|shardTarget
parameter_list|)
block|{
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
name|setSearchShardTarget
argument_list|(
name|shardTarget
argument_list|)
expr_stmt|;
block|}
DECL|method|result
specifier|public
name|QueryFetchSearchResult
name|result
parameter_list|()
block|{
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|setSearchShardTarget
specifier|public
name|void
name|setSearchShardTarget
parameter_list|(
name|SearchShardTarget
name|shardTarget
parameter_list|)
block|{
name|super
operator|.
name|setSearchShardTarget
argument_list|(
name|shardTarget
argument_list|)
expr_stmt|;
name|result
operator|.
name|setSearchShardTarget
argument_list|(
name|shardTarget
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setShardIndex
specifier|public
name|void
name|setShardIndex
parameter_list|(
name|int
name|shardIndex
parameter_list|)
block|{
name|super
operator|.
name|setShardIndex
argument_list|(
name|shardIndex
argument_list|)
expr_stmt|;
name|result
operator|.
name|setShardIndex
argument_list|(
name|shardIndex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|queryResult
specifier|public
name|QuerySearchResult
name|queryResult
parameter_list|()
block|{
return|return
name|result
operator|.
name|queryResult
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fetchResult
specifier|public
name|FetchSearchResult
name|fetchResult
parameter_list|()
block|{
return|return
name|result
operator|.
name|fetchResult
argument_list|()
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
name|SearchShardTarget
name|searchShardTarget
init|=
operator|new
name|SearchShardTarget
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|result
operator|=
name|readQueryFetchSearchResult
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|setSearchShardTarget
argument_list|(
name|searchShardTarget
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
name|getSearchShardTarget
argument_list|()
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|result
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

