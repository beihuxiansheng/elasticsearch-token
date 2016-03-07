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
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Console
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
comment|/**  * A Terminal wraps access to reading input and writing output for a {@link CliTool}.  *  * The available methods are similar to those of {@link Console}, with the ability  * to read either normal text or a password, and the ability to print a line  * of text. Printing is also gated by the {@link Verbosity} of the terminal,  * which allows {@link #println(Verbosity,String)} calls which act like a logger,  * only actually printing if the verbosity level of the terminal is above  * the verbosity of the message. */
end_comment

begin_class
DECL|class|Terminal
specifier|public
specifier|abstract
class|class
name|Terminal
block|{
comment|/** The default terminal implementation, which will be a console if available, or stdout/stderr if not. */
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|Terminal
name|DEFAULT
init|=
name|ConsoleTerminal
operator|.
name|isSupported
argument_list|()
condition|?
operator|new
name|ConsoleTerminal
argument_list|()
else|:
operator|new
name|SystemTerminal
argument_list|()
decl_stmt|;
comment|/** Defines the available verbosity levels of messages to be printed. */
DECL|enum|Verbosity
specifier|public
enum|enum
name|Verbosity
block|{
DECL|enum constant|SILENT
name|SILENT
block|,
comment|/* always printed */
DECL|enum constant|NORMAL
name|NORMAL
block|,
comment|/* printed when no options are given to cli */
DECL|enum constant|VERBOSE
name|VERBOSE
comment|/* printed only when cli is passed verbose option */
block|}
comment|/** The current verbosity for the terminal, defaulting to {@link Verbosity#NORMAL}. */
DECL|field|verbosity
specifier|private
name|Verbosity
name|verbosity
init|=
name|Verbosity
operator|.
name|NORMAL
decl_stmt|;
comment|/** The newline used when calling println. */
DECL|field|lineSeparator
specifier|private
specifier|final
name|String
name|lineSeparator
decl_stmt|;
DECL|method|Terminal
specifier|protected
name|Terminal
parameter_list|(
name|String
name|lineSeparator
parameter_list|)
block|{
name|this
operator|.
name|lineSeparator
operator|=
name|lineSeparator
expr_stmt|;
block|}
comment|/** Sets the verbosity of the terminal. */
DECL|method|setVerbosity
name|void
name|setVerbosity
parameter_list|(
name|Verbosity
name|verbosity
parameter_list|)
block|{
name|this
operator|.
name|verbosity
operator|=
name|verbosity
expr_stmt|;
block|}
comment|/** Reads clear text from the terminal input. See {@link Console#readLine()}. */
DECL|method|readText
specifier|public
specifier|abstract
name|String
name|readText
parameter_list|(
name|String
name|prompt
parameter_list|)
function_decl|;
comment|/** Reads password text from the terminal input. See {@link Console#readPassword()}}. */
DECL|method|readSecret
specifier|public
specifier|abstract
name|char
index|[]
name|readSecret
parameter_list|(
name|String
name|prompt
parameter_list|)
function_decl|;
comment|/** Returns a Writer which can be used to write to the terminal directly. */
DECL|method|getWriter
specifier|public
specifier|abstract
name|PrintWriter
name|getWriter
parameter_list|()
function_decl|;
comment|/** Prints a line to the terminal at {@link Verbosity#NORMAL} verbosity level. */
DECL|method|println
specifier|public
specifier|final
name|void
name|println
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|println
argument_list|(
name|Verbosity
operator|.
name|NORMAL
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
comment|/** Prints a line to the terminal at {@code verbosity} level. */
DECL|method|println
specifier|public
specifier|final
name|void
name|println
parameter_list|(
name|Verbosity
name|verbosity
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|verbosity
operator|.
name|ordinal
argument_list|()
operator|>=
name|verbosity
operator|.
name|ordinal
argument_list|()
condition|)
block|{
name|getWriter
argument_list|()
operator|.
name|print
argument_list|(
name|msg
operator|+
name|lineSeparator
argument_list|)
expr_stmt|;
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ConsoleTerminal
specifier|private
specifier|static
class|class
name|ConsoleTerminal
extends|extends
name|Terminal
block|{
DECL|field|console
specifier|private
specifier|static
specifier|final
name|Console
name|console
init|=
name|System
operator|.
name|console
argument_list|()
decl_stmt|;
DECL|method|ConsoleTerminal
name|ConsoleTerminal
parameter_list|()
block|{
name|super
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|isSupported
specifier|static
name|boolean
name|isSupported
parameter_list|()
block|{
return|return
name|console
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getWriter
specifier|public
name|PrintWriter
name|getWriter
parameter_list|()
block|{
return|return
name|console
operator|.
name|writer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readText
specifier|public
name|String
name|readText
parameter_list|(
name|String
name|prompt
parameter_list|)
block|{
return|return
name|console
operator|.
name|readLine
argument_list|(
literal|"%s"
argument_list|,
name|prompt
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readSecret
specifier|public
name|char
index|[]
name|readSecret
parameter_list|(
name|String
name|prompt
parameter_list|)
block|{
return|return
name|console
operator|.
name|readPassword
argument_list|(
literal|"%s"
argument_list|,
name|prompt
argument_list|)
return|;
block|}
block|}
DECL|class|SystemTerminal
specifier|private
specifier|static
class|class
name|SystemTerminal
extends|extends
name|Terminal
block|{
DECL|field|writer
specifier|private
specifier|final
name|PrintWriter
name|writer
init|=
name|newWriter
argument_list|()
decl_stmt|;
DECL|method|SystemTerminal
name|SystemTerminal
parameter_list|()
block|{
name|super
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Writer for System.out"
argument_list|)
DECL|method|newWriter
specifier|private
specifier|static
name|PrintWriter
name|newWriter
parameter_list|()
block|{
return|return
operator|new
name|PrintWriter
argument_list|(
name|System
operator|.
name|out
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getWriter
specifier|public
name|PrintWriter
name|getWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
annotation|@
name|Override
DECL|method|readText
specifier|public
name|String
name|readText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|getWriter
argument_list|()
operator|.
name|print
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|reader
operator|.
name|readLine
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|readSecret
specifier|public
name|char
index|[]
name|readSecret
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
name|readText
argument_list|(
name|text
argument_list|)
operator|.
name|toCharArray
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

