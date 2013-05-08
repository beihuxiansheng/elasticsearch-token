begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|Map
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
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|XMaps
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|Streamable
import|;
end_import

begin_class
DECL|class|AggregatedDfs
specifier|public
class|class
name|AggregatedDfs
implements|implements
name|Streamable
block|{
DECL|field|termStatistics
specifier|private
name|Map
argument_list|<
name|Term
argument_list|,
name|TermStatistics
argument_list|>
name|termStatistics
decl_stmt|;
DECL|field|fieldStatistics
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStatistics
argument_list|>
name|fieldStatistics
decl_stmt|;
DECL|field|maxDoc
specifier|private
name|long
name|maxDoc
decl_stmt|;
DECL|method|AggregatedDfs
specifier|private
name|AggregatedDfs
parameter_list|()
block|{     }
DECL|method|AggregatedDfs
specifier|public
name|AggregatedDfs
parameter_list|(
name|Map
argument_list|<
name|Term
argument_list|,
name|TermStatistics
argument_list|>
name|termStatistics
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStatistics
argument_list|>
name|fieldStatistics
parameter_list|,
name|long
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|termStatistics
operator|=
name|termStatistics
expr_stmt|;
name|this
operator|.
name|fieldStatistics
operator|=
name|fieldStatistics
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
DECL|method|termStatistics
specifier|public
name|Map
argument_list|<
name|Term
argument_list|,
name|TermStatistics
argument_list|>
name|termStatistics
parameter_list|()
block|{
return|return
name|termStatistics
return|;
block|}
DECL|method|fieldStatistics
specifier|public
name|Map
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
DECL|method|maxDoc
specifier|public
name|long
name|maxDoc
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
DECL|method|readAggregatedDfs
specifier|public
specifier|static
name|AggregatedDfs
name|readAggregatedDfs
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|AggregatedDfs
name|result
init|=
operator|new
name|AggregatedDfs
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
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|termStatistics
operator|=
name|XMaps
operator|.
name|newMap
argument_list|(
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|Term
name|term
init|=
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
decl_stmt|;
name|TermStatistics
name|stats
init|=
operator|new
name|TermStatistics
argument_list|(
name|in
operator|.
name|readBytesRef
argument_list|()
argument_list|,
name|in
operator|.
name|readVLong
argument_list|()
argument_list|,
name|DfsSearchResult
operator|.
name|toNotAvailable
argument_list|(
name|in
operator|.
name|readVLong
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|termStatistics
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
name|fieldStatistics
operator|=
name|DfsSearchResult
operator|.
name|readFieldStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|maxDoc
operator|=
name|in
operator|.
name|readVLong
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
specifier|final
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
name|termStatistics
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Term
argument_list|,
name|TermStatistics
argument_list|>
name|termTermStatisticsEntry
range|:
name|termStatistics
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Term
name|term
init|=
name|termTermStatisticsEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
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
name|TermStatistics
name|stats
init|=
name|termTermStatisticsEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeBytesRef
argument_list|(
name|stats
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|stats
operator|.
name|docFreq
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|DfsSearchResult
operator|.
name|plusOne
argument_list|(
name|stats
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|DfsSearchResult
operator|.
name|writeFieldStats
argument_list|(
name|out
argument_list|,
name|fieldStatistics
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

