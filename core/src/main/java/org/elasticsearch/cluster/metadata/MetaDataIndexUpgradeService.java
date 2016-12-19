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
name|logging
operator|.
name|log4j
operator|.
name|message
operator|.
name|ParameterizedMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|util
operator|.
name|Supplier
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
name|IndexScopedSettings
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
name|AnalyzerScope
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
name|IndexAnalyzers
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
name|AbstractMap
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
name|Map
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
DECL|field|indexScopedSettings
specifier|private
specifier|final
name|IndexScopedSettings
name|indexScopedSettings
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
parameter_list|,
name|IndexScopedSettings
name|indexScopedSettings
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
name|this
operator|.
name|indexScopedSettings
operator|=
name|indexScopedSettings
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
parameter_list|,
name|Version
name|minimumIndexCompatibilityVersion
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
assert|assert
name|indexMetaData
operator|==
name|archiveBrokenIndexSettings
argument_list|(
name|indexMetaData
argument_list|)
operator|:
literal|"all settings must have been upgraded before"
assert|;
return|return
name|indexMetaData
return|;
block|}
name|checkSupportedVersion
argument_list|(
name|indexMetaData
argument_list|,
name|minimumIndexCompatibilityVersion
argument_list|)
expr_stmt|;
name|IndexMetaData
name|newMetaData
init|=
name|indexMetaData
decl_stmt|;
comment|// we have to run this first otherwise in we try to create IndexSettings
comment|// with broken settings and fail in checkMappingsCompatibility
name|newMetaData
operator|=
name|archiveBrokenIndexSettings
argument_list|(
name|newMetaData
argument_list|)
expr_stmt|;
comment|// only run the check with the upgraded settings!!
name|checkMappingsCompatibility
argument_list|(
name|newMetaData
argument_list|)
expr_stmt|;
return|return
name|markAsUpgraded
argument_list|(
name|newMetaData
argument_list|)
return|;
block|}
comment|/**      * Checks if the index was already opened by this version of Elasticsearch and doesn't require any additional checks.      */
DECL|method|isUpgraded
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
name|CURRENT
argument_list|)
return|;
block|}
comment|/**      * Elasticsearch v6.0 no longer supports indices created pre v5.0. All indices      * that were created before Elasticsearch v5.0 should be re-indexed in Elasticsearch 5.x      * before they can be opened by this version of elasticsearch.      */
DECL|method|checkSupportedVersion
specifier|private
name|void
name|checkSupportedVersion
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|,
name|Version
name|minimumIndexCompatibilityVersion
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
argument_list|,
name|minimumIndexCompatibilityVersion
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
literal|"] was created with version ["
operator|+
name|indexMetaData
operator|.
name|getCreationVersion
argument_list|()
operator|+
literal|"] but the minimum compatible version is ["
operator|+
name|minimumIndexCompatibilityVersion
operator|+
literal|"]. It should be re-indexed in Elasticsearch "
operator|+
name|minimumIndexCompatibilityVersion
operator|.
name|major
operator|+
literal|".x before upgrading to "
operator|+
name|Version
operator|.
name|CURRENT
operator|+
literal|"."
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
parameter_list|,
name|Version
name|minimumIndexCompatibilityVersion
parameter_list|)
block|{
return|return
name|indexMetaData
operator|.
name|getCreationVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|minimumIndexCompatibilityVersion
argument_list|)
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
specifier|final
name|NamedAnalyzer
name|fakeDefault
init|=
operator|new
name|NamedAnalyzer
argument_list|(
literal|"fake_default"
argument_list|,
name|AnalyzerScope
operator|.
name|INDEX
argument_list|,
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
argument_list|)
decl_stmt|;
comment|// this is just a fake map that always returns the same value for any possible string key
comment|// also the entrySet impl isn't fully correct but we implement it since internally
comment|// IndexAnalyzers will iterate over all analyzers to close them.
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NamedAnalyzer
argument_list|>
name|analyzerMap
init|=
operator|new
name|AbstractMap
argument_list|<
name|String
argument_list|,
name|NamedAnalyzer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NamedAnalyzer
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
assert|assert
name|key
operator|instanceof
name|String
operator|:
literal|"key must be a string but was: "
operator|+
name|key
operator|.
name|getClass
argument_list|()
assert|;
return|return
operator|new
name|NamedAnalyzer
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|,
name|AnalyzerScope
operator|.
name|INDEX
argument_list|,
name|fakeDefault
operator|.
name|analyzer
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedAnalyzer
argument_list|>
argument_list|>
name|entrySet
parameter_list|()
block|{
comment|// just to ensure we can iterate over this single analzyer
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
name|fakeDefault
operator|.
name|name
argument_list|()
argument_list|,
name|fakeDefault
argument_list|)
operator|.
name|entrySet
argument_list|()
return|;
block|}
block|}
decl_stmt|;
try|try
init|(
name|IndexAnalyzers
name|fakeIndexAnalzyers
init|=
operator|new
name|IndexAnalyzers
argument_list|(
name|indexSettings
argument_list|,
name|fakeDefault
argument_list|,
name|fakeDefault
argument_list|,
name|fakeDefault
argument_list|,
name|analyzerMap
argument_list|)
init|)
block|{
name|MapperService
name|mapperService
init|=
operator|new
name|MapperService
argument_list|(
name|indexSettings
argument_list|,
name|fakeIndexAnalzyers
argument_list|,
name|similarityService
argument_list|,
name|mapperRegistry
argument_list|,
parameter_list|()
lambda|->
literal|null
argument_list|)
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
name|indexMetaData
argument_list|,
name|MapperService
operator|.
name|MergeReason
operator|.
name|MAPPING_RECOVERY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
comment|/**      * Marks index as upgraded so we don't have to test it again      */
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
DECL|method|archiveBrokenIndexSettings
name|IndexMetaData
name|archiveBrokenIndexSettings
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|)
block|{
specifier|final
name|Settings
name|settings
init|=
name|indexMetaData
operator|.
name|getSettings
argument_list|()
decl_stmt|;
specifier|final
name|Settings
name|upgrade
init|=
name|indexScopedSettings
operator|.
name|archiveUnknownOrInvalidSettings
argument_list|(
name|settings
argument_list|,
name|e
lambda|->
name|logger
operator|.
name|warn
argument_list|(
literal|"{} ignoring unknown index setting: [{}] with value [{}]; archiving"
argument_list|,
name|indexMetaData
operator|.
name|getIndex
argument_list|()
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
parameter_list|(
name|e
parameter_list|,
name|ex
parameter_list|)
lambda|->
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"{} ignoring invalid index setting: [{}] with value [{}]; archiving"
argument_list|,
name|indexMetaData
operator|.
name|getIndex
argument_list|()
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|ex
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|upgrade
operator|!=
name|settings
condition|)
block|{
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
name|upgrade
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|indexMetaData
return|;
block|}
block|}
block|}
end_class

end_unit

