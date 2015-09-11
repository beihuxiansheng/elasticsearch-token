begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
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
name|ObjectCursor
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
name|elasticsearch
operator|.
name|cluster
operator|.
name|AbstractDiffable
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
name|compress
operator|.
name|CompressedXContent
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
name|common
operator|.
name|settings
operator|.
name|loader
operator|.
name|SettingsLoader
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
name|set
operator|.
name|Sets
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
name|XContentFactory
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
name|XContentParser
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|IndexTemplateMetaData
specifier|public
class|class
name|IndexTemplateMetaData
extends|extends
name|AbstractDiffable
argument_list|<
name|IndexTemplateMetaData
argument_list|>
block|{
DECL|field|PROTO
specifier|public
specifier|static
specifier|final
name|IndexTemplateMetaData
name|PROTO
init|=
name|IndexTemplateMetaData
operator|.
name|builder
argument_list|(
literal|""
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|order
specifier|private
specifier|final
name|int
name|order
decl_stmt|;
DECL|field|template
specifier|private
specifier|final
name|String
name|template
decl_stmt|;
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
comment|// the mapping source should always include the type as top level
DECL|field|mappings
specifier|private
specifier|final
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|CompressedXContent
argument_list|>
name|mappings
decl_stmt|;
DECL|field|aliases
specifier|private
specifier|final
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|AliasMetaData
argument_list|>
name|aliases
decl_stmt|;
DECL|field|customs
specifier|private
specifier|final
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|IndexMetaData
operator|.
name|Custom
argument_list|>
name|customs
decl_stmt|;
DECL|method|IndexTemplateMetaData
specifier|public
name|IndexTemplateMetaData
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|order
parameter_list|,
name|String
name|template
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|CompressedXContent
argument_list|>
name|mappings
parameter_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|AliasMetaData
argument_list|>
name|aliases
parameter_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|IndexMetaData
operator|.
name|Custom
argument_list|>
name|customs
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
name|this
operator|.
name|template
operator|=
name|template
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|mappings
operator|=
name|mappings
expr_stmt|;
name|this
operator|.
name|aliases
operator|=
name|aliases
expr_stmt|;
name|this
operator|.
name|customs
operator|=
name|customs
expr_stmt|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
DECL|method|order
specifier|public
name|int
name|order
parameter_list|()
block|{
return|return
name|this
operator|.
name|order
return|;
block|}
DECL|method|getOrder
specifier|public
name|int
name|getOrder
parameter_list|()
block|{
return|return
name|order
argument_list|()
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
DECL|method|template
specifier|public
name|String
name|template
parameter_list|()
block|{
return|return
name|this
operator|.
name|template
return|;
block|}
DECL|method|getTemplate
specifier|public
name|String
name|getTemplate
parameter_list|()
block|{
return|return
name|this
operator|.
name|template
return|;
block|}
DECL|method|settings
specifier|public
name|Settings
name|settings
parameter_list|()
block|{
return|return
name|this
operator|.
name|settings
return|;
block|}
DECL|method|getSettings
specifier|public
name|Settings
name|getSettings
parameter_list|()
block|{
return|return
name|settings
argument_list|()
return|;
block|}
DECL|method|mappings
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|CompressedXContent
argument_list|>
name|mappings
parameter_list|()
block|{
return|return
name|this
operator|.
name|mappings
return|;
block|}
DECL|method|getMappings
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|CompressedXContent
argument_list|>
name|getMappings
parameter_list|()
block|{
return|return
name|this
operator|.
name|mappings
return|;
block|}
DECL|method|aliases
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|AliasMetaData
argument_list|>
name|aliases
parameter_list|()
block|{
return|return
name|this
operator|.
name|aliases
return|;
block|}
DECL|method|getAliases
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|AliasMetaData
argument_list|>
name|getAliases
parameter_list|()
block|{
return|return
name|this
operator|.
name|aliases
return|;
block|}
DECL|method|customs
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|IndexMetaData
operator|.
name|Custom
argument_list|>
name|customs
parameter_list|()
block|{
return|return
name|this
operator|.
name|customs
return|;
block|}
DECL|method|getCustoms
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|IndexMetaData
operator|.
name|Custom
argument_list|>
name|getCustoms
parameter_list|()
block|{
return|return
name|this
operator|.
name|customs
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|custom
specifier|public
parameter_list|<
name|T
extends|extends
name|IndexMetaData
operator|.
name|Custom
parameter_list|>
name|T
name|custom
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
operator|(
name|T
operator|)
name|customs
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|IndexTemplateMetaData
name|that
init|=
operator|(
name|IndexTemplateMetaData
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|order
operator|!=
name|that
operator|.
name|order
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|mappings
operator|.
name|equals
argument_list|(
name|that
operator|.
name|mappings
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|that
operator|.
name|name
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|settings
operator|.
name|equals
argument_list|(
name|that
operator|.
name|settings
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|template
operator|.
name|equals
argument_list|(
name|that
operator|.
name|template
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|name
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|order
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|template
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|settings
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|mappings
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|IndexTemplateMetaData
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|order
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|template
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|settings
argument_list|(
name|Settings
operator|.
name|readSettingsFromStream
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|mappingsSize
init|=
name|in
operator|.
name|readVInt
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
name|builder
operator|.
name|putMapping
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|,
name|CompressedXContent
operator|.
name|readCompressedString
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|aliasesSize
init|=
name|in
operator|.
name|readVInt
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
name|AliasMetaData
name|aliasMd
init|=
name|AliasMetaData
operator|.
name|Builder
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|builder
operator|.
name|putAlias
argument_list|(
name|aliasMd
argument_list|)
expr_stmt|;
block|}
name|int
name|customSize
init|=
name|in
operator|.
name|readVInt
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
name|customSize
condition|;
name|i
operator|++
control|)
block|{
name|String
name|type
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|IndexMetaData
operator|.
name|Custom
name|customIndexMetaData
init|=
name|IndexMetaData
operator|.
name|lookupPrototypeSafe
argument_list|(
name|type
argument_list|)
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|builder
operator|.
name|putCustom
argument_list|(
name|type
argument_list|,
name|customIndexMetaData
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
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
name|writeString
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|order
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|template
argument_list|)
expr_stmt|;
name|Settings
operator|.
name|writeSettingsToStream
argument_list|(
name|settings
argument_list|,
name|out
argument_list|)
expr_stmt|;
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
name|CompressedXContent
argument_list|>
name|cursor
range|:
name|mappings
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|cursor
operator|.
name|key
argument_list|)
expr_stmt|;
name|cursor
operator|.
name|value
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
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
name|ObjectCursor
argument_list|<
name|AliasMetaData
argument_list|>
name|cursor
range|:
name|aliases
operator|.
name|values
argument_list|()
control|)
block|{
name|cursor
operator|.
name|value
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|customs
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
name|IndexMetaData
operator|.
name|Custom
argument_list|>
name|cursor
range|:
name|customs
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|cursor
operator|.
name|key
argument_list|)
expr_stmt|;
name|cursor
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
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|VALID_FIELDS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|VALID_FIELDS
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"template"
argument_list|,
literal|"order"
argument_list|,
literal|"mappings"
argument_list|,
literal|"settings"
argument_list|)
decl_stmt|;
static|static
block|{
name|VALID_FIELDS
operator|.
name|addAll
argument_list|(
name|IndexMetaData
operator|.
name|customPrototypes
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|order
specifier|private
name|int
name|order
decl_stmt|;
DECL|field|template
specifier|private
name|String
name|template
decl_stmt|;
DECL|field|settings
specifier|private
name|Settings
name|settings
init|=
name|Settings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
decl_stmt|;
DECL|field|mappings
specifier|private
specifier|final
name|ImmutableOpenMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|CompressedXContent
argument_list|>
name|mappings
decl_stmt|;
DECL|field|aliases
specifier|private
specifier|final
name|ImmutableOpenMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|AliasMetaData
argument_list|>
name|aliases
decl_stmt|;
DECL|field|customs
specifier|private
specifier|final
name|ImmutableOpenMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|IndexMetaData
operator|.
name|Custom
argument_list|>
name|customs
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|mappings
operator|=
name|ImmutableOpenMap
operator|.
name|builder
argument_list|()
expr_stmt|;
name|aliases
operator|=
name|ImmutableOpenMap
operator|.
name|builder
argument_list|()
expr_stmt|;
name|customs
operator|=
name|ImmutableOpenMap
operator|.
name|builder
argument_list|()
expr_stmt|;
block|}
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|IndexTemplateMetaData
name|indexTemplateMetaData
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|indexTemplateMetaData
operator|.
name|name
argument_list|()
expr_stmt|;
name|order
argument_list|(
name|indexTemplateMetaData
operator|.
name|order
argument_list|()
argument_list|)
expr_stmt|;
name|template
argument_list|(
name|indexTemplateMetaData
operator|.
name|template
argument_list|()
argument_list|)
expr_stmt|;
name|settings
argument_list|(
name|indexTemplateMetaData
operator|.
name|settings
argument_list|()
argument_list|)
expr_stmt|;
name|mappings
operator|=
name|ImmutableOpenMap
operator|.
name|builder
argument_list|(
name|indexTemplateMetaData
operator|.
name|mappings
argument_list|()
argument_list|)
expr_stmt|;
name|aliases
operator|=
name|ImmutableOpenMap
operator|.
name|builder
argument_list|(
name|indexTemplateMetaData
operator|.
name|aliases
argument_list|()
argument_list|)
expr_stmt|;
name|customs
operator|=
name|ImmutableOpenMap
operator|.
name|builder
argument_list|(
name|indexTemplateMetaData
operator|.
name|customs
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|order
specifier|public
name|Builder
name|order
parameter_list|(
name|int
name|order
parameter_list|)
block|{
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|template
specifier|public
name|Builder
name|template
parameter_list|(
name|String
name|template
parameter_list|)
block|{
name|this
operator|.
name|template
operator|=
name|template
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|template
specifier|public
name|String
name|template
parameter_list|()
block|{
return|return
name|template
return|;
block|}
DECL|method|settings
specifier|public
name|Builder
name|settings
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|settings
specifier|public
name|Builder
name|settings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|removeMapping
specifier|public
name|Builder
name|removeMapping
parameter_list|(
name|String
name|mappingType
parameter_list|)
block|{
name|mappings
operator|.
name|remove
argument_list|(
name|mappingType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|putMapping
specifier|public
name|Builder
name|putMapping
parameter_list|(
name|String
name|mappingType
parameter_list|,
name|CompressedXContent
name|mappingSource
parameter_list|)
throws|throws
name|IOException
block|{
name|mappings
operator|.
name|put
argument_list|(
name|mappingType
argument_list|,
name|mappingSource
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|putMapping
specifier|public
name|Builder
name|putMapping
parameter_list|(
name|String
name|mappingType
parameter_list|,
name|String
name|mappingSource
parameter_list|)
throws|throws
name|IOException
block|{
name|mappings
operator|.
name|put
argument_list|(
name|mappingType
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mappingSource
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|putAlias
specifier|public
name|Builder
name|putAlias
parameter_list|(
name|AliasMetaData
name|aliasMetaData
parameter_list|)
block|{
name|aliases
operator|.
name|put
argument_list|(
name|aliasMetaData
operator|.
name|alias
argument_list|()
argument_list|,
name|aliasMetaData
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|putAlias
specifier|public
name|Builder
name|putAlias
parameter_list|(
name|AliasMetaData
operator|.
name|Builder
name|aliasMetaData
parameter_list|)
block|{
name|aliases
operator|.
name|put
argument_list|(
name|aliasMetaData
operator|.
name|alias
argument_list|()
argument_list|,
name|aliasMetaData
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|putCustom
specifier|public
name|Builder
name|putCustom
parameter_list|(
name|String
name|type
parameter_list|,
name|IndexMetaData
operator|.
name|Custom
name|customIndexMetaData
parameter_list|)
block|{
name|this
operator|.
name|customs
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|customIndexMetaData
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|removeCustom
specifier|public
name|Builder
name|removeCustom
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|customs
operator|.
name|remove
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getCustom
specifier|public
name|IndexMetaData
operator|.
name|Custom
name|getCustom
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|this
operator|.
name|customs
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
DECL|method|build
specifier|public
name|IndexTemplateMetaData
name|build
parameter_list|()
block|{
return|return
operator|new
name|IndexTemplateMetaData
argument_list|(
name|name
argument_list|,
name|order
argument_list|,
name|template
argument_list|,
name|settings
argument_list|,
name|mappings
operator|.
name|build
argument_list|()
argument_list|,
name|aliases
operator|.
name|build
argument_list|()
argument_list|,
name|customs
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|toXContent
specifier|public
specifier|static
name|void
name|toXContent
parameter_list|(
name|IndexTemplateMetaData
name|indexTemplateMetaData
parameter_list|,
name|XContentBuilder
name|builder
parameter_list|,
name|ToXContent
operator|.
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
name|indexTemplateMetaData
operator|.
name|name
argument_list|()
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"order"
argument_list|,
name|indexTemplateMetaData
operator|.
name|order
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"template"
argument_list|,
name|indexTemplateMetaData
operator|.
name|template
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"settings"
argument_list|)
expr_stmt|;
name|indexTemplateMetaData
operator|.
name|settings
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|paramAsBoolean
argument_list|(
literal|"reduce_mappings"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"mappings"
argument_list|)
expr_stmt|;
for|for
control|(
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|CompressedXContent
argument_list|>
name|cursor
range|:
name|indexTemplateMetaData
operator|.
name|mappings
argument_list|()
control|)
block|{
name|byte
index|[]
name|mappingSource
init|=
name|cursor
operator|.
name|value
operator|.
name|uncompressed
argument_list|()
decl_stmt|;
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|mappingSource
argument_list|)
operator|.
name|createParser
argument_list|(
name|mappingSource
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapping
init|=
name|parser
operator|.
name|map
argument_list|()
decl_stmt|;
if|if
condition|(
name|mapping
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|mapping
operator|.
name|containsKey
argument_list|(
name|cursor
operator|.
name|key
argument_list|)
condition|)
block|{
comment|// the type name is the root value, reduce it
name|mapping
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|mapping
operator|.
name|get
argument_list|(
name|cursor
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
name|cursor
operator|.
name|key
argument_list|)
expr_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startArray
argument_list|(
literal|"mappings"
argument_list|)
expr_stmt|;
for|for
control|(
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|CompressedXContent
argument_list|>
name|cursor
range|:
name|indexTemplateMetaData
operator|.
name|mappings
argument_list|()
control|)
block|{
name|byte
index|[]
name|data
init|=
name|cursor
operator|.
name|value
operator|.
name|uncompressed
argument_list|()
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|data
argument_list|)
operator|.
name|createParser
argument_list|(
name|data
argument_list|)
init|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapping
init|=
name|parser
operator|.
name|mapOrdered
argument_list|()
decl_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|IndexMetaData
operator|.
name|Custom
argument_list|>
name|cursor
range|:
name|indexTemplateMetaData
operator|.
name|customs
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|cursor
operator|.
name|key
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|cursor
operator|.
name|value
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
literal|"aliases"
argument_list|)
expr_stmt|;
for|for
control|(
name|ObjectCursor
argument_list|<
name|AliasMetaData
argument_list|>
name|cursor
range|:
name|indexTemplateMetaData
operator|.
name|aliases
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|AliasMetaData
operator|.
name|Builder
operator|.
name|toXContent
argument_list|(
name|cursor
operator|.
name|value
argument_list|,
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|IndexTemplateMetaData
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|String
name|templateName
parameter_list|)
throws|throws
name|IOException
block|{
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|(
name|templateName
argument_list|)
decl_stmt|;
name|String
name|currentFieldName
init|=
name|skipTemplateName
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
literal|"settings"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|Settings
operator|.
name|Builder
name|templateSettingsBuilder
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
decl_stmt|;
name|templateSettingsBuilder
operator|.
name|put
argument_list|(
name|SettingsLoader
operator|.
name|Helper
operator|.
name|loadNestedFromMap
argument_list|(
name|parser
operator|.
name|mapOrdered
argument_list|()
argument_list|)
argument_list|)
operator|.
name|normalizePrefix
argument_list|(
name|IndexMetaData
operator|.
name|INDEX_SETTING_PREFIX
argument_list|)
expr_stmt|;
name|builder
operator|.
name|settings
argument_list|(
name|templateSettingsBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"mappings"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|String
name|mappingType
init|=
name|currentFieldName
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mappingSource
init|=
name|MapBuilder
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|newMapBuilder
argument_list|()
decl|.
name|put
argument_list|(
name|mappingType
argument_list|,
name|parser
operator|.
name|mapOrdered
argument_list|()
argument_list|)
decl|.
name|map
argument_list|()
decl_stmt|;
name|builder
operator|.
name|putMapping
argument_list|(
name|mappingType
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|map
argument_list|(
name|mappingSource
argument_list|)
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"aliases"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
name|builder
operator|.
name|putAlias
argument_list|(
name|AliasMetaData
operator|.
name|Builder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// check if its a custom index metadata
name|IndexMetaData
operator|.
name|Custom
name|proto
init|=
name|IndexMetaData
operator|.
name|lookupPrototype
argument_list|(
name|currentFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|proto
operator|==
literal|null
condition|)
block|{
comment|//TODO warn
name|parser
operator|.
name|skipChildren
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|IndexMetaData
operator|.
name|Custom
name|custom
init|=
name|proto
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|builder
operator|.
name|putCustom
argument_list|(
name|custom
operator|.
name|type
argument_list|()
argument_list|,
name|custom
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"mappings"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapping
init|=
name|parser
operator|.
name|mapOrdered
argument_list|()
decl_stmt|;
if|if
condition|(
name|mapping
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|String
name|mappingType
init|=
name|mapping
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|mappingSource
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|map
argument_list|(
name|mapping
argument_list|)
operator|.
name|string
argument_list|()
decl_stmt|;
if|if
condition|(
name|mappingSource
operator|==
literal|null
condition|)
block|{
comment|// crap, no mapping source, warn?
block|}
else|else
block|{
name|builder
operator|.
name|putMapping
argument_list|(
name|mappingType
argument_list|,
name|mappingSource
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"template"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|template
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"order"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|order
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|skipTemplateName
specifier|private
specifier|static
name|String
name|skipTemplateName
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|!=
literal|null
operator|&&
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|String
name|currentFieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
if|if
condition|(
name|VALID_FIELDS
operator|.
name|contains
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
return|return
name|currentFieldName
return|;
block|}
else|else
block|{
comment|// we just hit the template name, which should be ignored and we move on
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|readFrom
specifier|public
specifier|static
name|IndexTemplateMetaData
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|PROTO
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

