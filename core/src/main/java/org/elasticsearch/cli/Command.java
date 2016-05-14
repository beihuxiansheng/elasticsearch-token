begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cli
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cli
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionException
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
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
name|elasticsearch
operator|.
name|common
operator|.
name|SuppressForbidden
import|;
end_import

begin_comment
comment|/**  * An action to execute within a cli.  */
end_comment

begin_class
DECL|class|Command
specifier|public
specifier|abstract
class|class
name|Command
block|{
comment|/** A description of the command, used in the help output. */
DECL|field|description
specifier|protected
specifier|final
name|String
name|description
decl_stmt|;
comment|/** The option parser for this command. */
DECL|field|parser
specifier|protected
specifier|final
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
DECL|field|helpOption
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|helpOption
init|=
name|parser
operator|.
name|acceptsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"h"
argument_list|,
literal|"help"
argument_list|)
argument_list|,
literal|"show help"
argument_list|)
operator|.
name|forHelp
argument_list|()
decl_stmt|;
DECL|field|silentOption
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|silentOption
init|=
name|parser
operator|.
name|acceptsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"s"
argument_list|,
literal|"silent"
argument_list|)
argument_list|,
literal|"show minimal output"
argument_list|)
decl_stmt|;
DECL|field|verboseOption
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|verboseOption
init|=
name|parser
operator|.
name|acceptsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"v"
argument_list|,
literal|"verbose"
argument_list|)
argument_list|,
literal|"show verbose output"
argument_list|)
decl_stmt|;
DECL|method|Command
specifier|public
name|Command
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
comment|/** Parses options for this command from args and executes it. */
DECL|method|main
specifier|public
specifier|final
name|int
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|Terminal
name|terminal
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|mainWithoutErrorHandling
argument_list|(
name|args
argument_list|,
name|terminal
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OptionException
name|e
parameter_list|)
block|{
name|printHelp
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ExitCodes
operator|.
name|USAGE
return|;
block|}
catch|catch
parameter_list|(
name|UserError
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|exitCode
operator|==
name|ExitCodes
operator|.
name|USAGE
condition|)
block|{
name|printHelp
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
block|}
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|e
operator|.
name|exitCode
return|;
block|}
return|return
name|ExitCodes
operator|.
name|OK
return|;
block|}
comment|/**      * Executes the command, but all errors are thrown.      */
DECL|method|mainWithoutErrorHandling
name|void
name|mainWithoutErrorHandling
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|Terminal
name|terminal
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|OptionSet
name|options
init|=
name|parser
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|helpOption
argument_list|)
condition|)
block|{
name|printHelp
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|silentOption
argument_list|)
condition|)
block|{
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|verboseOption
argument_list|)
condition|)
block|{
comment|// mutually exclusive, we can remove this with jopt-simple 5.0, which natively supports it
throw|throw
operator|new
name|UserError
argument_list|(
name|ExitCodes
operator|.
name|USAGE
argument_list|,
literal|"Cannot specify -s and -v together"
argument_list|)
throw|;
block|}
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
elseif|else
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|verboseOption
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
name|execute
argument_list|(
name|terminal
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
comment|/** Prints a help message for the command to the terminal. */
DECL|method|printHelp
specifier|private
name|void
name|printHelp
parameter_list|(
name|Terminal
name|terminal
parameter_list|)
throws|throws
name|IOException
block|{
name|terminal
operator|.
name|println
argument_list|(
name|description
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|printAdditionalHelp
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
name|parser
operator|.
name|printHelpOn
argument_list|(
name|terminal
operator|.
name|getWriter
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Prints additional help information, specific to the command */
DECL|method|printAdditionalHelp
specifier|protected
name|void
name|printAdditionalHelp
parameter_list|(
name|Terminal
name|terminal
parameter_list|)
block|{}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Allowed to exit explicitly from #main()"
argument_list|)
DECL|method|exit
specifier|protected
specifier|static
name|void
name|exit
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|System
operator|.
name|exit
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
comment|/**      * Executes this command.      *      * Any runtime user errors (like an input file that does not exist), should throw a {@link UserError}. */
DECL|method|execute
specifier|protected
specifier|abstract
name|void
name|execute
parameter_list|(
name|Terminal
name|terminal
parameter_list|,
name|OptionSet
name|options
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

