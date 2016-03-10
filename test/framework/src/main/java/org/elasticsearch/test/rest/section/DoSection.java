begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.section
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|section
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
name|ESLogger
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
name|test
operator|.
name|rest
operator|.
name|RestTestExecutionContext
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
name|client
operator|.
name|RestException
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
name|client
operator|.
name|RestResponse
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
comment|/**  * Represents a do section:  *  *   - do:  *      catch:      missing  *      headers:  *          Authorization: Basic user:pass  *          Content-Type: application/json  *      update:  *          index:  test_1  *          type:   test  *          id:     1  *          body:   { doc: { foo: bar } }  *  */
end_comment

begin_class
DECL|class|DoSection
specifier|public
class|class
name|DoSection
implements|implements
name|ExecutableSection
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|ESLogger
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
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|RestTestExecutionContext
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
name|RestResponse
name|restResponse
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
name|restResponse
argument_list|,
name|catchStatusCode
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RestException
name|e
parameter_list|)
block|{
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
name|e
operator|.
name|restResponse
argument_list|()
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
name|e
operator|.
name|restResponse
argument_list|()
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
name|e
operator|.
name|restResponse
argument_list|()
argument_list|,
literal|"4xx|5xx"
argument_list|)
argument_list|,
name|e
operator|.
name|statusCode
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
DECL|method|assertStatusCode
specifier|private
name|void
name|assertStatusCode
parameter_list|(
name|RestResponse
name|restResponse
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
name|restResponse
argument_list|,
name|stringMatcherTuple
operator|.
name|v1
argument_list|()
argument_list|)
argument_list|,
name|restResponse
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
name|RestResponse
name|restResponse
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
return|return
literal|"expected ["
operator|+
name|expected
operator|+
literal|"] status code but api ["
operator|+
name|apiCallSection
operator|.
name|getApi
argument_list|()
operator|+
literal|"] returned ["
operator|+
name|restResponse
operator|.
name|getStatusCode
argument_list|()
operator|+
literal|" "
operator|+
name|restResponse
operator|.
name|getReasonPhrase
argument_list|()
operator|+
literal|"] ["
operator|+
name|restResponse
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
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
literal|403
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
