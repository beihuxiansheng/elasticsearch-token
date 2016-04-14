begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
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
name|index
operator|.
name|fielddata
operator|.
name|IndexFieldDataService
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
name|internal
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
name|TestSearchContext
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
DECL|field|PARENT_TYPE
specifier|protected
specifier|static
specifier|final
name|String
name|PARENT_TYPE
init|=
literal|"parent"
decl_stmt|;
DECL|field|CHILD_TYPE
specifier|protected
specifier|static
specifier|final
name|String
name|CHILD_TYPE
init|=
literal|"child"
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|MapperService
name|mapperService
init|=
name|queryShardContext
argument_list|()
operator|.
name|getMapperService
argument_list|()
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
name|PARENT_TYPE
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|PutMappingRequest
operator|.
name|buildFromSimplifiedDef
argument_list|(
name|PARENT_TYPE
argument_list|,
name|STRING_FIELD_NAME
argument_list|,
literal|"type=text"
argument_list|,
name|INT_FIELD_NAME
argument_list|,
literal|"type=integer"
argument_list|,
name|DOUBLE_FIELD_NAME
argument_list|,
literal|"type=double"
argument_list|,
name|BOOLEAN_FIELD_NAME
argument_list|,
literal|"type=boolean"
argument_list|,
name|DATE_FIELD_NAME
argument_list|,
literal|"type=date"
argument_list|,
name|OBJECT_FIELD_NAME
argument_list|,
literal|"type=object"
argument_list|)
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
name|mapperService
operator|.
name|merge
argument_list|(
name|CHILD_TYPE
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|PutMappingRequest
operator|.
name|buildFromSimplifiedDef
argument_list|(
name|CHILD_TYPE
argument_list|,
literal|"_parent"
argument_list|,
literal|"type="
operator|+
name|PARENT_TYPE
argument_list|,
name|STRING_FIELD_NAME
argument_list|,
literal|"type=text"
argument_list|,
name|INT_FIELD_NAME
argument_list|,
literal|"type=integer"
argument_list|,
name|DOUBLE_FIELD_NAME
argument_list|,
literal|"type=double"
argument_list|,
name|BOOLEAN_FIELD_NAME
argument_list|,
literal|"type=boolean"
argument_list|,
name|DATE_FIELD_NAME
argument_list|,
literal|"type=date"
argument_list|,
name|OBJECT_FIELD_NAME
argument_list|,
literal|"type=object"
argument_list|)
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
DECL|method|setSearchContext
specifier|protected
name|void
name|setSearchContext
parameter_list|(
name|String
index|[]
name|types
parameter_list|)
block|{
specifier|final
name|MapperService
name|mapperService
init|=
name|queryShardContext
argument_list|()
operator|.
name|getMapperService
argument_list|()
decl_stmt|;
specifier|final
name|IndexFieldDataService
name|fieldData
init|=
name|indexFieldDataService
argument_list|()
decl_stmt|;
name|TestSearchContext
name|testSearchContext
init|=
operator|new
name|TestSearchContext
argument_list|(
name|queryShardContext
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|MapperService
name|mapperService
parameter_list|()
block|{
return|return
name|mapperService
return|;
comment|// need to build / parse inner hits sort fields
block|}
annotation|@
name|Override
specifier|public
name|IndexFieldDataService
name|fieldData
parameter_list|()
block|{
return|return
name|fieldData
return|;
comment|// need to build / parse inner hits sort fields
block|}
block|}
decl_stmt|;
name|testSearchContext
operator|.
name|getQueryShardContext
argument_list|()
operator|.
name|setTypes
argument_list|(
name|types
argument_list|)
expr_stmt|;
name|SearchContext
operator|.
name|setCurrent
argument_list|(
name|testSearchContext
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
name|CHILD_TYPE
argument_list|,
name|randomAsciiOfLength
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
name|QueryShardContext
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
name|DocValuesTermsQuery
name|idQuery
init|=
operator|(
name|DocValuesTermsQuery
operator|)
name|booleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
comment|// there are no getters to get the field and terms on DocValuesTermsQuery, so lets validate by creating a
comment|// new query based on the builder:
name|assertThat
argument_list|(
name|idQuery
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
operator|new
name|DocValuesTermsQuery
argument_list|(
literal|"_parent#"
operator|+
name|PARENT_TYPE
argument_list|,
name|queryBuilder
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|TermQuery
name|typeQuery
init|=
operator|(
name|TermQuery
operator|)
name|booleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|typeQuery
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|TypeFieldMapper
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|typeQuery
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|getType
argument_list|()
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
name|queryShardContext
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
name|queryShardContext
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
literal|"] no mapping found for type [unmapped]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

