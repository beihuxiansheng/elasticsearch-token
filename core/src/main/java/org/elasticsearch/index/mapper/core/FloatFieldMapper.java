begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|FloatArrayList
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|TokenStream
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
operator|.
name|NumericType
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
name|index
operator|.
name|Terms
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
name|NumericRangeQuery
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
name|BytesRefBuilder
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
name|action
operator|.
name|fieldstats
operator|.
name|FieldStats
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
name|Numbers
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
name|Strings
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
name|unit
operator|.
name|Fuzziness
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
name|common
operator|.
name|util
operator|.
name|CollectionUtils
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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
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
name|analysis
operator|.
name|NumericFloatAnalyzer
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
name|mapper
operator|.
name|MappedFieldType
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
name|Mapper
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
name|MapperParsingException
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
name|MergeMappingException
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
name|MergeResult
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
name|ParseContext
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
name|util
operator|.
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|NumericUtils
operator|.
name|floatToSortableInt
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|support
operator|.
name|XContentMapValues
operator|.
name|nodeFloatValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MapperBuilders
operator|.
name|floatField
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|core
operator|.
name|TypeParsers
operator|.
name|parseNumberField
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|FloatFieldMapper
specifier|public
class|class
name|FloatFieldMapper
extends|extends
name|NumberFieldMapper
block|{
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"float"
decl_stmt|;
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|NumberFieldMapper
operator|.
name|Defaults
block|{
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|MappedFieldType
name|FIELD_TYPE
init|=
operator|new
name|FloatFieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|FIELD_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|NumberFieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|FloatFieldMapper
argument_list|>
block|{
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|,
name|Defaults
operator|.
name|PRECISION_STEP_32_BIT
argument_list|)
expr_stmt|;
name|builder
operator|=
name|this
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|FloatFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|setupFieldType
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|FloatFieldMapper
name|fieldMapper
init|=
operator|new
name|FloatFieldMapper
argument_list|(
name|fieldType
argument_list|,
name|docValues
argument_list|,
name|ignoreMalformed
argument_list|(
name|context
argument_list|)
argument_list|,
name|coerce
argument_list|(
name|context
argument_list|)
argument_list|,
name|fieldDataSettings
argument_list|,
name|context
operator|.
name|indexSettings
argument_list|()
argument_list|,
name|multiFieldsBuilder
operator|.
name|build
argument_list|(
name|this
argument_list|,
name|context
argument_list|)
argument_list|,
name|copyTo
argument_list|)
decl_stmt|;
name|fieldMapper
operator|.
name|includeInAll
argument_list|(
name|includeInAll
argument_list|)
expr_stmt|;
return|return
name|fieldMapper
return|;
block|}
annotation|@
name|Override
DECL|method|makeNumberAnalyzer
specifier|protected
name|NamedAnalyzer
name|makeNumberAnalyzer
parameter_list|(
name|int
name|precisionStep
parameter_list|)
block|{
return|return
name|NumericFloatAnalyzer
operator|.
name|buildNamedAnalyzer
argument_list|(
name|precisionStep
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|maxPrecisionStep
specifier|protected
name|int
name|maxPrecisionStep
parameter_list|()
block|{
return|return
literal|32
return|;
block|}
block|}
DECL|class|TypeParser
specifier|public
specifier|static
class|class
name|TypeParser
implements|implements
name|Mapper
operator|.
name|TypeParser
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Mapper
operator|.
name|Builder
name|parse
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|node
parameter_list|,
name|ParserContext
name|parserContext
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|FloatFieldMapper
operator|.
name|Builder
name|builder
init|=
name|floatField
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|parseNumberField
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|node
argument_list|,
name|parserContext
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|iterator
init|=
name|node
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|propName
init|=
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|propNode
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|propName
operator|.
name|equals
argument_list|(
literal|"null_value"
argument_list|)
condition|)
block|{
if|if
condition|(
name|propNode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Property [null_value] cannot be null."
argument_list|)
throw|;
block|}
name|builder
operator|.
name|nullValue
argument_list|(
name|nodeFloatValue
argument_list|(
name|propNode
argument_list|)
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
block|}
DECL|class|FloatFieldType
specifier|static
specifier|final
class|class
name|FloatFieldType
extends|extends
name|NumberFieldType
block|{
DECL|method|FloatFieldType
specifier|public
name|FloatFieldType
parameter_list|()
block|{
name|super
argument_list|(
name|NumericType
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
block|}
DECL|method|FloatFieldType
specifier|protected
name|FloatFieldType
parameter_list|(
name|FloatFieldType
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|NumberFieldType
name|clone
parameter_list|()
block|{
return|return
operator|new
name|FloatFieldType
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nullValue
specifier|public
name|Float
name|nullValue
parameter_list|()
block|{
return|return
operator|(
name|Float
operator|)
name|super
operator|.
name|nullValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|Float
name|value
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
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
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|floatValue
argument_list|()
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|BytesRef
condition|)
block|{
return|return
name|Numbers
operator|.
name|bytesToFloat
argument_list|(
operator|(
name|BytesRef
operator|)
name|value
argument_list|)
return|;
block|}
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|indexedValueForSearch
specifier|public
name|BytesRef
name|indexedValueForSearch
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|int
name|intValue
init|=
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
name|parseValue
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRefBuilder
name|bytesRef
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|intValue
argument_list|,
literal|0
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
comment|// 0 because of exact match
return|return
name|bytesRef
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|rangeQuery
specifier|public
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
block|{
return|return
name|NumericRangeQuery
operator|.
name|newFloatRange
argument_list|(
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|numericPrecisionStep
argument_list|()
argument_list|,
name|lowerTerm
operator|==
literal|null
condition|?
literal|null
else|:
name|parseValue
argument_list|(
name|lowerTerm
argument_list|)
argument_list|,
name|upperTerm
operator|==
literal|null
condition|?
literal|null
else|:
name|parseValue
argument_list|(
name|upperTerm
argument_list|)
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fuzzyQuery
specifier|public
name|Query
name|fuzzyQuery
parameter_list|(
name|String
name|value
parameter_list|,
name|Fuzziness
name|fuzziness
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
block|{
name|float
name|iValue
init|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|value
argument_list|)
decl_stmt|;
specifier|final
name|float
name|iSim
init|=
name|fuzziness
operator|.
name|asFloat
argument_list|()
decl_stmt|;
return|return
name|NumericRangeQuery
operator|.
name|newFloatRange
argument_list|(
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|numericPrecisionStep
argument_list|()
argument_list|,
name|iValue
operator|-
name|iSim
argument_list|,
name|iValue
operator|+
name|iSim
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|stats
specifier|public
name|FieldStats
name|stats
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|float
name|minValue
init|=
name|NumericUtils
operator|.
name|sortableIntToFloat
argument_list|(
name|NumericUtils
operator|.
name|getMinInt
argument_list|(
name|terms
argument_list|)
argument_list|)
decl_stmt|;
name|float
name|maxValue
init|=
name|NumericUtils
operator|.
name|sortableIntToFloat
argument_list|(
name|NumericUtils
operator|.
name|getMaxInt
argument_list|(
name|terms
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|FieldStats
operator|.
name|Float
argument_list|(
name|maxDoc
argument_list|,
name|terms
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|terms
operator|.
name|getSumDocFreq
argument_list|()
argument_list|,
name|terms
operator|.
name|getSumTotalTermFreq
argument_list|()
argument_list|,
name|minValue
argument_list|,
name|maxValue
argument_list|)
return|;
block|}
block|}
DECL|method|FloatFieldMapper
specifier|protected
name|FloatFieldMapper
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|,
name|Boolean
name|docValues
parameter_list|,
name|Explicit
argument_list|<
name|Boolean
argument_list|>
name|ignoreMalformed
parameter_list|,
name|Explicit
argument_list|<
name|Boolean
argument_list|>
name|coerce
parameter_list|,
annotation|@
name|Nullable
name|Settings
name|fieldDataSettings
parameter_list|,
name|Settings
name|indexSettings
parameter_list|,
name|MultiFields
name|multiFields
parameter_list|,
name|CopyTo
name|copyTo
parameter_list|)
block|{
name|super
argument_list|(
name|fieldType
argument_list|,
name|docValues
argument_list|,
name|ignoreMalformed
argument_list|,
name|coerce
argument_list|,
name|fieldDataSettings
argument_list|,
name|indexSettings
argument_list|,
name|multiFields
argument_list|,
name|copyTo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldType
specifier|public
name|FloatFieldType
name|fieldType
parameter_list|()
block|{
return|return
operator|(
name|FloatFieldType
operator|)
name|super
operator|.
name|fieldType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|defaultFieldType
specifier|public
name|MappedFieldType
name|defaultFieldType
parameter_list|()
block|{
return|return
name|Defaults
operator|.
name|FIELD_TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|defaultFieldDataType
specifier|public
name|FieldDataType
name|defaultFieldDataType
parameter_list|()
block|{
return|return
operator|new
name|FieldDataType
argument_list|(
literal|"float"
argument_list|)
return|;
block|}
DECL|method|parseValue
specifier|private
specifier|static
name|float
name|parseValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|floatValue
argument_list|()
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|BytesRef
condition|)
block|{
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
operator|(
operator|(
name|BytesRef
operator|)
name|value
operator|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
return|;
block|}
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|customBoost
specifier|protected
name|boolean
name|customBoost
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|innerParseCreateField
specifier|protected
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
block|{
name|float
name|value
decl_stmt|;
name|float
name|boost
init|=
name|fieldType
argument_list|()
operator|.
name|boost
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|externalValueSet
argument_list|()
condition|)
block|{
name|Object
name|externalValue
init|=
name|context
operator|.
name|externalValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|externalValue
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|nullValue
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|value
operator|=
name|fieldType
argument_list|()
operator|.
name|nullValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|externalValue
operator|instanceof
name|String
condition|)
block|{
name|String
name|sExternalValue
init|=
operator|(
name|String
operator|)
name|externalValue
decl_stmt|;
if|if
condition|(
name|sExternalValue
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|nullValue
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|value
operator|=
name|fieldType
argument_list|()
operator|.
name|nullValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|sExternalValue
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|value
operator|=
operator|(
operator|(
name|Number
operator|)
name|externalValue
operator|)
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|includeInAll
argument_list|(
name|includeInAll
argument_list|,
name|this
argument_list|)
condition|)
block|{
name|context
operator|.
name|allEntries
argument_list|()
operator|.
name|addText
argument_list|(
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|,
name|Float
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|XContentParser
name|parser
init|=
name|context
operator|.
name|parser
argument_list|()
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NULL
operator|||
operator|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
operator|&&
name|parser
operator|.
name|textLength
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|nullValue
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|value
operator|=
name|fieldType
argument_list|()
operator|.
name|nullValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|nullValueAsString
argument_list|()
operator|!=
literal|null
operator|&&
operator|(
name|context
operator|.
name|includeInAll
argument_list|(
name|includeInAll
argument_list|,
name|this
argument_list|)
operator|)
condition|)
block|{
name|context
operator|.
name|allEntries
argument_list|()
operator|.
name|addText
argument_list|(
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|,
name|fieldType
argument_list|()
operator|.
name|nullValueAsString
argument_list|()
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|Float
name|objValue
init|=
name|fieldType
argument_list|()
operator|.
name|nullValue
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
literal|"value"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"_value"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NULL
condition|)
block|{
name|objValue
operator|=
name|parser
operator|.
name|floatValue
argument_list|(
name|coerce
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"_boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|boost
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown property ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|objValue
operator|==
literal|null
condition|)
block|{
comment|// no value
return|return;
block|}
name|value
operator|=
name|objValue
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|parser
operator|.
name|floatValue
argument_list|(
name|coerce
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|includeInAll
argument_list|(
name|includeInAll
argument_list|,
name|this
argument_list|)
condition|)
block|{
name|context
operator|.
name|allEntries
argument_list|()
operator|.
name|addText
argument_list|(
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|,
name|parser
operator|.
name|text
argument_list|()
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
operator|||
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
condition|)
block|{
name|CustomFloatNumericField
name|field
init|=
operator|new
name|CustomFloatNumericField
argument_list|(
name|this
argument_list|,
name|value
argument_list|,
name|fieldType
argument_list|()
argument_list|)
decl_stmt|;
name|field
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
if|if
condition|(
name|useSortedNumericDocValues
condition|)
block|{
name|addDocValue
argument_list|(
name|context
argument_list|,
name|fields
argument_list|,
name|floatToSortableInt
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CustomFloatNumericDocValuesField
name|field
init|=
operator|(
name|CustomFloatNumericDocValuesField
operator|)
name|context
operator|.
name|doc
argument_list|()
operator|.
name|getByKey
argument_list|(
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|field
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|field
operator|=
operator|new
name|CustomFloatNumericDocValuesField
argument_list|(
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|context
operator|.
name|doc
argument_list|()
operator|.
name|addWithKey
argument_list|(
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|contentType
specifier|protected
name|String
name|contentType
parameter_list|()
block|{
return|return
name|CONTENT_TYPE
return|;
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
parameter_list|,
name|boolean
name|includeDefaults
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|doXContentBody
argument_list|(
name|builder
argument_list|,
name|includeDefaults
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|numericPrecisionStep
argument_list|()
operator|!=
name|Defaults
operator|.
name|PRECISION_STEP_32_BIT
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"precision_step"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|numericPrecisionStep
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|nullValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"null_value"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeInAll
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"include_in_all"
argument_list|,
name|includeInAll
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeDefaults
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"include_in_all"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CustomFloatNumericField
specifier|public
specifier|static
class|class
name|CustomFloatNumericField
extends|extends
name|CustomNumericField
block|{
DECL|field|number
specifier|private
specifier|final
name|float
name|number
decl_stmt|;
DECL|field|mapper
specifier|private
specifier|final
name|NumberFieldMapper
name|mapper
decl_stmt|;
DECL|method|CustomFloatNumericField
specifier|public
name|CustomFloatNumericField
parameter_list|(
name|NumberFieldMapper
name|mapper
parameter_list|,
name|float
name|number
parameter_list|,
name|NumberFieldType
name|fieldType
parameter_list|)
block|{
name|super
argument_list|(
name|mapper
argument_list|,
name|number
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
name|this
operator|.
name|number
operator|=
name|number
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|TokenStream
name|previous
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
return|return
name|mapper
operator|.
name|popCachedStream
argument_list|()
operator|.
name|setFloatValue
argument_list|(
name|number
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|numericAsString
specifier|public
name|String
name|numericAsString
parameter_list|()
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|number
argument_list|)
return|;
block|}
block|}
DECL|class|CustomFloatNumericDocValuesField
specifier|public
specifier|static
class|class
name|CustomFloatNumericDocValuesField
extends|extends
name|CustomNumericDocValuesField
block|{
DECL|field|values
specifier|private
specifier|final
name|FloatArrayList
name|values
decl_stmt|;
DECL|method|CustomFloatNumericDocValuesField
specifier|public
name|CustomFloatNumericDocValuesField
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|FloatArrayList
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|float
name|value
parameter_list|)
block|{
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|binaryValue
specifier|public
name|BytesRef
name|binaryValue
parameter_list|()
block|{
name|CollectionUtils
operator|.
name|sortAndDedup
argument_list|(
name|values
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|values
operator|.
name|size
argument_list|()
operator|*
literal|4
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
name|values
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|ByteUtils
operator|.
name|writeFloatLE
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|bytes
argument_list|,
name|i
operator|*
literal|4
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

