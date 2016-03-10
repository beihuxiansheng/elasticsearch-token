begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories.azure
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|azure
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|azure
operator|.
name|storage
operator|.
name|AzureStorageSettings
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
name|Matchers
operator|.
name|hasSize
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
name|notNullValue
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
DECL|class|AzureSettingsParserTests
specifier|public
class|class
name|AzureSettingsParserTests
extends|extends
name|LuceneTestCase
block|{
DECL|method|testParseTwoSettingsExplicitDefault
specifier|public
name|void
name|testParseTwoSettingsExplicitDefault
parameter_list|()
block|{
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
literal|"cloud.azure.storage.azure1.account"
argument_list|,
literal|"myaccount1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cloud.azure.storage.azure1.key"
argument_list|,
literal|"mykey1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cloud.azure.storage.azure1.default"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"cloud.azure.storage.azure2.account"
argument_list|,
literal|"myaccount2"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cloud.azure.storage.azure2.key"
argument_list|,
literal|"mykey2"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Tuple
argument_list|<
name|AzureStorageSettings
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|AzureStorageSettings
argument_list|>
argument_list|>
name|tuple
init|=
name|AzureStorageSettings
operator|.
name|parse
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
operator|.
name|getAccount
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"myaccount1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"mykey1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tuple
operator|.
name|v2
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tuple
operator|.
name|v2
argument_list|()
operator|.
name|get
argument_list|(
literal|"azure2"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tuple
operator|.
name|v2
argument_list|()
operator|.
name|get
argument_list|(
literal|"azure2"
argument_list|)
operator|.
name|getAccount
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"myaccount2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tuple
operator|.
name|v2
argument_list|()
operator|.
name|get
argument_list|(
literal|"azure2"
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"mykey2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseUniqueSettings
specifier|public
name|void
name|testParseUniqueSettings
parameter_list|()
block|{
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
literal|"cloud.azure.storage.azure1.account"
argument_list|,
literal|"myaccount1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cloud.azure.storage.azure1.key"
argument_list|,
literal|"mykey1"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Tuple
argument_list|<
name|AzureStorageSettings
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|AzureStorageSettings
argument_list|>
argument_list|>
name|tuple
init|=
name|AzureStorageSettings
operator|.
name|parse
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
operator|.
name|getAccount
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"myaccount1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"mykey1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tuple
operator|.
name|v2
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseTwoSettingsNoDefault
specifier|public
name|void
name|testParseTwoSettingsNoDefault
parameter_list|()
block|{
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
literal|"cloud.azure.storage.azure1.account"
argument_list|,
literal|"myaccount1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cloud.azure.storage.azure1.key"
argument_list|,
literal|"mykey1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cloud.azure.storage.azure2.account"
argument_list|,
literal|"myaccount2"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cloud.azure.storage.azure2.key"
argument_list|,
literal|"mykey2"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|AzureStorageSettings
operator|.
name|parse
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed with a SettingsException (no default data store)"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SettingsException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"No default Azure data store configured"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseTwoSettingsTooManyDefaultSet
specifier|public
name|void
name|testParseTwoSettingsTooManyDefaultSet
parameter_list|()
block|{
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
literal|"cloud.azure.storage.azure1.account"
argument_list|,
literal|"myaccount1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cloud.azure.storage.azure1.key"
argument_list|,
literal|"mykey1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cloud.azure.storage.azure1.default"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"cloud.azure.storage.azure2.account"
argument_list|,
literal|"myaccount2"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cloud.azure.storage.azure2.key"
argument_list|,
literal|"mykey2"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cloud.azure.storage.azure2.default"
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|AzureStorageSettings
operator|.
name|parse
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed with a SettingsException (multiple default data stores)"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SettingsException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Multiple default Azure data stores configured: [azure1] and [azure2]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseEmptySettings
specifier|public
name|void
name|testParseEmptySettings
parameter_list|()
block|{
name|Tuple
argument_list|<
name|AzureStorageSettings
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|AzureStorageSettings
argument_list|>
argument_list|>
name|tuple
init|=
name|AzureStorageSettings
operator|.
name|parse
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tuple
operator|.
name|v2
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
