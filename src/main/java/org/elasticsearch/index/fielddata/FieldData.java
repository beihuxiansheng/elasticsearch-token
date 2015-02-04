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
name|*
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
name|NumericUtils
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * Utility methods, similar to Lucene's {@link DocValues}.  */
end_comment

begin_enum
DECL|enum|FieldData
specifier|public
enum|enum
name|FieldData
block|{     ;
comment|/**      * Return a {@link SortedBinaryDocValues} that doesn't contain any value.      */
DECL|method|emptySortedBinary
specifier|public
specifier|static
name|SortedBinaryDocValues
name|emptySortedBinary
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
return|return
name|singleton
argument_list|(
name|DocValues
operator|.
name|emptyBinary
argument_list|()
argument_list|,
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|maxDoc
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Return a {@link NumericDoubleValues} that doesn't contain any value.      */
DECL|method|emptyNumericDouble
specifier|public
specifier|static
name|NumericDoubleValues
name|emptyNumericDouble
parameter_list|()
block|{
return|return
operator|new
name|NumericDoubleValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|double
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
comment|/**      * Return a {@link SortedNumericDoubleValues} that doesn't contain any value.      */
DECL|method|emptySortedNumericDoubles
specifier|public
specifier|static
name|SortedNumericDoubleValues
name|emptySortedNumericDoubles
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
return|return
name|singleton
argument_list|(
name|emptyNumericDouble
argument_list|()
argument_list|,
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|maxDoc
argument_list|)
argument_list|)
return|;
block|}
DECL|method|emptyGeoPoint
specifier|public
specifier|static
name|GeoPointValues
name|emptyGeoPoint
parameter_list|()
block|{
specifier|final
name|GeoPoint
name|point
init|=
operator|new
name|GeoPoint
argument_list|()
decl_stmt|;
return|return
operator|new
name|GeoPointValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|GeoPoint
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|point
return|;
block|}
block|}
return|;
block|}
comment|/**      * Return a {@link SortedNumericDoubleValues} that doesn't contain any value.      */
DECL|method|emptyMultiGeoPoints
specifier|public
specifier|static
name|MultiGeoPointValues
name|emptyMultiGeoPoints
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
return|return
name|singleton
argument_list|(
name|emptyGeoPoint
argument_list|()
argument_list|,
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|maxDoc
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns a {@link Bits} representing all documents from<code>dv</code> that have a value.      */
DECL|method|docsWithValue
specifier|public
specifier|static
name|Bits
name|docsWithValue
parameter_list|(
specifier|final
name|SortedBinaryDocValues
name|dv
parameter_list|,
specifier|final
name|int
name|maxDoc
parameter_list|)
block|{
return|return
operator|new
name|Bits
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|dv
operator|.
name|setDocument
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|dv
operator|.
name|count
argument_list|()
operator|!=
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
return|;
block|}
comment|/**      * Returns a Bits representing all documents from<code>dv</code> that have a value.      */
DECL|method|docsWithValue
specifier|public
specifier|static
name|Bits
name|docsWithValue
parameter_list|(
specifier|final
name|MultiGeoPointValues
name|dv
parameter_list|,
specifier|final
name|int
name|maxDoc
parameter_list|)
block|{
return|return
operator|new
name|Bits
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|dv
operator|.
name|setDocument
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|dv
operator|.
name|count
argument_list|()
operator|!=
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
return|;
block|}
comment|/**      * Returns a Bits representing all documents from<code>dv</code> that have a value.      */
DECL|method|docsWithValue
specifier|public
specifier|static
name|Bits
name|docsWithValue
parameter_list|(
specifier|final
name|SortedNumericDoubleValues
name|dv
parameter_list|,
specifier|final
name|int
name|maxDoc
parameter_list|)
block|{
return|return
operator|new
name|Bits
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|dv
operator|.
name|setDocument
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|dv
operator|.
name|count
argument_list|()
operator|!=
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
return|;
block|}
comment|/**      * Given a {@link SortedNumericDoubleValues}, return a {@link SortedNumericDocValues}      * instance that will translate double values to sortable long bits using      * {@link NumericUtils#doubleToSortableLong(double)}.      */
DECL|method|toSortableLongBits
specifier|public
specifier|static
name|SortedNumericDocValues
name|toSortableLongBits
parameter_list|(
name|SortedNumericDoubleValues
name|values
parameter_list|)
block|{
specifier|final
name|NumericDoubleValues
name|singleton
init|=
name|unwrapSingleton
argument_list|(
name|values
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleton
operator|!=
literal|null
condition|)
block|{
specifier|final
name|NumericDocValues
name|longBits
decl_stmt|;
if|if
condition|(
name|singleton
operator|instanceof
name|SortableLongBitsToNumericDoubleValues
condition|)
block|{
name|longBits
operator|=
operator|(
operator|(
name|SortableLongBitsToNumericDoubleValues
operator|)
name|singleton
operator|)
operator|.
name|getLongValues
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|longBits
operator|=
operator|new
name|SortableLongBitsNumericDocValues
argument_list|(
name|singleton
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Bits
name|docsWithField
init|=
name|unwrapSingletonBits
argument_list|(
name|values
argument_list|)
decl_stmt|;
return|return
name|DocValues
operator|.
name|singleton
argument_list|(
name|longBits
argument_list|,
name|docsWithField
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|values
operator|instanceof
name|SortableLongBitsToSortedNumericDoubleValues
condition|)
block|{
return|return
operator|(
operator|(
name|SortableLongBitsToSortedNumericDoubleValues
operator|)
name|values
operator|)
operator|.
name|getLongValues
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|SortableLongBitsSortedNumericDocValues
argument_list|(
name|values
argument_list|)
return|;
block|}
block|}
block|}
comment|/**      * Given a {@link SortedNumericDocValues}, return a {@link SortedNumericDoubleValues}      * instance that will translate long values to doubles using      * {@link NumericUtils#sortableLongToDouble(long)}.      */
DECL|method|sortableLongBitsToDoubles
specifier|public
specifier|static
name|SortedNumericDoubleValues
name|sortableLongBitsToDoubles
parameter_list|(
name|SortedNumericDocValues
name|values
parameter_list|)
block|{
specifier|final
name|NumericDocValues
name|singleton
init|=
name|DocValues
operator|.
name|unwrapSingleton
argument_list|(
name|values
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleton
operator|!=
literal|null
condition|)
block|{
specifier|final
name|NumericDoubleValues
name|doubles
decl_stmt|;
if|if
condition|(
name|singleton
operator|instanceof
name|SortableLongBitsNumericDocValues
condition|)
block|{
name|doubles
operator|=
operator|(
operator|(
name|SortableLongBitsNumericDocValues
operator|)
name|singleton
operator|)
operator|.
name|getDoubleValues
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|doubles
operator|=
operator|new
name|SortableLongBitsToNumericDoubleValues
argument_list|(
name|singleton
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Bits
name|docsWithField
init|=
name|DocValues
operator|.
name|unwrapSingletonBits
argument_list|(
name|values
argument_list|)
decl_stmt|;
return|return
name|singleton
argument_list|(
name|doubles
argument_list|,
name|docsWithField
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|values
operator|instanceof
name|SortableLongBitsSortedNumericDocValues
condition|)
block|{
return|return
operator|(
operator|(
name|SortableLongBitsSortedNumericDocValues
operator|)
name|values
operator|)
operator|.
name|getDoubleValues
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|SortableLongBitsToSortedNumericDoubleValues
argument_list|(
name|values
argument_list|)
return|;
block|}
block|}
block|}
comment|/**      * Wrap the provided {@link SortedNumericDocValues} instance to cast all values to doubles.      */
DECL|method|castToDouble
specifier|public
specifier|static
name|SortedNumericDoubleValues
name|castToDouble
parameter_list|(
specifier|final
name|SortedNumericDocValues
name|values
parameter_list|)
block|{
specifier|final
name|NumericDocValues
name|singleton
init|=
name|DocValues
operator|.
name|unwrapSingleton
argument_list|(
name|values
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleton
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Bits
name|docsWithField
init|=
name|DocValues
operator|.
name|unwrapSingletonBits
argument_list|(
name|values
argument_list|)
decl_stmt|;
return|return
name|singleton
argument_list|(
operator|new
name|DoubleCastedValues
argument_list|(
name|singleton
argument_list|)
argument_list|,
name|docsWithField
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SortedDoubleCastedValues
argument_list|(
name|values
argument_list|)
return|;
block|}
block|}
comment|/**      * Wrap the provided {@link SortedNumericDoubleValues} instance to cast all values to longs.      */
DECL|method|castToLong
specifier|public
specifier|static
name|SortedNumericDocValues
name|castToLong
parameter_list|(
specifier|final
name|SortedNumericDoubleValues
name|values
parameter_list|)
block|{
specifier|final
name|NumericDoubleValues
name|singleton
init|=
name|unwrapSingleton
argument_list|(
name|values
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleton
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Bits
name|docsWithField
init|=
name|unwrapSingletonBits
argument_list|(
name|values
argument_list|)
decl_stmt|;
return|return
name|DocValues
operator|.
name|singleton
argument_list|(
operator|new
name|LongCastedValues
argument_list|(
name|singleton
argument_list|)
argument_list|,
name|docsWithField
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SortedLongCastedValues
argument_list|(
name|values
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns a multi-valued view over the provided {@link NumericDoubleValues}.      */
DECL|method|singleton
specifier|public
specifier|static
name|SortedNumericDoubleValues
name|singleton
parameter_list|(
name|NumericDoubleValues
name|values
parameter_list|,
name|Bits
name|docsWithField
parameter_list|)
block|{
return|return
operator|new
name|SingletonSortedNumericDoubleValues
argument_list|(
name|values
argument_list|,
name|docsWithField
argument_list|)
return|;
block|}
comment|/**      * Returns a single-valued view of the {@link SortedNumericDoubleValues},      * if it was previously wrapped with {@link #singleton(NumericDocValues, Bits)},      * or null.      * @see #unwrapSingletonBits(SortedNumericDocValues)      */
DECL|method|unwrapSingleton
specifier|public
specifier|static
name|NumericDoubleValues
name|unwrapSingleton
parameter_list|(
name|SortedNumericDoubleValues
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|instanceof
name|SingletonSortedNumericDoubleValues
condition|)
block|{
return|return
operator|(
operator|(
name|SingletonSortedNumericDoubleValues
operator|)
name|values
operator|)
operator|.
name|getNumericDoubleValues
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns the documents with a value for the {@link SortedNumericDoubleValues},      * if it was previously wrapped with {@link #singleton(NumericDoubleValues, Bits)},      * or null.      */
DECL|method|unwrapSingletonBits
specifier|public
specifier|static
name|Bits
name|unwrapSingletonBits
parameter_list|(
name|SortedNumericDoubleValues
name|dv
parameter_list|)
block|{
if|if
condition|(
name|dv
operator|instanceof
name|SingletonSortedNumericDoubleValues
condition|)
block|{
return|return
operator|(
operator|(
name|SingletonSortedNumericDoubleValues
operator|)
name|dv
operator|)
operator|.
name|getDocsWithField
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Returns a multi-valued view over the provided {@link GeoPointValues}.      */
DECL|method|singleton
specifier|public
specifier|static
name|MultiGeoPointValues
name|singleton
parameter_list|(
name|GeoPointValues
name|values
parameter_list|,
name|Bits
name|docsWithField
parameter_list|)
block|{
return|return
operator|new
name|SingletonMultiGeoPointValues
argument_list|(
name|values
argument_list|,
name|docsWithField
argument_list|)
return|;
block|}
comment|/**      * Returns a single-valued view of the {@link MultiGeoPointValues},      * if it was previously wrapped with {@link #singleton(GeoPointValues, Bits)},      * or null.      * @see #unwrapSingletonBits(MultiGeoPointValues)      */
DECL|method|unwrapSingleton
specifier|public
specifier|static
name|GeoPointValues
name|unwrapSingleton
parameter_list|(
name|MultiGeoPointValues
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|instanceof
name|SingletonMultiGeoPointValues
condition|)
block|{
return|return
operator|(
operator|(
name|SingletonMultiGeoPointValues
operator|)
name|values
operator|)
operator|.
name|getGeoPointValues
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns the documents with a value for the {@link MultiGeoPointValues},      * if it was previously wrapped with {@link #singleton(GeoPointValues, Bits)},      * or null.      */
DECL|method|unwrapSingletonBits
specifier|public
specifier|static
name|Bits
name|unwrapSingletonBits
parameter_list|(
name|MultiGeoPointValues
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|instanceof
name|SingletonMultiGeoPointValues
condition|)
block|{
return|return
operator|(
operator|(
name|SingletonMultiGeoPointValues
operator|)
name|values
operator|)
operator|.
name|getDocsWithField
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns a multi-valued view over the provided {@link BinaryDocValues}.      */
DECL|method|singleton
specifier|public
specifier|static
name|SortedBinaryDocValues
name|singleton
parameter_list|(
name|BinaryDocValues
name|values
parameter_list|,
name|Bits
name|docsWithField
parameter_list|)
block|{
return|return
operator|new
name|SingletonSortedBinaryDocValues
argument_list|(
name|values
argument_list|,
name|docsWithField
argument_list|)
return|;
block|}
comment|/**      * Returns a single-valued view of the {@link SortedBinaryDocValues},      * if it was previously wrapped with {@link #singleton(BinaryDocValues, Bits)},      * or null.      * @see #unwrapSingletonBits(SortedBinaryDocValues)      */
DECL|method|unwrapSingleton
specifier|public
specifier|static
name|BinaryDocValues
name|unwrapSingleton
parameter_list|(
name|SortedBinaryDocValues
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|instanceof
name|SingletonSortedBinaryDocValues
condition|)
block|{
return|return
operator|(
operator|(
name|SingletonSortedBinaryDocValues
operator|)
name|values
operator|)
operator|.
name|getBinaryDocValues
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns the documents with a value for the {@link SortedBinaryDocValues},      * if it was previously wrapped with {@link #singleton(BinaryDocValues, Bits)},      * or null.      */
DECL|method|unwrapSingletonBits
specifier|public
specifier|static
name|Bits
name|unwrapSingletonBits
parameter_list|(
name|SortedBinaryDocValues
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|instanceof
name|SingletonSortedBinaryDocValues
condition|)
block|{
return|return
operator|(
operator|(
name|SingletonSortedBinaryDocValues
operator|)
name|values
operator|)
operator|.
name|getDocsWithField
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns whether the provided values *might* be multi-valued. There is no      * guarantee that this method will return<tt>false</tt> in the single-valued case.      */
DECL|method|isMultiValued
specifier|public
specifier|static
name|boolean
name|isMultiValued
parameter_list|(
name|SortedSetDocValues
name|values
parameter_list|)
block|{
return|return
name|DocValues
operator|.
name|unwrapSingleton
argument_list|(
name|values
argument_list|)
operator|==
literal|null
return|;
block|}
comment|/**      * Returns whether the provided values *might* be multi-valued. There is no      * guarantee that this method will return<tt>false</tt> in the single-valued case.      */
DECL|method|isMultiValued
specifier|public
specifier|static
name|boolean
name|isMultiValued
parameter_list|(
name|SortedNumericDocValues
name|values
parameter_list|)
block|{
return|return
name|DocValues
operator|.
name|unwrapSingleton
argument_list|(
name|values
argument_list|)
operator|==
literal|null
return|;
block|}
comment|/**      * Returns whether the provided values *might* be multi-valued. There is no      * guarantee that this method will return<tt>false</tt> in the single-valued case.      */
DECL|method|isMultiValued
specifier|public
specifier|static
name|boolean
name|isMultiValued
parameter_list|(
name|SortedNumericDoubleValues
name|values
parameter_list|)
block|{
return|return
name|unwrapSingleton
argument_list|(
name|values
argument_list|)
operator|==
literal|null
return|;
block|}
comment|/**      * Returns whether the provided values *might* be multi-valued. There is no      * guarantee that this method will return<tt>false</tt> in the single-valued case.      */
DECL|method|isMultiValued
specifier|public
specifier|static
name|boolean
name|isMultiValued
parameter_list|(
name|SortedBinaryDocValues
name|values
parameter_list|)
block|{
return|return
name|unwrapSingleton
argument_list|(
name|values
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**      * Returns whether the provided values *might* be multi-valued. There is no      * guarantee that this method will return<tt>false</tt> in the single-valued case.      */
DECL|method|isMultiValued
specifier|public
specifier|static
name|boolean
name|isMultiValued
parameter_list|(
name|MultiGeoPointValues
name|values
parameter_list|)
block|{
return|return
name|unwrapSingleton
argument_list|(
name|values
argument_list|)
operator|==
literal|null
return|;
block|}
comment|/**      * Return a {@link String} representation of the provided values. That is      * typically used for scripts or for the `map` execution mode of terms aggs.      * NOTE: this is very slow!      */
DECL|method|toString
specifier|public
specifier|static
name|SortedBinaryDocValues
name|toString
parameter_list|(
specifier|final
name|SortedNumericDocValues
name|values
parameter_list|)
block|{
return|return
name|toString
argument_list|(
operator|new
name|ToStringValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|List
argument_list|<
name|CharSequence
argument_list|>
name|list
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
operator|,
name|count
operator|=
name|values
operator|.
name|count
argument_list|()
condition|;
name|i
operator|<
name|count
incr|;
control|++i)
block|{
name|list
operator|.
name|add
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Return a {@link String} representation of the provided values. That is      * typically used for scripts or for the `map` execution mode of terms aggs.      * NOTE: this is very slow!      */
DECL|method|toString
specifier|public
specifier|static
name|SortedBinaryDocValues
name|toString
parameter_list|(
specifier|final
name|SortedNumericDoubleValues
name|values
parameter_list|)
block|{
return|return
name|toString
argument_list|(
operator|new
name|ToStringValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|List
argument_list|<
name|CharSequence
argument_list|>
name|list
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
operator|,
name|count
operator|=
name|values
operator|.
name|count
argument_list|()
condition|;
name|i
operator|<
name|count
incr|;
control|++i)
block|{
name|list
operator|.
name|add
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Return a {@link String} representation of the provided values. That is      * typically used for scripts or for the `map` execution mode of terms aggs.      * NOTE: this is slow!      */
DECL|method|toString
specifier|public
specifier|static
name|SortedBinaryDocValues
name|toString
parameter_list|(
specifier|final
name|RandomAccessOrds
name|values
parameter_list|)
block|{
return|return
operator|new
name|SortedBinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BytesRef
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|values
operator|.
name|lookupOrd
argument_list|(
name|values
operator|.
name|ordAt
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|values
operator|.
name|cardinality
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/**      * Return a {@link String} representation of the provided values. That is      * typically used for scripts or for the `map` execution mode of terms aggs.      * NOTE: this is very slow!      */
DECL|method|toString
specifier|public
specifier|static
name|SortedBinaryDocValues
name|toString
parameter_list|(
specifier|final
name|MultiGeoPointValues
name|values
parameter_list|)
block|{
return|return
name|toString
argument_list|(
operator|new
name|ToStringValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|List
argument_list|<
name|CharSequence
argument_list|>
name|list
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
operator|,
name|count
operator|=
name|values
operator|.
name|count
argument_list|()
condition|;
name|i
operator|<
name|count
incr|;
control|++i)
block|{
name|list
operator|.
name|add
argument_list|(
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * If<code>dv</code> is an instance of {@link RandomAccessOrds}, then return      * it, otherwise wrap it into a slow wrapper that implements random access.      */
DECL|method|maybeSlowRandomAccessOrds
specifier|public
specifier|static
name|RandomAccessOrds
name|maybeSlowRandomAccessOrds
parameter_list|(
specifier|final
name|SortedSetDocValues
name|dv
parameter_list|)
block|{
if|if
condition|(
name|dv
operator|instanceof
name|RandomAccessOrds
condition|)
block|{
return|return
operator|(
name|RandomAccessOrds
operator|)
name|dv
return|;
block|}
else|else
block|{
assert|assert
name|DocValues
operator|.
name|unwrapSingleton
argument_list|(
name|dv
argument_list|)
operator|==
literal|null
operator|:
literal|"this method expect singleton to return random-access ords"
assert|;
return|return
operator|new
name|RandomAccessOrds
argument_list|()
block|{
name|int
name|cardinality
decl_stmt|;
name|long
index|[]
name|ords
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
name|int
name|ord
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|cardinality
operator|=
literal|0
expr_stmt|;
name|dv
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|ord
init|=
name|dv
operator|.
name|nextOrd
argument_list|()
init|;
name|ord
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|;
name|ord
operator|=
name|dv
operator|.
name|nextOrd
argument_list|()
control|)
block|{
name|ords
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|ords
argument_list|,
name|cardinality
operator|+
literal|1
argument_list|)
expr_stmt|;
name|ords
index|[
name|cardinality
operator|++
index|]
operator|=
name|ord
expr_stmt|;
block|}
name|ord
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
return|return
name|ords
index|[
name|ord
operator|++
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
return|return
name|dv
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getValueCount
parameter_list|()
block|{
return|return
name|dv
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|ordAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|ords
index|[
name|index
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|cardinality
parameter_list|()
block|{
return|return
name|cardinality
return|;
block|}
block|}
return|;
block|}
block|}
DECL|method|toString
specifier|private
specifier|static
name|SortedBinaryDocValues
name|toString
parameter_list|(
specifier|final
name|ToStringValues
name|toStringValues
parameter_list|)
block|{
return|return
operator|new
name|SortingBinaryDocValues
argument_list|()
block|{
specifier|final
name|List
argument_list|<
name|CharSequence
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
name|toStringValues
operator|.
name|get
argument_list|(
name|docID
argument_list|,
name|list
argument_list|)
expr_stmt|;
name|count
operator|=
name|list
operator|.
name|size
argument_list|()
expr_stmt|;
name|grow
argument_list|()
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
name|count
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|CharSequence
name|s
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|values
index|[
name|i
index|]
operator|.
name|copyChars
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|sort
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|interface|ToStringValues
specifier|private
specifier|static
interface|interface
name|ToStringValues
block|{
DECL|method|get
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|List
argument_list|<
name|CharSequence
argument_list|>
name|values
parameter_list|)
function_decl|;
block|}
DECL|class|DoubleCastedValues
specifier|private
specifier|static
class|class
name|DoubleCastedValues
extends|extends
name|NumericDoubleValues
block|{
DECL|field|values
specifier|private
specifier|final
name|NumericDocValues
name|values
decl_stmt|;
DECL|method|DoubleCastedValues
name|DoubleCastedValues
parameter_list|(
name|NumericDocValues
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
DECL|method|get
specifier|public
name|double
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|values
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
block|}
DECL|class|SortedDoubleCastedValues
specifier|private
specifier|static
class|class
name|SortedDoubleCastedValues
extends|extends
name|SortedNumericDoubleValues
block|{
DECL|field|values
specifier|private
specifier|final
name|SortedNumericDocValues
name|values
decl_stmt|;
DECL|method|SortedDoubleCastedValues
name|SortedDoubleCastedValues
parameter_list|(
name|SortedNumericDocValues
name|in
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|in
expr_stmt|;
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
name|values
operator|.
name|valueAt
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
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
name|values
operator|.
name|count
argument_list|()
return|;
block|}
block|}
DECL|class|LongCastedValues
specifier|private
specifier|static
class|class
name|LongCastedValues
extends|extends
name|NumericDocValues
block|{
DECL|field|values
specifier|private
specifier|final
name|NumericDoubleValues
name|values
decl_stmt|;
DECL|method|LongCastedValues
name|LongCastedValues
parameter_list|(
name|NumericDoubleValues
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
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|values
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
block|}
DECL|class|SortedLongCastedValues
specifier|private
specifier|static
class|class
name|SortedLongCastedValues
extends|extends
name|SortedNumericDocValues
block|{
DECL|field|values
specifier|private
specifier|final
name|SortedNumericDoubleValues
name|values
decl_stmt|;
DECL|method|SortedLongCastedValues
name|SortedLongCastedValues
parameter_list|(
name|SortedNumericDoubleValues
name|in
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|in
expr_stmt|;
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
operator|(
name|long
operator|)
name|values
operator|.
name|valueAt
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
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
name|values
operator|.
name|count
argument_list|()
return|;
block|}
block|}
block|}
end_enum

end_unit

