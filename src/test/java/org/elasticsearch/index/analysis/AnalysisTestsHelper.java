begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.analysis
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
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
name|inject
operator|.
name|Injector
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
name|ModulesBuilder
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
name|ImmutableSettings
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
name|common
operator|.
name|settings
operator|.
name|SettingsModule
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
name|env
operator|.
name|EnvironmentModule
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
name|Index
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
name|IndexNameModule
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
name|settings
operator|.
name|IndexSettingsModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|analysis
operator|.
name|IndicesAnalysisModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|analysis
operator|.
name|IndicesAnalysisService
import|;
end_import

begin_class
DECL|class|AnalysisTestsHelper
specifier|public
class|class
name|AnalysisTestsHelper
block|{
DECL|method|createAnalysisServiceFromClassPath
specifier|public
specifier|static
name|AnalysisService
name|createAnalysisServiceFromClassPath
parameter_list|(
name|String
name|resource
parameter_list|)
block|{
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|loadFromClasspath
argument_list|(
name|resource
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|createAnalysisServiceFromSettings
argument_list|(
name|settings
argument_list|)
return|;
block|}
DECL|method|createAnalysisServiceFromSettings
specifier|public
specifier|static
name|AnalysisService
name|createAnalysisServiceFromSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Injector
name|parentInjector
init|=
operator|new
name|ModulesBuilder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|EnvironmentModule
argument_list|(
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
argument_list|)
argument_list|,
operator|new
name|IndicesAnalysisModule
argument_list|()
argument_list|)
operator|.
name|createInjector
argument_list|()
decl_stmt|;
name|AnalysisModule
name|analysisModule
init|=
operator|new
name|AnalysisModule
argument_list|(
name|settings
argument_list|,
name|parentInjector
operator|.
name|getInstance
argument_list|(
name|IndicesAnalysisService
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|Injector
name|injector
init|=
operator|new
name|ModulesBuilder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|IndexSettingsModule
argument_list|(
name|index
argument_list|,
name|settings
argument_list|)
argument_list|,
operator|new
name|IndexNameModule
argument_list|(
name|index
argument_list|)
argument_list|,
name|analysisModule
argument_list|)
operator|.
name|createChildInjector
argument_list|(
name|parentInjector
argument_list|)
decl_stmt|;
return|return
name|injector
operator|.
name|getInstance
argument_list|(
name|AnalysisService
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

