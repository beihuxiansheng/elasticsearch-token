begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.attachment.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|attachment
operator|.
name|test
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
name|analysis
operator|.
name|AnalysisModule
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
name|analysis
operator|.
name|AnalysisService
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
name|mapper
operator|.
name|DocumentMapperParser
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
name|mapper
operator|.
name|MapperService
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
name|index
operator|.
name|similarity
operator|.
name|SimilarityLookupService
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
name|Locale
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|assumeTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|mapper
operator|.
name|attachments
operator|.
name|tika
operator|.
name|LocaleChecker
operator|.
name|isLocaleCompatible
import|;
end_import

begin_class
DECL|class|MapperTestUtils
specifier|public
class|class
name|MapperTestUtils
block|{
DECL|method|newMapperService
specifier|public
specifier|static
name|MapperService
name|newMapperService
parameter_list|(
name|Path
name|tempDir
parameter_list|)
block|{
return|return
name|newMapperService
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|,
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
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|tempDir
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|newMapperService
specifier|public
specifier|static
name|MapperService
name|newMapperService
parameter_list|(
name|Index
name|index
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
return|return
operator|new
name|MapperService
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|newAnalysisService
argument_list|(
name|indexSettings
argument_list|)
argument_list|,
name|newSimilarityLookupService
argument_list|(
name|indexSettings
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|newAnalysisService
specifier|public
specifier|static
name|AnalysisService
name|newAnalysisService
parameter_list|(
name|Path
name|tempDir
parameter_list|)
block|{
return|return
name|newAnalysisService
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|tempDir
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
argument_list|)
return|;
block|}
DECL|method|newAnalysisService
specifier|public
specifier|static
name|AnalysisService
name|newAnalysisService
parameter_list|(
name|Settings
name|indexSettings
parameter_list|)
block|{
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
name|indexSettings
argument_list|)
argument_list|,
operator|new
name|EnvironmentModule
argument_list|(
operator|new
name|Environment
argument_list|(
name|indexSettings
argument_list|)
argument_list|)
argument_list|)
operator|.
name|createInjector
argument_list|()
decl_stmt|;
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
name|indexSettings
argument_list|)
argument_list|,
operator|new
name|IndexNameModule
argument_list|(
name|index
argument_list|)
argument_list|,
operator|new
name|AnalysisModule
argument_list|(
name|indexSettings
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
DECL|method|newSimilarityLookupService
specifier|public
specifier|static
name|SimilarityLookupService
name|newSimilarityLookupService
parameter_list|(
name|Settings
name|indexSettings
parameter_list|)
block|{
return|return
operator|new
name|SimilarityLookupService
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|indexSettings
argument_list|)
return|;
block|}
DECL|method|newMapperParser
specifier|public
specifier|static
name|DocumentMapperParser
name|newMapperParser
parameter_list|(
name|Path
name|tempDir
parameter_list|)
block|{
return|return
name|newMapperParser
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|tempDir
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|newMapperParser
specifier|public
specifier|static
name|DocumentMapperParser
name|newMapperParser
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|Settings
name|forcedSettings
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
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|MapperService
name|mapperService
init|=
operator|new
name|MapperService
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|forcedSettings
argument_list|,
name|newAnalysisService
argument_list|(
name|forcedSettings
argument_list|)
argument_list|,
name|newSimilarityLookupService
argument_list|(
name|forcedSettings
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocumentMapperParser
argument_list|(
name|forcedSettings
argument_list|,
name|mapperService
argument_list|,
name|MapperTestUtils
operator|.
name|newAnalysisService
argument_list|(
name|forcedSettings
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * We can have issues with some JVMs and Locale      * See https://github.com/elasticsearch/elasticsearch-mapper-attachments/issues/105      */
DECL|method|assumeCorrectLocale
specifier|public
specifier|static
name|void
name|assumeCorrectLocale
parameter_list|()
block|{
name|assumeTrue
argument_list|(
literal|"Current Locale language "
operator|+
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|getLanguage
argument_list|()
operator|+
literal|" could cause an error with Java "
operator|+
name|Constants
operator|.
name|JAVA_VERSION
argument_list|,
name|isLocaleCompatible
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

