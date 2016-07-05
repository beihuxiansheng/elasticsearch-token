begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.range
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
name|range
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
comment|/**  * A {@code range} aggregation. Defines multiple buckets, each associated with a pre-defined value range of a field,  * and where the value of that fields in all documents in each bucket fall in the bucket's range.  */
end_comment

begin_interface
DECL|interface|Range
specifier|public
interface|interface
name|Range
extends|extends
name|MultiBucketsAggregation
block|{
comment|/**      * A bucket associated with a specific range      */
DECL|interface|Bucket
interface|interface
name|Bucket
extends|extends
name|MultiBucketsAggregation
operator|.
name|Bucket
block|{
comment|/**          * @return  The lower bound of the range          */
DECL|method|getFrom
name|Object
name|getFrom
parameter_list|()
function_decl|;
comment|/**          * @return The string value for the lower bound of the range          */
DECL|method|getFromAsString
name|String
name|getFromAsString
parameter_list|()
function_decl|;
comment|/**          * @return The upper bound of the range (excluding)          */
DECL|method|getTo
name|Object
name|getTo
parameter_list|()
function_decl|;
comment|/**          * @return The string value for the upper bound of the range (excluding)          */
DECL|method|getToAsString
name|String
name|getToAsString
parameter_list|()
function_decl|;
block|}
comment|/**      * Return the buckets of this range aggregation.      */
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
block|}
end_interface

end_unit

