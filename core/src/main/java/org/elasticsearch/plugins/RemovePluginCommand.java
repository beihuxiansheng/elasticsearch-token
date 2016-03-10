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
name|cli
operator|.
name|CliTool
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
name|common
operator|.
name|cli
operator|.
name|UserError
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
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
name|CliTool
operator|.
name|Command
block|{
DECL|field|pluginName
specifier|private
specifier|final
name|String
name|pluginName
decl_stmt|;
DECL|method|RemovePluginCommand
specifier|public
name|RemovePluginCommand
parameter_list|(
name|Terminal
name|terminal
parameter_list|,
name|String
name|pluginName
parameter_list|)
block|{
name|super
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
name|this
operator|.
name|pluginName
operator|=
name|pluginName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|CliTool
operator|.
name|ExitStatus
name|execute
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Environment
name|env
parameter_list|)
throws|throws
name|Exception
block|{
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
name|UserError
argument_list|(
name|CliTool
operator|.
name|ExitStatus
operator|.
name|USAGE
argument_list|,
literal|"Plugin "
operator|+
name|pluginName
operator|+
literal|" not found. Run 'plugin list' to get list of installed plugins."
argument_list|)
throw|;
block|}
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
name|UserError
argument_list|(
name|CliTool
operator|.
name|ExitStatus
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
return|return
name|CliTool
operator|.
name|ExitStatus
operator|.
name|OK
return|;
block|}
block|}
end_class

end_unit
