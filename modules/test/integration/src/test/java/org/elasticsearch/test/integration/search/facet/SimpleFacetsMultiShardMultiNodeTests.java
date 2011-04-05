begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration.search.facet
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
operator|.
name|search
operator|.
name|facet
package|;
end_package

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests for several shards case since some facets do optimizations in this case. Make sure  * behavior remains the same.  */
end_comment

begin_class
annotation|@
name|Test
DECL|class|SimpleFacetsMultiShardMultiNodeTests
specifier|public
class|class
name|SimpleFacetsMultiShardMultiNodeTests
extends|extends
name|SimpleFacetsTests
block|{
DECL|method|numberOfShards
annotation|@
name|Override
specifier|protected
name|int
name|numberOfShards
parameter_list|()
block|{
return|return
literal|3
return|;
block|}
DECL|method|numberOfNodes
annotation|@
name|Override
specifier|protected
name|int
name|numberOfNodes
parameter_list|()
block|{
return|return
literal|2
return|;
block|}
block|}
end_class

end_unit

