begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.range.geodistance
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
operator|.
name|geodistance
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
name|range
operator|.
name|RangeBase
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|GeoDistance
specifier|public
interface|interface
name|GeoDistance
extends|extends
name|RangeBase
argument_list|<
name|GeoDistance
operator|.
name|Bucket
argument_list|>
block|{
DECL|interface|Bucket
specifier|public
specifier|static
interface|interface
name|Bucket
extends|extends
name|RangeBase
operator|.
name|Bucket
block|{}
block|}
end_interface

end_unit

