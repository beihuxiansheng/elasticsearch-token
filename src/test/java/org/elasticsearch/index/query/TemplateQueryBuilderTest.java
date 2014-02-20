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
name|test
operator|.
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_comment
comment|/**  * Test building and serialising a template search request.  * */
end_comment

begin_class
DECL|class|TemplateQueryBuilderTest
specifier|public
class|class
name|TemplateQueryBuilderTest
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
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
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
literal|"I am a $template string"
argument_list|,
name|vars
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
name|content
operator|.
name|string
argument_list|()
argument_list|,
literal|"{\"template\":{\"query\":\"I am a $template string\",\"params\":{\"template\":\"filled\"}}}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

