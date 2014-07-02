begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
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
name|index
operator|.
name|fielddata
operator|.
name|FieldData
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
name|NumericDoubleValues
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
name|SortedBinaryDocValues
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
name|SortedNumericDoubleValues
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Defines what values to pick in the case a document contains multiple values for a particular field.  */
end_comment

begin_enum
DECL|enum|MultiValueMode
specifier|public
enum|enum
name|MultiValueMode
block|{
comment|/**      * Sum of all the values.      */
DECL|enum constant|SUM
name|SUM
block|{
comment|/**          * Returns the sum of the two values          */
annotation|@
name|Override
specifier|public
name|double
name|apply
parameter_list|(
name|double
name|a
parameter_list|,
name|double
name|b
parameter_list|)
block|{
return|return
name|a
operator|+
name|b
return|;
block|}
comment|/**          * Returns the sum of the two values          */
annotation|@
name|Override
specifier|public
name|long
name|apply
parameter_list|(
name|long
name|a
parameter_list|,
name|long
name|b
parameter_list|)
block|{
return|return
name|a
operator|+
name|b
return|;
block|}
block|}
block|,
comment|/**      * Average of all the values.      */
DECL|enum constant|AVG
name|AVG
block|{
comment|/**          * Returns the sum of the two values          */
annotation|@
name|Override
specifier|public
name|double
name|apply
parameter_list|(
name|double
name|a
parameter_list|,
name|double
name|b
parameter_list|)
block|{
return|return
name|a
operator|+
name|b
return|;
block|}
comment|/**          * Returns the sum of the two values          */
annotation|@
name|Override
specifier|public
name|long
name|apply
parameter_list|(
name|long
name|a
parameter_list|,
name|long
name|b
parameter_list|)
block|{
return|return
name|a
operator|+
name|b
return|;
block|}
comment|/**          * Returns<code>a / Math.max(1.0d, numValues)</code>          */
annotation|@
name|Override
specifier|public
name|double
name|reduce
parameter_list|(
name|double
name|a
parameter_list|,
name|int
name|numValues
parameter_list|)
block|{
return|return
name|a
operator|/
name|Math
operator|.
name|max
argument_list|(
literal|1.0d
argument_list|,
operator|(
name|double
operator|)
name|numValues
argument_list|)
return|;
block|}
comment|/**          * Returns<code>Math.round(a / Math.max(1.0, numValues))</code>          */
annotation|@
name|Override
specifier|public
name|long
name|reduce
parameter_list|(
name|long
name|a
parameter_list|,
name|int
name|numValues
parameter_list|)
block|{
return|return
name|Math
operator|.
name|round
argument_list|(
name|a
operator|/
name|Math
operator|.
name|max
argument_list|(
literal|1.0
argument_list|,
name|numValues
argument_list|)
argument_list|)
return|;
block|}
block|}
block|,
comment|/**      * Pick the lowest value.      */
DECL|enum constant|MIN
name|MIN
block|{
comment|/**          * Equivalent to {@link Math#min(double, double)}          */
annotation|@
name|Override
specifier|public
name|double
name|apply
parameter_list|(
name|double
name|a
parameter_list|,
name|double
name|b
parameter_list|)
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
comment|/**          * Equivalent to {@link Math#min(long, long)}          */
annotation|@
name|Override
specifier|public
name|long
name|apply
parameter_list|(
name|long
name|a
parameter_list|,
name|long
name|b
parameter_list|)
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
comment|/**          * Returns {@link Double#POSITIVE_INFINITY}          */
annotation|@
name|Override
specifier|public
name|double
name|startDouble
parameter_list|()
block|{
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
comment|/**          * Returns {@link Long#MAX_VALUE}          */
annotation|@
name|Override
specifier|public
name|long
name|startLong
parameter_list|()
block|{
return|return
name|Long
operator|.
name|MAX_VALUE
return|;
block|}
annotation|@
name|Override
specifier|protected
name|long
name|pick
parameter_list|(
name|SortedNumericDocValues
name|values
parameter_list|,
name|long
name|missingValue
parameter_list|)
block|{
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
name|missingValue
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|double
name|pick
parameter_list|(
name|SortedNumericDoubleValues
name|values
parameter_list|,
name|double
name|missingValue
parameter_list|)
block|{
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
name|missingValue
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|BytesRef
name|pick
parameter_list|(
name|SortedBinaryDocValues
name|values
parameter_list|,
name|BytesRef
name|missingValue
parameter_list|)
block|{
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
name|missingValue
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|int
name|pick
parameter_list|(
name|RandomAccessOrds
name|values
parameter_list|,
name|int
name|missingOrd
parameter_list|)
block|{
if|if
condition|(
name|values
operator|.
name|cardinality
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
operator|(
name|int
operator|)
name|values
operator|.
name|ordAt
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|missingOrd
return|;
block|}
block|}
block|}
block|,
comment|/**      * Pick the highest value.      */
DECL|enum constant|MAX
name|MAX
block|{
comment|/**          * Equivalent to {@link Math#max(double, double)}          */
annotation|@
name|Override
specifier|public
name|double
name|apply
parameter_list|(
name|double
name|a
parameter_list|,
name|double
name|b
parameter_list|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
comment|/**          * Equivalent to {@link Math#max(long, long)}          */
annotation|@
name|Override
specifier|public
name|long
name|apply
parameter_list|(
name|long
name|a
parameter_list|,
name|long
name|b
parameter_list|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
comment|/**          * Returns {@link Double#NEGATIVE_INFINITY}          */
annotation|@
name|Override
specifier|public
name|double
name|startDouble
parameter_list|()
block|{
return|return
name|Double
operator|.
name|NEGATIVE_INFINITY
return|;
block|}
comment|/**          * Returns {@link Long#MIN_VALUE}          */
annotation|@
name|Override
specifier|public
name|long
name|startLong
parameter_list|()
block|{
return|return
name|Long
operator|.
name|MIN_VALUE
return|;
block|}
annotation|@
name|Override
specifier|protected
name|long
name|pick
parameter_list|(
name|SortedNumericDocValues
name|values
parameter_list|,
name|long
name|missingValue
parameter_list|)
block|{
specifier|final
name|int
name|count
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
return|return
name|values
operator|.
name|valueAt
argument_list|(
name|count
operator|-
literal|1
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|missingValue
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|double
name|pick
parameter_list|(
name|SortedNumericDoubleValues
name|values
parameter_list|,
name|double
name|missingValue
parameter_list|)
block|{
specifier|final
name|int
name|count
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
return|return
name|values
operator|.
name|valueAt
argument_list|(
name|count
operator|-
literal|1
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|missingValue
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|BytesRef
name|pick
parameter_list|(
name|SortedBinaryDocValues
name|values
parameter_list|,
name|BytesRef
name|missingValue
parameter_list|)
block|{
specifier|final
name|int
name|count
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
return|return
name|values
operator|.
name|valueAt
argument_list|(
name|count
operator|-
literal|1
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|missingValue
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|int
name|pick
parameter_list|(
name|RandomAccessOrds
name|values
parameter_list|,
name|int
name|missingOrd
parameter_list|)
block|{
specifier|final
name|int
name|count
init|=
name|values
operator|.
name|cardinality
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
return|return
operator|(
name|int
operator|)
name|values
operator|.
name|ordAt
argument_list|(
name|count
operator|-
literal|1
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|missingOrd
return|;
block|}
block|}
block|}
block|;
comment|/**      * Applies the sort mode and returns the result. This method is meant to be      * a binary function that is commonly used in a loop to find the relevant      * value for the sort mode in a list of values. For instance if the sort mode      * is {@link MultiValueMode#MAX} this method is equivalent to {@link Math#max(double, double)}.      *      * Note: all implementations are idempotent.      *      * @param a an argument      * @param b another argument      * @return the result of the function.      */
DECL|method|apply
specifier|public
specifier|abstract
name|double
name|apply
parameter_list|(
name|double
name|a
parameter_list|,
name|double
name|b
parameter_list|)
function_decl|;
comment|/**      * Applies the sort mode and returns the result. This method is meant to be      * a binary function that is commonly used in a loop to find the relevant      * value for the sort mode in a list of values. For instance if the sort mode      * is {@link MultiValueMode#MAX} this method is equivalent to {@link Math#max(long, long)}.      *      * Note: all implementations are idempotent.      *      * @param a an argument      * @param b another argument      * @return the result of the function.      */
DECL|method|apply
specifier|public
specifier|abstract
name|long
name|apply
parameter_list|(
name|long
name|a
parameter_list|,
name|long
name|b
parameter_list|)
function_decl|;
comment|/**      * Returns an initial value for the sort mode that is guaranteed to have no impact if passed      * to {@link #apply(double, double)}. This value should be used as the initial value if the      * sort mode is applied to a non-empty list of values. For instance:      *<pre>      *     double relevantValue = sortMode.startDouble();      *     for (int i = 0; i< array.length; i++) {      *         relevantValue = sortMode.apply(array[i], relevantValue);      *     }      *</pre>      *      * Note: This method return<code>0</code> by default.      *      * @return an initial value for the sort mode.      */
DECL|method|startDouble
specifier|public
name|double
name|startDouble
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**      * Returns an initial value for the sort mode that is guaranteed to have no impact if passed      * to {@link #apply(long, long)}. This value should be used as the initial value if the      * sort mode is applied to a non-empty list of values. For instance:      *<pre>      *     long relevantValue = sortMode.startLong();      *     for (int i = 0; i< array.length; i++) {      *         relevantValue = sortMode.apply(array[i], relevantValue);      *     }      *</pre>      *      * Note: This method return<code>0</code> by default.      * @return an initial value for the sort mode.      */
DECL|method|startLong
specifier|public
name|long
name|startLong
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**      * Returns the aggregated value based on the sort mode. For instance if {@link MultiValueMode#AVG} is used      * this method divides the given value by the number of values. The default implementation returns      * the first argument.      *      * Note: all implementations are idempotent.      */
DECL|method|reduce
specifier|public
name|double
name|reduce
parameter_list|(
name|double
name|a
parameter_list|,
name|int
name|numValues
parameter_list|)
block|{
return|return
name|a
return|;
block|}
comment|/**      * Returns the aggregated value based on the sort mode. For instance if {@link MultiValueMode#AVG} is used      * this method divides the given value by the number of values. The default implementation returns      * the first argument.      *      * Note: all implementations are idempotent.      */
DECL|method|reduce
specifier|public
name|long
name|reduce
parameter_list|(
name|long
name|a
parameter_list|,
name|int
name|numValues
parameter_list|)
block|{
return|return
name|a
return|;
block|}
comment|/**      * A case insensitive version of {@link #valueOf(String)}      *      * @throws org.elasticsearch.ElasticsearchIllegalArgumentException if the given string doesn't match a sort mode or is<code>null</code>.      */
DECL|method|fromString
specifier|public
specifier|static
name|MultiValueMode
name|fromString
parameter_list|(
name|String
name|sortMode
parameter_list|)
block|{
try|try
block|{
return|return
name|valueOf
argument_list|(
name|sortMode
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Illegal sort_mode "
operator|+
name|sortMode
argument_list|)
throw|;
block|}
block|}
DECL|method|pick
specifier|protected
name|long
name|pick
parameter_list|(
name|SortedNumericDocValues
name|values
parameter_list|,
name|long
name|missingValue
parameter_list|)
block|{
specifier|final
name|int
name|count
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
name|missingValue
return|;
block|}
else|else
block|{
name|long
name|aggregate
init|=
name|startLong
argument_list|()
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
name|count
condition|;
operator|++
name|i
control|)
block|{
name|aggregate
operator|=
name|apply
argument_list|(
name|aggregate
argument_list|,
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|reduce
argument_list|(
name|aggregate
argument_list|,
name|count
argument_list|)
return|;
block|}
block|}
DECL|method|select
specifier|public
name|NumericDocValues
name|select
parameter_list|(
specifier|final
name|SortedNumericDocValues
name|values
parameter_list|,
specifier|final
name|long
name|missingValue
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
if|if
condition|(
name|docsWithField
operator|==
literal|null
operator|||
name|missingValue
operator|==
literal|0
condition|)
block|{
return|return
name|singleton
return|;
block|}
else|else
block|{
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
specifier|final
name|long
name|value
init|=
name|singleton
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|0
operator|&&
name|docsWithField
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
name|missingValue
return|;
block|}
return|return
name|value
return|;
block|}
block|}
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
return|return
name|pick
argument_list|(
name|values
argument_list|,
name|missingValue
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
DECL|method|pick
specifier|protected
name|double
name|pick
parameter_list|(
name|SortedNumericDoubleValues
name|values
parameter_list|,
name|double
name|missingValue
parameter_list|)
block|{
specifier|final
name|int
name|count
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
name|missingValue
return|;
block|}
else|else
block|{
name|double
name|aggregate
init|=
name|startDouble
argument_list|()
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
name|count
condition|;
operator|++
name|i
control|)
block|{
name|aggregate
operator|=
name|apply
argument_list|(
name|aggregate
argument_list|,
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|reduce
argument_list|(
name|aggregate
argument_list|,
name|count
argument_list|)
return|;
block|}
block|}
DECL|method|select
specifier|public
name|NumericDoubleValues
name|select
parameter_list|(
specifier|final
name|SortedNumericDoubleValues
name|values
parameter_list|,
specifier|final
name|double
name|missingValue
parameter_list|)
block|{
specifier|final
name|NumericDoubleValues
name|singleton
init|=
name|FieldData
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
name|FieldData
operator|.
name|unwrapSingletonBits
argument_list|(
name|values
argument_list|)
decl_stmt|;
if|if
condition|(
name|docsWithField
operator|==
literal|null
operator|||
name|missingValue
operator|==
literal|0
condition|)
block|{
return|return
name|singleton
return|;
block|}
else|else
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
specifier|final
name|double
name|value
init|=
name|singleton
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|0
operator|&&
name|docsWithField
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
name|missingValue
return|;
block|}
return|return
name|value
return|;
block|}
block|}
return|;
block|}
block|}
else|else
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
name|values
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
return|return
name|pick
argument_list|(
name|values
argument_list|,
name|missingValue
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
DECL|method|pick
specifier|protected
name|BytesRef
name|pick
parameter_list|(
name|SortedBinaryDocValues
name|values
parameter_list|,
name|BytesRef
name|missingValue
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Unsupported sort mode: "
operator|+
name|this
argument_list|)
throw|;
block|}
DECL|method|select
specifier|public
name|BinaryDocValues
name|select
parameter_list|(
specifier|final
name|SortedBinaryDocValues
name|values
parameter_list|,
specifier|final
name|BytesRef
name|missingValue
parameter_list|)
block|{
specifier|final
name|BinaryDocValues
name|singleton
init|=
name|FieldData
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
name|FieldData
operator|.
name|unwrapSingletonBits
argument_list|(
name|values
argument_list|)
decl_stmt|;
if|if
condition|(
name|docsWithField
operator|==
literal|null
operator|||
name|missingValue
operator|==
literal|null
operator|||
name|missingValue
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|singleton
return|;
block|}
else|else
block|{
return|return
operator|new
name|BinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BytesRef
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
specifier|final
name|BytesRef
name|value
init|=
name|singleton
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|length
operator|==
literal|0
operator|&&
name|docsWithField
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
name|missingValue
return|;
block|}
return|return
name|value
return|;
block|}
block|}
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|BinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BytesRef
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
return|return
name|pick
argument_list|(
name|values
argument_list|,
name|missingValue
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
DECL|method|pick
specifier|protected
name|int
name|pick
parameter_list|(
name|RandomAccessOrds
name|values
parameter_list|,
name|int
name|missingOrd
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Unsupported sort mode: "
operator|+
name|this
argument_list|)
throw|;
block|}
DECL|method|select
specifier|public
name|SortedDocValues
name|select
parameter_list|(
specifier|final
name|RandomAccessOrds
name|values
parameter_list|,
specifier|final
name|int
name|missingOrd
parameter_list|)
block|{
if|if
condition|(
name|values
operator|.
name|getValueCount
argument_list|()
operator|>=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"fields containing more than "
operator|+
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
operator|)
operator|+
literal|" unique terms are unsupported"
argument_list|)
throw|;
block|}
specifier|final
name|SortedDocValues
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
if|if
condition|(
name|missingOrd
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|singleton
return|;
block|}
else|else
block|{
return|return
operator|new
name|SortedDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|singleton
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|missingOrd
return|;
block|}
return|return
name|ord
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
return|return
name|values
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
name|int
name|getValueCount
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|values
operator|.
name|getValueCount
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|SortedDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
return|return
name|pick
argument_list|(
name|values
argument_list|,
name|missingOrd
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
return|return
name|values
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
name|int
name|getValueCount
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|values
operator|.
name|getValueCount
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_enum

end_unit

