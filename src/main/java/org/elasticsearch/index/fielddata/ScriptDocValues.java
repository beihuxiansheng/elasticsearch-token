begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
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
name|SortedNumericDocValues
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
name|ArrayUtil
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
name|BytesRef
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
name|RamUsageEstimator
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
name|GeoDistance
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
name|unit
operator|.
name|DistanceUnit
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
name|util
operator|.
name|SlicedDoubleList
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
name|util
operator|.
name|SlicedLongList
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
name|util
operator|.
name|SlicedObjectList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTimeZone
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|MutableDateTime
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
comment|/**  * Script level doc values, the assumption is that any implementation will implement a<code>getValue</code>  * and a<code>getValues</code> that return the relevant type that then can be used in scripts.  */
end_comment

begin_class
DECL|class|ScriptDocValues
specifier|public
specifier|abstract
class|class
name|ScriptDocValues
block|{
DECL|field|docId
specifier|protected
name|int
name|docId
decl_stmt|;
DECL|field|listLoaded
specifier|protected
name|boolean
name|listLoaded
init|=
literal|false
decl_stmt|;
DECL|method|setNextDocId
specifier|public
name|void
name|setNextDocId
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
name|this
operator|.
name|listLoaded
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|isEmpty
specifier|public
specifier|abstract
name|boolean
name|isEmpty
parameter_list|()
function_decl|;
DECL|method|getValues
specifier|public
specifier|abstract
name|List
argument_list|<
name|?
argument_list|>
name|getValues
parameter_list|()
function_decl|;
DECL|class|Strings
specifier|public
specifier|final
specifier|static
class|class
name|Strings
extends|extends
name|ScriptDocValues
block|{
DECL|field|values
specifier|private
specifier|final
name|SortedBinaryDocValues
name|values
decl_stmt|;
DECL|field|list
specifier|private
name|SlicedObjectList
argument_list|<
name|String
argument_list|>
name|list
decl_stmt|;
DECL|method|Strings
specifier|public
name|Strings
parameter_list|(
name|SortedBinaryDocValues
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|list
operator|=
operator|new
name|SlicedObjectList
argument_list|<
name|String
argument_list|>
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|newLength
parameter_list|)
block|{
assert|assert
name|offset
operator|==
literal|0
assert|;
comment|// NOTE: senseless if offset != 0
if|if
condition|(
name|values
operator|.
name|length
operator|>=
name|newLength
condition|)
block|{
return|return;
block|}
name|values
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|values
argument_list|,
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|newLength
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
return|return
name|values
operator|.
name|count
argument_list|()
operator|==
literal|0
return|;
block|}
DECL|method|getInternalValues
specifier|public
name|SortedBinaryDocValues
name|getInternalValues
parameter_list|()
block|{
return|return
name|this
operator|.
name|values
return|;
block|}
DECL|method|getBytesValue
specifier|public
name|BytesRef
name|getBytesValue
parameter_list|()
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
operator|.
name|count
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|values
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|getValue
specifier|public
name|String
name|getValue
parameter_list|()
block|{
name|BytesRef
name|value
init|=
name|getBytesValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
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
return|return
name|value
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
block|}
DECL|method|getValues
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getValues
parameter_list|()
block|{
if|if
condition|(
operator|!
name|listLoaded
condition|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numValues
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
name|list
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|list
operator|.
name|grow
argument_list|(
name|numValues
argument_list|)
expr_stmt|;
name|list
operator|.
name|length
operator|=
name|numValues
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numValues
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|values
index|[
name|i
index|]
operator|=
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
block|}
name|listLoaded
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
block|}
DECL|class|Longs
specifier|public
specifier|static
class|class
name|Longs
extends|extends
name|ScriptDocValues
block|{
DECL|field|values
specifier|private
specifier|final
name|SortedNumericDocValues
name|values
decl_stmt|;
DECL|field|date
specifier|private
specifier|final
name|MutableDateTime
name|date
init|=
operator|new
name|MutableDateTime
argument_list|(
literal|0
argument_list|,
name|DateTimeZone
operator|.
name|UTC
argument_list|)
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|SlicedLongList
name|list
decl_stmt|;
DECL|method|Longs
specifier|public
name|Longs
parameter_list|(
name|SortedNumericDocValues
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|list
operator|=
operator|new
name|SlicedLongList
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|getInternalValues
specifier|public
name|SortedNumericDocValues
name|getInternalValues
parameter_list|()
block|{
return|return
name|this
operator|.
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
return|return
name|values
operator|.
name|count
argument_list|()
operator|==
literal|0
return|;
block|}
DECL|method|getValue
specifier|public
name|long
name|getValue
parameter_list|()
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|int
name|numValues
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|numValues
operator|==
literal|0
condition|)
block|{
return|return
literal|0l
return|;
block|}
return|return
name|values
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|getValues
specifier|public
name|List
argument_list|<
name|Long
argument_list|>
name|getValues
parameter_list|()
block|{
if|if
condition|(
operator|!
name|listLoaded
condition|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numValues
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
name|list
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|list
operator|.
name|grow
argument_list|(
name|numValues
argument_list|)
expr_stmt|;
name|list
operator|.
name|length
operator|=
name|numValues
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numValues
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|values
index|[
name|i
index|]
operator|=
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|listLoaded
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
DECL|method|getDate
specifier|public
name|MutableDateTime
name|getDate
parameter_list|()
block|{
name|date
operator|.
name|setMillis
argument_list|(
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|date
return|;
block|}
block|}
DECL|class|Doubles
specifier|public
specifier|static
class|class
name|Doubles
extends|extends
name|ScriptDocValues
block|{
DECL|field|values
specifier|private
specifier|final
name|SortedNumericDoubleValues
name|values
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|SlicedDoubleList
name|list
decl_stmt|;
DECL|method|Doubles
specifier|public
name|Doubles
parameter_list|(
name|SortedNumericDoubleValues
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|list
operator|=
operator|new
name|SlicedDoubleList
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|getInternalValues
specifier|public
name|SortedNumericDoubleValues
name|getInternalValues
parameter_list|()
block|{
return|return
name|this
operator|.
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
return|return
name|values
operator|.
name|count
argument_list|()
operator|==
literal|0
return|;
block|}
DECL|method|getValue
specifier|public
name|double
name|getValue
parameter_list|()
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|int
name|numValues
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|numValues
operator|==
literal|0
condition|)
block|{
return|return
literal|0d
return|;
block|}
return|return
name|values
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|getValues
specifier|public
name|List
argument_list|<
name|Double
argument_list|>
name|getValues
parameter_list|()
block|{
if|if
condition|(
operator|!
name|listLoaded
condition|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|int
name|numValues
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
name|list
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|list
operator|.
name|grow
argument_list|(
name|numValues
argument_list|)
expr_stmt|;
name|list
operator|.
name|length
operator|=
name|numValues
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numValues
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|values
index|[
name|i
index|]
operator|=
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|listLoaded
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
block|}
DECL|class|GeoPoints
specifier|public
specifier|static
class|class
name|GeoPoints
extends|extends
name|ScriptDocValues
block|{
DECL|field|values
specifier|private
specifier|final
name|MultiGeoPointValues
name|values
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|SlicedObjectList
argument_list|<
name|GeoPoint
argument_list|>
name|list
decl_stmt|;
DECL|method|GeoPoints
specifier|public
name|GeoPoints
parameter_list|(
name|MultiGeoPointValues
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|list
operator|=
operator|new
name|SlicedObjectList
argument_list|<
name|GeoPoint
argument_list|>
argument_list|(
operator|new
name|GeoPoint
index|[
literal|0
index|]
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|newLength
parameter_list|)
block|{
assert|assert
name|offset
operator|==
literal|0
assert|;
comment|// NOTE: senseless if offset != 0
if|if
condition|(
name|values
operator|.
name|length
operator|>=
name|newLength
condition|)
block|{
return|return;
block|}
name|values
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|values
argument_list|,
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|newLength
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
return|return
name|values
operator|.
name|count
argument_list|()
operator|==
literal|0
return|;
block|}
DECL|method|getValue
specifier|public
name|GeoPoint
name|getValue
parameter_list|()
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|int
name|numValues
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|numValues
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|values
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|getLat
specifier|public
name|double
name|getLat
parameter_list|()
block|{
return|return
name|getValue
argument_list|()
operator|.
name|lat
argument_list|()
return|;
block|}
DECL|method|getLats
specifier|public
name|double
index|[]
name|getLats
parameter_list|()
block|{
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|points
init|=
name|getValues
argument_list|()
decl_stmt|;
name|double
index|[]
name|lats
init|=
operator|new
name|double
index|[
name|points
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|points
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|lats
index|[
name|i
index|]
operator|=
name|points
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|lat
argument_list|()
expr_stmt|;
block|}
return|return
name|lats
return|;
block|}
DECL|method|getLons
specifier|public
name|double
index|[]
name|getLons
parameter_list|()
block|{
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|points
init|=
name|getValues
argument_list|()
decl_stmt|;
name|double
index|[]
name|lons
init|=
operator|new
name|double
index|[
name|points
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|points
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|lons
index|[
name|i
index|]
operator|=
name|points
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|lon
argument_list|()
expr_stmt|;
block|}
return|return
name|lons
return|;
block|}
DECL|method|getLon
specifier|public
name|double
name|getLon
parameter_list|()
block|{
return|return
name|getValue
argument_list|()
operator|.
name|lon
argument_list|()
return|;
block|}
DECL|method|getValues
specifier|public
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|getValues
parameter_list|()
block|{
if|if
condition|(
operator|!
name|listLoaded
condition|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|int
name|numValues
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
name|list
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|list
operator|.
name|grow
argument_list|(
name|numValues
argument_list|)
expr_stmt|;
name|list
operator|.
name|length
operator|=
name|numValues
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numValues
condition|;
name|i
operator|++
control|)
block|{
name|GeoPoint
name|next
init|=
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|GeoPoint
name|point
init|=
name|list
operator|.
name|values
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|point
operator|==
literal|null
condition|)
block|{
name|point
operator|=
name|list
operator|.
name|values
index|[
name|i
index|]
operator|=
operator|new
name|GeoPoint
argument_list|()
expr_stmt|;
block|}
name|point
operator|.
name|reset
argument_list|(
name|next
operator|.
name|lat
argument_list|()
argument_list|,
name|next
operator|.
name|lon
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|values
index|[
name|i
index|]
operator|=
name|point
expr_stmt|;
block|}
name|listLoaded
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
DECL|method|factorDistance
specifier|public
name|double
name|factorDistance
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|FACTOR
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
DECL|method|factorDistanceWithDefault
specifier|public
name|double
name|factorDistanceWithDefault
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|FACTOR
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
DECL|method|factorDistance02
specifier|public
name|double
name|factorDistance02
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|FACTOR
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
operator|+
literal|1
return|;
block|}
DECL|method|factorDistance13
specifier|public
name|double
name|factorDistance13
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|FACTOR
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
operator|+
literal|2
return|;
block|}
DECL|method|arcDistance
specifier|public
name|double
name|arcDistance
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
DECL|method|arcDistanceWithDefault
specifier|public
name|double
name|arcDistanceWithDefault
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
DECL|method|arcDistanceInKm
specifier|public
name|double
name|arcDistanceInKm
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|)
return|;
block|}
DECL|method|arcDistanceInKmWithDefault
specifier|public
name|double
name|arcDistanceInKmWithDefault
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|)
return|;
block|}
DECL|method|arcDistanceInMiles
specifier|public
name|double
name|arcDistanceInMiles
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|MILES
argument_list|)
return|;
block|}
DECL|method|arcDistanceInMilesWithDefault
specifier|public
name|double
name|arcDistanceInMilesWithDefault
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|MILES
argument_list|)
return|;
block|}
DECL|method|distance
specifier|public
name|double
name|distance
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|PLANE
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
DECL|method|distanceWithDefault
specifier|public
name|double
name|distanceWithDefault
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|PLANE
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
DECL|method|distanceInKm
specifier|public
name|double
name|distanceInKm
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|PLANE
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|)
return|;
block|}
DECL|method|distanceInKmWithDefault
specifier|public
name|double
name|distanceInKmWithDefault
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|PLANE
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|)
return|;
block|}
DECL|method|distanceInMiles
specifier|public
name|double
name|distanceInMiles
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|PLANE
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|MILES
argument_list|)
return|;
block|}
DECL|method|distanceInMilesWithDefault
specifier|public
name|double
name|distanceInMilesWithDefault
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|PLANE
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|DistanceUnit
operator|.
name|MILES
argument_list|)
return|;
block|}
DECL|method|geohashDistance
specifier|public
name|double
name|geohashDistance
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
name|GeoPoint
name|p
init|=
operator|new
name|GeoPoint
argument_list|()
operator|.
name|resetFromGeoHash
argument_list|(
name|geohash
argument_list|)
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|p
operator|.
name|lat
argument_list|()
argument_list|,
name|p
operator|.
name|lon
argument_list|()
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
DECL|method|geohashDistanceInKm
specifier|public
name|double
name|geohashDistanceInKm
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
name|GeoPoint
name|p
init|=
operator|new
name|GeoPoint
argument_list|()
operator|.
name|resetFromGeoHash
argument_list|(
name|geohash
argument_list|)
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|p
operator|.
name|lat
argument_list|()
argument_list|,
name|p
operator|.
name|lon
argument_list|()
argument_list|,
name|DistanceUnit
operator|.
name|KILOMETERS
argument_list|)
return|;
block|}
DECL|method|geohashDistanceInMiles
specifier|public
name|double
name|geohashDistanceInMiles
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
name|getValue
argument_list|()
decl_stmt|;
name|GeoPoint
name|p
init|=
operator|new
name|GeoPoint
argument_list|()
operator|.
name|resetFromGeoHash
argument_list|(
name|geohash
argument_list|)
decl_stmt|;
return|return
name|GeoDistance
operator|.
name|ARC
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|p
operator|.
name|lat
argument_list|()
argument_list|,
name|p
operator|.
name|lon
argument_list|()
argument_list|,
name|DistanceUnit
operator|.
name|MILES
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

