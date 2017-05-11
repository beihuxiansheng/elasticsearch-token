begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket
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
name|search
operator|.
name|aggregations
operator|.
name|Aggregation
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
name|Aggregations
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
name|HasAggregations
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
comment|/**  * An aggregation that returns multiple buckets  */
end_comment

begin_interface
DECL|interface|MultiBucketsAggregation
specifier|public
interface|interface
name|MultiBucketsAggregation
extends|extends
name|Aggregation
block|{
comment|/**      * A bucket represents a criteria to which all documents that fall in it adhere to. It is also uniquely identified      * by a key, and can potentially hold sub-aggregations computed over all documents in it.      */
DECL|interface|Bucket
interface|interface
name|Bucket
extends|extends
name|HasAggregations
extends|,
name|ToXContent
block|{
comment|/**          * @return The key associated with the bucket          */
DECL|method|getKey
name|Object
name|getKey
parameter_list|()
function_decl|;
comment|/**          * @return The key associated with the bucket as a string          */
DECL|method|getKeyAsString
name|String
name|getKeyAsString
parameter_list|()
function_decl|;
comment|/**          * @return The number of documents that fall within this bucket          */
DECL|method|getDocCount
name|long
name|getDocCount
parameter_list|()
function_decl|;
comment|/**          * @return  The sub-aggregations of this bucket          */
annotation|@
name|Override
DECL|method|getAggregations
name|Aggregations
name|getAggregations
parameter_list|()
function_decl|;
block|}
comment|/**      * @return  The buckets of this aggregation.      */
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

