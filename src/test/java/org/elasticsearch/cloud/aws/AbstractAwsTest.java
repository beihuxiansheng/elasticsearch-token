begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch (the "Author") under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Author licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.aws
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|aws
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
DECL|class|AbstractAwsTest
specifier|public
specifier|abstract
class|class
name|AbstractAwsTest
extends|extends
name|ElasticsearchIntegrationTest
block|{
comment|/**      * Annotation for tests that require AWS to run. AWS tests are disabled by default.      *<p/>      * To enable test add -Dtests.aws=true -Des.config=/path/to/elasticsearch.yml      *<p/>      * The elasticsearch.yml file should contain the following keys      *<pre>      * cloud:      *      aws:      *          access_key: AKVAIQBF2RECL7FJWGJQ      *          secret_key: vExyMThREXeRMm/b/LRzEB8jWwvzQeXgjqMX+6br      *          region: "us-west"      *      * repositories:      *      s3:      *          bucket: "bucket_name"      *      *</pre>      */
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
name|SYSPROP_AWS
argument_list|)
DECL|interface|AwsTest
specifier|public
annotation_defn|@interface
name|AwsTest
block|{     }
comment|/**      */
DECL|field|SYSPROP_AWS
specifier|public
specifier|static
specifier|final
name|String
name|SYSPROP_AWS
init|=
literal|"tests.aws"
decl_stmt|;
block|}
end_class

end_unit

