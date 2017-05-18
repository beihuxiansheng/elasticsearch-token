begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.tribe
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|tribe
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|NamedDiff
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
name|metadata
operator|.
name|MetaData
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
name|network
operator|.
name|NetworkModule
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
name|util
operator|.
name|set
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
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
name|ScriptService
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
name|TestCustomMetaData
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
name|Arrays
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
name|EnumSet
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
DECL|class|TribeServiceTests
specifier|public
class|class
name|TribeServiceTests
extends|extends
name|ESTestCase
block|{
DECL|method|testMinimalSettings
specifier|public
name|void
name|testMinimalSettings
parameter_list|()
block|{
name|Settings
name|globalSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
literal|"nodename"
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
literal|"some/path"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Settings
name|clientSettings
init|=
name|TribeService
operator|.
name|buildClientSettings
argument_list|(
literal|"tribe1"
argument_list|,
literal|"parent_id"
argument_list|,
name|globalSettings
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"some/path"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"path.home"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nodename/tribe1"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"node.name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tribe1"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"tribe.name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|NetworkModule
operator|.
name|HTTP_ENABLED
operator|.
name|get
argument_list|(
name|clientSettings
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"node.master"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"node.data"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"node.ingest"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"node.local_storage"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3707202549613653169"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"node.id.seed"
argument_list|)
argument_list|)
expr_stmt|;
comment|// should be fixed by the parent id and tribe name
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|clientSettings
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEnvironmentSettings
specifier|public
name|void
name|testEnvironmentSettings
parameter_list|()
block|{
name|Settings
name|globalSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
literal|"nodename"
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
literal|"some/path"
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.conf"
argument_list|,
literal|"conf/path"
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.logs"
argument_list|,
literal|"logs/path"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Settings
name|clientSettings
init|=
name|TribeService
operator|.
name|buildClientSettings
argument_list|(
literal|"tribe1"
argument_list|,
literal|"parent_id"
argument_list|,
name|globalSettings
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"some/path"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"path.home"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"conf/path"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"path.conf"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"logs/path"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"path.logs"
argument_list|)
argument_list|)
expr_stmt|;
name|Settings
name|tribeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
literal|"alternate/path"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
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
block|{
name|TribeService
operator|.
name|buildClientSettings
argument_list|(
literal|"tribe1"
argument_list|,
literal|"parent_id"
argument_list|,
name|globalSettings
argument_list|,
name|tribeSettings
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Setting [path.home] not allowed in tribe client"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPassthroughSettings
specifier|public
name|void
name|testPassthroughSettings
parameter_list|()
block|{
name|Settings
name|globalSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
literal|"nodename"
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
literal|"some/path"
argument_list|)
operator|.
name|put
argument_list|(
literal|"network.host"
argument_list|,
literal|"0.0.0.0"
argument_list|)
operator|.
name|put
argument_list|(
literal|"network.bind_host"
argument_list|,
literal|"1.1.1.1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"network.publish_host"
argument_list|,
literal|"2.2.2.2"
argument_list|)
operator|.
name|put
argument_list|(
literal|"transport.host"
argument_list|,
literal|"3.3.3.3"
argument_list|)
operator|.
name|put
argument_list|(
literal|"transport.bind_host"
argument_list|,
literal|"4.4.4.4"
argument_list|)
operator|.
name|put
argument_list|(
literal|"transport.publish_host"
argument_list|,
literal|"5.5.5.5"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Settings
name|clientSettings
init|=
name|TribeService
operator|.
name|buildClientSettings
argument_list|(
literal|"tribe1"
argument_list|,
literal|"parent_id"
argument_list|,
name|globalSettings
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"0.0.0.0"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"network.host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.1.1.1"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"network.bind_host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2.2.2.2"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"network.publish_host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3.3.3.3"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"transport.host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4.4.4.4"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"transport.bind_host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"5.5.5.5"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"transport.publish_host"
argument_list|)
argument_list|)
expr_stmt|;
comment|// per tribe client overrides still work
name|Settings
name|tribeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"network.host"
argument_list|,
literal|"3.3.3.3"
argument_list|)
operator|.
name|put
argument_list|(
literal|"network.bind_host"
argument_list|,
literal|"4.4.4.4"
argument_list|)
operator|.
name|put
argument_list|(
literal|"network.publish_host"
argument_list|,
literal|"5.5.5.5"
argument_list|)
operator|.
name|put
argument_list|(
literal|"transport.host"
argument_list|,
literal|"6.6.6.6"
argument_list|)
operator|.
name|put
argument_list|(
literal|"transport.bind_host"
argument_list|,
literal|"7.7.7.7"
argument_list|)
operator|.
name|put
argument_list|(
literal|"transport.publish_host"
argument_list|,
literal|"8.8.8.8"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|clientSettings
operator|=
name|TribeService
operator|.
name|buildClientSettings
argument_list|(
literal|"tribe1"
argument_list|,
literal|"parent_id"
argument_list|,
name|globalSettings
argument_list|,
name|tribeSettings
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3.3.3.3"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"network.host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4.4.4.4"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"network.bind_host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"5.5.5.5"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"network.publish_host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"6.6.6.6"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"transport.host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"7.7.7.7"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"transport.bind_host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"8.8.8.8"
argument_list|,
name|clientSettings
operator|.
name|get
argument_list|(
literal|"transport.publish_host"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMergeCustomMetaDataSimple
specifier|public
name|void
name|testMergeCustomMetaDataSimple
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|MetaData
operator|.
name|Custom
argument_list|>
name|mergedCustoms
init|=
name|TribeService
operator|.
name|mergeChangedCustomMetaData
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|MergableCustomMetaData1
operator|.
name|TYPE
argument_list|)
argument_list|,
name|s
lambda|->
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|MergableCustomMetaData1
argument_list|(
literal|"data1"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TestCustomMetaData
name|mergedCustom
init|=
operator|(
name|TestCustomMetaData
operator|)
name|mergedCustoms
operator|.
name|get
argument_list|(
name|MergableCustomMetaData1
operator|.
name|TYPE
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|mergedCustom
argument_list|,
name|instanceOf
argument_list|(
name|MergableCustomMetaData1
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mergedCustom
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mergedCustom
operator|.
name|getData
argument_list|()
argument_list|,
literal|"data1"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMergeCustomMetaData
specifier|public
name|void
name|testMergeCustomMetaData
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|MetaData
operator|.
name|Custom
argument_list|>
name|mergedCustoms
init|=
name|TribeService
operator|.
name|mergeChangedCustomMetaData
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|MergableCustomMetaData1
operator|.
name|TYPE
argument_list|)
argument_list|,
name|s
lambda|->
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|MergableCustomMetaData1
argument_list|(
literal|"data1"
argument_list|)
argument_list|,
operator|new
name|MergableCustomMetaData1
argument_list|(
literal|"data2"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TestCustomMetaData
name|mergedCustom
init|=
operator|(
name|TestCustomMetaData
operator|)
name|mergedCustoms
operator|.
name|get
argument_list|(
name|MergableCustomMetaData1
operator|.
name|TYPE
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|mergedCustom
argument_list|,
name|instanceOf
argument_list|(
name|MergableCustomMetaData1
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mergedCustom
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mergedCustom
operator|.
name|getData
argument_list|()
argument_list|,
literal|"data2"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMergeMultipleCustomMetaData
specifier|public
name|void
name|testMergeMultipleCustomMetaData
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TribeService
operator|.
name|MergableCustomMetaData
argument_list|>
argument_list|>
name|inputMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|inputMap
operator|.
name|put
argument_list|(
name|MergableCustomMetaData1
operator|.
name|TYPE
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|MergableCustomMetaData1
argument_list|(
literal|"data10"
argument_list|)
argument_list|,
operator|new
name|MergableCustomMetaData1
argument_list|(
literal|"data11"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|inputMap
operator|.
name|put
argument_list|(
name|MergableCustomMetaData2
operator|.
name|TYPE
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|MergableCustomMetaData2
argument_list|(
literal|"data21"
argument_list|)
argument_list|,
operator|new
name|MergableCustomMetaData2
argument_list|(
literal|"data20"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|MetaData
operator|.
name|Custom
argument_list|>
name|mergedCustoms
init|=
name|TribeService
operator|.
name|mergeChangedCustomMetaData
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|MergableCustomMetaData1
operator|.
name|TYPE
argument_list|,
name|MergableCustomMetaData2
operator|.
name|TYPE
argument_list|)
argument_list|,
name|inputMap
operator|::
name|get
argument_list|)
decl_stmt|;
name|TestCustomMetaData
name|mergedCustom
init|=
operator|(
name|TestCustomMetaData
operator|)
name|mergedCustoms
operator|.
name|get
argument_list|(
name|MergableCustomMetaData1
operator|.
name|TYPE
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|mergedCustom
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mergedCustom
argument_list|,
name|instanceOf
argument_list|(
name|MergableCustomMetaData1
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mergedCustom
operator|.
name|getData
argument_list|()
argument_list|,
literal|"data11"
argument_list|)
expr_stmt|;
name|mergedCustom
operator|=
operator|(
name|TestCustomMetaData
operator|)
name|mergedCustoms
operator|.
name|get
argument_list|(
name|MergableCustomMetaData2
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mergedCustom
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mergedCustom
argument_list|,
name|instanceOf
argument_list|(
name|MergableCustomMetaData2
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mergedCustom
operator|.
name|getData
argument_list|()
argument_list|,
literal|"data21"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMergeCustomMetaDataFromMany
specifier|public
name|void
name|testMergeCustomMetaDataFromMany
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TribeService
operator|.
name|MergableCustomMetaData
argument_list|>
argument_list|>
name|inputMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|n
init|=
name|randomIntBetween
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|TribeService
operator|.
name|MergableCustomMetaData
argument_list|>
name|customList1
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
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|customList1
operator|.
name|add
argument_list|(
operator|new
name|MergableCustomMetaData1
argument_list|(
literal|"data1"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|customList1
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|inputMap
operator|.
name|put
argument_list|(
name|MergableCustomMetaData1
operator|.
name|TYPE
argument_list|,
name|customList1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TribeService
operator|.
name|MergableCustomMetaData
argument_list|>
name|customList2
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
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|customList2
operator|.
name|add
argument_list|(
operator|new
name|MergableCustomMetaData2
argument_list|(
literal|"data2"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|customList2
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|inputMap
operator|.
name|put
argument_list|(
name|MergableCustomMetaData2
operator|.
name|TYPE
argument_list|,
name|customList2
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|MetaData
operator|.
name|Custom
argument_list|>
name|mergedCustoms
init|=
name|TribeService
operator|.
name|mergeChangedCustomMetaData
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|MergableCustomMetaData1
operator|.
name|TYPE
argument_list|,
name|MergableCustomMetaData2
operator|.
name|TYPE
argument_list|)
argument_list|,
name|inputMap
operator|::
name|get
argument_list|)
decl_stmt|;
name|TestCustomMetaData
name|mergedCustom
init|=
operator|(
name|TestCustomMetaData
operator|)
name|mergedCustoms
operator|.
name|get
argument_list|(
name|MergableCustomMetaData1
operator|.
name|TYPE
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|mergedCustom
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mergedCustom
argument_list|,
name|instanceOf
argument_list|(
name|MergableCustomMetaData1
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mergedCustom
operator|.
name|getData
argument_list|()
argument_list|,
literal|"data1"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
name|mergedCustom
operator|=
operator|(
name|TestCustomMetaData
operator|)
name|mergedCustoms
operator|.
name|get
argument_list|(
name|MergableCustomMetaData2
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mergedCustom
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mergedCustom
argument_list|,
name|instanceOf
argument_list|(
name|MergableCustomMetaData2
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mergedCustom
operator|.
name|getData
argument_list|()
argument_list|,
literal|"data2"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|MergableCustomMetaData1
specifier|static
class|class
name|MergableCustomMetaData1
extends|extends
name|TestCustomMetaData
implements|implements
name|TribeService
operator|.
name|MergableCustomMetaData
argument_list|<
name|MergableCustomMetaData1
argument_list|>
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"custom_md_1"
decl_stmt|;
DECL|method|MergableCustomMetaData1
specifier|protected
name|MergableCustomMetaData1
parameter_list|(
name|String
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
DECL|method|readFrom
specifier|public
specifier|static
name|MergableCustomMetaData1
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readFrom
argument_list|(
name|MergableCustomMetaData1
operator|::
operator|new
argument_list|,
name|in
argument_list|)
return|;
block|}
DECL|method|readDiffFrom
specifier|public
specifier|static
name|NamedDiff
argument_list|<
name|MetaData
operator|.
name|Custom
argument_list|>
name|readDiffFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readDiffFrom
argument_list|(
name|TYPE
argument_list|,
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|context
specifier|public
name|EnumSet
argument_list|<
name|MetaData
operator|.
name|XContentContext
argument_list|>
name|context
parameter_list|()
block|{
return|return
name|EnumSet
operator|.
name|of
argument_list|(
name|MetaData
operator|.
name|XContentContext
operator|.
name|GATEWAY
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|MergableCustomMetaData1
name|merge
parameter_list|(
name|MergableCustomMetaData1
name|other
parameter_list|)
block|{
return|return
operator|(
name|getData
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getData
argument_list|()
argument_list|)
operator|>=
literal|0
operator|)
condition|?
name|this
else|:
name|other
return|;
block|}
block|}
DECL|class|MergableCustomMetaData2
specifier|static
class|class
name|MergableCustomMetaData2
extends|extends
name|TestCustomMetaData
implements|implements
name|TribeService
operator|.
name|MergableCustomMetaData
argument_list|<
name|MergableCustomMetaData2
argument_list|>
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"custom_md_2"
decl_stmt|;
DECL|method|MergableCustomMetaData2
specifier|protected
name|MergableCustomMetaData2
parameter_list|(
name|String
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
DECL|method|readFrom
specifier|public
specifier|static
name|MergableCustomMetaData2
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readFrom
argument_list|(
name|MergableCustomMetaData2
operator|::
operator|new
argument_list|,
name|in
argument_list|)
return|;
block|}
DECL|method|readDiffFrom
specifier|public
specifier|static
name|NamedDiff
argument_list|<
name|MetaData
operator|.
name|Custom
argument_list|>
name|readDiffFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readDiffFrom
argument_list|(
name|TYPE
argument_list|,
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|context
specifier|public
name|EnumSet
argument_list|<
name|MetaData
operator|.
name|XContentContext
argument_list|>
name|context
parameter_list|()
block|{
return|return
name|EnumSet
operator|.
name|of
argument_list|(
name|MetaData
operator|.
name|XContentContext
operator|.
name|GATEWAY
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|MergableCustomMetaData2
name|merge
parameter_list|(
name|MergableCustomMetaData2
name|other
parameter_list|)
block|{
return|return
operator|(
name|getData
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getData
argument_list|()
argument_list|)
operator|>=
literal|0
operator|)
condition|?
name|this
else|:
name|other
return|;
block|}
block|}
block|}
end_class

end_unit

