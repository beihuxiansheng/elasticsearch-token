begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|metrics
operator|.
name|CounterMetric
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
name|metrics
operator|.
name|MeanMetric
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * An extension to the {@link ConcurrentMergeScheduler} that provides tracking on merge times, total  * and current merges.  */
end_comment

begin_class
DECL|class|TrackingConcurrentMergeScheduler
specifier|public
class|class
name|TrackingConcurrentMergeScheduler
extends|extends
name|ConcurrentMergeScheduler
block|{
DECL|field|logger
specifier|protected
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|totalMerges
specifier|private
specifier|final
name|MeanMetric
name|totalMerges
init|=
operator|new
name|MeanMetric
argument_list|()
decl_stmt|;
DECL|field|totalMergesNumDocs
specifier|private
specifier|final
name|CounterMetric
name|totalMergesNumDocs
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|totalMergesSizeInBytes
specifier|private
specifier|final
name|CounterMetric
name|totalMergesSizeInBytes
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|currentMerges
specifier|private
specifier|final
name|CounterMetric
name|currentMerges
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|currentMergesNumDocs
specifier|private
specifier|final
name|CounterMetric
name|currentMergesNumDocs
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|currentMergesSizeInBytes
specifier|private
specifier|final
name|CounterMetric
name|currentMergesSizeInBytes
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|method|TrackingConcurrentMergeScheduler
specifier|public
name|TrackingConcurrentMergeScheduler
parameter_list|(
name|ESLogger
name|logger
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
DECL|method|totalMerges
specifier|public
name|long
name|totalMerges
parameter_list|()
block|{
return|return
name|totalMerges
operator|.
name|count
argument_list|()
return|;
block|}
DECL|method|totalMergeTime
specifier|public
name|long
name|totalMergeTime
parameter_list|()
block|{
return|return
name|totalMerges
operator|.
name|sum
argument_list|()
return|;
block|}
DECL|method|totalMergeNumDocs
specifier|public
name|long
name|totalMergeNumDocs
parameter_list|()
block|{
return|return
name|totalMergesNumDocs
operator|.
name|count
argument_list|()
return|;
block|}
DECL|method|totalMergeSizeInBytes
specifier|public
name|long
name|totalMergeSizeInBytes
parameter_list|()
block|{
return|return
name|totalMergesSizeInBytes
operator|.
name|count
argument_list|()
return|;
block|}
DECL|method|currentMerges
specifier|public
name|long
name|currentMerges
parameter_list|()
block|{
return|return
name|currentMerges
operator|.
name|count
argument_list|()
return|;
block|}
DECL|method|currentMergesNumDocs
specifier|public
name|long
name|currentMergesNumDocs
parameter_list|()
block|{
return|return
name|currentMergesNumDocs
operator|.
name|count
argument_list|()
return|;
block|}
DECL|method|currentMergesSizeInBytes
specifier|public
name|long
name|currentMergesSizeInBytes
parameter_list|()
block|{
return|return
name|currentMergesSizeInBytes
operator|.
name|count
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doMerge
specifier|protected
name|void
name|doMerge
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|totalNumDocs
init|=
name|merge
operator|.
name|totalNumDocs
argument_list|()
decl_stmt|;
name|long
name|totalSizeInBytes
init|=
name|merge
operator|.
name|totalBytesSize
argument_list|()
decl_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|currentMerges
operator|.
name|inc
argument_list|()
expr_stmt|;
name|currentMergesNumDocs
operator|.
name|inc
argument_list|(
name|totalNumDocs
argument_list|)
expr_stmt|;
name|currentMergesSizeInBytes
operator|.
name|inc
argument_list|(
name|totalSizeInBytes
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
literal|"merge [{}] starting..."
argument_list|,
name|merge
operator|.
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|TrackingMergeScheduler
operator|.
name|setCurrentMerge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
name|super
operator|.
name|doMerge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|TrackingMergeScheduler
operator|.
name|removeCurrentMerge
argument_list|()
expr_stmt|;
name|long
name|took
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
decl_stmt|;
name|currentMerges
operator|.
name|dec
argument_list|()
expr_stmt|;
name|currentMergesNumDocs
operator|.
name|dec
argument_list|(
name|totalNumDocs
argument_list|)
expr_stmt|;
name|currentMergesSizeInBytes
operator|.
name|dec
argument_list|(
name|totalSizeInBytes
argument_list|)
expr_stmt|;
name|totalMergesNumDocs
operator|.
name|inc
argument_list|(
name|totalNumDocs
argument_list|)
expr_stmt|;
name|totalMergesSizeInBytes
operator|.
name|inc
argument_list|(
name|totalSizeInBytes
argument_list|)
expr_stmt|;
name|totalMerges
operator|.
name|inc
argument_list|(
name|took
argument_list|)
expr_stmt|;
if|if
condition|(
name|took
operator|>
literal|20000
condition|)
block|{
comment|// if more than 20 seconds, DEBUG log it
name|logger
operator|.
name|debug
argument_list|(
literal|"merge [{}] done, took [{}]"
argument_list|,
name|merge
operator|.
name|info
operator|.
name|name
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|took
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
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
literal|"merge [{}] done, took [{}]"
argument_list|,
name|merge
operator|.
name|info
operator|.
name|name
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|took
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

