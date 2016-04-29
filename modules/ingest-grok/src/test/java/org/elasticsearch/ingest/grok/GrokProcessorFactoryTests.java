begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.grok
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|grok
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|AbstractProcessorFactory
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
name|Map
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
name|notNullValue
import|;
end_import

begin_class
DECL|class|GrokProcessorFactoryTests
specifier|public
class|class
name|GrokProcessorFactoryTests
extends|extends
name|ESTestCase
block|{
DECL|method|testBuild
specifier|public
name|void
name|testBuild
parameter_list|()
throws|throws
name|Exception
block|{
name|GrokProcessor
operator|.
name|Factory
name|factory
init|=
operator|new
name|GrokProcessor
operator|.
name|Factory
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"field"
argument_list|,
literal|"_field"
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"pattern"
argument_list|,
literal|"(?<foo>\\w+)"
argument_list|)
expr_stmt|;
name|String
name|processorTag
init|=
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
name|AbstractProcessorFactory
operator|.
name|TAG_KEY
argument_list|,
name|processorTag
argument_list|)
expr_stmt|;
name|GrokProcessor
name|processor
init|=
name|factory
operator|.
name|create
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getTag
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|processorTag
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getMatchField
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_field"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getGrok
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBuildMissingField
specifier|public
name|void
name|testBuildMissingField
parameter_list|()
throws|throws
name|Exception
block|{
name|GrokProcessor
operator|.
name|Factory
name|factory
init|=
operator|new
name|GrokProcessor
operator|.
name|Factory
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"pattern"
argument_list|,
literal|"(?<foo>\\w+)"
argument_list|)
expr_stmt|;
try|try
block|{
name|factory
operator|.
name|create
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
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
literal|"[field] required property is missing"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testBuildMissingPattern
specifier|public
name|void
name|testBuildMissingPattern
parameter_list|()
throws|throws
name|Exception
block|{
name|GrokProcessor
operator|.
name|Factory
name|factory
init|=
operator|new
name|GrokProcessor
operator|.
name|Factory
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"field"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
try|try
block|{
name|factory
operator|.
name|create
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
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
literal|"[pattern] required property is missing"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCreateWithCustomPatterns
specifier|public
name|void
name|testCreateWithCustomPatterns
parameter_list|()
throws|throws
name|Exception
block|{
name|GrokProcessor
operator|.
name|Factory
name|factory
init|=
operator|new
name|GrokProcessor
operator|.
name|Factory
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"field"
argument_list|,
literal|"_field"
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"pattern"
argument_list|,
literal|"%{MY_PATTERN:name}!"
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"pattern_definitions"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"MY_PATTERN"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|GrokProcessor
name|processor
init|=
name|factory
operator|.
name|create
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getMatchField
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_field"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getGrok
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getGrok
argument_list|()
operator|.
name|match
argument_list|(
literal|"foo!"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateWithInvalidPattern
specifier|public
name|void
name|testCreateWithInvalidPattern
parameter_list|()
throws|throws
name|Exception
block|{
name|GrokProcessor
operator|.
name|Factory
name|factory
init|=
operator|new
name|GrokProcessor
operator|.
name|Factory
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"field"
argument_list|,
literal|"_field"
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"pattern"
argument_list|,
literal|"["
argument_list|)
expr_stmt|;
try|try
block|{
name|factory
operator|.
name|create
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
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
literal|"[pattern] Invalid regex pattern. premature end of char-class"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCreateWithInvalidPatternDefinition
specifier|public
name|void
name|testCreateWithInvalidPatternDefinition
parameter_list|()
throws|throws
name|Exception
block|{
name|GrokProcessor
operator|.
name|Factory
name|factory
init|=
operator|new
name|GrokProcessor
operator|.
name|Factory
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"field"
argument_list|,
literal|"_field"
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"pattern"
argument_list|,
literal|"%{MY_PATTERN:name}!"
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"pattern_definitions"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"MY_PATTERN"
argument_list|,
literal|"["
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|factory
operator|.
name|create
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
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
literal|"[pattern] Invalid regex pattern. premature end of char-class"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

