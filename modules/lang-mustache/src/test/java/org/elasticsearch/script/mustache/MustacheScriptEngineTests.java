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
name|MustacheFactory
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
name|XContentParser
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
name|json
operator|.
name|JsonXContent
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
name|TemplateScript
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
name|Script
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

begin_comment
comment|/**  * Mustache based templating test  */
end_comment

begin_class
DECL|class|MustacheScriptEngineTests
specifier|public
class|class
name|MustacheScriptEngineTests
extends|extends
name|ESTestCase
block|{
DECL|field|qe
specifier|private
name|MustacheScriptEngine
name|qe
decl_stmt|;
DECL|field|factory
specifier|private
name|MustacheFactory
name|factory
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|qe
operator|=
operator|new
name|MustacheScriptEngine
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|CustomMustacheFactory
argument_list|()
expr_stmt|;
block|}
DECL|method|testSimpleParameterReplace
specifier|public
name|void
name|testSimpleParameterReplace
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|compileParams
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"content_type"
argument_list|,
literal|"application/json"
argument_list|)
decl_stmt|;
block|{
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
literal|"boost_val"
argument_list|,
literal|"0.3"
argument_list|)
expr_stmt|;
name|String
name|o
init|=
name|qe
operator|.
name|compile
argument_list|(
literal|null
argument_list|,
name|template
argument_list|,
name|TemplateScript
operator|.
name|CONTEXT
argument_list|,
name|compileParams
argument_list|)
operator|.
name|newInstance
argument_list|(
name|vars
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"GET _search {\"query\": {\"boosting\": {\"positive\": {\"match\": {\"body\": \"gift\"}},"
operator|+
literal|"\"negative\": {\"term\": {\"body\": {\"value\": \"solr\"}}}, \"negative_boost\": 0.3 } }}"
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|{
name|String
name|template
init|=
literal|"GET _search {\"query\": "
operator|+
literal|"{\"boosting\": {"
operator|+
literal|"\"positive\": {\"match\": {\"body\": \"gift\"}},"
operator|+
literal|"\"negative\": {\"term\": {\"body\": {\"value\": \"{{body_val}}\"}"
operator|+
literal|"}}, \"negative_boost\": {{boost_val}} } }}"
decl_stmt|;
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
literal|"boost_val"
argument_list|,
literal|"0.3"
argument_list|)
expr_stmt|;
name|vars
operator|.
name|put
argument_list|(
literal|"body_val"
argument_list|,
literal|"\"quick brown\""
argument_list|)
expr_stmt|;
name|String
name|o
init|=
name|qe
operator|.
name|compile
argument_list|(
literal|null
argument_list|,
name|template
argument_list|,
name|TemplateScript
operator|.
name|CONTEXT
argument_list|,
name|compileParams
argument_list|)
operator|.
name|newInstance
argument_list|(
name|vars
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"GET _search {\"query\": {\"boosting\": {\"positive\": {\"match\": {\"body\": \"gift\"}},"
operator|+
literal|"\"negative\": {\"term\": {\"body\": {\"value\": \"\\\"quick brown\\\"\"}}}, \"negative_boost\": 0.3 } }}"
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|templateString
init|=
literal|"{"
operator|+
literal|"\"source\":{\"match_{{template}}\": {}},"
operator|+
literal|"\"params\":{\"template\":\"all\"}"
operator|+
literal|"}"
decl_stmt|;
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|templateString
argument_list|)
decl_stmt|;
name|Script
name|script
init|=
name|Script
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|TemplateScript
operator|.
name|Factory
name|compiled
init|=
name|qe
operator|.
name|compile
argument_list|(
literal|null
argument_list|,
name|script
operator|.
name|getIdOrCode
argument_list|()
argument_list|,
name|TemplateScript
operator|.
name|CONTEXT
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|TemplateScript
name|TemplateScript
init|=
name|compiled
operator|.
name|newInstance
argument_list|(
name|script
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|TemplateScript
operator|.
name|execute
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"match_all\":{}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseTemplateAsSingleStringWithConditionalClause
specifier|public
name|void
name|testParseTemplateAsSingleStringWithConditionalClause
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|templateString
init|=
literal|"{"
operator|+
literal|"  \"source\" : \"{ \\\"match_{{#use_it}}{{template}}{{/use_it}}\\\":{} }\","
operator|+
literal|"  \"params\":{"
operator|+
literal|"    \"template\":\"all\","
operator|+
literal|"    \"use_it\": true"
operator|+
literal|"  }"
operator|+
literal|"}"
decl_stmt|;
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|templateString
argument_list|)
decl_stmt|;
name|Script
name|script
init|=
name|Script
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|TemplateScript
operator|.
name|Factory
name|compiled
init|=
name|qe
operator|.
name|compile
argument_list|(
literal|null
argument_list|,
name|script
operator|.
name|getIdOrCode
argument_list|()
argument_list|,
name|TemplateScript
operator|.
name|CONTEXT
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|TemplateScript
name|TemplateScript
init|=
name|compiled
operator|.
name|newInstance
argument_list|(
name|script
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|TemplateScript
operator|.
name|execute
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{ \"match_all\":{} }"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEscapeJson
specifier|public
name|void
name|testEscapeJson
parameter_list|()
throws|throws
name|IOException
block|{
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|factory
operator|.
name|encode
argument_list|(
literal|"hello \n world"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"hello \\n world"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|factory
operator|.
name|encode
argument_list|(
literal|"\n"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"\\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Character
index|[]
name|specialChars
init|=
operator|new
name|Character
index|[]
block|{
literal|'\"'
block|,
literal|'\\'
block|,
literal|'\u0000'
block|,
literal|'\u0001'
block|,
literal|'\u0002'
block|,
literal|'\u0003'
block|,
literal|'\u0004'
block|,
literal|'\u0005'
block|,
literal|'\u0006'
block|,
literal|'\u0007'
block|,
literal|'\u0008'
block|,
literal|'\u0009'
block|,
literal|'\u000B'
block|,
literal|'\u000C'
block|,
literal|'\u000E'
block|,
literal|'\u000F'
block|,
literal|'\u001F'
block|}
decl_stmt|;
name|String
index|[]
name|escapedChars
init|=
operator|new
name|String
index|[]
block|{
literal|"\\\""
block|,
literal|"\\\\"
block|,
literal|"\\u0000"
block|,
literal|"\\u0001"
block|,
literal|"\\u0002"
block|,
literal|"\\u0003"
block|,
literal|"\\u0004"
block|,
literal|"\\u0005"
block|,
literal|"\\u0006"
block|,
literal|"\\u0007"
block|,
literal|"\\u0008"
block|,
literal|"\\u0009"
block|,
literal|"\\u000B"
block|,
literal|"\\u000C"
block|,
literal|"\\u000E"
block|,
literal|"\\u000F"
block|,
literal|"\\u001F"
block|}
decl_stmt|;
name|int
name|iters
init|=
name|scaledRandomIntBetween
argument_list|(
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|int
name|rounds
init|=
name|scaledRandomIntBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|StringWriter
name|expect
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|rounds
condition|;
name|j
operator|++
control|)
block|{
name|String
name|s
init|=
name|getChars
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|expect
operator|.
name|write
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|int
name|charIndex
init|=
name|randomInt
argument_list|(
literal|7
argument_list|)
decl_stmt|;
name|writer
operator|.
name|append
argument_list|(
name|specialChars
index|[
name|charIndex
index|]
argument_list|)
expr_stmt|;
name|expect
operator|.
name|append
argument_list|(
name|escapedChars
index|[
name|charIndex
index|]
argument_list|)
expr_stmt|;
block|}
name|StringWriter
name|target
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|factory
operator|.
name|encode
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|expect
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|target
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getChars
specifier|private
name|String
name|getChars
parameter_list|()
block|{
name|String
name|string
init|=
name|randomRealisticUnicodeOfCodepointLengthBetween
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|string
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|isEscapeChar
argument_list|(
name|string
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|string
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
return|;
block|}
block|}
return|return
name|string
return|;
block|}
comment|/**      * From https://www.ietf.org/rfc/rfc4627.txt:      *      * All Unicode characters may be placed within the      * quotation marks except for the characters that must be escaped:      * quotation mark, reverse solidus, and the control characters (U+0000      * through U+001F).      * */
DECL|method|isEscapeChar
specifier|private
specifier|static
name|boolean
name|isEscapeChar
parameter_list|(
name|char
name|c
parameter_list|)
block|{
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'"'
case|:
case|case
literal|'\\'
case|:
return|return
literal|true
return|;
block|}
if|if
condition|(
name|c
operator|<
literal|'\u002F'
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

