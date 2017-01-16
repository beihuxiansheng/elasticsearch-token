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
name|config
operator|.
name|Configuration
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
name|core
operator|.
name|config
operator|.
name|LoggerConfig
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
name|Path
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
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
name|CoreMatchers
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
name|hasKey
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
name|hasToString
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
name|notNullValue
import|;
end_import

begin_class
DECL|class|EvilLoggerConfigurationTests
specifier|public
class|class
name|EvilLoggerConfigurationTests
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
DECL|method|testResolveMultipleConfigs
specifier|public
name|void
name|testResolveMultipleConfigs
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Level
name|level
init|=
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getLevel
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Path
name|configDir
init|=
name|getDataPath
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
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
block|{
specifier|final
name|LoggerContext
name|ctx
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
specifier|final
name|Configuration
name|config
init|=
name|ctx
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|LoggerConfig
name|loggerConfig
init|=
name|config
operator|.
name|getLoggerConfig
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
specifier|final
name|Appender
name|appender
init|=
name|loggerConfig
operator|.
name|getAppenders
argument_list|()
operator|.
name|get
argument_list|(
literal|"console"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|appender
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
specifier|final
name|LoggerContext
name|ctx
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
specifier|final
name|Configuration
name|config
init|=
name|ctx
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|LoggerConfig
name|loggerConfig
init|=
name|config
operator|.
name|getLoggerConfig
argument_list|(
literal|"second"
argument_list|)
decl_stmt|;
specifier|final
name|Appender
name|appender
init|=
name|loggerConfig
operator|.
name|getAppenders
argument_list|()
operator|.
name|get
argument_list|(
literal|"console2"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|appender
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
specifier|final
name|LoggerContext
name|ctx
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
specifier|final
name|Configuration
name|config
init|=
name|ctx
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|LoggerConfig
name|loggerConfig
init|=
name|config
operator|.
name|getLoggerConfig
argument_list|(
literal|"third"
argument_list|)
decl_stmt|;
specifier|final
name|Appender
name|appender
init|=
name|loggerConfig
operator|.
name|getAppenders
argument_list|()
operator|.
name|get
argument_list|(
literal|"console3"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|appender
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|Configurator
operator|.
name|setLevel
argument_list|(
literal|"test"
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
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
literal|"config"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|level
init|=
name|randomFrom
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|,
name|Level
operator|.
name|INFO
argument_list|,
name|Level
operator|.
name|WARN
argument_list|,
name|Level
operator|.
name|ERROR
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
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
name|put
argument_list|(
literal|"logger.level"
argument_list|,
name|level
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
specifier|final
name|String
name|loggerName
init|=
literal|"test"
decl_stmt|;
specifier|final
name|Logger
name|logger
init|=
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
name|loggerName
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|logger
operator|.
name|getLevel
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|level
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// tests that custom settings are not overwritten by settings in the config file
DECL|method|testResolveOrder
specifier|public
name|void
name|testResolveOrder
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|configDir
init|=
name|getDataPath
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
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
name|put
argument_list|(
literal|"logger.test_resolve_order"
argument_list|,
literal|"TRACE"
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
comment|// args should overwrite whatever is in the config
specifier|final
name|String
name|loggerName
init|=
literal|"test_resolve_order"
decl_stmt|;
specifier|final
name|Logger
name|logger
init|=
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
name|loggerName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testHierarchy
specifier|public
name|void
name|testHierarchy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|configDir
init|=
name|getDataPath
argument_list|(
literal|"hierarchy"
argument_list|)
decl_stmt|;
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
name|assertThat
argument_list|(
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"x"
argument_list|)
operator|.
name|getLevel
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"x.y"
argument_list|)
operator|.
name|getLevel
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Level
name|level
init|=
name|randomFrom
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|,
name|Level
operator|.
name|INFO
argument_list|,
name|Level
operator|.
name|WARN
argument_list|,
name|Level
operator|.
name|ERROR
argument_list|)
decl_stmt|;
name|Loggers
operator|.
name|setLevel
argument_list|(
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"x"
argument_list|)
argument_list|,
name|level
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"x"
argument_list|)
operator|.
name|getLevel
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|level
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"x.y"
argument_list|)
operator|.
name|getLevel
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|level
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMissingConfigFile
specifier|public
name|void
name|testMissingConfigFile
parameter_list|()
block|{
specifier|final
name|Path
name|configDir
init|=
name|getDataPath
argument_list|(
literal|"does_not_exist"
argument_list|)
decl_stmt|;
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
name|UserException
name|e
init|=
name|expectThrows
argument_list|(
name|UserException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|LogConfigurator
operator|.
name|configure
argument_list|(
name|environment
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"no log4j2.properties found; tried"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLoggingLevelsFromSettings
specifier|public
name|void
name|testLoggingLevelsFromSettings
parameter_list|()
throws|throws
name|IOException
throws|,
name|UserException
block|{
specifier|final
name|Level
name|rootLevel
init|=
name|randomFrom
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|,
name|Level
operator|.
name|INFO
argument_list|,
name|Level
operator|.
name|WARN
argument_list|,
name|Level
operator|.
name|ERROR
argument_list|)
decl_stmt|;
specifier|final
name|Level
name|fooLevel
init|=
name|randomFrom
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|,
name|Level
operator|.
name|INFO
argument_list|,
name|Level
operator|.
name|WARN
argument_list|,
name|Level
operator|.
name|ERROR
argument_list|)
decl_stmt|;
specifier|final
name|Level
name|barLevel
init|=
name|randomFrom
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|,
name|Level
operator|.
name|INFO
argument_list|,
name|Level
operator|.
name|WARN
argument_list|,
name|Level
operator|.
name|ERROR
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|configDir
init|=
name|getDataPath
argument_list|(
literal|"minimal"
argument_list|)
decl_stmt|;
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
name|put
argument_list|(
literal|"logger.level"
argument_list|,
name|rootLevel
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"logger.foo"
argument_list|,
name|fooLevel
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"logger.bar"
argument_list|,
name|barLevel
operator|.
name|name
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
specifier|final
name|LoggerContext
name|ctx
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
specifier|final
name|Configuration
name|config
init|=
name|ctx
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|LoggerConfig
argument_list|>
name|loggerConfigs
init|=
name|config
operator|.
name|getLoggers
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|loggerConfigs
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|loggerConfigs
argument_list|,
name|hasKey
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|loggerConfigs
operator|.
name|get
argument_list|(
literal|""
argument_list|)
operator|.
name|getLevel
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|rootLevel
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|loggerConfigs
argument_list|,
name|hasKey
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|loggerConfigs
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getLevel
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|fooLevel
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|loggerConfigs
argument_list|,
name|hasKey
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|loggerConfigs
operator|.
name|get
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|getLevel
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|barLevel
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ctx
operator|.
name|getLogger
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|16
argument_list|)
argument_list|)
operator|.
name|getLevel
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|rootLevel
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

