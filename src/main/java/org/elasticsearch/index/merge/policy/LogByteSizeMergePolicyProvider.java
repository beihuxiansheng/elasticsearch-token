begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.merge.policy
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|merge
operator|.
name|policy
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
name|index
operator|.
name|LogByteSizeMergePolicy
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
name|index
operator|.
name|MergePolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|Preconditions
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
name|common
operator|.
name|unit
operator|.
name|ByteSizeUnit
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
name|unit
operator|.
name|ByteSizeValue
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
name|IndexSettingsService
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
name|store
operator|.
name|Store
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArraySet
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|LogByteSizeMergePolicyProvider
specifier|public
class|class
name|LogByteSizeMergePolicyProvider
extends|extends
name|AbstractMergePolicyProvider
argument_list|<
name|LogByteSizeMergePolicy
argument_list|>
block|{
DECL|field|indexSettingsService
specifier|private
specifier|final
name|IndexSettingsService
name|indexSettingsService
decl_stmt|;
DECL|field|MAX_MERGE_BYTE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MAX_MERGE_BYTE_SIZE_KEY
init|=
literal|"index.merge.policy.max_merge_sizes"
decl_stmt|;
DECL|field|MIN_MERGE_BYTE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MIN_MERGE_BYTE_SIZE_KEY
init|=
literal|"index.merge.policy.min_merge_size"
decl_stmt|;
DECL|field|MERGE_FACTORY_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MERGE_FACTORY_KEY
init|=
literal|"index.merge.policy.merge_factor"
decl_stmt|;
DECL|field|minMergeSize
specifier|private
specifier|volatile
name|ByteSizeValue
name|minMergeSize
decl_stmt|;
DECL|field|maxMergeSize
specifier|private
specifier|volatile
name|ByteSizeValue
name|maxMergeSize
decl_stmt|;
DECL|field|mergeFactor
specifier|private
specifier|volatile
name|int
name|mergeFactor
decl_stmt|;
DECL|field|maxMergeDocs
specifier|private
specifier|volatile
name|int
name|maxMergeDocs
decl_stmt|;
DECL|field|calibrateSizeByDeletes
specifier|private
specifier|final
name|boolean
name|calibrateSizeByDeletes
decl_stmt|;
DECL|field|policies
specifier|private
specifier|final
name|Set
argument_list|<
name|CustomLogByteSizeMergePolicy
argument_list|>
name|policies
init|=
operator|new
name|CopyOnWriteArraySet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|applySettings
specifier|private
specifier|final
name|ApplySettings
name|applySettings
init|=
operator|new
name|ApplySettings
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|LogByteSizeMergePolicyProvider
specifier|public
name|LogByteSizeMergePolicyProvider
parameter_list|(
name|Store
name|store
parameter_list|,
name|IndexSettingsService
name|indexSettingsService
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|store
argument_list|,
literal|"Store must be provided to merge policy"
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexSettingsService
operator|=
name|indexSettingsService
expr_stmt|;
name|this
operator|.
name|minMergeSize
operator|=
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"min_merge_size"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
call|(
name|long
call|)
argument_list|(
name|LogByteSizeMergePolicy
operator|.
name|DEFAULT_MIN_MERGE_MB
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
argument_list|,
name|ByteSizeUnit
operator|.
name|BYTES
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxMergeSize
operator|=
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"max_merge_size"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
operator|(
name|long
operator|)
name|LogByteSizeMergePolicy
operator|.
name|DEFAULT_MAX_MERGE_MB
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|mergeFactor
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"merge_factor"
argument_list|,
name|LogByteSizeMergePolicy
operator|.
name|DEFAULT_MERGE_FACTOR
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxMergeDocs
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"max_merge_docs"
argument_list|,
name|LogByteSizeMergePolicy
operator|.
name|DEFAULT_MAX_MERGE_DOCS
argument_list|)
expr_stmt|;
name|this
operator|.
name|calibrateSizeByDeletes
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"calibrate_size_by_deletes"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using [log_bytes_size] merge policy with merge_factor[{}], min_merge_size[{}], max_merge_size[{}], max_merge_docs[{}], calibrate_size_by_deletes[{}]"
argument_list|,
name|mergeFactor
argument_list|,
name|minMergeSize
argument_list|,
name|maxMergeSize
argument_list|,
name|maxMergeDocs
argument_list|,
name|calibrateSizeByDeletes
argument_list|)
expr_stmt|;
name|indexSettingsService
operator|.
name|addListener
argument_list|(
name|applySettings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newMergePolicy
specifier|public
name|LogByteSizeMergePolicy
name|newMergePolicy
parameter_list|()
block|{
specifier|final
name|CustomLogByteSizeMergePolicy
name|mergePolicy
init|=
operator|new
name|CustomLogByteSizeMergePolicy
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|mergePolicy
operator|.
name|setMinMergeMB
argument_list|(
name|minMergeSize
operator|.
name|mbFrac
argument_list|()
argument_list|)
expr_stmt|;
name|mergePolicy
operator|.
name|setMaxMergeMB
argument_list|(
name|maxMergeSize
operator|.
name|mbFrac
argument_list|()
argument_list|)
expr_stmt|;
name|mergePolicy
operator|.
name|setMergeFactor
argument_list|(
name|mergeFactor
argument_list|)
expr_stmt|;
name|mergePolicy
operator|.
name|setMaxMergeDocs
argument_list|(
name|maxMergeDocs
argument_list|)
expr_stmt|;
name|mergePolicy
operator|.
name|setCalibrateSizeByDeletes
argument_list|(
name|calibrateSizeByDeletes
argument_list|)
expr_stmt|;
name|mergePolicy
operator|.
name|setNoCFSRatio
argument_list|(
name|noCFSRatio
argument_list|)
expr_stmt|;
name|policies
operator|.
name|add
argument_list|(
name|mergePolicy
argument_list|)
expr_stmt|;
return|return
name|mergePolicy
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
name|indexSettingsService
operator|.
name|removeListener
argument_list|(
name|applySettings
argument_list|)
expr_stmt|;
block|}
DECL|field|INDEX_MERGE_POLICY_MIN_MERGE_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_MERGE_POLICY_MIN_MERGE_SIZE
init|=
literal|"index.merge.policy.min_merge_size"
decl_stmt|;
DECL|field|INDEX_MERGE_POLICY_MAX_MERGE_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_MERGE_POLICY_MAX_MERGE_SIZE
init|=
literal|"index.merge.policy.max_merge_size"
decl_stmt|;
DECL|field|INDEX_MERGE_POLICY_MAX_MERGE_DOCS
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_MERGE_POLICY_MAX_MERGE_DOCS
init|=
literal|"index.merge.policy.max_merge_docs"
decl_stmt|;
DECL|field|INDEX_MERGE_POLICY_MERGE_FACTOR
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_MERGE_POLICY_MERGE_FACTOR
init|=
literal|"index.merge.policy.merge_factor"
decl_stmt|;
DECL|class|ApplySettings
class|class
name|ApplySettings
implements|implements
name|IndexSettingsService
operator|.
name|Listener
block|{
annotation|@
name|Override
DECL|method|onRefreshSettings
specifier|public
name|void
name|onRefreshSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|ByteSizeValue
name|minMergeSize
init|=
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|INDEX_MERGE_POLICY_MIN_MERGE_SIZE
argument_list|,
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|minMergeSize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|minMergeSize
operator|.
name|equals
argument_list|(
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|minMergeSize
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating min_merge_size from [{}] to [{}]"
argument_list|,
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|minMergeSize
argument_list|,
name|minMergeSize
argument_list|)
expr_stmt|;
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|minMergeSize
operator|=
name|minMergeSize
expr_stmt|;
for|for
control|(
name|CustomLogByteSizeMergePolicy
name|policy
range|:
name|policies
control|)
block|{
name|policy
operator|.
name|setMinMergeMB
argument_list|(
name|minMergeSize
operator|.
name|mbFrac
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|ByteSizeValue
name|maxMergeSize
init|=
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|INDEX_MERGE_POLICY_MAX_MERGE_SIZE
argument_list|,
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|maxMergeSize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|maxMergeSize
operator|.
name|equals
argument_list|(
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|maxMergeSize
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating max_merge_size from [{}] to [{}]"
argument_list|,
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|maxMergeSize
argument_list|,
name|maxMergeSize
argument_list|)
expr_stmt|;
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|maxMergeSize
operator|=
name|maxMergeSize
expr_stmt|;
for|for
control|(
name|CustomLogByteSizeMergePolicy
name|policy
range|:
name|policies
control|)
block|{
name|policy
operator|.
name|setMaxMergeMB
argument_list|(
name|maxMergeSize
operator|.
name|mbFrac
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|maxMergeDocs
init|=
name|settings
operator|.
name|getAsInt
argument_list|(
name|INDEX_MERGE_POLICY_MAX_MERGE_DOCS
argument_list|,
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|maxMergeDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxMergeDocs
operator|!=
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|maxMergeDocs
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating max_merge_docs from [{}] to [{}]"
argument_list|,
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|maxMergeDocs
argument_list|,
name|maxMergeDocs
argument_list|)
expr_stmt|;
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|maxMergeDocs
operator|=
name|maxMergeDocs
expr_stmt|;
for|for
control|(
name|CustomLogByteSizeMergePolicy
name|policy
range|:
name|policies
control|)
block|{
name|policy
operator|.
name|setMaxMergeDocs
argument_list|(
name|maxMergeDocs
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|mergeFactor
init|=
name|settings
operator|.
name|getAsInt
argument_list|(
name|INDEX_MERGE_POLICY_MERGE_FACTOR
argument_list|,
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|mergeFactor
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergeFactor
operator|!=
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|mergeFactor
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating merge_factor from [{}] to [{}]"
argument_list|,
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|mergeFactor
argument_list|,
name|mergeFactor
argument_list|)
expr_stmt|;
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|mergeFactor
operator|=
name|mergeFactor
expr_stmt|;
for|for
control|(
name|CustomLogByteSizeMergePolicy
name|policy
range|:
name|policies
control|)
block|{
name|policy
operator|.
name|setMergeFactor
argument_list|(
name|mergeFactor
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|double
name|noCFSRatio
init|=
name|parseNoCFSRatio
argument_list|(
name|settings
operator|.
name|get
argument_list|(
name|INDEX_COMPOUND_FORMAT
argument_list|,
name|Double
operator|.
name|toString
argument_list|(
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|noCFSRatio
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|noCFSRatio
operator|!=
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|noCFSRatio
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating index.compound_format from [{}] to [{}]"
argument_list|,
name|formatNoCFSRatio
argument_list|(
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|noCFSRatio
argument_list|)
argument_list|,
name|formatNoCFSRatio
argument_list|(
name|noCFSRatio
argument_list|)
argument_list|)
expr_stmt|;
name|LogByteSizeMergePolicyProvider
operator|.
name|this
operator|.
name|noCFSRatio
operator|=
name|noCFSRatio
expr_stmt|;
for|for
control|(
name|CustomLogByteSizeMergePolicy
name|policy
range|:
name|policies
control|)
block|{
name|policy
operator|.
name|setNoCFSRatio
argument_list|(
name|noCFSRatio
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|CustomLogByteSizeMergePolicy
specifier|public
specifier|static
class|class
name|CustomLogByteSizeMergePolicy
extends|extends
name|LogByteSizeMergePolicy
block|{
DECL|field|provider
specifier|private
specifier|final
name|LogByteSizeMergePolicyProvider
name|provider
decl_stmt|;
DECL|method|CustomLogByteSizeMergePolicy
specifier|public
name|CustomLogByteSizeMergePolicy
parameter_list|(
name|LogByteSizeMergePolicyProvider
name|provider
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|provider
operator|.
name|policies
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|MergePolicy
name|clone
parameter_list|()
block|{
comment|// Lucene IW makes a clone internally but since we hold on to this instance
comment|// the clone will just be the identity.
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

