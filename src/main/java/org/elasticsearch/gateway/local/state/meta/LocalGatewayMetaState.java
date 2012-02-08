begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.local.state.meta
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|local
operator|.
name|state
operator|.
name|meta
package|;
end_package

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
name|Maps
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
name|io
operator|.
name|Closeables
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
name|cluster
operator|.
name|ClusterChangedEvent
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
name|ClusterStateListener
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
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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
name|Nullable
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
name|io
operator|.
name|FileSystemUtils
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
name|Streams
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
name|CachedStreamOutput
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
name|ImmutableSettings
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
name|unit
operator|.
name|TimeValue
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
DECL|class|LocalGatewayMetaState
specifier|public
class|class
name|LocalGatewayMetaState
extends|extends
name|AbstractComponent
implements|implements
name|ClusterStateListener
block|{
DECL|field|nodeEnv
specifier|private
specifier|final
name|NodeEnvironment
name|nodeEnv
decl_stmt|;
DECL|field|currentMetaData
specifier|private
specifier|volatile
name|MetaData
name|currentMetaData
decl_stmt|;
DECL|field|format
specifier|private
specifier|final
name|XContentType
name|format
decl_stmt|;
DECL|field|formatParams
specifier|private
specifier|final
name|ToXContent
operator|.
name|Params
name|formatParams
decl_stmt|;
annotation|@
name|Inject
DECL|method|LocalGatewayMetaState
specifier|public
name|LocalGatewayMetaState
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeEnvironment
name|nodeEnv
parameter_list|,
name|TransportNodesListGatewayMetaState
name|nodesListGatewayMetaState
parameter_list|)
throws|throws
name|Exception
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
name|format
operator|=
name|XContentType
operator|.
name|fromRestContentType
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"format"
argument_list|,
literal|"smile"
argument_list|)
argument_list|)
expr_stmt|;
name|nodesListGatewayMetaState
operator|.
name|init
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|format
operator|==
name|XContentType
operator|.
name|SMILE
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"binary"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|formatParams
operator|=
operator|new
name|ToXContent
operator|.
name|MapParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|formatParams
operator|=
name|ToXContent
operator|.
name|EMPTY_PARAMS
expr_stmt|;
block|}
if|if
condition|(
name|DiscoveryNode
operator|.
name|masterNode
argument_list|(
name|settings
argument_list|)
condition|)
block|{
try|try
block|{
name|pre019Upgrade
argument_list|()
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|loadState
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"took {} to load state"
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"failed to read local state, exiting..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
DECL|method|currentMetaData
specifier|public
name|MetaData
name|currentMetaData
parameter_list|()
block|{
return|return
name|currentMetaData
return|;
block|}
annotation|@
name|Override
DECL|method|clusterChanged
specifier|public
name|void
name|clusterChanged
parameter_list|(
name|ClusterChangedEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|blocks
argument_list|()
operator|.
name|disableStatePersistence
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|event
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
operator|.
name|masterNode
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|event
operator|.
name|metaDataChanged
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// check if the global state changed?
name|boolean
name|success
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|currentMetaData
operator|==
literal|null
operator|||
operator|!
name|MetaData
operator|.
name|isGlobalStateEquals
argument_list|(
name|currentMetaData
argument_list|,
name|event
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|writeGlobalState
argument_list|(
literal|"changed"
argument_list|,
name|event
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
argument_list|,
name|currentMetaData
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|success
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|// check and write changes in indices
for|for
control|(
name|IndexMetaData
name|indexMetaData
range|:
name|event
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
control|)
block|{
name|String
name|writeReason
init|=
literal|null
decl_stmt|;
name|IndexMetaData
name|currentIndexMetaData
init|=
name|currentMetaData
operator|==
literal|null
condition|?
literal|null
else|:
name|currentMetaData
operator|.
name|index
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentIndexMetaData
operator|==
literal|null
condition|)
block|{
name|writeReason
operator|=
literal|"freshly created"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentIndexMetaData
operator|.
name|version
argument_list|()
operator|!=
name|indexMetaData
operator|.
name|version
argument_list|()
condition|)
block|{
name|writeReason
operator|=
literal|"version changed from ["
operator|+
name|currentIndexMetaData
operator|.
name|version
argument_list|()
operator|+
literal|"] to ["
operator|+
name|indexMetaData
operator|.
name|version
argument_list|()
operator|+
literal|"]"
expr_stmt|;
block|}
comment|// we update the writeReason only if we really need to write it
if|if
condition|(
name|writeReason
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|writeIndex
argument_list|(
name|writeReason
argument_list|,
name|indexMetaData
argument_list|,
name|currentIndexMetaData
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|success
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|// delete indices that are no longer there...
if|if
condition|(
name|currentMetaData
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|IndexMetaData
name|current
range|:
name|currentMetaData
control|)
block|{
if|if
condition|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|current
operator|.
name|index
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
name|deleteIndex
argument_list|(
name|current
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|success
condition|)
block|{
name|currentMetaData
operator|=
name|event
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|deleteIndex
specifier|private
name|void
name|deleteIndex
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[{}] delete index state"
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|File
index|[]
name|indexLocations
init|=
name|nodeEnv
operator|.
name|indexLocations
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|indexLocation
range|:
name|indexLocations
control|)
block|{
if|if
condition|(
operator|!
name|indexLocation
operator|.
name|exists
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|FileSystemUtils
operator|.
name|deleteRecursively
argument_list|(
operator|new
name|File
argument_list|(
name|indexLocation
argument_list|,
literal|"_state"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeIndex
specifier|private
name|void
name|writeIndex
parameter_list|(
name|String
name|reason
parameter_list|,
name|IndexMetaData
name|indexMetaData
parameter_list|,
annotation|@
name|Nullable
name|IndexMetaData
name|previousIndexMetaData
parameter_list|)
throws|throws
name|Exception
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[{}] writing state, reason [{}]"
argument_list|,
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|,
name|reason
argument_list|)
expr_stmt|;
name|CachedStreamOutput
operator|.
name|Entry
name|cachedEntry
init|=
name|CachedStreamOutput
operator|.
name|popEntry
argument_list|()
decl_stmt|;
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|format
argument_list|,
name|cachedEntry
operator|.
name|cachedBytes
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|IndexMetaData
operator|.
name|Builder
operator|.
name|toXContent
argument_list|(
name|indexMetaData
argument_list|,
name|builder
argument_list|,
name|formatParams
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|flush
argument_list|()
expr_stmt|;
name|Exception
name|lastFailure
init|=
literal|null
decl_stmt|;
name|boolean
name|wroteAtLeastOnce
init|=
literal|false
decl_stmt|;
for|for
control|(
name|File
name|indexLocation
range|:
name|nodeEnv
operator|.
name|indexLocations
argument_list|(
operator|new
name|Index
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
control|)
block|{
name|File
name|stateLocation
init|=
operator|new
name|File
argument_list|(
name|indexLocation
argument_list|,
literal|"_state"
argument_list|)
decl_stmt|;
name|FileSystemUtils
operator|.
name|mkdirs
argument_list|(
name|stateLocation
argument_list|)
expr_stmt|;
name|File
name|stateFile
init|=
operator|new
name|File
argument_list|(
name|stateLocation
argument_list|,
literal|"state-"
operator|+
name|indexMetaData
operator|.
name|version
argument_list|()
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fos
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fos
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|stateFile
argument_list|)
expr_stmt|;
name|fos
operator|.
name|write
argument_list|(
name|cachedEntry
operator|.
name|bytes
argument_list|()
operator|.
name|underlyingBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|cachedEntry
operator|.
name|bytes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|getChannel
argument_list|()
operator|.
name|force
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Closeables
operator|.
name|closeQuietly
argument_list|(
name|fos
argument_list|)
expr_stmt|;
name|wroteAtLeastOnce
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|lastFailure
operator|=
name|e
expr_stmt|;
block|}
finally|finally
block|{
name|Closeables
operator|.
name|closeQuietly
argument_list|(
name|fos
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|wroteAtLeastOnce
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[{}]: failed to state"
argument_list|,
name|lastFailure
argument_list|,
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"failed to write state for ["
operator|+
name|indexMetaData
operator|.
name|index
argument_list|()
operator|+
literal|"]"
argument_list|,
name|lastFailure
argument_list|)
throw|;
block|}
comment|// delete the old files
if|if
condition|(
name|previousIndexMetaData
operator|!=
literal|null
operator|&&
name|previousIndexMetaData
operator|.
name|version
argument_list|()
operator|!=
name|indexMetaData
operator|.
name|version
argument_list|()
condition|)
block|{
for|for
control|(
name|File
name|indexLocation
range|:
name|nodeEnv
operator|.
name|indexLocations
argument_list|(
operator|new
name|Index
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
control|)
block|{
name|File
name|stateFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|indexLocation
argument_list|,
literal|"_state"
argument_list|)
argument_list|,
literal|"state-"
operator|+
name|previousIndexMetaData
operator|.
name|version
argument_list|()
argument_list|)
decl_stmt|;
name|stateFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|CachedStreamOutput
operator|.
name|pushEntry
argument_list|(
name|cachedEntry
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeGlobalState
specifier|private
name|void
name|writeGlobalState
parameter_list|(
name|String
name|reason
parameter_list|,
name|MetaData
name|metaData
parameter_list|,
annotation|@
name|Nullable
name|MetaData
name|previousMetaData
parameter_list|)
throws|throws
name|Exception
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[_global] writing state, reason [{}]"
argument_list|,
name|reason
argument_list|)
expr_stmt|;
comment|// create metadata to write with just the global state
name|MetaData
name|globalMetaData
init|=
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|removeAllIndices
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|CachedStreamOutput
operator|.
name|Entry
name|cachedEntry
init|=
name|CachedStreamOutput
operator|.
name|popEntry
argument_list|()
decl_stmt|;
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|format
argument_list|,
name|cachedEntry
operator|.
name|cachedBytes
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|MetaData
operator|.
name|Builder
operator|.
name|toXContent
argument_list|(
name|globalMetaData
argument_list|,
name|builder
argument_list|,
name|formatParams
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|flush
argument_list|()
expr_stmt|;
name|Exception
name|lastFailure
init|=
literal|null
decl_stmt|;
name|boolean
name|wroteAtLeastOnce
init|=
literal|false
decl_stmt|;
for|for
control|(
name|File
name|dataLocation
range|:
name|nodeEnv
operator|.
name|nodeDataLocations
argument_list|()
control|)
block|{
name|File
name|stateLocation
init|=
operator|new
name|File
argument_list|(
name|dataLocation
argument_list|,
literal|"_state"
argument_list|)
decl_stmt|;
name|FileSystemUtils
operator|.
name|mkdirs
argument_list|(
name|stateLocation
argument_list|)
expr_stmt|;
name|File
name|stateFile
init|=
operator|new
name|File
argument_list|(
name|stateLocation
argument_list|,
literal|"global-"
operator|+
name|globalMetaData
operator|.
name|version
argument_list|()
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fos
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fos
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|stateFile
argument_list|)
expr_stmt|;
name|fos
operator|.
name|write
argument_list|(
name|cachedEntry
operator|.
name|bytes
argument_list|()
operator|.
name|underlyingBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|cachedEntry
operator|.
name|bytes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|getChannel
argument_list|()
operator|.
name|force
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Closeables
operator|.
name|closeQuietly
argument_list|(
name|fos
argument_list|)
expr_stmt|;
name|wroteAtLeastOnce
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|lastFailure
operator|=
name|e
expr_stmt|;
block|}
finally|finally
block|{
name|Closeables
operator|.
name|closeQuietly
argument_list|(
name|fos
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|wroteAtLeastOnce
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[_global]: failed to write global state"
argument_list|,
name|lastFailure
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"failed to write global state"
argument_list|,
name|lastFailure
argument_list|)
throw|;
block|}
comment|// delete the old files
if|if
condition|(
name|previousMetaData
operator|!=
literal|null
operator|&&
name|previousMetaData
operator|.
name|version
argument_list|()
operator|!=
name|currentMetaData
operator|.
name|version
argument_list|()
condition|)
block|{
for|for
control|(
name|File
name|dataLocation
range|:
name|nodeEnv
operator|.
name|nodeDataLocations
argument_list|()
control|)
block|{
name|File
name|stateFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|dataLocation
argument_list|,
literal|"_state"
argument_list|)
argument_list|,
literal|"global-"
operator|+
name|previousMetaData
operator|.
name|version
argument_list|()
argument_list|)
decl_stmt|;
name|stateFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|CachedStreamOutput
operator|.
name|pushEntry
argument_list|(
name|cachedEntry
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|loadState
specifier|private
name|void
name|loadState
parameter_list|()
throws|throws
name|Exception
block|{
name|MetaData
operator|.
name|Builder
name|metaDataBuilder
init|=
name|MetaData
operator|.
name|builder
argument_list|()
decl_stmt|;
name|MetaData
name|globalMetaData
init|=
name|loadGlobalState
argument_list|()
decl_stmt|;
if|if
condition|(
name|globalMetaData
operator|!=
literal|null
condition|)
block|{
name|metaDataBuilder
operator|.
name|metaData
argument_list|(
name|globalMetaData
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|indices
init|=
name|nodeEnv
operator|.
name|finalAllIndices
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|loadIndex
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] failed to find metadata for existing index location"
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metaDataBuilder
operator|.
name|put
argument_list|(
name|indexMetaData
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
name|currentMetaData
operator|=
name|metaDataBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|loadIndex
specifier|private
name|IndexMetaData
name|loadIndex
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|long
name|highestVersion
init|=
operator|-
literal|1
decl_stmt|;
name|IndexMetaData
name|indexMetaData
init|=
literal|null
decl_stmt|;
for|for
control|(
name|File
name|indexLocation
range|:
name|nodeEnv
operator|.
name|indexLocations
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
control|)
block|{
name|File
name|stateDir
init|=
operator|new
name|File
argument_list|(
name|indexLocation
argument_list|,
literal|"_state"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|stateDir
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|stateDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// now, iterate over the current versions, and find latest one
name|File
index|[]
name|stateFiles
init|=
name|stateDir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|stateFiles
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|File
name|stateFile
range|:
name|stateFiles
control|)
block|{
if|if
condition|(
operator|!
name|stateFile
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"state-"
argument_list|)
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|long
name|version
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|stateFile
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|"state-"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|>
name|highestVersion
condition|)
block|{
name|byte
index|[]
name|data
init|=
name|Streams
operator|.
name|copyToByteArray
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|stateFile
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}]: no data for ["
operator|+
name|stateFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"], ignoring..."
argument_list|,
name|index
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|XContentParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|// move to START_OBJECT
name|indexMetaData
operator|=
name|IndexMetaData
operator|.
name|Builder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|highestVersion
operator|=
name|version
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}]: failed to read ["
operator|+
name|stateFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"], ignoring..."
argument_list|,
name|e
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|indexMetaData
return|;
block|}
DECL|method|loadGlobalState
specifier|private
name|MetaData
name|loadGlobalState
parameter_list|()
block|{
name|long
name|highestVersion
init|=
operator|-
literal|1
decl_stmt|;
name|MetaData
name|metaData
init|=
literal|null
decl_stmt|;
for|for
control|(
name|File
name|dataLocation
range|:
name|nodeEnv
operator|.
name|nodeDataLocations
argument_list|()
control|)
block|{
name|File
name|stateLocation
init|=
operator|new
name|File
argument_list|(
name|dataLocation
argument_list|,
literal|"_state"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|stateLocation
operator|.
name|exists
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|File
index|[]
name|stateFiles
init|=
name|stateLocation
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|stateFiles
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|File
name|stateFile
range|:
name|stateFiles
control|)
block|{
name|String
name|name
init|=
name|stateFile
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"global-"
argument_list|)
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|long
name|version
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|stateFile
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|"global-"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|>
name|highestVersion
condition|)
block|{
name|byte
index|[]
name|data
init|=
name|Streams
operator|.
name|copyToByteArray
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|stateFile
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[_global] no data for ["
operator|+
name|stateFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"], ignoring..."
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|XContentParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|metaData
operator|=
name|MetaData
operator|.
name|Builder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|highestVersion
operator|=
name|version
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|metaData
return|;
block|}
DECL|method|pre019Upgrade
specifier|private
name|void
name|pre019Upgrade
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|index
init|=
operator|-
literal|1
decl_stmt|;
name|File
name|metaDataFile
init|=
literal|null
decl_stmt|;
name|MetaData
name|metaData
init|=
literal|null
decl_stmt|;
name|long
name|version
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|File
name|dataLocation
range|:
name|nodeEnv
operator|.
name|nodeDataLocations
argument_list|()
control|)
block|{
name|File
name|stateLocation
init|=
operator|new
name|File
argument_list|(
name|dataLocation
argument_list|,
literal|"_state"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|stateLocation
operator|.
name|exists
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|File
index|[]
name|stateFiles
init|=
name|stateLocation
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|stateFiles
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|File
name|stateFile
range|:
name|stateFiles
control|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[upgrade]: processing ["
operator|+
name|stateFile
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|String
name|name
init|=
name|stateFile
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"metadata-"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|long
name|fileIndex
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileIndex
operator|>=
name|index
condition|)
block|{
comment|// try and read the meta data
try|try
block|{
name|byte
index|[]
name|data
init|=
name|Streams
operator|.
name|copyToByteArray
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|stateFile
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|length
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|XContentParser
name|parser
init|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
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
if|if
condition|(
literal|"meta-data"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|metaData
operator|=
name|MetaData
operator|.
name|Builder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
expr_stmt|;
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
literal|"version"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|version
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|index
operator|=
name|fileIndex
expr_stmt|;
name|metaDataFile
operator|=
name|stateFile
expr_stmt|;
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
literal|"failed to read pre 0.19 state from ["
operator|+
name|name
operator|+
literal|"], ignoring..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|metaData
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"found old metadata state, loading metadata from [{}] and converting to new metadata location and strucutre..."
argument_list|,
name|metaDataFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|writeGlobalState
argument_list|(
literal|"upgrade"
argument_list|,
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|version
argument_list|(
name|version
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexMetaData
name|indexMetaData
range|:
name|metaData
control|)
block|{
name|IndexMetaData
operator|.
name|Builder
name|indexMetaDataBuilder
init|=
name|IndexMetaData
operator|.
name|newIndexMetaDataBuilder
argument_list|(
name|indexMetaData
argument_list|)
operator|.
name|version
argument_list|(
name|version
argument_list|)
decl_stmt|;
comment|// set the created version to 0.18
name|indexMetaDataBuilder
operator|.
name|settings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|V_0_18_0
argument_list|)
argument_list|)
expr_stmt|;
name|writeIndex
argument_list|(
literal|"upgrade"
argument_list|,
name|indexMetaDataBuilder
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// rename shards state to backup state
name|File
name|backupFile
init|=
operator|new
name|File
argument_list|(
name|metaDataFile
operator|.
name|getParentFile
argument_list|()
argument_list|,
literal|"backup-"
operator|+
name|metaDataFile
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|metaDataFile
operator|.
name|renameTo
argument_list|(
name|backupFile
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"failed to rename old state to backup state ["
operator|+
name|metaDataFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|// delete all other shards state files
for|for
control|(
name|File
name|dataLocation
range|:
name|nodeEnv
operator|.
name|nodeDataLocations
argument_list|()
control|)
block|{
name|File
name|stateLocation
init|=
operator|new
name|File
argument_list|(
name|dataLocation
argument_list|,
literal|"_state"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|stateLocation
operator|.
name|exists
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|File
index|[]
name|stateFiles
init|=
name|stateLocation
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|stateFiles
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|File
name|stateFile
range|:
name|stateFiles
control|)
block|{
name|String
name|name
init|=
name|stateFile
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"metadata-"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|stateFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"conversion to new metadata location and format done, backup create at [{}]"
argument_list|,
name|backupFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

