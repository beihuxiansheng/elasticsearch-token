begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|internal
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
name|index
operator|.
name|Term
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
name|search
operator|.
name|TermQuery
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
name|lucene
operator|.
name|Lucene
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
name|lucene
operator|.
name|all
operator|.
name|AllField
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
name|lucene
operator|.
name|all
operator|.
name|AllTermQuery
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
name|core
operator|.
name|AbstractFieldMapper
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
name|nodeBooleanValue
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
name|all
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
name|parseField
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AllFieldMapper
specifier|public
class|class
name|AllFieldMapper
extends|extends
name|AbstractFieldMapper
argument_list|<
name|Void
argument_list|>
implements|implements
name|InternalMapper
implements|,
name|RootMapper
block|{
DECL|interface|IncludeInAll
specifier|public
interface|interface
name|IncludeInAll
extends|extends
name|Mapper
block|{
DECL|method|includeInAll
name|void
name|includeInAll
parameter_list|(
name|Boolean
name|includeInAll
parameter_list|)
function_decl|;
DECL|method|includeInAllIfNotSet
name|void
name|includeInAllIfNotSet
parameter_list|(
name|Boolean
name|includeInAll
parameter_list|)
function_decl|;
block|}
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"_all"
decl_stmt|;
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"_all"
decl_stmt|;
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
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|AllFieldMapper
operator|.
name|NAME
decl_stmt|;
DECL|field|INDEX_NAME
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_NAME
init|=
name|AllFieldMapper
operator|.
name|NAME
decl_stmt|;
DECL|field|ENABLED
specifier|public
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
literal|true
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
argument_list|()
decl_stmt|;
static|static
block|{
name|FIELD_TYPE
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|AbstractFieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|AllFieldMapper
argument_list|>
block|{
DECL|field|enabled
specifier|private
name|boolean
name|enabled
init|=
name|Defaults
operator|.
name|ENABLED
decl_stmt|;
comment|// an internal flag, automatically set if we encounter boosting
DECL|field|autoBoost
name|boolean
name|autoBoost
init|=
literal|false
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|()
block|{
name|super
argument_list|(
name|Defaults
operator|.
name|NAME
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
name|indexName
operator|=
name|Defaults
operator|.
name|INDEX_NAME
expr_stmt|;
block|}
DECL|method|enabled
specifier|public
name|Builder
name|enabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|AllFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
comment|// In case the mapping overrides these
name|fieldType
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|AllFieldMapper
argument_list|(
name|name
argument_list|,
name|fieldType
argument_list|,
name|indexAnalyzer
argument_list|,
name|searchAnalyzer
argument_list|,
name|enabled
argument_list|,
name|autoBoost
argument_list|,
name|provider
argument_list|,
name|similarity
argument_list|)
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
name|AllFieldMapper
operator|.
name|Builder
name|builder
init|=
name|all
argument_list|()
decl_stmt|;
name|parseField
argument_list|(
name|builder
argument_list|,
name|builder
operator|.
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
name|fieldName
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
name|fieldNode
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"enabled"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|enabled
argument_list|(
name|nodeBooleanValue
argument_list|(
name|fieldNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"auto_boost"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|autoBoost
operator|=
name|nodeBooleanValue
argument_list|(
name|fieldNode
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
block|}
DECL|field|enabled
specifier|private
name|boolean
name|enabled
decl_stmt|;
comment|// The autoBoost flag is automatically set based on indexed docs on the mappings
comment|// if a doc is indexed with a specific boost value and part of _all, it is automatically
comment|// set to true. This allows to optimize (automatically, which we like) for the common case
comment|// where fields don't usually have boost associated with them, and we don't need to use the
comment|// special SpanTermQuery to look at payloads
DECL|field|autoBoost
specifier|private
specifier|volatile
name|boolean
name|autoBoost
decl_stmt|;
DECL|method|AllFieldMapper
specifier|public
name|AllFieldMapper
parameter_list|()
block|{
name|this
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|,
operator|new
name|FieldType
argument_list|(
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Defaults
operator|.
name|ENABLED
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|AllFieldMapper
specifier|protected
name|AllFieldMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|fieldType
parameter_list|,
name|NamedAnalyzer
name|indexAnalyzer
parameter_list|,
name|NamedAnalyzer
name|searchAnalyzer
parameter_list|,
name|boolean
name|enabled
parameter_list|,
name|boolean
name|autoBoost
parameter_list|,
name|PostingsFormatProvider
name|provider
parameter_list|,
name|SimilarityProvider
name|similarity
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|Names
argument_list|(
name|name
argument_list|,
name|name
argument_list|,
name|name
argument_list|,
name|name
argument_list|)
argument_list|,
literal|1.0f
argument_list|,
name|fieldType
argument_list|,
name|indexAnalyzer
argument_list|,
name|searchAnalyzer
argument_list|,
name|provider
argument_list|,
name|similarity
argument_list|)
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
name|this
operator|.
name|autoBoost
operator|=
name|autoBoost
expr_stmt|;
block|}
DECL|method|enabled
specifier|public
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|this
operator|.
name|enabled
return|;
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
DECL|method|queryStringTermQuery
specifier|public
name|Query
name|queryStringTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
if|if
condition|(
operator|!
name|autoBoost
condition|)
block|{
return|return
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
return|;
block|}
if|if
condition|(
name|fieldType
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
condition|)
block|{
return|return
operator|new
name|AllTermQuery
argument_list|(
name|term
argument_list|)
return|;
block|}
return|return
operator|new
name|TermQuery
argument_list|(
name|term
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
name|String
name|value
parameter_list|,
name|QueryParseContext
name|context
parameter_list|)
block|{
return|return
name|queryStringTermQuery
argument_list|(
name|names
argument_list|()
operator|.
name|createIndexNameTerm
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|preParse
specifier|public
name|void
name|preParse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|postParse
specifier|public
name|void
name|postParse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we parse in post parse
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|void
name|validate
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|MapperParsingException
block|{     }
annotation|@
name|Override
DECL|method|includeInObject
specifier|public
name|boolean
name|includeInObject
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|parseCreateField
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
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// reset the entries
name|context
operator|.
name|allEntries
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// if the autoBoost flag is not set, and we indexed a doc with custom boost, make
comment|// sure to update the flag, and notify mappings on change
if|if
condition|(
operator|!
name|autoBoost
operator|&&
name|context
operator|.
name|allEntries
argument_list|()
operator|.
name|customBoost
argument_list|()
condition|)
block|{
name|autoBoost
operator|=
literal|true
expr_stmt|;
name|context
operator|.
name|setMappingsModified
argument_list|()
expr_stmt|;
block|}
name|Analyzer
name|analyzer
init|=
name|findAnalyzer
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
operator|new
name|AllField
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|context
operator|.
name|allEntries
argument_list|()
argument_list|,
name|analyzer
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
DECL|method|findAnalyzer
specifier|private
name|Analyzer
name|findAnalyzer
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
name|Analyzer
name|analyzer
init|=
name|indexAnalyzer
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
name|analyzer
operator|=
name|context
operator|.
name|analyzer
argument_list|()
expr_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
name|analyzer
operator|=
name|context
operator|.
name|docMapper
argument_list|()
operator|.
name|indexAnalyzer
argument_list|()
expr_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
comment|// This should not happen, should we log warn it?
name|analyzer
operator|=
name|Lucene
operator|.
name|STANDARD_ANALYZER
expr_stmt|;
block|}
block|}
block|}
return|return
name|analyzer
return|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|Void
name|value
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
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
literal|null
return|;
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
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if all are defaults, no need to write it at all
if|if
condition|(
name|enabled
operator|==
name|Defaults
operator|.
name|ENABLED
operator|&&
name|fieldType
operator|.
name|stored
argument_list|()
operator|==
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|stored
argument_list|()
operator|&&
name|fieldType
operator|.
name|storeTermVectors
argument_list|()
operator|==
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|storeTermVectors
argument_list|()
operator|&&
name|indexAnalyzer
operator|==
literal|null
operator|&&
name|searchAnalyzer
operator|==
literal|null
condition|)
block|{
return|return
name|builder
return|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
name|CONTENT_TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|enabled
operator|!=
name|Defaults
operator|.
name|ENABLED
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
name|enabled
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|autoBoost
operator|!=
literal|false
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"auto_boost"
argument_list|,
name|autoBoost
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldType
operator|.
name|stored
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|stored
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
name|fieldType
operator|.
name|stored
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldType
operator|.
name|storeTermVectors
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|storeTermVectors
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"store_term_vector"
argument_list|,
name|fieldType
operator|.
name|storeTermVectors
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldType
operator|.
name|storeTermVectorOffsets
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|storeTermVectorOffsets
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"store_term_vector_offsets"
argument_list|,
name|fieldType
operator|.
name|storeTermVectorOffsets
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldType
operator|.
name|storeTermVectorPositions
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|storeTermVectorPositions
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"store_term_vector_positions"
argument_list|,
name|fieldType
operator|.
name|storeTermVectorPositions
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldType
operator|.
name|storeTermVectorPayloads
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|storeTermVectorPayloads
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"store_term_vector_payloads"
argument_list|,
name|fieldType
operator|.
name|storeTermVectorPayloads
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexAnalyzer
operator|!=
literal|null
operator|&&
name|searchAnalyzer
operator|!=
literal|null
operator|&&
name|indexAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|searchAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
operator|&&
operator|!
name|indexAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
comment|// same analyzers, output it once
name|builder
operator|.
name|field
argument_list|(
literal|"analyzer"
argument_list|,
name|indexAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|indexAnalyzer
operator|!=
literal|null
operator|&&
operator|!
name|indexAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index_analyzer"
argument_list|,
name|indexAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|searchAnalyzer
operator|!=
literal|null
operator|&&
operator|!
name|searchAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"search_analyzer"
argument_list|,
name|searchAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|similarity
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"similarity"
argument_list|,
name|similarity
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
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
comment|// do nothing here, no merging, but also no exception
block|}
block|}
end_class

end_unit

