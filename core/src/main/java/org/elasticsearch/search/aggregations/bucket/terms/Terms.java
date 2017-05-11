begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.terms
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
name|terms
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
comment|/**  * A {@code terms} aggregation. Defines multiple bucket, each associated with a unique term for a specific field.  * All documents in a bucket has the bucket's term in that field.  */
end_comment

begin_interface
DECL|interface|Terms
specifier|public
interface|interface
name|Terms
extends|extends
name|MultiBucketsAggregation
block|{
comment|/**      * A bucket that is associated with a single term      */
DECL|interface|Bucket
interface|interface
name|Bucket
extends|extends
name|MultiBucketsAggregation
operator|.
name|Bucket
block|{
DECL|method|getKeyAsNumber
name|Number
name|getKeyAsNumber
parameter_list|()
function_decl|;
DECL|method|getDocCountError
name|long
name|getDocCountError
parameter_list|()
function_decl|;
block|}
comment|/**      * Return the sorted list of the buckets in this terms aggregation.      */
annotation|@
name|Override
DECL|method|getBuckets
name|List
argument_list|<
name|?
extends|extends
name|Bucket
argument_list|>
name|getBuckets
parameter_list|()
function_decl|;
comment|/**      * Get the bucket for the given term, or null if there is no such bucket.      */
DECL|method|getBucketByKey
name|Bucket
name|getBucketByKey
parameter_list|(
name|String
name|term
parameter_list|)
function_decl|;
comment|/**      * Get an upper bound of the error on document counts in this aggregation.      */
DECL|method|getDocCountError
name|long
name|getDocCountError
parameter_list|()
function_decl|;
comment|/**      * Return the sum of the document counts of all buckets that did not make      * it to the top buckets.      */
DECL|method|getSumOfOtherDocCounts
name|long
name|getSumOfOtherDocCounts
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

