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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|IndexableField
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
name|bytes
operator|.
name|BytesArray
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
name|Index
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
name|AnalyzerScope
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
name|MapperService
operator|.
name|MergeReason
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
name|SimilarityService
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
name|IndicesModule
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
name|elasticsearch
operator|.
name|test
operator|.
name|IndexSettingsModule
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|emptyList
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

begin_class
DECL|class|ParentFieldMapperTests
specifier|public
class|class
name|ParentFieldMapperTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|method|testParentSetInDocNotAllowed
specifier|public
name|void
name|testParentSetInDocNotAllowed
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
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
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|docMapper
operator|.
name|parse
argument_list|(
name|SourceToParse
operator|.
name|source
argument_list|(
literal|"test"
argument_list|,
literal|"type"
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
literal|"_parent"
argument_list|,
literal|"1122"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected failure to parse metadata field"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Field [_parent] is a metadata field and cannot be added inside a document"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testJoinFieldSet
specifier|public
name|void
name|testJoinFieldSet
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|parentMapping
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
literal|"parent_type"
argument_list|)
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
name|String
name|childMapping
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
literal|"child_type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_parent"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"parent_type"
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
name|string
argument_list|()
decl_stmt|;
name|IndexService
name|indexService
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"mapping.single_type"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|merge
argument_list|(
literal|"parent_type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|parentMapping
argument_list|)
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|merge
argument_list|(
literal|"child_type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|childMapping
argument_list|)
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Indexing parent doc:
name|DocumentMapper
name|parentDocMapper
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
literal|"parent_type"
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|parentDocMapper
operator|.
name|parse
argument_list|(
name|SourceToParse
operator|.
name|source
argument_list|(
literal|"test"
argument_list|,
literal|"parent_type"
argument_list|,
literal|"1122"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{}"
argument_list|)
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getNumberOfFieldWithParentPrefix
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1122"
argument_list|,
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getBinaryValue
argument_list|(
literal|"_parent#parent_type"
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Indexing child doc:
name|DocumentMapper
name|childDocMapper
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
literal|"child_type"
argument_list|)
decl_stmt|;
name|doc
operator|=
name|childDocMapper
operator|.
name|parse
argument_list|(
name|SourceToParse
operator|.
name|source
argument_list|(
literal|"test"
argument_list|,
literal|"child_type"
argument_list|,
literal|"1"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{}"
argument_list|)
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|parent
argument_list|(
literal|"1122"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getNumberOfFieldWithParentPrefix
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1122"
argument_list|,
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getBinaryValue
argument_list|(
literal|"_parent#parent_type"
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testJoinFieldNotSet
specifier|public
name|void
name|testJoinFieldNotSet
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
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
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
name|SourceToParse
operator|.
name|source
argument_list|(
literal|"test"
argument_list|,
literal|"type"
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
literal|"x_field"
argument_list|,
literal|"x_value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getNumberOfFieldWithParentPrefix
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoParentNullFieldCreatedIfNoParentSpecified
specifier|public
name|void
name|testNoParentNullFieldCreatedIfNoParentSpecified
parameter_list|()
throws|throws
name|Exception
block|{
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
literal|"_index"
argument_list|,
literal|"testUUID"
argument_list|)
decl_stmt|;
name|IndexSettings
name|indexSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
name|index
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|NamedAnalyzer
name|namedAnalyzer
init|=
operator|new
name|NamedAnalyzer
argument_list|(
literal|"default"
argument_list|,
name|AnalyzerScope
operator|.
name|INDEX
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|IndexAnalyzers
name|indexAnalyzers
init|=
operator|new
name|IndexAnalyzers
argument_list|(
name|indexSettings
argument_list|,
name|namedAnalyzer
argument_list|,
name|namedAnalyzer
argument_list|,
name|namedAnalyzer
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|SimilarityService
name|similarityService
init|=
operator|new
name|SimilarityService
argument_list|(
name|indexSettings
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|MapperService
name|mapperService
init|=
operator|new
name|MapperService
argument_list|(
name|indexSettings
argument_list|,
name|indexAnalyzers
argument_list|,
name|xContentRegistry
argument_list|()
argument_list|,
name|similarityService
argument_list|,
operator|new
name|IndicesModule
argument_list|(
name|emptyList
argument_list|()
argument_list|)
operator|.
name|getMapperRegistry
argument_list|()
argument_list|,
parameter_list|()
lambda|->
literal|null
argument_list|)
decl_stmt|;
name|XContentBuilder
name|mappingSource
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"some_type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
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
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
literal|"some_type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mappingSource
operator|.
name|string
argument_list|()
argument_list|)
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|allFields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|mapperService
operator|.
name|simpleMatchToIndexNames
argument_list|(
literal|"*"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|allFields
operator|.
name|contains
argument_list|(
literal|"_parent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|allFields
operator|.
name|contains
argument_list|(
literal|"_parent#null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getNumberOfFieldWithParentPrefix
specifier|private
specifier|static
name|int
name|getNumberOfFieldWithParentPrefix
parameter_list|(
name|ParseContext
operator|.
name|Document
name|doc
parameter_list|)
block|{
name|int
name|numFieldWithParentPrefix
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IndexableField
name|field
range|:
name|doc
control|)
block|{
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_parent"
argument_list|)
condition|)
block|{
name|numFieldWithParentPrefix
operator|++
expr_stmt|;
block|}
block|}
return|return
name|numFieldWithParentPrefix
return|;
block|}
block|}
end_class

end_unit

