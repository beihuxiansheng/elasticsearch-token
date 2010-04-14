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
name|Strings
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|json
operator|.
name|JacksonNodes
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|JsonFloatFieldMapper
specifier|public
class|class
name|JsonFloatFieldMapper
extends|extends
name|JsonNumberFieldMapper
argument_list|<
name|Float
argument_list|>
block|{
DECL|field|JSON_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|JSON_TYPE
init|=
literal|"float"
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
DECL|field|NULL_VALUE
specifier|public
specifier|static
specifier|final
name|Float
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
name|JsonFloatFieldMapper
argument_list|>
block|{
DECL|field|nullValue
specifier|protected
name|Float
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
name|float
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
DECL|method|build
annotation|@
name|Override
specifier|public
name|JsonFloatFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|JsonFloatFieldMapper
name|fieldMapper
init|=
operator|new
name|JsonFloatFieldMapper
argument_list|(
name|buildNames
argument_list|(
name|context
argument_list|)
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
name|floatNode
init|=
operator|(
name|ObjectNode
operator|)
name|node
decl_stmt|;
name|JsonFloatFieldMapper
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
name|floatNode
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
name|floatNode
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
literal|"null_value"
argument_list|)
condition|)
block|{
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
block|}
block|}
return|return
name|builder
return|;
block|}
block|}
DECL|field|nullValue
specifier|private
specifier|final
name|Float
name|nullValue
decl_stmt|;
DECL|field|nullValueAsString
specifier|private
specifier|final
name|String
name|nullValueAsString
decl_stmt|;
DECL|method|JsonFloatFieldMapper
specifier|protected
name|JsonFloatFieldMapper
parameter_list|(
name|Names
name|names
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
name|Float
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
literal|"_float/"
operator|+
name|precisionStep
argument_list|,
operator|new
name|NumericFloatAnalyzer
argument_list|(
name|precisionStep
argument_list|)
argument_list|)
argument_list|,
operator|new
name|NamedAnalyzer
argument_list|(
literal|"_float/max"
argument_list|,
operator|new
name|NumericFloatAnalyzer
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
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
DECL|method|maxPrecisionStep
annotation|@
name|Override
specifier|protected
name|int
name|maxPrecisionStep
parameter_list|()
block|{
return|return
literal|32
return|;
block|}
DECL|method|value
annotation|@
name|Override
specifier|public
name|Float
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
name|bytesToFloat
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
name|indexedValue
argument_list|(
name|Float
operator|.
name|parseFloat
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
name|Float
name|value
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|floatToPrefixCoded
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
name|SHIFT_START_INT
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|0
operator|&&
name|shift
operator|<=
literal|31
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|NumericUtils
operator|.
name|prefixCodedToFloat
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
name|Float
operator|.
name|parseFloat
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
name|newFloatRange
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
name|Float
operator|.
name|parseFloat
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
name|Float
operator|.
name|parseFloat
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
name|newFloatRange
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
name|Float
operator|.
name|parseFloat
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
name|Float
operator|.
name|parseFloat
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
name|float
name|value
decl_stmt|;
if|if
condition|(
name|jsonContext
operator|.
name|externalValueSet
argument_list|()
condition|)
block|{
name|Object
name|externalValue
init|=
name|jsonContext
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
return|return
literal|null
return|;
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
name|includeInAll
operator|==
literal|null
operator|||
name|includeInAll
operator|)
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
name|nullValueAsString
argument_list|,
name|boost
argument_list|)
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
name|VALUE_STRING
condition|)
block|{
name|value
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|jsonContext
operator|.
name|jp
argument_list|()
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|jsonContext
operator|.
name|jp
argument_list|()
operator|.
name|getFloatValue
argument_list|()
expr_stmt|;
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
name|jsonContext
operator|.
name|jp
argument_list|()
operator|.
name|getText
argument_list|()
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|floatToBytes
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
name|setFloatValue
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
name|setFloatValue
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
name|FLOAT
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
block|}
block|}
end_class

end_unit

