begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.metrics
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|metrics
package|;
end_package

begin_import
import|import
name|jsr166e
operator|.
name|LongAdder
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
name|FutureUtils
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
name|ScheduledExecutorService
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
name|ScheduledFuture
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
comment|/**  * A meter metric which measures mean throughput and one-, five-, and  * fifteen-minute exponentially-weighted moving average throughputs.  *  * @see<a href="http://en.wikipedia.org/wiki/Moving_average#Exponential_moving_average">EMA</a>  *<p/>  *      taken from codahale metric module, replaced with LongAdder  */
end_comment

begin_class
DECL|class|MeterMetric
specifier|public
class|class
name|MeterMetric
implements|implements
name|Metric
block|{
DECL|field|INTERVAL
specifier|private
specifier|static
specifier|final
name|long
name|INTERVAL
init|=
literal|5
decl_stmt|;
comment|// seconds
DECL|field|m1Rate
specifier|private
specifier|final
name|EWMA
name|m1Rate
init|=
name|EWMA
operator|.
name|oneMinuteEWMA
argument_list|()
decl_stmt|;
DECL|field|m5Rate
specifier|private
specifier|final
name|EWMA
name|m5Rate
init|=
name|EWMA
operator|.
name|fiveMinuteEWMA
argument_list|()
decl_stmt|;
DECL|field|m15Rate
specifier|private
specifier|final
name|EWMA
name|m15Rate
init|=
name|EWMA
operator|.
name|fifteenMinuteEWMA
argument_list|()
decl_stmt|;
DECL|field|count
specifier|private
specifier|final
name|LongAdder
name|count
init|=
operator|new
name|LongAdder
argument_list|()
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
DECL|field|rateUnit
specifier|private
specifier|final
name|TimeUnit
name|rateUnit
decl_stmt|;
DECL|field|future
specifier|private
specifier|final
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|future
decl_stmt|;
DECL|method|MeterMetric
specifier|public
name|MeterMetric
parameter_list|(
name|ScheduledExecutorService
name|tickThread
parameter_list|,
name|TimeUnit
name|rateUnit
parameter_list|)
block|{
name|this
operator|.
name|rateUnit
operator|=
name|rateUnit
expr_stmt|;
name|this
operator|.
name|future
operator|=
name|tickThread
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|tick
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|INTERVAL
argument_list|,
name|INTERVAL
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
DECL|method|rateUnit
specifier|public
name|TimeUnit
name|rateUnit
parameter_list|()
block|{
return|return
name|rateUnit
return|;
block|}
comment|/**      * Updates the moving averages.      */
DECL|method|tick
name|void
name|tick
parameter_list|()
block|{
name|m1Rate
operator|.
name|tick
argument_list|()
expr_stmt|;
name|m5Rate
operator|.
name|tick
argument_list|()
expr_stmt|;
name|m15Rate
operator|.
name|tick
argument_list|()
expr_stmt|;
block|}
comment|/**      * Mark the occurrence of an event.      */
DECL|method|mark
specifier|public
name|void
name|mark
parameter_list|()
block|{
name|mark
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Mark the occurrence of a given number of events.      *      * @param n the number of events      */
DECL|method|mark
specifier|public
name|void
name|mark
parameter_list|(
name|long
name|n
parameter_list|)
block|{
name|count
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|m1Rate
operator|.
name|update
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|m5Rate
operator|.
name|update
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|m15Rate
operator|.
name|update
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
DECL|method|count
specifier|public
name|long
name|count
parameter_list|()
block|{
return|return
name|count
operator|.
name|sum
argument_list|()
return|;
block|}
DECL|method|fifteenMinuteRate
specifier|public
name|double
name|fifteenMinuteRate
parameter_list|()
block|{
return|return
name|m15Rate
operator|.
name|rate
argument_list|(
name|rateUnit
argument_list|)
return|;
block|}
DECL|method|fiveMinuteRate
specifier|public
name|double
name|fiveMinuteRate
parameter_list|()
block|{
return|return
name|m5Rate
operator|.
name|rate
argument_list|(
name|rateUnit
argument_list|)
return|;
block|}
DECL|method|meanRate
specifier|public
name|double
name|meanRate
parameter_list|()
block|{
name|long
name|count
init|=
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
literal|0.0
return|;
block|}
else|else
block|{
specifier|final
name|long
name|elapsed
init|=
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
operator|)
decl_stmt|;
return|return
name|convertNsRate
argument_list|(
name|count
operator|/
operator|(
name|double
operator|)
name|elapsed
argument_list|)
return|;
block|}
block|}
DECL|method|oneMinuteRate
specifier|public
name|double
name|oneMinuteRate
parameter_list|()
block|{
return|return
name|m1Rate
operator|.
name|rate
argument_list|(
name|rateUnit
argument_list|)
return|;
block|}
DECL|method|convertNsRate
specifier|private
name|double
name|convertNsRate
parameter_list|(
name|double
name|ratePerNs
parameter_list|)
block|{
return|return
name|ratePerNs
operator|*
operator|(
name|double
operator|)
name|rateUnit
operator|.
name|toNanos
argument_list|(
literal|1
argument_list|)
return|;
block|}
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|FutureUtils
operator|.
name|cancel
argument_list|(
name|future
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

