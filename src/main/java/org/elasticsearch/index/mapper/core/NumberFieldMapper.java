begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.core
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|core
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
name|analysis
operator|.
name|NumericTokenStream
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FieldType
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
name|document
operator|.
name|SortedSetDocValuesField
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|search
operator|.
name|Query
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
name|Explicit
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
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
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
name|analysis
operator|.
name|NamedAnalyzer
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
name|codec
operator|.
name|docvaluesformat
operator|.
name|DocValuesFormatProvider
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
name|codec
operator|.
name|postingsformat
operator|.
name|PostingsFormatProvider
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
name|IndexFieldDataService
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
name|mapper
operator|.
name|internal
operator|.
name|AllFieldMapper
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
name|query
operator|.
name|QueryParseContext
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
name|similarity
operator|.
name|SimilarityProvider
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
comment|/**  *  */
end_comment

begin_class
DECL|class|NumberFieldMapper
specifier|public
specifier|abstract
class|class
name|NumberFieldMapper
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
extends|extends
name|AbstractFieldMapper
argument_list|<
name|T
argument_list|>
implements|implements
name|AllFieldMapper
operator|.
name|IncludeInAll
block|{
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|AbstractFieldMapper
operator|.
name|Defaults
block|{
DECL|field|PRECISION_STEP
specifier|public
specifier|static
specifier|final
name|int
name|PRECISION_STEP
init|=
name|NumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
decl_stmt|;
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|FieldType
name|FIELD_TYPE
init|=
operator|new
name|FieldType
argument_list|(
name|AbstractFieldMapper
operator|.
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
decl_stmt|;
static|static
block|{
name|FIELD_TYPE
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setStoreTermVectors
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
DECL|field|IGNORE_MALFORMED
specifier|public
specifier|static
specifier|final
name|Explicit
argument_list|<
name|Boolean
argument_list|>
name|IGNORE_MALFORMED
init|=
operator|new
name|Explicit
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
block|}
DECL|class|Builder
specifier|public
specifier|abstract
specifier|static
class|class
name|Builder
parameter_list|<
name|T
extends|extends
name|Builder
parameter_list|,
name|Y
extends|extends
name|NumberFieldMapper
parameter_list|>
extends|extends
name|AbstractFieldMapper
operator|.
name|Builder
argument_list|<
name|T
argument_list|,
name|Y
argument_list|>
block|{
DECL|field|precisionStep
specifier|protected
name|int
name|precisionStep
init|=
name|Defaults
operator|.
name|PRECISION_STEP
decl_stmt|;
DECL|field|ignoreMalformed
specifier|private
name|Boolean
name|ignoreMalformed
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|fieldType
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|fieldType
argument_list|)
expr_stmt|;
block|}
DECL|method|precisionStep
specifier|public
name|T
name|precisionStep
parameter_list|(
name|int
name|precisionStep
parameter_list|)
block|{
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|ignoreMalformed
specifier|public
name|T
name|ignoreMalformed
parameter_list|(
name|boolean
name|ignoreMalformed
parameter_list|)
block|{
name|this
operator|.
name|ignoreMalformed
operator|=
name|ignoreMalformed
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|ignoreMalformed
specifier|protected
name|Explicit
argument_list|<
name|Boolean
argument_list|>
name|ignoreMalformed
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|ignoreMalformed
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|Explicit
argument_list|<
name|Boolean
argument_list|>
argument_list|(
name|ignoreMalformed
argument_list|,
literal|true
argument_list|)
return|;
block|}
if|if
condition|(
name|context
operator|.
name|indexSettings
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|Explicit
argument_list|<
name|Boolean
argument_list|>
argument_list|(
name|context
operator|.
name|indexSettings
argument_list|()
operator|.
name|getAsBoolean
argument_list|(
literal|"index.mapping.ignore_malformed"
argument_list|,
name|Defaults
operator|.
name|IGNORE_MALFORMED
operator|.
name|value
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
return|;
block|}
return|return
name|Defaults
operator|.
name|IGNORE_MALFORMED
return|;
block|}
block|}
DECL|field|precisionStep
specifier|protected
name|int
name|precisionStep
decl_stmt|;
DECL|field|includeInAll
specifier|protected
name|Boolean
name|includeInAll
decl_stmt|;
DECL|field|ignoreMalformed
specifier|protected
name|Explicit
argument_list|<
name|Boolean
argument_list|>
name|ignoreMalformed
decl_stmt|;
DECL|field|tokenStream
specifier|private
name|ThreadLocal
argument_list|<
name|NumericTokenStream
argument_list|>
name|tokenStream
init|=
operator|new
name|ThreadLocal
argument_list|<
name|NumericTokenStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|NumericTokenStream
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|NumericTokenStream
argument_list|(
name|precisionStep
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|tokenStream4
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|NumericTokenStream
argument_list|>
name|tokenStream4
init|=
operator|new
name|ThreadLocal
argument_list|<
name|NumericTokenStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|NumericTokenStream
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|NumericTokenStream
argument_list|(
literal|4
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|tokenStream8
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|NumericTokenStream
argument_list|>
name|tokenStream8
init|=
operator|new
name|ThreadLocal
argument_list|<
name|NumericTokenStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|NumericTokenStream
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|NumericTokenStream
argument_list|(
literal|8
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|tokenStreamMax
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|NumericTokenStream
argument_list|>
name|tokenStreamMax
init|=
operator|new
name|ThreadLocal
argument_list|<
name|NumericTokenStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|NumericTokenStream
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|NumericTokenStream
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|NumberFieldMapper
specifier|protected
name|NumberFieldMapper
parameter_list|(
name|Names
name|names
parameter_list|,
name|int
name|precisionStep
parameter_list|,
name|float
name|boost
parameter_list|,
name|FieldType
name|fieldType
parameter_list|,
name|Explicit
argument_list|<
name|Boolean
argument_list|>
name|ignoreMalformed
parameter_list|,
name|NamedAnalyzer
name|indexAnalyzer
parameter_list|,
name|NamedAnalyzer
name|searchAnalyzer
parameter_list|,
name|PostingsFormatProvider
name|postingsProvider
parameter_list|,
name|DocValuesFormatProvider
name|docValuesProvider
parameter_list|,
name|SimilarityProvider
name|similarity
parameter_list|,
annotation|@
name|Nullable
name|Settings
name|fieldDataSettings
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
comment|// LUCENE 4 UPGRADE: Since we can't do anything before the super call, we have to push the boost check down to subclasses
name|super
argument_list|(
name|names
argument_list|,
name|boost
argument_list|,
name|fieldType
argument_list|,
name|indexAnalyzer
argument_list|,
name|searchAnalyzer
argument_list|,
name|postingsProvider
argument_list|,
name|docValuesProvider
argument_list|,
name|similarity
argument_list|,
name|fieldDataSettings
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
if|if
condition|(
name|precisionStep
operator|<=
literal|0
operator|||
name|precisionStep
operator|>=
name|maxPrecisionStep
argument_list|()
condition|)
block|{
name|this
operator|.
name|precisionStep
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
block|}
name|this
operator|.
name|ignoreMalformed
operator|=
name|ignoreMalformed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|includeInAll
specifier|public
name|void
name|includeInAll
parameter_list|(
name|Boolean
name|includeInAll
parameter_list|)
block|{
if|if
condition|(
name|includeInAll
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|includeInAll
operator|=
name|includeInAll
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|includeInAllIfNotSet
specifier|public
name|void
name|includeInAllIfNotSet
parameter_list|(
name|Boolean
name|includeInAll
parameter_list|)
block|{
if|if
condition|(
name|includeInAll
operator|!=
literal|null
operator|&&
name|this
operator|.
name|includeInAll
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|includeInAll
operator|=
name|includeInAll
expr_stmt|;
block|}
block|}
DECL|method|maxPrecisionStep
specifier|protected
specifier|abstract
name|int
name|maxPrecisionStep
parameter_list|()
function_decl|;
DECL|method|precisionStep
specifier|public
name|int
name|precisionStep
parameter_list|()
block|{
return|return
name|this
operator|.
name|precisionStep
return|;
block|}
annotation|@
name|Override
DECL|method|parseCreateField
specifier|protected
name|void
name|parseCreateField
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|List
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
name|RuntimeException
name|e
init|=
literal|null
decl_stmt|;
try|try
block|{
name|innerParseCreateField
argument_list|(
name|context
argument_list|,
name|fields
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e1
parameter_list|)
block|{
name|e
operator|=
name|e1
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|e2
parameter_list|)
block|{
name|e
operator|=
name|e2
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|!=
literal|null
operator|&&
operator|!
name|ignoreMalformed
operator|.
name|value
argument_list|()
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
comment|/** Utility method to convert a long to a doc values field using {@link NumericUtils} encoding. */
DECL|method|toDocValues
specifier|protected
specifier|final
name|Field
name|toDocValues
parameter_list|(
name|long
name|l
parameter_list|)
block|{
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|l
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
operator|new
name|SortedSetDocValuesField
argument_list|(
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|bytes
argument_list|)
return|;
block|}
comment|/** Utility method to convert an int to a doc values field using {@link NumericUtils} encoding. */
DECL|method|toDocValues
specifier|protected
specifier|final
name|Field
name|toDocValues
parameter_list|(
name|int
name|i
parameter_list|)
block|{
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|i
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
operator|new
name|SortedSetDocValuesField
argument_list|(
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|bytes
argument_list|)
return|;
block|}
comment|/** Utility method to convert a float to a doc values field using {@link NumericUtils} encoding. */
DECL|method|toDocValues
specifier|protected
specifier|final
name|Field
name|toDocValues
parameter_list|(
name|float
name|f
parameter_list|)
block|{
return|return
name|toDocValues
argument_list|(
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
name|f
argument_list|)
argument_list|)
return|;
block|}
comment|/** Utility method to convert a double to a doc values field using {@link NumericUtils} encoding. */
DECL|method|toDocValues
specifier|protected
specifier|final
name|Field
name|toDocValues
parameter_list|(
name|double
name|d
parameter_list|)
block|{
return|return
name|toDocValues
argument_list|(
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|d
argument_list|)
argument_list|)
return|;
block|}
DECL|method|innerParseCreateField
specifier|protected
specifier|abstract
name|void
name|innerParseCreateField
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|List
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Use the field query created here when matching on numbers.      */
annotation|@
name|Override
DECL|method|useTermQueryWithQueryString
specifier|public
name|boolean
name|useTermQueryWithQueryString
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Numeric field level query are basically range queries with same value and included. That's the recommended      * way to execute it.      */
annotation|@
name|Override
DECL|method|termQuery
specifier|public
name|Query
name|termQuery
parameter_list|(
name|Object
name|value
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
block|{
return|return
name|rangeQuery
argument_list|(
name|value
argument_list|,
name|value
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|context
argument_list|)
return|;
block|}
comment|/**      * Numeric field level filter are basically range queries with same value and included. That's the recommended      * way to execute it.      */
annotation|@
name|Override
DECL|method|termFilter
specifier|public
name|Filter
name|termFilter
parameter_list|(
name|Object
name|value
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
block|{
return|return
name|rangeFilter
argument_list|(
name|value
argument_list|,
name|value
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|rangeQuery
specifier|public
specifier|abstract
name|Query
name|rangeQuery
parameter_list|(
name|Object
name|lowerTerm
parameter_list|,
name|Object
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|rangeFilter
specifier|public
specifier|abstract
name|Filter
name|rangeFilter
parameter_list|(
name|Object
name|lowerTerm
parameter_list|,
name|Object
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|fuzzyQuery
specifier|public
specifier|abstract
name|Query
name|fuzzyQuery
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|minSim
parameter_list|,
name|int
name|prefixLength
parameter_list|,
name|int
name|maxExpansions
parameter_list|,
name|boolean
name|transpositions
parameter_list|)
function_decl|;
comment|/**      * A range filter based on the field data cache.      */
DECL|method|rangeFilter
specifier|public
specifier|abstract
name|Filter
name|rangeFilter
parameter_list|(
name|IndexFieldDataService
name|fieldData
parameter_list|,
name|Object
name|lowerTerm
parameter_list|,
name|Object
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
function_decl|;
comment|/**      * Override the default behavior (to return the string, and return the actual Number instance).      *      * @param value      */
annotation|@
name|Override
DECL|method|valueForSearch
specifier|public
name|Object
name|valueForSearch
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|value
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|Mapper
name|mergeWith
parameter_list|,
name|MergeContext
name|mergeContext
parameter_list|)
throws|throws
name|MergeMappingException
block|{
name|super
operator|.
name|merge
argument_list|(
name|mergeWith
argument_list|,
name|mergeContext
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|mergeWith
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|mergeContext
operator|.
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|()
condition|)
block|{
name|NumberFieldMapper
name|nfmMergeWith
init|=
operator|(
name|NumberFieldMapper
operator|)
name|mergeWith
decl_stmt|;
name|this
operator|.
name|precisionStep
operator|=
name|nfmMergeWith
operator|.
name|precisionStep
expr_stmt|;
name|this
operator|.
name|includeInAll
operator|=
name|nfmMergeWith
operator|.
name|includeInAll
expr_stmt|;
if|if
condition|(
name|nfmMergeWith
operator|.
name|ignoreMalformed
operator|.
name|explicit
argument_list|()
condition|)
block|{
name|this
operator|.
name|ignoreMalformed
operator|=
name|nfmMergeWith
operator|.
name|ignoreMalformed
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{     }
DECL|method|popCachedStream
specifier|protected
name|NumericTokenStream
name|popCachedStream
parameter_list|()
block|{
if|if
condition|(
name|precisionStep
operator|==
literal|4
condition|)
block|{
return|return
name|tokenStream4
operator|.
name|get
argument_list|()
return|;
block|}
if|if
condition|(
name|precisionStep
operator|==
literal|8
condition|)
block|{
return|return
name|tokenStream8
operator|.
name|get
argument_list|()
return|;
block|}
if|if
condition|(
name|precisionStep
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
name|tokenStreamMax
operator|.
name|get
argument_list|()
return|;
block|}
return|return
name|tokenStream
operator|.
name|get
argument_list|()
return|;
block|}
comment|// used to we can use a numeric field in a document that is then parsed twice!
DECL|class|CustomNumericField
specifier|public
specifier|abstract
specifier|static
class|class
name|CustomNumericField
extends|extends
name|Field
block|{
DECL|field|mapper
specifier|protected
specifier|final
name|NumberFieldMapper
name|mapper
decl_stmt|;
DECL|method|CustomNumericField
specifier|public
name|CustomNumericField
parameter_list|(
name|NumberFieldMapper
name|mapper
parameter_list|,
name|Number
name|value
parameter_list|,
name|FieldType
name|fieldType
parameter_list|)
block|{
name|super
argument_list|(
name|mapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|fieldType
argument_list|)
expr_stmt|;
name|this
operator|.
name|mapper
operator|=
name|mapper
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|numericAsString
specifier|public
specifier|abstract
name|String
name|numericAsString
parameter_list|()
function_decl|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|protected
name|void
name|doXContentBody
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|doXContentBody
argument_list|(
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|ignoreMalformed
operator|.
name|explicit
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"ignore_malformed"
argument_list|,
name|ignoreMalformed
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|isNumeric
specifier|public
name|boolean
name|isNumeric
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

