begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.cli
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|cli
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
name|AlreadySelectedException
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
name|CommandLineParser
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
name|DefaultParser
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
name|MissingArgumentException
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
name|MissingOptionException
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
name|UnrecognizedOptionException
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
name|node
operator|.
name|internal
operator|.
name|InternalSettingsPreparer
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
import|;
end_import

begin_comment
comment|/**  * A base class for command-line interface tool.  *  * Two modes are supported:  *  * - Single command mode. The tool exposes a single command that can potentially accept arguments (eg. CLI options).  * - Multi command mode. The tool support multiple commands, each for different tasks, each potentially accepts arguments.  *  * In a multi-command mode. The first argument must be the command name. For example, the plugin manager  * can be seen as a multi-command tool with two possible commands: install and uninstall  *  * The tool is configured using a {@link CliToolConfig} which encapsulates the tool's commands and their  * potential options. The tool also comes with out of the box simple help support (the -h/--help option is  * automatically handled) where the help text is configured in a dedicated *.help files located in the same package  * as the tool.  */
end_comment

begin_class
DECL|class|CliTool
specifier|public
specifier|abstract
class|class
name|CliTool
block|{
comment|// based on sysexits.h
DECL|enum|ExitStatus
specifier|public
enum|enum
name|ExitStatus
block|{
DECL|enum constant|OK
name|OK
argument_list|(
literal|0
argument_list|)
block|,
DECL|enum constant|OK_AND_EXIT
name|OK_AND_EXIT
argument_list|(
literal|0
argument_list|)
block|,
DECL|enum constant|USAGE
name|USAGE
argument_list|(
literal|64
argument_list|)
block|,
comment|/* command line usage error */
DECL|enum constant|DATA_ERROR
name|DATA_ERROR
argument_list|(
literal|65
argument_list|)
block|,
comment|/* data format error */
DECL|enum constant|NO_INPUT
name|NO_INPUT
argument_list|(
literal|66
argument_list|)
block|,
comment|/* cannot open input */
DECL|enum constant|NO_USER
name|NO_USER
argument_list|(
literal|67
argument_list|)
block|,
comment|/* addressee unknown */
DECL|enum constant|NO_HOST
name|NO_HOST
argument_list|(
literal|68
argument_list|)
block|,
comment|/* host name unknown */
DECL|enum constant|UNAVAILABLE
name|UNAVAILABLE
argument_list|(
literal|69
argument_list|)
block|,
comment|/* service unavailable */
DECL|enum constant|CODE_ERROR
name|CODE_ERROR
argument_list|(
literal|70
argument_list|)
block|,
comment|/* internal software error */
DECL|enum constant|CANT_CREATE
name|CANT_CREATE
argument_list|(
literal|73
argument_list|)
block|,
comment|/* can't create (user) output file */
DECL|enum constant|IO_ERROR
name|IO_ERROR
argument_list|(
literal|74
argument_list|)
block|,
comment|/* input/output error */
DECL|enum constant|TEMP_FAILURE
name|TEMP_FAILURE
argument_list|(
literal|75
argument_list|)
block|,
comment|/* temp failure; user is invited to retry */
DECL|enum constant|PROTOCOL
name|PROTOCOL
argument_list|(
literal|76
argument_list|)
block|,
comment|/* remote error in protocol */
DECL|enum constant|NOPERM
name|NOPERM
argument_list|(
literal|77
argument_list|)
block|,
comment|/* permission denied */
DECL|enum constant|CONFIG
name|CONFIG
argument_list|(
literal|78
argument_list|)
block|;
comment|/* configuration error */
DECL|field|status
specifier|final
name|int
name|status
decl_stmt|;
DECL|method|ExitStatus
name|ExitStatus
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
DECL|method|status
specifier|public
name|int
name|status
parameter_list|()
block|{
return|return
name|status
return|;
block|}
block|}
DECL|field|terminal
specifier|protected
specifier|final
name|Terminal
name|terminal
decl_stmt|;
DECL|field|env
specifier|protected
specifier|final
name|Environment
name|env
decl_stmt|;
DECL|field|settings
specifier|protected
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|CliToolConfig
name|config
decl_stmt|;
DECL|method|CliTool
specifier|protected
name|CliTool
parameter_list|(
name|CliToolConfig
name|config
parameter_list|)
block|{
name|this
argument_list|(
name|config
argument_list|,
name|Terminal
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
DECL|method|CliTool
specifier|protected
name|CliTool
parameter_list|(
name|CliToolConfig
name|config
parameter_list|,
name|Terminal
name|terminal
parameter_list|)
block|{
if|if
condition|(
name|config
operator|.
name|cmds
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"At least one command must be configured"
argument_list|)
throw|;
block|}
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|terminal
operator|=
name|terminal
expr_stmt|;
name|env
operator|=
name|InternalSettingsPreparer
operator|.
name|prepareEnvironment
argument_list|(
name|EMPTY_SETTINGS
argument_list|,
name|terminal
argument_list|)
expr_stmt|;
name|settings
operator|=
name|env
operator|.
name|settings
argument_list|()
expr_stmt|;
block|}
DECL|method|execute
specifier|public
specifier|final
name|ExitStatus
name|execute
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
comment|// first lets see if the user requests tool help. We're doing it only if
comment|// this is a multi-command tool. If it's a single command tool, the -h/--help
comment|// option will be taken care of on the command level
if|if
condition|(
operator|!
name|config
operator|.
name|isSingle
argument_list|()
operator|&&
name|args
operator|.
name|length
operator|>
literal|0
operator|&&
operator|(
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"-h"
argument_list|)
operator|||
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"--help"
argument_list|)
operator|)
condition|)
block|{
name|config
operator|.
name|printUsage
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
return|return
name|ExitStatus
operator|.
name|OK_AND_EXIT
return|;
block|}
name|CliToolConfig
operator|.
name|Cmd
name|cmd
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|isSingle
argument_list|()
condition|)
block|{
name|cmd
operator|=
name|config
operator|.
name|single
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|terminal
operator|.
name|println
argument_list|(
name|Terminal
operator|.
name|Verbosity
operator|.
name|SILENT
argument_list|,
literal|"ERROR: command not specified"
argument_list|)
expr_stmt|;
name|config
operator|.
name|printUsage
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
return|return
name|ExitStatus
operator|.
name|USAGE
return|;
block|}
name|String
name|cmdName
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|cmd
operator|=
name|config
operator|.
name|cmd
argument_list|(
name|cmdName
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmd
operator|==
literal|null
condition|)
block|{
name|terminal
operator|.
name|println
argument_list|(
name|Terminal
operator|.
name|Verbosity
operator|.
name|SILENT
argument_list|,
literal|"ERROR: unknown command ["
operator|+
name|cmdName
operator|+
literal|"]. Use [-h] option to list available commands"
argument_list|)
expr_stmt|;
return|return
name|ExitStatus
operator|.
name|USAGE
return|;
block|}
comment|// we now remove the command name from the args
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|args
operator|=
operator|new
name|String
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|cmdArgs
init|=
operator|new
name|String
index|[
name|args
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|args
argument_list|,
literal|1
argument_list|,
name|cmdArgs
argument_list|,
literal|0
argument_list|,
name|cmdArgs
operator|.
name|length
argument_list|)
expr_stmt|;
name|args
operator|=
name|cmdArgs
expr_stmt|;
block|}
block|}
try|try
block|{
return|return
name|parse
argument_list|(
name|cmd
argument_list|,
name|args
argument_list|)
operator|.
name|execute
argument_list|(
name|settings
argument_list|,
name|env
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UserError
name|error
parameter_list|)
block|{
name|terminal
operator|.
name|println
argument_list|(
name|Terminal
operator|.
name|Verbosity
operator|.
name|SILENT
argument_list|,
literal|"ERROR: "
operator|+
name|error
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ExitStatus
operator|.
name|USAGE
return|;
comment|//return error.exitCode;
block|}
block|}
DECL|method|parse
specifier|public
name|Command
name|parse
parameter_list|(
name|String
name|cmdName
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|CliToolConfig
operator|.
name|Cmd
name|cmd
init|=
name|config
operator|.
name|cmd
argument_list|(
name|cmdName
argument_list|)
decl_stmt|;
return|return
name|parse
argument_list|(
name|cmd
argument_list|,
name|args
argument_list|)
return|;
block|}
DECL|method|parse
specifier|public
name|Command
name|parse
parameter_list|(
name|CliToolConfig
operator|.
name|Cmd
name|cmd
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|CommandLineParser
name|parser
init|=
operator|new
name|DefaultParser
argument_list|()
decl_stmt|;
name|CommandLine
name|cli
init|=
name|parser
operator|.
name|parse
argument_list|(
name|CliToolConfig
operator|.
name|OptionsSource
operator|.
name|HELP
operator|.
name|options
argument_list|()
argument_list|,
name|args
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|cli
operator|.
name|hasOption
argument_list|(
literal|"h"
argument_list|)
condition|)
block|{
return|return
name|helpCmd
argument_list|(
name|cmd
argument_list|)
return|;
block|}
try|try
block|{
name|cli
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|cmd
operator|.
name|options
argument_list|()
argument_list|,
name|args
argument_list|,
name|cmd
operator|.
name|isStopAtNonOption
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadySelectedException
decl||
name|MissingArgumentException
decl||
name|MissingOptionException
decl||
name|UnrecognizedOptionException
name|e
parameter_list|)
block|{
comment|// intentionally drop the stack trace here as these are really user errors,
comment|// the stack trace into cli parsing lib is not important
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
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|cli
operator|.
name|hasOption
argument_list|(
literal|"v"
argument_list|)
condition|)
block|{
name|terminal
operator|.
name|setVerbosity
argument_list|(
name|Terminal
operator|.
name|Verbosity
operator|.
name|VERBOSE
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cli
operator|.
name|hasOption
argument_list|(
literal|"s"
argument_list|)
condition|)
block|{
name|terminal
operator|.
name|setVerbosity
argument_list|(
name|Terminal
operator|.
name|Verbosity
operator|.
name|SILENT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|terminal
operator|.
name|setVerbosity
argument_list|(
name|Terminal
operator|.
name|Verbosity
operator|.
name|NORMAL
argument_list|)
expr_stmt|;
block|}
return|return
name|parse
argument_list|(
name|cmd
operator|.
name|name
argument_list|()
argument_list|,
name|cli
argument_list|)
return|;
block|}
DECL|method|helpCmd
specifier|protected
name|Command
operator|.
name|Help
name|helpCmd
parameter_list|(
name|CliToolConfig
operator|.
name|Cmd
name|cmd
parameter_list|)
block|{
return|return
operator|new
name|Command
operator|.
name|Help
argument_list|(
name|cmd
argument_list|,
name|terminal
argument_list|)
return|;
block|}
DECL|method|exitCmd
specifier|protected
specifier|static
name|Command
operator|.
name|Exit
name|exitCmd
parameter_list|(
name|ExitStatus
name|status
parameter_list|)
block|{
return|return
operator|new
name|Command
operator|.
name|Exit
argument_list|(
literal|null
argument_list|,
name|status
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|exitCmd
specifier|protected
specifier|static
name|Command
operator|.
name|Exit
name|exitCmd
parameter_list|(
name|ExitStatus
name|status
parameter_list|,
name|Terminal
name|terminal
parameter_list|,
name|String
name|msg
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
operator|new
name|Command
operator|.
name|Exit
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
name|msg
argument_list|,
name|args
argument_list|)
argument_list|,
name|status
argument_list|,
name|terminal
argument_list|)
return|;
block|}
DECL|method|parse
specifier|protected
specifier|abstract
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
function_decl|;
DECL|class|Command
specifier|public
specifier|static
specifier|abstract
class|class
name|Command
block|{
DECL|field|terminal
specifier|protected
specifier|final
name|Terminal
name|terminal
decl_stmt|;
DECL|method|Command
specifier|protected
name|Command
parameter_list|(
name|Terminal
name|terminal
parameter_list|)
block|{
name|this
operator|.
name|terminal
operator|=
name|terminal
expr_stmt|;
block|}
DECL|method|execute
specifier|public
specifier|abstract
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
function_decl|;
DECL|class|Help
specifier|public
specifier|static
class|class
name|Help
extends|extends
name|Command
block|{
DECL|field|cmd
specifier|private
specifier|final
name|CliToolConfig
operator|.
name|Cmd
name|cmd
decl_stmt|;
DECL|method|Help
specifier|private
name|Help
parameter_list|(
name|CliToolConfig
operator|.
name|Cmd
name|cmd
parameter_list|,
name|Terminal
name|terminal
parameter_list|)
block|{
name|super
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
name|this
operator|.
name|cmd
operator|=
name|cmd
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
name|cmd
operator|.
name|printUsage
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
return|return
name|ExitStatus
operator|.
name|OK_AND_EXIT
return|;
block|}
block|}
DECL|class|Exit
specifier|public
specifier|static
class|class
name|Exit
extends|extends
name|Command
block|{
DECL|field|msg
specifier|private
specifier|final
name|String
name|msg
decl_stmt|;
DECL|field|status
specifier|private
specifier|final
name|ExitStatus
name|status
decl_stmt|;
DECL|method|Exit
specifier|private
name|Exit
parameter_list|(
name|String
name|msg
parameter_list|,
name|ExitStatus
name|status
parameter_list|,
name|Terminal
name|terminal
parameter_list|)
block|{
name|super
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
name|this
operator|.
name|msg
operator|=
name|msg
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
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
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|status
operator|!=
name|ExitStatus
operator|.
name|OK
condition|)
block|{
name|terminal
operator|.
name|println
argument_list|(
name|Terminal
operator|.
name|Verbosity
operator|.
name|SILENT
argument_list|,
literal|"ERROR: "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|terminal
operator|.
name|println
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|status
return|;
block|}
DECL|method|status
specifier|public
name|ExitStatus
name|status
parameter_list|()
block|{
return|return
name|status
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

