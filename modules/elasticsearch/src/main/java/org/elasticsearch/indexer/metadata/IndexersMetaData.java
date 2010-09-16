begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indexer.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indexer
operator|.
name|metadata
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
name|ImmutableMap
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
name|xcontent
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indexer
operator|.
name|IndexerName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Iterator
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|IndexersMetaData
specifier|public
class|class
name|IndexersMetaData
implements|implements
name|Iterable
argument_list|<
name|IndexerMetaData
argument_list|>
block|{
DECL|field|indexers
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|IndexerName
argument_list|,
name|IndexerMetaData
argument_list|>
name|indexers
decl_stmt|;
DECL|field|recoveredFromGateway
specifier|private
specifier|final
name|boolean
name|recoveredFromGateway
decl_stmt|;
DECL|method|IndexersMetaData
specifier|private
name|IndexersMetaData
parameter_list|(
name|ImmutableMap
argument_list|<
name|IndexerName
argument_list|,
name|IndexerMetaData
argument_list|>
name|indexers
parameter_list|,
name|boolean
name|recoveredFromGateway
parameter_list|)
block|{
name|this
operator|.
name|indexers
operator|=
name|indexers
expr_stmt|;
name|this
operator|.
name|recoveredFromGateway
operator|=
name|recoveredFromGateway
expr_stmt|;
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|IndexerMetaData
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|indexers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|indexer
specifier|public
name|IndexerMetaData
name|indexer
parameter_list|(
name|IndexerName
name|indexerName
parameter_list|)
block|{
return|return
name|indexers
operator|.
name|get
argument_list|(
name|indexerName
argument_list|)
return|;
block|}
DECL|method|recoveredFromGateway
specifier|public
name|boolean
name|recoveredFromGateway
parameter_list|()
block|{
return|return
name|recoveredFromGateway
return|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|indexers
specifier|private
name|MapBuilder
argument_list|<
name|IndexerName
argument_list|,
name|IndexerMetaData
argument_list|>
name|indexers
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
DECL|field|recoveredFromGateway
specifier|private
name|boolean
name|recoveredFromGateway
init|=
literal|false
decl_stmt|;
DECL|method|put
specifier|public
name|Builder
name|put
parameter_list|(
name|IndexerMetaData
operator|.
name|Builder
name|builder
parameter_list|)
block|{
return|return
name|put
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|put
specifier|public
name|Builder
name|put
parameter_list|(
name|IndexerMetaData
name|indexerMetaData
parameter_list|)
block|{
name|indexers
operator|.
name|put
argument_list|(
name|indexerMetaData
operator|.
name|indexerName
argument_list|()
argument_list|,
name|indexerMetaData
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|get
specifier|public
name|IndexerMetaData
name|get
parameter_list|(
name|IndexerName
name|indexerName
parameter_list|)
block|{
return|return
name|indexers
operator|.
name|get
argument_list|(
name|indexerName
argument_list|)
return|;
block|}
DECL|method|remove
specifier|public
name|Builder
name|remove
parameter_list|(
name|IndexerName
name|indexerName
parameter_list|)
block|{
name|indexers
operator|.
name|remove
argument_list|(
name|indexerName
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|metaData
specifier|public
name|Builder
name|metaData
parameter_list|(
name|IndexersMetaData
name|metaData
parameter_list|)
block|{
name|this
operator|.
name|indexers
operator|.
name|putAll
argument_list|(
name|metaData
operator|.
name|indexers
argument_list|)
expr_stmt|;
name|this
operator|.
name|recoveredFromGateway
operator|=
name|metaData
operator|.
name|recoveredFromGateway
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Indicates that this cluster state has been recovered from the gateawy.          */
DECL|method|markAsRecoveredFromGateway
specifier|public
name|Builder
name|markAsRecoveredFromGateway
parameter_list|()
block|{
name|this
operator|.
name|recoveredFromGateway
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|IndexersMetaData
name|build
parameter_list|()
block|{
return|return
operator|new
name|IndexersMetaData
argument_list|(
name|indexers
operator|.
name|immutableMap
argument_list|()
argument_list|,
name|recoveredFromGateway
argument_list|)
return|;
block|}
DECL|method|toXContent
specifier|public
specifier|static
name|String
name|toXContent
parameter_list|(
name|IndexersMetaData
name|metaData
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|toXContent
argument_list|(
name|metaData
argument_list|,
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|string
argument_list|()
return|;
block|}
DECL|method|toXContent
specifier|public
specifier|static
name|void
name|toXContent
parameter_list|(
name|IndexersMetaData
name|metaData
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
literal|"meta-data"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"indexers"
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexerMetaData
name|indexMetaData
range|:
name|metaData
control|)
block|{
name|IndexerMetaData
operator|.
name|Builder
operator|.
name|toXContent
argument_list|(
name|indexMetaData
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
name|IndexersMetaData
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
annotation|@
name|Nullable
name|Settings
name|globalSettings
parameter_list|)
throws|throws
name|IOException
block|{
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|currentToken
argument_list|()
decl_stmt|;
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
operator|!
literal|"meta-data"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
comment|// no data...
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
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
literal|"indexers"
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
name|put
argument_list|(
name|IndexerMetaData
operator|.
name|Builder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|,
name|globalSettings
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
DECL|method|readFrom
specifier|public
specifier|static
name|IndexersMetaData
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|,
annotation|@
name|Nullable
name|Settings
name|globalSettings
parameter_list|)
throws|throws
name|IOException
block|{
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|()
decl_stmt|;
comment|// we only serialize it using readFrom, not in to/from XContent
name|builder
operator|.
name|recoveredFromGateway
operator|=
name|in
operator|.
name|readBoolean
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
name|builder
operator|.
name|put
argument_list|(
name|IndexerMetaData
operator|.
name|Builder
operator|.
name|readFrom
argument_list|(
name|in
argument_list|,
name|globalSettings
argument_list|)
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
DECL|method|writeTo
specifier|public
specifier|static
name|void
name|writeTo
parameter_list|(
name|IndexersMetaData
name|metaData
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
name|metaData
operator|.
name|recoveredFromGateway
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|metaData
operator|.
name|indexers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexerMetaData
name|indexMetaData
range|:
name|metaData
control|)
block|{
name|IndexerMetaData
operator|.
name|Builder
operator|.
name|writeTo
argument_list|(
name|indexMetaData
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

