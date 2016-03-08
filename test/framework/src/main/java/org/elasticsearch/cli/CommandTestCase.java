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
name|joptsimple
operator|.
name|OptionSet
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
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_comment
comment|/**  * A base test case for cli tools.  */
end_comment

begin_class
DECL|class|CommandTestCase
specifier|public
specifier|abstract
class|class
name|CommandTestCase
extends|extends
name|ESTestCase
block|{
comment|/** The terminal that execute uses. */
DECL|field|terminal
specifier|protected
specifier|final
name|MockTerminal
name|terminal
init|=
operator|new
name|MockTerminal
argument_list|()
decl_stmt|;
comment|/** The last command that was executed. */
DECL|field|command
specifier|protected
name|Command
name|command
decl_stmt|;
annotation|@
name|Before
DECL|method|resetTerminal
specifier|public
name|void
name|resetTerminal
parameter_list|()
block|{
name|terminal
operator|.
name|reset
argument_list|()
expr_stmt|;
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
comment|/** Creates a Command to test execution. */
DECL|method|newCommand
specifier|protected
specifier|abstract
name|Command
name|newCommand
parameter_list|()
function_decl|;
comment|/**      * Runs the command with the given args.      *      * Output can be found in {@link #terminal}.      * The command created can be found in {@link #command}.      */
DECL|method|execute
specifier|public
name|String
name|execute
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|command
operator|=
name|newCommand
argument_list|()
expr_stmt|;
name|command
operator|.
name|mainWithoutErrorHandling
argument_list|(
name|args
argument_list|,
name|terminal
argument_list|)
expr_stmt|;
return|return
name|terminal
operator|.
name|getOutput
argument_list|()
return|;
block|}
block|}
end_class

end_unit

