begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.settings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
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
name|Strings
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
name|common
operator|.
name|xcontent
operator|.
name|json
operator|.
name|JsonXContent
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
name|Arrays
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|SettingsFilterTests
specifier|public
class|class
name|SettingsFilterTests
extends|extends
name|ESTestCase
block|{
DECL|method|testAddingAndRemovingFilters
specifier|public
name|void
name|testAddingAndRemovingFilters
parameter_list|()
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|hashSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
decl_stmt|;
name|SettingsFilter
name|settingsFilter
init|=
operator|new
name|SettingsFilter
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|hashSet
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|settingsFilter
operator|.
name|getPatterns
argument_list|()
argument_list|,
name|hashSet
argument_list|)
expr_stmt|;
block|}
DECL|method|testSettingsFiltering
specifier|public
name|void
name|testSettingsFiltering
parameter_list|()
throws|throws
name|IOException
block|{
name|testFiltering
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|"foo_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foo1"
argument_list|,
literal|"foo1_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"bar"
argument_list|,
literal|"bar_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"bar1"
argument_list|,
literal|"bar1_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"bar.2"
argument_list|,
literal|"bar2_test"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo1"
argument_list|,
literal|"foo1_test"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|"foo"
argument_list|,
literal|"bar*"
argument_list|)
expr_stmt|;
name|testFiltering
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|"foo_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foo1"
argument_list|,
literal|"foo1_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"bar"
argument_list|,
literal|"bar_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"bar1"
argument_list|,
literal|"bar1_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"bar.2"
argument_list|,
literal|"bar2_test"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|"foo_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foo1"
argument_list|,
literal|"foo1_test"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|"bar*"
argument_list|)
expr_stmt|;
name|testFiltering
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|"foo_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foo1"
argument_list|,
literal|"foo1_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"bar"
argument_list|,
literal|"bar_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"bar1"
argument_list|,
literal|"bar1_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"bar.2"
argument_list|,
literal|"bar2_test"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|,
literal|"foo"
argument_list|,
literal|"bar*"
argument_list|,
literal|"foo*"
argument_list|)
expr_stmt|;
name|testFiltering
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|"foo_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"bar"
argument_list|,
literal|"bar_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"baz"
argument_list|,
literal|"baz_test"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|"foo_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"bar"
argument_list|,
literal|"bar_test"
argument_list|)
operator|.
name|put
argument_list|(
literal|"baz"
argument_list|,
literal|"baz_test"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFiltering
specifier|private
name|void
name|testFiltering
parameter_list|(
name|Settings
name|source
parameter_list|,
name|Settings
name|filtered
parameter_list|,
name|String
modifier|...
name|patterns
parameter_list|)
throws|throws
name|IOException
block|{
name|SettingsFilter
name|settingsFilter
init|=
operator|new
name|SettingsFilter
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|patterns
argument_list|)
argument_list|)
decl_stmt|;
comment|// Test using direct filtering
name|Settings
name|filteredSettings
init|=
name|settingsFilter
operator|.
name|filter
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filteredSettings
operator|.
name|getAsMap
argument_list|()
operator|.
name|entrySet
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|filtered
operator|.
name|getAsMap
argument_list|()
operator|.
name|entrySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test using toXContent filtering
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
argument_list|()
decl_stmt|;
name|settingsFilter
operator|.
name|addFilterSettingParams
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|XContentBuilder
name|xContentBuilder
init|=
name|XContentBuilder
operator|.
name|builder
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|)
decl_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|source
operator|.
name|toXContent
argument_list|(
name|xContentBuilder
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|String
name|filteredSettingsString
init|=
name|xContentBuilder
operator|.
name|string
argument_list|()
decl_stmt|;
name|filteredSettings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|loadFromSource
argument_list|(
name|filteredSettingsString
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|filteredSettings
operator|.
name|getAsMap
argument_list|()
operator|.
name|entrySet
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|filtered
operator|.
name|getAsMap
argument_list|()
operator|.
name|entrySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

