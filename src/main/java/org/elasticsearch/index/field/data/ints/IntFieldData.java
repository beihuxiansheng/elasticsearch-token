begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.field.data.ints
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|field
operator|.
name|data
operator|.
name|ints
package|;
end_package

begin_import
import|import
name|gnu
operator|.
name|trove
operator|.
name|list
operator|.
name|array
operator|.
name|TIntArrayList
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
name|IndexReader
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
name|FieldCache
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
name|RamUsage
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
name|field
operator|.
name|data
operator|.
name|FieldDataType
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
name|field
operator|.
name|data
operator|.
name|NumericFieldData
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
name|field
operator|.
name|data
operator|.
name|support
operator|.
name|FieldDataLoader
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
comment|/**  *  */
end_comment

begin_class
DECL|class|IntFieldData
specifier|public
specifier|abstract
class|class
name|IntFieldData
extends|extends
name|NumericFieldData
argument_list|<
name|IntDocFieldData
argument_list|>
block|{
DECL|field|EMPTY_INT_ARRAY
specifier|static
specifier|final
name|int
index|[]
name|EMPTY_INT_ARRAY
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
DECL|field|values
specifier|protected
specifier|final
name|int
index|[]
name|values
decl_stmt|;
DECL|method|IntFieldData
specifier|protected
name|IntFieldData
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
index|[]
name|values
parameter_list|)
block|{
name|super
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeSizeInBytes
specifier|protected
name|long
name|computeSizeInBytes
parameter_list|()
block|{
return|return
name|RamUsage
operator|.
name|NUM_BYTES_INT
operator|*
name|values
operator|.
name|length
operator|+
name|RamUsage
operator|.
name|NUM_BYTES_ARRAY_HEADER
return|;
block|}
DECL|method|values
specifier|public
specifier|final
name|int
index|[]
name|values
parameter_list|()
block|{
return|return
name|this
operator|.
name|values
return|;
block|}
DECL|method|value
specifier|abstract
specifier|public
name|int
name|value
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
DECL|method|values
specifier|abstract
specifier|public
name|int
index|[]
name|values
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|docFieldData
specifier|public
name|IntDocFieldData
name|docFieldData
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|super
operator|.
name|docFieldData
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createFieldData
specifier|protected
name|IntDocFieldData
name|createFieldData
parameter_list|()
block|{
return|return
operator|new
name|IntDocFieldData
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|value
argument_list|(
name|docId
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|forEachValue
specifier|public
name|void
name|forEachValue
parameter_list|(
name|StringValueProc
name|proc
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|proc
operator|.
name|onValue
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|byteValue
specifier|public
name|byte
name|byteValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|value
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shortValue
specifier|public
name|short
name|shortValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|value
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|intValue
specifier|public
name|int
name|intValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|value
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|longValue
specifier|public
name|long
name|longValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|value
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|floatValue
specifier|public
name|float
name|floatValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|value
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doubleValue
specifier|public
name|double
name|doubleValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|value
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|FieldDataType
name|type
parameter_list|()
block|{
return|return
name|FieldDataType
operator|.
name|DefaultTypes
operator|.
name|INT
return|;
block|}
DECL|method|forEachValue
specifier|public
name|void
name|forEachValue
parameter_list|(
name|ValueProc
name|proc
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|proc
operator|.
name|onValue
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|interface|ValueProc
specifier|public
specifier|static
interface|interface
name|ValueProc
block|{
DECL|method|onValue
name|void
name|onValue
parameter_list|(
name|int
name|value
parameter_list|)
function_decl|;
block|}
DECL|method|forEachValueInDoc
specifier|public
specifier|abstract
name|void
name|forEachValueInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|ValueInDocProc
name|proc
parameter_list|)
function_decl|;
DECL|interface|ValueInDocProc
specifier|public
specifier|static
interface|interface
name|ValueInDocProc
block|{
DECL|method|onValue
name|void
name|onValue
parameter_list|(
name|int
name|docId
parameter_list|,
name|int
name|value
parameter_list|)
function_decl|;
DECL|method|onMissing
name|void
name|onMissing
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
block|}
DECL|method|load
specifier|public
specifier|static
name|IntFieldData
name|load
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FieldDataLoader
operator|.
name|load
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|IntTypeLoader
argument_list|()
argument_list|)
return|;
block|}
DECL|class|IntTypeLoader
specifier|static
class|class
name|IntTypeLoader
extends|extends
name|FieldDataLoader
operator|.
name|FreqsTypeLoader
argument_list|<
name|IntFieldData
argument_list|>
block|{
DECL|field|terms
specifier|private
specifier|final
name|TIntArrayList
name|terms
init|=
operator|new
name|TIntArrayList
argument_list|()
decl_stmt|;
DECL|method|IntTypeLoader
name|IntTypeLoader
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|// the first one indicates null value
name|terms
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collectTerm
specifier|public
name|void
name|collectTerm
parameter_list|(
name|String
name|term
parameter_list|)
block|{
name|terms
operator|.
name|add
argument_list|(
name|FieldCache
operator|.
name|NUMERIC_UTILS_INT_PARSER
operator|.
name|parseInt
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|buildSingleValue
specifier|public
name|IntFieldData
name|buildSingleValue
parameter_list|(
name|String
name|field
parameter_list|,
name|int
index|[]
name|ordinals
parameter_list|)
block|{
return|return
operator|new
name|SingleValueIntFieldData
argument_list|(
name|field
argument_list|,
name|ordinals
argument_list|,
name|terms
operator|.
name|toArray
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|buildMultiValue
specifier|public
name|IntFieldData
name|buildMultiValue
parameter_list|(
name|String
name|field
parameter_list|,
name|int
index|[]
index|[]
name|ordinals
parameter_list|)
block|{
return|return
operator|new
name|MultiValueIntFieldData
argument_list|(
name|field
argument_list|,
name|ordinals
argument_list|,
name|terms
operator|.
name|toArray
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

