begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
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
name|search
operator|.
name|SearchRequest
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
name|search
operator|.
name|SearchType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|ShardRouting
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
name|ContextAndHeaderHolder
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
name|Strings
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
name|bytes
operator|.
name|BytesReference
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
name|BytesStreamOutput
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
name|index
operator|.
name|shard
operator|.
name|ShardId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Template
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
name|Scroll
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
name|builder
operator|.
name|SearchSourceBuilder
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
name|Scroll
operator|.
name|readScroll
import|;
end_import

begin_comment
comment|/**  * Shard level search request that gets created and consumed on the local node.  * Used by warmers and by api that need to create a search context within their execution.  *  * Source structure:  *<pre>  * {  *  from : 0, size : 20, (optional, can be set on the request)  *  sort : { "name.first" : {}, "name.last" : { reverse : true } }  *  fields : [ "name.first", "name.last" ]  *  query : { ... }  *  aggs : {  *      "agg1" : {  *          terms : { ... }  *      }  *  }  * }  *</pre>  */
end_comment

begin_class
DECL|class|ShardSearchLocalRequest
specifier|public
class|class
name|ShardSearchLocalRequest
extends|extends
name|ContextAndHeaderHolder
implements|implements
name|ShardSearchRequest
block|{
DECL|field|index
specifier|private
name|String
name|index
decl_stmt|;
DECL|field|shardId
specifier|private
name|int
name|shardId
decl_stmt|;
DECL|field|numberOfShards
specifier|private
name|int
name|numberOfShards
decl_stmt|;
DECL|field|searchType
specifier|private
name|SearchType
name|searchType
decl_stmt|;
DECL|field|scroll
specifier|private
name|Scroll
name|scroll
decl_stmt|;
DECL|field|types
specifier|private
name|String
index|[]
name|types
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|filteringAliases
specifier|private
name|String
index|[]
name|filteringAliases
decl_stmt|;
DECL|field|source
specifier|private
name|SearchSourceBuilder
name|source
decl_stmt|;
DECL|field|template
specifier|private
name|Template
name|template
decl_stmt|;
DECL|field|requestCache
specifier|private
name|Boolean
name|requestCache
decl_stmt|;
DECL|field|nowInMillis
specifier|private
name|long
name|nowInMillis
decl_stmt|;
DECL|method|ShardSearchLocalRequest
name|ShardSearchLocalRequest
parameter_list|()
block|{     }
DECL|method|ShardSearchLocalRequest
name|ShardSearchLocalRequest
parameter_list|(
name|SearchRequest
name|searchRequest
parameter_list|,
name|ShardRouting
name|shardRouting
parameter_list|,
name|int
name|numberOfShards
parameter_list|,
name|String
index|[]
name|filteringAliases
parameter_list|,
name|long
name|nowInMillis
parameter_list|)
block|{
name|this
argument_list|(
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|,
name|numberOfShards
argument_list|,
name|searchRequest
operator|.
name|searchType
argument_list|()
argument_list|,
name|searchRequest
operator|.
name|source
argument_list|()
argument_list|,
name|searchRequest
operator|.
name|types
argument_list|()
argument_list|,
name|searchRequest
operator|.
name|requestCache
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|template
operator|=
name|searchRequest
operator|.
name|template
argument_list|()
expr_stmt|;
name|this
operator|.
name|scroll
operator|=
name|searchRequest
operator|.
name|scroll
argument_list|()
expr_stmt|;
name|this
operator|.
name|filteringAliases
operator|=
name|filteringAliases
expr_stmt|;
name|this
operator|.
name|nowInMillis
operator|=
name|nowInMillis
expr_stmt|;
name|copyContextAndHeadersFrom
argument_list|(
name|searchRequest
argument_list|)
expr_stmt|;
block|}
DECL|method|ShardSearchLocalRequest
specifier|public
name|ShardSearchLocalRequest
parameter_list|(
name|String
index|[]
name|types
parameter_list|,
name|long
name|nowInMillis
parameter_list|)
block|{
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
name|this
operator|.
name|nowInMillis
operator|=
name|nowInMillis
expr_stmt|;
block|}
DECL|method|ShardSearchLocalRequest
specifier|public
name|ShardSearchLocalRequest
parameter_list|(
name|String
index|[]
name|types
parameter_list|,
name|long
name|nowInMillis
parameter_list|,
name|String
index|[]
name|filteringAliases
parameter_list|)
block|{
name|this
argument_list|(
name|types
argument_list|,
name|nowInMillis
argument_list|)
expr_stmt|;
name|this
operator|.
name|filteringAliases
operator|=
name|filteringAliases
expr_stmt|;
block|}
DECL|method|ShardSearchLocalRequest
specifier|public
name|ShardSearchLocalRequest
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|int
name|numberOfShards
parameter_list|,
name|SearchType
name|searchType
parameter_list|,
name|SearchSourceBuilder
name|source
parameter_list|,
name|String
index|[]
name|types
parameter_list|,
name|Boolean
name|requestCache
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|shardId
operator|.
name|getIndex
argument_list|()
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
operator|.
name|id
argument_list|()
expr_stmt|;
name|this
operator|.
name|numberOfShards
operator|=
name|numberOfShards
expr_stmt|;
name|this
operator|.
name|searchType
operator|=
name|searchType
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
name|this
operator|.
name|requestCache
operator|=
name|requestCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
return|return
name|index
return|;
block|}
annotation|@
name|Override
DECL|method|shardId
specifier|public
name|int
name|shardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
annotation|@
name|Override
DECL|method|types
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
name|types
return|;
block|}
annotation|@
name|Override
DECL|method|source
specifier|public
name|SearchSourceBuilder
name|source
parameter_list|()
block|{
return|return
name|source
return|;
block|}
annotation|@
name|Override
DECL|method|source
specifier|public
name|void
name|source
parameter_list|(
name|SearchSourceBuilder
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|numberOfShards
specifier|public
name|int
name|numberOfShards
parameter_list|()
block|{
return|return
name|numberOfShards
return|;
block|}
annotation|@
name|Override
DECL|method|searchType
specifier|public
name|SearchType
name|searchType
parameter_list|()
block|{
return|return
name|searchType
return|;
block|}
annotation|@
name|Override
DECL|method|filteringAliases
specifier|public
name|String
index|[]
name|filteringAliases
parameter_list|()
block|{
return|return
name|filteringAliases
return|;
block|}
annotation|@
name|Override
DECL|method|nowInMillis
specifier|public
name|long
name|nowInMillis
parameter_list|()
block|{
return|return
name|nowInMillis
return|;
block|}
annotation|@
name|Override
DECL|method|template
specifier|public
name|Template
name|template
parameter_list|()
block|{
return|return
name|template
return|;
block|}
annotation|@
name|Override
DECL|method|requestCache
specifier|public
name|Boolean
name|requestCache
parameter_list|()
block|{
return|return
name|requestCache
return|;
block|}
annotation|@
name|Override
DECL|method|scroll
specifier|public
name|Scroll
name|scroll
parameter_list|()
block|{
return|return
name|scroll
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|innerReadFrom
specifier|protected
name|void
name|innerReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|index
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|shardId
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|searchType
operator|=
name|SearchType
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|numberOfShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|scroll
operator|=
name|readScroll
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
name|source
operator|=
name|SearchSourceBuilder
operator|.
name|readSearchSourceFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|types
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|filteringAliases
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|nowInMillis
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|template
operator|=
name|in
operator|.
name|readOptionalStreamable
argument_list|(
operator|new
name|Template
argument_list|()
argument_list|)
expr_stmt|;
name|requestCache
operator|=
name|in
operator|.
name|readOptionalBoolean
argument_list|()
expr_stmt|;
block|}
DECL|method|innerWriteTo
specifier|protected
name|void
name|innerWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|boolean
name|asKey
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|searchType
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|asKey
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|numberOfShards
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scroll
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
name|scroll
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|source
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
name|source
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeStringArray
argument_list|(
name|types
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArrayNullable
argument_list|(
name|filteringAliases
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|asKey
condition|)
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|nowInMillis
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeOptionalStreamable
argument_list|(
name|template
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalBoolean
argument_list|(
name|requestCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cacheKey
specifier|public
name|BytesReference
name|cacheKey
parameter_list|()
throws|throws
name|IOException
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|this
operator|.
name|innerWriteTo
argument_list|(
name|out
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// copy it over, most requests are small, we might as well copy to make sure we are not sliced...
comment|// we could potentially keep it without copying, but then pay the price of extra unused bytes up to a page
return|return
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|copyBytesArray
argument_list|()
return|;
block|}
block|}
end_class

end_unit

