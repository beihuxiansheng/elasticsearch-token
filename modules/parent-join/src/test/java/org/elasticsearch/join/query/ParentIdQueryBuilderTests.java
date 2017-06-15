begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.join.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|join
operator|.
name|query
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
name|BooleanClause
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
name|BooleanQuery
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
name|DocValuesTermsQuery
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
name|MatchNoDocsQuery
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
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|put
operator|.
name|PutMappingRequest
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
name|TypeFieldMapper
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
name|join
operator|.
name|ParentJoinPlugin
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
name|search
operator|.
name|internal
operator|.
name|SearchContext
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
name|AbstractQueryTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
name|CoreMatchers
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
name|CoreMatchers
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
name|CoreMatchers
operator|.
name|notNullValue
import|;
end_import

begin_class
DECL|class|ParentIdQueryBuilderTests
specifier|public
class|class
name|ParentIdQueryBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|ParentIdQueryBuilder
argument_list|>
block|{
DECL|field|TYPE
specifier|private
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"doc"
decl_stmt|;
DECL|field|JOIN_FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|JOIN_FIELD_NAME
init|=
literal|"join_field"
decl_stmt|;
DECL|field|PARENT_NAME
specifier|private
specifier|static
specifier|final
name|String
name|PARENT_NAME
init|=
literal|"parent"
decl_stmt|;
DECL|field|CHILD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CHILD_NAME
init|=
literal|"child"
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
name|singletonList
argument_list|(
name|ParentJoinPlugin
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|indexSettings
specifier|protected
name|Settings
name|indexSettings
parameter_list|()
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|super
operator|.
name|indexSettings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|initializeAdditionalMappings
specifier|protected
name|void
name|initializeAdditionalMappings
parameter_list|(
name|MapperService
name|mapperService
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|mapping
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"doc"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"join_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"join"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"relations"
argument_list|)
operator|.
name|field
argument_list|(
literal|"parent"
argument_list|,
literal|"child"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
name|STRING_FIELD_NAME
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
name|STRING_FIELD_NAME_2
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
name|startObject
argument_list|(
name|INT_FIELD_NAME
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"integer"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
name|DOUBLE_FIELD_NAME
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"double"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
name|BOOLEAN_FIELD_NAME
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
name|DATE_FIELD_NAME
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
name|startObject
argument_list|(
name|OBJECT_FIELD_NAME
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
name|endObject
argument_list|()
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
name|TYPE
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
operator|.
name|string
argument_list|()
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
block|}
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|ParentIdQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
return|return
operator|new
name|ParentIdQueryBuilder
argument_list|(
name|CHILD_NAME
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|ignoreUnmapped
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doAssertLuceneQuery
specifier|protected
name|void
name|doAssertLuceneQuery
parameter_list|(
name|ParentIdQueryBuilder
name|queryBuilder
parameter_list|,
name|Query
name|query
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|Matchers
operator|.
name|instanceOf
argument_list|(
name|BooleanQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
name|booleanQuery
init|=
operator|(
name|BooleanQuery
operator|)
name|query
decl_stmt|;
name|assertThat
argument_list|(
name|booleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
name|expected
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|JOIN_FIELD_NAME
operator|+
literal|"#"
operator|+
name|PARENT_NAME
argument_list|,
name|queryBuilder
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|JOIN_FIELD_NAME
argument_list|,
name|queryBuilder
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|expected
argument_list|,
name|equalTo
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFromJson
specifier|public
name|void
name|testFromJson
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|query
init|=
literal|"{\n"
operator|+
literal|"  \"parent_id\" : {\n"
operator|+
literal|"    \"type\" : \"child\",\n"
operator|+
literal|"    \"id\" : \"123\",\n"
operator|+
literal|"    \"ignore_unmapped\" : false,\n"
operator|+
literal|"    \"boost\" : 3.0,\n"
operator|+
literal|"    \"_name\" : \"name\""
operator|+
literal|"  }\n"
operator|+
literal|"}"
decl_stmt|;
name|ParentIdQueryBuilder
name|queryBuilder
init|=
operator|(
name|ParentIdQueryBuilder
operator|)
name|parseQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|checkGeneratedJson
argument_list|(
name|query
argument_list|,
name|queryBuilder
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryBuilder
operator|.
name|getType
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|"child"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryBuilder
operator|.
name|getId
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|"123"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryBuilder
operator|.
name|boost
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|3f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryBuilder
operator|.
name|queryName
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIgnoreUnmapped
specifier|public
name|void
name|testIgnoreUnmapped
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|ParentIdQueryBuilder
name|queryBuilder
init|=
operator|new
name|ParentIdQueryBuilder
argument_list|(
literal|"unmapped"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|queryBuilder
operator|.
name|ignoreUnmapped
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|queryBuilder
operator|.
name|toQuery
argument_list|(
name|createShardContext
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|MatchNoDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|ParentIdQueryBuilder
name|failingQueryBuilder
init|=
operator|new
name|ParentIdQueryBuilder
argument_list|(
literal|"unmapped"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|failingQueryBuilder
operator|.
name|ignoreUnmapped
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|QueryShardException
name|e
init|=
name|expectThrows
argument_list|(
name|QueryShardException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|failingQueryBuilder
operator|.
name|toQuery
argument_list|(
name|createShardContext
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
literal|"["
operator|+
name|ParentIdQueryBuilder
operator|.
name|NAME
operator|+
literal|"] no relation found for child [unmapped]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

