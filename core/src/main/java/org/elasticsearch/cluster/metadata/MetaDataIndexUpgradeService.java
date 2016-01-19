begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectCursor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|common
operator|.
name|component
operator|.
name|AbstractComponent
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
name|Inject
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
name|analysis
operator|.
name|NamedAnalyzer
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
name|similarity
operator|.
name|SimilarityService
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
name|mapper
operator|.
name|MapperRegistry
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableSet
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
name|util
operator|.
name|set
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_comment
comment|/**  * This service is responsible for upgrading legacy index metadata to the current version  *<p>  * Every time an existing index is introduced into cluster this service should be used  * to upgrade the existing index metadata to the latest version of the cluster. It typically  * occurs during cluster upgrade, when dangling indices are imported into the cluster or indices  * are restored from a repository.  */
end_comment

begin_class
DECL|class|MetaDataIndexUpgradeService
specifier|public
class|class
name|MetaDataIndexUpgradeService
extends|extends
name|AbstractComponent
block|{
DECL|field|mapperRegistry
specifier|private
specifier|final
name|MapperRegistry
name|mapperRegistry
decl_stmt|;
annotation|@
name|Inject
DECL|method|MetaDataIndexUpgradeService
specifier|public
name|MetaDataIndexUpgradeService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|MapperRegistry
name|mapperRegistry
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|mapperRegistry
operator|=
name|mapperRegistry
expr_stmt|;
block|}
comment|/**      * Checks that the index can be upgraded to the current version of the master node.      *      *<p>      * If the index does not need upgrade it returns the index metadata unchanged, otherwise it returns a modified index metadata. If index      * cannot be updated the method throws an exception.      */
DECL|method|upgradeIndexMetaData
specifier|public
name|IndexMetaData
name|upgradeIndexMetaData
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|)
block|{
comment|// Throws an exception if there are too-old segments:
if|if
condition|(
name|isUpgraded
argument_list|(
name|indexMetaData
argument_list|)
condition|)
block|{
return|return
name|indexMetaData
return|;
block|}
name|checkSupportedVersion
argument_list|(
name|indexMetaData
argument_list|)
expr_stmt|;
name|IndexMetaData
name|newMetaData
init|=
name|indexMetaData
decl_stmt|;
name|checkMappingsCompatibility
argument_list|(
name|newMetaData
argument_list|)
expr_stmt|;
name|newMetaData
operator|=
name|markAsUpgraded
argument_list|(
name|newMetaData
argument_list|)
expr_stmt|;
return|return
name|newMetaData
return|;
block|}
comment|/**      * Checks if the index was already opened by this version of Elasticsearch and doesn't require any additional checks.      */
DECL|method|isUpgraded
specifier|private
name|boolean
name|isUpgraded
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|)
block|{
return|return
name|indexMetaData
operator|.
name|getUpgradedVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_3_0_0
argument_list|)
return|;
block|}
comment|/**      * Elasticsearch 3.0 no longer supports indices with pre Lucene v5.0 (Elasticsearch v2.0.0.beta1) segments. All indices      * that were created before Elasticsearch v2.0.0.beta1 should be upgraded using upgrade API before they can      * be open by this version of elasticsearch.      */
DECL|method|checkSupportedVersion
specifier|private
name|void
name|checkSupportedVersion
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|)
block|{
if|if
condition|(
name|indexMetaData
operator|.
name|getState
argument_list|()
operator|==
name|IndexMetaData
operator|.
name|State
operator|.
name|OPEN
operator|&&
name|isSupportedVersion
argument_list|(
name|indexMetaData
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The index ["
operator|+
name|indexMetaData
operator|.
name|getIndex
argument_list|()
operator|+
literal|"] was created before v2.0.0.beta1 and wasn't upgraded."
operator|+
literal|" This index should be open using a version before "
operator|+
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
operator|+
literal|" and upgraded using the upgrade API."
argument_list|)
throw|;
block|}
block|}
comment|/*      * Returns true if this index can be supported by the current version of elasticsearch      */
DECL|method|isSupportedVersion
specifier|private
specifier|static
name|boolean
name|isSupportedVersion
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|)
block|{
if|if
condition|(
name|indexMetaData
operator|.
name|getCreationVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_2_0_0_beta1
argument_list|)
condition|)
block|{
comment|// The index was created with elasticsearch that was using Lucene 5.2.1
return|return
literal|true
return|;
block|}
if|if
condition|(
name|indexMetaData
operator|.
name|getMinimumCompatibleVersion
argument_list|()
operator|!=
literal|null
operator|&&
name|indexMetaData
operator|.
name|getMinimumCompatibleVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_5_0_0
argument_list|)
condition|)
block|{
comment|//The index was upgraded we can work with it
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Checks the mappings for compatibility with the current version      */
DECL|method|checkMappingsCompatibility
specifier|private
name|void
name|checkMappingsCompatibility
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|)
block|{
try|try
block|{
comment|// We cannot instantiate real analysis server at this point because the node might not have
comment|// been started yet. However, we don't really need real analyzers at this stage - so we can fake it
name|IndexSettings
name|indexSettings
init|=
operator|new
name|IndexSettings
argument_list|(
name|indexMetaData
argument_list|,
name|this
operator|.
name|settings
argument_list|)
decl_stmt|;
name|SimilarityService
name|similarityService
init|=
operator|new
name|SimilarityService
argument_list|(
name|indexSettings
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|AnalysisService
name|analysisService
init|=
operator|new
name|FakeAnalysisService
argument_list|(
name|indexSettings
argument_list|)
init|)
block|{
try|try
init|(
name|MapperService
name|mapperService
init|=
operator|new
name|MapperService
argument_list|(
name|indexSettings
argument_list|,
name|analysisService
argument_list|,
name|similarityService
argument_list|,
name|mapperRegistry
argument_list|,
parameter_list|()
lambda|->
literal|null
argument_list|)
init|)
block|{
for|for
control|(
name|ObjectCursor
argument_list|<
name|MappingMetaData
argument_list|>
name|cursor
range|:
name|indexMetaData
operator|.
name|getMappings
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|MappingMetaData
name|mappingMetaData
init|=
name|cursor
operator|.
name|value
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
name|mappingMetaData
operator|.
name|type
argument_list|()
argument_list|,
name|mappingMetaData
operator|.
name|source
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// Wrap the inner exception so we have the index name in the exception message
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unable to upgrade the mappings for the index ["
operator|+
name|indexMetaData
operator|.
name|getIndex
argument_list|()
operator|+
literal|"]"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
end_class

begin_comment
comment|/**      * Marks index as upgraded so we don't have to test it again      */
end_comment

begin_function
DECL|method|markAsUpgraded
specifier|private
name|IndexMetaData
name|markAsUpgraded
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|)
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
name|indexMetaData
operator|.
name|getSettings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_UPGRADED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|indexMetaData
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
end_function

begin_comment
comment|/**      * A fake analysis server that returns the same keyword analyzer for all requests      */
end_comment

begin_class
DECL|class|FakeAnalysisService
specifier|private
specifier|static
class|class
name|FakeAnalysisService
extends|extends
name|AnalysisService
block|{
DECL|field|fakeAnalyzer
specifier|private
name|Analyzer
name|fakeAnalyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"shouldn't be here"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
DECL|method|FakeAnalysisService
specifier|public
name|FakeAnalysisService
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|indexSettings
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|analyzer
specifier|public
name|NamedAnalyzer
name|analyzer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|NamedAnalyzer
argument_list|(
name|name
argument_list|,
name|fakeAnalyzer
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|fakeAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

unit|}
end_unit

