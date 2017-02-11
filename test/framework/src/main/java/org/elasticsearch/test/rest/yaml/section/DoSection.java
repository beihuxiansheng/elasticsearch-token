begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.yaml.section
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|yaml
operator|.
name|section
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|ParsingException
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
name|Strings
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
name|collect
operator|.
name|Tuple
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
name|Loggers
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
name|NamedXContentRegistry
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
name|XContentLocation
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
name|test
operator|.
name|rest
operator|.
name|yaml
operator|.
name|ClientYamlTestExecutionContext
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
name|rest
operator|.
name|yaml
operator|.
name|ClientYamlTestResponse
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
name|rest
operator|.
name|yaml
operator|.
name|ClientYamlTestResponseException
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
name|Map
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
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
name|unmodifiableList
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
name|collect
operator|.
name|Tuple
operator|.
name|tuple
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
name|allOf
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
name|greaterThanOrEqualTo
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
name|not
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Represents a do section:  *  *   - do:  *      catch:      missing  *      headers:  *          Authorization: Basic user:pass  *          Content-Type: application/json  *      warnings:  *          - Stuff is deprecated, yo  *          - Don't use deprecated stuff  *          - Please, stop. It hurts.  *      update:  *          index:  test_1  *          type:   test  *          id:     1  *          body:   { doc: { foo: bar } }  *  */
end_comment

begin_class
DECL|class|DoSection
specifier|public
class|class
name|DoSection
implements|implements
name|ExecutableSection
block|{
DECL|method|parse
specifier|public
specifier|static
name|DoSection
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|DoSection
name|doSection
init|=
operator|new
name|DoSection
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|)
decl_stmt|;
name|ApiCallSection
name|apiCallSection
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedWarnings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"catch"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|doSection
operator|.
name|setCatch
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"warnings"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|expectedWarnings
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"[warnings] must be a string array but saw ["
operator|+
name|token
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"unknown array ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
literal|"headers"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|String
name|headerName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|headerName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|headerName
argument_list|,
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|currentFieldName
operator|!=
literal|null
condition|)
block|{
comment|// must be part of API call then
name|apiCallSection
operator|=
operator|new
name|ApiCallSection
argument_list|(
name|currentFieldName
argument_list|)
expr_stmt|;
name|String
name|paramName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|paramName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"body"
operator|.
name|equals
argument_list|(
name|paramName
argument_list|)
condition|)
block|{
name|String
name|body
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
name|XContentParser
name|bodyParser
init|=
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
name|NamedXContentRegistry
operator|.
name|EMPTY
argument_list|,
name|body
argument_list|)
decl_stmt|;
comment|//multiple bodies are supported e.g. in case of bulk provided as a whole string
while|while
condition|(
name|bodyParser
operator|.
name|nextToken
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|apiCallSection
operator|.
name|addBody
argument_list|(
name|bodyParser
operator|.
name|mapOrdered
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|apiCallSection
operator|.
name|addParam
argument_list|(
name|paramName
argument_list|,
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
literal|"body"
operator|.
name|equals
argument_list|(
name|paramName
argument_list|)
condition|)
block|{
name|apiCallSection
operator|.
name|addBody
argument_list|(
name|parser
operator|.
name|mapOrdered
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
try|try
block|{
if|if
condition|(
name|apiCallSection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"client call section is mandatory within a do section"
argument_list|)
throw|;
block|}
if|if
condition|(
name|headers
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|apiCallSection
operator|.
name|addHeaders
argument_list|(
name|headers
argument_list|)
expr_stmt|;
block|}
name|doSection
operator|.
name|setApiCallSection
argument_list|(
name|apiCallSection
argument_list|)
expr_stmt|;
name|doSection
operator|.
name|setExpectedWarningHeaders
argument_list|(
name|unmodifiableList
argument_list|(
name|expectedWarnings
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
return|return
name|doSection
return|;
block|}
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|DoSection
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|location
specifier|private
specifier|final
name|XContentLocation
name|location
decl_stmt|;
DECL|field|catchParam
specifier|private
name|String
name|catchParam
decl_stmt|;
DECL|field|apiCallSection
specifier|private
name|ApiCallSection
name|apiCallSection
decl_stmt|;
DECL|field|expectedWarningHeaders
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|expectedWarningHeaders
init|=
name|emptyList
argument_list|()
decl_stmt|;
DECL|method|DoSection
specifier|public
name|DoSection
parameter_list|(
name|XContentLocation
name|location
parameter_list|)
block|{
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
block|}
DECL|method|getCatch
specifier|public
name|String
name|getCatch
parameter_list|()
block|{
return|return
name|catchParam
return|;
block|}
DECL|method|setCatch
specifier|public
name|void
name|setCatch
parameter_list|(
name|String
name|catchParam
parameter_list|)
block|{
name|this
operator|.
name|catchParam
operator|=
name|catchParam
expr_stmt|;
block|}
DECL|method|getApiCallSection
specifier|public
name|ApiCallSection
name|getApiCallSection
parameter_list|()
block|{
return|return
name|apiCallSection
return|;
block|}
DECL|method|setApiCallSection
specifier|public
name|void
name|setApiCallSection
parameter_list|(
name|ApiCallSection
name|apiCallSection
parameter_list|)
block|{
name|this
operator|.
name|apiCallSection
operator|=
name|apiCallSection
expr_stmt|;
block|}
comment|/**      * Warning headers that we expect from this response. If the headers don't match exactly this request is considered to have failed.      * Defaults to emptyList.      */
DECL|method|getExpectedWarningHeaders
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getExpectedWarningHeaders
parameter_list|()
block|{
return|return
name|expectedWarningHeaders
return|;
block|}
comment|/**      * Set the warning headers that we expect from this response. If the headers don't match exactly this request is considered to have      * failed. Defaults to emptyList.      */
DECL|method|setExpectedWarningHeaders
specifier|public
name|void
name|setExpectedWarningHeaders
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|expectedWarningHeaders
parameter_list|)
block|{
name|this
operator|.
name|expectedWarningHeaders
operator|=
name|expectedWarningHeaders
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLocation
specifier|public
name|XContentLocation
name|getLocation
parameter_list|()
block|{
return|return
name|location
return|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|ClientYamlTestExecutionContext
name|executionContext
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|"param"
operator|.
name|equals
argument_list|(
name|catchParam
argument_list|)
condition|)
block|{
comment|//client should throw validation error before sending request
comment|//lets just return without doing anything as we don't have any client to test here
name|logger
operator|.
name|info
argument_list|(
literal|"found [catch: param], no request sent"
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|ClientYamlTestResponse
name|response
init|=
name|executionContext
operator|.
name|callApi
argument_list|(
name|apiCallSection
operator|.
name|getApi
argument_list|()
argument_list|,
name|apiCallSection
operator|.
name|getParams
argument_list|()
argument_list|,
name|apiCallSection
operator|.
name|getBodies
argument_list|()
argument_list|,
name|apiCallSection
operator|.
name|getHeaders
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|catchParam
argument_list|)
condition|)
block|{
name|String
name|catchStatusCode
decl_stmt|;
if|if
condition|(
name|catches
operator|.
name|containsKey
argument_list|(
name|catchParam
argument_list|)
condition|)
block|{
name|catchStatusCode
operator|=
name|catches
operator|.
name|get
argument_list|(
name|catchParam
argument_list|)
operator|.
name|v1
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|catchParam
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
operator|&&
name|catchParam
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|catchStatusCode
operator|=
literal|"4xx|5xx"
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"catch value ["
operator|+
name|catchParam
operator|+
literal|"] not supported"
argument_list|)
throw|;
block|}
name|fail
argument_list|(
name|formatStatusCodeMessage
argument_list|(
name|response
argument_list|,
name|catchStatusCode
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|checkWarningHeaders
argument_list|(
name|response
operator|.
name|getWarningHeaders
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClientYamlTestResponseException
name|e
parameter_list|)
block|{
name|ClientYamlTestResponse
name|restTestResponse
init|=
name|e
operator|.
name|getRestTestResponse
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasLength
argument_list|(
name|catchParam
argument_list|)
condition|)
block|{
name|fail
argument_list|(
name|formatStatusCodeMessage
argument_list|(
name|restTestResponse
argument_list|,
literal|"2xx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|catches
operator|.
name|containsKey
argument_list|(
name|catchParam
argument_list|)
condition|)
block|{
name|assertStatusCode
argument_list|(
name|restTestResponse
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|catchParam
operator|.
name|length
argument_list|()
operator|>
literal|2
operator|&&
name|catchParam
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
operator|&&
name|catchParam
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|//the text of the error message matches regular expression
name|assertThat
argument_list|(
name|formatStatusCodeMessage
argument_list|(
name|restTestResponse
argument_list|,
literal|"4xx|5xx"
argument_list|)
argument_list|,
name|e
operator|.
name|getResponseException
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|400
argument_list|)
argument_list|)
expr_stmt|;
name|Object
name|error
init|=
name|executionContext
operator|.
name|response
argument_list|(
literal|"error"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"error was expected in the response"
argument_list|,
name|error
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|//remove delimiters from regex
name|String
name|regex
init|=
name|catchParam
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|catchParam
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"the error message was expected to match the provided regex but didn't"
argument_list|,
name|error
operator|.
name|toString
argument_list|()
argument_list|,
name|matches
argument_list|(
name|regex
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"catch value ["
operator|+
name|catchParam
operator|+
literal|"] not supported"
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Check that the response contains only the warning headers that we expect.      */
DECL|method|checkWarningHeaders
name|void
name|checkWarningHeaders
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|warningHeaders
parameter_list|)
block|{
name|StringBuilder
name|failureMessage
init|=
literal|null
decl_stmt|;
comment|// LinkedHashSet so that missing expected warnings come back in a predictable order which is nice for testing
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|(
name|expectedWarningHeaders
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|header
range|:
name|warningHeaders
control|)
block|{
if|if
condition|(
name|expected
operator|.
name|remove
argument_list|(
name|header
argument_list|)
condition|)
block|{
comment|// Was expected, all good.
continue|continue;
block|}
if|if
condition|(
name|failureMessage
operator|==
literal|null
condition|)
block|{
name|failureMessage
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"got unexpected warning headers ["
argument_list|)
expr_stmt|;
block|}
name|failureMessage
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|append
argument_list|(
name|header
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|false
operator|==
name|expected
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|failureMessage
operator|==
literal|null
condition|)
block|{
name|failureMessage
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|failureMessage
operator|.
name|append
argument_list|(
literal|"\n] "
argument_list|)
expr_stmt|;
block|}
name|failureMessage
operator|.
name|append
argument_list|(
literal|"didn't get expected warning headers ["
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|header
range|:
name|expected
control|)
block|{
name|failureMessage
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|append
argument_list|(
name|header
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|failureMessage
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
name|failureMessage
operator|+
literal|"\n]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertStatusCode
specifier|private
name|void
name|assertStatusCode
parameter_list|(
name|ClientYamlTestResponse
name|restTestResponse
parameter_list|)
block|{
name|Tuple
argument_list|<
name|String
argument_list|,
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|stringMatcherTuple
init|=
name|catches
operator|.
name|get
argument_list|(
name|catchParam
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|formatStatusCodeMessage
argument_list|(
name|restTestResponse
argument_list|,
name|stringMatcherTuple
operator|.
name|v1
argument_list|()
argument_list|)
argument_list|,
name|restTestResponse
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|stringMatcherTuple
operator|.
name|v2
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|formatStatusCodeMessage
specifier|private
name|String
name|formatStatusCodeMessage
parameter_list|(
name|ClientYamlTestResponse
name|restTestResponse
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|String
name|api
init|=
name|apiCallSection
operator|.
name|getApi
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"raw"
operator|.
name|equals
argument_list|(
name|api
argument_list|)
condition|)
block|{
name|api
operator|+=
literal|"[method="
operator|+
name|apiCallSection
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"method"
argument_list|)
operator|+
literal|" path="
operator|+
name|apiCallSection
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
operator|+
literal|"]"
expr_stmt|;
block|}
return|return
literal|"expected ["
operator|+
name|expected
operator|+
literal|"] status code but api ["
operator|+
name|api
operator|+
literal|"] returned ["
operator|+
name|restTestResponse
operator|.
name|getStatusCode
argument_list|()
operator|+
literal|" "
operator|+
name|restTestResponse
operator|.
name|getReasonPhrase
argument_list|()
operator|+
literal|"] ["
operator|+
name|restTestResponse
operator|.
name|getBodyAsString
argument_list|()
operator|+
literal|"]"
return|;
block|}
DECL|field|catches
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Tuple
argument_list|<
name|String
argument_list|,
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|>
name|catches
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|catches
operator|.
name|put
argument_list|(
literal|"unauthorized"
argument_list|,
name|tuple
argument_list|(
literal|"401"
argument_list|,
name|equalTo
argument_list|(
literal|401
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|catches
operator|.
name|put
argument_list|(
literal|"forbidden"
argument_list|,
name|tuple
argument_list|(
literal|"403"
argument_list|,
name|equalTo
argument_list|(
literal|403
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|catches
operator|.
name|put
argument_list|(
literal|"missing"
argument_list|,
name|tuple
argument_list|(
literal|"404"
argument_list|,
name|equalTo
argument_list|(
literal|404
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|catches
operator|.
name|put
argument_list|(
literal|"request_timeout"
argument_list|,
name|tuple
argument_list|(
literal|"408"
argument_list|,
name|equalTo
argument_list|(
literal|408
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|catches
operator|.
name|put
argument_list|(
literal|"conflict"
argument_list|,
name|tuple
argument_list|(
literal|"409"
argument_list|,
name|equalTo
argument_list|(
literal|409
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|catches
operator|.
name|put
argument_list|(
literal|"unavailable"
argument_list|,
name|tuple
argument_list|(
literal|"503"
argument_list|,
name|equalTo
argument_list|(
literal|503
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|catches
operator|.
name|put
argument_list|(
literal|"request"
argument_list|,
name|tuple
argument_list|(
literal|"4xx|5xx"
argument_list|,
name|allOf
argument_list|(
name|greaterThanOrEqualTo
argument_list|(
literal|400
argument_list|)
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
literal|401
argument_list|)
argument_list|)
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
literal|403
argument_list|)
argument_list|)
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
literal|404
argument_list|)
argument_list|)
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
literal|408
argument_list|)
argument_list|)
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
literal|409
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

