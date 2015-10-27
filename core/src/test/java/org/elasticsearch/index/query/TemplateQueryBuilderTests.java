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
name|script
operator|.
name|ScriptService
operator|.
name|ScriptType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Template
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|HashMap
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
DECL|class|TemplateQueryBuilderTests
specifier|public
class|class
name|TemplateQueryBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|TemplateQueryBuilder
argument_list|>
block|{
comment|/**      * The query type all template tests will be based on.      */
DECL|field|templateBase
specifier|private
specifier|static
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|templateBase
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupClass
specifier|public
specifier|static
name|void
name|setupClass
parameter_list|()
block|{
name|templateBase
operator|=
name|RandomQueryBuilder
operator|.
name|createQuery
argument_list|(
name|getRandom
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|supportsBoostAndQueryName
specifier|protected
name|boolean
name|supportsBoostAndQueryName
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|TemplateQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
return|return
operator|new
name|TemplateQueryBuilder
argument_list|(
operator|new
name|Template
argument_list|(
name|templateBase
operator|.
name|toString
argument_list|()
argument_list|)
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
name|TemplateQueryBuilder
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
name|assertEquals
argument_list|(
name|templateBase
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
DECL|method|testIllegalArgument
specifier|public
name|void
name|testIllegalArgument
parameter_list|()
block|{
try|try
block|{
operator|new
name|TemplateQueryBuilder
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"cannot be null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Override
DECL|method|assertBoost
specifier|protected
name|void
name|assertBoost
parameter_list|(
name|TemplateQueryBuilder
name|queryBuilder
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
comment|//no-op boost is checked already above as part of doAssertLuceneQuery as we rely on lucene equals impl
block|}
DECL|method|testJSONGeneration
specifier|public
name|void
name|testJSONGeneration
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|vars
operator|.
name|put
argument_list|(
literal|"template"
argument_list|,
literal|"filled"
argument_list|)
expr_stmt|;
name|TemplateQueryBuilder
name|builder
init|=
operator|new
name|TemplateQueryBuilder
argument_list|(
operator|new
name|Template
argument_list|(
literal|"I am a $template string"
argument_list|,
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|vars
argument_list|)
argument_list|)
decl_stmt|;
name|XContentBuilder
name|content
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|content
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|doXContent
argument_list|(
name|content
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|content
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|content
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"template\":{\"inline\":\"I am a $template string\",\"lang\":\"mustache\",\"params\":{\"template\":\"filled\"}}}"
argument_list|,
name|content
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRawEscapedTemplate
specifier|public
name|void
name|testRawEscapedTemplate
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|expectedTemplateString
init|=
literal|"{\"match_{{template}}\": {}}\""
decl_stmt|;
name|String
name|query
init|=
literal|"{\"template\": {\"query\": \"{\\\"match_{{template}}\\\": {}}\\\"\",\"params\" : {\"template\" : \"all\"}}}"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"template"
argument_list|,
literal|"all"
argument_list|)
expr_stmt|;
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|expectedBuilder
init|=
operator|new
name|TemplateQueryBuilder
argument_list|(
operator|new
name|Template
argument_list|(
name|expectedTemplateString
argument_list|,
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|params
argument_list|)
argument_list|)
decl_stmt|;
name|assertParsedQuery
argument_list|(
name|query
argument_list|,
name|expectedBuilder
argument_list|)
expr_stmt|;
block|}
DECL|method|testRawTemplate
specifier|public
name|void
name|testRawTemplate
parameter_list|()
throws|throws
name|IOException
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"match_{{template}}"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|String
name|expectedTemplateString
init|=
literal|"{\"match_{{template}}\":{}}"
decl_stmt|;
name|String
name|query
init|=
literal|"{\"template\": {\"query\": {\"match_{{template}}\": {}},\"params\" : {\"template\" : \"all\"}}}"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"template"
argument_list|,
literal|"all"
argument_list|)
expr_stmt|;
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|expectedBuilder
init|=
operator|new
name|TemplateQueryBuilder
argument_list|(
operator|new
name|Template
argument_list|(
name|expectedTemplateString
argument_list|,
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|null
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|,
name|params
argument_list|)
argument_list|)
decl_stmt|;
name|assertParsedQuery
argument_list|(
name|query
argument_list|,
name|expectedBuilder
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

