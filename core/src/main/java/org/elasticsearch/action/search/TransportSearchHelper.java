begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
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
name|store
operator|.
name|ByteArrayDataInput
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
name|store
operator|.
name|RAMOutputStream
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
name|transport
operator|.
name|RemoteClusterAware
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
name|Base64
import|;
end_import

begin_class
DECL|class|TransportSearchHelper
specifier|final
class|class
name|TransportSearchHelper
block|{
DECL|method|internalScrollSearchRequest
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
specifier|static
name|String
name|buildScrollId
parameter_list|(
name|AtomicArray
argument_list|<
name|?
extends|extends
name|SearchPhaseResult
argument_list|>
name|searchPhaseResults
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|RAMOutputStream
name|out
init|=
operator|new
name|RAMOutputStream
argument_list|()
init|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|searchPhaseResults
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|?
name|ParsedScrollId
operator|.
name|QUERY_AND_FETCH_TYPE
else|:
name|ParsedScrollId
operator|.
name|QUERY_THEN_FETCH_TYPE
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|searchPhaseResults
operator|.
name|asList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchPhaseResult
name|searchPhaseResult
range|:
name|searchPhaseResults
operator|.
name|asList
argument_list|()
control|)
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|searchPhaseResult
operator|.
name|getRequestId
argument_list|()
argument_list|)
expr_stmt|;
name|SearchShardTarget
name|searchShardTarget
init|=
name|searchPhaseResult
operator|.
name|getSearchShardTarget
argument_list|()
decl_stmt|;
if|if
condition|(
name|searchShardTarget
operator|.
name|getClusterAlias
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|RemoteClusterAware
operator|.
name|buildRemoteIndexName
argument_list|(
name|searchShardTarget
operator|.
name|getClusterAlias
argument_list|()
argument_list|,
name|searchShardTarget
operator|.
name|getNodeId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeString
argument_list|(
name|searchShardTarget
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|out
operator|.
name|getFilePointer
argument_list|()
index|]
decl_stmt|;
name|out
operator|.
name|writeTo
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|Base64
operator|.
name|getUrlEncoder
argument_list|()
operator|.
name|encodeToString
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
DECL|method|parseScrollId
specifier|static
name|ParsedScrollId
name|parseScrollId
parameter_list|(
name|String
name|scrollId
parameter_list|)
block|{
try|try
block|{
name|byte
index|[]
name|bytes
init|=
name|Base64
operator|.
name|getUrlDecoder
argument_list|()
operator|.
name|decode
argument_list|(
name|scrollId
argument_list|)
decl_stmt|;
name|ByteArrayDataInput
name|in
init|=
operator|new
name|ByteArrayDataInput
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|ScrollIdForNode
index|[]
name|context
init|=
operator|new
name|ScrollIdForNode
index|[
name|in
operator|.
name|readVInt
argument_list|()
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
name|context
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|long
name|id
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|String
name|target
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|clusterAlias
decl_stmt|;
specifier|final
name|int
name|index
init|=
name|target
operator|.
name|indexOf
argument_list|(
name|RemoteClusterAware
operator|.
name|REMOTE_CLUSTER_INDEX_SEPARATOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
operator|-
literal|1
condition|)
block|{
name|clusterAlias
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|clusterAlias
operator|=
name|target
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|target
operator|=
name|target
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|context
index|[
name|i
index|]
operator|=
operator|new
name|ScrollIdForNode
argument_list|(
name|clusterAlias
argument_list|,
name|target
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|getPosition
argument_list|()
operator|!=
name|bytes
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not all bytes were read"
argument_list|)
throw|;
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
argument_list|)
return|;
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
literal|"Cannot parse scroll id"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|TransportSearchHelper
specifier|private
name|TransportSearchHelper
parameter_list|()
block|{      }
block|}
end_class

end_unit

