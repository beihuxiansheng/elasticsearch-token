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
name|elasticsearch
operator|.
name|common
operator|.
name|cli
operator|.
name|CliToolTestCase
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
name|MockTerminal
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
name|CliTool
operator|.
name|ExitStatus
operator|.
name|OK_AND_EXIT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|hasItem
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|is
import|;
end_import

begin_class
DECL|class|PluginCliTests
specifier|public
class|class
name|PluginCliTests
extends|extends
name|CliToolTestCase
block|{
DECL|method|testHelpWorks
specifier|public
name|void
name|testHelpWorks
parameter_list|()
throws|throws
name|Exception
block|{
name|MockTerminal
name|terminal
init|=
operator|new
name|MockTerminal
argument_list|()
decl_stmt|;
comment|/* nocommit         assertThat(new PluginCli(terminal).execute(args("--help")), is(OK_AND_EXIT));         assertTerminalOutputContainsHelpFile(terminal, "/org/elasticsearch/plugins/plugin.help");          terminal.resetOutput();         assertThat(new PluginCli(terminal).execute(args("install -h")), is(OK_AND_EXIT));         assertTerminalOutputContainsHelpFile(terminal, "/org/elasticsearch/plugins/plugin-install.help");         for (String plugin : InstallPluginCommand.OFFICIAL_PLUGINS) {             assertThat(terminal.getOutput(), containsString(plugin));         }          terminal.resetOutput();         assertThat(new PluginCli(terminal).execute(args("remove --help")), is(OK_AND_EXIT));         assertTerminalOutputContainsHelpFile(terminal, "/org/elasticsearch/plugins/plugin-remove.help");          terminal.resetOutput();         assertThat(new PluginCli(terminal).execute(args("list -h")), is(OK_AND_EXIT));         assertTerminalOutputContainsHelpFile(terminal, "/org/elasticsearch/plugins/plugin-list.help");         */
block|}
block|}
end_class

end_unit

