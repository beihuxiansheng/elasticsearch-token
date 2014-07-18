begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|mapper
operator|.
name|DocumentMapper
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
name|test
operator|.
name|ElasticsearchSingleNodeTest
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

begin_class
DECL|class|FieldNamesFieldMapperTests
specifier|public
class|class
name|FieldNamesFieldMapperTests
extends|extends
name|ElasticsearchSingleNodeTest
block|{
DECL|method|extract
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|extract
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|ImmutableSet
operator|.
expr|<
name|String
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|FieldNamesFieldMapper
operator|.
name|extractFieldNames
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|set
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Set
argument_list|<
name|T
argument_list|>
name|set
parameter_list|(
name|T
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|HashSet
argument_list|<
name|T
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
argument_list|)
return|;
block|}
DECL|method|testExtractFieldNames
specifier|public
name|void
name|testExtractFieldNames
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|set
argument_list|(
literal|"abc"
argument_list|)
argument_list|,
name|extract
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|set
argument_list|(
literal|"a"
argument_list|,
literal|"a.b"
argument_list|)
argument_list|,
name|extract
argument_list|(
literal|"a.b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|set
argument_list|(
literal|"a"
argument_list|,
literal|"a.b"
argument_list|,
literal|"a.b.c"
argument_list|)
argument_list|,
name|extract
argument_list|(
literal|"a.b.c"
argument_list|)
argument_list|)
expr_stmt|;
comment|// and now corner cases
name|assertEquals
argument_list|(
name|set
argument_list|(
literal|""
argument_list|,
literal|".a"
argument_list|)
argument_list|,
name|extract
argument_list|(
literal|".a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|set
argument_list|(
literal|"a"
argument_list|,
literal|"a."
argument_list|)
argument_list|,
name|extract
argument_list|(
literal|"a."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|set
argument_list|(
literal|""
argument_list|,
literal|"."
argument_list|,
literal|".."
argument_list|)
argument_list|,
name|extract
argument_list|(
literal|".."
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentMapper
name|defaultMapper
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
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|defaultMapper
operator|.
name|parse
argument_list|(
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
literal|"a"
argument_list|,
literal|"100"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"b"
argument_list|)
operator|.
name|field
argument_list|(
literal|"c"
argument_list|,
literal|42
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
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexableField
name|field
range|:
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
name|FieldNamesFieldMapper
operator|.
name|CONTENT_TYPE
operator|.
name|equals
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|fieldNames
operator|.
name|add
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"b.c"
argument_list|,
literal|"_uid"
argument_list|,
literal|"_type"
argument_list|,
literal|"_version"
argument_list|,
literal|"_source"
argument_list|,
literal|"_all"
argument_list|)
argument_list|)
argument_list|,
name|fieldNames
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

