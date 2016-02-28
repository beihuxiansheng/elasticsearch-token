begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.breaker
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
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
name|common
operator|.
name|breaker
operator|.
name|ChildMemoryCircuitBreaker
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
name|CircuitBreaker
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
name|CircuitBreakingException
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
name|NoopCircuitBreaker
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
name|logging
operator|.
name|Loggers
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
name|ClusterSettings
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
name|SettingsProperty
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
name|List
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
name|ConcurrentHashMap
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
name|ConcurrentMap
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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * CircuitBreakerService that attempts to redistribute space between breakers  * if tripped  */
end_comment

begin_class
DECL|class|HierarchyCircuitBreakerService
specifier|public
class|class
name|HierarchyCircuitBreakerService
extends|extends
name|CircuitBreakerService
block|{
DECL|field|CHILD_LOGGER_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|CHILD_LOGGER_PREFIX
init|=
literal|"org.elasticsearch.indices.breaker."
decl_stmt|;
DECL|field|breakers
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|CircuitBreaker
argument_list|>
name|breakers
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
DECL|field|TOTAL_CIRCUIT_BREAKER_LIMIT_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|ByteSizeValue
argument_list|>
name|TOTAL_CIRCUIT_BREAKER_LIMIT_SETTING
init|=
name|Setting
operator|.
name|byteSizeSetting
argument_list|(
literal|"indices.breaker.total.limit"
argument_list|,
literal|"70%"
argument_list|,
name|SettingsProperty
operator|.
name|Dynamic
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
DECL|field|FIELDDATA_CIRCUIT_BREAKER_LIMIT_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|ByteSizeValue
argument_list|>
name|FIELDDATA_CIRCUIT_BREAKER_LIMIT_SETTING
init|=
name|Setting
operator|.
name|byteSizeSetting
argument_list|(
literal|"indices.breaker.fielddata.limit"
argument_list|,
literal|"60%"
argument_list|,
name|SettingsProperty
operator|.
name|Dynamic
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
DECL|field|FIELDDATA_CIRCUIT_BREAKER_OVERHEAD_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Double
argument_list|>
name|FIELDDATA_CIRCUIT_BREAKER_OVERHEAD_SETTING
init|=
name|Setting
operator|.
name|doubleSetting
argument_list|(
literal|"indices.breaker.fielddata.overhead"
argument_list|,
literal|1.03d
argument_list|,
literal|0.0d
argument_list|,
name|SettingsProperty
operator|.
name|Dynamic
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
DECL|field|FIELDDATA_CIRCUIT_BREAKER_TYPE_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|CircuitBreaker
operator|.
name|Type
argument_list|>
name|FIELDDATA_CIRCUIT_BREAKER_TYPE_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"indices.breaker.fielddata.type"
argument_list|,
literal|"memory"
argument_list|,
name|CircuitBreaker
operator|.
name|Type
operator|::
name|parseValue
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
DECL|field|REQUEST_CIRCUIT_BREAKER_LIMIT_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|ByteSizeValue
argument_list|>
name|REQUEST_CIRCUIT_BREAKER_LIMIT_SETTING
init|=
name|Setting
operator|.
name|byteSizeSetting
argument_list|(
literal|"indices.breaker.request.limit"
argument_list|,
literal|"40%"
argument_list|,
name|SettingsProperty
operator|.
name|Dynamic
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
DECL|field|REQUEST_CIRCUIT_BREAKER_OVERHEAD_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Double
argument_list|>
name|REQUEST_CIRCUIT_BREAKER_OVERHEAD_SETTING
init|=
name|Setting
operator|.
name|doubleSetting
argument_list|(
literal|"indices.breaker.request.overhead"
argument_list|,
literal|1.0d
argument_list|,
literal|0.0d
argument_list|,
name|SettingsProperty
operator|.
name|Dynamic
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
DECL|field|REQUEST_CIRCUIT_BREAKER_TYPE_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|CircuitBreaker
operator|.
name|Type
argument_list|>
name|REQUEST_CIRCUIT_BREAKER_TYPE_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"indices.breaker.request.type"
argument_list|,
literal|"memory"
argument_list|,
name|CircuitBreaker
operator|.
name|Type
operator|::
name|parseValue
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
DECL|field|parentSettings
specifier|private
specifier|volatile
name|BreakerSettings
name|parentSettings
decl_stmt|;
DECL|field|fielddataSettings
specifier|private
specifier|volatile
name|BreakerSettings
name|fielddataSettings
decl_stmt|;
DECL|field|requestSettings
specifier|private
specifier|volatile
name|BreakerSettings
name|requestSettings
decl_stmt|;
comment|// Tripped count for when redistribution was attempted but wasn't successful
DECL|field|parentTripCount
specifier|private
specifier|final
name|AtomicLong
name|parentTripCount
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|Inject
DECL|method|HierarchyCircuitBreakerService
specifier|public
name|HierarchyCircuitBreakerService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterSettings
name|clusterSettings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|fielddataSettings
operator|=
operator|new
name|BreakerSettings
argument_list|(
name|CircuitBreaker
operator|.
name|FIELDDATA
argument_list|,
name|FIELDDATA_CIRCUIT_BREAKER_LIMIT_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
operator|.
name|bytes
argument_list|()
argument_list|,
name|FIELDDATA_CIRCUIT_BREAKER_OVERHEAD_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|,
name|FIELDDATA_CIRCUIT_BREAKER_TYPE_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestSettings
operator|=
operator|new
name|BreakerSettings
argument_list|(
name|CircuitBreaker
operator|.
name|REQUEST
argument_list|,
name|REQUEST_CIRCUIT_BREAKER_LIMIT_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
operator|.
name|bytes
argument_list|()
argument_list|,
name|REQUEST_CIRCUIT_BREAKER_OVERHEAD_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|,
name|REQUEST_CIRCUIT_BREAKER_TYPE_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|parentSettings
operator|=
operator|new
name|BreakerSettings
argument_list|(
name|CircuitBreaker
operator|.
name|PARENT
argument_list|,
name|TOTAL_CIRCUIT_BREAKER_LIMIT_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
operator|.
name|bytes
argument_list|()
argument_list|,
literal|1.0
argument_list|,
name|CircuitBreaker
operator|.
name|Type
operator|.
name|PARENT
argument_list|)
expr_stmt|;
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
literal|"parent circuit breaker with settings {}"
argument_list|,
name|this
operator|.
name|parentSettings
argument_list|)
expr_stmt|;
block|}
name|registerBreaker
argument_list|(
name|this
operator|.
name|requestSettings
argument_list|)
expr_stmt|;
name|registerBreaker
argument_list|(
name|this
operator|.
name|fielddataSettings
argument_list|)
expr_stmt|;
name|clusterSettings
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|TOTAL_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
name|this
operator|::
name|setTotalCircuitBreakerLimit
argument_list|,
name|this
operator|::
name|validateTotalCircuitBreakerLimit
argument_list|)
expr_stmt|;
name|clusterSettings
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|FIELDDATA_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
name|FIELDDATA_CIRCUIT_BREAKER_OVERHEAD_SETTING
argument_list|,
name|this
operator|::
name|setFieldDataBreakerLimit
argument_list|)
expr_stmt|;
name|clusterSettings
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|REQUEST_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
name|REQUEST_CIRCUIT_BREAKER_OVERHEAD_SETTING
argument_list|,
name|this
operator|::
name|setRequestBreakerLimit
argument_list|)
expr_stmt|;
block|}
DECL|method|setRequestBreakerLimit
specifier|private
name|void
name|setRequestBreakerLimit
parameter_list|(
name|ByteSizeValue
name|newRequestMax
parameter_list|,
name|Double
name|newRequestOverhead
parameter_list|)
block|{
name|BreakerSettings
name|newRequestSettings
init|=
operator|new
name|BreakerSettings
argument_list|(
name|CircuitBreaker
operator|.
name|REQUEST
argument_list|,
name|newRequestMax
operator|.
name|bytes
argument_list|()
argument_list|,
name|newRequestOverhead
argument_list|,
name|HierarchyCircuitBreakerService
operator|.
name|this
operator|.
name|requestSettings
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|registerBreaker
argument_list|(
name|newRequestSettings
argument_list|)
expr_stmt|;
name|HierarchyCircuitBreakerService
operator|.
name|this
operator|.
name|requestSettings
operator|=
name|newRequestSettings
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Updated breaker settings request: {}"
argument_list|,
name|newRequestSettings
argument_list|)
expr_stmt|;
block|}
DECL|method|setFieldDataBreakerLimit
specifier|private
name|void
name|setFieldDataBreakerLimit
parameter_list|(
name|ByteSizeValue
name|newFielddataMax
parameter_list|,
name|Double
name|newFielddataOverhead
parameter_list|)
block|{
name|long
name|newFielddataLimitBytes
init|=
name|newFielddataMax
operator|==
literal|null
condition|?
name|HierarchyCircuitBreakerService
operator|.
name|this
operator|.
name|fielddataSettings
operator|.
name|getLimit
argument_list|()
else|:
name|newFielddataMax
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|newFielddataOverhead
operator|=
name|newFielddataOverhead
operator|==
literal|null
condition|?
name|HierarchyCircuitBreakerService
operator|.
name|this
operator|.
name|fielddataSettings
operator|.
name|getOverhead
argument_list|()
else|:
name|newFielddataOverhead
expr_stmt|;
name|BreakerSettings
name|newFielddataSettings
init|=
operator|new
name|BreakerSettings
argument_list|(
name|CircuitBreaker
operator|.
name|FIELDDATA
argument_list|,
name|newFielddataLimitBytes
argument_list|,
name|newFielddataOverhead
argument_list|,
name|HierarchyCircuitBreakerService
operator|.
name|this
operator|.
name|fielddataSettings
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|registerBreaker
argument_list|(
name|newFielddataSettings
argument_list|)
expr_stmt|;
name|HierarchyCircuitBreakerService
operator|.
name|this
operator|.
name|fielddataSettings
operator|=
name|newFielddataSettings
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Updated breaker settings field data: {}"
argument_list|,
name|newFielddataSettings
argument_list|)
expr_stmt|;
block|}
DECL|method|validateTotalCircuitBreakerLimit
specifier|private
name|boolean
name|validateTotalCircuitBreakerLimit
parameter_list|(
name|ByteSizeValue
name|byteSizeValue
parameter_list|)
block|{
name|BreakerSettings
name|newParentSettings
init|=
operator|new
name|BreakerSettings
argument_list|(
name|CircuitBreaker
operator|.
name|PARENT
argument_list|,
name|byteSizeValue
operator|.
name|bytes
argument_list|()
argument_list|,
literal|1.0
argument_list|,
name|CircuitBreaker
operator|.
name|Type
operator|.
name|PARENT
argument_list|)
decl_stmt|;
name|validateSettings
argument_list|(
operator|new
name|BreakerSettings
index|[]
block|{
name|newParentSettings
block|}
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|setTotalCircuitBreakerLimit
specifier|private
name|void
name|setTotalCircuitBreakerLimit
parameter_list|(
name|ByteSizeValue
name|byteSizeValue
parameter_list|)
block|{
name|BreakerSettings
name|newParentSettings
init|=
operator|new
name|BreakerSettings
argument_list|(
name|CircuitBreaker
operator|.
name|PARENT
argument_list|,
name|byteSizeValue
operator|.
name|bytes
argument_list|()
argument_list|,
literal|1.0
argument_list|,
name|CircuitBreaker
operator|.
name|Type
operator|.
name|PARENT
argument_list|)
decl_stmt|;
name|this
operator|.
name|parentSettings
operator|=
name|newParentSettings
expr_stmt|;
block|}
comment|/**      * Validate that child settings are valid      */
DECL|method|validateSettings
specifier|public
specifier|static
name|void
name|validateSettings
parameter_list|(
name|BreakerSettings
index|[]
name|childrenSettings
parameter_list|)
throws|throws
name|IllegalStateException
block|{
for|for
control|(
name|BreakerSettings
name|childSettings
range|:
name|childrenSettings
control|)
block|{
comment|// If the child is disabled, ignore it
if|if
condition|(
name|childSettings
operator|.
name|getLimit
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|childSettings
operator|.
name|getOverhead
argument_list|()
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Child breaker overhead "
operator|+
name|childSettings
operator|+
literal|" must be non-negative"
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getBreaker
specifier|public
name|CircuitBreaker
name|getBreaker
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|this
operator|.
name|breakers
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|stats
specifier|public
name|AllCircuitBreakerStats
name|stats
parameter_list|()
block|{
name|long
name|parentEstimated
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|CircuitBreakerStats
argument_list|>
name|allStats
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Gather the "estimated" count for the parent breaker by adding the
comment|// estimations for each individual breaker
for|for
control|(
name|CircuitBreaker
name|breaker
range|:
name|this
operator|.
name|breakers
operator|.
name|values
argument_list|()
control|)
block|{
name|allStats
operator|.
name|add
argument_list|(
name|stats
argument_list|(
name|breaker
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|parentEstimated
operator|+=
name|breaker
operator|.
name|getUsed
argument_list|()
expr_stmt|;
block|}
comment|// Manually add the parent breaker settings since they aren't part of the breaker map
name|allStats
operator|.
name|add
argument_list|(
operator|new
name|CircuitBreakerStats
argument_list|(
name|CircuitBreaker
operator|.
name|PARENT
argument_list|,
name|parentSettings
operator|.
name|getLimit
argument_list|()
argument_list|,
name|parentEstimated
argument_list|,
literal|1.0
argument_list|,
name|parentTripCount
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|AllCircuitBreakerStats
argument_list|(
name|allStats
operator|.
name|toArray
argument_list|(
operator|new
name|CircuitBreakerStats
index|[
name|allStats
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|stats
specifier|public
name|CircuitBreakerStats
name|stats
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|CircuitBreaker
name|breaker
init|=
name|this
operator|.
name|breakers
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
operator|new
name|CircuitBreakerStats
argument_list|(
name|breaker
operator|.
name|getName
argument_list|()
argument_list|,
name|breaker
operator|.
name|getLimit
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
argument_list|,
name|breaker
operator|.
name|getTrippedCount
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Checks whether the parent breaker has been tripped      */
DECL|method|checkParentLimit
specifier|public
name|void
name|checkParentLimit
parameter_list|(
name|String
name|label
parameter_list|)
throws|throws
name|CircuitBreakingException
block|{
name|long
name|totalUsed
init|=
literal|0
decl_stmt|;
for|for
control|(
name|CircuitBreaker
name|breaker
range|:
name|this
operator|.
name|breakers
operator|.
name|values
argument_list|()
control|)
block|{
name|totalUsed
operator|+=
operator|(
name|breaker
operator|.
name|getUsed
argument_list|()
operator|*
name|breaker
operator|.
name|getOverhead
argument_list|()
operator|)
expr_stmt|;
block|}
name|long
name|parentLimit
init|=
name|this
operator|.
name|parentSettings
operator|.
name|getLimit
argument_list|()
decl_stmt|;
if|if
condition|(
name|totalUsed
operator|>
name|parentLimit
condition|)
block|{
name|this
operator|.
name|parentTripCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|CircuitBreakingException
argument_list|(
literal|"[parent] Data too large, data for ["
operator|+
name|label
operator|+
literal|"] would be larger than limit of ["
operator|+
name|parentLimit
operator|+
literal|"/"
operator|+
operator|new
name|ByteSizeValue
argument_list|(
name|parentLimit
argument_list|)
operator|+
literal|"]"
argument_list|,
name|totalUsed
argument_list|,
name|parentLimit
argument_list|)
throw|;
block|}
block|}
comment|/**      * Allows to register a custom circuit breaker.      * Warning: Will overwrite any existing custom breaker with the same name.      */
annotation|@
name|Override
DECL|method|registerBreaker
specifier|public
name|void
name|registerBreaker
parameter_list|(
name|BreakerSettings
name|breakerSettings
parameter_list|)
block|{
comment|// Validate the settings
name|validateSettings
argument_list|(
operator|new
name|BreakerSettings
index|[]
block|{
name|breakerSettings
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|breakerSettings
operator|.
name|getType
argument_list|()
operator|==
name|CircuitBreaker
operator|.
name|Type
operator|.
name|NOOP
condition|)
block|{
name|CircuitBreaker
name|breaker
init|=
operator|new
name|NoopCircuitBreaker
argument_list|(
name|breakerSettings
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|breakers
operator|.
name|put
argument_list|(
name|breakerSettings
operator|.
name|getName
argument_list|()
argument_list|,
name|breaker
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CircuitBreaker
name|oldBreaker
decl_stmt|;
name|CircuitBreaker
name|breaker
init|=
operator|new
name|ChildMemoryCircuitBreaker
argument_list|(
name|breakerSettings
argument_list|,
name|Loggers
operator|.
name|getLogger
argument_list|(
name|CHILD_LOGGER_PREFIX
operator|+
name|breakerSettings
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|this
argument_list|,
name|breakerSettings
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|oldBreaker
operator|=
name|breakers
operator|.
name|putIfAbsent
argument_list|(
name|breakerSettings
operator|.
name|getName
argument_list|()
argument_list|,
name|breaker
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldBreaker
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|breaker
operator|=
operator|new
name|ChildMemoryCircuitBreaker
argument_list|(
name|breakerSettings
argument_list|,
operator|(
name|ChildMemoryCircuitBreaker
operator|)
name|oldBreaker
argument_list|,
name|Loggers
operator|.
name|getLogger
argument_list|(
name|CHILD_LOGGER_PREFIX
operator|+
name|breakerSettings
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|this
argument_list|,
name|breakerSettings
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|breakers
operator|.
name|replace
argument_list|(
name|breakerSettings
operator|.
name|getName
argument_list|()
argument_list|,
name|oldBreaker
argument_list|,
name|breaker
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

