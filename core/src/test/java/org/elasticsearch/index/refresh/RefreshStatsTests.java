begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.refresh
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|refresh
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
name|test
operator|.
name|AbstractStreamableTestCase
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

begin_class
DECL|class|RefreshStatsTests
specifier|public
class|class
name|RefreshStatsTests
extends|extends
name|AbstractStreamableTestCase
argument_list|<
name|RefreshStats
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createTestInstance
specifier|protected
name|RefreshStats
name|createTestInstance
parameter_list|()
block|{
return|return
operator|new
name|RefreshStats
argument_list|(
name|randomNonNegativeLong
argument_list|()
argument_list|,
name|randomNonNegativeLong
argument_list|()
argument_list|,
name|between
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createBlankInstance
specifier|protected
name|RefreshStats
name|createBlankInstance
parameter_list|()
block|{
return|return
operator|new
name|RefreshStats
argument_list|()
return|;
block|}
DECL|method|testPre5Dot2
specifier|public
name|void
name|testPre5Dot2
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We can drop the compatibility once the assertion just below this list fails
name|assertTrue
argument_list|(
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_5_2_0
argument_list|)
argument_list|)
expr_stmt|;
name|RefreshStats
name|instance
init|=
name|createTestInstance
argument_list|()
decl_stmt|;
name|RefreshStats
name|copied
init|=
name|copyInstance
argument_list|(
name|instance
argument_list|,
name|Version
operator|.
name|V_5_1_1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|instance
operator|.
name|getTotal
argument_list|()
argument_list|,
name|copied
operator|.
name|getTotal
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|instance
operator|.
name|getTotalTimeInMillis
argument_list|()
argument_list|,
name|copied
operator|.
name|getTotalTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|copied
operator|.
name|getListeners
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

