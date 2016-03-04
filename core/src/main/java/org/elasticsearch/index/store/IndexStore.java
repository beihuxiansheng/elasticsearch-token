begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
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
name|common
operator|.
name|settings
operator|.
name|Setting
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
name|Setting
operator|.
name|Property
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
name|shard
operator|.
name|ShardPath
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|IndexStore
specifier|public
class|class
name|IndexStore
extends|extends
name|AbstractIndexComponent
block|{
DECL|field|INDEX_STORE_THROTTLE_TYPE_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|IndexRateLimitingType
argument_list|>
name|INDEX_STORE_THROTTLE_TYPE_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"index.store.throttle.type"
argument_list|,
literal|"none"
argument_list|,
name|IndexRateLimitingType
operator|::
name|fromString
argument_list|,
name|Property
operator|.
name|Dynamic
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|field|INDEX_STORE_THROTTLE_MAX_BYTES_PER_SEC_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|ByteSizeValue
argument_list|>
name|INDEX_STORE_THROTTLE_MAX_BYTES_PER_SEC_SETTING
init|=
name|Setting
operator|.
name|byteSizeSetting
argument_list|(
literal|"index.store.throttle.max_bytes_per_sec"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|0
argument_list|)
argument_list|,
name|Property
operator|.
name|Dynamic
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|field|indexStoreConfig
specifier|protected
specifier|final
name|IndexStoreConfig
name|indexStoreConfig
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
DECL|field|type
specifier|private
specifier|volatile
name|IndexRateLimitingType
name|type
decl_stmt|;
DECL|method|IndexStore
specifier|public
name|IndexStore
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|IndexStoreConfig
name|indexStoreConfig
parameter_list|)
block|{
name|super
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexStoreConfig
operator|=
name|indexStoreConfig
expr_stmt|;
name|setType
argument_list|(
name|indexSettings
operator|.
name|getValue
argument_list|(
name|INDEX_STORE_THROTTLE_TYPE_SETTING
argument_list|)
argument_list|)
expr_stmt|;
name|rateLimiting
operator|.
name|setMaxRate
argument_list|(
name|indexSettings
operator|.
name|getValue
argument_list|(
name|INDEX_STORE_THROTTLE_MAX_BYTES_PER_SEC_SETTING
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using index.store.throttle.type [{}], with index.store.throttle.max_bytes_per_sec [{}]"
argument_list|,
name|rateLimiting
operator|.
name|getType
argument_list|()
argument_list|,
name|rateLimiting
operator|.
name|getRateLimiter
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the rate limiting, either of the index is explicitly configured, or      * the node level one (defaults to the node level one).      */
DECL|method|rateLimiting
specifier|public
name|StoreRateLimiting
name|rateLimiting
parameter_list|()
block|{
return|return
name|type
operator|.
name|useStoreLimiter
argument_list|()
condition|?
name|indexStoreConfig
operator|.
name|getNodeRateLimiter
argument_list|()
else|:
name|this
operator|.
name|rateLimiting
return|;
block|}
comment|/**      * The shard store class that should be used for each shard.      */
DECL|method|newDirectoryService
specifier|public
name|DirectoryService
name|newDirectoryService
parameter_list|(
name|ShardPath
name|path
parameter_list|)
block|{
return|return
operator|new
name|FsDirectoryService
argument_list|(
name|indexSettings
argument_list|,
name|this
argument_list|,
name|path
argument_list|)
return|;
block|}
DECL|method|setType
specifier|public
name|void
name|setType
parameter_list|(
name|IndexRateLimitingType
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
if|if
condition|(
name|type
operator|.
name|useStoreLimiter
argument_list|()
operator|==
literal|false
condition|)
block|{
name|rateLimiting
operator|.
name|setType
argument_list|(
name|type
operator|.
name|type
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setMaxRate
specifier|public
name|void
name|setMaxRate
parameter_list|(
name|ByteSizeValue
name|rate
parameter_list|)
block|{
name|rateLimiting
operator|.
name|setMaxRate
argument_list|(
name|rate
argument_list|)
expr_stmt|;
block|}
comment|/**      * On an index level we can configure all of {@link org.apache.lucene.store.StoreRateLimiting.Type} as well as      *<tt>node</tt> which will then use a global rate limiter that has it's own configuration. The global one is      * configured in {@link IndexStoreConfig} which is managed by the per-node {@link org.elasticsearch.indices.IndicesService}      */
DECL|class|IndexRateLimitingType
specifier|public
specifier|static
specifier|final
class|class
name|IndexRateLimitingType
block|{
DECL|field|type
specifier|private
specifier|final
name|StoreRateLimiting
operator|.
name|Type
name|type
decl_stmt|;
DECL|method|IndexRateLimitingType
specifier|private
name|IndexRateLimitingType
parameter_list|(
name|StoreRateLimiting
operator|.
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|useStoreLimiter
specifier|private
name|boolean
name|useStoreLimiter
parameter_list|()
block|{
return|return
name|type
operator|==
literal|null
return|;
block|}
DECL|method|fromString
specifier|static
name|IndexRateLimitingType
name|fromString
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
literal|"node"
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
operator|new
name|IndexRateLimitingType
argument_list|(
literal|null
argument_list|)
return|;
block|}
else|else
block|{
try|try
block|{
return|return
operator|new
name|IndexRateLimitingType
argument_list|(
name|StoreRateLimiting
operator|.
name|Type
operator|.
name|fromString
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"rate limiting type ["
operator|+
name|type
operator|+
literal|"] not valid, can be one of [all|merge|none|node]"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

