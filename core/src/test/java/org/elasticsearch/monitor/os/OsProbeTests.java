begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.os
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|os
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Constants
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|*
import|;
end_import

begin_class
DECL|class|OsProbeTests
specifier|public
class|class
name|OsProbeTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|probe
name|OsProbe
name|probe
init|=
name|OsProbe
operator|.
name|getInstance
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testOsInfo
specifier|public
name|void
name|testOsInfo
parameter_list|()
block|{
name|OsInfo
name|info
init|=
name|probe
operator|.
name|osInfo
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getRefreshInterval
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Constants
operator|.
name|OS_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getArch
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Constants
operator|.
name|OS_ARCH
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Constants
operator|.
name|OS_VERSION
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getAvailableProcessors
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOsStats
specifier|public
name|void
name|testOsStats
parameter_list|()
block|{
name|OsStats
name|stats
init|=
name|probe
operator|.
name|osStats
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getTimestamp
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getLoadAverage
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
operator|(
name|double
operator|)
operator|-
literal|1
argument_list|)
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
operator|(
name|double
operator|)
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|stats
operator|.
name|getMem
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getMem
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getMem
argument_list|()
operator|.
name|getFree
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getMem
argument_list|()
operator|.
name|getFreePercent
argument_list|()
argument_list|,
name|allOf
argument_list|(
name|greaterThanOrEqualTo
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|,
name|lessThanOrEqualTo
argument_list|(
operator|(
name|short
operator|)
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getMem
argument_list|()
operator|.
name|getUsed
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getMem
argument_list|()
operator|.
name|getUsedPercent
argument_list|()
argument_list|,
name|allOf
argument_list|(
name|greaterThanOrEqualTo
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|,
name|lessThanOrEqualTo
argument_list|(
operator|(
name|short
operator|)
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|stats
operator|.
name|getSwap
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getSwap
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getSwap
argument_list|()
operator|.
name|getFree
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getSwap
argument_list|()
operator|.
name|getUsed
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

