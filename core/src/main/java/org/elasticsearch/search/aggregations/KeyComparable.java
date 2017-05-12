begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
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
operator|.
name|Bucket
import|;
end_import

begin_comment
comment|/**  * Defines behavior for comparing {@link Bucket#getKey() bucket keys} to imposes a total ordering  * of buckets of the same type.  *  * @param<T> {@link Bucket} of the same type that also implements {@link KeyComparable}.  * @see BucketOrder#key(boolean)  */
end_comment

begin_interface
DECL|interface|KeyComparable
specifier|public
interface|interface
name|KeyComparable
parameter_list|<
name|T
extends|extends
name|Bucket
operator|&
name|KeyComparable
parameter_list|<
name|T
parameter_list|>
parameter_list|>
block|{
comment|/**      * Compare this {@link Bucket}s {@link Bucket#getKey() key} with another bucket.      *      * @param other the bucket that contains the key to compare to.      * @return a negative integer, zero, or a positive integer as this buckets key      * is less than, equal to, or greater than the other buckets key.      * @see Comparable#compareTo(Object)      */
DECL|method|compareKey
name|int
name|compareKey
parameter_list|(
name|T
name|other
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

