begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
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
name|metadata
operator|.
name|IndexMetaData
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
name|ESIntegTestCase
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAcked
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

begin_class
DECL|class|TransportSearchIT
specifier|public
class|class
name|TransportSearchIT
extends|extends
name|ESIntegTestCase
block|{
DECL|method|testShardCountLimit
specifier|public
name|void
name|testShardCountLimit
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
specifier|final
name|int
name|numPrimaries1
init|=
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numPrimaries2
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
name|numPrimaries1
argument_list|)
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
name|numPrimaries2
argument_list|)
argument_list|)
expr_stmt|;
comment|// no exception
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|()
operator|.
name|setTransientSettings
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
name|TransportSearchAction
operator|.
name|SHARD_COUNT_LIMIT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|numPrimaries1
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"Trying to query "
operator|+
name|numPrimaries1
operator|+
literal|" shards, which is over the limit of "
operator|+
operator|(
name|numPrimaries1
operator|-
literal|1
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|()
operator|.
name|setTransientSettings
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
name|TransportSearchAction
operator|.
name|SHARD_COUNT_LIMIT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|numPrimaries1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// no exception
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|get
argument_list|()
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
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"Trying to query "
operator|+
operator|(
name|numPrimaries1
operator|+
name|numPrimaries2
operator|)
operator|+
literal|" shards, which is over the limit of "
operator|+
name|numPrimaries1
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|()
operator|.
name|setTransientSettings
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
name|TransportSearchAction
operator|.
name|SHARD_COUNT_LIMIT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

