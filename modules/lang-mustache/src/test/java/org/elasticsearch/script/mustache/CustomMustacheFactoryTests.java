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
name|Mustache
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
name|ExecutableScript
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
name|script
operator|.
name|ScriptContext
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
name|ScriptEngine
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
name|Map
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|mustache
operator|.
name|CustomMustacheFactory
operator|.
name|JSON_MIME_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|mustache
operator|.
name|CustomMustacheFactory
operator|.
name|PLAIN_TEXT_MIME_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|mustache
operator|.
name|CustomMustacheFactory
operator|.
name|X_WWW_FORM_URLENCODED_MIME_TYPE
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
name|instanceOf
import|;
end_import

begin_class
DECL|class|CustomMustacheFactoryTests
specifier|public
class|class
name|CustomMustacheFactoryTests
extends|extends
name|ESTestCase
block|{
DECL|method|testCreateEncoder
specifier|public
name|void
name|testCreateEncoder
parameter_list|()
block|{
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
name|CustomMustacheFactory
operator|.
name|createEncoder
argument_list|(
literal|null
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
name|equalTo
argument_list|(
literal|"No encoder found for MIME type [null]"
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|CustomMustacheFactory
operator|.
name|createEncoder
argument_list|(
literal|""
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
name|equalTo
argument_list|(
literal|"No encoder found for MIME type []"
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|CustomMustacheFactory
operator|.
name|createEncoder
argument_list|(
literal|"test"
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
name|equalTo
argument_list|(
literal|"No encoder found for MIME type [test]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|CustomMustacheFactory
operator|.
name|createEncoder
argument_list|(
name|CustomMustacheFactory
operator|.
name|JSON_MIME_TYPE_WITH_CHARSET
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|CustomMustacheFactory
operator|.
name|JsonEscapeEncoder
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|CustomMustacheFactory
operator|.
name|createEncoder
argument_list|(
name|CustomMustacheFactory
operator|.
name|JSON_MIME_TYPE
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|CustomMustacheFactory
operator|.
name|JsonEscapeEncoder
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|CustomMustacheFactory
operator|.
name|createEncoder
argument_list|(
name|CustomMustacheFactory
operator|.
name|PLAIN_TEXT_MIME_TYPE
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|CustomMustacheFactory
operator|.
name|DefaultEncoder
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|CustomMustacheFactory
operator|.
name|createEncoder
argument_list|(
name|CustomMustacheFactory
operator|.
name|X_WWW_FORM_URLENCODED_MIME_TYPE
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|CustomMustacheFactory
operator|.
name|UrlEncoder
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testJsonEscapeEncoder
specifier|public
name|void
name|testJsonEscapeEncoder
parameter_list|()
block|{
specifier|final
name|ScriptEngine
name|engine
init|=
operator|new
name|MustacheScriptEngine
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|randomBoolean
argument_list|()
condition|?
name|singletonMap
argument_list|(
name|Script
operator|.
name|CONTENT_TYPE_OPTION
argument_list|,
name|JSON_MIME_TYPE
argument_list|)
else|:
name|emptyMap
argument_list|()
decl_stmt|;
name|ExecutableScript
operator|.
name|Compiled
name|compiled
init|=
name|engine
operator|.
name|compile
argument_list|(
literal|null
argument_list|,
literal|"{\"field\": \"{{value}}\"}"
argument_list|,
name|ScriptContext
operator|.
name|EXECUTABLE
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|ExecutableScript
name|executable
init|=
name|compiled
operator|.
name|newInstance
argument_list|(
name|singletonMap
argument_list|(
literal|"value"
argument_list|,
literal|"a \"value\""
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|executable
operator|.
name|run
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"field\": \"a \\\"value\\\"\"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultEncoder
specifier|public
name|void
name|testDefaultEncoder
parameter_list|()
block|{
specifier|final
name|ScriptEngine
name|engine
init|=
operator|new
name|MustacheScriptEngine
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|singletonMap
argument_list|(
name|Script
operator|.
name|CONTENT_TYPE_OPTION
argument_list|,
name|PLAIN_TEXT_MIME_TYPE
argument_list|)
decl_stmt|;
name|ExecutableScript
operator|.
name|Compiled
name|compiled
init|=
name|engine
operator|.
name|compile
argument_list|(
literal|null
argument_list|,
literal|"{\"field\": \"{{value}}\"}"
argument_list|,
name|ScriptContext
operator|.
name|EXECUTABLE
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|ExecutableScript
name|executable
init|=
name|compiled
operator|.
name|newInstance
argument_list|(
name|singletonMap
argument_list|(
literal|"value"
argument_list|,
literal|"a \"value\""
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|executable
operator|.
name|run
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"field\": \"a \"value\"\"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUrlEncoder
specifier|public
name|void
name|testUrlEncoder
parameter_list|()
block|{
specifier|final
name|ScriptEngine
name|engine
init|=
operator|new
name|MustacheScriptEngine
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|singletonMap
argument_list|(
name|Script
operator|.
name|CONTENT_TYPE_OPTION
argument_list|,
name|X_WWW_FORM_URLENCODED_MIME_TYPE
argument_list|)
decl_stmt|;
name|ExecutableScript
operator|.
name|Compiled
name|compiled
init|=
name|engine
operator|.
name|compile
argument_list|(
literal|null
argument_list|,
literal|"{\"field\": \"{{value}}\"}"
argument_list|,
name|ScriptContext
operator|.
name|EXECUTABLE
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|ExecutableScript
name|executable
init|=
name|compiled
operator|.
name|newInstance
argument_list|(
name|singletonMap
argument_list|(
literal|"value"
argument_list|,
literal|"tilde~ AND date:[2016 FROM*]"
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|executable
operator|.
name|run
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"field\": \"tilde%7E+AND+date%3A%5B2016+FROM*%5D\"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

