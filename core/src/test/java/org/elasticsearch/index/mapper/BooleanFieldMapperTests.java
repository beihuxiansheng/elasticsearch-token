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
name|MockAnalyzer
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
name|DirectoryReader
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReader
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
name|SortedNumericDocValues
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|RAMDirectory
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
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|Booleans
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
name|BytesReference
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
name|XContentFactory
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
name|ParseContext
operator|.
name|Document
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
name|elasticsearch
operator|.
name|test
operator|.
name|InternalSettingsPlugin
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
name|ArrayList
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
name|List
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

begin_class
DECL|class|BooleanFieldMapperTests
specifier|public
class|class
name|BooleanFieldMapperTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|field|indexService
specifier|private
name|IndexService
name|indexService
decl_stmt|;
DECL|field|parser
specifier|private
name|DocumentMapperParser
name|parser
decl_stmt|;
DECL|field|preEs6Parser
specifier|private
name|DocumentMapperParser
name|preEs6Parser
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|indexService
operator|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|parser
operator|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
expr_stmt|;
name|IndexService
name|preEs6IndexService
init|=
name|createIndex
argument_list|(
literal|"legacy"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|V_5_0_0
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|preEs6Parser
operator|=
name|preEs6IndexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
expr_stmt|;
block|}
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
name|pluginList
argument_list|(
name|InternalSettingsPlugin
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|IOException
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
literal|"boolean"
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
name|DocumentMapper
name|defaultMapper
init|=
name|parser
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
name|defaultMapper
operator|.
name|parse
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
literal|"field"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
init|;
name|IndexWriter
name|w
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|w
operator|.
name|addDocuments
argument_list|(
name|doc
operator|.
name|docs
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
init|)
block|{
specifier|final
name|LeafReader
name|leaf
init|=
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
decl_stmt|;
comment|// boolean fields are indexed and have doc values by default
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"T"
argument_list|)
argument_list|,
name|leaf
operator|.
name|terms
argument_list|(
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|SortedNumericDocValues
name|values
init|=
name|leaf
operator|.
name|getSortedNumericDocValues
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|values
operator|.
name|setDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|values
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|values
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
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
literal|"boolean"
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
name|DocumentMapper
name|defaultMapper
init|=
name|parser
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
name|FieldMapper
name|mapper
init|=
name|defaultMapper
operator|.
name|mappers
argument_list|()
operator|.
name|getMapper
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"field\":{\"type\":\"boolean\"}}"
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
comment|// now change some parameters
name|mapping
operator|=
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
literal|"boolean"
argument_list|)
operator|.
name|field
argument_list|(
literal|"doc_values"
argument_list|,
literal|"false"
argument_list|)
operator|.
name|field
argument_list|(
literal|"null_value"
argument_list|,
literal|true
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
expr_stmt|;
name|defaultMapper
operator|=
name|parser
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
expr_stmt|;
name|mapper
operator|=
name|defaultMapper
operator|.
name|mappers
argument_list|()
operator|.
name|getMapper
argument_list|(
literal|"field"
argument_list|)
expr_stmt|;
name|builder
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|mapper
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"field\":{\"type\":\"boolean\",\"doc_values\":false,\"null_value\":true}}"
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testParsesPreEs6BooleansLenient
specifier|public
name|void
name|testParsesPreEs6BooleansLenient
parameter_list|()
throws|throws
name|IOException
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
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"boolean"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"boolean"
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
name|DocumentMapper
name|defaultMapper
init|=
name|preEs6Parser
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
name|String
name|falsy
init|=
name|randomFrom
argument_list|(
literal|"false"
argument_list|,
literal|"off"
argument_list|,
literal|"no"
argument_list|,
literal|"0"
argument_list|)
decl_stmt|;
name|String
name|truthy
init|=
name|randomFrom
argument_list|(
literal|"true"
argument_list|,
literal|"on"
argument_list|,
literal|"yes"
argument_list|,
literal|"1"
argument_list|)
decl_stmt|;
name|ParsedDocument
name|parsedDoc
init|=
name|defaultMapper
operator|.
name|parse
argument_list|(
literal|"legacy"
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
literal|"field1"
argument_list|,
name|falsy
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
name|truthy
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|parsedDoc
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"F"
argument_list|,
name|doc
operator|.
name|getField
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"T"
argument_list|,
name|doc
operator|.
name|getField
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedDeprecationWarnings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|Booleans
operator|.
name|isBoolean
argument_list|(
name|falsy
argument_list|)
operator|==
literal|false
condition|)
block|{
name|expectedDeprecationWarnings
operator|.
name|add
argument_list|(
literal|"Expected a boolean for property [field1] but got ["
operator|+
name|falsy
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Booleans
operator|.
name|isBoolean
argument_list|(
name|truthy
argument_list|)
operator|==
literal|false
condition|)
block|{
name|expectedDeprecationWarnings
operator|.
name|add
argument_list|(
literal|"Expected a boolean for property [field2] but got ["
operator|+
name|truthy
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expectedDeprecationWarnings
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|assertWarnings
argument_list|(
name|expectedDeprecationWarnings
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParsesEs6BooleansStrict
specifier|public
name|void
name|testParsesEs6BooleansStrict
parameter_list|()
throws|throws
name|IOException
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
literal|"boolean"
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
name|DocumentMapper
name|defaultMapper
init|=
name|parser
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
name|BytesReference
name|source
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
comment|// omit "false"/"true" here as they should still be parsed correctly
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
name|randomFrom
argument_list|(
literal|"off"
argument_list|,
literal|"no"
argument_list|,
literal|"0"
argument_list|,
literal|"on"
argument_list|,
literal|"yes"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|MapperParsingException
name|ex
init|=
name|expectThrows
argument_list|(
name|MapperParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|defaultMapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|source
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"failed to parse [field]"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiFields
specifier|public
name|void
name|testMultiFields
parameter_list|()
throws|throws
name|IOException
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
literal|"boolean"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fields"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"as_string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"keyword"
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
name|DocumentMapper
name|mapper
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|merge
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|,
name|MapperService
operator|.
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|mapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|BytesReference
name|source
init|=
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
literal|"field"
argument_list|,
literal|false
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field.as_string"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocValues
specifier|public
name|void
name|testDocValues
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
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"bool1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"boolean"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"bool2"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"boolean"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|false
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"bool3"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"boolean"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|true
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
name|DocumentMapper
name|defaultMapper
init|=
name|indexService
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
name|parsedDoc
init|=
name|defaultMapper
operator|.
name|parse
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
literal|"bool1"
argument_list|,
literal|true
argument_list|)
operator|.
name|field
argument_list|(
literal|"bool2"
argument_list|,
literal|true
argument_list|)
operator|.
name|field
argument_list|(
literal|"bool3"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|parsedDoc
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
name|IndexableField
index|[]
name|fields
init|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"bool1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|NONE
argument_list|,
name|fields
index|[
literal|0
index|]
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|,
name|fields
index|[
literal|1
index|]
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"bool2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|,
name|fields
index|[
literal|0
index|]
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"bool3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|NONE
argument_list|,
name|fields
index|[
literal|0
index|]
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|,
name|fields
index|[
literal|1
index|]
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyName
specifier|public
name|void
name|testEmptyName
parameter_list|()
throws|throws
name|IOException
block|{
comment|// after 5.x
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
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|""
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"boolean"
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
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|parser
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
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"name cannot be empty string"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

