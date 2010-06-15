begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|common
operator|.
name|collect
operator|.
name|Iterators
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
name|trove
operator|.
name|TIntObjectHashMap
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
name|builder
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
name|SearchHit
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
name|SearchHits
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
name|util
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
name|util
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchShardTarget
operator|.
name|*
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
name|internal
operator|.
name|InternalSearchHit
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|InternalSearchHits
specifier|public
class|class
name|InternalSearchHits
implements|implements
name|SearchHits
block|{
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|InternalSearchHit
index|[]
name|EMPTY
init|=
operator|new
name|InternalSearchHit
index|[
literal|0
index|]
decl_stmt|;
DECL|field|hits
specifier|private
name|InternalSearchHit
index|[]
name|hits
decl_stmt|;
DECL|field|totalHits
specifier|private
name|long
name|totalHits
decl_stmt|;
DECL|method|InternalSearchHits
name|InternalSearchHits
parameter_list|()
block|{      }
DECL|method|InternalSearchHits
specifier|public
name|InternalSearchHits
parameter_list|(
name|InternalSearchHit
index|[]
name|hits
parameter_list|,
name|long
name|totalHits
parameter_list|)
block|{
name|this
operator|.
name|hits
operator|=
name|hits
expr_stmt|;
name|this
operator|.
name|totalHits
operator|=
name|totalHits
expr_stmt|;
block|}
DECL|method|totalHits
specifier|public
name|long
name|totalHits
parameter_list|()
block|{
return|return
name|totalHits
return|;
block|}
DECL|method|getTotalHits
annotation|@
name|Override
specifier|public
name|long
name|getTotalHits
parameter_list|()
block|{
return|return
name|totalHits
argument_list|()
return|;
block|}
DECL|method|hits
specifier|public
name|SearchHit
index|[]
name|hits
parameter_list|()
block|{
return|return
name|this
operator|.
name|hits
return|;
block|}
DECL|method|getAt
annotation|@
name|Override
specifier|public
name|SearchHit
name|getAt
parameter_list|(
name|int
name|position
parameter_list|)
block|{
return|return
name|hits
index|[
name|position
index|]
return|;
block|}
DECL|method|getHits
annotation|@
name|Override
specifier|public
name|SearchHit
index|[]
name|getHits
parameter_list|()
block|{
return|return
name|hits
argument_list|()
return|;
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|SearchHit
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Iterators
operator|.
name|forArray
argument_list|(
name|hits
argument_list|()
argument_list|)
return|;
block|}
DECL|method|internalHits
specifier|public
name|InternalSearchHit
index|[]
name|internalHits
parameter_list|()
block|{
return|return
name|this
operator|.
name|hits
return|;
block|}
DECL|method|toXContent
annotation|@
name|Override
specifier|public
name|void
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
literal|"hits"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"total"
argument_list|,
name|totalHits
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"hits"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|()
expr_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|hits
control|)
block|{
name|hit
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
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|method|readSearchHits
specifier|public
specifier|static
name|InternalSearchHits
name|readSearchHits
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalSearchHits
name|hits
init|=
operator|new
name|InternalSearchHits
argument_list|()
decl_stmt|;
name|hits
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|hits
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
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
name|totalHits
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
name|hits
operator|=
name|EMPTY
expr_stmt|;
block|}
else|else
block|{
comment|// read the lookup table first
name|int
name|lookupSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|TIntObjectHashMap
argument_list|<
name|SearchShardTarget
argument_list|>
name|shardLookupMap
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|lookupSize
operator|>
literal|0
condition|)
block|{
name|shardLookupMap
operator|=
operator|new
name|TIntObjectHashMap
argument_list|<
name|SearchShardTarget
argument_list|>
argument_list|(
name|lookupSize
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
name|lookupSize
condition|;
name|i
operator|++
control|)
block|{
name|shardLookupMap
operator|.
name|put
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|readSearchShardTarget
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|hits
operator|=
operator|new
name|InternalSearchHit
index|[
name|size
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
name|hits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|hits
index|[
name|i
index|]
operator|=
name|readSearchHit
argument_list|(
name|in
argument_list|,
name|shardLookupMap
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|writeTo
annotation|@
name|Override
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
name|writeVLong
argument_list|(
name|totalHits
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|hits
operator|.
name|length
operator|>
literal|0
condition|)
block|{
comment|// write the header search shard targets (we assume identity equality)
name|IdentityHashMap
argument_list|<
name|SearchShardTarget
argument_list|,
name|Integer
argument_list|>
name|shardLookupMap
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|SearchShardTarget
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
comment|// start from 1, 0 is for null!
name|int
name|counter
init|=
literal|1
decl_stmt|;
comment|// put an entry for null
for|for
control|(
name|InternalSearchHit
name|hit
range|:
name|hits
control|)
block|{
if|if
condition|(
name|hit
operator|.
name|shard
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Integer
name|handle
init|=
name|shardLookupMap
operator|.
name|get
argument_list|(
name|hit
operator|.
name|shard
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|handle
operator|==
literal|null
condition|)
block|{
name|shardLookupMap
operator|.
name|put
argument_list|(
name|hit
operator|.
name|shard
argument_list|()
argument_list|,
name|counter
operator|++
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|shardLookupMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|shardLookupMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|SearchShardTarget
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|shardLookupMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|InternalSearchHit
name|hit
range|:
name|hits
control|)
block|{
name|hit
operator|.
name|writeTo
argument_list|(
name|out
argument_list|,
name|shardLookupMap
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|shardLookupMap
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

