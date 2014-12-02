begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.recovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|recovery
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
name|base
operator|.
name|Objects
import|;
end_import

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
name|RateLimiter
import|;
end_import

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
name|RateLimiter
operator|.
name|SimpleRateLimiter
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
name|unit
operator|.
name|ByteSizeUnit
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
name|util
operator|.
name|concurrent
operator|.
name|EsExecutors
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|settings
operator|.
name|NodeSettingsService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadPoolExecutor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|RecoverySettings
specifier|public
class|class
name|RecoverySettings
extends|extends
name|AbstractComponent
block|{
DECL|field|INDICES_RECOVERY_FILE_CHUNK_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|INDICES_RECOVERY_FILE_CHUNK_SIZE
init|=
literal|"indices.recovery.file_chunk_size"
decl_stmt|;
DECL|field|INDICES_RECOVERY_TRANSLOG_OPS
specifier|public
specifier|static
specifier|final
name|String
name|INDICES_RECOVERY_TRANSLOG_OPS
init|=
literal|"indices.recovery.translog_ops"
decl_stmt|;
DECL|field|INDICES_RECOVERY_TRANSLOG_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|INDICES_RECOVERY_TRANSLOG_SIZE
init|=
literal|"indices.recovery.translog_size"
decl_stmt|;
DECL|field|INDICES_RECOVERY_COMPRESS
specifier|public
specifier|static
specifier|final
name|String
name|INDICES_RECOVERY_COMPRESS
init|=
literal|"indices.recovery.compress"
decl_stmt|;
DECL|field|INDICES_RECOVERY_CONCURRENT_STREAMS
specifier|public
specifier|static
specifier|final
name|String
name|INDICES_RECOVERY_CONCURRENT_STREAMS
init|=
literal|"indices.recovery.concurrent_streams"
decl_stmt|;
DECL|field|INDICES_RECOVERY_CONCURRENT_SMALL_FILE_STREAMS
specifier|public
specifier|static
specifier|final
name|String
name|INDICES_RECOVERY_CONCURRENT_SMALL_FILE_STREAMS
init|=
literal|"indices.recovery.concurrent_small_file_streams"
decl_stmt|;
DECL|field|INDICES_RECOVERY_MAX_BYTES_PER_SEC
specifier|public
specifier|static
specifier|final
name|String
name|INDICES_RECOVERY_MAX_BYTES_PER_SEC
init|=
literal|"indices.recovery.max_bytes_per_sec"
decl_stmt|;
DECL|field|INDICES_RECOVERY_RETRY_DELAY
specifier|public
specifier|static
specifier|final
name|String
name|INDICES_RECOVERY_RETRY_DELAY
init|=
literal|"indices.recovery.retry_delay"
decl_stmt|;
DECL|field|SMALL_FILE_CUTOFF_BYTES
specifier|public
specifier|static
specifier|final
name|long
name|SMALL_FILE_CUTOFF_BYTES
init|=
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"5mb"
argument_list|)
operator|.
name|bytes
argument_list|()
decl_stmt|;
comment|/**      * Use {@link #INDICES_RECOVERY_MAX_BYTES_PER_SEC} instead      */
annotation|@
name|Deprecated
DECL|field|INDICES_RECOVERY_MAX_SIZE_PER_SEC
specifier|public
specifier|static
specifier|final
name|String
name|INDICES_RECOVERY_MAX_SIZE_PER_SEC
init|=
literal|"indices.recovery.max_size_per_sec"
decl_stmt|;
DECL|field|fileChunkSize
specifier|private
specifier|volatile
name|ByteSizeValue
name|fileChunkSize
decl_stmt|;
DECL|field|compress
specifier|private
specifier|volatile
name|boolean
name|compress
decl_stmt|;
DECL|field|translogOps
specifier|private
specifier|volatile
name|int
name|translogOps
decl_stmt|;
DECL|field|translogSize
specifier|private
specifier|volatile
name|ByteSizeValue
name|translogSize
decl_stmt|;
DECL|field|concurrentStreams
specifier|private
specifier|volatile
name|int
name|concurrentStreams
decl_stmt|;
DECL|field|concurrentSmallFileStreams
specifier|private
specifier|volatile
name|int
name|concurrentSmallFileStreams
decl_stmt|;
DECL|field|concurrentStreamPool
specifier|private
specifier|final
name|ThreadPoolExecutor
name|concurrentStreamPool
decl_stmt|;
DECL|field|concurrentSmallFileStreamPool
specifier|private
specifier|final
name|ThreadPoolExecutor
name|concurrentSmallFileStreamPool
decl_stmt|;
DECL|field|maxBytesPerSec
specifier|private
specifier|volatile
name|ByteSizeValue
name|maxBytesPerSec
decl_stmt|;
DECL|field|rateLimiter
specifier|private
specifier|volatile
name|SimpleRateLimiter
name|rateLimiter
decl_stmt|;
DECL|field|retryDelay
specifier|private
specifier|volatile
name|TimeValue
name|retryDelay
decl_stmt|;
annotation|@
name|Inject
DECL|method|RecoverySettings
specifier|public
name|RecoverySettings
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeSettingsService
name|nodeSettingsService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|fileChunkSize
operator|=
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"file_chunk_size"
argument_list|,
name|settings
operator|.
name|getAsBytesSize
argument_list|(
literal|"index.shard.recovery.file_chunk_size"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|512
argument_list|,
name|ByteSizeUnit
operator|.
name|KB
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|translogOps
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"translog_ops"
argument_list|,
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"index.shard.recovery.translog_ops"
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|translogSize
operator|=
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"translog_size"
argument_list|,
name|settings
operator|.
name|getAsBytesSize
argument_list|(
literal|"index.shard.recovery.translog_size"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|512
argument_list|,
name|ByteSizeUnit
operator|.
name|KB
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|compress
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"compress"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|retryDelay
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"retry_delay"
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|concurrentStreams
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"concurrent_streams"
argument_list|,
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"index.shard.recovery.concurrent_streams"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|concurrentStreamPool
operator|=
name|EsExecutors
operator|.
name|newScaling
argument_list|(
literal|0
argument_list|,
name|concurrentStreams
argument_list|,
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
name|EsExecutors
operator|.
name|daemonThreadFactory
argument_list|(
name|settings
argument_list|,
literal|"[recovery_stream]"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|concurrentSmallFileStreams
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"concurrent_small_file_streams"
argument_list|,
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"index.shard.recovery.concurrent_small_file_streams"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|concurrentSmallFileStreamPool
operator|=
name|EsExecutors
operator|.
name|newScaling
argument_list|(
literal|0
argument_list|,
name|concurrentSmallFileStreams
argument_list|,
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
name|EsExecutors
operator|.
name|daemonThreadFactory
argument_list|(
name|settings
argument_list|,
literal|"[small_file_recovery_stream]"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxBytesPerSec
operator|=
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"max_bytes_per_sec"
argument_list|,
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"max_size_per_sec"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|20
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxBytesPerSec
operator|.
name|bytes
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|rateLimiter
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|rateLimiter
operator|=
operator|new
name|SimpleRateLimiter
argument_list|(
name|maxBytesPerSec
operator|.
name|mbFrac
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"using max_bytes_per_sec[{}], concurrent_streams [{}], file_chunk_size [{}], translog_size [{}], translog_ops [{}], and compress [{}]"
argument_list|,
name|maxBytesPerSec
argument_list|,
name|concurrentStreams
argument_list|,
name|fileChunkSize
argument_list|,
name|translogSize
argument_list|,
name|translogOps
argument_list|,
name|compress
argument_list|)
expr_stmt|;
name|nodeSettingsService
operator|.
name|addListener
argument_list|(
operator|new
name|ApplySettings
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|ThreadPool
operator|.
name|terminate
argument_list|(
name|concurrentStreamPool
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
DECL|method|fileChunkSize
specifier|public
name|ByteSizeValue
name|fileChunkSize
parameter_list|()
block|{
return|return
name|fileChunkSize
return|;
block|}
DECL|method|compress
specifier|public
name|boolean
name|compress
parameter_list|()
block|{
return|return
name|compress
return|;
block|}
DECL|method|translogOps
specifier|public
name|int
name|translogOps
parameter_list|()
block|{
return|return
name|translogOps
return|;
block|}
DECL|method|translogSize
specifier|public
name|ByteSizeValue
name|translogSize
parameter_list|()
block|{
return|return
name|translogSize
return|;
block|}
DECL|method|concurrentStreams
specifier|public
name|int
name|concurrentStreams
parameter_list|()
block|{
return|return
name|concurrentStreams
return|;
block|}
DECL|method|concurrentStreamPool
specifier|public
name|ThreadPoolExecutor
name|concurrentStreamPool
parameter_list|()
block|{
return|return
name|concurrentStreamPool
return|;
block|}
DECL|method|concurrentSmallFileStreamPool
specifier|public
name|ThreadPoolExecutor
name|concurrentSmallFileStreamPool
parameter_list|()
block|{
return|return
name|concurrentSmallFileStreamPool
return|;
block|}
DECL|method|rateLimiter
specifier|public
name|RateLimiter
name|rateLimiter
parameter_list|()
block|{
return|return
name|rateLimiter
return|;
block|}
DECL|method|retryDelay
specifier|public
name|TimeValue
name|retryDelay
parameter_list|()
block|{
return|return
name|retryDelay
return|;
block|}
DECL|class|ApplySettings
class|class
name|ApplySettings
implements|implements
name|NodeSettingsService
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
name|ByteSizeValue
name|maxSizePerSec
init|=
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|INDICES_RECOVERY_MAX_BYTES_PER_SEC
argument_list|,
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|INDICES_RECOVERY_MAX_SIZE_PER_SEC
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|maxBytesPerSec
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equal
argument_list|(
name|maxSizePerSec
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|maxBytesPerSec
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [{}] from [{}] to [{}]"
argument_list|,
name|INDICES_RECOVERY_MAX_BYTES_PER_SEC
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|maxBytesPerSec
argument_list|,
name|maxSizePerSec
argument_list|)
expr_stmt|;
name|RecoverySettings
operator|.
name|this
operator|.
name|maxBytesPerSec
operator|=
name|maxSizePerSec
expr_stmt|;
if|if
condition|(
name|maxSizePerSec
operator|.
name|bytes
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|rateLimiter
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rateLimiter
operator|!=
literal|null
condition|)
block|{
name|rateLimiter
operator|.
name|setMbPerSec
argument_list|(
name|maxSizePerSec
operator|.
name|mbFrac
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rateLimiter
operator|=
operator|new
name|SimpleRateLimiter
argument_list|(
name|maxSizePerSec
operator|.
name|mbFrac
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|ByteSizeValue
name|fileChunkSize
init|=
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|INDICES_RECOVERY_FILE_CHUNK_SIZE
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|fileChunkSize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fileChunkSize
operator|.
name|equals
argument_list|(
name|RecoverySettings
operator|.
name|this
operator|.
name|fileChunkSize
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [indices.recovery.file_chunk_size] from [{}] to [{}]"
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|fileChunkSize
argument_list|,
name|fileChunkSize
argument_list|)
expr_stmt|;
name|RecoverySettings
operator|.
name|this
operator|.
name|fileChunkSize
operator|=
name|fileChunkSize
expr_stmt|;
block|}
name|int
name|translogOps
init|=
name|settings
operator|.
name|getAsInt
argument_list|(
name|INDICES_RECOVERY_TRANSLOG_OPS
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|translogOps
argument_list|)
decl_stmt|;
if|if
condition|(
name|translogOps
operator|!=
name|RecoverySettings
operator|.
name|this
operator|.
name|translogOps
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [indices.recovery.translog_ops] from [{}] to [{}]"
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|translogOps
argument_list|,
name|translogOps
argument_list|)
expr_stmt|;
name|RecoverySettings
operator|.
name|this
operator|.
name|translogOps
operator|=
name|translogOps
expr_stmt|;
block|}
name|ByteSizeValue
name|translogSize
init|=
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|INDICES_RECOVERY_TRANSLOG_SIZE
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|translogSize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|translogSize
operator|.
name|equals
argument_list|(
name|RecoverySettings
operator|.
name|this
operator|.
name|translogSize
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [indices.recovery.translog_size] from [{}] to [{}]"
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|translogSize
argument_list|,
name|translogSize
argument_list|)
expr_stmt|;
name|RecoverySettings
operator|.
name|this
operator|.
name|translogSize
operator|=
name|translogSize
expr_stmt|;
block|}
name|boolean
name|compress
init|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|INDICES_RECOVERY_COMPRESS
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|compress
argument_list|)
decl_stmt|;
if|if
condition|(
name|compress
operator|!=
name|RecoverySettings
operator|.
name|this
operator|.
name|compress
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [indices.recovery.compress] from [{}] to [{}]"
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|compress
argument_list|,
name|compress
argument_list|)
expr_stmt|;
name|RecoverySettings
operator|.
name|this
operator|.
name|compress
operator|=
name|compress
expr_stmt|;
block|}
name|int
name|concurrentStreams
init|=
name|settings
operator|.
name|getAsInt
argument_list|(
name|INDICES_RECOVERY_CONCURRENT_STREAMS
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|concurrentStreams
argument_list|)
decl_stmt|;
if|if
condition|(
name|concurrentStreams
operator|!=
name|RecoverySettings
operator|.
name|this
operator|.
name|concurrentStreams
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [indices.recovery.concurrent_streams] from [{}] to [{}]"
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|concurrentStreams
argument_list|,
name|concurrentStreams
argument_list|)
expr_stmt|;
name|RecoverySettings
operator|.
name|this
operator|.
name|concurrentStreams
operator|=
name|concurrentStreams
expr_stmt|;
name|RecoverySettings
operator|.
name|this
operator|.
name|concurrentStreamPool
operator|.
name|setMaximumPoolSize
argument_list|(
name|concurrentStreams
argument_list|)
expr_stmt|;
block|}
name|int
name|concurrentSmallFileStreams
init|=
name|settings
operator|.
name|getAsInt
argument_list|(
name|INDICES_RECOVERY_CONCURRENT_SMALL_FILE_STREAMS
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|concurrentSmallFileStreams
argument_list|)
decl_stmt|;
if|if
condition|(
name|concurrentSmallFileStreams
operator|!=
name|RecoverySettings
operator|.
name|this
operator|.
name|concurrentSmallFileStreams
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [indices.recovery.concurrent_small_file_streams] from [{}] to [{}]"
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|concurrentSmallFileStreams
argument_list|,
name|concurrentSmallFileStreams
argument_list|)
expr_stmt|;
name|RecoverySettings
operator|.
name|this
operator|.
name|concurrentSmallFileStreams
operator|=
name|concurrentSmallFileStreams
expr_stmt|;
name|RecoverySettings
operator|.
name|this
operator|.
name|concurrentSmallFileStreamPool
operator|.
name|setMaximumPoolSize
argument_list|(
name|concurrentSmallFileStreams
argument_list|)
expr_stmt|;
block|}
specifier|final
name|TimeValue
name|retryDelay
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
name|INDICES_RECOVERY_RETRY_DELAY
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|retryDelay
argument_list|)
decl_stmt|;
if|if
condition|(
name|retryDelay
operator|.
name|equals
argument_list|(
name|RecoverySettings
operator|.
name|this
operator|.
name|retryDelay
argument_list|)
operator|==
literal|false
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [] from [{}] to [{}]"
argument_list|,
name|INDICES_RECOVERY_RETRY_DELAY
argument_list|,
name|RecoverySettings
operator|.
name|this
operator|.
name|retryDelay
argument_list|,
name|retryDelay
argument_list|)
expr_stmt|;
name|RecoverySettings
operator|.
name|this
operator|.
name|retryDelay
operator|=
name|retryDelay
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

