begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.breaker
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
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
name|logging
operator|.
name|ESLogger
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
name|breaker
operator|.
name|BreakerSettings
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
name|breaker
operator|.
name|HierarchyCircuitBreakerService
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
comment|/**  * Breaker that will check a parent's when incrementing  */
end_comment

begin_class
DECL|class|ChildMemoryCircuitBreaker
specifier|public
class|class
name|ChildMemoryCircuitBreaker
implements|implements
name|CircuitBreaker
block|{
DECL|field|memoryBytesLimit
specifier|private
specifier|final
name|long
name|memoryBytesLimit
decl_stmt|;
DECL|field|settings
specifier|private
specifier|final
name|BreakerSettings
name|settings
decl_stmt|;
DECL|field|overheadConstant
specifier|private
specifier|final
name|double
name|overheadConstant
decl_stmt|;
DECL|field|used
specifier|private
specifier|final
name|AtomicLong
name|used
decl_stmt|;
DECL|field|trippedCount
specifier|private
specifier|final
name|AtomicLong
name|trippedCount
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|parent
specifier|private
specifier|final
name|HierarchyCircuitBreakerService
name|parent
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|Name
name|name
decl_stmt|;
comment|/**      * Create a circuit breaker that will break if the number of estimated      * bytes grows above the limit. All estimations will be multiplied by      * the given overheadConstant. This breaker starts with 0 bytes used.      * @param settings settings to configure this breaker      * @param parent parent circuit breaker service to delegate tripped breakers to      * @param name the name of the breaker      */
DECL|method|ChildMemoryCircuitBreaker
specifier|public
name|ChildMemoryCircuitBreaker
parameter_list|(
name|BreakerSettings
name|settings
parameter_list|,
name|ESLogger
name|logger
parameter_list|,
name|HierarchyCircuitBreakerService
name|parent
parameter_list|,
name|Name
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|settings
argument_list|,
literal|null
argument_list|,
name|logger
argument_list|,
name|parent
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a circuit breaker that will break if the number of estimated      * bytes grows above the limit. All estimations will be multiplied by      * the given overheadConstant. Uses the given oldBreaker to initialize      * the starting offset.      * @param settings settings to configure this breaker      * @param parent parent circuit breaker service to delegate tripped breakers to      * @param name the name of the breaker      * @param oldBreaker the previous circuit breaker to inherit the used value from (starting offset)      */
DECL|method|ChildMemoryCircuitBreaker
specifier|public
name|ChildMemoryCircuitBreaker
parameter_list|(
name|BreakerSettings
name|settings
parameter_list|,
name|ChildMemoryCircuitBreaker
name|oldBreaker
parameter_list|,
name|ESLogger
name|logger
parameter_list|,
name|HierarchyCircuitBreakerService
name|parent
parameter_list|,
name|Name
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|memoryBytesLimit
operator|=
name|settings
operator|.
name|getLimit
argument_list|()
expr_stmt|;
name|this
operator|.
name|overheadConstant
operator|=
name|settings
operator|.
name|getOverhead
argument_list|()
expr_stmt|;
if|if
condition|(
name|oldBreaker
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|used
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|trippedCount
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|used
operator|=
name|oldBreaker
operator|.
name|used
expr_stmt|;
name|this
operator|.
name|trippedCount
operator|=
name|oldBreaker
operator|.
name|trippedCount
expr_stmt|;
block|}
name|this
operator|.
name|logger
operator|=
name|logger
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
literal|"creating ChildCircuitBreaker with settings {}"
argument_list|,
name|this
operator|.
name|settings
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
comment|/**      * Method used to trip the breaker, delegates to the parent to determine      * whether to trip the breaker or not      */
annotation|@
name|Override
DECL|method|circuitBreak
specifier|public
name|void
name|circuitBreak
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|long
name|bytesNeeded
parameter_list|)
block|{
name|this
operator|.
name|trippedCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|CircuitBreakingException
argument_list|(
literal|"["
operator|+
name|this
operator|.
name|name
operator|+
literal|"] Data too large, data for ["
operator|+
name|fieldName
operator|+
literal|"] would be larger than limit of ["
operator|+
name|memoryBytesLimit
operator|+
literal|"/"
operator|+
operator|new
name|ByteSizeValue
argument_list|(
name|memoryBytesLimit
argument_list|)
operator|+
literal|"]"
argument_list|,
name|bytesNeeded
argument_list|,
name|this
operator|.
name|memoryBytesLimit
argument_list|)
throw|;
block|}
comment|/**      * Add a number of bytes, tripping the circuit breaker if the aggregated      * estimates are above the limit. Automatically trips the breaker if the      * memory limit is set to 0. Will never trip the breaker if the limit is      * set< 0, but can still be used to aggregate estimations.      * @param bytes number of bytes to add to the breaker      * @return number of "used" bytes so far      * @throws CircuitBreakingException      */
annotation|@
name|Override
DECL|method|addEstimateBytesAndMaybeBreak
specifier|public
name|double
name|addEstimateBytesAndMaybeBreak
parameter_list|(
name|long
name|bytes
parameter_list|,
name|String
name|label
parameter_list|)
throws|throws
name|CircuitBreakingException
block|{
comment|// short-circuit on no data allowed, immediately throwing an exception
if|if
condition|(
name|memoryBytesLimit
operator|==
literal|0
condition|)
block|{
name|circuitBreak
argument_list|(
name|label
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
name|long
name|newUsed
decl_stmt|;
comment|// If there is no limit (-1), we can optimize a bit by using
comment|// .addAndGet() instead of looping (because we don't have to check a
comment|// limit), which makes the RamAccountingTermsEnum case faster.
if|if
condition|(
name|this
operator|.
name|memoryBytesLimit
operator|==
operator|-
literal|1
condition|)
block|{
name|newUsed
operator|=
name|this
operator|.
name|used
operator|.
name|addAndGet
argument_list|(
name|bytes
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
literal|"[{}] Adding [{}][{}] to used bytes [new used: [{}], limit: [-1b]]"
argument_list|,
name|this
operator|.
name|name
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|bytes
argument_list|)
argument_list|,
name|label
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|newUsed
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Otherwise, check the addition and commit the addition, looping if
comment|// there are conflicts. May result in additional logging, but it's
comment|// trace logging and shouldn't be counted on for additions.
name|long
name|currentUsed
decl_stmt|;
do|do
block|{
name|currentUsed
operator|=
name|this
operator|.
name|used
operator|.
name|get
argument_list|()
expr_stmt|;
name|newUsed
operator|=
name|currentUsed
operator|+
name|bytes
expr_stmt|;
name|long
name|newUsedWithOverhead
init|=
call|(
name|long
call|)
argument_list|(
name|newUsed
operator|*
name|overheadConstant
argument_list|)
decl_stmt|;
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
literal|"[{}] Adding [{}][{}] to used bytes [new used: [{}], limit: {} [{}], estimate: {} [{}]]"
argument_list|,
name|this
operator|.
name|name
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|bytes
argument_list|)
argument_list|,
name|label
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|newUsed
argument_list|)
argument_list|,
name|memoryBytesLimit
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|memoryBytesLimit
argument_list|)
argument_list|,
name|newUsedWithOverhead
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|newUsedWithOverhead
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|memoryBytesLimit
operator|>
literal|0
operator|&&
name|newUsedWithOverhead
operator|>
name|memoryBytesLimit
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[{}] New used memory {} [{}] from field [{}] would be larger than configured breaker: {} [{}], breaking"
argument_list|,
name|this
operator|.
name|name
argument_list|,
name|newUsedWithOverhead
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|newUsedWithOverhead
argument_list|)
argument_list|,
name|label
argument_list|,
name|memoryBytesLimit
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|memoryBytesLimit
argument_list|)
argument_list|)
expr_stmt|;
name|circuitBreak
argument_list|(
name|label
argument_list|,
name|newUsedWithOverhead
argument_list|)
expr_stmt|;
block|}
comment|// Attempt to set the new used value, but make sure it hasn't changed
comment|// underneath us, if it has, keep trying until we are able to set it
block|}
do|while
condition|(
operator|!
name|this
operator|.
name|used
operator|.
name|compareAndSet
argument_list|(
name|currentUsed
argument_list|,
name|newUsed
argument_list|)
condition|)
do|;
block|}
comment|// Additionally, we need to check that we haven't exceeded the parent's limit
try|try
block|{
name|parent
operator|.
name|checkParentLimit
argument_list|(
name|label
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CircuitBreakingException
name|e
parameter_list|)
block|{
comment|// If the parent breaker is tripped, this breaker has to be
comment|// adjusted back down because the allocation is "blocked" but the
comment|// breaker has already been incremented
name|this
operator|.
name|used
operator|.
name|addAndGet
argument_list|(
operator|-
name|bytes
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
return|return
name|newUsed
return|;
block|}
comment|/**      * Add an<b>exact</b> number of bytes, not checking for tripping the      * circuit breaker. This bypasses the overheadConstant multiplication.      *      * Also does not check with the parent breaker to see if the parent limit      * has been exceeded.      *      * @param bytes number of bytes to add to the breaker      * @return number of "used" bytes so far      */
annotation|@
name|Override
DECL|method|addWithoutBreaking
specifier|public
name|long
name|addWithoutBreaking
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|long
name|u
init|=
name|used
operator|.
name|addAndGet
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
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
literal|"[{}] Adjusted breaker by [{}] bytes, now [{}]"
argument_list|,
name|this
operator|.
name|name
argument_list|,
name|bytes
argument_list|,
name|u
argument_list|)
expr_stmt|;
block|}
assert|assert
name|u
operator|>=
literal|0
operator|:
literal|"Used bytes: ["
operator|+
name|u
operator|+
literal|"] must be>= 0"
assert|;
return|return
name|u
return|;
block|}
comment|/**      * @return the number of aggregated "used" bytes so far      */
annotation|@
name|Override
DECL|method|getUsed
specifier|public
name|long
name|getUsed
parameter_list|()
block|{
return|return
name|this
operator|.
name|used
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * @return the number of bytes that can be added before the breaker trips      */
annotation|@
name|Override
DECL|method|getLimit
specifier|public
name|long
name|getLimit
parameter_list|()
block|{
return|return
name|this
operator|.
name|memoryBytesLimit
return|;
block|}
comment|/**      * @return the constant multiplier the breaker uses for aggregations      */
annotation|@
name|Override
DECL|method|getOverhead
specifier|public
name|double
name|getOverhead
parameter_list|()
block|{
return|return
name|this
operator|.
name|overheadConstant
return|;
block|}
comment|/**      * @return the number of times the breaker has been tripped      */
annotation|@
name|Override
DECL|method|getTrippedCount
specifier|public
name|long
name|getTrippedCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|trippedCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * @return the name of the breaker      */
DECL|method|getName
specifier|public
name|Name
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
block|}
end_class

end_unit

