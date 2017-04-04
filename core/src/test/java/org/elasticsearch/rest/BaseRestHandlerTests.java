begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|node
operator|.
name|NodeClient
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
name|Table
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
name|rest
operator|.
name|action
operator|.
name|cat
operator|.
name|AbstractCatAction
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
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|FakeRestChannel
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
name|FakeRestRequest
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|StringContains
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
name|object
operator|.
name|HasToString
operator|.
name|hasToString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_class
DECL|class|BaseRestHandlerTests
specifier|public
class|class
name|BaseRestHandlerTests
extends|extends
name|ESTestCase
block|{
DECL|method|testOneUnconsumedParameters
specifier|public
name|void
name|testOneUnconsumedParameters
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicBoolean
name|executed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|BaseRestHandler
name|handler
init|=
operator|new
name|BaseRestHandler
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|RestChannelConsumer
name|prepareRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|NodeClient
name|client
parameter_list|)
throws|throws
name|IOException
block|{
name|request
operator|.
name|param
argument_list|(
literal|"consumed"
argument_list|)
expr_stmt|;
return|return
name|channel
lambda|->
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
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
literal|"consumed"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"unconsumed"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
operator|.
name|Builder
argument_list|(
name|xContentRegistry
argument_list|()
argument_list|)
operator|.
name|withParams
argument_list|(
name|params
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RestChannel
name|channel
init|=
operator|new
name|FakeRestChannel
argument_list|(
name|request
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|final
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
name|handler
operator|.
name|handleRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|mock
argument_list|(
name|NodeClient
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"request [/] contains unrecognized parameter: [unconsumed]"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|executed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleUnconsumedParameters
specifier|public
name|void
name|testMultipleUnconsumedParameters
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicBoolean
name|executed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|BaseRestHandler
name|handler
init|=
operator|new
name|BaseRestHandler
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|RestChannelConsumer
name|prepareRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|NodeClient
name|client
parameter_list|)
throws|throws
name|IOException
block|{
name|request
operator|.
name|param
argument_list|(
literal|"consumed"
argument_list|)
expr_stmt|;
return|return
name|channel
lambda|->
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
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
literal|"consumed"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"unconsumed-first"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"unconsumed-second"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
operator|.
name|Builder
argument_list|(
name|xContentRegistry
argument_list|()
argument_list|)
operator|.
name|withParams
argument_list|(
name|params
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RestChannel
name|channel
init|=
operator|new
name|FakeRestChannel
argument_list|(
name|request
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|final
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
name|handler
operator|.
name|handleRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|mock
argument_list|(
name|NodeClient
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"request [/] contains unrecognized parameters: [unconsumed-first], [unconsumed-second]"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|executed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnconsumedParametersDidYouMean
specifier|public
name|void
name|testUnconsumedParametersDidYouMean
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicBoolean
name|executed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|BaseRestHandler
name|handler
init|=
operator|new
name|BaseRestHandler
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|RestChannelConsumer
name|prepareRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|NodeClient
name|client
parameter_list|)
throws|throws
name|IOException
block|{
name|request
operator|.
name|param
argument_list|(
literal|"consumed"
argument_list|)
expr_stmt|;
name|request
operator|.
name|param
argument_list|(
literal|"field"
argument_list|)
expr_stmt|;
name|request
operator|.
name|param
argument_list|(
literal|"tokenizer"
argument_list|)
expr_stmt|;
name|request
operator|.
name|param
argument_list|(
literal|"very_close_to_parameter_1"
argument_list|)
expr_stmt|;
name|request
operator|.
name|param
argument_list|(
literal|"very_close_to_parameter_2"
argument_list|)
expr_stmt|;
return|return
name|channel
lambda|->
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|responseParams
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
literal|"response_param"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
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
literal|"consumed"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"flied"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"respones_param"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"tokenzier"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"very_close_to_parametre"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"very_far_from_every_consumed_parameter"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
operator|.
name|Builder
argument_list|(
name|xContentRegistry
argument_list|()
argument_list|)
operator|.
name|withParams
argument_list|(
name|params
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RestChannel
name|channel
init|=
operator|new
name|FakeRestChannel
argument_list|(
name|request
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|final
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
name|handler
operator|.
name|handleRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|mock
argument_list|(
name|NodeClient
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"request [/] contains unrecognized parameters: "
operator|+
literal|"[flied] -> did you mean [field]?, "
operator|+
literal|"[respones_param] -> did you mean [response_param]?, "
operator|+
literal|"[tokenzier] -> did you mean [tokenizer]?, "
operator|+
literal|"[very_close_to_parametre] -> did you mean any of [very_close_to_parameter_1, very_close_to_parameter_2]?, "
operator|+
literal|"[very_far_from_every_consumed_parameter]"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|executed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnconsumedResponseParameters
specifier|public
name|void
name|testUnconsumedResponseParameters
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicBoolean
name|executed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|BaseRestHandler
name|handler
init|=
operator|new
name|BaseRestHandler
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|RestChannelConsumer
name|prepareRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|NodeClient
name|client
parameter_list|)
throws|throws
name|IOException
block|{
name|request
operator|.
name|param
argument_list|(
literal|"consumed"
argument_list|)
expr_stmt|;
return|return
name|channel
lambda|->
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|responseParams
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
literal|"response_param"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
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
literal|"consumed"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"response_param"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
operator|.
name|Builder
argument_list|(
name|xContentRegistry
argument_list|()
argument_list|)
operator|.
name|withParams
argument_list|(
name|params
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RestChannel
name|channel
init|=
operator|new
name|FakeRestChannel
argument_list|(
name|request
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|mock
argument_list|(
name|NodeClient
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|executed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultResponseParameters
specifier|public
name|void
name|testDefaultResponseParameters
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicBoolean
name|executed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|BaseRestHandler
name|handler
init|=
operator|new
name|BaseRestHandler
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|RestChannelConsumer
name|prepareRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|NodeClient
name|client
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|channel
lambda|->
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
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
literal|"format"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"filter_path"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"pretty"
argument_list|,
name|randomFrom
argument_list|(
literal|"true"
argument_list|,
literal|"false"
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"human"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
operator|.
name|Builder
argument_list|(
name|xContentRegistry
argument_list|()
argument_list|)
operator|.
name|withParams
argument_list|(
name|params
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RestChannel
name|channel
init|=
operator|new
name|FakeRestChannel
argument_list|(
name|request
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|mock
argument_list|(
name|NodeClient
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|executed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCatResponseParameters
specifier|public
name|void
name|testCatResponseParameters
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicBoolean
name|executed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|AbstractCatAction
name|handler
init|=
operator|new
name|AbstractCatAction
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|RestChannelConsumer
name|doCatRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|NodeClient
name|client
parameter_list|)
block|{
return|return
name|channel
lambda|->
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|documentation
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{              }
annotation|@
name|Override
specifier|protected
name|Table
name|getTableWithHeader
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
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
literal|"format"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"h"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"v"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"ts"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"pri"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"bytes"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"time"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
operator|.
name|Builder
argument_list|(
name|xContentRegistry
argument_list|()
argument_list|)
operator|.
name|withParams
argument_list|(
name|params
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RestChannel
name|channel
init|=
operator|new
name|FakeRestChannel
argument_list|(
name|request
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|mock
argument_list|(
name|NodeClient
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|executed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

