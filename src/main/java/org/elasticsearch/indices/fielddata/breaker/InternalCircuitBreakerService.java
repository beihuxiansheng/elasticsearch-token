begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.fielddata.breaker
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|fielddata
operator|.
name|breaker
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|breaker
operator|.
name|MemoryCircuitBreaker
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
name|AbstractLifecycleComponent
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
name|ByteSizeValue
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
name|fielddata
operator|.
name|cache
operator|.
name|IndicesFieldDataCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmInfo
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

begin_comment
comment|/**  * The InternalCircuitBreakerService handles providing  * {@link org.elasticsearch.common.breaker.MemoryCircuitBreaker}s  * that can be used to keep track of memory usage across the node, preventing  * actions that could cause an {@link OutOfMemoryError} on the node.  */
end_comment

begin_class
DECL|class|InternalCircuitBreakerService
specifier|public
class|class
name|InternalCircuitBreakerService
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|InternalCircuitBreakerService
argument_list|>
implements|implements
name|CircuitBreakerService
block|{
DECL|field|CIRCUIT_BREAKER_MAX_BYTES_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|CIRCUIT_BREAKER_MAX_BYTES_SETTING
init|=
literal|"indices.fielddata.breaker.limit"
decl_stmt|;
DECL|field|CIRCUIT_BREAKER_OVERHEAD_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|CIRCUIT_BREAKER_OVERHEAD_SETTING
init|=
literal|"indices.fielddata.breaker.overhead"
decl_stmt|;
DECL|field|DEFAULT_OVERHEAD_CONSTANT
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_OVERHEAD_CONSTANT
init|=
literal|1.03
decl_stmt|;
DECL|field|JVM_HEAP_MAX_BYTES
specifier|private
specifier|static
specifier|final
name|long
name|JVM_HEAP_MAX_BYTES
init|=
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|getMem
argument_list|()
operator|.
name|getHeapMax
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
DECL|field|DEFAULT_BREAKER_LIMIT
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_BREAKER_LIMIT
init|=
call|(
name|long
call|)
argument_list|(
literal|0.8
operator|*
name|JVM_HEAP_MAX_BYTES
argument_list|)
decl_stmt|;
comment|// 80% of the max heap
DECL|field|breaker
specifier|private
specifier|volatile
name|MemoryCircuitBreaker
name|breaker
decl_stmt|;
DECL|field|maxBytes
specifier|private
specifier|volatile
name|long
name|maxBytes
decl_stmt|;
DECL|field|overhead
specifier|private
specifier|volatile
name|double
name|overhead
decl_stmt|;
annotation|@
name|Inject
DECL|method|InternalCircuitBreakerService
specifier|public
name|InternalCircuitBreakerService
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
name|maxBytes
operator|=
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|CIRCUIT_BREAKER_MAX_BYTES_SETTING
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|DEFAULT_BREAKER_LIMIT
argument_list|)
argument_list|)
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|this
operator|.
name|overhead
operator|=
name|settings
operator|.
name|getAsDouble
argument_list|(
name|CIRCUIT_BREAKER_OVERHEAD_SETTING
argument_list|,
name|DEFAULT_OVERHEAD_CONSTANT
argument_list|)
expr_stmt|;
name|this
operator|.
name|breaker
operator|=
operator|new
name|MemoryCircuitBreaker
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
name|maxBytes
argument_list|)
argument_list|,
name|overhead
argument_list|,
literal|null
argument_list|,
name|logger
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
comment|// clear breaker now that settings have changed
name|ByteSizeValue
name|newMaxByteSizeValue
init|=
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|CIRCUIT_BREAKER_MAX_BYTES_SETTING
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|boolean
name|breakerResetNeeded
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|newMaxByteSizeValue
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [{}] from [{}] to [{}]"
argument_list|,
name|CIRCUIT_BREAKER_MAX_BYTES_SETTING
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|InternalCircuitBreakerService
operator|.
name|this
operator|.
name|maxBytes
argument_list|)
argument_list|,
name|newMaxByteSizeValue
argument_list|)
expr_stmt|;
name|InternalCircuitBreakerService
operator|.
name|this
operator|.
name|maxBytes
operator|=
name|newMaxByteSizeValue
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|breakerResetNeeded
operator|=
literal|true
expr_stmt|;
block|}
name|double
name|newOverhead
init|=
name|settings
operator|.
name|getAsDouble
argument_list|(
name|CIRCUIT_BREAKER_OVERHEAD_SETTING
argument_list|,
name|overhead
argument_list|)
decl_stmt|;
if|if
condition|(
name|newOverhead
operator|!=
name|overhead
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [{}] from [{}] to [{}]"
argument_list|,
name|CIRCUIT_BREAKER_OVERHEAD_SETTING
argument_list|,
name|overhead
argument_list|,
name|newOverhead
argument_list|)
expr_stmt|;
name|InternalCircuitBreakerService
operator|.
name|this
operator|.
name|overhead
operator|=
name|newOverhead
expr_stmt|;
name|breakerResetNeeded
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|breakerResetNeeded
condition|)
block|{
name|resetBreaker
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @return a {@link org.elasticsearch.common.breaker.MemoryCircuitBreaker} that can be used for aggregating memory usage      */
DECL|method|getBreaker
specifier|public
name|MemoryCircuitBreaker
name|getBreaker
parameter_list|()
block|{
return|return
name|this
operator|.
name|breaker
return|;
block|}
comment|/**      * Reset the breaker, creating a new one and initializing its used value      * to the actual field data usage, or the existing estimated usage if the      * actual value is not available. Will not trip the breaker even if the      * used value is higher than the limit for the breaker.      */
DECL|method|resetBreaker
specifier|public
specifier|synchronized
name|void
name|resetBreaker
parameter_list|()
block|{
specifier|final
name|MemoryCircuitBreaker
name|oldBreaker
init|=
name|this
operator|.
name|breaker
decl_stmt|;
comment|// discard old breaker by creating a new one and pre-populating from the current breaker
name|this
operator|.
name|breaker
operator|=
operator|new
name|MemoryCircuitBreaker
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
name|maxBytes
argument_list|)
argument_list|,
name|overhead
argument_list|,
name|oldBreaker
argument_list|,
name|logger
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stats
specifier|public
name|FieldDataBreakerStats
name|stats
parameter_list|()
block|{
return|return
operator|new
name|FieldDataBreakerStats
argument_list|(
name|breaker
operator|.
name|getMaximum
argument_list|()
argument_list|,
name|breaker
operator|.
name|getUsed
argument_list|()
argument_list|,
name|breaker
operator|.
name|getOverhead
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
block|}
end_class

end_unit

