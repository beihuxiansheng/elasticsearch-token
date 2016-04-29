begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
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
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|MetaData
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
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentCollections
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
name|NodeEnvironment
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableMap
import|;
end_import

begin_comment
comment|/**  * The dangling indices state is responsible for finding new dangling indices (indices that have  * their state written on disk, but don't exists in the metadata of the cluster), and importing  * them into the cluster.  */
end_comment

begin_class
DECL|class|DanglingIndicesState
specifier|public
class|class
name|DanglingIndicesState
extends|extends
name|AbstractComponent
block|{
DECL|field|nodeEnv
specifier|private
specifier|final
name|NodeEnvironment
name|nodeEnv
decl_stmt|;
DECL|field|metaStateService
specifier|private
specifier|final
name|MetaStateService
name|metaStateService
decl_stmt|;
DECL|field|allocateDangledIndices
specifier|private
specifier|final
name|LocalAllocateDangledIndices
name|allocateDangledIndices
decl_stmt|;
DECL|field|danglingIndices
specifier|private
specifier|final
name|Map
argument_list|<
name|Index
argument_list|,
name|IndexMetaData
argument_list|>
name|danglingIndices
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|DanglingIndicesState
specifier|public
name|DanglingIndicesState
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeEnvironment
name|nodeEnv
parameter_list|,
name|MetaStateService
name|metaStateService
parameter_list|,
name|LocalAllocateDangledIndices
name|allocateDangledIndices
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeEnv
operator|=
name|nodeEnv
expr_stmt|;
name|this
operator|.
name|metaStateService
operator|=
name|metaStateService
expr_stmt|;
name|this
operator|.
name|allocateDangledIndices
operator|=
name|allocateDangledIndices
expr_stmt|;
block|}
comment|/**      * Process dangling indices based on the provided meta data, handling cleanup, finding      * new dangling indices, and allocating outstanding ones.      */
DECL|method|processDanglingIndices
specifier|public
name|void
name|processDanglingIndices
parameter_list|(
name|MetaData
name|metaData
parameter_list|)
block|{
if|if
condition|(
name|nodeEnv
operator|.
name|hasNodeFile
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return;
block|}
name|cleanupAllocatedDangledIndices
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
name|findNewAndAddDanglingIndices
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
name|allocateDanglingIndices
argument_list|()
expr_stmt|;
block|}
comment|/**      * The current set of dangling indices.      */
DECL|method|getDanglingIndices
name|Map
argument_list|<
name|Index
argument_list|,
name|IndexMetaData
argument_list|>
name|getDanglingIndices
parameter_list|()
block|{
comment|// This might be a good use case for CopyOnWriteHashMap
return|return
name|unmodifiableMap
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|danglingIndices
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Cleans dangling indices if they are already allocated on the provided meta data.      */
DECL|method|cleanupAllocatedDangledIndices
name|void
name|cleanupAllocatedDangledIndices
parameter_list|(
name|MetaData
name|metaData
parameter_list|)
block|{
for|for
control|(
name|Index
name|index
range|:
name|danglingIndices
operator|.
name|keySet
argument_list|()
control|)
block|{
specifier|final
name|IndexMetaData
name|indexMetaData
init|=
name|metaData
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|!=
literal|null
operator|&&
name|indexMetaData
operator|.
name|getIndex
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|index
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|indexMetaData
operator|.
name|getIndex
argument_list|()
operator|.
name|getUUID
argument_list|()
operator|.
name|equals
argument_list|(
name|index
operator|.
name|getUUID
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[{}] can not be imported as a dangling index, as there is already another index "
operator|+
literal|"with the same name but a different uuid. local index will be ignored (but not deleted)"
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] no longer dangling (created), removing from dangling list"
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
name|danglingIndices
operator|.
name|remove
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Finds (@{link #findNewAndAddDanglingIndices}) and adds the new dangling indices      * to the currently tracked dangling indices.      */
DECL|method|findNewAndAddDanglingIndices
name|void
name|findNewAndAddDanglingIndices
parameter_list|(
name|MetaData
name|metaData
parameter_list|)
block|{
name|danglingIndices
operator|.
name|putAll
argument_list|(
name|findNewDanglingIndices
argument_list|(
name|metaData
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Finds new dangling indices by iterating over the indices and trying to find indices      * that have state on disk, but are not part of the provided meta data, or not detected      * as dangled already.      */
DECL|method|findNewDanglingIndices
name|Map
argument_list|<
name|Index
argument_list|,
name|IndexMetaData
argument_list|>
name|findNewDanglingIndices
parameter_list|(
name|MetaData
name|metaData
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludeIndexPathIds
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|metaData
operator|.
name|indices
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|danglingIndices
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ObjectCursor
argument_list|<
name|IndexMetaData
argument_list|>
name|cursor
range|:
name|metaData
operator|.
name|indices
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|excludeIndexPathIds
operator|.
name|add
argument_list|(
name|cursor
operator|.
name|value
operator|.
name|getIndex
argument_list|()
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|excludeIndexPathIds
operator|.
name|addAll
argument_list|(
name|danglingIndices
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Index
operator|::
name|getUUID
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|IndexMetaData
argument_list|>
name|indexMetaDataList
init|=
name|metaStateService
operator|.
name|loadIndicesStates
argument_list|(
name|excludeIndexPathIds
operator|::
name|contains
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Index
argument_list|,
name|IndexMetaData
argument_list|>
name|newIndices
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|indexMetaDataList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexMetaData
name|indexMetaData
range|:
name|indexMetaDataList
control|)
block|{
if|if
condition|(
name|metaData
operator|.
name|hasIndex
argument_list|(
name|indexMetaData
operator|.
name|getIndex
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[{}] can not be imported as a dangling index, as index with same name already exists in cluster metadata"
argument_list|,
name|indexMetaData
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] dangling index, exists on local file system, but not in cluster metadata, auto import to cluster state"
argument_list|,
name|indexMetaData
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|newIndices
operator|.
name|put
argument_list|(
name|indexMetaData
operator|.
name|getIndex
argument_list|()
argument_list|,
name|indexMetaData
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|newIndices
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to list dangling indices"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|emptyMap
argument_list|()
return|;
block|}
block|}
comment|/**      * Allocates the provided list of the dangled indices by sending them to the master node      * for allocation.      */
DECL|method|allocateDanglingIndices
specifier|private
name|void
name|allocateDanglingIndices
parameter_list|()
block|{
if|if
condition|(
name|danglingIndices
operator|.
name|isEmpty
argument_list|()
operator|==
literal|true
condition|)
block|{
return|return;
block|}
try|try
block|{
name|allocateDangledIndices
operator|.
name|allocateDangled
argument_list|(
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|danglingIndices
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|,
operator|new
name|LocalAllocateDangledIndices
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|LocalAllocateDangledIndices
operator|.
name|AllocateDangledResponse
name|response
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"allocated dangled"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"failed to send allocated dangled"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to send allocate dangled"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

