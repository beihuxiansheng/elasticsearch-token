begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
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
name|LeafReaderContext
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
name|Scorer
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
name|Weight
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchGenerationException
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
name|compress
operator|.
name|CompressedXContent
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
name|text
operator|.
name|Text
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
name|ToXContent
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
name|XContentType
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
name|IndexSettings
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
name|IndexAnalyzers
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
name|MetadataFieldMapper
operator|.
name|TypeParser
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
name|QueryShardContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_class
DECL|class|DocumentMapper
specifier|public
class|class
name|DocumentMapper
implements|implements
name|ToXContent
block|{
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|metadataMappers
specifier|private
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|MetadataFieldMapper
argument_list|>
argument_list|,
name|MetadataFieldMapper
argument_list|>
name|metadataMappers
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|rootObjectMapper
specifier|private
specifier|final
name|RootObjectMapper
name|rootObjectMapper
decl_stmt|;
DECL|field|meta
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|meta
init|=
name|emptyMap
argument_list|()
decl_stmt|;
DECL|field|builderContext
specifier|private
specifier|final
name|Mapper
operator|.
name|BuilderContext
name|builderContext
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|RootObjectMapper
operator|.
name|Builder
name|builder
parameter_list|,
name|MapperService
name|mapperService
parameter_list|)
block|{
specifier|final
name|Settings
name|indexSettings
init|=
name|mapperService
operator|.
name|getIndexSettings
argument_list|()
operator|.
name|getSettings
argument_list|()
decl_stmt|;
name|this
operator|.
name|builderContext
operator|=
operator|new
name|Mapper
operator|.
name|BuilderContext
argument_list|(
name|indexSettings
argument_list|,
operator|new
name|ContentPath
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|rootObjectMapper
operator|=
name|builder
operator|.
name|build
argument_list|(
name|builderContext
argument_list|)
expr_stmt|;
specifier|final
name|String
name|type
init|=
name|rootObjectMapper
operator|.
name|name
argument_list|()
decl_stmt|;
name|DocumentMapper
name|existingMapper
init|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|type
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|MetadataFieldMapper
operator|.
name|TypeParser
argument_list|>
name|entry
range|:
name|mapperService
operator|.
name|mapperRegistry
operator|.
name|getMetadataMapperParsers
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|MetadataFieldMapper
name|existingMetadataMapper
init|=
name|existingMapper
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|MetadataFieldMapper
operator|)
name|existingMapper
operator|.
name|mappers
argument_list|()
operator|.
name|getMapper
argument_list|(
name|name
argument_list|)
decl_stmt|;
specifier|final
name|MetadataFieldMapper
name|metadataMapper
decl_stmt|;
if|if
condition|(
name|existingMetadataMapper
operator|==
literal|null
condition|)
block|{
specifier|final
name|TypeParser
name|parser
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|metadataMapper
operator|=
name|parser
operator|.
name|getDefault
argument_list|(
name|mapperService
operator|.
name|fullName
argument_list|(
name|name
argument_list|)
argument_list|,
name|mapperService
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parserContext
argument_list|(
name|builder
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metadataMapper
operator|=
name|existingMetadataMapper
expr_stmt|;
block|}
name|metadataMappers
operator|.
name|put
argument_list|(
name|metadataMapper
operator|.
name|getClass
argument_list|()
argument_list|,
name|metadataMapper
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|meta
specifier|public
name|Builder
name|meta
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|meta
parameter_list|)
block|{
name|this
operator|.
name|meta
operator|=
name|meta
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|put
specifier|public
name|Builder
name|put
parameter_list|(
name|MetadataFieldMapper
operator|.
name|Builder
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|mapper
parameter_list|)
block|{
name|MetadataFieldMapper
name|metadataMapper
init|=
name|mapper
operator|.
name|build
argument_list|(
name|builderContext
argument_list|)
decl_stmt|;
name|metadataMappers
operator|.
name|put
argument_list|(
name|metadataMapper
operator|.
name|getClass
argument_list|()
argument_list|,
name|metadataMapper
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|DocumentMapper
name|build
parameter_list|(
name|MapperService
name|mapperService
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|rootObjectMapper
argument_list|,
literal|"Mapper builder must have the root object mapper set"
argument_list|)
expr_stmt|;
name|Mapping
name|mapping
init|=
operator|new
name|Mapping
argument_list|(
name|mapperService
operator|.
name|getIndexSettings
argument_list|()
operator|.
name|getIndexVersionCreated
argument_list|()
argument_list|,
name|rootObjectMapper
argument_list|,
name|metadataMappers
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|MetadataFieldMapper
index|[
name|metadataMappers
operator|.
name|values
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|meta
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocumentMapper
argument_list|(
name|mapperService
argument_list|,
name|mapping
argument_list|)
return|;
block|}
block|}
DECL|field|mapperService
specifier|private
specifier|final
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|typeText
specifier|private
specifier|final
name|Text
name|typeText
decl_stmt|;
DECL|field|mappingSource
specifier|private
specifier|final
name|CompressedXContent
name|mappingSource
decl_stmt|;
DECL|field|mapping
specifier|private
specifier|final
name|Mapping
name|mapping
decl_stmt|;
DECL|field|documentParser
specifier|private
specifier|final
name|DocumentParser
name|documentParser
decl_stmt|;
DECL|field|fieldMappers
specifier|private
specifier|final
name|DocumentFieldMappers
name|fieldMappers
decl_stmt|;
DECL|field|objectMappers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ObjectMapper
argument_list|>
name|objectMappers
decl_stmt|;
DECL|field|hasNestedObjects
specifier|private
specifier|final
name|boolean
name|hasNestedObjects
decl_stmt|;
DECL|method|DocumentMapper
specifier|public
name|DocumentMapper
parameter_list|(
name|MapperService
name|mapperService
parameter_list|,
name|Mapping
name|mapping
parameter_list|)
block|{
name|this
operator|.
name|mapperService
operator|=
name|mapperService
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|mapping
operator|.
name|root
argument_list|()
operator|.
name|name
argument_list|()
expr_stmt|;
name|this
operator|.
name|typeText
operator|=
operator|new
name|Text
argument_list|(
name|this
operator|.
name|type
argument_list|)
expr_stmt|;
specifier|final
name|IndexSettings
name|indexSettings
init|=
name|mapperService
operator|.
name|getIndexSettings
argument_list|()
decl_stmt|;
name|this
operator|.
name|mapping
operator|=
name|mapping
expr_stmt|;
name|this
operator|.
name|documentParser
operator|=
operator|new
name|DocumentParser
argument_list|(
name|indexSettings
argument_list|,
name|mapperService
operator|.
name|documentMapperParser
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|metadataMapper
argument_list|(
name|ParentFieldMapper
operator|.
name|class
argument_list|)
operator|.
name|active
argument_list|()
condition|)
block|{
comment|// mark the routing field mapper as required
name|metadataMapper
argument_list|(
name|RoutingFieldMapper
operator|.
name|class
argument_list|)
operator|.
name|markAsRequired
argument_list|()
expr_stmt|;
block|}
comment|// collect all the mappers for this type
name|List
argument_list|<
name|ObjectMapper
argument_list|>
name|newObjectMappers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FieldMapper
argument_list|>
name|newFieldMappers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|MetadataFieldMapper
name|metadataMapper
range|:
name|this
operator|.
name|mapping
operator|.
name|metadataMappers
control|)
block|{
if|if
condition|(
name|metadataMapper
operator|instanceof
name|FieldMapper
condition|)
block|{
name|newFieldMappers
operator|.
name|add
argument_list|(
name|metadataMapper
argument_list|)
expr_stmt|;
block|}
block|}
name|MapperUtils
operator|.
name|collect
argument_list|(
name|this
operator|.
name|mapping
operator|.
name|root
argument_list|,
name|newObjectMappers
argument_list|,
name|newFieldMappers
argument_list|)
expr_stmt|;
specifier|final
name|IndexAnalyzers
name|indexAnalyzers
init|=
name|mapperService
operator|.
name|getIndexAnalyzers
argument_list|()
decl_stmt|;
name|this
operator|.
name|fieldMappers
operator|=
operator|new
name|DocumentFieldMappers
argument_list|(
name|newFieldMappers
argument_list|,
name|indexAnalyzers
operator|.
name|getDefaultIndexAnalyzer
argument_list|()
argument_list|,
name|indexAnalyzers
operator|.
name|getDefaultSearchAnalyzer
argument_list|()
argument_list|,
name|indexAnalyzers
operator|.
name|getDefaultSearchQuoteAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ObjectMapper
argument_list|>
name|builder
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ObjectMapper
name|objectMapper
range|:
name|newObjectMappers
control|)
block|{
name|ObjectMapper
name|previous
init|=
name|builder
operator|.
name|put
argument_list|(
name|objectMapper
operator|.
name|fullPath
argument_list|()
argument_list|,
name|objectMapper
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"duplicate key "
operator|+
name|objectMapper
operator|.
name|fullPath
argument_list|()
operator|+
literal|" encountered"
argument_list|)
throw|;
block|}
block|}
name|boolean
name|hasNestedObjects
init|=
literal|false
decl_stmt|;
name|this
operator|.
name|objectMappers
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|builder
argument_list|)
expr_stmt|;
for|for
control|(
name|ObjectMapper
name|objectMapper
range|:
name|newObjectMappers
control|)
block|{
if|if
condition|(
name|objectMapper
operator|.
name|nested
argument_list|()
operator|.
name|isNested
argument_list|()
condition|)
block|{
name|hasNestedObjects
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|this
operator|.
name|hasNestedObjects
operator|=
name|hasNestedObjects
expr_stmt|;
try|try
block|{
name|mappingSource
operator|=
operator|new
name|CompressedXContent
argument_list|(
name|this
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchGenerationException
argument_list|(
literal|"failed to serialize source for type ["
operator|+
name|type
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|mapping
specifier|public
name|Mapping
name|mapping
parameter_list|()
block|{
return|return
name|mapping
return|;
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
DECL|method|typeText
specifier|public
name|Text
name|typeText
parameter_list|()
block|{
return|return
name|this
operator|.
name|typeText
return|;
block|}
DECL|method|meta
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|meta
parameter_list|()
block|{
return|return
name|mapping
operator|.
name|meta
return|;
block|}
DECL|method|mappingSource
specifier|public
name|CompressedXContent
name|mappingSource
parameter_list|()
block|{
return|return
name|this
operator|.
name|mappingSource
return|;
block|}
DECL|method|root
specifier|public
name|RootObjectMapper
name|root
parameter_list|()
block|{
return|return
name|mapping
operator|.
name|root
return|;
block|}
DECL|method|uidMapper
specifier|public
name|UidFieldMapper
name|uidMapper
parameter_list|()
block|{
return|return
name|metadataMapper
argument_list|(
name|UidFieldMapper
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|metadataMapper
specifier|public
parameter_list|<
name|T
extends|extends
name|MetadataFieldMapper
parameter_list|>
name|T
name|metadataMapper
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|mapping
operator|.
name|metadataMapper
argument_list|(
name|type
argument_list|)
return|;
block|}
DECL|method|indexMapper
specifier|public
name|IndexFieldMapper
name|indexMapper
parameter_list|()
block|{
return|return
name|metadataMapper
argument_list|(
name|IndexFieldMapper
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|typeMapper
specifier|public
name|TypeFieldMapper
name|typeMapper
parameter_list|()
block|{
return|return
name|metadataMapper
argument_list|(
name|TypeFieldMapper
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|sourceMapper
specifier|public
name|SourceFieldMapper
name|sourceMapper
parameter_list|()
block|{
return|return
name|metadataMapper
argument_list|(
name|SourceFieldMapper
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|allFieldMapper
specifier|public
name|AllFieldMapper
name|allFieldMapper
parameter_list|()
block|{
return|return
name|metadataMapper
argument_list|(
name|AllFieldMapper
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|idFieldMapper
specifier|public
name|IdFieldMapper
name|idFieldMapper
parameter_list|()
block|{
return|return
name|metadataMapper
argument_list|(
name|IdFieldMapper
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|routingFieldMapper
specifier|public
name|RoutingFieldMapper
name|routingFieldMapper
parameter_list|()
block|{
return|return
name|metadataMapper
argument_list|(
name|RoutingFieldMapper
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|parentFieldMapper
specifier|public
name|ParentFieldMapper
name|parentFieldMapper
parameter_list|()
block|{
return|return
name|metadataMapper
argument_list|(
name|ParentFieldMapper
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|IndexFieldMapper
specifier|public
name|IndexFieldMapper
name|IndexFieldMapper
parameter_list|()
block|{
return|return
name|metadataMapper
argument_list|(
name|IndexFieldMapper
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|typeFilter
specifier|public
name|Query
name|typeFilter
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
block|{
return|return
name|typeMapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
name|type
argument_list|,
name|context
argument_list|)
return|;
block|}
DECL|method|hasNestedObjects
specifier|public
name|boolean
name|hasNestedObjects
parameter_list|()
block|{
return|return
name|hasNestedObjects
return|;
block|}
DECL|method|mappers
specifier|public
name|DocumentFieldMappers
name|mappers
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldMappers
return|;
block|}
DECL|method|objectMappers
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ObjectMapper
argument_list|>
name|objectMappers
parameter_list|()
block|{
return|return
name|this
operator|.
name|objectMappers
return|;
block|}
DECL|method|parse
specifier|public
name|ParsedDocument
name|parse
parameter_list|(
name|SourceToParse
name|source
parameter_list|)
throws|throws
name|MapperParsingException
block|{
return|return
name|documentParser
operator|.
name|parseDocument
argument_list|(
name|source
argument_list|)
return|;
block|}
comment|/**      * Returns the best nested {@link ObjectMapper} instances that is in the scope of the specified nested docId.      */
DECL|method|findNestedObjectMapper
specifier|public
name|ObjectMapper
name|findNestedObjectMapper
parameter_list|(
name|int
name|nestedDocId
parameter_list|,
name|SearchContext
name|sc
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectMapper
name|nestedObjectMapper
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ObjectMapper
name|objectMapper
range|:
name|objectMappers
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|objectMapper
operator|.
name|nested
argument_list|()
operator|.
name|isNested
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|Query
name|filter
init|=
name|objectMapper
operator|.
name|nestedTypeFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// We can pass down 'null' as acceptedDocs, because nestedDocId is a doc to be fetched and
comment|// therefor is guaranteed to be a live doc.
specifier|final
name|Weight
name|nestedWeight
init|=
name|filter
operator|.
name|createWeight
argument_list|(
name|sc
operator|.
name|searcher
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|1f
argument_list|)
decl_stmt|;
name|Scorer
name|scorer
init|=
name|nestedWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|scorer
operator|.
name|iterator
argument_list|()
operator|.
name|advance
argument_list|(
name|nestedDocId
argument_list|)
operator|==
name|nestedDocId
condition|)
block|{
if|if
condition|(
name|nestedObjectMapper
operator|==
literal|null
condition|)
block|{
name|nestedObjectMapper
operator|=
name|objectMapper
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|nestedObjectMapper
operator|.
name|fullPath
argument_list|()
operator|.
name|length
argument_list|()
operator|<
name|objectMapper
operator|.
name|fullPath
argument_list|()
operator|.
name|length
argument_list|()
condition|)
block|{
name|nestedObjectMapper
operator|=
name|objectMapper
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|nestedObjectMapper
return|;
block|}
comment|/**      * Returns the parent {@link ObjectMapper} instance of the specified object mapper or<code>null</code> if there      * isn't any.      */
comment|// TODO: We should add: ObjectMapper#getParentObjectMapper()
DECL|method|findParentObjectMapper
specifier|public
name|ObjectMapper
name|findParentObjectMapper
parameter_list|(
name|ObjectMapper
name|objectMapper
parameter_list|)
block|{
name|int
name|indexOfLastDot
init|=
name|objectMapper
operator|.
name|fullPath
argument_list|()
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexOfLastDot
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|parentNestObjectPath
init|=
name|objectMapper
operator|.
name|fullPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|indexOfLastDot
argument_list|)
decl_stmt|;
return|return
name|objectMappers
argument_list|()
operator|.
name|get
argument_list|(
name|parentNestObjectPath
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|isParent
specifier|public
name|boolean
name|isParent
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|mapperService
operator|.
name|getParentTypes
argument_list|()
operator|.
name|contains
argument_list|(
name|type
argument_list|)
return|;
block|}
DECL|method|merge
specifier|public
name|DocumentMapper
name|merge
parameter_list|(
name|Mapping
name|mapping
parameter_list|,
name|boolean
name|updateAllTypes
parameter_list|)
block|{
name|Mapping
name|merged
init|=
name|this
operator|.
name|mapping
operator|.
name|merge
argument_list|(
name|mapping
argument_list|,
name|updateAllTypes
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocumentMapper
argument_list|(
name|mapperService
argument_list|,
name|merged
argument_list|)
return|;
block|}
comment|/**      * Recursively update sub field types.      */
DECL|method|updateFieldType
specifier|public
name|DocumentMapper
name|updateFieldType
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|MappedFieldType
argument_list|>
name|fullNameToFieldType
parameter_list|)
block|{
name|Mapping
name|updated
init|=
name|this
operator|.
name|mapping
operator|.
name|updateFieldType
argument_list|(
name|fullNameToFieldType
argument_list|)
decl_stmt|;
if|if
condition|(
name|updated
operator|==
name|this
operator|.
name|mapping
condition|)
block|{
comment|// no change
return|return
name|this
return|;
block|}
assert|assert
name|updated
operator|==
name|updated
operator|.
name|updateFieldType
argument_list|(
name|fullNameToFieldType
argument_list|)
operator|:
literal|"updateFieldType operation is not idempotent"
assert|;
return|return
operator|new
name|DocumentMapper
argument_list|(
name|mapperService
argument_list|,
name|updated
argument_list|)
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
return|return
name|mapping
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
return|;
block|}
block|}
end_class

end_unit

