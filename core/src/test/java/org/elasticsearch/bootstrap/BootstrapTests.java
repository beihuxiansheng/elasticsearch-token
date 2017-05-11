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
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_class
DECL|class|BootstrapTests
specifier|public
class|class
name|BootstrapTests
extends|extends
name|ESTestCase
block|{
DECL|method|testConfigDeprecation
specifier|public
name|void
name|testConfigDeprecation
parameter_list|()
block|{
name|Bootstrap
operator|.
name|checkConfigExtension
argument_list|(
literal|".json"
argument_list|)
expr_stmt|;
name|assertWarnings
argument_list|(
literal|"elasticsearch.json is deprecated; rename your configuration file to elasticsearch.yaml"
argument_list|)
expr_stmt|;
name|Bootstrap
operator|.
name|checkConfigExtension
argument_list|(
literal|".yml"
argument_list|)
expr_stmt|;
name|assertWarnings
argument_list|(
literal|"elasticsearch.yml is deprecated; rename your configuration file to elasticsearch.yaml"
argument_list|)
expr_stmt|;
name|Bootstrap
operator|.
name|checkConfigExtension
argument_list|(
literal|".yaml"
argument_list|)
expr_stmt|;
comment|// no warnings, will be checked in @After
block|}
block|}
end_class

end_unit

