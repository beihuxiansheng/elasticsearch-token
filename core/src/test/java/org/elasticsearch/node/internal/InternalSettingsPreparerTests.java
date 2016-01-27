begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.node.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|internal
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterName
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
name|Terminal
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
name|common
operator|.
name|settings
operator|.
name|SettingsException
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
name|Before
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
name|InputStream
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
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
operator|.
name|settingsBuilder
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
name|is
import|;
end_import

begin_class
DECL|class|InternalSettingsPreparerTests
specifier|public
class|class
name|InternalSettingsPreparerTests
extends|extends
name|ESTestCase
block|{
DECL|field|baseEnvSettings
name|Settings
name|baseEnvSettings
decl_stmt|;
annotation|@
name|Before
DECL|method|createBaseEnvSettings
specifier|public
name|void
name|createBaseEnvSettings
parameter_list|()
block|{
name|baseEnvSettings
operator|=
name|settingsBuilder
argument_list|()
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
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|clearBaseEnvSettings
specifier|public
name|void
name|clearBaseEnvSettings
parameter_list|()
block|{
name|baseEnvSettings
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testEmptySettings
specifier|public
name|void
name|testEmptySettings
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|InternalSettingsPreparer
operator|.
name|prepareSettings
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
comment|// a name was set
name|assertNotNull
argument_list|(
name|settings
operator|.
name|get
argument_list|(
name|ClusterName
operator|.
name|CLUSTER_NAME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// a cluster name was set
name|int
name|size
init|=
name|settings
operator|.
name|names
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|Environment
name|env
init|=
name|InternalSettingsPreparer
operator|.
name|prepareEnvironment
argument_list|(
name|baseEnvSettings
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|settings
operator|=
name|env
operator|.
name|settings
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
comment|// a name was set
name|assertNotNull
argument_list|(
name|settings
operator|.
name|get
argument_list|(
name|ClusterName
operator|.
name|CLUSTER_NAME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// a cluster name was set
name|assertEquals
argument_list|(
name|settings
operator|.
name|toString
argument_list|()
argument_list|,
name|size
operator|+
literal|1
comment|/* path.home is in the base settings */
argument_list|,
name|settings
operator|.
name|names
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|home
init|=
name|Environment
operator|.
name|PATH_HOME_SETTING
operator|.
name|get
argument_list|(
name|baseEnvSettings
argument_list|)
decl_stmt|;
name|String
name|configDir
init|=
name|env
operator|.
name|configFile
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|configDir
argument_list|,
name|configDir
operator|.
name|startsWith
argument_list|(
name|home
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testClusterNameDefault
specifier|public
name|void
name|testClusterNameDefault
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|InternalSettingsPreparer
operator|.
name|prepareSettings
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ClusterName
operator|.
name|DEFAULT
operator|.
name|value
argument_list|()
argument_list|,
name|settings
operator|.
name|get
argument_list|(
name|ClusterName
operator|.
name|CLUSTER_NAME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|settings
operator|=
name|InternalSettingsPreparer
operator|.
name|prepareEnvironment
argument_list|(
name|baseEnvSettings
argument_list|,
literal|null
argument_list|)
operator|.
name|settings
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|ClusterName
operator|.
name|DEFAULT
operator|.
name|value
argument_list|()
argument_list|,
name|settings
operator|.
name|get
argument_list|(
name|ClusterName
operator|.
name|CLUSTER_NAME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReplacePromptPlaceholders
specifier|public
name|void
name|testReplacePromptPlaceholders
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|replacedSecretProperties
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|replacedTextProperties
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Terminal
name|terminal
init|=
operator|new
name|CliToolTestCase
operator|.
name|MockTerminal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|char
index|[]
name|readSecret
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
for|for
control|(
name|Object
name|arg
range|:
name|args
control|)
block|{
name|replacedSecretProperties
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|arg
argument_list|)
expr_stmt|;
block|}
return|return
literal|"replaced"
operator|.
name|toCharArray
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|readText
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
for|for
control|(
name|Object
name|arg
range|:
name|args
control|)
block|{
name|replacedTextProperties
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|arg
argument_list|)
expr_stmt|;
block|}
return|return
literal|"text"
return|;
block|}
block|}
decl_stmt|;
name|Settings
operator|.
name|Builder
name|builder
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|baseEnvSettings
argument_list|)
operator|.
name|put
argument_list|(
literal|"password.replace"
argument_list|,
name|InternalSettingsPreparer
operator|.
name|SECRET_PROMPT_VALUE
argument_list|)
operator|.
name|put
argument_list|(
literal|"dont.replace"
argument_list|,
literal|"prompt:secret"
argument_list|)
operator|.
name|put
argument_list|(
literal|"dont.replace2"
argument_list|,
literal|"_prompt:secret_"
argument_list|)
operator|.
name|put
argument_list|(
literal|"dont.replace3"
argument_list|,
literal|"_prompt:text__"
argument_list|)
operator|.
name|put
argument_list|(
literal|"dont.replace4"
argument_list|,
literal|"__prompt:text_"
argument_list|)
operator|.
name|put
argument_list|(
literal|"dont.replace5"
argument_list|,
literal|"prompt:secret__"
argument_list|)
operator|.
name|put
argument_list|(
literal|"replace_me"
argument_list|,
name|InternalSettingsPreparer
operator|.
name|TEXT_PROMPT_VALUE
argument_list|)
decl_stmt|;
name|Settings
name|settings
init|=
name|InternalSettingsPreparer
operator|.
name|prepareEnvironment
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|,
name|terminal
argument_list|)
operator|.
name|settings
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|replacedSecretProperties
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|replacedTextProperties
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"password.replace"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"replaced"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"replace_me"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify other values unchanged
name|assertThat
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"dont.replace"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"prompt:secret"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"dont.replace2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"_prompt:secret_"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"dont.replace3"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"_prompt:text__"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"dont.replace4"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"__prompt:text_"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"dont.replace5"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"prompt:secret__"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReplaceSecretPromptPlaceholderWithNullTerminal
specifier|public
name|void
name|testReplaceSecretPromptPlaceholderWithNullTerminal
parameter_list|()
block|{
name|Settings
operator|.
name|Builder
name|builder
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|baseEnvSettings
argument_list|)
operator|.
name|put
argument_list|(
literal|"replace_me1"
argument_list|,
name|InternalSettingsPreparer
operator|.
name|SECRET_PROMPT_VALUE
argument_list|)
decl_stmt|;
try|try
block|{
name|InternalSettingsPreparer
operator|.
name|prepareEnvironment
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"an exception should have been thrown since no terminal was provided!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"with value ["
operator|+
name|InternalSettingsPreparer
operator|.
name|SECRET_PROMPT_VALUE
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testReplaceTextPromptPlaceholderWithNullTerminal
specifier|public
name|void
name|testReplaceTextPromptPlaceholderWithNullTerminal
parameter_list|()
block|{
name|Settings
operator|.
name|Builder
name|builder
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|baseEnvSettings
argument_list|)
operator|.
name|put
argument_list|(
literal|"replace_me1"
argument_list|,
name|InternalSettingsPreparer
operator|.
name|TEXT_PROMPT_VALUE
argument_list|)
decl_stmt|;
try|try
block|{
name|InternalSettingsPreparer
operator|.
name|prepareEnvironment
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"an exception should have been thrown since no terminal was provided!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"with value ["
operator|+
name|InternalSettingsPreparer
operator|.
name|TEXT_PROMPT_VALUE
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGarbageIsNotSwallowed
specifier|public
name|void
name|testGarbageIsNotSwallowed
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|InputStream
name|garbage
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/config/garbage/garbage.yml"
argument_list|)
decl_stmt|;
name|Path
name|home
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Path
name|config
init|=
name|home
operator|.
name|resolve
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|garbage
argument_list|,
name|config
operator|.
name|resolve
argument_list|(
literal|"elasticsearch.yml"
argument_list|)
argument_list|)
expr_stmt|;
name|InternalSettingsPreparer
operator|.
name|prepareEnvironment
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"config.ignore_system_properties"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
name|baseEnvSettings
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SettingsException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Failed to load settings from [elasticsearch.yml]"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMultipleSettingsFileNotAllowed
specifier|public
name|void
name|testMultipleSettingsFileNotAllowed
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|yaml
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/config/elasticsearch.yaml"
argument_list|)
decl_stmt|;
name|InputStream
name|properties
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/config/elasticsearch.properties"
argument_list|)
decl_stmt|;
name|Path
name|home
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Path
name|config
init|=
name|home
operator|.
name|resolve
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|yaml
argument_list|,
name|config
operator|.
name|resolve
argument_list|(
literal|"elasticsearch.yaml"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|properties
argument_list|,
name|config
operator|.
name|resolve
argument_list|(
literal|"elasticsearch.properties"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|InternalSettingsPreparer
operator|.
name|prepareEnvironment
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"config.ignore_system_properties"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
name|baseEnvSettings
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SettingsException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"multiple settings files found with suffixes"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|".yaml"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|".properties"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

