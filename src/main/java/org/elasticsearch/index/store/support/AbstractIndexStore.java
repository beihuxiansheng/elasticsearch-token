begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
operator|.
name|support
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
name|store
operator|.
name|StoreRateLimiting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalStateException
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
name|ByteSizeValue
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
name|AbstractIndexComponent
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
name|index
operator|.
name|settings
operator|.
name|IndexSettings
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
name|settings
operator|.
name|IndexSettingsService
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
name|shard
operator|.
name|ShardId
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
name|store
operator|.
name|IndexStore
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
name|store
operator|.
name|IndicesStore
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AbstractIndexStore
specifier|public
specifier|abstract
class|class
name|AbstractIndexStore
extends|extends
name|AbstractIndexComponent
implements|implements
name|IndexStore
block|{
DECL|field|INDEX_STORE_THROTTLE_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_STORE_THROTTLE_TYPE
init|=
literal|"index.store.throttle.type"
decl_stmt|;
DECL|field|INDEX_STORE_THROTTLE_MAX_BYTES_PER_SEC
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_STORE_THROTTLE_MAX_BYTES_PER_SEC
init|=
literal|"index.store.throttle.max_bytes_per_sec"
decl_stmt|;
DECL|class|ApplySettings
class|class
name|ApplySettings
implements|implements
name|IndexSettingsService
operator|.
name|Listener
block|{
annotation|@
name|Override
DECL|method|onRefreshSettings
specifier|public
name|void
name|onRefreshSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|String
name|rateLimitingType
init|=
name|settings
operator|.
name|get
argument_list|(
name|INDEX_STORE_THROTTLE_TYPE
argument_list|,
name|AbstractIndexStore
operator|.
name|this
operator|.
name|rateLimitingType
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|rateLimitingType
operator|.
name|equals
argument_list|(
name|AbstractIndexStore
operator|.
name|this
operator|.
name|rateLimitingType
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating index.store.throttle.type from [{}] to [{}]"
argument_list|,
name|AbstractIndexStore
operator|.
name|this
operator|.
name|rateLimitingType
argument_list|,
name|rateLimitingType
argument_list|)
expr_stmt|;
if|if
condition|(
name|rateLimitingType
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"node"
argument_list|)
condition|)
block|{
name|AbstractIndexStore
operator|.
name|this
operator|.
name|rateLimitingType
operator|=
name|rateLimitingType
expr_stmt|;
name|AbstractIndexStore
operator|.
name|this
operator|.
name|nodeRateLimiting
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|StoreRateLimiting
operator|.
name|Type
operator|.
name|fromString
argument_list|(
name|rateLimitingType
argument_list|)
expr_stmt|;
name|AbstractIndexStore
operator|.
name|this
operator|.
name|rateLimitingType
operator|=
name|rateLimitingType
expr_stmt|;
name|AbstractIndexStore
operator|.
name|this
operator|.
name|nodeRateLimiting
operator|=
literal|false
expr_stmt|;
name|AbstractIndexStore
operator|.
name|this
operator|.
name|rateLimiting
operator|.
name|setType
argument_list|(
name|rateLimitingType
argument_list|)
expr_stmt|;
block|}
block|}
name|ByteSizeValue
name|rateLimitingThrottle
init|=
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|INDEX_STORE_THROTTLE_MAX_BYTES_PER_SEC
argument_list|,
name|AbstractIndexStore
operator|.
name|this
operator|.
name|rateLimitingThrottle
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|rateLimitingThrottle
operator|.
name|equals
argument_list|(
name|AbstractIndexStore
operator|.
name|this
operator|.
name|rateLimitingThrottle
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating index.store.throttle.max_bytes_per_sec from [{}] to [{}], note, type is [{}]"
argument_list|,
name|AbstractIndexStore
operator|.
name|this
operator|.
name|rateLimitingThrottle
argument_list|,
name|rateLimitingThrottle
argument_list|,
name|AbstractIndexStore
operator|.
name|this
operator|.
name|rateLimitingType
argument_list|)
expr_stmt|;
name|AbstractIndexStore
operator|.
name|this
operator|.
name|rateLimitingThrottle
operator|=
name|rateLimitingThrottle
expr_stmt|;
name|AbstractIndexStore
operator|.
name|this
operator|.
name|rateLimiting
operator|.
name|setMaxRate
argument_list|(
name|rateLimitingThrottle
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|nodeEnv
specifier|private
specifier|final
name|NodeEnvironment
name|nodeEnv
decl_stmt|;
DECL|field|locations
specifier|private
specifier|final
name|Path
index|[]
name|locations
decl_stmt|;
DECL|field|indexService
specifier|protected
specifier|final
name|IndexService
name|indexService
decl_stmt|;
DECL|field|indicesStore
specifier|protected
specifier|final
name|IndicesStore
name|indicesStore
decl_stmt|;
DECL|field|rateLimitingType
specifier|private
specifier|volatile
name|String
name|rateLimitingType
decl_stmt|;
DECL|field|rateLimitingThrottle
specifier|private
specifier|volatile
name|ByteSizeValue
name|rateLimitingThrottle
decl_stmt|;
DECL|field|nodeRateLimiting
specifier|private
specifier|volatile
name|boolean
name|nodeRateLimiting
decl_stmt|;
DECL|field|rateLimiting
specifier|private
specifier|final
name|StoreRateLimiting
name|rateLimiting
init|=
operator|new
name|StoreRateLimiting
argument_list|()
decl_stmt|;
DECL|field|applySettings
specifier|private
specifier|final
name|ApplySettings
name|applySettings
init|=
operator|new
name|ApplySettings
argument_list|()
decl_stmt|;
DECL|method|AbstractIndexStore
specifier|protected
name|AbstractIndexStore
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|IndexService
name|indexService
parameter_list|,
name|IndicesStore
name|indicesStore
parameter_list|,
name|NodeEnvironment
name|nodeEnv
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexService
operator|=
name|indexService
expr_stmt|;
name|this
operator|.
name|indicesStore
operator|=
name|indicesStore
expr_stmt|;
name|this
operator|.
name|rateLimitingType
operator|=
name|indexSettings
operator|.
name|get
argument_list|(
name|INDEX_STORE_THROTTLE_TYPE
argument_list|,
literal|"node"
argument_list|)
expr_stmt|;
if|if
condition|(
name|rateLimitingType
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"node"
argument_list|)
condition|)
block|{
name|nodeRateLimiting
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|nodeRateLimiting
operator|=
literal|false
expr_stmt|;
name|rateLimiting
operator|.
name|setType
argument_list|(
name|rateLimitingType
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|rateLimitingThrottle
operator|=
name|indexSettings
operator|.
name|getAsBytesSize
argument_list|(
name|INDEX_STORE_THROTTLE_MAX_BYTES_PER_SEC
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|rateLimiting
operator|.
name|setMaxRate
argument_list|(
name|rateLimitingThrottle
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using index.store.throttle.type [{}], with index.store.throttle.max_bytes_per_sec [{}]"
argument_list|,
name|rateLimitingType
argument_list|,
name|rateLimitingThrottle
argument_list|)
expr_stmt|;
name|indexService
operator|.
name|settingsService
argument_list|()
operator|.
name|addListener
argument_list|(
name|applySettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeEnv
operator|=
name|nodeEnv
expr_stmt|;
if|if
condition|(
name|nodeEnv
operator|.
name|hasNodeFile
argument_list|()
condition|)
block|{
name|this
operator|.
name|locations
operator|=
name|nodeEnv
operator|.
name|indexPaths
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|locations
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
name|indexService
operator|.
name|settingsService
argument_list|()
operator|.
name|removeListener
argument_list|(
name|applySettings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rateLimiting
specifier|public
name|StoreRateLimiting
name|rateLimiting
parameter_list|()
block|{
return|return
name|nodeRateLimiting
condition|?
name|indicesStore
operator|.
name|rateLimiting
argument_list|()
else|:
name|this
operator|.
name|rateLimiting
return|;
block|}
annotation|@
name|Override
DECL|method|canDeleteUnallocated
specifier|public
name|boolean
name|canDeleteUnallocated
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{
if|if
condition|(
name|locations
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|indexService
operator|.
name|hasShard
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|FileSystemUtils
operator|.
name|exists
argument_list|(
name|nodeEnv
operator|.
name|shardPaths
argument_list|(
name|shardId
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteUnallocated
specifier|public
name|void
name|deleteUnallocated
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|locations
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|indexService
operator|.
name|hasShard
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
name|shardId
operator|+
literal|" allocated, can't be deleted"
argument_list|)
throw|;
block|}
try|try
block|{
name|nodeEnv
operator|.
name|deleteShardDirectorySafe
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to delete shard locations"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shardIndexLocations
specifier|public
name|Path
index|[]
name|shardIndexLocations
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{
name|Path
index|[]
name|shardLocations
init|=
name|nodeEnv
operator|.
name|shardPaths
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
name|Path
index|[]
name|shardIndexLocations
init|=
operator|new
name|Path
index|[
name|shardLocations
operator|.
name|length
index|]
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
name|shardLocations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|shardIndexLocations
index|[
name|i
index|]
operator|=
name|shardLocations
index|[
name|i
index|]
operator|.
name|resolve
argument_list|(
literal|"index"
argument_list|)
expr_stmt|;
block|}
return|return
name|shardIndexLocations
return|;
block|}
block|}
end_class

end_unit

