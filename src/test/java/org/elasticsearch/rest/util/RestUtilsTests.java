begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|support
operator|.
name|RestUtils
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
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
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
comment|/**  *  */
end_comment

begin_class
DECL|class|RestUtilsTests
specifier|public
class|class
name|RestUtilsTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testDecodeQueryString
specifier|public
name|void
name|testDecodeQueryString
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|String
name|uri
init|=
literal|"something?test=value"
decl_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|uri
operator|=
literal|"something?test=value&test1=value1"
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|uri
operator|=
literal|"something"
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|uri
operator|.
name|length
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|uri
operator|=
literal|"something"
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
operator|-
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDecodeQueryStringEdgeCases
specifier|public
name|void
name|testDecodeQueryStringEdgeCases
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|String
name|uri
init|=
literal|"something?"
decl_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|uri
operator|=
literal|"something?&"
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|uri
operator|=
literal|"something?p=v&&p1=v1"
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"p"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"v"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"p1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"v1"
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|uri
operator|=
literal|"something?="
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|uri
operator|=
literal|"something?&="
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|uri
operator|=
literal|"something?a"
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|uri
operator|=
literal|"something?p=v&a"
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"p"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"v"
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|uri
operator|=
literal|"something?p=v&a&p1=v1"
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"p"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"v"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"p1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"v1"
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|uri
operator|=
literal|"something?p=v&a&b&p1=v1"
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"p"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"v"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"p1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"v1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

