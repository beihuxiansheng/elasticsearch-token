begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|status
operator|.
name|TransportNodesSnapshotsStatus
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
name|AbstractModule
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
name|multibindings
operator|.
name|MapBinder
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
name|NamedXContentRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|RepositoryPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|fs
operator|.
name|FsRepository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
operator|.
name|RestoreService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
operator|.
name|SnapshotShardsService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
operator|.
name|SnapshotsService
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
name|HashMap
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

begin_comment
comment|/**  * Sets up classes for Snapshot/Restore.  */
end_comment

begin_class
DECL|class|RepositoriesModule
specifier|public
class|class
name|RepositoriesModule
extends|extends
name|AbstractModule
block|{
DECL|field|repositoryTypes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Repository
operator|.
name|Factory
argument_list|>
name|repositoryTypes
decl_stmt|;
DECL|method|RepositoriesModule
specifier|public
name|RepositoriesModule
parameter_list|(
name|Environment
name|env
parameter_list|,
name|List
argument_list|<
name|RepositoryPlugin
argument_list|>
name|repoPlugins
parameter_list|,
name|NamedXContentRegistry
name|namedXContentRegistry
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Repository
operator|.
name|Factory
argument_list|>
name|factories
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|factories
operator|.
name|put
argument_list|(
name|FsRepository
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|metadata
parameter_list|)
lambda|->
operator|new
name|FsRepository
argument_list|(
name|metadata
argument_list|,
name|env
argument_list|,
name|namedXContentRegistry
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|RepositoryPlugin
name|repoPlugin
range|:
name|repoPlugins
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Repository
operator|.
name|Factory
argument_list|>
name|newRepoTypes
init|=
name|repoPlugin
operator|.
name|getRepositories
argument_list|(
name|env
argument_list|,
name|namedXContentRegistry
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Repository
operator|.
name|Factory
argument_list|>
name|entry
range|:
name|newRepoTypes
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|factories
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Repository type ["
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"] is already registered"
argument_list|)
throw|;
block|}
block|}
block|}
name|repositoryTypes
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|factories
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|RepositoriesService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|SnapshotsService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|SnapshotShardsService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|TransportNodesSnapshotsStatus
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|RestoreService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|MapBinder
argument_list|<
name|String
argument_list|,
name|Repository
operator|.
name|Factory
argument_list|>
name|typesBinder
init|=
name|MapBinder
operator|.
name|newMapBinder
argument_list|(
name|binder
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Repository
operator|.
name|Factory
operator|.
name|class
argument_list|)
decl_stmt|;
name|repositoryTypes
operator|.
name|forEach
argument_list|(
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
name|typesBinder
operator|.
name|addBinding
argument_list|(
name|k
argument_list|)
operator|.
name|toInstance
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

