begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
package|;
end_package

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
name|AtomicMoveNotSupportedException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSpec
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
name|cli
operator|.
name|EnvironmentAwareCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cli
operator|.
name|ExitCodes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cli
operator|.
name|Terminal
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cli
operator|.
name|UserException
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
name|Strings
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cli
operator|.
name|Terminal
operator|.
name|Verbosity
operator|.
name|VERBOSE
import|;
end_import

begin_comment
comment|/**  * A command for the plugin cli to remove a plugin from elasticsearch.  */
end_comment

begin_class
DECL|class|RemovePluginCommand
class|class
name|RemovePluginCommand
extends|extends
name|EnvironmentAwareCommand
block|{
DECL|field|arguments
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|arguments
decl_stmt|;
DECL|method|RemovePluginCommand
name|RemovePluginCommand
parameter_list|()
block|{
name|super
argument_list|(
literal|"Removes a plugin from elasticsearch"
argument_list|)
expr_stmt|;
name|this
operator|.
name|arguments
operator|=
name|parser
operator|.
name|nonOptions
argument_list|(
literal|"plugin name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|protected
name|void
name|execute
parameter_list|(
name|Terminal
name|terminal
parameter_list|,
name|OptionSet
name|options
parameter_list|,
name|Environment
name|env
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|arg
init|=
name|arguments
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|execute
argument_list|(
name|terminal
argument_list|,
name|arg
argument_list|,
name|env
argument_list|)
expr_stmt|;
block|}
comment|/**      * Remove the plugin specified by {@code pluginName}.      *      * @param terminal   the terminal to use for input/output      * @param pluginName the name of the plugin to remove      * @param env        the environment for the local node      * @throws IOException   if any I/O exception occurs while performing a file operation      * @throws UserException if plugin name is null      * @throws UserException if plugin directory does not exist      * @throws UserException if the plugin bin directory is not a directory      */
DECL|method|execute
name|void
name|execute
parameter_list|(
name|Terminal
name|terminal
parameter_list|,
name|String
name|pluginName
parameter_list|,
name|Environment
name|env
parameter_list|)
throws|throws
name|IOException
throws|,
name|UserException
block|{
if|if
condition|(
name|pluginName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UserException
argument_list|(
name|ExitCodes
operator|.
name|USAGE
argument_list|,
literal|"plugin name is required"
argument_list|)
throw|;
block|}
name|terminal
operator|.
name|println
argument_list|(
literal|"-> Removing "
operator|+
name|Strings
operator|.
name|coalesceToEmpty
argument_list|(
name|pluginName
argument_list|)
operator|+
literal|"..."
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|pluginDir
init|=
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|pluginDir
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|UserException
argument_list|(
name|ExitCodes
operator|.
name|CONFIG
argument_list|,
literal|"plugin "
operator|+
name|pluginName
operator|+
literal|" not found; run 'elasticsearch-plugin list' to get list of installed plugins"
argument_list|)
throw|;
block|}
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|pluginPaths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|pluginBinDir
init|=
name|env
operator|.
name|binFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|pluginBinDir
argument_list|)
condition|)
block|{
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|pluginBinDir
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|UserException
argument_list|(
name|ExitCodes
operator|.
name|IO_ERROR
argument_list|,
literal|"Bin dir for "
operator|+
name|pluginName
operator|+
literal|" is not a directory"
argument_list|)
throw|;
block|}
name|pluginPaths
operator|.
name|add
argument_list|(
name|pluginBinDir
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|println
argument_list|(
name|VERBOSE
argument_list|,
literal|"Removing: "
operator|+
name|pluginBinDir
argument_list|)
expr_stmt|;
block|}
name|terminal
operator|.
name|println
argument_list|(
name|VERBOSE
argument_list|,
literal|"Removing: "
operator|+
name|pluginDir
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|tmpPluginDir
init|=
name|env
operator|.
name|pluginsFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|".removing-"
operator|+
name|pluginName
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|move
argument_list|(
name|pluginDir
argument_list|,
name|tmpPluginDir
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|AtomicMoveNotSupportedException
name|e
parameter_list|)
block|{
comment|// this can happen on a union filesystem when a plugin is not installed on the top layer; we fall back to a non-atomic move
name|Files
operator|.
name|move
argument_list|(
name|pluginDir
argument_list|,
name|tmpPluginDir
argument_list|)
expr_stmt|;
block|}
name|pluginPaths
operator|.
name|add
argument_list|(
name|tmpPluginDir
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|rm
argument_list|(
name|pluginPaths
operator|.
name|toArray
argument_list|(
operator|new
name|Path
index|[
name|pluginPaths
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|// we preserve the config files in case the user is upgrading the plugin, but we print
comment|// a message so the user knows in case they want to remove manually
specifier|final
name|Path
name|pluginConfigDir
init|=
name|env
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|pluginConfigDir
argument_list|)
condition|)
block|{
name|terminal
operator|.
name|println
argument_list|(
literal|"-> Preserving plugin config files ["
operator|+
name|pluginConfigDir
operator|+
literal|"] in case of upgrade, delete manually if not needed"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

