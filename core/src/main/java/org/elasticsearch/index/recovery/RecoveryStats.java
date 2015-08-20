begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.recovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|recovery
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|StreamOutput
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
name|Streamable
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
name|ToXContent
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
name|XContentBuilder
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
name|XContentBuilderString
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
comment|/**  * Recovery related statistics, starting at the shard level and allowing aggregation to  * indices and node level  */
end_comment

begin_class
DECL|class|RecoveryStats
specifier|public
class|class
name|RecoveryStats
implements|implements
name|ToXContent
implements|,
name|Streamable
block|{
DECL|field|currentAsSource
specifier|private
specifier|final
name|AtomicInteger
name|currentAsSource
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|currentAsTarget
specifier|private
specifier|final
name|AtomicInteger
name|currentAsTarget
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|throttleTimeInNanos
specifier|private
specifier|final
name|AtomicLong
name|throttleTimeInNanos
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|method|RecoveryStats
specifier|public
name|RecoveryStats
parameter_list|()
block|{     }
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|RecoveryStats
name|recoveryStats
parameter_list|)
block|{
if|if
condition|(
name|recoveryStats
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|currentAsSource
operator|.
name|addAndGet
argument_list|(
name|recoveryStats
operator|.
name|currentAsSource
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentAsTarget
operator|.
name|addAndGet
argument_list|(
name|recoveryStats
operator|.
name|currentAsTarget
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|throttleTimeInNanos
operator|.
name|addAndGet
argument_list|(
name|recoveryStats
operator|.
name|throttleTime
argument_list|()
operator|.
name|nanos
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * add statistics that should be accumulated about old shards after they have been      * deleted or relocated      */
DECL|method|addAsOld
specifier|public
name|void
name|addAsOld
parameter_list|(
name|RecoveryStats
name|recoveryStats
parameter_list|)
block|{
if|if
condition|(
name|recoveryStats
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|throttleTimeInNanos
operator|.
name|addAndGet
argument_list|(
name|recoveryStats
operator|.
name|throttleTime
argument_list|()
operator|.
name|nanos
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Number of ongoing recoveries for which a shard serves as a source      */
DECL|method|currentAsSource
specifier|public
name|int
name|currentAsSource
parameter_list|()
block|{
return|return
name|currentAsSource
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * Number of ongoing recoveries for which a shard serves as a target      */
DECL|method|currentAsTarget
specifier|public
name|int
name|currentAsTarget
parameter_list|()
block|{
return|return
name|currentAsTarget
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * Total time recoveries waited due to throttling      */
DECL|method|throttleTime
specifier|public
name|TimeValue
name|throttleTime
parameter_list|()
block|{
return|return
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|throttleTimeInNanos
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|method|incCurrentAsTarget
specifier|public
name|void
name|incCurrentAsTarget
parameter_list|()
block|{
name|currentAsTarget
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|decCurrentAsTarget
specifier|public
name|void
name|decCurrentAsTarget
parameter_list|()
block|{
name|currentAsTarget
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|incCurrentAsSource
specifier|public
name|void
name|incCurrentAsSource
parameter_list|()
block|{
name|currentAsSource
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|decCurrentAsSource
specifier|public
name|void
name|decCurrentAsSource
parameter_list|()
block|{
name|currentAsSource
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|addThrottleTime
specifier|public
name|void
name|addThrottleTime
parameter_list|(
name|long
name|nanos
parameter_list|)
block|{
name|throttleTimeInNanos
operator|.
name|addAndGet
argument_list|(
name|nanos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|RECOVERY
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|CURRENT_AS_SOURCE
argument_list|,
name|currentAsSource
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|CURRENT_AS_TARGET
argument_list|,
name|currentAsTarget
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|timeValueField
argument_list|(
name|Fields
operator|.
name|THROTTLE_TIME_IN_MILLIS
argument_list|,
name|Fields
operator|.
name|THROTTLE_TIME
argument_list|,
name|throttleTime
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|readRecoveryStats
specifier|public
specifier|static
name|RecoveryStats
name|readRecoveryStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|RecoveryStats
name|stats
init|=
operator|new
name|RecoveryStats
argument_list|()
decl_stmt|;
name|stats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|stats
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|RECOVERY
specifier|static
specifier|final
name|XContentBuilderString
name|RECOVERY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"recovery"
argument_list|)
decl_stmt|;
DECL|field|CURRENT_AS_SOURCE
specifier|static
specifier|final
name|XContentBuilderString
name|CURRENT_AS_SOURCE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"current_as_source"
argument_list|)
decl_stmt|;
DECL|field|CURRENT_AS_TARGET
specifier|static
specifier|final
name|XContentBuilderString
name|CURRENT_AS_TARGET
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"current_as_target"
argument_list|)
decl_stmt|;
DECL|field|THROTTLE_TIME
specifier|static
specifier|final
name|XContentBuilderString
name|THROTTLE_TIME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"throttle_time"
argument_list|)
decl_stmt|;
DECL|field|THROTTLE_TIME_IN_MILLIS
specifier|static
specifier|final
name|XContentBuilderString
name|THROTTLE_TIME_IN_MILLIS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"throttle_time_in_millis"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|currentAsSource
operator|.
name|set
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
name|currentAsTarget
operator|.
name|set
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
name|throttleTimeInNanos
operator|.
name|set
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|currentAsSource
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|currentAsTarget
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|throttleTimeInNanos
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"recoveryStats, currentAsSource ["
operator|+
name|currentAsSource
argument_list|()
operator|+
literal|"],currentAsTarget ["
operator|+
name|currentAsTarget
argument_list|()
operator|+
literal|"], throttle ["
operator|+
name|throttleTime
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit
