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
name|log4j
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
name|log4j
operator|.
name|Logger
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
name|log4j
operator|.
name|Log4jESLogger
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
name|log4j
operator|.
name|Log4jESLoggerFactory
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
name|log4j
operator|.
name|LogConfigurator
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
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|LoggingConfigurationTests
specifier|public
class|class
name|LoggingConfigurationTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testMultipleConfigs
specifier|public
name|void
name|testMultipleConfigs
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|configDir
init|=
name|resolveConfigDir
argument_list|()
decl_stmt|;
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
name|getAbsolutePath
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
name|ESLogger
name|esLogger
init|=
name|Log4jESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"first"
argument_list|)
decl_stmt|;
name|Logger
name|logger
init|=
operator|(
operator|(
name|Log4jESLogger
operator|)
name|esLogger
operator|)
operator|.
name|logger
argument_list|()
decl_stmt|;
name|Appender
name|appender
init|=
name|logger
operator|.
name|getAppender
argument_list|(
literal|"console1"
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
name|esLogger
operator|=
name|Log4jESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"second"
argument_list|)
expr_stmt|;
name|logger
operator|=
operator|(
operator|(
name|Log4jESLogger
operator|)
name|esLogger
operator|)
operator|.
name|logger
argument_list|()
expr_stmt|;
name|appender
operator|=
name|logger
operator|.
name|getAppender
argument_list|(
literal|"console2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|appender
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|esLogger
operator|=
name|Log4jESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"third"
argument_list|)
expr_stmt|;
name|logger
operator|=
operator|(
operator|(
name|Log4jESLogger
operator|)
name|esLogger
operator|)
operator|.
name|logger
argument_list|()
expr_stmt|;
name|appender
operator|=
name|logger
operator|.
name|getAppender
argument_list|(
literal|"console3"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|appender
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|resolveConfigDir
specifier|private
specifier|static
name|File
name|resolveConfigDir
parameter_list|()
throws|throws
name|Exception
block|{
name|URL
name|url
init|=
name|LoggingConfigurationTests
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
name|url
operator|.
name|toURI
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

