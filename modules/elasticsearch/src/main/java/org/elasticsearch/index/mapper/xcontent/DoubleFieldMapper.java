begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|xcontent
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
name|NumericDoubleAnalyzer
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
name|xcontent
operator|.
name|XContentMapperBuilders
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
name|xcontent
operator|.
name|XContentTypeParsers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|DoubleFieldMapper
specifier|public
class|class
name|DoubleFieldMapper
extends|extends
name|NumberFieldMapper
argument_list|<
name|Double
argument_list|>
block|{
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"double"
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
DECL|field|NULL_VALUE
specifier|public
specifier|static
specifier|final
name|Double
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
name|DoubleFieldMapper
argument_list|>
block|{
DECL|field|nullValue
specifier|protected
name|Double
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
name|double
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
name|DoubleFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|DoubleFieldMapper
name|fieldMapper
init|=
operator|new
name|DoubleFieldMapper
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
name|XContentMapper
operator|.
name|TypeParser
block|{
DECL|method|parse
annotation|@
name|Override
specifier|public
name|XContentMapper
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
name|DoubleFieldMapper
operator|.
name|Builder
name|builder
init|=
name|doubleField
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
name|entry
operator|.
name|getKey
argument_list|()
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
name|nodeDoubleValue
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
name|Double
name|nullValue
decl_stmt|;
DECL|field|nullValueAsString
specifier|private
name|String
name|nullValueAsString
decl_stmt|;
DECL|method|DoubleFieldMapper
specifier|protected
name|DoubleFieldMapper
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
name|Double
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
literal|"_double/"
operator|+
name|precisionStep
argument_list|,
operator|new
name|NumericDoubleAnalyzer
argument_list|(
name|precisionStep
argument_list|)
argument_list|)
argument_list|,
operator|new
name|NamedAnalyzer
argument_list|(
literal|"_double/max"
argument_list|,
operator|new
name|NumericDoubleAnalyzer
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
literal|64
return|;
block|}
DECL|method|value
annotation|@
name|Override
specifier|public
name|Double
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
name|bytesToDouble
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|valueFromString
annotation|@
name|Override
specifier|public
name|Double
name|valueFromString
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Double
operator|.
name|valueOf
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
name|doubleToPrefixCoded
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
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
name|newDoubleRange
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
name|Double
operator|.
name|parseDouble
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
name|Double
operator|.
name|parseDouble
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
name|newDoubleRange
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
name|Double
operator|.
name|parseDouble
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
name|Double
operator|.
name|parseDouble
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
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|double
name|value
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
name|doubleValue
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
name|Double
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
name|context
operator|.
name|parser
argument_list|()
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
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
else|else
block|{
name|value
operator|=
name|context
operator|.
name|parser
argument_list|()
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|includeInAll
operator|==
literal|null
operator|||
name|includeInAll
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
name|context
operator|.
name|parser
argument_list|()
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
name|doubleToBytes
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
name|setDoubleValue
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
name|setDoubleValue
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
DECL|method|fieldDataType
annotation|@
name|Override
specifier|public
name|FieldDataType
name|fieldDataType
parameter_list|()
block|{
return|return
name|FieldDataType
operator|.
name|DefaultTypes
operator|.
name|DOUBLE
return|;
block|}
DECL|method|contentType
annotation|@
name|Override
specifier|protected
name|String
name|contentType
parameter_list|()
block|{
return|return
name|CONTENT_TYPE
return|;
block|}
DECL|method|merge
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|XContentMapper
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
name|DoubleFieldMapper
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
name|DoubleFieldMapper
operator|)
name|mergeWith
operator|)
operator|.
name|nullValueAsString
expr_stmt|;
block|}
block|}
DECL|method|doXContentBody
annotation|@
name|Override
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
name|index
operator|!=
name|Defaults
operator|.
name|INDEX
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
name|index
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|store
operator|!=
name|Defaults
operator|.
name|STORE
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
name|store
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|termVector
operator|!=
name|Defaults
operator|.
name|TERM_VECTOR
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"term_vector"
argument_list|,
name|termVector
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|omitNorms
operator|!=
name|Defaults
operator|.
name|OMIT_NORMS
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"omit_norms"
argument_list|,
name|omitNorms
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|omitTermFreqAndPositions
operator|!=
name|Defaults
operator|.
name|OMIT_TERM_FREQ_AND_POSITIONS
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"omit_term_freq_and_positions"
argument_list|,
name|omitTermFreqAndPositions
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
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

