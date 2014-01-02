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
name|NumericRangeFilter
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
name|NumericUtils
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
name|NumericIntegerAnalyzer
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
name|fielddata
operator|.
name|IndexNumericFieldData
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
name|search
operator|.
name|NumericRangeFieldDataFilter
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
name|nodeShortValue
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
name|shortField
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
DECL|class|ShortFieldMapper
specifier|public
class|class
name|ShortFieldMapper
extends|extends
name|NumberFieldMapper
argument_list|<
name|Short
argument_list|>
block|{
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"short"
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
name|FieldType
name|FIELD_TYPE
init|=
operator|new
name|FieldType
argument_list|(
name|NumberFieldMapper
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
name|freeze
argument_list|()
expr_stmt|;
block|}
DECL|field|NULL_VALUE
specifier|public
specifier|static
specifier|final
name|Short
name|NULL_VALUE
init|=
literal|null
decl_stmt|;
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
name|ShortFieldMapper
argument_list|>
block|{
DECL|field|nullValue
specifier|protected
name|Short
name|nullValue
init|=
name|Defaults
operator|.
name|NULL_VALUE
decl_stmt|;
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
operator|new
name|FieldType
argument_list|(
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
name|this
expr_stmt|;
block|}
DECL|method|nullValue
specifier|public
name|Builder
name|nullValue
parameter_list|(
name|short
name|nullValue
parameter_list|)
block|{
name|this
operator|.
name|nullValue
operator|=
name|nullValue
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|ShortFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|fieldType
operator|.
name|setOmitNorms
argument_list|(
name|fieldType
operator|.
name|omitNorms
argument_list|()
operator|&&
name|boost
operator|==
literal|1.0f
argument_list|)
expr_stmt|;
name|ShortFieldMapper
name|fieldMapper
init|=
operator|new
name|ShortFieldMapper
argument_list|(
name|buildNames
argument_list|(
name|context
argument_list|)
argument_list|,
name|precisionStep
argument_list|,
name|boost
argument_list|,
name|fieldType
argument_list|,
name|docValues
argument_list|,
name|nullValue
argument_list|,
name|ignoreMalformed
argument_list|(
name|context
argument_list|)
argument_list|,
name|postingsProvider
argument_list|,
name|docValuesProvider
argument_list|,
name|similarity
argument_list|,
name|normsLoading
argument_list|,
name|fieldDataSettings
argument_list|,
name|context
operator|.
name|indexSettings
argument_list|()
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
name|ShortFieldMapper
operator|.
name|Builder
name|builder
init|=
name|shortField
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
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|node
operator|.
name|entrySet
argument_list|()
control|)
block|{
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
name|builder
operator|.
name|nullValue
argument_list|(
name|nodeShortValue
argument_list|(
name|propNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
block|}
DECL|field|nullValue
specifier|private
name|Short
name|nullValue
decl_stmt|;
DECL|field|nullValueAsString
specifier|private
name|String
name|nullValueAsString
decl_stmt|;
DECL|method|ShortFieldMapper
specifier|protected
name|ShortFieldMapper
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
name|Boolean
name|docValues
parameter_list|,
name|Short
name|nullValue
parameter_list|,
name|Explicit
argument_list|<
name|Boolean
argument_list|>
name|ignoreMalformed
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
name|Loading
name|normsLoading
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
name|super
argument_list|(
name|names
argument_list|,
name|precisionStep
argument_list|,
name|boost
argument_list|,
name|fieldType
argument_list|,
name|docValues
argument_list|,
name|ignoreMalformed
argument_list|,
operator|new
name|NamedAnalyzer
argument_list|(
literal|"_short/"
operator|+
name|precisionStep
argument_list|,
operator|new
name|NumericIntegerAnalyzer
argument_list|(
name|precisionStep
argument_list|)
argument_list|)
argument_list|,
operator|new
name|NamedAnalyzer
argument_list|(
literal|"_short/max"
argument_list|,
operator|new
name|NumericIntegerAnalyzer
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
argument_list|,
name|postingsProvider
argument_list|,
name|docValuesProvider
argument_list|,
name|similarity
argument_list|,
name|normsLoading
argument_list|,
name|fieldDataSettings
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|nullValue
operator|=
name|nullValue
expr_stmt|;
name|this
operator|.
name|nullValueAsString
operator|=
name|nullValue
operator|==
literal|null
condition|?
literal|null
else|:
name|nullValue
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|defaultFieldType
specifier|public
name|FieldType
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
literal|"short"
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
annotation|@
name|Override
DECL|method|value
specifier|public
name|Short
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
name|shortValue
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
name|bytesToShort
argument_list|(
operator|(
name|BytesRef
operator|)
name|value
argument_list|)
return|;
block|}
return|return
name|Short
operator|.
name|parseShort
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
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|parseValue
argument_list|(
name|value
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
comment|// 0 because of exact match
return|return
name|bytesRef
return|;
block|}
DECL|method|parseValue
specifier|private
name|short
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
name|shortValue
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
name|Short
operator|.
name|parseShort
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
name|Short
operator|.
name|parseShort
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|parseValueAsInt
specifier|private
name|int
name|parseValueAsInt
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|parseValue
argument_list|(
name|value
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
name|short
name|iValue
init|=
name|Short
operator|.
name|parseShort
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|short
name|iSim
init|=
name|fuzziness
operator|.
name|asShort
argument_list|()
decl_stmt|;
return|return
name|NumericRangeQuery
operator|.
name|newIntRange
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|precisionStep
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
name|int
name|iValue
init|=
name|parseValueAsInt
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
name|NumericRangeQuery
operator|.
name|newIntRange
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|precisionStep
argument_list|,
name|iValue
argument_list|,
name|iValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
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
name|newIntRange
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|precisionStep
argument_list|,
name|lowerTerm
operator|==
literal|null
condition|?
literal|null
else|:
name|parseValueAsInt
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
name|parseValueAsInt
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
name|int
name|iValue
init|=
name|parseValueAsInt
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
name|NumericRangeFilter
operator|.
name|newIntRange
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|precisionStep
argument_list|,
name|iValue
argument_list|,
name|iValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|rangeFilter
specifier|public
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
block|{
return|return
name|NumericRangeFilter
operator|.
name|newIntRange
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|precisionStep
argument_list|,
name|lowerTerm
operator|==
literal|null
condition|?
literal|null
else|:
name|parseValueAsInt
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
name|parseValueAsInt
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
DECL|method|rangeFilter
specifier|public
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
block|{
return|return
name|NumericRangeFieldDataFilter
operator|.
name|newShortRange
argument_list|(
operator|(
name|IndexNumericFieldData
operator|)
name|fieldData
operator|.
name|getForField
argument_list|(
name|this
argument_list|)
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
DECL|method|nullValueFilter
specifier|public
name|Filter
name|nullValueFilter
parameter_list|()
block|{
if|if
condition|(
name|nullValue
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|NumericRangeFilter
operator|.
name|newIntRange
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|precisionStep
argument_list|,
name|nullValue
operator|.
name|intValue
argument_list|()
argument_list|,
name|nullValue
operator|.
name|intValue
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
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
name|short
name|value
decl_stmt|;
name|float
name|boost
init|=
name|this
operator|.
name|boost
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
name|nullValue
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|value
operator|=
name|nullValue
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
name|nullValue
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|value
operator|=
name|nullValue
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|Short
operator|.
name|parseShort
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
name|shortValue
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
name|names
operator|.
name|fullName
argument_list|()
argument_list|,
name|Short
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
name|nullValue
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|value
operator|=
name|nullValue
expr_stmt|;
if|if
condition|(
name|nullValueAsString
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
name|names
operator|.
name|fullName
argument_list|()
argument_list|,
name|nullValueAsString
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
name|Short
name|objValue
init|=
name|nullValue
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
name|shortValue
argument_list|()
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
name|ElasticsearchIllegalArgumentException
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
name|shortValue
argument_list|()
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
name|names
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
operator|.
name|indexed
argument_list|()
operator|||
name|fieldType
operator|.
name|stored
argument_list|()
condition|)
block|{
name|CustomShortNumericField
name|field
init|=
operator|new
name|CustomShortNumericField
argument_list|(
name|this
argument_list|,
name|value
argument_list|,
name|fieldType
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
name|hasDocValues
argument_list|()
condition|)
block|{
name|addDocValue
argument_list|(
name|context
argument_list|,
name|value
argument_list|)
expr_stmt|;
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
name|this
operator|.
name|nullValue
operator|=
operator|(
operator|(
name|ShortFieldMapper
operator|)
name|mergeWith
operator|)
operator|.
name|nullValue
expr_stmt|;
name|this
operator|.
name|nullValueAsString
operator|=
operator|(
operator|(
name|ShortFieldMapper
operator|)
name|mergeWith
operator|)
operator|.
name|nullValueAsString
expr_stmt|;
block|}
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
name|precisionStep
operator|!=
name|Defaults
operator|.
name|PRECISION_STEP
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"precision_step"
argument_list|,
name|precisionStep
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|nullValue
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
name|nullValue
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
DECL|class|CustomShortNumericField
specifier|public
specifier|static
class|class
name|CustomShortNumericField
extends|extends
name|CustomNumericField
block|{
DECL|field|number
specifier|private
specifier|final
name|short
name|number
decl_stmt|;
DECL|field|mapper
specifier|private
specifier|final
name|NumberFieldMapper
name|mapper
decl_stmt|;
DECL|method|CustomShortNumericField
specifier|public
name|CustomShortNumericField
parameter_list|(
name|NumberFieldMapper
name|mapper
parameter_list|,
name|short
name|number
parameter_list|,
name|FieldType
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
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
condition|)
block|{
return|return
name|mapper
operator|.
name|popCachedStream
argument_list|()
operator|.
name|setIntValue
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
name|Short
operator|.
name|toString
argument_list|(
name|number
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

