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
name|elasticsearch
operator|.
name|ElasticSearchParseException
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
name|common
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
name|mapper
operator|.
name|ContentPath
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TypeParsers
specifier|public
class|class
name|TypeParsers
block|{
DECL|field|INDEX_OPTIONS_DOCS
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_OPTIONS_DOCS
init|=
literal|"docs"
decl_stmt|;
DECL|field|INDEX_OPTIONS_FREQS
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_OPTIONS_FREQS
init|=
literal|"freqs"
decl_stmt|;
DECL|field|INDEX_OPTIONS_POSITIONS
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_OPTIONS_POSITIONS
init|=
literal|"positions"
decl_stmt|;
DECL|method|parseNumberField
specifier|public
specifier|static
name|void
name|parseNumberField
parameter_list|(
name|NumberFieldMapper
operator|.
name|Builder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|numberNode
parameter_list|,
name|Mapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|parseField
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|numberNode
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
name|numberNode
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
literal|"precision_step"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|precisionStep
argument_list|(
name|nodeIntegerValue
argument_list|(
name|propNode
argument_list|)
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
literal|"fuzzy_factor"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|fuzzyFactor
argument_list|(
name|propNode
operator|.
name|toString
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
literal|"ignore_malformed"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|ignoreMalformed
argument_list|(
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
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
literal|"omit_norms"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|omitNorms
argument_list|(
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|parseField
specifier|public
specifier|static
name|void
name|parseField
parameter_list|(
name|AbstractFieldMapper
operator|.
name|Builder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldNode
parameter_list|,
name|Mapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
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
name|fieldNode
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
literal|"index_name"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|indexName
argument_list|(
name|propNode
operator|.
name|toString
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
literal|"store"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|store
argument_list|(
name|parseStore
argument_list|(
name|name
argument_list|,
name|propNode
operator|.
name|toString
argument_list|()
argument_list|)
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
literal|"index"
argument_list|)
condition|)
block|{
name|parseIndex
argument_list|(
name|name
argument_list|,
name|propNode
operator|.
name|toString
argument_list|()
argument_list|,
name|builder
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
literal|"tokenized"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|tokenized
argument_list|(
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
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
literal|"term_vector"
argument_list|)
condition|)
block|{
name|parseTermVector
argument_list|(
name|name
argument_list|,
name|propNode
operator|.
name|toString
argument_list|()
argument_list|,
name|builder
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
literal|"boost"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|boost
argument_list|(
name|nodeFloatValue
argument_list|(
name|propNode
argument_list|)
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
literal|"store_term_vectors"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|storeTermVectors
argument_list|(
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
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
literal|"store_term_vector_offsets"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|storeTermVectorOffsets
argument_list|(
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
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
literal|"store_term_vector_positions"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|storeTermVectorPositions
argument_list|(
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
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
literal|"store_term_vector_payloads"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|storeTermVectorPayloads
argument_list|(
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
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
literal|"omit_norms"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|omitNorms
argument_list|(
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
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
literal|"omit_term_freq_and_positions"
argument_list|)
condition|)
block|{
comment|// deprecated option for BW compat
name|builder
operator|.
name|indexOptions
argument_list|(
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
condition|?
name|IndexOptions
operator|.
name|DOCS_ONLY
else|:
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
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
literal|"index_options"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|indexOptions
argument_list|(
name|nodeIndexOptionValue
argument_list|(
name|propNode
argument_list|)
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
literal|"analyzer"
argument_list|)
condition|)
block|{
name|NamedAnalyzer
name|analyzer
init|=
name|parserContext
operator|.
name|analysisService
argument_list|()
operator|.
name|analyzer
argument_list|(
name|propNode
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Analyzer ["
operator|+
name|propNode
operator|.
name|toString
argument_list|()
operator|+
literal|"] not found for field ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|builder
operator|.
name|indexAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|builder
operator|.
name|searchAnalyzer
argument_list|(
name|analyzer
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
literal|"index_analyzer"
argument_list|)
condition|)
block|{
name|NamedAnalyzer
name|analyzer
init|=
name|parserContext
operator|.
name|analysisService
argument_list|()
operator|.
name|analyzer
argument_list|(
name|propNode
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Analyzer ["
operator|+
name|propNode
operator|.
name|toString
argument_list|()
operator|+
literal|"] not found for field ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|builder
operator|.
name|indexAnalyzer
argument_list|(
name|analyzer
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
literal|"search_analyzer"
argument_list|)
condition|)
block|{
name|NamedAnalyzer
name|analyzer
init|=
name|parserContext
operator|.
name|analysisService
argument_list|()
operator|.
name|analyzer
argument_list|(
name|propNode
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Analyzer ["
operator|+
name|propNode
operator|.
name|toString
argument_list|()
operator|+
literal|"] not found for field ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|builder
operator|.
name|searchAnalyzer
argument_list|(
name|analyzer
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
literal|"include_in_all"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|includeInAll
argument_list|(
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
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
literal|"postings_format"
argument_list|)
condition|)
block|{
name|String
name|postingFormatName
init|=
name|propNode
operator|.
name|toString
argument_list|()
decl_stmt|;
name|builder
operator|.
name|postingsFormat
argument_list|(
name|parserContext
operator|.
name|postingFormatService
argument_list|()
operator|.
name|get
argument_list|(
name|postingFormatName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// LUCENE 4 UPGRADE: when ew move into feature mode, we need to support DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
DECL|method|nodeIndexOptionValue
specifier|private
specifier|static
name|IndexOptions
name|nodeIndexOptionValue
parameter_list|(
specifier|final
name|Object
name|propNode
parameter_list|)
block|{
specifier|final
name|String
name|value
init|=
name|propNode
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|INDEX_OPTIONS_POSITIONS
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
return|;
block|}
elseif|else
if|if
condition|(
name|INDEX_OPTIONS_FREQS
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
return|;
block|}
elseif|else
if|if
condition|(
name|INDEX_OPTIONS_DOCS
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|IndexOptions
operator|.
name|DOCS_ONLY
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchParseException
argument_list|(
literal|"Failed to parse index option ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|parseDateTimeFormatter
specifier|public
specifier|static
name|FormatDateTimeFormatter
name|parseDateTimeFormatter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Object
name|node
parameter_list|)
block|{
return|return
name|Joda
operator|.
name|forPattern
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|parseTermVector
specifier|public
specifier|static
name|void
name|parseTermVector
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|termVector
parameter_list|,
name|AbstractFieldMapper
operator|.
name|Builder
name|builder
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|termVector
operator|=
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|termVector
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"no"
operator|.
name|equals
argument_list|(
name|termVector
argument_list|)
condition|)
block|{
name|builder
operator|.
name|storeTermVectors
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"yes"
operator|.
name|equals
argument_list|(
name|termVector
argument_list|)
condition|)
block|{
name|builder
operator|.
name|storeTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"with_offsets"
operator|.
name|equals
argument_list|(
name|termVector
argument_list|)
condition|)
block|{
name|builder
operator|.
name|storeTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"with_positions"
operator|.
name|equals
argument_list|(
name|termVector
argument_list|)
condition|)
block|{
name|builder
operator|.
name|storeTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"with_positions_offsets"
operator|.
name|equals
argument_list|(
name|termVector
argument_list|)
condition|)
block|{
name|builder
operator|.
name|storeTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|storeTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Wrong value for termVector ["
operator|+
name|termVector
operator|+
literal|"] for field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|parseIndex
specifier|public
specifier|static
name|void
name|parseIndex
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|index
parameter_list|,
name|AbstractFieldMapper
operator|.
name|Builder
name|builder
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|index
operator|=
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|index
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"no"
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|builder
operator|.
name|index
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"not_analyzed"
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|builder
operator|.
name|index
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|tokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"analyzed"
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|builder
operator|.
name|index
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|tokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Wrong value for index ["
operator|+
name|index
operator|+
literal|"] for field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|parseStore
specifier|public
specifier|static
name|boolean
name|parseStore
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|store
parameter_list|)
throws|throws
name|MapperParsingException
block|{
if|if
condition|(
literal|"no"
operator|.
name|equals
argument_list|(
name|store
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
literal|"yes"
operator|.
name|equals
argument_list|(
name|store
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|nodeBooleanValue
argument_list|(
name|store
argument_list|)
return|;
block|}
block|}
DECL|method|parsePathType
specifier|public
specifier|static
name|ContentPath
operator|.
name|Type
name|parsePathType
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|path
operator|=
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"just_name"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
name|ContentPath
operator|.
name|Type
operator|.
name|JUST_NAME
return|;
block|}
elseif|else
if|if
condition|(
literal|"full"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
name|ContentPath
operator|.
name|Type
operator|.
name|FULL
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Wrong value for pathType ["
operator|+
name|path
operator|+
literal|"] for object ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

