begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|transport
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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
name|inject
operator|.
name|Inject
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
name|io
operator|.
name|stream
operator|.
name|NamedWriteableRegistry
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
name|test
operator|.
name|ElasticsearchIntegrationTest
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
name|VersionUtils
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
name|hamcrest
operator|.
name|ElasticsearchAssertions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|local
operator|.
name|LocalTransport
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
name|Random
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AssertingLocalTransport
specifier|public
class|class
name|AssertingLocalTransport
extends|extends
name|LocalTransport
block|{
DECL|field|ASSERTING_TRANSPORT_MIN_VERSION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|ASSERTING_TRANSPORT_MIN_VERSION_KEY
init|=
literal|"transport.asserting.version.min"
decl_stmt|;
DECL|field|ASSERTING_TRANSPORT_MAX_VERSION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|ASSERTING_TRANSPORT_MAX_VERSION_KEY
init|=
literal|"transport.asserting.version.max"
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|minVersion
specifier|private
specifier|final
name|Version
name|minVersion
decl_stmt|;
DECL|field|maxVersion
specifier|private
specifier|final
name|Version
name|maxVersion
decl_stmt|;
annotation|@
name|Inject
DECL|method|AssertingLocalTransport
specifier|public
name|AssertingLocalTransport
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|Version
name|version
parameter_list|,
name|NamedWriteableRegistry
name|namedWriteableRegistry
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|,
name|version
argument_list|,
name|namedWriteableRegistry
argument_list|)
expr_stmt|;
specifier|final
name|long
name|seed
init|=
name|settings
operator|.
name|getAsLong
argument_list|(
name|ElasticsearchIntegrationTest
operator|.
name|SETTING_INDEX_SEED
argument_list|,
literal|0l
argument_list|)
decl_stmt|;
name|random
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|minVersion
operator|=
name|settings
operator|.
name|getAsVersion
argument_list|(
name|ASSERTING_TRANSPORT_MIN_VERSION_KEY
argument_list|,
name|Version
operator|.
name|V_0_18_0
argument_list|)
expr_stmt|;
name|maxVersion
operator|=
name|settings
operator|.
name|getAsVersion
argument_list|(
name|ASSERTING_TRANSPORT_MAX_VERSION_KEY
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleParsedResponse
specifier|protected
name|void
name|handleParsedResponse
parameter_list|(
specifier|final
name|TransportResponse
name|response
parameter_list|,
specifier|final
name|TransportResponseHandler
name|handler
parameter_list|)
block|{
name|ElasticsearchAssertions
operator|.
name|assertVersionSerializable
argument_list|(
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|,
name|minVersion
argument_list|,
name|maxVersion
argument_list|)
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|super
operator|.
name|handleParsedResponse
argument_list|(
name|response
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendRequest
specifier|public
name|void
name|sendRequest
parameter_list|(
specifier|final
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|long
name|requestId
parameter_list|,
specifier|final
name|String
name|action
parameter_list|,
specifier|final
name|TransportRequest
name|request
parameter_list|,
name|TransportRequestOptions
name|options
parameter_list|)
throws|throws
name|IOException
throws|,
name|TransportException
block|{
name|ElasticsearchAssertions
operator|.
name|assertVersionSerializable
argument_list|(
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|,
name|minVersion
argument_list|,
name|maxVersion
argument_list|)
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|super
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|requestId
argument_list|,
name|action
argument_list|,
name|request
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

