begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.warmer.get
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|warmer
operator|.
name|get
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
name|cursors
operator|.
name|ObjectObjectCursor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|action
operator|.
name|ActionResponse
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
name|collect
operator|.
name|ImmutableOpenMap
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
name|warmer
operator|.
name|IndexWarmersMetaData
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
comment|/**  * Holds a warmer-name to a list of {@link IndexWarmersMetaData} mapping for each warmer specified  * in the {@link GetWarmersRequest}. This information is fetched from the current master since the metadata  * is contained inside the cluster-state  */
end_comment

begin_class
DECL|class|GetWarmersResponse
specifier|public
class|class
name|GetWarmersResponse
extends|extends
name|ActionResponse
block|{
DECL|field|warmers
specifier|private
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableList
argument_list|<
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|>
argument_list|>
name|warmers
init|=
name|ImmutableOpenMap
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|method|GetWarmersResponse
name|GetWarmersResponse
parameter_list|(
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableList
argument_list|<
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|>
argument_list|>
name|warmers
parameter_list|)
block|{
name|this
operator|.
name|warmers
operator|=
name|warmers
expr_stmt|;
block|}
DECL|method|GetWarmersResponse
name|GetWarmersResponse
parameter_list|()
block|{     }
DECL|method|warmers
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableList
argument_list|<
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|>
argument_list|>
name|warmers
parameter_list|()
block|{
return|return
name|warmers
return|;
block|}
DECL|method|getWarmers
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableList
argument_list|<
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|>
argument_list|>
name|getWarmers
parameter_list|()
block|{
return|return
name|warmers
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
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|ImmutableOpenMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|ImmutableList
argument_list|<
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|>
argument_list|>
name|indexMapBuilder
init|=
name|ImmutableOpenMap
operator|.
name|builder
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|int
name|valueSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|>
name|warmerEntryBuilder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|valueSize
condition|;
name|j
operator|++
control|)
block|{
name|String
name|name
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
index|[]
name|types
init|=
name|in
operator|.
name|readStringArray
argument_list|()
decl_stmt|;
name|BytesReference
name|source
init|=
name|in
operator|.
name|readBytesReference
argument_list|()
decl_stmt|;
name|Boolean
name|queryCache
init|=
literal|null
decl_stmt|;
name|queryCache
operator|=
name|in
operator|.
name|readOptionalBoolean
argument_list|()
expr_stmt|;
name|warmerEntryBuilder
operator|.
name|add
argument_list|(
operator|new
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|(
name|name
argument_list|,
name|types
argument_list|,
name|queryCache
argument_list|,
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexMapBuilder
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|warmerEntryBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|warmers
operator|=
name|indexMapBuilder
operator|.
name|build
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
name|writeVInt
argument_list|(
name|warmers
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
name|ImmutableList
argument_list|<
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|>
argument_list|>
name|indexEntry
range|:
name|warmers
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|indexEntry
operator|.
name|key
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|indexEntry
operator|.
name|value
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexWarmersMetaData
operator|.
name|Entry
name|warmerEntry
range|:
name|indexEntry
operator|.
name|value
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|warmerEntry
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArray
argument_list|(
name|warmerEntry
operator|.
name|types
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytesReference
argument_list|(
name|warmerEntry
operator|.
name|source
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalBoolean
argument_list|(
name|warmerEntry
operator|.
name|requestCache
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

