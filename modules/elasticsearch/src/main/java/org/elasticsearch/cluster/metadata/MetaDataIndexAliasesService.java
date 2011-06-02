begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|ClusterService
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
name|ClusterState
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
name|ProcessedClusterStateUpdateTask
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
name|collect
operator|.
name|Lists
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
name|Maps
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
name|component
operator|.
name|AbstractComponent
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
name|inject
operator|.
name|Inject
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|Index
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|xcontent
operator|.
name|XContentIndexQueryParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|service
operator|.
name|IndexService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndexMissingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|InvalidAliasNameException
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
name|cluster
operator|.
name|ClusterState
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
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|cluster
operator|.
name|metadata
operator|.
name|MetaData
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|MetaDataIndexAliasesService
specifier|public
class|class
name|MetaDataIndexAliasesService
extends|extends
name|AbstractComponent
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|method|MetaDataIndexAliasesService
annotation|@
name|Inject
specifier|public
name|MetaDataIndexAliasesService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
expr_stmt|;
block|}
DECL|method|indicesAliases
specifier|public
name|void
name|indicesAliases
parameter_list|(
specifier|final
name|Request
name|request
parameter_list|,
specifier|final
name|Listener
name|listener
parameter_list|)
block|{
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"index-aliases"
argument_list|,
operator|new
name|ProcessedClusterStateUpdateTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ClusterState
name|execute
parameter_list|(
name|ClusterState
name|currentState
parameter_list|)
block|{
for|for
control|(
name|AliasAction
name|aliasAction
range|:
name|request
operator|.
name|actions
control|)
block|{
if|if
condition|(
operator|!
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|aliasAction
operator|.
name|index
argument_list|()
argument_list|)
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|aliasAction
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|currentState
return|;
block|}
if|if
condition|(
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|aliasAction
operator|.
name|alias
argument_list|()
argument_list|)
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|InvalidAliasNameException
argument_list|(
operator|new
name|Index
argument_list|(
name|aliasAction
operator|.
name|index
argument_list|()
argument_list|)
argument_list|,
name|aliasAction
operator|.
name|alias
argument_list|()
argument_list|,
literal|"an index exists with the same name as the alias"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|currentState
return|;
block|}
block|}
name|List
argument_list|<
name|String
argument_list|>
name|indicesToClose
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|IndexService
argument_list|>
name|indices
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
try|try
block|{
name|MetaData
operator|.
name|Builder
name|builder
init|=
name|newMetaDataBuilder
argument_list|()
operator|.
name|metaData
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|AliasAction
name|aliasAction
range|:
name|request
operator|.
name|actions
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|builder
operator|.
name|get
argument_list|(
name|aliasAction
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|aliasAction
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|IndexMetaData
operator|.
name|Builder
name|indexMetaDataBuilder
init|=
name|newIndexMetaDataBuilder
argument_list|(
name|indexMetaData
argument_list|)
decl_stmt|;
if|if
condition|(
name|aliasAction
operator|.
name|actionType
argument_list|()
operator|==
name|AliasAction
operator|.
name|Type
operator|.
name|ADD
condition|)
block|{
name|String
name|filter
init|=
name|aliasAction
operator|.
name|filter
argument_list|()
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|filter
argument_list|)
condition|)
block|{
comment|// parse the filter, in order to validate it
name|IndexService
name|indexService
init|=
name|indices
operator|.
name|get
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexService
operator|==
literal|null
condition|)
block|{
name|indexService
operator|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexService
operator|==
literal|null
condition|)
block|{
comment|// temporarily create the index so we have can parse the filter
name|indexService
operator|=
name|indicesService
operator|.
name|createIndex
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|,
name|indexMetaData
operator|.
name|settings
argument_list|()
argument_list|,
name|currentState
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|indicesToClose
operator|.
name|add
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|indices
operator|.
name|put
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|,
name|indexService
argument_list|)
expr_stmt|;
block|}
comment|// now, parse the filter
name|XContentIndexQueryParser
name|indexQueryParser
init|=
operator|(
name|XContentIndexQueryParser
operator|)
name|indexService
operator|.
name|queryParserService
argument_list|()
operator|.
name|defaultIndexQueryParser
argument_list|()
decl_stmt|;
try|try
block|{
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|filter
argument_list|)
operator|.
name|createParser
argument_list|(
name|filter
argument_list|)
decl_stmt|;
try|try
block|{
name|indexQueryParser
operator|.
name|parseInnerFilter
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"failed to parse filter for ["
operator|+
name|aliasAction
operator|.
name|alias
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|currentState
return|;
block|}
block|}
name|indexMetaDataBuilder
operator|.
name|putAlias
argument_list|(
name|AliasMetaData
operator|.
name|newAliasMetaDataBuilder
argument_list|(
name|aliasAction
operator|.
name|alias
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|filter
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|aliasAction
operator|.
name|actionType
argument_list|()
operator|==
name|AliasAction
operator|.
name|Type
operator|.
name|REMOVE
condition|)
block|{
name|indexMetaDataBuilder
operator|.
name|removerAlias
argument_list|(
name|aliasAction
operator|.
name|alias
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|put
argument_list|(
name|indexMetaDataBuilder
argument_list|)
expr_stmt|;
block|}
return|return
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|metaData
argument_list|(
name|builder
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
finally|finally
block|{
for|for
control|(
name|String
name|index
range|:
name|indicesToClose
control|)
block|{
name|indicesService
operator|.
name|cleanIndex
argument_list|(
name|index
argument_list|,
literal|"created for mapping processing"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|clusterStateProcessed
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|Response
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|interface|Listener
specifier|public
specifier|static
interface|interface
name|Listener
block|{
DECL|method|onResponse
name|void
name|onResponse
parameter_list|(
name|Response
name|response
parameter_list|)
function_decl|;
DECL|method|onFailure
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
DECL|class|Request
specifier|public
specifier|static
class|class
name|Request
block|{
DECL|field|actions
specifier|final
name|AliasAction
index|[]
name|actions
decl_stmt|;
DECL|method|Request
specifier|public
name|Request
parameter_list|(
name|AliasAction
index|[]
name|actions
parameter_list|)
block|{
name|this
operator|.
name|actions
operator|=
name|actions
expr_stmt|;
block|}
block|}
DECL|class|Response
specifier|public
specifier|static
class|class
name|Response
block|{      }
block|}
end_class

end_unit

