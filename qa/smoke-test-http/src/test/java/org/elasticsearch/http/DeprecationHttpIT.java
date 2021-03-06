begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.http
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|http
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|Header
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|ContentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|StringEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Response
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
name|logging
operator|.
name|DeprecationLogger
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
name|logging
operator|.
name|LoggerMessageFormat
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
name|Setting
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
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
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
name|ArrayList
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
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
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
name|logging
operator|.
name|DeprecationLogger
operator|.
name|WARNING_HEADER_PATTERN
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|TestDeprecationHeaderRestAction
operator|.
name|TEST_DEPRECATED_SETTING_TRUE1
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|TestDeprecationHeaderRestAction
operator|.
name|TEST_DEPRECATED_SETTING_TRUE2
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|TestDeprecationHeaderRestAction
operator|.
name|TEST_NOT_DEPRECATED_SETTING
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
operator|.
name|OK
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|RegexMatcher
operator|.
name|matches
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
name|hasItem
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
name|hasSize
import|;
end_import

begin_comment
comment|/**  * Tests {@code DeprecationLogger} uses the {@code ThreadContext} to add response headers.  */
end_comment

begin_class
DECL|class|DeprecationHttpIT
specifier|public
class|class
name|DeprecationHttpIT
extends|extends
name|HttpSmokeTestCase
block|{
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
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
name|nodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"force.http.enabled"
argument_list|,
literal|true
argument_list|)
comment|// change values of deprecated settings so that accessing them is logged
operator|.
name|put
argument_list|(
name|TEST_DEPRECATED_SETTING_TRUE1
operator|.
name|getKey
argument_list|()
argument_list|,
operator|!
name|TEST_DEPRECATED_SETTING_TRUE1
operator|.
name|getDefault
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|TEST_DEPRECATED_SETTING_TRUE2
operator|.
name|getKey
argument_list|()
argument_list|,
operator|!
name|TEST_DEPRECATED_SETTING_TRUE2
operator|.
name|getDefault
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
comment|// non-deprecated setting to ensure not everything is logged
operator|.
name|put
argument_list|(
name|TEST_NOT_DEPRECATED_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
operator|!
name|TEST_NOT_DEPRECATED_SETTING
operator|.
name|getDefault
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nodePlugins
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
name|nodePlugins
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|plugins
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|super
operator|.
name|nodePlugins
argument_list|()
argument_list|)
decl_stmt|;
name|plugins
operator|.
name|add
argument_list|(
name|TestDeprecationPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|plugins
return|;
block|}
comment|/**      * Attempts to do a scatter/gather request that expects unique responses per sub-request.      */
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://github.com/elastic/elasticsearch/issues/19222"
argument_list|)
DECL|method|testUniqueDeprecationResponsesMergedTogether
specifier|public
name|void
name|testUniqueDeprecationResponsesMergedTogether
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
index|[]
name|indices
init|=
operator|new
name|String
index|[
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|)
index|]
decl_stmt|;
comment|// add at least one document for each index
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indices
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|indices
index|[
name|i
index|]
operator|=
literal|"test"
operator|+
name|i
expr_stmt|;
comment|// create indices with a single shard to reduce noise; the query only deprecates uniquely by index anyway
name|assertTrue
argument_list|(
name|prepareCreate
argument_list|(
name|indices
index|[
name|i
index|]
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
literal|"number_of_shards"
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|randomDocCount
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
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
name|randomDocCount
condition|;
operator|++
name|j
control|)
block|{
name|index
argument_list|(
name|indices
index|[
name|i
index|]
argument_list|,
literal|"type"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|,
literal|"{\"field\":"
operator|+
name|j
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
block|}
name|refresh
argument_list|(
name|indices
argument_list|)
expr_stmt|;
specifier|final
name|String
name|commaSeparatedIndices
init|=
name|Stream
operator|.
name|of
argument_list|(
name|indices
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|","
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|body
init|=
literal|"{\"query\":{\"bool\":{\"filter\":[{\""
operator|+
name|TestDeprecatedQueryBuilder
operator|.
name|NAME
operator|+
literal|"\":{}}]}}}"
decl_stmt|;
comment|// trigger all index deprecations
name|Response
name|response
init|=
name|getRestClient
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"/"
operator|+
name|commaSeparatedIndices
operator|+
literal|"/_search"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|body
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|OK
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|deprecatedWarnings
init|=
name|getWarningHeaders
argument_list|(
name|response
operator|.
name|getHeaders
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Matcher
argument_list|<
name|String
argument_list|>
argument_list|>
name|headerMatchers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|indices
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|headerMatchers
operator|.
name|add
argument_list|(
name|containsString
argument_list|(
name|LoggerMessageFormat
operator|.
name|format
argument_list|(
literal|"[{}] index"
argument_list|,
operator|(
name|Object
operator|)
name|index
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|deprecatedWarnings
argument_list|,
name|hasSize
argument_list|(
name|headerMatchers
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Matcher
argument_list|<
name|String
argument_list|>
name|headerMatcher
range|:
name|headerMatchers
control|)
block|{
name|assertThat
argument_list|(
name|deprecatedWarnings
argument_list|,
name|hasItem
argument_list|(
name|headerMatcher
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDeprecationWarningsAppearInHeaders
specifier|public
name|void
name|testDeprecationWarningsAppearInHeaders
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestDeprecationWarningsAppearInHeaders
argument_list|()
expr_stmt|;
block|}
DECL|method|testDeprecationHeadersDoNotGetStuck
specifier|public
name|void
name|testDeprecationHeadersDoNotGetStuck
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestDeprecationWarningsAppearInHeaders
argument_list|()
expr_stmt|;
name|doTestDeprecationWarningsAppearInHeaders
argument_list|()
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|doTestDeprecationWarningsAppearInHeaders
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Run a request that receives a predictably randomized number of deprecation warnings.      *<p>      * Re-running this back-to-back helps to ensure that warnings are not being maintained across requests.      */
DECL|method|doTestDeprecationWarningsAppearInHeaders
specifier|private
name|void
name|doTestDeprecationWarningsAppearInHeaders
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|boolean
name|useDeprecatedField
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|useNonDeprecatedSetting
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
comment|// deprecated settings should also trigger a deprecation warning
specifier|final
name|List
argument_list|<
name|Setting
argument_list|<
name|Boolean
argument_list|>
argument_list|>
name|settings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|settings
operator|.
name|add
argument_list|(
name|TEST_DEPRECATED_SETTING_TRUE1
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|settings
operator|.
name|add
argument_list|(
name|TEST_DEPRECATED_SETTING_TRUE2
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|useNonDeprecatedSetting
condition|)
block|{
name|settings
operator|.
name|add
argument_list|(
name|TEST_NOT_DEPRECATED_SETTING
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|settings
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
comment|// trigger all deprecations
name|Response
name|response
init|=
name|getRestClient
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"/_test_cluster/deprecated_settings"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|buildSettingsRequest
argument_list|(
name|settings
argument_list|,
name|useDeprecatedField
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|OK
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|deprecatedWarnings
init|=
name|getWarningHeaders
argument_list|(
name|response
operator|.
name|getHeaders
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Matcher
argument_list|<
name|String
argument_list|>
argument_list|>
name|headerMatchers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|headerMatchers
operator|.
name|add
argument_list|(
name|equalTo
argument_list|(
name|TestDeprecationHeaderRestAction
operator|.
name|DEPRECATED_ENDPOINT
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|useDeprecatedField
condition|)
block|{
name|headerMatchers
operator|.
name|add
argument_list|(
name|equalTo
argument_list|(
name|TestDeprecationHeaderRestAction
operator|.
name|DEPRECATED_USAGE
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Setting
argument_list|<
name|?
argument_list|>
name|setting
range|:
name|settings
control|)
block|{
if|if
condition|(
name|setting
operator|.
name|isDeprecated
argument_list|()
condition|)
block|{
name|headerMatchers
operator|.
name|add
argument_list|(
name|equalTo
argument_list|(
literal|"["
operator|+
name|setting
operator|.
name|getKey
argument_list|()
operator|+
literal|"] setting was deprecated in Elasticsearch and will be removed in a future release! "
operator|+
literal|"See the breaking changes documentation for the next major version."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertThat
argument_list|(
name|deprecatedWarnings
argument_list|,
name|hasSize
argument_list|(
name|headerMatchers
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|deprecatedWarning
range|:
name|deprecatedWarnings
control|)
block|{
name|assertThat
argument_list|(
name|deprecatedWarning
argument_list|,
name|matches
argument_list|(
name|WARNING_HEADER_PATTERN
operator|.
name|pattern
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|actualWarningValues
init|=
name|deprecatedWarnings
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|DeprecationLogger
operator|::
name|extractWarningValueFromWarningHeader
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Matcher
argument_list|<
name|String
argument_list|>
name|headerMatcher
range|:
name|headerMatchers
control|)
block|{
name|assertThat
argument_list|(
name|actualWarningValues
argument_list|,
name|hasItem
argument_list|(
name|headerMatcher
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getWarningHeaders
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getWarningHeaders
parameter_list|(
name|Header
index|[]
name|headers
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|warnings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Header
name|header
range|:
name|headers
control|)
block|{
if|if
condition|(
name|header
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Warning"
argument_list|)
condition|)
block|{
name|warnings
operator|.
name|add
argument_list|(
name|header
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|warnings
return|;
block|}
DECL|method|buildSettingsRequest
specifier|private
name|HttpEntity
name|buildSettingsRequest
parameter_list|(
name|List
argument_list|<
name|Setting
argument_list|<
name|Boolean
argument_list|>
argument_list|>
name|settings
parameter_list|,
name|boolean
name|useDeprecatedField
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|builder
init|=
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|startArray
argument_list|(
name|useDeprecatedField
condition|?
literal|"deprecated_settings"
else|:
literal|"settings"
argument_list|)
expr_stmt|;
for|for
control|(
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|setting
range|:
name|settings
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|setting
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
operator|new
name|StringEntity
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
return|;
block|}
block|}
end_class

end_unit

