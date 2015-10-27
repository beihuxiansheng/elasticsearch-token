begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search.type
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|type
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CharsRefBuilder
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
name|SearchScrollRequest
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
name|Base64
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
name|Nullable
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
name|util
operator|.
name|concurrent
operator|.
name|AtomicArray
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
name|internal
operator|.
name|InternalScrollSearchRequest
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
name|internal
operator|.
name|ShardSearchTransportRequest
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportSearchHelper
specifier|public
specifier|abstract
class|class
name|TransportSearchHelper
block|{
DECL|method|internalSearchRequest
specifier|public
specifier|static
name|ShardSearchTransportRequest
name|internalSearchRequest
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|int
name|numberOfShards
parameter_list|,
name|SearchRequest
name|request
parameter_list|,
name|String
index|[]
name|filteringAliases
parameter_list|,
name|long
name|nowInMillis
parameter_list|)
block|{
return|return
operator|new
name|ShardSearchTransportRequest
argument_list|(
name|request
argument_list|,
name|shardRouting
argument_list|,
name|numberOfShards
argument_list|,
name|filteringAliases
argument_list|,
name|nowInMillis
argument_list|)
return|;
block|}
DECL|method|internalScrollSearchRequest
specifier|public
specifier|static
name|InternalScrollSearchRequest
name|internalScrollSearchRequest
parameter_list|(
name|long
name|id
parameter_list|,
name|SearchScrollRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|InternalScrollSearchRequest
argument_list|(
name|request
argument_list|,
name|id
argument_list|)
return|;
block|}
DECL|method|buildScrollId
specifier|public
specifier|static
name|String
name|buildScrollId
parameter_list|(
name|SearchType
name|searchType
parameter_list|,
name|AtomicArray
argument_list|<
name|?
extends|extends
name|SearchPhaseResult
argument_list|>
name|searchPhaseResults
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|searchType
operator|==
name|SearchType
operator|.
name|DFS_QUERY_THEN_FETCH
operator|||
name|searchType
operator|==
name|SearchType
operator|.
name|QUERY_THEN_FETCH
condition|)
block|{
return|return
name|buildScrollId
argument_list|(
name|ParsedScrollId
operator|.
name|QUERY_THEN_FETCH_TYPE
argument_list|,
name|searchPhaseResults
argument_list|,
name|attributes
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|searchType
operator|==
name|SearchType
operator|.
name|QUERY_AND_FETCH
operator|||
name|searchType
operator|==
name|SearchType
operator|.
name|DFS_QUERY_AND_FETCH
condition|)
block|{
return|return
name|buildScrollId
argument_list|(
name|ParsedScrollId
operator|.
name|QUERY_AND_FETCH_TYPE
argument_list|,
name|searchPhaseResults
argument_list|,
name|attributes
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"search_type ["
operator|+
name|searchType
operator|+
literal|"] not supported"
argument_list|)
throw|;
block|}
block|}
DECL|method|buildScrollId
specifier|public
specifier|static
name|String
name|buildScrollId
parameter_list|(
name|String
name|type
parameter_list|,
name|AtomicArray
argument_list|<
name|?
extends|extends
name|SearchPhaseResult
argument_list|>
name|searchPhaseResults
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|searchPhaseResults
operator|.
name|asList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
for|for
control|(
name|AtomicArray
operator|.
name|Entry
argument_list|<
name|?
extends|extends
name|SearchPhaseResult
argument_list|>
name|entry
range|:
name|searchPhaseResults
operator|.
name|asList
argument_list|()
control|)
block|{
name|SearchPhaseResult
name|searchPhaseResult
init|=
name|entry
operator|.
name|value
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|searchPhaseResult
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|searchPhaseResult
operator|.
name|shardTarget
argument_list|()
operator|.
name|nodeId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"0;"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|attributes
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|attributes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
block|}
block|}
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|(
name|sb
argument_list|)
decl_stmt|;
return|return
name|Base64
operator|.
name|encodeBytes
argument_list|(
name|bytesRef
operator|.
name|bytes
argument_list|,
name|bytesRef
operator|.
name|offset
argument_list|,
name|bytesRef
operator|.
name|length
argument_list|,
name|Base64
operator|.
name|URL_SAFE
argument_list|)
return|;
block|}
DECL|method|parseScrollId
specifier|public
specifier|static
name|ParsedScrollId
name|parseScrollId
parameter_list|(
name|String
name|scrollId
parameter_list|)
block|{
name|CharsRefBuilder
name|spare
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|decode
init|=
name|Base64
operator|.
name|decode
argument_list|(
name|scrollId
argument_list|,
name|Base64
operator|.
name|URL_SAFE
argument_list|)
decl_stmt|;
name|spare
operator|.
name|copyUTF8Bytes
argument_list|(
name|decode
argument_list|,
literal|0
argument_list|,
name|decode
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Failed to decode scrollId"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|String
index|[]
name|elements
init|=
name|Strings
operator|.
name|splitStringToArray
argument_list|(
name|spare
operator|.
name|get
argument_list|()
argument_list|,
literal|';'
argument_list|)
decl_stmt|;
if|if
condition|(
name|elements
operator|.
name|length
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Malformed scrollId ["
operator|+
name|scrollId
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|int
name|index
init|=
literal|0
decl_stmt|;
name|String
name|type
init|=
name|elements
index|[
name|index
operator|++
index|]
decl_stmt|;
name|int
name|contextSize
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|elements
index|[
name|index
operator|++
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|elements
operator|.
name|length
operator|<
name|contextSize
operator|+
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Malformed scrollId ["
operator|+
name|scrollId
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|ScrollIdForNode
index|[]
name|context
init|=
operator|new
name|ScrollIdForNode
index|[
name|contextSize
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|contextSize
condition|;
name|i
operator|++
control|)
block|{
name|String
name|element
init|=
name|elements
index|[
name|index
operator|++
index|]
decl_stmt|;
name|int
name|sep
init|=
name|element
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|sep
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Malformed scrollId ["
operator|+
name|scrollId
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|context
index|[
name|i
index|]
operator|=
operator|new
name|ScrollIdForNode
argument_list|(
name|element
operator|.
name|substring
argument_list|(
name|sep
operator|+
literal|1
argument_list|)
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|element
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sep
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
decl_stmt|;
name|int
name|attributesSize
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|elements
index|[
name|index
operator|++
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|attributesSize
operator|==
literal|0
condition|)
block|{
name|attributes
operator|=
name|emptyMap
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|attributes
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|attributesSize
argument_list|)
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
name|attributesSize
condition|;
name|i
operator|++
control|)
block|{
name|String
name|element
init|=
name|elements
index|[
name|index
operator|++
index|]
decl_stmt|;
name|int
name|sep
init|=
name|element
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|attributes
operator|.
name|put
argument_list|(
name|element
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sep
argument_list|)
argument_list|,
name|element
operator|.
name|substring
argument_list|(
name|sep
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ParsedScrollId
argument_list|(
name|scrollId
argument_list|,
name|type
argument_list|,
name|context
argument_list|,
name|attributes
argument_list|)
return|;
block|}
DECL|method|TransportSearchHelper
specifier|private
name|TransportSearchHelper
parameter_list|()
block|{      }
block|}
end_class

end_unit

