begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|geo
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
name|index
operator|.
name|AtomicReaderContext
import|;
end_import

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
name|DocIdSet
import|;
end_import

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
name|Filter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Bits
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|FixedBitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|common
operator|.
name|lucene
operator|.
name|docset
operator|.
name|DocIdSets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|geo
operator|.
name|GeoPointFieldMapper
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
comment|/**  */
end_comment

begin_class
DECL|class|IndexedGeoBoundingBoxFilter
specifier|public
class|class
name|IndexedGeoBoundingBoxFilter
block|{
DECL|method|create
specifier|public
specifier|static
name|Filter
name|create
parameter_list|(
name|GeoPoint
name|topLeft
parameter_list|,
name|GeoPoint
name|bottomRight
parameter_list|,
name|GeoPointFieldMapper
name|fieldMapper
parameter_list|)
block|{
if|if
condition|(
operator|!
name|fieldMapper
operator|.
name|isEnableLatLon
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"lat/lon is not enabled (indexed) for field ["
operator|+
name|fieldMapper
operator|.
name|name
argument_list|()
operator|+
literal|"], can't use indexed filter on it"
argument_list|)
throw|;
block|}
comment|//checks to see if bounding box crosses 180 degrees
if|if
condition|(
name|topLeft
operator|.
name|lon
argument_list|()
operator|>
name|bottomRight
operator|.
name|lon
argument_list|()
condition|)
block|{
return|return
operator|new
name|LeftGeoBoundingBoxFilter
argument_list|(
name|topLeft
argument_list|,
name|bottomRight
argument_list|,
name|fieldMapper
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|RightGeoBoundingBoxFilter
argument_list|(
name|topLeft
argument_list|,
name|bottomRight
argument_list|,
name|fieldMapper
argument_list|)
return|;
block|}
block|}
DECL|class|LeftGeoBoundingBoxFilter
specifier|static
class|class
name|LeftGeoBoundingBoxFilter
extends|extends
name|Filter
block|{
DECL|field|lonFilter1
specifier|final
name|Filter
name|lonFilter1
decl_stmt|;
DECL|field|lonFilter2
specifier|final
name|Filter
name|lonFilter2
decl_stmt|;
DECL|field|latFilter
specifier|final
name|Filter
name|latFilter
decl_stmt|;
DECL|method|LeftGeoBoundingBoxFilter
specifier|public
name|LeftGeoBoundingBoxFilter
parameter_list|(
name|GeoPoint
name|topLeft
parameter_list|,
name|GeoPoint
name|bottomRight
parameter_list|,
name|GeoPointFieldMapper
name|fieldMapper
parameter_list|)
block|{
name|lonFilter1
operator|=
name|fieldMapper
operator|.
name|lonMapper
argument_list|()
operator|.
name|rangeFilter
argument_list|(
literal|null
argument_list|,
name|bottomRight
operator|.
name|lon
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|lonFilter2
operator|=
name|fieldMapper
operator|.
name|lonMapper
argument_list|()
operator|.
name|rangeFilter
argument_list|(
name|topLeft
operator|.
name|lon
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|latFilter
operator|=
name|fieldMapper
operator|.
name|latMapper
argument_list|()
operator|.
name|rangeFilter
argument_list|(
name|bottomRight
operator|.
name|lat
argument_list|()
argument_list|,
name|topLeft
operator|.
name|lat
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptedDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|FixedBitSet
name|main
decl_stmt|;
name|DocIdSet
name|set
init|=
name|lonFilter1
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptedDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|DocIdSets
operator|.
name|isEmpty
argument_list|(
name|set
argument_list|)
condition|)
block|{
name|main
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|main
operator|=
operator|(
name|FixedBitSet
operator|)
name|set
expr_stmt|;
block|}
name|set
operator|=
name|lonFilter2
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptedDocs
argument_list|)
expr_stmt|;
if|if
condition|(
name|DocIdSets
operator|.
name|isEmpty
argument_list|(
name|set
argument_list|)
condition|)
block|{
if|if
condition|(
name|main
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// nothing to do here, we remain with the main one
block|}
block|}
else|else
block|{
if|if
condition|(
name|main
operator|==
literal|null
condition|)
block|{
name|main
operator|=
operator|(
name|FixedBitSet
operator|)
name|set
expr_stmt|;
block|}
else|else
block|{
name|main
operator|.
name|or
argument_list|(
operator|(
name|FixedBitSet
operator|)
name|set
argument_list|)
expr_stmt|;
block|}
block|}
name|set
operator|=
name|latFilter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptedDocs
argument_list|)
expr_stmt|;
if|if
condition|(
name|DocIdSets
operator|.
name|isEmpty
argument_list|(
name|set
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|main
operator|.
name|and
argument_list|(
name|set
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|main
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|LeftGeoBoundingBoxFilter
name|that
init|=
operator|(
name|LeftGeoBoundingBoxFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|latFilter
operator|!=
literal|null
condition|?
operator|!
name|latFilter
operator|.
name|equals
argument_list|(
name|that
operator|.
name|latFilter
argument_list|)
else|:
name|that
operator|.
name|latFilter
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|lonFilter1
operator|!=
literal|null
condition|?
operator|!
name|lonFilter1
operator|.
name|equals
argument_list|(
name|that
operator|.
name|lonFilter1
argument_list|)
else|:
name|that
operator|.
name|lonFilter1
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|lonFilter2
operator|!=
literal|null
condition|?
operator|!
name|lonFilter2
operator|.
name|equals
argument_list|(
name|that
operator|.
name|lonFilter2
argument_list|)
else|:
name|that
operator|.
name|lonFilter2
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|lonFilter1
operator|!=
literal|null
condition|?
name|lonFilter1
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|lonFilter2
operator|!=
literal|null
condition|?
name|lonFilter2
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|latFilter
operator|!=
literal|null
condition|?
name|latFilter
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
DECL|class|RightGeoBoundingBoxFilter
specifier|static
class|class
name|RightGeoBoundingBoxFilter
extends|extends
name|Filter
block|{
DECL|field|lonFilter
specifier|final
name|Filter
name|lonFilter
decl_stmt|;
DECL|field|latFilter
specifier|final
name|Filter
name|latFilter
decl_stmt|;
DECL|method|RightGeoBoundingBoxFilter
specifier|public
name|RightGeoBoundingBoxFilter
parameter_list|(
name|GeoPoint
name|topLeft
parameter_list|,
name|GeoPoint
name|bottomRight
parameter_list|,
name|GeoPointFieldMapper
name|fieldMapper
parameter_list|)
block|{
name|lonFilter
operator|=
name|fieldMapper
operator|.
name|lonMapper
argument_list|()
operator|.
name|rangeFilter
argument_list|(
name|topLeft
operator|.
name|lon
argument_list|()
argument_list|,
name|bottomRight
operator|.
name|lon
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|latFilter
operator|=
name|fieldMapper
operator|.
name|latMapper
argument_list|()
operator|.
name|rangeFilter
argument_list|(
name|bottomRight
operator|.
name|lat
argument_list|()
argument_list|,
name|topLeft
operator|.
name|lat
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|FixedBitSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptedDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|FixedBitSet
name|main
decl_stmt|;
name|DocIdSet
name|set
init|=
name|lonFilter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptedDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|DocIdSets
operator|.
name|isEmpty
argument_list|(
name|set
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|main
operator|=
operator|(
name|FixedBitSet
operator|)
name|set
expr_stmt|;
name|set
operator|=
name|latFilter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptedDocs
argument_list|)
expr_stmt|;
if|if
condition|(
name|DocIdSets
operator|.
name|isEmpty
argument_list|(
name|set
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|main
operator|.
name|and
argument_list|(
name|set
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|main
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|RightGeoBoundingBoxFilter
name|that
init|=
operator|(
name|RightGeoBoundingBoxFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|latFilter
operator|!=
literal|null
condition|?
operator|!
name|latFilter
operator|.
name|equals
argument_list|(
name|that
operator|.
name|latFilter
argument_list|)
else|:
name|that
operator|.
name|latFilter
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|lonFilter
operator|!=
literal|null
condition|?
operator|!
name|lonFilter
operator|.
name|equals
argument_list|(
name|that
operator|.
name|lonFilter
argument_list|)
else|:
name|that
operator|.
name|lonFilter
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|lonFilter
operator|!=
literal|null
condition|?
name|lonFilter
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|latFilter
operator|!=
literal|null
condition|?
name|latFilter
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
end_class

end_unit

