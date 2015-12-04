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
name|IndexSettings
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
name|HunspellService
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
name|IndexSettingsModule
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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|Path
name|baseDir
parameter_list|,
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|loadFromStream
argument_list|(
name|resource
argument_list|,
name|AnalysisTestsHelper
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|resource
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|baseDir
operator|.
name|toString
argument_list|()
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
throws|throws
name|IOException
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
if|if
condition|(
name|settings
operator|.
name|get
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|)
operator|==
literal|null
condition|)
block|{
name|settings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|settings
argument_list|)
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
expr_stmt|;
block|}
name|IndexSettings
name|idxSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
name|index
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|Environment
name|environment
init|=
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
decl_stmt|;
return|return
operator|new
name|AnalysisRegistry
argument_list|(
operator|new
name|HunspellService
argument_list|(
name|settings
argument_list|,
name|environment
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|,
name|environment
argument_list|)
operator|.
name|build
argument_list|(
name|idxSettings
argument_list|)
return|;
block|}
block|}
end_class

end_unit

