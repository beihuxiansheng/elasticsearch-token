begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.profile.aggregation
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|profile
operator|.
name|aggregation
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
name|search
operator|.
name|Scorer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|LeafBucketCollector
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

begin_class
DECL|class|ProfilingLeafBucketCollector
specifier|public
class|class
name|ProfilingLeafBucketCollector
extends|extends
name|LeafBucketCollector
block|{
DECL|field|delegate
specifier|private
name|LeafBucketCollector
name|delegate
decl_stmt|;
DECL|field|profileBreakdown
specifier|private
name|AggregationProfileBreakdown
name|profileBreakdown
decl_stmt|;
DECL|method|ProfilingLeafBucketCollector
specifier|public
name|ProfilingLeafBucketCollector
parameter_list|(
name|LeafBucketCollector
name|delegate
parameter_list|,
name|AggregationProfileBreakdown
name|profileBreakdown
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|profileBreakdown
operator|=
name|profileBreakdown
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|long
name|bucket
parameter_list|)
throws|throws
name|IOException
block|{
name|profileBreakdown
operator|.
name|startTime
argument_list|(
name|AggregationTimingType
operator|.
name|COLLECT
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
name|profileBreakdown
operator|.
name|stopAndRecordTime
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

