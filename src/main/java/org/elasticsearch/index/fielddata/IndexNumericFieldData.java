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
name|TermsEnum
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
name|SortField
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
name|index
operator|.
name|fielddata
operator|.
name|ordinals
operator|.
name|OrdinalsBuilder
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_interface
DECL|interface|IndexNumericFieldData
specifier|public
interface|interface
name|IndexNumericFieldData
extends|extends
name|IndexFieldData
argument_list|<
name|AtomicNumericFieldData
argument_list|>
block|{
DECL|enum|NumericType
specifier|public
specifier|static
enum|enum
name|NumericType
block|{
DECL|enum constant|BYTE
name|BYTE
argument_list|(
literal|8
argument_list|,
literal|false
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|toLong
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
name|INT
operator|.
name|toLong
argument_list|(
name|indexForm
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|toIndexForm
parameter_list|(
name|Number
name|number
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
block|{
name|INT
operator|.
name|toIndexForm
argument_list|(
name|number
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|toNumber
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
name|INT
operator|.
name|toNumber
argument_list|(
name|indexForm
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|SHORT
name|SHORT
argument_list|(
literal|16
argument_list|,
literal|false
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
name|Short
operator|.
name|MIN_VALUE
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|toLong
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
name|INT
operator|.
name|toLong
argument_list|(
name|indexForm
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|toIndexForm
parameter_list|(
name|Number
name|number
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
block|{
name|INT
operator|.
name|toIndexForm
argument_list|(
name|number
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|toNumber
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
name|INT
operator|.
name|toNumber
argument_list|(
name|indexForm
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|INT
name|INT
argument_list|(
literal|32
argument_list|,
literal|false
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|toLong
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|indexForm
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|toIndexForm
parameter_list|(
name|Number
name|number
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
block|{
name|NumericUtils
operator|.
name|intToPrefixCodedBytes
argument_list|(
name|number
operator|.
name|intValue
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|toNumber
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|indexForm
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|LONG
name|LONG
argument_list|(
literal|64
argument_list|,
literal|false
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|toLong
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|indexForm
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|toIndexForm
parameter_list|(
name|Number
name|number
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
block|{
name|NumericUtils
operator|.
name|longToPrefixCodedBytes
argument_list|(
name|number
operator|.
name|longValue
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|toNumber
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|indexForm
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|FLOAT
name|FLOAT
argument_list|(
literal|32
argument_list|,
literal|true
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
name|Float
operator|.
name|NEGATIVE_INFINITY
argument_list|,
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|double
name|toDouble
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|sortableIntToFloat
argument_list|(
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|indexForm
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|toIndexForm
parameter_list|(
name|Number
name|number
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
block|{
name|NumericUtils
operator|.
name|intToPrefixCodedBytes
argument_list|(
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
name|number
operator|.
name|floatValue
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|toNumber
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|sortableIntToFloat
argument_list|(
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|indexForm
argument_list|)
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|DOUBLE
name|DOUBLE
argument_list|(
literal|64
argument_list|,
literal|true
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|double
name|toDouble
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|sortableLongToDouble
argument_list|(
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|indexForm
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|toIndexForm
parameter_list|(
name|Number
name|number
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
block|{
name|NumericUtils
operator|.
name|longToPrefixCodedBytes
argument_list|(
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|number
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|toNumber
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|sortableLongToDouble
argument_list|(
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|indexForm
argument_list|)
argument_list|)
return|;
block|}
block|}
block|;
DECL|field|requiredBits
specifier|private
specifier|final
name|int
name|requiredBits
decl_stmt|;
DECL|field|floatingPoint
specifier|private
specifier|final
name|boolean
name|floatingPoint
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|SortField
operator|.
name|Type
name|type
decl_stmt|;
DECL|field|minValue
DECL|field|maxValue
specifier|private
specifier|final
name|Number
name|minValue
block|,
name|maxValue
block|;
DECL|method|NumericType
specifier|private
name|NumericType
parameter_list|(
name|int
name|requiredBits
parameter_list|,
name|boolean
name|floatingPoint
parameter_list|,
name|SortField
operator|.
name|Type
name|type
parameter_list|,
name|Number
name|minValue
parameter_list|,
name|Number
name|maxValue
parameter_list|)
block|{
name|this
operator|.
name|requiredBits
operator|=
name|requiredBits
expr_stmt|;
name|this
operator|.
name|floatingPoint
operator|=
name|floatingPoint
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|minValue
operator|=
name|minValue
expr_stmt|;
name|this
operator|.
name|maxValue
operator|=
name|maxValue
expr_stmt|;
block|}
DECL|method|sortFieldType
specifier|public
specifier|final
name|SortField
operator|.
name|Type
name|sortFieldType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|minValue
specifier|public
specifier|final
name|Number
name|minValue
parameter_list|()
block|{
return|return
name|minValue
return|;
block|}
DECL|method|maxValue
specifier|public
specifier|final
name|Number
name|maxValue
parameter_list|()
block|{
return|return
name|maxValue
return|;
block|}
DECL|method|isFloatingPoint
specifier|public
specifier|final
name|boolean
name|isFloatingPoint
parameter_list|()
block|{
return|return
name|floatingPoint
return|;
block|}
DECL|method|requiredBits
specifier|public
specifier|final
name|int
name|requiredBits
parameter_list|()
block|{
return|return
name|requiredBits
return|;
block|}
DECL|method|toIndexForm
specifier|public
specifier|abstract
name|void
name|toIndexForm
parameter_list|(
name|Number
name|number
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
function_decl|;
DECL|method|toLong
specifier|public
name|long
name|toLong
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|toDouble
argument_list|(
name|indexForm
argument_list|)
return|;
block|}
DECL|method|toDouble
specifier|public
name|double
name|toDouble
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|toLong
argument_list|(
name|indexForm
argument_list|)
return|;
block|}
DECL|method|toNumber
specifier|public
specifier|abstract
name|Number
name|toNumber
parameter_list|(
name|BytesRef
name|indexForm
parameter_list|)
function_decl|;
DECL|method|wrapTermsEnum
specifier|public
specifier|final
name|TermsEnum
name|wrapTermsEnum
parameter_list|(
name|TermsEnum
name|termsEnum
parameter_list|)
block|{
if|if
condition|(
name|requiredBits
argument_list|()
operator|>
literal|32
condition|)
block|{
return|return
name|OrdinalsBuilder
operator|.
name|wrapNumeric64Bit
argument_list|(
name|termsEnum
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|OrdinalsBuilder
operator|.
name|wrapNumeric32Bit
argument_list|(
name|termsEnum
argument_list|)
return|;
block|}
block|}
block|}
DECL|method|getNumericType
name|NumericType
name|getNumericType
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

