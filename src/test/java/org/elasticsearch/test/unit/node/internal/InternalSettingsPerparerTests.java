begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.node.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|node
operator|.
name|internal
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|Tuple
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
name|node
operator|.
name|internal
operator|.
name|InternalSettingsPerparer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|settingsBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
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
DECL|class|InternalSettingsPerparerTests
specifier|public
class|class
name|InternalSettingsPerparerTests
block|{
annotation|@
name|Before
DECL|method|setupSystemProperties
specifier|public
name|void
name|setupSystemProperties
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"es.node.zone"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanupSystemProperties
specifier|public
name|void
name|cleanupSystemProperties
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"es.node.zone"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIgnoreSystemProperties
specifier|public
name|void
name|testIgnoreSystemProperties
parameter_list|()
block|{
name|Tuple
argument_list|<
name|Settings
argument_list|,
name|Environment
argument_list|>
name|tuple
init|=
name|InternalSettingsPerparer
operator|.
name|prepareSettings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.zone"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// Should use setting from the system property
name|assertThat
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
operator|.
name|get
argument_list|(
literal|"node.zone"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|tuple
operator|=
name|InternalSettingsPerparer
operator|.
name|prepareSettings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"config.ignore_system_properties"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.zone"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Should use setting from the system property
name|assertThat
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
operator|.
name|get
argument_list|(
literal|"node.zone"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

