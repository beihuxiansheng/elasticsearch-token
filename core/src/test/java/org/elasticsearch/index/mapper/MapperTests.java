begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
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
name|ESTestCase
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
DECL|class|MapperTests
specifier|public
class|class
name|MapperTests
extends|extends
name|ESTestCase
block|{
DECL|method|testBuilderContextWithIndexSettings
specifier|public
name|void
name|testBuilderContextWithIndexSettings
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Mapper
operator|.
name|BuilderContext
name|context
init|=
operator|new
name|Mapper
operator|.
name|BuilderContext
argument_list|(
name|settings
argument_list|,
operator|new
name|ContentPath
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|indexSettings
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|context
operator|.
name|indexSettings
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBuilderContextWithIndexSettingsAsNull
specifier|public
name|void
name|testBuilderContextWithIndexSettingsAsNull
parameter_list|()
block|{
name|AssertionError
name|e
init|=
name|expectThrows
argument_list|(
name|AssertionError
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|Mapper
operator|.
name|BuilderContext
argument_list|(
literal|null
argument_list|,
operator|new
name|ContentPath
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

