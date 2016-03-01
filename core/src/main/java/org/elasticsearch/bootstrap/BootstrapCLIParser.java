begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bootstrap
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Build
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
name|SuppressForbidden
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
name|CliToolConfig
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
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Properties
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
name|CliToolConfig
operator|.
name|Builder
operator|.
name|cmd
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
name|CliToolConfig
operator|.
name|Builder
operator|.
name|optionBuilder
import|;
end_import

begin_class
DECL|class|BootstrapCLIParser
specifier|final
class|class
name|BootstrapCLIParser
extends|extends
name|CliTool
block|{
DECL|field|CONFIG
specifier|private
specifier|static
specifier|final
name|CliToolConfig
name|CONFIG
init|=
name|CliToolConfig
operator|.
name|config
argument_list|(
literal|"elasticsearch"
argument_list|,
name|BootstrapCLIParser
operator|.
name|class
argument_list|)
operator|.
name|cmds
argument_list|(
name|Start
operator|.
name|CMD
argument_list|,
name|Version
operator|.
name|CMD
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|method|BootstrapCLIParser
specifier|public
name|BootstrapCLIParser
parameter_list|()
block|{
name|super
argument_list|(
name|CONFIG
argument_list|)
expr_stmt|;
block|}
DECL|method|BootstrapCLIParser
specifier|public
name|BootstrapCLIParser
parameter_list|(
name|Terminal
name|terminal
parameter_list|)
block|{
name|super
argument_list|(
name|CONFIG
argument_list|,
name|terminal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|protected
name|Command
name|parse
parameter_list|(
name|String
name|cmdName
parameter_list|,
name|CommandLine
name|cli
parameter_list|)
throws|throws
name|Exception
block|{
switch|switch
condition|(
name|cmdName
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
condition|)
block|{
case|case
name|Start
operator|.
name|NAME
case|:
return|return
name|Start
operator|.
name|parse
argument_list|(
name|terminal
argument_list|,
name|cli
argument_list|)
return|;
case|case
name|Version
operator|.
name|NAME
case|:
return|return
name|Version
operator|.
name|parse
argument_list|(
name|terminal
argument_list|,
name|cli
argument_list|)
return|;
default|default:
assert|assert
literal|false
operator|:
literal|"should never get here, if the user enters an unknown command, an error message should be shown before parse is called"
assert|;
return|return
literal|null
return|;
block|}
block|}
DECL|class|Version
specifier|static
class|class
name|Version
extends|extends
name|CliTool
operator|.
name|Command
block|{
DECL|field|NAME
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"version"
decl_stmt|;
DECL|field|CMD
specifier|private
specifier|static
specifier|final
name|CliToolConfig
operator|.
name|Cmd
name|CMD
init|=
name|cmd
argument_list|(
name|NAME
argument_list|,
name|Version
operator|.
name|class
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|method|parse
specifier|public
specifier|static
name|Command
name|parse
parameter_list|(
name|Terminal
name|terminal
parameter_list|,
name|CommandLine
name|cli
parameter_list|)
block|{
return|return
operator|new
name|Version
argument_list|(
name|terminal
argument_list|)
return|;
block|}
DECL|method|Version
specifier|public
name|Version
parameter_list|(
name|Terminal
name|terminal
parameter_list|)
block|{
name|super
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
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
literal|"Version: "
operator|+
name|org
operator|.
name|elasticsearch
operator|.
name|Version
operator|.
name|CURRENT
operator|+
literal|", Build: "
operator|+
name|Build
operator|.
name|CURRENT
operator|.
name|shortHash
argument_list|()
operator|+
literal|"/"
operator|+
name|Build
operator|.
name|CURRENT
operator|.
name|date
argument_list|()
operator|+
literal|", JVM: "
operator|+
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ExitStatus
operator|.
name|OK_AND_EXIT
return|;
block|}
block|}
DECL|class|Start
specifier|static
class|class
name|Start
extends|extends
name|CliTool
operator|.
name|Command
block|{
DECL|field|NAME
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"start"
decl_stmt|;
DECL|field|CMD
specifier|private
specifier|static
specifier|final
name|CliToolConfig
operator|.
name|Cmd
name|CMD
init|=
name|cmd
argument_list|(
name|NAME
argument_list|,
name|Start
operator|.
name|class
argument_list|)
operator|.
name|options
argument_list|(
name|optionBuilder
argument_list|(
literal|"d"
argument_list|,
literal|"daemonize"
argument_list|)
operator|.
name|hasArg
argument_list|(
literal|false
argument_list|)
operator|.
name|required
argument_list|(
literal|false
argument_list|)
argument_list|,
name|optionBuilder
argument_list|(
literal|"p"
argument_list|,
literal|"pidfile"
argument_list|)
operator|.
name|hasArg
argument_list|(
literal|true
argument_list|)
operator|.
name|required
argument_list|(
literal|false
argument_list|)
argument_list|,
name|optionBuilder
argument_list|(
literal|"V"
argument_list|,
literal|"version"
argument_list|)
operator|.
name|hasArg
argument_list|(
literal|false
argument_list|)
operator|.
name|required
argument_list|(
literal|false
argument_list|)
argument_list|,
name|Option
operator|.
name|builder
argument_list|(
literal|"D"
argument_list|)
operator|.
name|argName
argument_list|(
literal|"property=value"
argument_list|)
operator|.
name|valueSeparator
argument_list|(
literal|'='
argument_list|)
operator|.
name|numberOfArgs
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|stopAtNonOption
argument_list|(
literal|true
argument_list|)
comment|// needed to parse the --foo.bar options, so this parser must be lenient
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// TODO: don't use system properties as a way to do this, its horrible...
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Sets system properties passed as CLI parameters"
argument_list|)
DECL|method|parse
specifier|public
specifier|static
name|Command
name|parse
parameter_list|(
name|Terminal
name|terminal
parameter_list|,
name|CommandLine
name|cli
parameter_list|)
throws|throws
name|UserError
block|{
if|if
condition|(
name|cli
operator|.
name|hasOption
argument_list|(
literal|"V"
argument_list|)
condition|)
block|{
return|return
name|Version
operator|.
name|parse
argument_list|(
name|terminal
argument_list|,
name|cli
argument_list|)
return|;
block|}
if|if
condition|(
name|cli
operator|.
name|hasOption
argument_list|(
literal|"d"
argument_list|)
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"es.foreground"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
name|String
name|pidFile
init|=
name|cli
operator|.
name|getOptionValue
argument_list|(
literal|"pidfile"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|pidFile
argument_list|)
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"es.pidfile"
argument_list|,
name|pidFile
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cli
operator|.
name|hasOption
argument_list|(
literal|"D"
argument_list|)
condition|)
block|{
name|Properties
name|properties
init|=
name|cli
operator|.
name|getOptionProperties
argument_list|(
literal|"D"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|properties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|propertyName
init|=
name|key
operator|.
name|startsWith
argument_list|(
literal|"es."
argument_list|)
condition|?
name|key
else|:
literal|"es."
operator|+
name|key
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|propertyName
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// hacky way to extract all the fancy extra args, there is no CLI tool helper for this
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
init|=
name|cli
operator|.
name|getArgList
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|arg
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|arg
operator|.
name|startsWith
argument_list|(
literal|"--"
argument_list|)
condition|)
block|{
if|if
condition|(
name|arg
operator|.
name|startsWith
argument_list|(
literal|"-D"
argument_list|)
operator|||
name|arg
operator|.
name|startsWith
argument_list|(
literal|"-d"
argument_list|)
operator|||
name|arg
operator|.
name|startsWith
argument_list|(
literal|"-p"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UserError
argument_list|(
name|ExitStatus
operator|.
name|USAGE
operator|.
name|status
argument_list|()
argument_list|,
literal|"Parameter ["
operator|+
name|arg
operator|+
literal|"] starting with \"-D\", \"-d\" or \"-p\" must be before any parameters starting with --"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UserError
argument_list|(
name|ExitStatus
operator|.
name|USAGE
operator|.
name|status
argument_list|()
argument_list|,
literal|"Parameter ["
operator|+
name|arg
operator|+
literal|"]does not start with --"
argument_list|)
throw|;
block|}
block|}
comment|// if there is no = sign, we have to get the next argu
name|arg
operator|=
name|arg
operator|.
name|replace
argument_list|(
literal|"--"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|arg
operator|.
name|contains
argument_list|(
literal|"="
argument_list|)
condition|)
block|{
name|String
index|[]
name|splitArg
init|=
name|arg
operator|.
name|split
argument_list|(
literal|"="
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|splitArg
index|[
literal|0
index|]
decl_stmt|;
name|String
name|value
init|=
name|splitArg
index|[
literal|1
index|]
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"es."
operator|+
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|value
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"--"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UserError
argument_list|(
name|ExitStatus
operator|.
name|USAGE
operator|.
name|status
argument_list|()
argument_list|,
literal|"Parameter ["
operator|+
name|arg
operator|+
literal|"] needs value"
argument_list|)
throw|;
block|}
name|properties
operator|.
name|put
argument_list|(
literal|"es."
operator|+
name|arg
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UserError
argument_list|(
name|ExitStatus
operator|.
name|USAGE
operator|.
name|status
argument_list|()
argument_list|,
literal|"Parameter ["
operator|+
name|arg
operator|+
literal|"] needs value"
argument_list|)
throw|;
block|}
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|properties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Start
argument_list|(
name|terminal
argument_list|)
return|;
block|}
DECL|method|Start
specifier|public
name|Start
parameter_list|(
name|Terminal
name|terminal
parameter_list|)
block|{
name|super
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
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
return|return
name|ExitStatus
operator|.
name|OK
return|;
block|}
block|}
block|}
end_class

end_unit

