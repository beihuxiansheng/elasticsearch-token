begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster
package|package
name|org
operator|.
name|elasticsearch
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
name|equalTo
import|;
end_import

begin_class
DECL|class|DiskUsageTests
specifier|public
class|class
name|DiskUsageTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|diskUsageCalcTest
specifier|public
name|void
name|diskUsageCalcTest
parameter_list|()
block|{
name|DiskUsage
name|du
init|=
operator|new
name|DiskUsage
argument_list|(
literal|"node1"
argument_list|,
literal|100
argument_list|,
literal|40
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getFreeDiskAsPercentage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|40.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getFreeBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|40L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getUsedBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|60L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getTotalBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100L
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|randomDiskUsageTest
specifier|public
name|void
name|randomDiskUsageTest
parameter_list|()
block|{
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|long
name|total
init|=
name|between
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|long
name|free
init|=
name|between
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
if|if
condition|(
name|free
operator|>
name|total
operator|||
name|total
operator|<=
literal|0
condition|)
block|{
try|try
block|{
operator|new
name|DiskUsage
argument_list|(
literal|"random"
argument_list|,
name|total
argument_list|,
name|free
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should never reach this"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
assert|assert
literal|true
assert|;
block|}
block|}
else|else
block|{
name|DiskUsage
name|du
init|=
operator|new
name|DiskUsage
argument_list|(
literal|"random"
argument_list|,
name|total
argument_list|,
name|free
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getFreeBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|free
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getTotalBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|total
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getUsedBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|total
operator|-
name|free
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getFreeDiskAsPercentage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100.0
operator|*
operator|(
operator|(
name|double
operator|)
name|free
operator|/
name|total
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

