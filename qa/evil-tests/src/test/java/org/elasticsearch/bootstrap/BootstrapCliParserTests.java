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
name|Version
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
operator|.
name|ExitStatus
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
name|collect
operator|.
name|Tuple
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
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Arrays
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
name|cli
operator|.
name|CliTool
operator|.
name|ExitStatus
operator|.
name|OK
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
name|USAGE
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|nullValue
import|;
end_import

begin_class
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"modifies system properties intentionally"
argument_list|)
DECL|class|BootstrapCliParserTests
specifier|public
class|class
name|BootstrapCliParserTests
extends|extends
name|CliToolTestCase
block|{
DECL|field|terminal
specifier|private
name|CaptureOutputTerminal
name|terminal
init|=
operator|new
name|CaptureOutputTerminal
argument_list|()
decl_stmt|;
DECL|field|propertiesToClear
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|propertiesToClear
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|After
DECL|method|clearProperties
specifier|public
name|void
name|clearProperties
parameter_list|()
block|{
for|for
control|(
name|String
name|property
range|:
name|propertiesToClear
control|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|property
argument_list|)
expr_stmt|;
block|}
name|propertiesToClear
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|testThatVersionIsReturned
specifier|public
name|void
name|testThatVersionIsReturned
parameter_list|()
throws|throws
name|Exception
block|{
name|BootstrapCLIParser
name|parser
init|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
decl_stmt|;
name|ExitStatus
name|status
init|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"version"
argument_list|)
argument_list|)
decl_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|OK_AND_EXIT
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
name|Version
operator|.
name|CURRENT
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
name|Build
operator|.
name|CURRENT
operator|.
name|shortHash
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
name|Build
operator|.
name|CURRENT
operator|.
name|date
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|version
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatVersionIsReturnedAsStartParameter
specifier|public
name|void
name|testThatVersionIsReturnedAsStartParameter
parameter_list|()
throws|throws
name|Exception
block|{
name|BootstrapCLIParser
name|parser
init|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
decl_stmt|;
name|ExitStatus
name|status
init|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start -V"
argument_list|)
argument_list|)
decl_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|OK_AND_EXIT
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
name|Version
operator|.
name|CURRENT
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
name|Build
operator|.
name|CURRENT
operator|.
name|shortHash
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
name|Build
operator|.
name|CURRENT
operator|.
name|date
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|version
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|CaptureOutputTerminal
name|terminal
init|=
operator|new
name|CaptureOutputTerminal
argument_list|()
decl_stmt|;
name|parser
operator|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
name|status
operator|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start --version"
argument_list|)
argument_list|)
expr_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|OK_AND_EXIT
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
name|Version
operator|.
name|CURRENT
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
name|Build
operator|.
name|CURRENT
operator|.
name|shortHash
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
name|Build
operator|.
name|CURRENT
operator|.
name|date
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|version
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatPidFileCanBeConfigured
specifier|public
name|void
name|testThatPidFileCanBeConfigured
parameter_list|()
throws|throws
name|Exception
block|{
name|BootstrapCLIParser
name|parser
init|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
decl_stmt|;
name|registerProperties
argument_list|(
literal|"es.pidfile"
argument_list|)
expr_stmt|;
name|ExitStatus
name|status
init|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start --pidfile"
argument_list|)
argument_list|)
decl_stmt|;
comment|// missing pid file
name|assertStatus
argument_list|(
name|status
argument_list|,
name|USAGE
argument_list|)
expr_stmt|;
comment|// good cases
name|status
operator|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start --pidfile /tmp/pid"
argument_list|)
argument_list|)
expr_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|OK
argument_list|)
expr_stmt|;
name|assertSystemProperty
argument_list|(
literal|"es.pidfile"
argument_list|,
literal|"/tmp/pid"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"es.pidfile"
argument_list|)
expr_stmt|;
name|status
operator|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start -p /tmp/pid"
argument_list|)
argument_list|)
expr_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|OK
argument_list|)
expr_stmt|;
name|assertSystemProperty
argument_list|(
literal|"es.pidfile"
argument_list|,
literal|"/tmp/pid"
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatParsingDaemonizeWorks
specifier|public
name|void
name|testThatParsingDaemonizeWorks
parameter_list|()
throws|throws
name|Exception
block|{
name|BootstrapCLIParser
name|parser
init|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
decl_stmt|;
name|registerProperties
argument_list|(
literal|"es.foreground"
argument_list|)
expr_stmt|;
name|ExitStatus
name|status
init|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start -d"
argument_list|)
argument_list|)
decl_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|OK
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.foreground"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatNotDaemonizingDoesNotConfigureProperties
specifier|public
name|void
name|testThatNotDaemonizingDoesNotConfigureProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|BootstrapCLIParser
name|parser
init|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
decl_stmt|;
name|registerProperties
argument_list|(
literal|"es.foreground"
argument_list|)
expr_stmt|;
name|ExitStatus
name|status
init|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start"
argument_list|)
argument_list|)
decl_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|OK
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.foreground"
argument_list|)
argument_list|,
name|is
argument_list|(
name|nullValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatJavaPropertyStyleArgumentsCanBeParsed
specifier|public
name|void
name|testThatJavaPropertyStyleArgumentsCanBeParsed
parameter_list|()
throws|throws
name|Exception
block|{
name|BootstrapCLIParser
name|parser
init|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
decl_stmt|;
name|registerProperties
argument_list|(
literal|"es.foo"
argument_list|,
literal|"es.spam"
argument_list|)
expr_stmt|;
name|ExitStatus
name|status
init|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start -Dfoo=bar -Dspam=eggs"
argument_list|)
argument_list|)
decl_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|OK
argument_list|)
expr_stmt|;
name|assertSystemProperty
argument_list|(
literal|"es.foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertSystemProperty
argument_list|(
literal|"es.spam"
argument_list|,
literal|"eggs"
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatJavaPropertyStyleArgumentsWithEsPrefixAreNotPrefixedTwice
specifier|public
name|void
name|testThatJavaPropertyStyleArgumentsWithEsPrefixAreNotPrefixedTwice
parameter_list|()
throws|throws
name|Exception
block|{
name|BootstrapCLIParser
name|parser
init|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
decl_stmt|;
name|registerProperties
argument_list|(
literal|"es.spam"
argument_list|,
literal|"es.pidfile"
argument_list|)
expr_stmt|;
name|ExitStatus
name|status
init|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start -Des.pidfile=/path/to/foo/elasticsearch/distribution/zip/target/integ-tests/es.pid -Dspam=eggs"
argument_list|)
argument_list|)
decl_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|OK
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.es.pidfile"
argument_list|)
argument_list|,
name|is
argument_list|(
name|nullValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertSystemProperty
argument_list|(
literal|"es.pidfile"
argument_list|,
literal|"/path/to/foo/elasticsearch/distribution/zip/target/integ-tests/es.pid"
argument_list|)
expr_stmt|;
name|assertSystemProperty
argument_list|(
literal|"es.spam"
argument_list|,
literal|"eggs"
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatUnknownLongOptionsCanBeParsed
specifier|public
name|void
name|testThatUnknownLongOptionsCanBeParsed
parameter_list|()
throws|throws
name|Exception
block|{
name|BootstrapCLIParser
name|parser
init|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
decl_stmt|;
name|registerProperties
argument_list|(
literal|"es.network.host"
argument_list|,
literal|"es.my.option"
argument_list|)
expr_stmt|;
name|ExitStatus
name|status
init|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start --network.host 127.0.0.1 --my.option=true"
argument_list|)
argument_list|)
decl_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|OK
argument_list|)
expr_stmt|;
name|assertSystemProperty
argument_list|(
literal|"es.network.host"
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|assertSystemProperty
argument_list|(
literal|"es.my.option"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatUnknownLongOptionsNeedAValue
specifier|public
name|void
name|testThatUnknownLongOptionsNeedAValue
parameter_list|()
throws|throws
name|Exception
block|{
name|BootstrapCLIParser
name|parser
init|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
decl_stmt|;
name|registerProperties
argument_list|(
literal|"es.network.host"
argument_list|)
expr_stmt|;
name|ExitStatus
name|status
init|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start --network.host"
argument_list|)
argument_list|)
decl_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|USAGE
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
literal|"Parameter [network.host] needs value"
argument_list|)
argument_list|)
expr_stmt|;
name|status
operator|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start --network.host --foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|USAGE
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
literal|"Parameter [network.host] needs value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParsingErrors
specifier|public
name|void
name|testParsingErrors
parameter_list|()
throws|throws
name|Exception
block|{
name|BootstrapCLIParser
name|parser
init|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
decl_stmt|;
comment|// unknown params
name|ExitStatus
name|status
init|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"version --unknown-param /tmp/pid"
argument_list|)
argument_list|)
decl_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|USAGE
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
literal|"Unrecognized option: --unknown-param"
argument_list|)
argument_list|)
expr_stmt|;
comment|// single dash in extra params
name|terminal
operator|=
operator|new
name|CaptureOutputTerminal
argument_list|()
expr_stmt|;
name|parser
operator|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
name|status
operator|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start -network.host 127.0.0.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|USAGE
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
literal|"Parameter [-network.host]does not start with --"
argument_list|)
argument_list|)
expr_stmt|;
comment|// never ended parameter
name|terminal
operator|=
operator|new
name|CaptureOutputTerminal
argument_list|()
expr_stmt|;
name|parser
operator|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
name|status
operator|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start --network.host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|USAGE
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
literal|"Parameter [network.host] needs value"
argument_list|)
argument_list|)
expr_stmt|;
comment|// free floating value
name|terminal
operator|=
operator|new
name|CaptureOutputTerminal
argument_list|()
expr_stmt|;
name|parser
operator|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
expr_stmt|;
name|status
operator|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
literal|"start 127.0.0.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|USAGE
argument_list|)
expr_stmt|;
name|assertThatTerminalOutput
argument_list|(
name|containsString
argument_list|(
literal|"Parameter [127.0.0.1]does not start with --"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testHelpWorks
specifier|public
name|void
name|testHelpWorks
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|tuples
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|tuples
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
literal|"version --help"
argument_list|,
literal|"elasticsearch-version.help"
argument_list|)
argument_list|)
expr_stmt|;
name|tuples
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
literal|"version -h"
argument_list|,
literal|"elasticsearch-version.help"
argument_list|)
argument_list|)
expr_stmt|;
name|tuples
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
literal|"start --help"
argument_list|,
literal|"elasticsearch-start.help"
argument_list|)
argument_list|)
expr_stmt|;
name|tuples
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
literal|"start -h"
argument_list|,
literal|"elasticsearch-start.help"
argument_list|)
argument_list|)
expr_stmt|;
name|tuples
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
literal|"--help"
argument_list|,
literal|"elasticsearch.help"
argument_list|)
argument_list|)
expr_stmt|;
name|tuples
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
literal|"-h"
argument_list|,
literal|"elasticsearch.help"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tuple
range|:
name|tuples
control|)
block|{
name|terminal
operator|=
operator|new
name|CaptureOutputTerminal
argument_list|()
expr_stmt|;
name|BootstrapCLIParser
name|parser
init|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
decl_stmt|;
name|ExitStatus
name|status
init|=
name|parser
operator|.
name|execute
argument_list|(
name|args
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|OK_AND_EXIT
argument_list|)
expr_stmt|;
name|assertTerminalOutputContainsHelpFile
argument_list|(
name|terminal
argument_list|,
literal|"/org/elasticsearch/bootstrap/"
operator|+
name|tuple
operator|.
name|v2
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testThatSpacesInParametersAreSupported
specifier|public
name|void
name|testThatSpacesInParametersAreSupported
parameter_list|()
throws|throws
name|Exception
block|{
comment|// emulates: bin/elasticsearch --node.name "'my node with spaces'" --pidfile "'/tmp/my pid.pid'"
name|BootstrapCLIParser
name|parser
init|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
decl_stmt|;
name|registerProperties
argument_list|(
literal|"es.pidfile"
argument_list|,
literal|"es.my.param"
argument_list|)
expr_stmt|;
name|ExitStatus
name|status
init|=
name|parser
operator|.
name|execute
argument_list|(
literal|"start"
argument_list|,
literal|"--pidfile"
argument_list|,
literal|"foo with space"
argument_list|,
literal|"--my.param"
argument_list|,
literal|"my awesome neighbour"
argument_list|)
decl_stmt|;
name|assertStatus
argument_list|(
name|status
argument_list|,
name|OK
argument_list|)
expr_stmt|;
name|assertSystemProperty
argument_list|(
literal|"es.pidfile"
argument_list|,
literal|"foo with space"
argument_list|)
expr_stmt|;
name|assertSystemProperty
argument_list|(
literal|"es.my.param"
argument_list|,
literal|"my awesome neighbour"
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatHelpfulErrorMessageIsGivenWhenParametersAreOutOfOrder
specifier|public
name|void
name|testThatHelpfulErrorMessageIsGivenWhenParametersAreOutOfOrder
parameter_list|()
throws|throws
name|Exception
block|{
name|BootstrapCLIParser
name|parser
init|=
operator|new
name|BootstrapCLIParser
argument_list|(
name|terminal
argument_list|)
decl_stmt|;
name|UserError
name|e
init|=
name|expectThrows
argument_list|(
name|UserError
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|parser
operator|.
name|parse
argument_list|(
literal|"start"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"--foo=bar"
operator|,
literal|"-Dbaz=qux"
block|}
argument_list|)
decl_stmt|;
block|}
block|)
class|;
end_class

begin_expr_stmt
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"must be before any parameters starting with --"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}      private
DECL|method|registerProperties
name|void
name|registerProperties
parameter_list|(
name|String
modifier|...
name|systemProperties
parameter_list|)
block|{
name|propertiesToClear
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|systemProperties
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|assertSystemProperty
specifier|private
name|void
name|assertSystemProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|expectedValue
parameter_list|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Expected property %s to be %s, terminal output was %s"
argument_list|,
name|name
argument_list|,
name|expectedValue
argument_list|,
name|terminal
operator|.
name|getTerminalOutput
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|msg
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
argument_list|,
name|is
argument_list|(
name|expectedValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|assertStatus
specifier|private
name|void
name|assertStatus
parameter_list|(
name|ExitStatus
name|status
parameter_list|,
name|ExitStatus
name|expectedStatus
parameter_list|)
block|{
name|assertThat
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Expected status to be [%s], but was [%s], terminal output was %s"
argument_list|,
name|expectedStatus
argument_list|,
name|status
argument_list|,
name|terminal
operator|.
name|getTerminalOutput
argument_list|()
argument_list|)
argument_list|,
name|status
argument_list|,
name|is
argument_list|(
name|expectedStatus
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|assertThatTerminalOutput
specifier|private
name|void
name|assertThatTerminalOutput
parameter_list|(
name|Matcher
argument_list|<
name|String
argument_list|>
name|matcher
parameter_list|)
block|{
name|assertThat
argument_list|(
name|terminal
operator|.
name|getTerminalOutput
argument_list|()
argument_list|,
name|hasItem
argument_list|(
name|matcher
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

unit|}
end_unit

