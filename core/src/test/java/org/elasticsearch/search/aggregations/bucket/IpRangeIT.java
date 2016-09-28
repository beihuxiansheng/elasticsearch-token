begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
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
name|health
operator|.
name|ClusterHealthStatus
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
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertSearchResponse
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
name|Map
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
name|search
operator|.
name|SearchResponse
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
name|internal
operator|.
name|Nullable
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
name|elasticsearch
operator|.
name|plugins
operator|.
name|ScriptPlugin
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
name|AbstractSearchScript
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
name|ExecutableScript
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
name|NativeScriptFactory
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
name|Script
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
name|ScriptModule
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
operator|.
name|ScriptType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|AggregationBuilders
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|range
operator|.
name|Range
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

begin_class
annotation|@
name|ESIntegTestCase
operator|.
name|SuiteScopeTestCase
DECL|class|IpRangeIT
specifier|public
class|class
name|IpRangeIT
extends|extends
name|ESIntegTestCase
block|{
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
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|DummyScriptPlugin
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setupSuiteScopeCluster
specifier|public
name|void
name|setupSuiteScopeCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"ip"
argument_list|,
literal|"type=ip"
argument_list|,
literal|"ips"
argument_list|,
literal|"type=ip"
argument_list|)
argument_list|)
expr_stmt|;
name|waitForRelocation
argument_list|(
name|ClusterHealthStatus
operator|.
name|GREEN
argument_list|)
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"ip"
argument_list|,
literal|"192.168.1.7"
argument_list|,
literal|"ips"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"192.168.0.13"
argument_list|,
literal|"192.168.1.2"
argument_list|)
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"ip"
argument_list|,
literal|"192.168.1.10"
argument_list|,
literal|"ips"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"192.168.1.25"
argument_list|,
literal|"192.168.1.28"
argument_list|)
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"ip"
argument_list|,
literal|"2001:db8::ff00:42:8329"
argument_list|,
literal|"ips"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"2001:db8::ff00:42:8329"
argument_list|,
literal|"2001:db8::ff00:42:8380"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"idx_unmapped"
argument_list|)
argument_list|)
expr_stmt|;
name|waitForRelocation
argument_list|(
name|ClusterHealthStatus
operator|.
name|GREEN
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
DECL|method|testSingleValuedField
specifier|public
name|void
name|testSingleValuedField
parameter_list|()
block|{
name|SearchResponse
name|rsp
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|ipRange
argument_list|(
literal|"my_range"
argument_list|)
operator|.
name|field
argument_list|(
literal|"ip"
argument_list|)
operator|.
name|addUnboundedTo
argument_list|(
literal|"192.168.1.0"
argument_list|)
operator|.
name|addRange
argument_list|(
literal|"192.168.1.0"
argument_list|,
literal|"192.168.1.10"
argument_list|)
operator|.
name|addUnboundedFrom
argument_list|(
literal|"192.168.1.10"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
name|Range
name|range
init|=
name|rsp
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"my_range"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket1
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|bucket1
operator|.
name|getFrom
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.0"
argument_list|,
name|bucket1
operator|.
name|getTo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bucket1
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket2
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.0"
argument_list|,
name|bucket2
operator|.
name|getFrom
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.10"
argument_list|,
name|bucket2
operator|.
name|getTo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bucket2
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket3
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.10"
argument_list|,
name|bucket3
operator|.
name|getFrom
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|bucket3
operator|.
name|getTo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|bucket3
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiValuedField
specifier|public
name|void
name|testMultiValuedField
parameter_list|()
block|{
name|SearchResponse
name|rsp
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|ipRange
argument_list|(
literal|"my_range"
argument_list|)
operator|.
name|field
argument_list|(
literal|"ips"
argument_list|)
operator|.
name|addUnboundedTo
argument_list|(
literal|"192.168.1.0"
argument_list|)
operator|.
name|addRange
argument_list|(
literal|"192.168.1.0"
argument_list|,
literal|"192.168.1.10"
argument_list|)
operator|.
name|addUnboundedFrom
argument_list|(
literal|"192.168.1.10"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
name|Range
name|range
init|=
name|rsp
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"my_range"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket1
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|bucket1
operator|.
name|getFrom
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.0"
argument_list|,
name|bucket1
operator|.
name|getTo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bucket1
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket2
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.0"
argument_list|,
name|bucket2
operator|.
name|getFrom
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.10"
argument_list|,
name|bucket2
operator|.
name|getTo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bucket2
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket3
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.10"
argument_list|,
name|bucket3
operator|.
name|getFrom
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|bucket3
operator|.
name|getTo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|bucket3
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testIpMask
specifier|public
name|void
name|testIpMask
parameter_list|()
block|{
name|SearchResponse
name|rsp
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|ipRange
argument_list|(
literal|"my_range"
argument_list|)
operator|.
name|field
argument_list|(
literal|"ips"
argument_list|)
operator|.
name|addMaskRange
argument_list|(
literal|"::/0"
argument_list|)
operator|.
name|addMaskRange
argument_list|(
literal|"0.0.0.0/0"
argument_list|)
operator|.
name|addMaskRange
argument_list|(
literal|"2001:db8::/64"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
name|Range
name|range
init|=
name|rsp
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"my_range"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket1
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"::/0"
argument_list|,
name|bucket1
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|bucket1
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket2
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"0.0.0.0/0"
argument_list|,
name|bucket2
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|bucket2
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket3
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2001:db8::/64"
argument_list|,
name|bucket3
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bucket3
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPartiallyUnmapped
specifier|public
name|void
name|testPartiallyUnmapped
parameter_list|()
block|{
name|SearchResponse
name|rsp
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|,
literal|"idx_unmapped"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|ipRange
argument_list|(
literal|"my_range"
argument_list|)
operator|.
name|field
argument_list|(
literal|"ip"
argument_list|)
operator|.
name|addUnboundedTo
argument_list|(
literal|"192.168.1.0"
argument_list|)
operator|.
name|addRange
argument_list|(
literal|"192.168.1.0"
argument_list|,
literal|"192.168.1.10"
argument_list|)
operator|.
name|addUnboundedFrom
argument_list|(
literal|"192.168.1.10"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
name|Range
name|range
init|=
name|rsp
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"my_range"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket1
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|bucket1
operator|.
name|getFrom
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.0"
argument_list|,
name|bucket1
operator|.
name|getTo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bucket1
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket2
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.0"
argument_list|,
name|bucket2
operator|.
name|getFrom
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.10"
argument_list|,
name|bucket2
operator|.
name|getTo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bucket2
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket3
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.10"
argument_list|,
name|bucket3
operator|.
name|getFrom
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|bucket3
operator|.
name|getTo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|bucket3
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnmapped
specifier|public
name|void
name|testUnmapped
parameter_list|()
block|{
name|SearchResponse
name|rsp
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx_unmapped"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|ipRange
argument_list|(
literal|"my_range"
argument_list|)
operator|.
name|field
argument_list|(
literal|"ip"
argument_list|)
operator|.
name|addUnboundedTo
argument_list|(
literal|"192.168.1.0"
argument_list|)
operator|.
name|addRange
argument_list|(
literal|"192.168.1.0"
argument_list|,
literal|"192.168.1.10"
argument_list|)
operator|.
name|addUnboundedFrom
argument_list|(
literal|"192.168.1.10"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
name|Range
name|range
init|=
name|rsp
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"my_range"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket1
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|bucket1
operator|.
name|getFrom
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.0"
argument_list|,
name|bucket1
operator|.
name|getTo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bucket1
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket2
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.0"
argument_list|,
name|bucket2
operator|.
name|getFrom
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.10"
argument_list|,
name|bucket2
operator|.
name|getTo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bucket2
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|Range
operator|.
name|Bucket
name|bucket3
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"192.168.1.10"
argument_list|,
name|bucket3
operator|.
name|getFrom
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|bucket3
operator|.
name|getTo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bucket3
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRejectsScript
specifier|public
name|void
name|testRejectsScript
parameter_list|()
block|{
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
literal|"idx"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|ipRange
argument_list|(
literal|"my_range"
argument_list|)
operator|.
name|script
argument_list|(
operator|new
name|Script
argument_list|(
name|DummyScript
operator|.
name|NAME
argument_list|,
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"native"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
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
literal|"[ip_range] does not support scripts"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRejectsValueScript
specifier|public
name|void
name|testRejectsValueScript
parameter_list|()
block|{
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
literal|"idx"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|ipRange
argument_list|(
literal|"my_range"
argument_list|)
operator|.
name|field
argument_list|(
literal|"ip"
argument_list|)
operator|.
name|script
argument_list|(
operator|new
name|Script
argument_list|(
name|DummyScript
operator|.
name|NAME
argument_list|,
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"native"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
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
literal|"[ip_range] does not support scripts"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|DummyScriptPlugin
specifier|public
specifier|static
class|class
name|DummyScriptPlugin
extends|extends
name|Plugin
implements|implements
name|ScriptPlugin
block|{
annotation|@
name|Override
DECL|method|getNativeScripts
specifier|public
name|List
argument_list|<
name|NativeScriptFactory
argument_list|>
name|getNativeScripts
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|DummyScriptFactory
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|DummyScriptFactory
specifier|public
specifier|static
class|class
name|DummyScriptFactory
implements|implements
name|NativeScriptFactory
block|{
DECL|method|DummyScriptFactory
specifier|public
name|DummyScriptFactory
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|newScript
specifier|public
name|ExecutableScript
name|newScript
parameter_list|(
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
return|return
operator|new
name|DummyScript
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|DummyScript
operator|.
name|NAME
return|;
block|}
block|}
DECL|class|DummyScript
specifier|private
specifier|static
class|class
name|DummyScript
extends|extends
name|AbstractSearchScript
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"dummy"
decl_stmt|;
annotation|@
name|Override
DECL|method|run
specifier|public
name|Object
name|run
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit
