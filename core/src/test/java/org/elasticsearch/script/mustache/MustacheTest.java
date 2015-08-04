begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.mustache
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|mustache
package|;
end_package

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|mustachejava
operator|.
name|DefaultMustacheFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|mustachejava
operator|.
name|Mustache
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|mustachejava
operator|.
name|MustacheFactory
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
name|ESTestCase
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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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

begin_comment
comment|/**  * Figure out how Mustache works for the simplest use case. Leaving in here for now for reference.  * */
end_comment

begin_class
DECL|class|MustacheTest
specifier|public
class|class
name|MustacheTest
extends|extends
name|ESTestCase
block|{
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|scopes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|scopes
operator|.
name|put
argument_list|(
literal|"boost_val"
argument_list|,
literal|"0.2"
argument_list|)
expr_stmt|;
name|String
name|template
init|=
literal|"GET _search {\"query\": "
operator|+
literal|"{\"boosting\": {"
operator|+
literal|"\"positive\": {\"match\": {\"body\": \"gift\"}},"
operator|+
literal|"\"negative\": {\"term\": {\"body\": {\"value\": \"solr\"}"
operator|+
literal|"}}, \"negative_boost\": {{boost_val}} } }}"
decl_stmt|;
name|MustacheFactory
name|f
init|=
operator|new
name|DefaultMustacheFactory
argument_list|()
decl_stmt|;
name|Mustache
name|mustache
init|=
name|f
operator|.
name|compile
argument_list|(
operator|new
name|StringReader
argument_list|(
name|template
argument_list|)
argument_list|,
literal|"example"
argument_list|)
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|mustache
operator|.
name|execute
argument_list|(
name|writer
argument_list|,
name|scopes
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Mustache templating broken"
argument_list|,
literal|"GET _search {\"query\": {\"boosting\": {\"positive\": {\"match\": {\"body\": \"gift\"}},"
operator|+
literal|"\"negative\": {\"term\": {\"body\": {\"value\": \"solr\"}}}, \"negative_boost\": 0.2 } }}"
argument_list|,
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

