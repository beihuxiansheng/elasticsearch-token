begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|json
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
name|Fieldable
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
name|NumericUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonToken
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|node
operator|.
name|ObjectNode
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
name|NumericDateAnalyzer
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
name|util
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
name|util
operator|.
name|joda
operator|.
name|FormatDateTimeFormatter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|joda
operator|.
name|Joda
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|json
operator|.
name|JsonBuilder
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
name|Map
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
name|json
operator|.
name|JsonMapperBuilders
operator|.
name|*
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
name|json
operator|.
name|JsonTypeParsers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|JsonDateFieldMapper
specifier|public
class|class
name|JsonDateFieldMapper
extends|extends
name|JsonNumberFieldMapper
argument_list|<
name|Long
argument_list|>
block|{
DECL|field|JSON_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|JSON_TYPE
init|=
literal|"date"
decl_stmt|;
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|JsonNumberFieldMapper
operator|.
name|Defaults
block|{
DECL|field|DATE_TIME_FORMATTER
specifier|public
specifier|static
specifier|final
name|FormatDateTimeFormatter
name|DATE_TIME_FORMATTER
init|=
name|Joda
operator|.
name|forPattern
argument_list|(
literal|"dateOptionalTime"
argument_list|)
decl_stmt|;
DECL|field|NULL_VALUE
specifier|public
specifier|static
specifier|final
name|String
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
name|JsonNumberFieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|JsonDateFieldMapper
argument_list|>
block|{
DECL|field|nullValue
specifier|protected
name|String
name|nullValue
init|=
name|Defaults
operator|.
name|NULL_VALUE
decl_stmt|;
DECL|field|dateTimeFormatter
specifier|protected
name|FormatDateTimeFormatter
name|dateTimeFormatter
init|=
name|Defaults
operator|.
name|DATE_TIME_FORMATTER
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
name|String
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
DECL|method|dateTimeFormatter
specifier|public
name|Builder
name|dateTimeFormatter
parameter_list|(
name|FormatDateTimeFormatter
name|dateTimeFormatter
parameter_list|)
block|{
name|this
operator|.
name|dateTimeFormatter
operator|=
name|dateTimeFormatter
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
annotation|@
name|Override
specifier|public
name|JsonDateFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|JsonDateFieldMapper
name|fieldMapper
init|=
operator|new
name|JsonDateFieldMapper
argument_list|(
name|buildNames
argument_list|(
name|context
argument_list|)
argument_list|,
name|dateTimeFormatter
argument_list|,
name|precisionStep
argument_list|,
name|index
argument_list|,
name|store
argument_list|,
name|boost
argument_list|,
name|omitNorms
argument_list|,
name|omitTermFreqAndPositions
argument_list|,
name|nullValue
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
name|JsonTypeParser
block|{
DECL|method|parse
annotation|@
name|Override
specifier|public
name|JsonMapper
operator|.
name|Builder
name|parse
parameter_list|(
name|String
name|name
parameter_list|,
name|JsonNode
name|node
parameter_list|,
name|ParserContext
name|parserContext
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|ObjectNode
name|dateNode
init|=
operator|(
name|ObjectNode
operator|)
name|node
decl_stmt|;
name|JsonDateFieldMapper
operator|.
name|Builder
name|builder
init|=
name|dateField
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
name|dateNode
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
name|JsonNode
argument_list|>
argument_list|>
name|propsIt
init|=
name|dateNode
operator|.
name|getFields
argument_list|()
init|;
name|propsIt
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
name|JsonNode
argument_list|>
name|entry
init|=
name|propsIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|propName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|JsonNode
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
literal|"nullValue"
argument_list|)
operator|||
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
name|propNode
operator|.
name|getValueAsText
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|propName
operator|.
name|equals
argument_list|(
literal|"format"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|dateTimeFormatter
argument_list|(
name|parseDateTimeFormatter
argument_list|(
name|propName
argument_list|,
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
DECL|field|dateTimeFormatter
specifier|private
specifier|final
name|FormatDateTimeFormatter
name|dateTimeFormatter
decl_stmt|;
DECL|field|nullValue
specifier|private
specifier|final
name|String
name|nullValue
decl_stmt|;
DECL|method|JsonDateFieldMapper
specifier|protected
name|JsonDateFieldMapper
parameter_list|(
name|Names
name|names
parameter_list|,
name|FormatDateTimeFormatter
name|dateTimeFormatter
parameter_list|,
name|int
name|precisionStep
parameter_list|,
name|Field
operator|.
name|Index
name|index
parameter_list|,
name|Field
operator|.
name|Store
name|store
parameter_list|,
name|float
name|boost
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|omitTermFreqAndPositions
parameter_list|,
name|String
name|nullValue
parameter_list|)
block|{
name|super
argument_list|(
name|names
argument_list|,
name|precisionStep
argument_list|,
name|index
argument_list|,
name|store
argument_list|,
name|boost
argument_list|,
name|omitNorms
argument_list|,
name|omitTermFreqAndPositions
argument_list|,
operator|new
name|NamedAnalyzer
argument_list|(
literal|"_date/"
operator|+
name|precisionStep
argument_list|,
operator|new
name|NumericDateAnalyzer
argument_list|(
name|precisionStep
argument_list|,
name|dateTimeFormatter
operator|.
name|parser
argument_list|()
argument_list|)
argument_list|)
argument_list|,
operator|new
name|NamedAnalyzer
argument_list|(
literal|"_date/max"
argument_list|,
operator|new
name|NumericDateAnalyzer
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|dateTimeFormatter
operator|.
name|parser
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|dateTimeFormatter
operator|=
name|dateTimeFormatter
expr_stmt|;
name|this
operator|.
name|nullValue
operator|=
name|nullValue
expr_stmt|;
block|}
DECL|method|maxPrecisionStep
annotation|@
name|Override
specifier|protected
name|int
name|maxPrecisionStep
parameter_list|()
block|{
return|return
literal|64
return|;
block|}
DECL|method|value
annotation|@
name|Override
specifier|public
name|Long
name|value
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
name|byte
index|[]
name|value
init|=
name|field
operator|.
name|getBinaryValue
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
return|return
name|Numbers
operator|.
name|bytesToLong
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/**      * Dates should return as a string, delegates to {@link #valueAsString(org.apache.lucene.document.Fieldable)}.      */
DECL|method|valueForSearch
annotation|@
name|Override
specifier|public
name|Object
name|valueForSearch
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
return|return
name|valueAsString
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|valueForSearch
annotation|@
name|Override
specifier|public
name|Object
name|valueForSearch
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|dateTimeFormatter
operator|.
name|printer
argument_list|()
operator|.
name|print
argument_list|(
operator|(
name|Long
operator|)
name|value
argument_list|)
return|;
block|}
DECL|method|valueAsString
annotation|@
name|Override
specifier|public
name|String
name|valueAsString
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
name|Long
name|value
init|=
name|value
argument_list|(
name|field
argument_list|)
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
return|return
name|dateTimeFormatter
operator|.
name|printer
argument_list|()
operator|.
name|print
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|indexedValue
annotation|@
name|Override
specifier|public
name|String
name|indexedValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|dateTimeFormatter
operator|.
name|parser
argument_list|()
operator|.
name|parseMillis
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
DECL|method|indexedValue
annotation|@
name|Override
specifier|public
name|String
name|indexedValue
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|valueFromTerm
annotation|@
name|Override
specifier|public
name|Object
name|valueFromTerm
parameter_list|(
name|String
name|term
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|term
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|-
name|NumericUtils
operator|.
name|SHIFT_START_LONG
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|0
operator|&&
name|shift
operator|<=
literal|63
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|valueFromString
annotation|@
name|Override
specifier|public
name|Object
name|valueFromString
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
name|dateTimeFormatter
operator|.
name|parser
argument_list|()
operator|.
name|parseMillis
argument_list|(
name|text
argument_list|)
return|;
block|}
DECL|method|rangeQuery
annotation|@
name|Override
specifier|public
name|Query
name|rangeQuery
parameter_list|(
name|String
name|lowerTerm
parameter_list|,
name|String
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
return|return
name|NumericRangeQuery
operator|.
name|newLongRange
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
name|dateTimeFormatter
operator|.
name|parser
argument_list|()
operator|.
name|parseMillis
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
name|dateTimeFormatter
operator|.
name|parser
argument_list|()
operator|.
name|parseMillis
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
DECL|method|rangeFilter
annotation|@
name|Override
specifier|public
name|Filter
name|rangeFilter
parameter_list|(
name|String
name|lowerTerm
parameter_list|,
name|String
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
return|return
name|NumericRangeFilter
operator|.
name|newLongRange
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
name|dateTimeFormatter
operator|.
name|parser
argument_list|()
operator|.
name|parseMillis
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
name|dateTimeFormatter
operator|.
name|parser
argument_list|()
operator|.
name|parseMillis
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
DECL|method|parseCreateField
annotation|@
name|Override
specifier|protected
name|Field
name|parseCreateField
parameter_list|(
name|JsonParseContext
name|jsonContext
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|dateAsString
decl_stmt|;
if|if
condition|(
name|jsonContext
operator|.
name|externalValueSet
argument_list|()
condition|)
block|{
name|dateAsString
operator|=
operator|(
name|String
operator|)
name|jsonContext
operator|.
name|externalValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|dateAsString
operator|==
literal|null
condition|)
block|{
name|dateAsString
operator|=
name|nullValue
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|jsonContext
operator|.
name|jp
argument_list|()
operator|.
name|getCurrentToken
argument_list|()
operator|==
name|JsonToken
operator|.
name|VALUE_NULL
condition|)
block|{
name|dateAsString
operator|=
name|nullValue
expr_stmt|;
block|}
else|else
block|{
name|dateAsString
operator|=
name|jsonContext
operator|.
name|jp
argument_list|()
operator|.
name|getText
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|dateAsString
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
name|includeInAll
operator|==
literal|null
operator|||
name|includeInAll
condition|)
block|{
name|jsonContext
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
name|dateAsString
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
name|long
name|value
init|=
name|dateTimeFormatter
operator|.
name|parser
argument_list|()
operator|.
name|parseMillis
argument_list|(
name|dateAsString
argument_list|)
decl_stmt|;
name|Field
name|field
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|stored
argument_list|()
condition|)
block|{
name|field
operator|=
operator|new
name|Field
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|Numbers
operator|.
name|longToBytes
argument_list|(
name|value
argument_list|)
argument_list|,
name|store
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexed
argument_list|()
condition|)
block|{
name|field
operator|.
name|setTokenStream
argument_list|(
name|popCachedStream
argument_list|(
name|precisionStep
argument_list|)
operator|.
name|setLongValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|indexed
argument_list|()
condition|)
block|{
name|field
operator|=
operator|new
name|Field
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|popCachedStream
argument_list|(
name|precisionStep
argument_list|)
operator|.
name|setLongValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|field
return|;
block|}
DECL|method|sortType
annotation|@
name|Override
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|LONG
return|;
block|}
DECL|method|jsonType
annotation|@
name|Override
specifier|protected
name|String
name|jsonType
parameter_list|()
block|{
return|return
name|JSON_TYPE
return|;
block|}
DECL|method|doJsonBody
annotation|@
name|Override
specifier|protected
name|void
name|doJsonBody
parameter_list|(
name|JsonBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|doJsonBody
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
name|dateTimeFormatter
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|nullValue
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"nullValue"
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
literal|"includeInAll"
argument_list|,
name|includeInAll
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

