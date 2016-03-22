begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|percolator
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
name|StoredField
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
name|DocValuesType
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
name|search
operator|.
name|Query
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
name|ParsingException
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
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|mapper
operator|.
name|core
operator|.
name|BinaryFieldMapper
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
name|KeywordFieldMapper
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
name|StringFieldMapper
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
name|ParsedQuery
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
name|QueryBuilder
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
name|query
operator|.
name|QueryShardContext
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
name|Arrays
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

begin_class
DECL|class|PercolatorFieldMapper
specifier|public
class|class
name|PercolatorFieldMapper
extends|extends
name|FieldMapper
block|{
DECL|field|TYPE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|TYPE_NAME
init|=
literal|".percolator"
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"query"
decl_stmt|;
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"percolator"
decl_stmt|;
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|PercolatorFieldType
name|FIELD_TYPE
init|=
operator|new
name|PercolatorFieldType
argument_list|()
decl_stmt|;
DECL|field|EXTRACTED_TERMS_FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|EXTRACTED_TERMS_FIELD_NAME
init|=
literal|"extracted_terms"
decl_stmt|;
DECL|field|UNKNOWN_QUERY_FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|UNKNOWN_QUERY_FIELD_NAME
init|=
literal|"unknown_query"
decl_stmt|;
DECL|field|QUERY_BUILDER_FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|QUERY_BUILDER_FIELD_NAME
init|=
literal|"query_builder_field"
decl_stmt|;
DECL|field|EXTRACTED_TERMS_FULL_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|EXTRACTED_TERMS_FULL_FIELD_NAME
init|=
name|NAME
operator|+
literal|"."
operator|+
name|EXTRACTED_TERMS_FIELD_NAME
decl_stmt|;
DECL|field|UNKNOWN_QUERY_FULL_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|UNKNOWN_QUERY_FULL_FIELD_NAME
init|=
name|NAME
operator|+
literal|"."
operator|+
name|UNKNOWN_QUERY_FIELD_NAME
decl_stmt|;
DECL|field|QUERY_BUILDER_FULL_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_BUILDER_FULL_FIELD_NAME
init|=
name|NAME
operator|+
literal|"."
operator|+
name|QUERY_BUILDER_FIELD_NAME
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|FieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|PercolatorFieldMapper
argument_list|>
block|{
DECL|field|queryShardContext
specifier|private
specifier|final
name|QueryShardContext
name|queryShardContext
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|QueryShardContext
name|queryShardContext
parameter_list|)
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|FIELD_TYPE
argument_list|,
name|FIELD_TYPE
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryShardContext
operator|=
name|queryShardContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|PercolatorFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|context
operator|.
name|path
argument_list|()
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|KeywordFieldMapper
name|extractedTermsField
init|=
name|createExtractQueryFieldBuilder
argument_list|(
name|EXTRACTED_TERMS_FIELD_NAME
argument_list|)
operator|.
name|build
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|KeywordFieldMapper
name|unknownQueryField
init|=
name|createExtractQueryFieldBuilder
argument_list|(
name|UNKNOWN_QUERY_FIELD_NAME
argument_list|)
operator|.
name|build
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|BinaryFieldMapper
name|queryBuilderField
init|=
name|createQueryBuilderFieldBuilder
argument_list|()
operator|.
name|build
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|context
operator|.
name|path
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
operator|new
name|PercolatorFieldMapper
argument_list|(
name|name
argument_list|()
argument_list|,
name|fieldType
argument_list|,
name|defaultFieldType
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
argument_list|,
name|queryShardContext
argument_list|,
name|extractedTermsField
argument_list|,
name|unknownQueryField
argument_list|,
name|queryBuilderField
argument_list|)
return|;
block|}
DECL|method|createExtractQueryFieldBuilder
specifier|static
name|KeywordFieldMapper
operator|.
name|Builder
name|createExtractQueryFieldBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|KeywordFieldMapper
operator|.
name|Builder
name|queryMetaDataFieldBuilder
init|=
operator|new
name|KeywordFieldMapper
operator|.
name|Builder
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|queryMetaDataFieldBuilder
operator|.
name|docValues
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|queryMetaDataFieldBuilder
operator|.
name|store
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|queryMetaDataFieldBuilder
operator|.
name|indexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
return|return
name|queryMetaDataFieldBuilder
return|;
block|}
DECL|method|createQueryBuilderFieldBuilder
specifier|static
name|BinaryFieldMapper
operator|.
name|Builder
name|createQueryBuilderFieldBuilder
parameter_list|()
block|{
name|BinaryFieldMapper
operator|.
name|Builder
name|builder
init|=
operator|new
name|BinaryFieldMapper
operator|.
name|Builder
argument_list|(
name|QUERY_BUILDER_FIELD_NAME
argument_list|)
decl_stmt|;
name|builder
operator|.
name|docValues
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|indexOptions
argument_list|(
name|IndexOptions
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|store
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|builder
operator|.
name|fieldType
argument_list|()
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|BINARY
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
DECL|class|TypeParser
specifier|public
specifier|static
class|class
name|TypeParser
implements|implements
name|FieldMapper
operator|.
name|TypeParser
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
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
return|return
operator|new
name|Builder
argument_list|(
name|parserContext
operator|.
name|queryShardContext
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|PercolatorFieldType
specifier|public
specifier|static
specifier|final
class|class
name|PercolatorFieldType
extends|extends
name|MappedFieldType
block|{
DECL|method|PercolatorFieldType
specifier|public
name|PercolatorFieldType
parameter_list|()
block|{
name|setName
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|setStored
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|PercolatorFieldType
specifier|public
name|PercolatorFieldType
parameter_list|(
name|MappedFieldType
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
name|MappedFieldType
name|clone
parameter_list|()
block|{
return|return
operator|new
name|PercolatorFieldType
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|typeName
specifier|public
name|String
name|typeName
parameter_list|()
block|{
return|return
name|CONTENT_TYPE
return|;
block|}
block|}
DECL|field|mapUnmappedFieldAsString
specifier|private
specifier|final
name|boolean
name|mapUnmappedFieldAsString
decl_stmt|;
DECL|field|queryShardContext
specifier|private
specifier|final
name|QueryShardContext
name|queryShardContext
decl_stmt|;
DECL|field|queryTermsField
specifier|private
specifier|final
name|KeywordFieldMapper
name|queryTermsField
decl_stmt|;
DECL|field|unknownQueryField
specifier|private
specifier|final
name|KeywordFieldMapper
name|unknownQueryField
decl_stmt|;
DECL|field|queryBuilderField
specifier|private
specifier|final
name|BinaryFieldMapper
name|queryBuilderField
decl_stmt|;
DECL|method|PercolatorFieldMapper
specifier|public
name|PercolatorFieldMapper
parameter_list|(
name|String
name|simpleName
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|,
name|MappedFieldType
name|defaultFieldType
parameter_list|,
name|Settings
name|indexSettings
parameter_list|,
name|MultiFields
name|multiFields
parameter_list|,
name|CopyTo
name|copyTo
parameter_list|,
name|QueryShardContext
name|queryShardContext
parameter_list|,
name|KeywordFieldMapper
name|queryTermsField
parameter_list|,
name|KeywordFieldMapper
name|unknownQueryField
parameter_list|,
name|BinaryFieldMapper
name|queryBuilderField
parameter_list|)
block|{
name|super
argument_list|(
name|simpleName
argument_list|,
name|fieldType
argument_list|,
name|defaultFieldType
argument_list|,
name|indexSettings
argument_list|,
name|multiFields
argument_list|,
name|copyTo
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryShardContext
operator|=
name|queryShardContext
expr_stmt|;
name|this
operator|.
name|queryTermsField
operator|=
name|queryTermsField
expr_stmt|;
name|this
operator|.
name|unknownQueryField
operator|=
name|unknownQueryField
expr_stmt|;
name|this
operator|.
name|queryBuilderField
operator|=
name|queryBuilderField
expr_stmt|;
name|this
operator|.
name|mapUnmappedFieldAsString
operator|=
name|PercolatorQueryCache
operator|.
name|INDEX_MAP_UNMAPPED_FIELDS_AS_STRING_SETTING
operator|.
name|get
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Mapper
name|parse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryShardContext
name|queryShardContext
init|=
operator|new
name|QueryShardContext
argument_list|(
name|this
operator|.
name|queryShardContext
argument_list|)
decl_stmt|;
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|queryBuilder
init|=
name|parseQueryBuilder
argument_list|(
name|queryShardContext
operator|.
name|parseContext
argument_list|()
argument_list|,
name|context
operator|.
name|parser
argument_list|()
argument_list|)
decl_stmt|;
comment|// Fetching of terms, shapes and indexed scripts happen during this rewrite:
name|queryBuilder
operator|=
name|queryBuilder
operator|.
name|rewrite
argument_list|(
name|queryShardContext
argument_list|)
expr_stmt|;
try|try
init|(
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|PercolatorQueryCache
operator|.
name|QUERY_BUILDER_CONTENT_TYPE
argument_list|)
init|)
block|{
name|queryBuilder
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
operator|new
name|MapParams
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|flush
argument_list|()
expr_stmt|;
name|byte
index|[]
name|queryBuilderAsBytes
init|=
name|builder
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
decl_stmt|;
name|context
operator|.
name|doc
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|queryBuilderField
operator|.
name|name
argument_list|()
argument_list|,
name|queryBuilderAsBytes
argument_list|,
name|queryBuilderField
operator|.
name|fieldType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Query
name|query
init|=
name|toQuery
argument_list|(
name|queryShardContext
argument_list|,
name|mapUnmappedFieldAsString
argument_list|,
name|queryBuilder
argument_list|)
decl_stmt|;
name|ExtractQueryTermsService
operator|.
name|extractQueryTerms
argument_list|(
name|query
argument_list|,
name|context
operator|.
name|doc
argument_list|()
argument_list|,
name|queryTermsField
operator|.
name|name
argument_list|()
argument_list|,
name|unknownQueryField
operator|.
name|name
argument_list|()
argument_list|,
name|queryTermsField
operator|.
name|fieldType
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|parseQuery
specifier|public
specifier|static
name|Query
name|parseQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|,
name|boolean
name|mapUnmappedFieldsAsString
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|toQuery
argument_list|(
name|context
argument_list|,
name|mapUnmappedFieldsAsString
argument_list|,
name|parseQueryBuilder
argument_list|(
name|context
operator|.
name|parseContext
argument_list|()
argument_list|,
name|parser
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toQuery
specifier|static
name|Query
name|toQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|,
name|boolean
name|mapUnmappedFieldsAsString
parameter_list|,
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|queryBuilder
parameter_list|)
throws|throws
name|IOException
block|{
name|context
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// This means that fields in the query need to exist in the mapping prior to registering this query
comment|// The reason that this is required, is that if a field doesn't exist then the query assumes defaults, which may be undesired.
comment|//
comment|// Even worse when fields mentioned in percolator queries do go added to map after the queries have been registered
comment|// then the percolator queries don't work as expected any more.
comment|//
comment|// Query parsing can't introduce new fields in mappings (which happens when registering a percolator query),
comment|// because field type can't be inferred from queries (like document do) so the best option here is to disallow
comment|// the usage of unmapped fields in percolator queries to avoid unexpected behaviour
comment|//
comment|// if index.percolator.map_unmapped_fields_as_string is set to true, query can contain unmapped fields which will be mapped
comment|// as an analyzed string.
name|context
operator|.
name|setAllowUnmappedFields
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|context
operator|.
name|setMapUnmappedFieldAsString
argument_list|(
name|mapUnmappedFieldsAsString
argument_list|)
expr_stmt|;
name|context
operator|.
name|parseFieldMatcher
argument_list|(
name|context
operator|.
name|getIndexSettings
argument_list|()
operator|.
name|getParseFieldMatcher
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|queryBuilder
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|parseQueryBuilder
specifier|static
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|parseQueryBuilder
parameter_list|(
name|QueryParseContext
name|context
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
block|{
name|context
operator|.
name|reset
argument_list|(
name|parser
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|context
operator|.
name|parseInnerQueryBuilder
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"Failed to parse"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|context
operator|.
name|reset
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Mapper
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Arrays
operator|.
expr|<
name|Mapper
operator|>
name|asList
argument_list|(
name|queryTermsField
argument_list|,
name|unknownQueryField
argument_list|,
name|queryBuilderField
argument_list|)
operator|.
name|iterator
argument_list|()
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"should not be invoked"
argument_list|)
throw|;
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
block|}
end_class

end_unit

