begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.histogram
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|histogram
package|;
end_package

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
name|InternalAggregation
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
name|InternalAggregations
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
name|bucket
operator|.
name|MultiBucketsAggregation
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

begin_comment
comment|/** Implemented by histogram aggregations and used by pipeline aggregations to insert buckets. */
end_comment

begin_comment
comment|// public so that pipeline aggs can use this API: can we fix it?
end_comment

begin_interface
DECL|interface|HistogramFactory
specifier|public
interface|interface
name|HistogramFactory
block|{
comment|/** Get the key for the given bucket. Date histograms must return the      *  number of millis since Epoch of the bucket key while numeric histograms      *  must return the double value of the key. */
DECL|method|getKey
name|Number
name|getKey
parameter_list|(
name|MultiBucketsAggregation
operator|.
name|Bucket
name|bucket
parameter_list|)
function_decl|;
comment|/** Given a key returned by {@link #getKey}, compute the lowest key that is       *  greater than it. */
DECL|method|nextKey
name|Number
name|nextKey
parameter_list|(
name|Number
name|key
parameter_list|)
function_decl|;
comment|/** Create an {@link InternalAggregation} object that wraps the given buckets. */
DECL|method|createAggregation
name|InternalAggregation
name|createAggregation
parameter_list|(
name|List
argument_list|<
name|MultiBucketsAggregation
operator|.
name|Bucket
argument_list|>
name|buckets
parameter_list|)
function_decl|;
comment|/** Create a {@link MultiBucketsAggregation.Bucket} object that wraps the      *  given key, document count and aggregations. */
DECL|method|createBucket
name|MultiBucketsAggregation
operator|.
name|Bucket
name|createBucket
parameter_list|(
name|Number
name|key
parameter_list|,
name|long
name|docCount
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

