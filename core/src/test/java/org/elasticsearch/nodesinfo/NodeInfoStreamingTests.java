begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.nodesinfo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|nodesinfo
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Build
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|NodeInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|PluginsAndModules
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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|StreamInput
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
name|transport
operator|.
name|BoundTransportAddress
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
name|transport
operator|.
name|TransportAddress
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
name|unit
operator|.
name|ByteSizeValue
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
name|ToXContent
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
name|http
operator|.
name|HttpInfo
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
name|IngestInfo
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
name|ProcessorInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|os
operator|.
name|OsInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|process
operator|.
name|ProcessInfo
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
name|PluginInfo
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
name|VersionUtils
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
name|threadpool
operator|.
name|ThreadPoolInfo
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
name|TransportInfo
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
name|emptySet
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
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
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
name|IsEqual
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|NodeInfoStreamingTests
specifier|public
class|class
name|NodeInfoStreamingTests
extends|extends
name|ESTestCase
block|{
DECL|method|testNodeInfoStreaming
specifier|public
name|void
name|testNodeInfoStreaming
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeInfo
name|nodeInfo
init|=
name|createNodeInfo
argument_list|()
decl_stmt|;
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|nodeInfo
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
init|)
block|{
name|NodeInfo
name|readNodeInfo
init|=
name|NodeInfo
operator|.
name|readNodeInfo
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertExpectedUnchanged
argument_list|(
name|nodeInfo
argument_list|,
name|readNodeInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// checks all properties that are expected to be unchanged.
comment|// Once we start changing them between versions this method has to be changed as well
DECL|method|assertExpectedUnchanged
specifier|private
name|void
name|assertExpectedUnchanged
parameter_list|(
name|NodeInfo
name|nodeInfo
parameter_list|,
name|NodeInfo
name|readNodeInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|assertThat
argument_list|(
name|nodeInfo
operator|.
name|getBuild
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readNodeInfo
operator|.
name|getBuild
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|nodeInfo
operator|.
name|getHostname
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readNodeInfo
operator|.
name|getHostname
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|nodeInfo
operator|.
name|getVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readNodeInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|compareJsonOutput
argument_list|(
name|nodeInfo
operator|.
name|getHttp
argument_list|()
argument_list|,
name|readNodeInfo
operator|.
name|getHttp
argument_list|()
argument_list|)
expr_stmt|;
name|compareJsonOutput
argument_list|(
name|nodeInfo
operator|.
name|getJvm
argument_list|()
argument_list|,
name|readNodeInfo
operator|.
name|getJvm
argument_list|()
argument_list|)
expr_stmt|;
name|compareJsonOutput
argument_list|(
name|nodeInfo
operator|.
name|getProcess
argument_list|()
argument_list|,
name|readNodeInfo
operator|.
name|getProcess
argument_list|()
argument_list|)
expr_stmt|;
name|compareJsonOutput
argument_list|(
name|nodeInfo
operator|.
name|getSettings
argument_list|()
argument_list|,
name|readNodeInfo
operator|.
name|getSettings
argument_list|()
argument_list|)
expr_stmt|;
name|compareJsonOutput
argument_list|(
name|nodeInfo
operator|.
name|getThreadPool
argument_list|()
argument_list|,
name|readNodeInfo
operator|.
name|getThreadPool
argument_list|()
argument_list|)
expr_stmt|;
name|compareJsonOutput
argument_list|(
name|nodeInfo
operator|.
name|getTransport
argument_list|()
argument_list|,
name|readNodeInfo
operator|.
name|getTransport
argument_list|()
argument_list|)
expr_stmt|;
name|compareJsonOutput
argument_list|(
name|nodeInfo
operator|.
name|getNode
argument_list|()
argument_list|,
name|readNodeInfo
operator|.
name|getNode
argument_list|()
argument_list|)
expr_stmt|;
name|compareJsonOutput
argument_list|(
name|nodeInfo
operator|.
name|getOs
argument_list|()
argument_list|,
name|readNodeInfo
operator|.
name|getOs
argument_list|()
argument_list|)
expr_stmt|;
name|compareJsonOutput
argument_list|(
name|nodeInfo
operator|.
name|getPlugins
argument_list|()
argument_list|,
name|readNodeInfo
operator|.
name|getPlugins
argument_list|()
argument_list|)
expr_stmt|;
name|compareJsonOutput
argument_list|(
name|nodeInfo
operator|.
name|getIngest
argument_list|()
argument_list|,
name|readNodeInfo
operator|.
name|getIngest
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|compareJsonOutput
specifier|private
name|void
name|compareJsonOutput
parameter_list|(
name|ToXContent
name|param1
parameter_list|,
name|ToXContent
name|param2
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|param1
operator|==
literal|null
condition|)
block|{
name|assertNull
argument_list|(
name|param2
argument_list|)
expr_stmt|;
return|return;
block|}
name|ToXContent
operator|.
name|Params
name|params
init|=
name|ToXContent
operator|.
name|EMPTY_PARAMS
decl_stmt|;
name|XContentBuilder
name|param1Builder
init|=
name|jsonBuilder
argument_list|()
decl_stmt|;
name|param1Builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|param1
operator|.
name|toXContent
argument_list|(
name|param1Builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|param1Builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|XContentBuilder
name|param2Builder
init|=
name|jsonBuilder
argument_list|()
decl_stmt|;
name|param2Builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|param2
operator|.
name|toXContent
argument_list|(
name|param2Builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|param2Builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|param1Builder
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|param2Builder
operator|.
name|string
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createNodeInfo
specifier|private
specifier|static
name|NodeInfo
name|createNodeInfo
parameter_list|()
block|{
name|Build
name|build
init|=
name|Build
operator|.
name|CURRENT
decl_stmt|;
name|DiscoveryNode
name|node
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"test_node"
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|emptySet
argument_list|()
argument_list|,
name|VersionUtils
operator|.
name|randomVersion
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Settings
name|settings
init|=
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"test"
argument_list|,
literal|"setting"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OsInfo
name|osInfo
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|int
name|availableProcessors
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|64
argument_list|)
decl_stmt|;
name|int
name|allocatedProcessors
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|availableProcessors
argument_list|)
decl_stmt|;
name|long
name|refreshInterval
init|=
name|randomBoolean
argument_list|()
condition|?
operator|-
literal|1
else|:
name|randomNonNegativeLong
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|arch
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|version
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|osInfo
operator|=
operator|new
name|OsInfo
argument_list|(
name|refreshInterval
argument_list|,
name|availableProcessors
argument_list|,
name|allocatedProcessors
argument_list|,
name|name
argument_list|,
name|arch
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
name|ProcessInfo
name|process
init|=
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
operator|new
name|ProcessInfo
argument_list|(
name|randomInt
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|randomNonNegativeLong
argument_list|()
argument_list|)
decl_stmt|;
name|JvmInfo
name|jvm
init|=
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
decl_stmt|;
name|ThreadPoolInfo
name|threadPoolInfo
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|int
name|numThreadPools
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ThreadPool
operator|.
name|Info
argument_list|>
name|threadPoolInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numThreadPools
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
name|numThreadPools
condition|;
name|i
operator|++
control|)
block|{
name|threadPoolInfos
operator|.
name|add
argument_list|(
operator|new
name|ThreadPool
operator|.
name|Info
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomFrom
argument_list|(
name|ThreadPool
operator|.
name|ThreadPoolType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|,
name|randomInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|threadPoolInfo
operator|=
operator|new
name|ThreadPoolInfo
argument_list|(
name|threadPoolInfos
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|BoundTransportAddress
argument_list|>
name|profileAddresses
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|BoundTransportAddress
name|dummyBoundTransportAddress
init|=
operator|new
name|BoundTransportAddress
argument_list|(
operator|new
name|TransportAddress
index|[]
block|{
name|buildNewFakeTransportAddress
argument_list|()
block|}
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|)
decl_stmt|;
name|profileAddresses
operator|.
name|put
argument_list|(
literal|"test_address"
argument_list|,
name|dummyBoundTransportAddress
argument_list|)
expr_stmt|;
name|TransportInfo
name|transport
init|=
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
operator|new
name|TransportInfo
argument_list|(
name|dummyBoundTransportAddress
argument_list|,
name|profileAddresses
argument_list|)
decl_stmt|;
name|HttpInfo
name|httpInfo
init|=
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
operator|new
name|HttpInfo
argument_list|(
name|dummyBoundTransportAddress
argument_list|,
name|randomLong
argument_list|()
argument_list|)
decl_stmt|;
name|PluginsAndModules
name|pluginsAndModules
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|int
name|numPlugins
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|plugins
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|numPlugins
condition|;
name|i
operator|++
control|)
block|{
name|plugins
operator|.
name|add
argument_list|(
operator|new
name|PluginInfo
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|numModules
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|numModules
condition|;
name|i
operator|++
control|)
block|{
name|modules
operator|.
name|add
argument_list|(
operator|new
name|PluginInfo
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|pluginsAndModules
operator|=
operator|new
name|PluginsAndModules
argument_list|(
name|plugins
argument_list|,
name|modules
argument_list|)
expr_stmt|;
block|}
name|IngestInfo
name|ingestInfo
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|int
name|numProcessors
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ProcessorInfo
argument_list|>
name|processors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numProcessors
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
name|numProcessors
condition|;
name|i
operator|++
control|)
block|{
name|processors
operator|.
name|add
argument_list|(
operator|new
name|ProcessorInfo
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ingestInfo
operator|=
operator|new
name|IngestInfo
argument_list|(
name|processors
argument_list|)
expr_stmt|;
block|}
name|ByteSizeValue
name|indexingBuffer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
comment|// pick a random long that sometimes exceeds an int:
name|indexingBuffer
operator|=
operator|new
name|ByteSizeValue
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
operator|&
operator|(
operator|(
literal|1L
operator|<<
literal|40
operator|)
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|NodeInfo
argument_list|(
name|VersionUtils
operator|.
name|randomVersion
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|build
argument_list|,
name|node
argument_list|,
name|settings
argument_list|,
name|osInfo
argument_list|,
name|process
argument_list|,
name|jvm
argument_list|,
name|threadPoolInfo
argument_list|,
name|transport
argument_list|,
name|httpInfo
argument_list|,
name|pluginsAndModules
argument_list|,
name|ingestInfo
argument_list|,
name|indexingBuffer
argument_list|)
return|;
block|}
block|}
end_class

end_unit

