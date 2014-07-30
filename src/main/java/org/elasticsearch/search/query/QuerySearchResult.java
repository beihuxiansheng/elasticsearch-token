begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|query
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TopDocs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|aggregations
operator|.
name|Aggregations
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
name|aggregations
operator|.
name|InternalAggregations
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
name|facet
operator|.
name|Facets
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
name|facet
operator|.
name|InternalFacets
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
name|suggest
operator|.
name|Suggest
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
name|TransportResponse
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
name|common
operator|.
name|lucene
operator|.
name|Lucene
operator|.
name|readTopDocs
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|Lucene
operator|.
name|writeTopDocs
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|QuerySearchResult
specifier|public
class|class
name|QuerySearchResult
extends|extends
name|TransportResponse
implements|implements
name|QuerySearchResultProvider
block|{
DECL|field|id
specifier|private
name|long
name|id
decl_stmt|;
DECL|field|shardTarget
specifier|private
name|SearchShardTarget
name|shardTarget
decl_stmt|;
DECL|field|from
specifier|private
name|int
name|from
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|topDocs
specifier|private
name|TopDocs
name|topDocs
decl_stmt|;
DECL|field|facets
specifier|private
name|InternalFacets
name|facets
decl_stmt|;
DECL|field|aggregations
specifier|private
name|InternalAggregations
name|aggregations
decl_stmt|;
DECL|field|suggest
specifier|private
name|Suggest
name|suggest
decl_stmt|;
DECL|field|searchTimedOut
specifier|private
name|boolean
name|searchTimedOut
decl_stmt|;
DECL|field|terminatedEarly
specifier|private
name|Boolean
name|terminatedEarly
init|=
literal|null
decl_stmt|;
DECL|method|QuerySearchResult
specifier|public
name|QuerySearchResult
parameter_list|()
block|{      }
DECL|method|QuerySearchResult
specifier|public
name|QuerySearchResult
parameter_list|(
name|long
name|id
parameter_list|,
name|SearchShardTarget
name|shardTarget
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|shardTarget
operator|=
name|shardTarget
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|includeFetch
specifier|public
name|boolean
name|includeFetch
parameter_list|()
block|{
return|return
literal|false
return|;
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
name|this
return|;
block|}
DECL|method|id
specifier|public
name|long
name|id
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|shardTarget
specifier|public
name|SearchShardTarget
name|shardTarget
parameter_list|()
block|{
return|return
name|shardTarget
return|;
block|}
annotation|@
name|Override
DECL|method|shardTarget
specifier|public
name|void
name|shardTarget
parameter_list|(
name|SearchShardTarget
name|shardTarget
parameter_list|)
block|{
name|this
operator|.
name|shardTarget
operator|=
name|shardTarget
expr_stmt|;
block|}
DECL|method|searchTimedOut
specifier|public
name|void
name|searchTimedOut
parameter_list|(
name|boolean
name|searchTimedOut
parameter_list|)
block|{
name|this
operator|.
name|searchTimedOut
operator|=
name|searchTimedOut
expr_stmt|;
block|}
DECL|method|searchTimedOut
specifier|public
name|boolean
name|searchTimedOut
parameter_list|()
block|{
return|return
name|searchTimedOut
return|;
block|}
DECL|method|terminatedEarly
specifier|public
name|void
name|terminatedEarly
parameter_list|(
name|boolean
name|terminatedEarly
parameter_list|)
block|{
name|this
operator|.
name|terminatedEarly
operator|=
name|terminatedEarly
expr_stmt|;
block|}
DECL|method|terminatedEarly
specifier|public
name|Boolean
name|terminatedEarly
parameter_list|()
block|{
return|return
name|this
operator|.
name|terminatedEarly
return|;
block|}
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|()
block|{
return|return
name|topDocs
return|;
block|}
DECL|method|topDocs
specifier|public
name|void
name|topDocs
parameter_list|(
name|TopDocs
name|topDocs
parameter_list|)
block|{
name|this
operator|.
name|topDocs
operator|=
name|topDocs
expr_stmt|;
block|}
DECL|method|facets
specifier|public
name|Facets
name|facets
parameter_list|()
block|{
return|return
name|facets
return|;
block|}
DECL|method|facets
specifier|public
name|void
name|facets
parameter_list|(
name|InternalFacets
name|facets
parameter_list|)
block|{
name|this
operator|.
name|facets
operator|=
name|facets
expr_stmt|;
block|}
DECL|method|aggregations
specifier|public
name|Aggregations
name|aggregations
parameter_list|()
block|{
return|return
name|aggregations
return|;
block|}
DECL|method|aggregations
specifier|public
name|void
name|aggregations
parameter_list|(
name|InternalAggregations
name|aggregations
parameter_list|)
block|{
name|this
operator|.
name|aggregations
operator|=
name|aggregations
expr_stmt|;
block|}
DECL|method|suggest
specifier|public
name|Suggest
name|suggest
parameter_list|()
block|{
return|return
name|suggest
return|;
block|}
DECL|method|suggest
specifier|public
name|void
name|suggest
parameter_list|(
name|Suggest
name|suggest
parameter_list|)
block|{
name|this
operator|.
name|suggest
operator|=
name|suggest
expr_stmt|;
block|}
DECL|method|from
specifier|public
name|int
name|from
parameter_list|()
block|{
return|return
name|from
return|;
block|}
DECL|method|from
specifier|public
name|QuerySearchResult
name|from
parameter_list|(
name|int
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|size
specifier|public
name|QuerySearchResult
name|size
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|readQuerySearchResult
specifier|public
specifier|static
name|QuerySearchResult
name|readQuerySearchResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|QuerySearchResult
name|result
init|=
operator|new
name|QuerySearchResult
argument_list|()
decl_stmt|;
name|result
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|result
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
name|id
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
comment|//        shardTarget = readSearchShardTarget(in);
name|from
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|size
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|topDocs
operator|=
name|readTopDocs
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|facets
operator|=
name|InternalFacets
operator|.
name|readFacets
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|aggregations
operator|=
name|InternalAggregations
operator|.
name|readAggregations
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|suggest
operator|=
name|Suggest
operator|.
name|readSuggest
argument_list|(
name|Suggest
operator|.
name|Fields
operator|.
name|SUGGEST
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
name|searchTimedOut
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_4_0
argument_list|)
condition|)
block|{
name|terminatedEarly
operator|=
name|in
operator|.
name|readOptionalBoolean
argument_list|()
expr_stmt|;
block|}
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
name|writeLong
argument_list|(
name|id
argument_list|)
expr_stmt|;
comment|//        shardTarget.writeTo(out);
name|out
operator|.
name|writeVInt
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|writeTopDocs
argument_list|(
name|out
argument_list|,
name|topDocs
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|facets
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|facets
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|aggregations
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|aggregations
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|suggest
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|suggest
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|searchTimedOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_4_0
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeOptionalBoolean
argument_list|(
name|terminatedEarly
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

