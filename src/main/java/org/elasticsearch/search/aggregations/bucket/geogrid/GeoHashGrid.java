begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.geogrid
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
name|geogrid
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
name|geo
operator|.
name|GeoPoint
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
name|Collection
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
comment|/**  * A {@code geohash_grid} aggregation. Defines multiple buckets, each representing a cell in a geo-grid of a specific  * precision.  */
end_comment

begin_interface
DECL|interface|GeoHashGrid
specifier|public
interface|interface
name|GeoHashGrid
extends|extends
name|MultiBucketsAggregation
block|{
comment|/**      * A bucket that is associated with a {@code geohash_grid} cell. The key of the bucket is the {@cod geohash} of the cell      */
DECL|interface|Bucket
specifier|public
specifier|static
interface|interface
name|Bucket
extends|extends
name|MultiBucketsAggregation
operator|.
name|Bucket
block|{
comment|/**          * @return  The geohash of the cell as a geo point          */
DECL|method|getKeyAsGeoPoint
name|GeoPoint
name|getKeyAsGeoPoint
parameter_list|()
function_decl|;
comment|/**          * @return  A numeric representation of the geohash of the cell          */
DECL|method|getKeyAsNumber
name|Number
name|getKeyAsNumber
parameter_list|()
function_decl|;
block|}
comment|/**      * @return  The buckets of this aggregation (each bucket representing a geohash grid cell)      */
annotation|@
name|Override
DECL|method|getBuckets
name|List
argument_list|<
name|Bucket
argument_list|>
name|getBuckets
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|getBucketByKey
name|Bucket
name|getBucketByKey
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|getBucketByKey
name|Bucket
name|getBucketByKey
parameter_list|(
name|Number
name|key
parameter_list|)
function_decl|;
DECL|method|getBucketByKey
name|Bucket
name|getBucketByKey
parameter_list|(
name|GeoPoint
name|key
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

