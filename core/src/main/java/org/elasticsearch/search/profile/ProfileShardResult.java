begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.profile
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|profile
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|Writeable
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
name|xcontent
operator|.
name|ToXContent
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
name|xcontent
operator|.
name|XContentBuilder
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
name|profile
operator|.
name|query
operator|.
name|CollectorResult
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
comment|/**  * A container class to hold the profile results for a single shard in the request.  * Contains a list of query profiles, a collector tree and a total rewrite tree.  */
end_comment

begin_class
DECL|class|ProfileShardResult
specifier|public
specifier|final
class|class
name|ProfileShardResult
implements|implements
name|Writeable
implements|,
name|ToXContent
block|{
DECL|field|queryProfileResults
specifier|private
specifier|final
name|List
argument_list|<
name|ProfileResult
argument_list|>
name|queryProfileResults
decl_stmt|;
DECL|field|profileCollector
specifier|private
specifier|final
name|CollectorResult
name|profileCollector
decl_stmt|;
DECL|field|rewriteTime
specifier|private
specifier|final
name|long
name|rewriteTime
decl_stmt|;
DECL|method|ProfileShardResult
specifier|public
name|ProfileShardResult
parameter_list|(
name|List
argument_list|<
name|ProfileResult
argument_list|>
name|queryProfileResults
parameter_list|,
name|long
name|rewriteTime
parameter_list|,
name|CollectorResult
name|profileCollector
parameter_list|)
block|{
assert|assert
operator|(
name|profileCollector
operator|!=
literal|null
operator|)
assert|;
name|this
operator|.
name|queryProfileResults
operator|=
name|queryProfileResults
expr_stmt|;
name|this
operator|.
name|profileCollector
operator|=
name|profileCollector
expr_stmt|;
name|this
operator|.
name|rewriteTime
operator|=
name|rewriteTime
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|ProfileShardResult
specifier|public
name|ProfileShardResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|profileSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|queryProfileResults
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|profileSize
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|profileSize
condition|;
name|j
operator|++
control|)
block|{
name|queryProfileResults
operator|.
name|add
argument_list|(
operator|new
name|ProfileResult
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|profileCollector
operator|=
operator|new
name|CollectorResult
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|rewriteTime
operator|=
name|in
operator|.
name|readLong
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
name|out
operator|.
name|writeVInt
argument_list|(
name|queryProfileResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ProfileResult
name|p
range|:
name|queryProfileResults
control|)
block|{
name|p
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|profileCollector
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|rewriteTime
argument_list|)
expr_stmt|;
block|}
DECL|method|getQueryResults
specifier|public
name|List
argument_list|<
name|ProfileResult
argument_list|>
name|getQueryResults
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|queryProfileResults
argument_list|)
return|;
block|}
DECL|method|getRewriteTime
specifier|public
name|long
name|getRewriteTime
parameter_list|()
block|{
return|return
name|rewriteTime
return|;
block|}
DECL|method|getCollectorResult
specifier|public
name|CollectorResult
name|getCollectorResult
parameter_list|()
block|{
return|return
name|profileCollector
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startArray
argument_list|(
literal|"query"
argument_list|)
expr_stmt|;
for|for
control|(
name|ProfileResult
name|p
range|:
name|queryProfileResults
control|)
block|{
name|p
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"rewrite_time"
argument_list|,
name|rewriteTime
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
literal|"collector"
argument_list|)
expr_stmt|;
name|profileCollector
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

