begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.memcached.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|memcached
operator|.
name|test
package|;
end_package

begin_import
import|import
name|net
operator|.
name|spy
operator|.
name|memcached
operator|.
name|AddrUtil
import|;
end_import

begin_import
import|import
name|net
operator|.
name|spy
operator|.
name|memcached
operator|.
name|MemcachedClient
import|;
end_import

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
annotation|@
name|Test
DECL|class|TextMemcachedActionTests
specifier|public
class|class
name|TextMemcachedActionTests
extends|extends
name|AbstractMemcachedActionsTests
block|{
DECL|method|createMemcachedClient
annotation|@
name|Override
specifier|protected
name|MemcachedClient
name|createMemcachedClient
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|MemcachedClient
argument_list|(
name|AddrUtil
operator|.
name|getAddresses
argument_list|(
literal|"localhost:11211"
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

