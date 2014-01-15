begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.azure
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|azure
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|TestGroup
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
name|ElasticsearchIntegrationTest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Documented
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Inherited
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AbstractAzureTest
specifier|public
specifier|abstract
class|class
name|AbstractAzureTest
extends|extends
name|ElasticsearchIntegrationTest
block|{
comment|/**      * Annotation for tests that require Azure to run. Azure tests are disabled by default.      *<p/>      * To enable test add -Dtests.azure=true -Des.config=/path/to/elasticsearch.yml      *<p/>      * The elasticsearch.yml file should contain the following keys      *<pre>       cloud:           azure:               keystore: FULLPATH-TO-YOUR-KEYSTORE               password: YOUR-PASSWORD               subscription_id: YOUR-AZURE-SUBSCRIPTION-ID               service_name: YOUR-AZURE-SERVICE-NAME        discovery:               type: azure      *</pre>      */
annotation|@
name|Documented
annotation|@
name|Inherited
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|TestGroup
argument_list|(
name|enabled
operator|=
literal|false
argument_list|,
name|sysProperty
operator|=
name|SYSPROP_AZURE
argument_list|)
DECL|interface|AzureTest
specifier|public
annotation_defn|@interface
name|AzureTest
block|{     }
comment|/**      */
DECL|field|SYSPROP_AZURE
specifier|public
specifier|static
specifier|final
name|String
name|SYSPROP_AZURE
init|=
literal|"tests.azure"
decl_stmt|;
block|}
end_class

end_unit

