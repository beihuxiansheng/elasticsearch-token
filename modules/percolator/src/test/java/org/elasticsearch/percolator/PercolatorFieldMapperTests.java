begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.percolator
package|package
name|org
operator|.
name|elasticsearch
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
name|util
operator|.
name|BytesRef
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
name|IndexService
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
name|MapperService
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
name|ParsedDocument
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
name|QueryShardException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|TermsLookup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESSingleNodeTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|Collection
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
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
name|query
operator|.
name|QueryBuilders
operator|.
name|matchAllQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|matchPhraseQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|matchQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|prefixQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|rangeQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|termQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|termsLookupQuery
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
name|query
operator|.
name|QueryBuilders
operator|.
name|wildcardQuery
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|percolator
operator|.
name|ExtractQueryTermsService
operator|.
name|EXTRACTION_COMPLETE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|percolator
operator|.
name|ExtractQueryTermsService
operator|.
name|EXTRACTION_FAILED
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|instanceOf
import|;
end_import

begin_class
DECL|class|PercolatorFieldMapperTests
specifier|public
class|class
name|PercolatorFieldMapperTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|field|typeName
specifier|private
name|String
name|typeName
decl_stmt|;
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|field|indexService
specifier|private
name|IndexService
name|indexService
decl_stmt|;
DECL|field|mapperService
specifier|private
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|fieldType
specifier|private
name|PercolatorFieldMapper
operator|.
name|PercolatorFieldType
name|fieldType
decl_stmt|;
annotation|@
name|Override
DECL|method|getPlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|getPlugins
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|PercolatorPlugin
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Before
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|indexService
operator|=
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|mapperService
operator|=
name|indexService
operator|.
name|mapperService
argument_list|()
expr_stmt|;
name|String
name|mapper
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_field_names"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|endObject
argument_list|()
comment|// makes testing easier
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"text"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"number_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"long"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"date"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapper
argument_list|)
argument_list|,
name|MapperService
operator|.
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|addQueryMapping
specifier|private
name|void
name|addQueryMapping
parameter_list|()
throws|throws
name|Exception
block|{
name|typeName
operator|=
name|randomAsciiOfLength
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|fieldName
operator|=
name|randomAsciiOfLength
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|String
name|percolatorMapper
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
name|typeName
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
name|fieldName
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"percolator"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
name|typeName
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|percolatorMapper
argument_list|)
argument_list|,
name|MapperService
operator|.
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fieldType
operator|=
operator|(
name|PercolatorFieldMapper
operator|.
name|PercolatorFieldType
operator|)
name|mapperService
operator|.
name|fullName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
DECL|method|testPercolatorFieldMapper
specifier|public
name|void
name|testPercolatorFieldMapper
parameter_list|()
throws|throws
name|Exception
block|{
name|addQueryMapping
argument_list|()
expr_stmt|;
name|QueryBuilder
name|queryBuilder
init|=
name|termQuery
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|typeName
argument_list|)
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
name|typeName
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|fieldName
argument_list|,
name|queryBuilder
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getExtractedTermsField
argument_list|()
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getExtractedTermsField
argument_list|()
argument_list|)
index|[
literal|0
index|]
operator|.
name|binaryValue
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"field\0value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getQueryBuilderFieldName
argument_list|()
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getExtractionResultFieldName
argument_list|()
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getExtractionResultFieldName
argument_list|()
argument_list|)
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|EXTRACTION_COMPLETE
argument_list|)
argument_list|)
expr_stmt|;
name|BytesRef
name|qbSource
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getQueryBuilderFieldName
argument_list|()
argument_list|)
index|[
literal|0
index|]
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
name|assertQueryBuilder
argument_list|(
name|qbSource
argument_list|,
name|queryBuilder
argument_list|)
expr_stmt|;
comment|// add an query for which we don't extract terms from
name|queryBuilder
operator|=
name|rangeQuery
argument_list|(
literal|"field"
argument_list|)
operator|.
name|from
argument_list|(
literal|"a"
argument_list|)
operator|.
name|to
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|doc
operator|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|typeName
argument_list|)
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
name|typeName
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|fieldName
argument_list|,
name|queryBuilder
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getExtractionResultFieldName
argument_list|()
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getExtractionResultFieldName
argument_list|()
argument_list|)
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|EXTRACTION_FAILED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getExtractedTermsField
argument_list|()
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getQueryBuilderFieldName
argument_list|()
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|qbSource
operator|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getQueryBuilderFieldName
argument_list|()
argument_list|)
index|[
literal|0
index|]
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
name|assertQueryBuilder
argument_list|(
name|qbSource
argument_list|,
name|queryBuilder
argument_list|)
expr_stmt|;
block|}
DECL|method|testStoringQueries
specifier|public
name|void
name|testStoringQueries
parameter_list|()
throws|throws
name|Exception
block|{
name|addQueryMapping
argument_list|()
expr_stmt|;
name|QueryBuilder
index|[]
name|queries
init|=
operator|new
name|QueryBuilder
index|[]
block|{
name|termQuery
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
block|,
name|matchAllQuery
argument_list|()
block|,
name|matchQuery
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
block|,
name|matchPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
block|,
name|prefixQuery
argument_list|(
literal|"field"
argument_list|,
literal|"v"
argument_list|)
block|,
name|wildcardQuery
argument_list|(
literal|"field"
argument_list|,
literal|"v*"
argument_list|)
block|,
name|rangeQuery
argument_list|(
literal|"number_field"
argument_list|)
operator|.
name|gte
argument_list|(
literal|0
argument_list|)
operator|.
name|lte
argument_list|(
literal|9
argument_list|)
block|,
name|rangeQuery
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|from
argument_list|(
literal|"2015-01-01T00:00"
argument_list|)
operator|.
name|to
argument_list|(
literal|"2015-01-01T00:00"
argument_list|)
block|}
decl_stmt|;
comment|// note: it important that range queries never rewrite, otherwise it will cause results to be wrong.
comment|// (it can't use shard data for rewriting purposes, because percolator queries run on MemoryIndex)
for|for
control|(
name|QueryBuilder
name|query
range|:
name|queries
control|)
block|{
name|ParsedDocument
name|doc
init|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|typeName
argument_list|)
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
name|typeName
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|fieldName
argument_list|,
name|query
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRef
name|qbSource
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getQueryBuilderFieldName
argument_list|()
argument_list|)
index|[
literal|0
index|]
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
name|assertQueryBuilder
argument_list|(
name|qbSource
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testQueryWithRewrite
specifier|public
name|void
name|testQueryWithRewrite
parameter_list|()
throws|throws
name|Exception
block|{
name|addQueryMapping
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"remote"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|QueryBuilder
name|queryBuilder
init|=
name|termsLookupQuery
argument_list|(
literal|"field"
argument_list|,
operator|new
name|TermsLookup
argument_list|(
literal|"remote"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|,
literal|"field"
argument_list|)
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|typeName
argument_list|)
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
name|typeName
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|fieldName
argument_list|,
name|queryBuilder
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRef
name|qbSource
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getQueryBuilderFieldName
argument_list|()
argument_list|)
index|[
literal|0
index|]
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
name|assertQueryBuilder
argument_list|(
name|qbSource
argument_list|,
name|queryBuilder
operator|.
name|rewrite
argument_list|(
name|indexService
operator|.
name|newQueryShardContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPercolatorFieldMapperUnMappedField
specifier|public
name|void
name|testPercolatorFieldMapperUnMappedField
parameter_list|()
throws|throws
name|Exception
block|{
name|addQueryMapping
argument_list|()
expr_stmt|;
name|MapperParsingException
name|exception
init|=
name|expectThrows
argument_list|(
name|MapperParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|typeName
argument_list|)
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
name|typeName
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|fieldName
argument_list|,
name|termQuery
argument_list|(
literal|"unmapped_field"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|QueryShardException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"No field mapping can be found for the field with name [unmapped_field]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPercolatorFieldMapper_noQuery
specifier|public
name|void
name|testPercolatorFieldMapper_noQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|addQueryMapping
argument_list|()
expr_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|typeName
argument_list|)
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
name|typeName
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|fieldType
operator|.
name|getQueryBuilderFieldName
argument_list|()
argument_list|)
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|typeName
argument_list|)
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
name|typeName
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|nullField
argument_list|(
name|fieldName
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getDetailedMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"query malformed, must start with start_object"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAllowNoAdditionalSettings
specifier|public
name|void
name|testAllowNoAdditionalSettings
parameter_list|()
throws|throws
name|Exception
block|{
name|addQueryMapping
argument_list|()
expr_stmt|;
name|IndexService
name|indexService
init|=
name|createIndex
argument_list|(
literal|"test1"
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|MapperService
name|mapperService
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|String
name|percolatorMapper
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
name|typeName
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
name|fieldName
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"percolator"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"no"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
try|try
block|{
name|mapperService
operator|.
name|merge
argument_list|(
name|typeName
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|percolatorMapper
argument_list|)
argument_list|,
name|MapperService
operator|.
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"MapperParsingException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Mapping definition for ["
operator|+
name|fieldName
operator|+
literal|"] has unsupported parameters:  [index : no]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// multiple percolator fields are allowed in the mapping, but only one field can be used at index time.
DECL|method|testMultiplePercolatorFields
specifier|public
name|void
name|testMultiplePercolatorFields
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|typeName
init|=
literal|"another_type"
decl_stmt|;
name|String
name|percolatorMapper
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
name|typeName
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_field_names"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|endObject
argument_list|()
comment|// makes testing easier
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"query_field1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"percolator"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"query_field2"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"percolator"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
name|typeName
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|percolatorMapper
argument_list|)
argument_list|,
name|MapperService
operator|.
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|QueryBuilder
name|queryBuilder
init|=
name|matchQuery
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|typeName
argument_list|)
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
name|typeName
argument_list|,
literal|"1"
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query_field1"
argument_list|,
name|queryBuilder
argument_list|)
operator|.
name|field
argument_list|(
literal|"query_field2"
argument_list|,
name|queryBuilder
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|12
argument_list|)
argument_list|)
expr_stmt|;
comment|// also includes all other meta fields
name|BytesRef
name|queryBuilderAsBytes
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"query_field1.query_builder_field"
argument_list|)
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
name|assertQueryBuilder
argument_list|(
name|queryBuilderAsBytes
argument_list|,
name|queryBuilder
argument_list|)
expr_stmt|;
name|queryBuilderAsBytes
operator|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"query_field2.query_builder_field"
argument_list|)
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
name|assertQueryBuilder
argument_list|(
name|queryBuilderAsBytes
argument_list|,
name|queryBuilder
argument_list|)
expr_stmt|;
block|}
comment|// percolator field can be nested under an object field, but only one query can be specified per document
DECL|method|testNestedPercolatorField
specifier|public
name|void
name|testNestedPercolatorField
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|typeName
init|=
literal|"another_type"
decl_stmt|;
name|String
name|percolatorMapper
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
name|typeName
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_field_names"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|endObject
argument_list|()
comment|// makes testing easier
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"object_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"object"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"query_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"percolator"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
name|typeName
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|percolatorMapper
argument_list|)
argument_list|,
name|MapperService
operator|.
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|QueryBuilder
name|queryBuilder
init|=
name|matchQuery
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|typeName
argument_list|)
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
name|typeName
argument_list|,
literal|"1"
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"object_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"query_field"
argument_list|,
name|queryBuilder
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
comment|// also includes all other meta fields
name|BytesRef
name|queryBuilderAsBytes
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"object_field.query_field.query_builder_field"
argument_list|)
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
name|assertQueryBuilder
argument_list|(
name|queryBuilderAsBytes
argument_list|,
name|queryBuilder
argument_list|)
expr_stmt|;
name|doc
operator|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|typeName
argument_list|)
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
name|typeName
argument_list|,
literal|"1"
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startArray
argument_list|(
literal|"object_field"
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query_field"
argument_list|,
name|queryBuilder
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
comment|// also includes all other meta fields
name|queryBuilderAsBytes
operator|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"object_field.query_field.query_builder_field"
argument_list|)
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
name|assertQueryBuilder
argument_list|(
name|queryBuilderAsBytes
argument_list|,
name|queryBuilder
argument_list|)
expr_stmt|;
name|MapperParsingException
name|e
init|=
name|expectThrows
argument_list|(
name|MapperParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|typeName
argument_list|)
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
name|typeName
argument_list|,
literal|"1"
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startArray
argument_list|(
literal|"object_field"
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query_field"
argument_list|,
name|queryBuilder
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"query_field"
argument_list|,
name|queryBuilder
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"a document can only contain one percolator query"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertQueryBuilder
specifier|private
name|void
name|assertQueryBuilder
parameter_list|(
name|BytesRef
name|actual
parameter_list|,
name|QueryBuilder
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|sourceParser
init|=
name|PercolatorFieldMapper
operator|.
name|QUERY_BUILDER_CONTENT_TYPE
operator|.
name|xContent
argument_list|()
operator|.
name|createParser
argument_list|(
name|actual
operator|.
name|bytes
argument_list|,
name|actual
operator|.
name|offset
argument_list|,
name|actual
operator|.
name|length
argument_list|)
decl_stmt|;
name|QueryParseContext
name|qsc
init|=
name|indexService
operator|.
name|newQueryShardContext
argument_list|()
operator|.
name|newParseContext
argument_list|(
name|sourceParser
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|qsc
operator|.
name|parseInnerQueryBuilder
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

