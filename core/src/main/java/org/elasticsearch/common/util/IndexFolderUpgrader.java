begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
package|;
end_package

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
name|Logger
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
name|util
operator|.
name|IOUtils
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
name|logging
operator|.
name|Loggers
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
name|NodeEnvironment
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
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|Files
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
name|NoSuchFileException
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
name|nio
operator|.
name|file
operator|.
name|StandardCopyOption
import|;
end_import

begin_comment
comment|/**  * Renames index folders from {index.name} to {index.uuid}  */
end_comment

begin_class
DECL|class|IndexFolderUpgrader
specifier|public
class|class
name|IndexFolderUpgrader
block|{
DECL|field|nodeEnv
specifier|private
specifier|final
name|NodeEnvironment
name|nodeEnv
decl_stmt|;
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|IndexFolderUpgrader
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Creates a new upgrader instance      * @param settings node settings      * @param nodeEnv the node env to operate on      */
DECL|method|IndexFolderUpgrader
name|IndexFolderUpgrader
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeEnvironment
name|nodeEnv
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|nodeEnv
operator|=
name|nodeEnv
expr_stmt|;
block|}
comment|/**      * Moves the index folder found in<code>source</code> to<code>target</code>      */
DECL|method|upgrade
name|void
name|upgrade
parameter_list|(
specifier|final
name|Index
name|index
parameter_list|,
specifier|final
name|Path
name|source
parameter_list|,
specifier|final
name|Path
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|move
argument_list|(
name|source
argument_list|,
name|target
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchFileException
decl||
name|FileNotFoundException
name|exception
parameter_list|)
block|{
comment|// thrown when the source is non-existent because the folder was renamed
comment|// by another node (shared FS) after we checked if the target exists
name|logger
operator|.
name|error
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
literal|"multiple nodes trying to upgrade [{}] in parallel, retry "
operator|+
literal|"upgrading with single node"
argument_list|,
name|target
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
throw|throw
name|exception
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"{} moved from [{}] to [{}]"
argument_list|,
name|index
argument_list|,
name|source
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"{} syncing directory [{}]"
argument_list|,
name|index
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|fsync
argument_list|(
name|target
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Renames<code>indexFolderName</code> index folders found in node paths and custom path      * iff {@link #needsUpgrade(Index, String)} is true.      * Index folder in custom paths are renamed first followed by index folders in each node path.      */
DECL|method|upgrade
name|void
name|upgrade
parameter_list|(
specifier|final
name|String
name|indexFolderName
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|NodeEnvironment
operator|.
name|NodePath
name|nodePath
range|:
name|nodeEnv
operator|.
name|nodePaths
argument_list|()
control|)
block|{
specifier|final
name|Path
name|indexFolderPath
init|=
name|nodePath
operator|.
name|indicesPath
operator|.
name|resolve
argument_list|(
name|indexFolderName
argument_list|)
decl_stmt|;
specifier|final
name|IndexMetaData
name|indexMetaData
init|=
name|IndexMetaData
operator|.
name|FORMAT
operator|.
name|loadLatestState
argument_list|(
name|logger
argument_list|,
name|indexFolderPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Index
name|index
init|=
name|indexMetaData
operator|.
name|getIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|needsUpgrade
argument_list|(
name|index
argument_list|,
name|indexFolderName
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"{} upgrading [{}] to new naming convention"
argument_list|,
name|index
argument_list|,
name|indexFolderPath
argument_list|)
expr_stmt|;
specifier|final
name|IndexSettings
name|indexSettings
init|=
operator|new
name|IndexSettings
argument_list|(
name|indexMetaData
argument_list|,
name|settings
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexSettings
operator|.
name|hasCustomDataPath
argument_list|()
condition|)
block|{
comment|// we rename index folder in custom path before renaming them in any node path
comment|// to have the index state under a not-yet-upgraded index folder, which we use to
comment|// continue renaming after a incomplete upgrade.
specifier|final
name|Path
name|customLocationSource
init|=
name|nodeEnv
operator|.
name|resolveBaseCustomLocation
argument_list|(
name|indexSettings
argument_list|)
operator|.
name|resolve
argument_list|(
name|indexFolderName
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|customLocationTarget
init|=
name|customLocationSource
operator|.
name|resolveSibling
argument_list|(
name|index
operator|.
name|getUUID
argument_list|()
argument_list|)
decl_stmt|;
comment|// we rename the folder in custom path only the first time we encounter a state
comment|// in a node path, which needs upgrading, it is a no-op for subsequent node paths
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|customLocationSource
argument_list|)
comment|// might not exist if no data was written for this index
operator|&&
name|Files
operator|.
name|exists
argument_list|(
name|customLocationTarget
argument_list|)
operator|==
literal|false
condition|)
block|{
name|upgrade
argument_list|(
name|index
argument_list|,
name|customLocationSource
argument_list|,
name|customLocationTarget
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] no upgrade needed - already upgraded"
argument_list|,
name|customLocationTarget
argument_list|)
expr_stmt|;
block|}
block|}
name|upgrade
argument_list|(
name|index
argument_list|,
name|indexFolderPath
argument_list|,
name|indexFolderPath
operator|.
name|resolveSibling
argument_list|(
name|index
operator|.
name|getUUID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] no upgrade needed - already upgraded"
argument_list|,
name|indexFolderPath
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[{}] no index state found - ignoring"
argument_list|,
name|indexFolderPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Upgrades all indices found under<code>nodeEnv</code>. Already upgraded indices are ignored.      */
DECL|method|upgradeIndicesIfNeeded
specifier|public
specifier|static
name|void
name|upgradeIndicesIfNeeded
parameter_list|(
specifier|final
name|Settings
name|settings
parameter_list|,
specifier|final
name|NodeEnvironment
name|nodeEnv
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexFolderUpgrader
name|upgrader
init|=
operator|new
name|IndexFolderUpgrader
argument_list|(
name|settings
argument_list|,
name|nodeEnv
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|indexFolderName
range|:
name|nodeEnv
operator|.
name|availableIndexFolders
argument_list|()
control|)
block|{
name|upgrader
operator|.
name|upgrade
argument_list|(
name|indexFolderName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|needsUpgrade
specifier|static
name|boolean
name|needsUpgrade
parameter_list|(
name|Index
name|index
parameter_list|,
name|String
name|indexFolderName
parameter_list|)
block|{
return|return
name|indexFolderName
operator|.
name|equals
argument_list|(
name|index
operator|.
name|getUUID
argument_list|()
argument_list|)
operator|==
literal|false
return|;
block|}
block|}
end_class

end_unit
