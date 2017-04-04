begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.admin.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
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
name|RestController
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
name|RestRequest
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
name|HashSet
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
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
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
DECL|class|RestNodesStatsActionTests
specifier|public
class|class
name|RestNodesStatsActionTests
extends|extends
name|ESTestCase
block|{
DECL|field|action
specifier|private
name|RestNodesStatsAction
name|action
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|action
operator|=
operator|new
name|RestNodesStatsAction
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|RestController
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnrecognizedMetric
specifier|public
name|void
name|testUnrecognizedMetric
parameter_list|()
throws|throws
name|IOException
block|{
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
specifier|final
name|String
name|metric
init|=
name|randomAlphaOfLength
argument_list|(
literal|64
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"metric"
argument_list|,
name|metric
argument_list|)
expr_stmt|;
specifier|final
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
name|withPath
argument_list|(
literal|"/_nodes/stats"
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
name|action
operator|.
name|prepareRequest
argument_list|(
name|request
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
literal|"request [/_nodes/stats] contains unrecognized metric: ["
operator|+
name|metric
operator|+
literal|"]"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnrecognizedMetricDidYouMean
specifier|public
name|void
name|testUnrecognizedMetricDidYouMean
parameter_list|()
throws|throws
name|IOException
block|{
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
literal|"metric"
argument_list|,
literal|"os,transprot,unrecognized"
argument_list|)
expr_stmt|;
specifier|final
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
name|withPath
argument_list|(
literal|"/_nodes/stats"
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
name|action
operator|.
name|prepareRequest
argument_list|(
name|request
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
literal|"request [/_nodes/stats] contains unrecognized metrics: [transprot] -> did you mean [transport]?, [unrecognized]"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAllRequestWithOtherMetrics
specifier|public
name|void
name|testAllRequestWithOtherMetrics
parameter_list|()
throws|throws
name|IOException
block|{
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
specifier|final
name|String
name|metric
init|=
name|randomSubsetOf
argument_list|(
literal|1
argument_list|,
name|RestNodesStatsAction
operator|.
name|METRICS
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"metric"
argument_list|,
literal|"_all,"
operator|+
name|metric
argument_list|)
expr_stmt|;
specifier|final
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
name|withPath
argument_list|(
literal|"/_nodes/stats"
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
name|action
operator|.
name|prepareRequest
argument_list|(
name|request
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
literal|"request [/_nodes/stats] contains _all and individual metrics [_all,"
operator|+
name|metric
operator|+
literal|"]"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnrecognizedIndexMetric
specifier|public
name|void
name|testUnrecognizedIndexMetric
parameter_list|()
block|{
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
literal|"metric"
argument_list|,
literal|"indices"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|indexMetric
init|=
name|randomAlphaOfLength
argument_list|(
literal|64
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"index_metric"
argument_list|,
name|indexMetric
argument_list|)
expr_stmt|;
specifier|final
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
name|withPath
argument_list|(
literal|"/_nodes/stats"
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
name|action
operator|.
name|prepareRequest
argument_list|(
name|request
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
literal|"request [/_nodes/stats] contains unrecognized index metric: ["
operator|+
name|indexMetric
operator|+
literal|"]"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnrecognizedIndexMetricDidYouMean
specifier|public
name|void
name|testUnrecognizedIndexMetricDidYouMean
parameter_list|()
block|{
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
literal|"metric"
argument_list|,
literal|"indices"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"index_metric"
argument_list|,
literal|"indexing,stroe,unrecognized"
argument_list|)
expr_stmt|;
specifier|final
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
name|withPath
argument_list|(
literal|"/_nodes/stats"
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
name|action
operator|.
name|prepareRequest
argument_list|(
name|request
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
literal|"request [/_nodes/stats] contains unrecognized index metrics: [stroe] -> did you mean [store]?, [unrecognized]"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndexMetricsRequestWithoutIndicesMetric
specifier|public
name|void
name|testIndexMetricsRequestWithoutIndicesMetric
parameter_list|()
throws|throws
name|IOException
block|{
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
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|metrics
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|RestNodesStatsAction
operator|.
name|METRICS
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|metrics
operator|.
name|remove
argument_list|(
literal|"indices"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"metric"
argument_list|,
name|randomSubsetOf
argument_list|(
literal|1
argument_list|,
name|metrics
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|indexMetric
init|=
name|randomSubsetOf
argument_list|(
literal|1
argument_list|,
name|RestNodesStatsAction
operator|.
name|FLAGS
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"index_metric"
argument_list|,
name|indexMetric
argument_list|)
expr_stmt|;
specifier|final
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
name|withPath
argument_list|(
literal|"/_nodes/stats"
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
name|action
operator|.
name|prepareRequest
argument_list|(
name|request
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
literal|"request [/_nodes/stats] contains index metrics ["
operator|+
name|indexMetric
operator|+
literal|"] but indices stats not requested"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndexMetricsRequestOnAllRequest
specifier|public
name|void
name|testIndexMetricsRequestOnAllRequest
parameter_list|()
throws|throws
name|IOException
block|{
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
literal|"metric"
argument_list|,
literal|"_all"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|indexMetric
init|=
name|randomSubsetOf
argument_list|(
literal|1
argument_list|,
name|RestNodesStatsAction
operator|.
name|FLAGS
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"index_metric"
argument_list|,
name|indexMetric
argument_list|)
expr_stmt|;
specifier|final
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
name|withPath
argument_list|(
literal|"/_nodes/stats"
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
name|action
operator|.
name|prepareRequest
argument_list|(
name|request
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
literal|"request [/_nodes/stats] contains index metrics ["
operator|+
name|indexMetric
operator|+
literal|"] but all stats requested"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

