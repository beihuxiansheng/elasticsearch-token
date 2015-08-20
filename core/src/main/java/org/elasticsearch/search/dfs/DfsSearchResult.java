begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.dfs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|dfs
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectObjectHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectObjectCursor
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
name|index
operator|.
name|Term
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
name|search
operator|.
name|CollectionStatistics
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
name|search
operator|.
name|TermStatistics
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
name|BytesRef
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
name|collect
operator|.
name|HppcMaps
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|DfsSearchResult
specifier|public
class|class
name|DfsSearchResult
extends|extends
name|TransportResponse
implements|implements
name|SearchPhaseResult
block|{
DECL|field|EMPTY_TERMS
specifier|private
specifier|static
specifier|final
name|Term
index|[]
name|EMPTY_TERMS
init|=
operator|new
name|Term
index|[
literal|0
index|]
decl_stmt|;
DECL|field|EMPTY_TERM_STATS
specifier|private
specifier|static
specifier|final
name|TermStatistics
index|[]
name|EMPTY_TERM_STATS
init|=
operator|new
name|TermStatistics
index|[
literal|0
index|]
decl_stmt|;
DECL|field|shardTarget
specifier|private
name|SearchShardTarget
name|shardTarget
decl_stmt|;
DECL|field|id
specifier|private
name|long
name|id
decl_stmt|;
DECL|field|terms
specifier|private
name|Term
index|[]
name|terms
decl_stmt|;
DECL|field|termStatistics
specifier|private
name|TermStatistics
index|[]
name|termStatistics
decl_stmt|;
DECL|field|fieldStatistics
specifier|private
name|ObjectObjectHashMap
argument_list|<
name|String
argument_list|,
name|CollectionStatistics
argument_list|>
name|fieldStatistics
init|=
name|HppcMaps
operator|.
name|newNoNullKeysMap
argument_list|()
decl_stmt|;
DECL|field|maxDoc
specifier|private
name|int
name|maxDoc
decl_stmt|;
DECL|method|DfsSearchResult
specifier|public
name|DfsSearchResult
parameter_list|()
block|{      }
DECL|method|DfsSearchResult
specifier|public
name|DfsSearchResult
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
annotation|@
name|Override
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
DECL|method|maxDoc
specifier|public
name|DfsSearchResult
name|maxDoc
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
DECL|method|termsStatistics
specifier|public
name|DfsSearchResult
name|termsStatistics
parameter_list|(
name|Term
index|[]
name|terms
parameter_list|,
name|TermStatistics
index|[]
name|termStatistics
parameter_list|)
block|{
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|this
operator|.
name|termStatistics
operator|=
name|termStatistics
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fieldStatistics
specifier|public
name|DfsSearchResult
name|fieldStatistics
parameter_list|(
name|ObjectObjectHashMap
argument_list|<
name|String
argument_list|,
name|CollectionStatistics
argument_list|>
name|fieldStatistics
parameter_list|)
block|{
name|this
operator|.
name|fieldStatistics
operator|=
name|fieldStatistics
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|terms
specifier|public
name|Term
index|[]
name|terms
parameter_list|()
block|{
return|return
name|terms
return|;
block|}
DECL|method|termStatistics
specifier|public
name|TermStatistics
index|[]
name|termStatistics
parameter_list|()
block|{
return|return
name|termStatistics
return|;
block|}
DECL|method|fieldStatistics
specifier|public
name|ObjectObjectHashMap
argument_list|<
name|String
argument_list|,
name|CollectionStatistics
argument_list|>
name|fieldStatistics
parameter_list|()
block|{
return|return
name|fieldStatistics
return|;
block|}
DECL|method|readDfsSearchResult
specifier|public
specifier|static
name|DfsSearchResult
name|readDfsSearchResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|DfsSearchResult
name|result
init|=
operator|new
name|DfsSearchResult
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
name|int
name|termsSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsSize
operator|==
literal|0
condition|)
block|{
name|terms
operator|=
name|EMPTY_TERMS
expr_stmt|;
block|}
else|else
block|{
name|terms
operator|=
operator|new
name|Term
index|[
name|termsSize
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|terms
index|[
name|i
index|]
operator|=
operator|new
name|Term
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|,
name|in
operator|.
name|readBytesRef
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|termStatistics
operator|=
name|readTermStats
argument_list|(
name|in
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|readFieldStats
argument_list|(
name|in
argument_list|,
name|fieldStatistics
argument_list|)
expr_stmt|;
name|maxDoc
operator|=
name|in
operator|.
name|readVInt
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
name|writeLong
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|terms
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytesRef
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writeTermStats
argument_list|(
name|out
argument_list|,
name|termStatistics
argument_list|)
expr_stmt|;
name|writeFieldStats
argument_list|(
name|out
argument_list|,
name|fieldStatistics
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
DECL|method|writeFieldStats
specifier|public
specifier|static
name|void
name|writeFieldStats
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|ObjectObjectHashMap
argument_list|<
name|String
argument_list|,
name|CollectionStatistics
argument_list|>
name|fieldStatistics
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|fieldStatistics
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|CollectionStatistics
argument_list|>
name|c
range|:
name|fieldStatistics
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|c
operator|.
name|key
argument_list|)
expr_stmt|;
name|CollectionStatistics
name|statistics
init|=
name|c
operator|.
name|value
decl_stmt|;
assert|assert
name|statistics
operator|.
name|maxDoc
argument_list|()
operator|>=
literal|0
assert|;
name|out
operator|.
name|writeVLong
argument_list|(
name|statistics
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|addOne
argument_list|(
name|statistics
operator|.
name|docCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|addOne
argument_list|(
name|statistics
operator|.
name|sumTotalTermFreq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|addOne
argument_list|(
name|statistics
operator|.
name|sumDocFreq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeTermStats
specifier|public
specifier|static
name|void
name|writeTermStats
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|TermStatistics
index|[]
name|termStatistics
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|termStatistics
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|TermStatistics
name|termStatistic
range|:
name|termStatistics
control|)
block|{
name|writeSingleTermStats
argument_list|(
name|out
argument_list|,
name|termStatistic
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeSingleTermStats
specifier|public
specifier|static
name|void
name|writeSingleTermStats
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|TermStatistics
name|termStatistic
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|termStatistic
operator|.
name|docFreq
argument_list|()
operator|>=
literal|0
assert|;
name|out
operator|.
name|writeVLong
argument_list|(
name|termStatistic
operator|.
name|docFreq
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|addOne
argument_list|(
name|termStatistic
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|readFieldStats
specifier|public
specifier|static
name|ObjectObjectHashMap
argument_list|<
name|String
argument_list|,
name|CollectionStatistics
argument_list|>
name|readFieldStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readFieldStats
argument_list|(
name|in
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|readFieldStats
specifier|public
specifier|static
name|ObjectObjectHashMap
argument_list|<
name|String
argument_list|,
name|CollectionStatistics
argument_list|>
name|readFieldStats
parameter_list|(
name|StreamInput
name|in
parameter_list|,
name|ObjectObjectHashMap
argument_list|<
name|String
argument_list|,
name|CollectionStatistics
argument_list|>
name|fieldStatistics
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numFieldStatistics
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldStatistics
operator|==
literal|null
condition|)
block|{
name|fieldStatistics
operator|=
name|HppcMaps
operator|.
name|newNoNullKeysMap
argument_list|(
name|numFieldStatistics
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numFieldStatistics
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|field
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
assert|assert
name|field
operator|!=
literal|null
assert|;
specifier|final
name|long
name|maxDoc
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|long
name|docCount
init|=
name|subOne
argument_list|(
name|in
operator|.
name|readVLong
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|long
name|sumTotalTermFreq
init|=
name|subOne
argument_list|(
name|in
operator|.
name|readVLong
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|long
name|sumDocFreq
init|=
name|subOne
argument_list|(
name|in
operator|.
name|readVLong
argument_list|()
argument_list|)
decl_stmt|;
name|CollectionStatistics
name|stats
init|=
operator|new
name|CollectionStatistics
argument_list|(
name|field
argument_list|,
name|maxDoc
argument_list|,
name|docCount
argument_list|,
name|sumTotalTermFreq
argument_list|,
name|sumDocFreq
argument_list|)
decl_stmt|;
name|fieldStatistics
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldStatistics
return|;
block|}
DECL|method|readTermStats
specifier|public
specifier|static
name|TermStatistics
index|[]
name|readTermStats
parameter_list|(
name|StreamInput
name|in
parameter_list|,
name|Term
index|[]
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|termsStatsSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|TermStatistics
index|[]
name|termStatistics
decl_stmt|;
if|if
condition|(
name|termsStatsSize
operator|==
literal|0
condition|)
block|{
name|termStatistics
operator|=
name|EMPTY_TERM_STATS
expr_stmt|;
block|}
else|else
block|{
name|termStatistics
operator|=
operator|new
name|TermStatistics
index|[
name|termsStatsSize
index|]
expr_stmt|;
assert|assert
name|terms
operator|.
name|length
operator|==
name|termsStatsSize
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|termStatistics
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|term
init|=
name|terms
index|[
name|i
index|]
operator|.
name|bytes
argument_list|()
decl_stmt|;
specifier|final
name|long
name|docFreq
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
assert|assert
name|docFreq
operator|>=
literal|0
assert|;
specifier|final
name|long
name|totalTermFreq
init|=
name|subOne
argument_list|(
name|in
operator|.
name|readVLong
argument_list|()
argument_list|)
decl_stmt|;
name|termStatistics
index|[
name|i
index|]
operator|=
operator|new
name|TermStatistics
argument_list|(
name|term
argument_list|,
name|docFreq
argument_list|,
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|termStatistics
return|;
block|}
comment|/*      * optional statistics are set to -1 in lucene by default.      * Since we are using var longs to encode values we add one to each value      * to ensure we don't waste space and don't add negative values.      */
DECL|method|addOne
specifier|public
specifier|static
name|long
name|addOne
parameter_list|(
name|long
name|value
parameter_list|)
block|{
assert|assert
name|value
operator|+
literal|1
operator|>=
literal|0
assert|;
return|return
name|value
operator|+
literal|1
return|;
block|}
comment|/*      * See #addOne this just subtracting one and asserts that the actual value      * is positive.      */
DECL|method|subOne
specifier|public
specifier|static
name|long
name|subOne
parameter_list|(
name|long
name|value
parameter_list|)
block|{
assert|assert
name|value
operator|>=
literal|0
assert|;
return|return
name|value
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit
