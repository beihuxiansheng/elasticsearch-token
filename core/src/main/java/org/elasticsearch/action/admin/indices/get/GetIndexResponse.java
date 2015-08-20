begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.get
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
name|cluster
operator|.
name|metadata
operator|.
name|AliasMetaData
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
name|metadata
operator|.
name|MappingMetaData
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
name|common
operator|.
name|settings
operator|.
name|Settings
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
comment|/**  * A response for a delete index action.  */
end_comment

begin_class
DECL|class|GetIndexResponse
specifier|public
class|class
name|GetIndexResponse
extends|extends
name|ActionResponse
block|{
DECL|field|warmers
specifier|private
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|List
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
DECL|field|mappings
specifier|private
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
argument_list|>
name|mappings
init|=
name|ImmutableOpenMap
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|aliases
specifier|private
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|aliases
init|=
name|ImmutableOpenMap
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|settings
specifier|private
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|settings
init|=
name|ImmutableOpenMap
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|indices
specifier|private
name|String
index|[]
name|indices
decl_stmt|;
DECL|method|GetIndexResponse
name|GetIndexResponse
parameter_list|(
name|String
index|[]
name|indices
parameter_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|>
argument_list|>
name|warmers
parameter_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
argument_list|>
name|mappings
parameter_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|aliases
parameter_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|settings
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
if|if
condition|(
name|warmers
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|warmers
operator|=
name|warmers
expr_stmt|;
block|}
if|if
condition|(
name|mappings
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|mappings
operator|=
name|mappings
expr_stmt|;
block|}
if|if
condition|(
name|aliases
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|aliases
operator|=
name|aliases
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
block|}
block|}
DECL|method|GetIndexResponse
name|GetIndexResponse
parameter_list|()
block|{     }
DECL|method|indices
specifier|public
name|String
index|[]
name|indices
parameter_list|()
block|{
return|return
name|indices
return|;
block|}
DECL|method|getIndices
specifier|public
name|String
index|[]
name|getIndices
parameter_list|()
block|{
return|return
name|indices
argument_list|()
return|;
block|}
DECL|method|warmers
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|List
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
name|List
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
DECL|method|mappings
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
argument_list|>
name|mappings
parameter_list|()
block|{
return|return
name|mappings
return|;
block|}
DECL|method|getMappings
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
argument_list|>
name|getMappings
parameter_list|()
block|{
return|return
name|mappings
argument_list|()
return|;
block|}
DECL|method|aliases
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|aliases
parameter_list|()
block|{
return|return
name|aliases
return|;
block|}
DECL|method|getAliases
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|getAliases
parameter_list|()
block|{
return|return
name|aliases
argument_list|()
return|;
block|}
DECL|method|settings
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|settings
parameter_list|()
block|{
return|return
name|settings
return|;
block|}
DECL|method|getSettings
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|getSettings
parameter_list|()
block|{
return|return
name|settings
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
name|this
operator|.
name|indices
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|int
name|warmersSize
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
name|List
argument_list|<
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|>
argument_list|>
name|warmersMapBuilder
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
name|warmersSize
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
name|List
argument_list|<
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|>
name|warmerEntryBuilder
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|warmerEntryBuilder
operator|.
name|add
argument_list|(
operator|new
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|,
name|in
operator|.
name|readStringArray
argument_list|()
argument_list|,
name|in
operator|.
name|readOptionalBoolean
argument_list|()
argument_list|,
name|in
operator|.
name|readBytesReference
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|warmersMapBuilder
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|warmerEntryBuilder
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|warmers
operator|=
name|warmersMapBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|int
name|mappingsSize
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
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
argument_list|>
name|mappingsMapBuilder
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
name|mappingsSize
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
name|ImmutableOpenMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
name|mappingEntryBuilder
init|=
name|ImmutableOpenMap
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
name|mappingEntryBuilder
operator|.
name|put
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|,
name|MappingMetaData
operator|.
name|PROTO
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|mappingsMapBuilder
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|mappingEntryBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mappings
operator|=
name|mappingsMapBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|int
name|aliasesSize
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
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|aliasesMapBuilder
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
name|aliasesSize
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
name|List
argument_list|<
name|AliasMetaData
argument_list|>
name|aliasEntryBuilder
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|aliasEntryBuilder
operator|.
name|add
argument_list|(
name|AliasMetaData
operator|.
name|Builder
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|aliasesMapBuilder
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|aliasEntryBuilder
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|aliases
operator|=
name|aliasesMapBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|int
name|settingsSize
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
name|Settings
argument_list|>
name|settingsMapBuilder
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
name|settingsSize
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
name|settingsMapBuilder
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|Settings
operator|.
name|readSettingsFromStream
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|settings
operator|=
name|settingsMapBuilder
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
name|writeStringArray
argument_list|(
name|indices
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
name|List
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
name|writeOptionalBoolean
argument_list|(
name|warmerEntry
operator|.
name|requestCache
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
block|}
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|mappings
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
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
argument_list|>
name|indexEntry
range|:
name|mappings
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
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
name|mappingEntry
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
name|mappingEntry
operator|.
name|key
argument_list|)
expr_stmt|;
name|mappingEntry
operator|.
name|value
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|aliases
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
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|indexEntry
range|:
name|aliases
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
name|AliasMetaData
name|aliasEntry
range|:
name|indexEntry
operator|.
name|value
control|)
block|{
name|aliasEntry
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|settings
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
name|Settings
argument_list|>
name|indexEntry
range|:
name|settings
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
name|Settings
operator|.
name|writeSettingsToStream
argument_list|(
name|indexEntry
operator|.
name|value
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
