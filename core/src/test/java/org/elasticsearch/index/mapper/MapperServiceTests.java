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
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|KeywordFieldMapper
operator|.
name|KeywordFieldType
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
name|mapper
operator|.
name|NumberFieldMapper
operator|.
name|NumberFieldType
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
name|io
operator|.
name|UncheckedIOException
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
name|HashMap
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
name|Map
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
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
name|instanceOf
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
name|startsWith
import|;
end_import

begin_class
DECL|class|MapperServiceTests
specifier|public
class|class
name|MapperServiceTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|method|testTypeNameStartsWithIllegalDot
specifier|public
name|void
name|testTypeNameStartsWithIllegalDot
parameter_list|()
block|{
name|String
name|index
init|=
literal|"test-index"
decl_stmt|;
name|String
name|type
init|=
literal|".test-type"
decl_stmt|;
name|String
name|field
init|=
literal|"field"
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
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
name|index
argument_list|)
operator|.
name|addMapping
argument_list|(
name|type
argument_list|,
name|field
argument_list|,
literal|"type=text"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
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
literal|"mapping type name [.test-type] must not start with a '.'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTypeNameTooLong
specifier|public
name|void
name|testTypeNameTooLong
parameter_list|()
block|{
name|String
name|index
init|=
literal|"text-index"
decl_stmt|;
name|String
name|field
init|=
literal|"field"
decl_stmt|;
name|String
name|type
init|=
operator|new
name|String
argument_list|(
operator|new
name|char
index|[
literal|256
index|]
argument_list|)
operator|.
name|replace
argument_list|(
literal|"\0"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|MapperException
name|e
init|=
name|expectThrows
argument_list|(
name|MapperException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
name|index
argument_list|)
operator|.
name|addMapping
argument_list|(
name|type
argument_list|,
name|field
argument_list|,
literal|"type=text"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
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
literal|"mapping type name ["
operator|+
name|type
operator|+
literal|"] is too long; limit is length 255 but was [256]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTypes
specifier|public
name|void
name|testTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexService
name|indexService1
init|=
name|createIndex
argument_list|(
literal|"index1"
argument_list|)
decl_stmt|;
name|MapperService
name|mapperService
init|=
name|indexService1
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
name|mapperService
operator|.
name|types
argument_list|()
argument_list|)
expr_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
literal|"type1"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
literal|"{\"type1\":{}}"
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
expr_stmt|;
name|assertNull
argument_list|(
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|MapperService
operator|.
name|DEFAULT_MAPPING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
literal|"type1"
argument_list|)
argument_list|,
name|mapperService
operator|.
name|types
argument_list|()
argument_list|)
expr_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
name|MapperService
operator|.
name|DEFAULT_MAPPING
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
literal|"{\"_default_\":{}}"
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
expr_stmt|;
name|assertNotNull
argument_list|(
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|MapperService
operator|.
name|DEFAULT_MAPPING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
literal|"type1"
argument_list|)
argument_list|,
name|mapperService
operator|.
name|types
argument_list|()
argument_list|)
expr_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
literal|"type2"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
literal|"{\"type2\":{}}"
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
expr_stmt|;
name|assertNotNull
argument_list|(
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|MapperService
operator|.
name|DEFAULT_MAPPING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"type1"
argument_list|,
literal|"type2"
argument_list|)
argument_list|)
argument_list|,
name|mapperService
operator|.
name|types
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndexIntoDefaultMapping
specifier|public
name|void
name|testIndexIntoDefaultMapping
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// 1. test implicit index creation
name|ExecutionException
name|e
init|=
name|expectThrows
argument_list|(
name|ExecutionException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"index1"
argument_list|,
name|MapperService
operator|.
name|DEFAULT_MAPPING
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{}"
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|Throwable
name|throwable
init|=
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|throwable
operator|instanceof
name|IllegalArgumentException
condition|)
block|{
name|assertEquals
argument_list|(
literal|"It is forbidden to index into the default mapping [_default_]"
argument_list|,
name|throwable
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
comment|// 2. already existing index
name|IndexService
name|indexService
init|=
name|createIndex
argument_list|(
literal|"index2"
argument_list|)
decl_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|ExecutionException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"index1"
argument_list|,
name|MapperService
operator|.
name|DEFAULT_MAPPING
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|throwable
operator|=
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|throwable
operator|instanceof
name|IllegalArgumentException
condition|)
block|{
name|assertEquals
argument_list|(
literal|"It is forbidden to index into the default mapping [_default_]"
argument_list|,
name|throwable
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
name|assertFalse
argument_list|(
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|hasMapping
argument_list|(
name|MapperService
operator|.
name|DEFAULT_MAPPING
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTotalFieldsExceedsLimit
specifier|public
name|void
name|testTotalFieldsExceedsLimit
parameter_list|()
throws|throws
name|Throwable
block|{
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mapping
init|=
name|type
lambda|->
block|{
try|try
block|{
return|return
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
name|type
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
name|string
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
name|UncheckedIOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|)
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
operator|.
name|apply
argument_list|(
literal|"type"
argument_list|)
argument_list|)
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//set total number of fields to 1 to trigger an exception
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
block|{
name|createIndex
argument_list|(
literal|"test2"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|MapperService
operator|.
name|INDEX_MAPPING_TOTAL_FIELDS_LIMIT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|1
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
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
operator|.
name|apply
argument_list|(
literal|"type"
argument_list|)
argument_list|)
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
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
literal|"Limit of total fields [1] in index [test2] has been exceeded"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMappingDepthExceedsLimit
specifier|public
name|void
name|testMappingDepthExceedsLimit
parameter_list|()
throws|throws
name|Throwable
block|{
name|CompressedXContent
name|simpleMapping
init|=
operator|new
name|CompressedXContent
argument_list|(
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
name|IndexService
name|indexService1
init|=
name|createIndex
argument_list|(
literal|"test1"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|MapperService
operator|.
name|INDEX_MAPPING_DEPTH_LIMIT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|1
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
comment|// no exception
name|indexService1
operator|.
name|mapperService
argument_list|()
operator|.
name|merge
argument_list|(
literal|"type"
argument_list|,
name|simpleMapping
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|CompressedXContent
name|objectMapping
init|=
operator|new
name|CompressedXContent
argument_list|(
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
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"object1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"object"
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
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|IndexService
name|indexService2
init|=
name|createIndex
argument_list|(
literal|"test2"
argument_list|)
decl_stmt|;
comment|// no exception
name|indexService2
operator|.
name|mapperService
argument_list|()
operator|.
name|merge
argument_list|(
literal|"type"
argument_list|,
name|objectMapping
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
name|indexService1
operator|.
name|mapperService
argument_list|()
operator|.
name|merge
argument_list|(
literal|"type2"
argument_list|,
name|objectMapping
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
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
literal|"Limit of mapping depth [1] in index [test1] has been exceeded"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnmappedFieldType
specifier|public
name|void
name|testUnmappedFieldType
parameter_list|()
block|{
name|MapperService
name|mapperService
init|=
name|createIndex
argument_list|(
literal|"index"
argument_list|)
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|mapperService
operator|.
name|unmappedFieldType
argument_list|(
literal|"keyword"
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|KeywordFieldType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mapperService
operator|.
name|unmappedFieldType
argument_list|(
literal|"long"
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|NumberFieldType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// back compat
name|assertThat
argument_list|(
name|mapperService
operator|.
name|unmappedFieldType
argument_list|(
literal|"string"
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|KeywordFieldType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertWarnings
argument_list|(
literal|"[unmapped_type:string] should be replaced with [unmapped_type:keyword]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMergeWithMap
specifier|public
name|void
name|testMergeWithMap
parameter_list|()
throws|throws
name|Throwable
block|{
name|IndexService
name|indexService1
init|=
name|createIndex
argument_list|(
literal|"index1"
argument_list|)
decl_stmt|;
name|MapperService
name|mapperService
init|=
name|indexService1
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|mappings
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|mappings
operator|.
name|put
argument_list|(
name|MapperService
operator|.
name|DEFAULT_MAPPING
argument_list|,
name|MapperService
operator|.
name|parseMapping
argument_list|(
name|xContentRegistry
argument_list|()
argument_list|,
literal|"{}"
argument_list|)
argument_list|)
expr_stmt|;
name|MapperException
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
name|mapperService
operator|.
name|merge
argument_list|(
name|mappings
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
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
name|startsWith
argument_list|(
literal|"Failed to parse mapping ["
operator|+
name|MapperService
operator|.
name|DEFAULT_MAPPING
operator|+
literal|"]: "
argument_list|)
argument_list|)
expr_stmt|;
name|mappings
operator|.
name|clear
argument_list|()
expr_stmt|;
name|mappings
operator|.
name|put
argument_list|(
literal|"type1"
argument_list|,
name|MapperService
operator|.
name|parseMapping
argument_list|(
name|xContentRegistry
argument_list|()
argument_list|,
literal|"{}"
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|MapperParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|mapperService
operator|.
name|merge
argument_list|(
name|mappings
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|startsWith
argument_list|(
literal|"Failed to parse mapping [type1]: "
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMergeParentTypesSame
specifier|public
name|void
name|testMergeParentTypesSame
parameter_list|()
block|{
comment|// Verifies that a merge (absent a DocumentMapper change)
comment|// doesn't change the parentTypes reference.
comment|// The collection was being rewrapped with each merge
comment|// in v5.2 resulting in eventual StackOverflowErrors.
comment|// https://github.com/elastic/elasticsearch/issues/23604
name|IndexService
name|indexService1
init|=
name|createIndex
argument_list|(
literal|"index1"
argument_list|)
decl_stmt|;
name|MapperService
name|mapperService
init|=
name|indexService1
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|parentTypes
init|=
name|mapperService
operator|.
name|getParentTypes
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|mappings
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
name|mappings
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|parentTypes
argument_list|,
name|mapperService
operator|.
name|getParentTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOtherDocumentMappersOnlyUpdatedWhenChangingFieldType
specifier|public
name|void
name|testOtherDocumentMappersOnlyUpdatedWhenChangingFieldType
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexService
name|indexService
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|CompressedXContent
name|simpleMapping
init|=
operator|new
name|CompressedXContent
argument_list|(
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
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|merge
argument_list|(
literal|"type1"
argument_list|,
name|simpleMapping
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DocumentMapper
name|documentMapper
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
literal|"type1"
argument_list|)
decl_stmt|;
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|merge
argument_list|(
literal|"type2"
argument_list|,
name|simpleMapping
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
literal|"type1"
argument_list|)
argument_list|,
name|documentMapper
argument_list|)
expr_stmt|;
name|CompressedXContent
name|normsDisabledMapping
init|=
operator|new
name|CompressedXContent
argument_list|(
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
name|field
argument_list|(
literal|"norms"
argument_list|,
literal|false
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
name|bytes
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
literal|"type3"
argument_list|,
name|normsDisabledMapping
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
literal|"type1"
argument_list|)
argument_list|,
name|documentMapper
argument_list|)
expr_stmt|;
block|}
DECL|method|testAllEnabled
specifier|public
name|void
name|testAllEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexService
name|indexService
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|allEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|CompressedXContent
name|enabledAll
init|=
operator|new
name|CompressedXContent
argument_list|(
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
literal|"_all"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|CompressedXContent
name|disabledAll
init|=
operator|new
name|CompressedXContent
argument_list|(
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
literal|"_all"
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
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|Exception
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
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|merge
argument_list|(
name|MapperService
operator|.
name|DEFAULT_MAPPING
argument_list|,
name|enabledAll
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
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
literal|"[_all] is disabled in 6.0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPartitionedConstraints
specifier|public
name|void
name|testPartitionedConstraints
parameter_list|()
block|{
comment|// partitioned index must have routing
name|IllegalArgumentException
name|noRoutingException
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test-index"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"{\"type\":{}}"
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|4
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.routing_partition_size"
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|noRoutingException
operator|.
name|getMessage
argument_list|()
argument_list|,
name|noRoutingException
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"must have routing"
argument_list|)
argument_list|)
expr_stmt|;
comment|// partitioned index cannot have parent/child relationships
name|IllegalArgumentException
name|parentException
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test-index"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"parent"
argument_list|,
literal|"{\"parent\":{\"_routing\":{\"required\":true}}}"
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"child"
argument_list|,
literal|"{\"child\": {\"_routing\":{\"required\":true}, \"_parent\": {\"type\": \"parent\"}}}"
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|4
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.routing_partition_size"
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|parentException
operator|.
name|getMessage
argument_list|()
argument_list|,
name|parentException
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"cannot have a _parent field"
argument_list|)
argument_list|)
expr_stmt|;
comment|// valid partitioned index
name|assertTrue
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test-index"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"{\"type\":{\"_routing\":{\"required\":true}}}"
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|4
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.routing_partition_size"
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

