begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
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
name|index
operator|.
name|SegmentInfos
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
name|collect
operator|.
name|MapBuilder
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|Lucene
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
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilderString
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
name|Map
import|;
end_import

begin_comment
comment|/** a class the returns dynamic information with respect to the last commit point of this shard */
end_comment

begin_class
DECL|class|CommitStats
specifier|public
specifier|final
class|class
name|CommitStats
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|field|userData
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|userData
decl_stmt|;
DECL|field|generation
specifier|private
name|long
name|generation
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
comment|// lucene commit id in base 64;
DECL|field|numDocs
specifier|private
name|int
name|numDocs
decl_stmt|;
DECL|method|CommitStats
specifier|public
name|CommitStats
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|)
block|{
comment|// clone the map to protect against concurrent changes
name|userData
operator|=
name|MapBuilder
operator|.
expr|<
name|String
operator|,
name|String
operator|>
name|newMapBuilder
argument_list|()
operator|.
name|putAll
argument_list|(
name|segmentInfos
operator|.
name|getUserData
argument_list|()
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
comment|// lucene calls the current generation, last generation.
name|generation
operator|=
name|segmentInfos
operator|.
name|getLastGeneration
argument_list|()
expr_stmt|;
if|if
condition|(
name|segmentInfos
operator|.
name|getId
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// id is only written starting with Lucene 5.0
name|id
operator|=
name|Base64
operator|.
name|encodeBytes
argument_list|(
name|segmentInfos
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|numDocs
operator|=
name|Lucene
operator|.
name|getNumDocs
argument_list|(
name|segmentInfos
argument_list|)
expr_stmt|;
block|}
DECL|method|CommitStats
specifier|private
name|CommitStats
parameter_list|()
block|{      }
DECL|method|readCommitStatsFrom
specifier|public
specifier|static
name|CommitStats
name|readCommitStatsFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|CommitStats
name|commitStats
init|=
operator|new
name|CommitStats
argument_list|()
decl_stmt|;
name|commitStats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|commitStats
return|;
block|}
DECL|method|readOptionalCommitStatsFrom
specifier|public
specifier|static
name|CommitStats
name|readOptionalCommitStatsFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readOptionalStreamable
argument_list|(
operator|new
name|CommitStats
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getUserData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getUserData
parameter_list|()
block|{
return|return
name|userData
return|;
block|}
DECL|method|getGeneration
specifier|public
name|long
name|getGeneration
parameter_list|()
block|{
return|return
name|generation
return|;
block|}
comment|/** base64 version of the commit id (see {@link SegmentInfos#getId()} */
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**      * Returns the number of documents in the in this commit      */
DECL|method|getNumDocs
specifier|public
name|int
name|getNumDocs
parameter_list|()
block|{
return|return
name|numDocs
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
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|builder
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|in
operator|.
name|readVInt
argument_list|()
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|,
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|userData
operator|=
name|builder
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|generation
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|id
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|numDocs
operator|=
name|in
operator|.
name|readInt
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
name|userData
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
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|userData
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeLong
argument_list|(
name|generation
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|GENERATION
specifier|static
specifier|final
name|XContentBuilderString
name|GENERATION
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"generation"
argument_list|)
decl_stmt|;
DECL|field|USER_DATA
specifier|static
specifier|final
name|XContentBuilderString
name|USER_DATA
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"user_data"
argument_list|)
decl_stmt|;
DECL|field|ID
specifier|static
specifier|final
name|XContentBuilderString
name|ID
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
DECL|field|COMMIT
specifier|static
specifier|final
name|XContentBuilderString
name|COMMIT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"commit"
argument_list|)
decl_stmt|;
DECL|field|NUM_DOCS
specifier|static
specifier|final
name|XContentBuilderString
name|NUM_DOCS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"num_docs"
argument_list|)
decl_stmt|;
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
name|startObject
argument_list|(
name|Fields
operator|.
name|COMMIT
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ID
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|GENERATION
argument_list|,
name|generation
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|USER_DATA
argument_list|,
name|userData
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NUM_DOCS
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

