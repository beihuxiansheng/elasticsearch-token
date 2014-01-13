begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.ec2
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|ec2
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * Just an empty Node Start test to check eveything if fine when  * starting.  * This test is marked as ignored.  * If you want to run your own test, please modify first test/resources/elasticsearch.yml file  * with your own AWS credentials  */
end_comment

begin_class
DECL|class|Ec2DiscoveryITest
specifier|public
class|class
name|Ec2DiscoveryITest
block|{
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|testStart
specifier|public
name|void
name|testStart
parameter_list|()
block|{
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|node
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

