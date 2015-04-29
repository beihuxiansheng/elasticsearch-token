begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.logging.log4j
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|logging
operator|.
name|log4j
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|AppenderSkeleton
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|log4j
operator|.
name|spi
operator|.
name|LocationInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|LoggingEvent
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
name|logging
operator|.
name|ESLogger
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
name|ImmutableSettings
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
name|test
operator|.
name|ElasticsearchTestCase
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Paths
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
name|notNullValue
import|;
end_import

begin_class
DECL|class|Log4jESLoggerTests
specifier|public
class|class
name|Log4jESLoggerTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|esTestLogger
specifier|private
name|ESLogger
name|esTestLogger
decl_stmt|;
DECL|field|testAppender
specifier|private
name|TestAppender
name|testAppender
decl_stmt|;
DECL|field|testLevel
specifier|private
name|String
name|testLevel
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|this
operator|.
name|testLevel
operator|=
name|Log4jESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getLevel
argument_list|()
expr_stmt|;
name|LogConfigurator
operator|.
name|reset
argument_list|()
expr_stmt|;
name|Path
name|configDir
init|=
name|getDataPath
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
comment|// Need to set custom path.conf so we can use a custom logging.yml file for the test
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"path.conf"
argument_list|,
name|configDir
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.home"
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
name|LogConfigurator
operator|.
name|configure
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|esTestLogger
operator|=
name|Log4jESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|Logger
name|testLogger
init|=
operator|(
operator|(
name|Log4jESLogger
operator|)
name|esTestLogger
operator|)
operator|.
name|logger
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|testLogger
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
name|testAppender
operator|=
operator|new
name|TestAppender
argument_list|()
expr_stmt|;
name|testLogger
operator|.
name|addAppender
argument_list|(
name|testAppender
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|esTestLogger
operator|.
name|setLevel
argument_list|(
name|testLevel
argument_list|)
expr_stmt|;
name|Logger
name|testLogger
init|=
operator|(
operator|(
name|Log4jESLogger
operator|)
name|esTestLogger
operator|)
operator|.
name|logger
argument_list|()
decl_stmt|;
name|testLogger
operator|.
name|removeAppender
argument_list|(
name|testAppender
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|locationInfoTest
specifier|public
name|void
name|locationInfoTest
parameter_list|()
block|{
name|esTestLogger
operator|.
name|error
argument_list|(
literal|"This is an error"
argument_list|)
expr_stmt|;
name|esTestLogger
operator|.
name|warn
argument_list|(
literal|"This is a warning"
argument_list|)
expr_stmt|;
name|esTestLogger
operator|.
name|info
argument_list|(
literal|"This is an info"
argument_list|)
expr_stmt|;
name|esTestLogger
operator|.
name|debug
argument_list|(
literal|"This is a debug"
argument_list|)
expr_stmt|;
name|esTestLogger
operator|.
name|trace
argument_list|(
literal|"This is a trace"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LoggingEvent
argument_list|>
name|events
init|=
name|testAppender
operator|.
name|getEvents
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|events
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
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
name|LoggingEvent
name|event
init|=
name|events
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|event
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|event
operator|.
name|getLevel
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|event
operator|.
name|getRenderedMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"This is an error"
argument_list|)
argument_list|)
expr_stmt|;
name|LocationInfo
name|locationInfo
init|=
name|event
operator|.
name|getLocationInformation
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|locationInfo
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
operator|.
name|getClassName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Log4jESLoggerTests
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
operator|.
name|getMethodName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"locationInfoTest"
argument_list|)
argument_list|)
expr_stmt|;
name|event
operator|=
name|events
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|event
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|event
operator|.
name|getLevel
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Level
operator|.
name|WARN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|event
operator|.
name|getRenderedMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"This is a warning"
argument_list|)
argument_list|)
expr_stmt|;
name|locationInfo
operator|=
name|event
operator|.
name|getLocationInformation
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
operator|.
name|getClassName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Log4jESLoggerTests
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
operator|.
name|getMethodName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"locationInfoTest"
argument_list|)
argument_list|)
expr_stmt|;
name|event
operator|=
name|events
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|event
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|event
operator|.
name|getLevel
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Level
operator|.
name|INFO
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|event
operator|.
name|getRenderedMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"This is an info"
argument_list|)
argument_list|)
expr_stmt|;
name|locationInfo
operator|=
name|event
operator|.
name|getLocationInformation
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
operator|.
name|getClassName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Log4jESLoggerTests
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
operator|.
name|getMethodName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"locationInfoTest"
argument_list|)
argument_list|)
expr_stmt|;
name|event
operator|=
name|events
operator|.
name|get
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|event
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|event
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
name|assertThat
argument_list|(
name|event
operator|.
name|getRenderedMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"This is a debug"
argument_list|)
argument_list|)
expr_stmt|;
name|locationInfo
operator|=
name|event
operator|.
name|getLocationInformation
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
operator|.
name|getClassName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Log4jESLoggerTests
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
operator|.
name|getMethodName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"locationInfoTest"
argument_list|)
argument_list|)
expr_stmt|;
name|event
operator|=
name|events
operator|.
name|get
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|event
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|event
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
name|event
operator|.
name|getRenderedMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"This is a trace"
argument_list|)
argument_list|)
expr_stmt|;
name|locationInfo
operator|=
name|event
operator|.
name|getLocationInformation
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
operator|.
name|getClassName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Log4jESLoggerTests
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locationInfo
operator|.
name|getMethodName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"locationInfoTest"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|TestAppender
specifier|private
specifier|static
class|class
name|TestAppender
extends|extends
name|AppenderSkeleton
block|{
DECL|field|events
specifier|private
name|List
argument_list|<
name|LoggingEvent
argument_list|>
name|events
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{         }
annotation|@
name|Override
DECL|method|requiresLayout
specifier|public
name|boolean
name|requiresLayout
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|append
specifier|protected
name|void
name|append
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
comment|// Forces it to generate the location information
name|event
operator|.
name|getLocationInformation
argument_list|()
expr_stmt|;
name|events
operator|.
name|add
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
DECL|method|getEvents
specifier|public
name|List
argument_list|<
name|LoggingEvent
argument_list|>
name|getEvents
parameter_list|()
block|{
return|return
name|events
return|;
block|}
block|}
block|}
end_class

end_unit

