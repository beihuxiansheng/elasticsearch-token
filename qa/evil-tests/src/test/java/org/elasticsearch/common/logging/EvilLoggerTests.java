begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.logging
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|logging
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
name|Level
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
name|LogManager
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
name|core
operator|.
name|Appender
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
name|core
operator|.
name|LoggerContext
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
name|core
operator|.
name|appender
operator|.
name|ConsoleAppender
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
name|core
operator|.
name|appender
operator|.
name|CountingNoOpAppender
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
name|core
operator|.
name|config
operator|.
name|Configurator
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
name|io
operator|.
name|PathUtils
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
name|test
operator|.
name|ESTestCase
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
name|hamcrest
operator|.
name|RegexMatcher
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerPermission
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
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Permission
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|equalTo
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
name|startsWith
import|;
end_import

begin_class
DECL|class|EvilLoggerTests
specifier|public
class|class
name|EvilLoggerTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|LoggerContext
name|context
init|=
operator|(
name|LoggerContext
operator|)
name|LogManager
operator|.
name|getContext
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Configurator
operator|.
name|shutdown
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testLocationInfoTest
specifier|public
name|void
name|testLocationInfoTest
parameter_list|()
throws|throws
name|IOException
throws|,
name|UserException
block|{
name|setupLogging
argument_list|(
literal|"location_info"
argument_list|)
expr_stmt|;
specifier|final
name|Logger
name|testLogger
init|=
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|testLogger
operator|.
name|error
argument_list|(
literal|"This is an error message"
argument_list|)
expr_stmt|;
name|testLogger
operator|.
name|warn
argument_list|(
literal|"This is a warning message"
argument_list|)
expr_stmt|;
name|testLogger
operator|.
name|info
argument_list|(
literal|"This is an info message"
argument_list|)
expr_stmt|;
name|testLogger
operator|.
name|debug
argument_list|(
literal|"This is a debug message"
argument_list|)
expr_stmt|;
name|testLogger
operator|.
name|trace
argument_list|(
literal|"This is a trace message"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|path
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.logs"
argument_list|)
operator|+
literal|".log"
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|events
init|=
name|Files
operator|.
name|readAllLines
argument_list|(
name|PathUtils
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|events
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|location
init|=
literal|"org.elasticsearch.common.logging.EvilLoggerTests.testLocationInfoTest"
decl_stmt|;
comment|// the first message is a warning for unsupported configuration files
name|assertLogLine
argument_list|(
name|events
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|Level
operator|.
name|ERROR
argument_list|,
name|location
argument_list|,
literal|"This is an error message"
argument_list|)
expr_stmt|;
name|assertLogLine
argument_list|(
name|events
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Level
operator|.
name|WARN
argument_list|,
name|location
argument_list|,
literal|"This is a warning message"
argument_list|)
expr_stmt|;
name|assertLogLine
argument_list|(
name|events
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|Level
operator|.
name|INFO
argument_list|,
name|location
argument_list|,
literal|"This is an info message"
argument_list|)
expr_stmt|;
name|assertLogLine
argument_list|(
name|events
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|,
name|location
argument_list|,
literal|"This is a debug message"
argument_list|)
expr_stmt|;
name|assertLogLine
argument_list|(
name|events
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|,
name|Level
operator|.
name|TRACE
argument_list|,
name|location
argument_list|,
literal|"This is a trace message"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeprecationLogger
specifier|public
name|void
name|testDeprecationLogger
parameter_list|()
throws|throws
name|IOException
throws|,
name|UserException
block|{
name|setupLogging
argument_list|(
literal|"deprecation"
argument_list|)
expr_stmt|;
specifier|final
name|DeprecationLogger
name|deprecationLogger
init|=
operator|new
name|DeprecationLogger
argument_list|(
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"deprecation"
argument_list|)
argument_list|)
decl_stmt|;
name|deprecationLogger
operator|.
name|deprecated
argument_list|(
literal|"This is a deprecation message"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|deprecationPath
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.logs"
argument_list|)
operator|+
literal|"_deprecation.log"
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|deprecationEvents
init|=
name|Files
operator|.
name|readAllLines
argument_list|(
name|PathUtils
operator|.
name|get
argument_list|(
name|deprecationPath
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|deprecationEvents
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertLogLine
argument_list|(
name|deprecationEvents
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|Level
operator|.
name|WARN
argument_list|,
literal|"org.elasticsearch.common.logging.DeprecationLogger.deprecated"
argument_list|,
literal|"This is a deprecation message"
argument_list|)
expr_stmt|;
name|assertWarnings
argument_list|(
literal|"This is a deprecation message"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFindAppender
specifier|public
name|void
name|testFindAppender
parameter_list|()
throws|throws
name|IOException
throws|,
name|UserException
block|{
name|setupLogging
argument_list|(
literal|"find_appender"
argument_list|)
expr_stmt|;
specifier|final
name|Logger
name|hasConsoleAppender
init|=
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"has_console_appender"
argument_list|)
decl_stmt|;
specifier|final
name|Appender
name|testLoggerConsoleAppender
init|=
name|Loggers
operator|.
name|findAppender
argument_list|(
name|hasConsoleAppender
argument_list|,
name|ConsoleAppender
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testLoggerConsoleAppender
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testLoggerConsoleAppender
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"console"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Logger
name|hasCountingNoOpAppender
init|=
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"has_counting_no_op_appender"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|Loggers
operator|.
name|findAppender
argument_list|(
name|hasCountingNoOpAppender
argument_list|,
name|ConsoleAppender
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Appender
name|countingNoOpAppender
init|=
name|Loggers
operator|.
name|findAppender
argument_list|(
name|hasCountingNoOpAppender
argument_list|,
name|CountingNoOpAppender
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|countingNoOpAppender
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"counting_no_op"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrefixLogger
specifier|public
name|void
name|testPrefixLogger
parameter_list|()
throws|throws
name|IOException
throws|,
name|IllegalAccessException
throws|,
name|UserException
block|{
name|setupLogging
argument_list|(
literal|"prefix"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|prefix
init|=
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
name|randomAsciiOfLength
argument_list|(
literal|16
argument_list|)
decl_stmt|;
specifier|final
name|Logger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
literal|"prefix"
argument_list|,
name|prefix
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"{}"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
specifier|final
name|Exception
name|e
init|=
operator|new
name|Exception
argument_list|(
literal|"exception"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
operator|new
name|ParameterizedMessage
argument_list|(
literal|"{}"
argument_list|,
literal|"test"
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
specifier|final
name|String
name|path
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.logs"
argument_list|)
operator|+
literal|".log"
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|events
init|=
name|Files
operator|.
name|readAllLines
argument_list|(
name|PathUtils
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|pw
argument_list|)
expr_stmt|;
specifier|final
name|int
name|stackTraceLength
init|=
name|sw
operator|.
name|toString
argument_list|()
operator|.
name|split
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
operator|.
name|length
decl_stmt|;
specifier|final
name|int
name|expectedLogLines
init|=
literal|3
decl_stmt|;
name|assertThat
argument_list|(
name|events
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedLogLines
operator|+
name|stackTraceLength
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedLogLines
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
name|assertThat
argument_list|(
name|events
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|startsWith
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|events
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|startsWith
argument_list|(
literal|"["
operator|+
name|prefix
operator|+
literal|"] test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testLog4jShutdownHack
specifier|public
name|void
name|testLog4jShutdownHack
parameter_list|()
block|{
specifier|final
name|AtomicBoolean
name|denied
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|SecurityManager
name|sm
init|=
name|System
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
try|try
block|{
name|System
operator|.
name|setSecurityManager
argument_list|(
operator|new
name|SecurityManager
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|checkPermission
parameter_list|(
name|Permission
name|perm
parameter_list|)
block|{
comment|// just grant all permissions to Log4j, except we deny MBeanServerPermission
comment|// "createMBeanServer" as this will trigger the Log4j bug
if|if
condition|(
name|perm
operator|instanceof
name|MBeanServerPermission
operator|&&
literal|"createMBeanServer"
operator|.
name|equals
argument_list|(
name|perm
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// without the hack in place, Log4j will try to get an MBean server which we will deny
comment|// with the hack in place, this permission should never be requested by Log4j
name|denied
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"denied"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkPropertyAccess
parameter_list|(
name|String
name|key
parameter_list|)
block|{
comment|/*                      * grant access to all properties; this is so that Log4j can check if its usage                      * of JMX is disabled or not by reading log4j2.disable.jmx but there are other                      * properties that Log4j will try to read as well and its simpler to just grant                      * them all                      */
block|}
block|}
argument_list|)
expr_stmt|;
comment|// this will trigger the bug without the hack
name|LoggerContext
name|context
init|=
operator|(
name|LoggerContext
operator|)
name|LogManager
operator|.
name|getContext
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Configurator
operator|.
name|shutdown
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// Log4j should have never requested permissions to create an MBean server
name|assertFalse
argument_list|(
name|denied
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setSecurityManager
argument_list|(
name|sm
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setupLogging
specifier|private
name|void
name|setupLogging
parameter_list|(
specifier|final
name|String
name|config
parameter_list|)
throws|throws
name|IOException
throws|,
name|UserException
block|{
specifier|final
name|Path
name|configDir
init|=
name|getDataPath
argument_list|(
name|config
argument_list|)
decl_stmt|;
comment|// need to set custom path.conf so we can use a custom log4j2.properties file for the test
specifier|final
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|Environment
operator|.
name|PATH_CONF_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|configDir
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Environment
operator|.
name|PATH_HOME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Environment
name|environment
init|=
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|LogConfigurator
operator|.
name|configure
argument_list|(
name|environment
argument_list|)
expr_stmt|;
block|}
DECL|method|assertLogLine
specifier|private
name|void
name|assertLogLine
parameter_list|(
specifier|final
name|String
name|logLine
parameter_list|,
specifier|final
name|Level
name|level
parameter_list|,
specifier|final
name|String
name|location
parameter_list|,
specifier|final
name|String
name|message
parameter_list|)
block|{
specifier|final
name|Matcher
name|matcher
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\[(.*)\\]\\[(.*)\\(.*\\)\\] (.*)"
argument_list|)
operator|.
name|matcher
argument_list|(
name|logLine
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|logLine
argument_list|,
name|matcher
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|level
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|,
name|RegexMatcher
operator|.
name|matches
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|,
name|RegexMatcher
operator|.
name|matches
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

