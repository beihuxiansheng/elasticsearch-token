begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration.document
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
operator|.
name|document
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
operator|.
name|*
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
name|ImmutableSettings
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|AliasedIndexDocumentActionsTests
specifier|public
class|class
name|AliasedIndexDocumentActionsTests
extends|extends
name|DocumentActionsTests
block|{
DECL|method|createIndex
specifier|protected
name|void
name|createIndex
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Creating index [test1] with alias [test]"
argument_list|)
expr_stmt|;
name|client1
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|create
argument_list|(
name|createIndexRequest
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|settings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|putArray
argument_list|(
literal|"index.aliases"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
DECL|method|getConcreteIndexName
annotation|@
name|Override
specifier|protected
name|String
name|getConcreteIndexName
parameter_list|()
block|{
return|return
literal|"test1"
return|;
block|}
block|}
end_class

end_unit

