begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|search
operator|.
name|FieldCache
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
name|FixedBitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|Nullable
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
name|settings
operator|.
name|Settings
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
name|ordinals
operator|.
name|MultiFlatArrayOrdinals
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
name|settings
operator|.
name|IndexSettings
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|IntArrayIndexFieldData
specifier|public
class|class
name|IntArrayIndexFieldData
extends|extends
name|AbstractIndexFieldData
argument_list|<
name|IntArrayAtomicFieldData
argument_list|>
implements|implements
name|IndexNumericFieldData
argument_list|<
name|IntArrayAtomicFieldData
argument_list|>
block|{
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
implements|implements
name|IndexFieldData
operator|.
name|Builder
block|{
annotation|@
name|Override
DECL|method|build
specifier|public
name|IndexFieldData
name|build
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|FieldMapper
operator|.
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|type
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|)
block|{
return|return
operator|new
name|IntArrayIndexFieldData
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|fieldNames
argument_list|,
name|type
argument_list|,
name|cache
argument_list|)
return|;
block|}
block|}
DECL|method|IntArrayIndexFieldData
specifier|public
name|IntArrayIndexFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|FieldMapper
operator|.
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|fieldNames
argument_list|,
name|fieldDataType
argument_list|,
name|cache
argument_list|)
expr_stmt|;
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
name|NumericType
operator|.
name|INT
return|;
block|}
annotation|@
name|Override
DECL|method|valuesOrdered
specifier|public
name|boolean
name|valuesOrdered
parameter_list|()
block|{
comment|// because we might have single values? we can dynamically update a flag to reflect that
comment|// based on the atomic field data loaded
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|IntArrayAtomicFieldData
name|load
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
try|try
block|{
return|return
name|cache
operator|.
name|load
argument_list|(
name|context
argument_list|,
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|ElasticSearchException
condition|)
block|{
throw|throw
operator|(
name|ElasticSearchException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|loadDirect
specifier|public
name|IntArrayAtomicFieldData
name|loadDirect
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|AtomicReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|IntArrayAtomicFieldData
operator|.
name|Single
argument_list|(
operator|new
name|int
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|// TODO: how can we guess the number of terms? numerics end up creating more terms per value...
specifier|final
name|TIntArrayList
name|values
init|=
operator|new
name|TIntArrayList
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|int
index|[]
argument_list|>
name|ordinals
init|=
operator|new
name|ArrayList
argument_list|<
name|int
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|int
index|[]
name|idx
init|=
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|ordinals
operator|.
name|add
argument_list|(
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// first "t" indicates null value
name|int
name|termOrd
init|=
literal|1
decl_stmt|;
comment|// current term number
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|DocsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
init|;
name|term
operator|!=
literal|null
condition|;
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
control|)
block|{
name|values
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
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
name|docsEnum
argument_list|,
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|docId
init|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
init|;
name|docId
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|;
name|docId
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|int
index|[]
name|ordinal
decl_stmt|;
if|if
condition|(
name|idx
index|[
name|docId
index|]
operator|>=
name|ordinals
operator|.
name|size
argument_list|()
condition|)
block|{
name|ordinal
operator|=
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
expr_stmt|;
name|ordinals
operator|.
name|add
argument_list|(
name|ordinal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ordinal
operator|=
name|ordinals
operator|.
name|get
argument_list|(
name|idx
index|[
name|docId
index|]
argument_list|)
expr_stmt|;
block|}
name|ordinal
index|[
name|docId
index|]
operator|=
name|termOrd
expr_stmt|;
name|idx
index|[
name|docId
index|]
operator|++
expr_stmt|;
block|}
name|termOrd
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"StopFillCacheException"
argument_list|)
condition|)
block|{
comment|// all is well, in case numeric parsers are used.
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
if|if
condition|(
name|ordinals
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|int
index|[]
name|nativeOrdinals
init|=
name|ordinals
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|int
index|[]
name|sValues
init|=
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|boolean
name|allHaveValue
init|=
literal|true
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
name|nativeOrdinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|nativeOrdinal
init|=
name|nativeOrdinals
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|nativeOrdinal
operator|==
literal|0
condition|)
block|{
name|allHaveValue
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|set
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|sValues
index|[
name|i
index|]
operator|=
name|values
operator|.
name|get
argument_list|(
name|nativeOrdinal
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|allHaveValue
condition|)
block|{
return|return
operator|new
name|IntArrayAtomicFieldData
operator|.
name|Single
argument_list|(
name|sValues
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|IntArrayAtomicFieldData
operator|.
name|SingleFixedSet
argument_list|(
name|sValues
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|set
argument_list|)
return|;
block|}
block|}
else|else
block|{
name|int
index|[]
index|[]
name|nativeOrdinals
init|=
operator|new
name|int
index|[
name|ordinals
operator|.
name|size
argument_list|()
index|]
index|[]
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
name|nativeOrdinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nativeOrdinals
index|[
name|i
index|]
operator|=
name|ordinals
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|IntArrayAtomicFieldData
operator|.
name|WithOrdinals
argument_list|(
name|values
operator|.
name|toArray
argument_list|(
operator|new
name|int
index|[
name|values
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
operator|new
name|MultiFlatArrayOrdinals
argument_list|(
name|nativeOrdinals
argument_list|,
name|termOrd
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|comparatorSource
specifier|public
name|XFieldComparatorSource
name|comparatorSource
parameter_list|(
annotation|@
name|Nullable
name|Object
name|missingValue
parameter_list|)
block|{
return|return
operator|new
name|DoubleValuesComparatorSource
argument_list|(
name|this
argument_list|,
name|missingValue
argument_list|)
return|;
block|}
block|}
end_class

end_unit

