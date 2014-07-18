begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.plain
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|plain
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|index
operator|.
name|BinaryDocValues
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
name|index
operator|.
name|DocValues
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
name|store
operator|.
name|ByteArrayDataInput
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
name|ElasticsearchIllegalStateException
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
name|ByteUtils
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
name|Index
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
name|fielddata
operator|.
name|*
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
name|fielddata
operator|.
name|IndexFieldData
operator|.
name|XFieldComparatorSource
operator|.
name|Nested
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
name|fielddata
operator|.
name|fieldcomparator
operator|.
name|DoubleValuesComparatorSource
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
name|fielddata
operator|.
name|fieldcomparator
operator|.
name|FloatValuesComparatorSource
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
name|fielddata
operator|.
name|fieldcomparator
operator|.
name|LongValuesComparatorSource
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
name|FieldMapper
operator|.
name|Names
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
name|MultiValueMode
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
DECL|class|BinaryDVNumericIndexFieldData
specifier|public
class|class
name|BinaryDVNumericIndexFieldData
extends|extends
name|DocValuesIndexFieldData
implements|implements
name|IndexNumericFieldData
block|{
DECL|field|numericType
specifier|private
specifier|final
name|NumericType
name|numericType
decl_stmt|;
DECL|method|BinaryDVNumericIndexFieldData
specifier|public
name|BinaryDVNumericIndexFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
name|Names
name|fieldNames
parameter_list|,
name|NumericType
name|numericType
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|fieldNames
argument_list|,
name|fieldDataType
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|numericType
operator|!=
literal|null
argument_list|,
literal|"numericType must be non-null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|numericType
operator|=
name|numericType
expr_stmt|;
block|}
DECL|method|comparatorSource
specifier|public
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|IndexFieldData
operator|.
name|XFieldComparatorSource
name|comparatorSource
parameter_list|(
specifier|final
name|Object
name|missingValue
parameter_list|,
specifier|final
name|MultiValueMode
name|sortMode
parameter_list|,
name|Nested
name|nested
parameter_list|)
block|{
switch|switch
condition|(
name|numericType
condition|)
block|{
case|case
name|FLOAT
case|:
return|return
operator|new
name|FloatValuesComparatorSource
argument_list|(
name|this
argument_list|,
name|missingValue
argument_list|,
name|sortMode
argument_list|,
name|nested
argument_list|)
return|;
case|case
name|DOUBLE
case|:
return|return
operator|new
name|DoubleValuesComparatorSource
argument_list|(
name|this
argument_list|,
name|missingValue
argument_list|,
name|sortMode
argument_list|,
name|nested
argument_list|)
return|;
default|default:
assert|assert
operator|!
name|numericType
operator|.
name|isFloatingPoint
argument_list|()
assert|;
return|return
operator|new
name|LongValuesComparatorSource
argument_list|(
name|this
argument_list|,
name|missingValue
argument_list|,
name|sortMode
argument_list|,
name|nested
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|AtomicNumericFieldData
name|load
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
try|try
block|{
specifier|final
name|BinaryDocValues
name|values
init|=
name|DocValues
operator|.
name|getBinary
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|fieldNames
operator|.
name|indexName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|numericType
operator|.
name|isFloatingPoint
argument_list|()
condition|)
block|{
return|return
operator|new
name|AtomicDoubleFieldData
argument_list|(
operator|-
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SortedNumericDoubleValues
name|getDoubleValues
parameter_list|()
block|{
switch|switch
condition|(
name|numericType
condition|)
block|{
case|case
name|FLOAT
case|:
return|return
operator|new
name|BinaryAsSortedNumericFloatValues
argument_list|(
name|values
argument_list|)
return|;
case|case
name|DOUBLE
case|:
return|return
operator|new
name|BinaryAsSortedNumericDoubleValues
argument_list|(
name|values
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|""
operator|+
name|numericType
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
else|else
block|{
return|return
operator|new
name|AtomicLongFieldData
argument_list|(
operator|-
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SortedNumericDocValues
name|getLongValues
parameter_list|()
block|{
return|return
operator|new
name|BinaryAsSortedNumericDocValues
argument_list|(
name|values
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"Cannot load doc values"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|loadDirect
specifier|public
name|AtomicNumericFieldData
name|loadDirect
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|load
argument_list|(
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNumericType
specifier|public
name|NumericType
name|getNumericType
parameter_list|()
block|{
return|return
name|numericType
return|;
block|}
DECL|class|BinaryAsSortedNumericDocValues
specifier|private
specifier|static
class|class
name|BinaryAsSortedNumericDocValues
extends|extends
name|SortedNumericDocValues
block|{
DECL|field|values
specifier|private
specifier|final
name|BinaryDocValues
name|values
decl_stmt|;
DECL|field|bytes
specifier|private
name|BytesRef
name|bytes
decl_stmt|;
DECL|field|in
specifier|private
specifier|final
name|ByteArrayDataInput
name|in
init|=
operator|new
name|ByteArrayDataInput
argument_list|()
decl_stmt|;
DECL|field|longs
specifier|private
name|long
index|[]
name|longs
init|=
operator|new
name|long
index|[
literal|1
index|]
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|method|BinaryAsSortedNumericDocValues
name|BinaryAsSortedNumericDocValues
parameter_list|(
name|BinaryDocValues
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|bytes
operator|=
name|values
operator|.
name|get
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|in
operator|.
name|eof
argument_list|()
condition|)
block|{
comment|// first value uses vLong on top of zig-zag encoding, then deltas are encoded using vLong
name|long
name|previousValue
init|=
name|longs
index|[
literal|0
index|]
operator|=
name|ByteUtils
operator|.
name|zigZagDecode
argument_list|(
name|ByteUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|count
operator|=
literal|1
expr_stmt|;
while|while
condition|(
operator|!
name|in
operator|.
name|eof
argument_list|()
condition|)
block|{
name|longs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|longs
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
name|previousValue
operator|=
name|longs
index|[
name|count
operator|++
index|]
operator|=
name|previousValue
operator|+
name|ByteUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|count
operator|=
literal|0
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|count
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|valueAt
specifier|public
name|long
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|longs
index|[
name|index
index|]
return|;
block|}
block|}
DECL|class|BinaryAsSortedNumericDoubleValues
specifier|private
specifier|static
class|class
name|BinaryAsSortedNumericDoubleValues
extends|extends
name|SortedNumericDoubleValues
block|{
DECL|field|values
specifier|private
specifier|final
name|BinaryDocValues
name|values
decl_stmt|;
DECL|field|bytes
specifier|private
name|BytesRef
name|bytes
decl_stmt|;
DECL|field|valueCount
specifier|private
name|int
name|valueCount
init|=
literal|0
decl_stmt|;
DECL|method|BinaryAsSortedNumericDoubleValues
name|BinaryAsSortedNumericDoubleValues
parameter_list|(
name|BinaryDocValues
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|bytes
operator|=
name|values
operator|.
name|get
argument_list|(
name|docId
argument_list|)
expr_stmt|;
assert|assert
name|bytes
operator|.
name|length
operator|%
literal|8
operator|==
literal|0
assert|;
name|valueCount
operator|=
name|bytes
operator|.
name|length
operator|/
literal|8
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|count
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|valueCount
return|;
block|}
annotation|@
name|Override
DECL|method|valueAt
specifier|public
name|double
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|ByteUtils
operator|.
name|readDoubleLE
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
operator|+
name|index
operator|*
literal|8
argument_list|)
return|;
block|}
block|}
DECL|class|BinaryAsSortedNumericFloatValues
specifier|private
specifier|static
class|class
name|BinaryAsSortedNumericFloatValues
extends|extends
name|SortedNumericDoubleValues
block|{
DECL|field|values
specifier|private
specifier|final
name|BinaryDocValues
name|values
decl_stmt|;
DECL|field|bytes
specifier|private
name|BytesRef
name|bytes
decl_stmt|;
DECL|field|valueCount
specifier|private
name|int
name|valueCount
init|=
literal|0
decl_stmt|;
DECL|method|BinaryAsSortedNumericFloatValues
name|BinaryAsSortedNumericFloatValues
parameter_list|(
name|BinaryDocValues
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|bytes
operator|=
name|values
operator|.
name|get
argument_list|(
name|docId
argument_list|)
expr_stmt|;
assert|assert
name|bytes
operator|.
name|length
operator|%
literal|4
operator|==
literal|0
assert|;
name|valueCount
operator|=
name|bytes
operator|.
name|length
operator|/
literal|4
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|count
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|valueCount
return|;
block|}
annotation|@
name|Override
DECL|method|valueAt
specifier|public
name|double
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|ByteUtils
operator|.
name|readFloatLE
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
operator|+
name|index
operator|*
literal|4
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

