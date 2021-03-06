begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.stats
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|stats
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|stats
operator|.
name|SearchStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|stats
operator|.
name|SearchStats
operator|.
name|Stats
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

begin_class
DECL|class|SearchStatsUnitTests
specifier|public
class|class
name|SearchStatsUnitTests
extends|extends
name|ESTestCase
block|{
comment|// https://github.com/elastic/elasticsearch/issues/7644
DECL|method|testShardLevelSearchGroupStats
specifier|public
name|void
name|testShardLevelSearchGroupStats
parameter_list|()
throws|throws
name|Exception
block|{
comment|// let's create two dummy search stats with groups
name|Map
argument_list|<
name|String
argument_list|,
name|Stats
argument_list|>
name|groupStats1
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Stats
argument_list|>
name|groupStats2
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|groupStats2
operator|.
name|put
argument_list|(
literal|"group1"
argument_list|,
operator|new
name|Stats
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|SearchStats
name|searchStats1
init|=
operator|new
name|SearchStats
argument_list|(
operator|new
name|Stats
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
name|groupStats1
argument_list|)
decl_stmt|;
name|SearchStats
name|searchStats2
init|=
operator|new
name|SearchStats
argument_list|(
operator|new
name|Stats
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
name|groupStats2
argument_list|)
decl_stmt|;
comment|// adding these two search stats and checking group stats are correct
name|searchStats1
operator|.
name|add
argument_list|(
name|searchStats2
argument_list|)
expr_stmt|;
name|assertStats
argument_list|(
name|groupStats1
operator|.
name|get
argument_list|(
literal|"group1"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// another call, adding again ...
name|searchStats1
operator|.
name|add
argument_list|(
name|searchStats2
argument_list|)
expr_stmt|;
name|assertStats
argument_list|(
name|groupStats1
operator|.
name|get
argument_list|(
literal|"group1"
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// making sure stats2 was not affected (this would previously return 2!)
name|assertStats
argument_list|(
name|groupStats2
operator|.
name|get
argument_list|(
literal|"group1"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// adding again would then return wrong search stats (would return 4! instead of 3)
name|searchStats1
operator|.
name|add
argument_list|(
name|searchStats2
argument_list|)
expr_stmt|;
name|assertStats
argument_list|(
name|groupStats1
operator|.
name|get
argument_list|(
literal|"group1"
argument_list|)
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|assertStats
specifier|private
name|void
name|assertStats
parameter_list|(
name|Stats
name|stats
parameter_list|,
name|long
name|equalTo
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|equalTo
argument_list|,
name|stats
operator|.
name|getQueryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|equalTo
argument_list|,
name|stats
operator|.
name|getQueryTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|equalTo
argument_list|,
name|stats
operator|.
name|getQueryCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|equalTo
argument_list|,
name|stats
operator|.
name|getFetchCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|equalTo
argument_list|,
name|stats
operator|.
name|getFetchTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|equalTo
argument_list|,
name|stats
operator|.
name|getFetchCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|equalTo
argument_list|,
name|stats
operator|.
name|getScrollCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|equalTo
argument_list|,
name|stats
operator|.
name|getScrollTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|equalTo
argument_list|,
name|stats
operator|.
name|getScrollCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|equalTo
argument_list|,
name|stats
operator|.
name|getSuggestCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|equalTo
argument_list|,
name|stats
operator|.
name|getSuggestTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|equalTo
argument_list|,
name|stats
operator|.
name|getSuggestCurrent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

