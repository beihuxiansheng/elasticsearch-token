begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|HttpServerTransport
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_enum
DECL|enum|Transports
specifier|public
enum|enum
name|Transports
block|{     ;
comment|/** threads whose name is prefixed by this string will be considered network threads, even though they aren't */
DECL|field|TEST_MOCK_TRANSPORT_THREAD_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|TEST_MOCK_TRANSPORT_THREAD_PREFIX
init|=
literal|"__mock_network_thread"
decl_stmt|;
comment|/**      * Utility method to detect whether a thread is a network thread. Typically      * used in assertions to make sure that we do not call blocking code from      * networking threads.      */
DECL|method|isTransportThread
specifier|public
specifier|static
specifier|final
name|boolean
name|isTransportThread
parameter_list|(
name|Thread
name|t
parameter_list|)
block|{
specifier|final
name|String
name|threadName
init|=
name|t
operator|.
name|getName
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|Arrays
operator|.
name|asList
argument_list|(
name|HttpServerTransport
operator|.
name|HTTP_SERVER_WORKER_THREAD_NAME_PREFIX
argument_list|,
name|TcpTransport
operator|.
name|TRANSPORT_SERVER_WORKER_THREAD_NAME_PREFIX
argument_list|,
name|TcpTransport
operator|.
name|TRANSPORT_CLIENT_BOSS_THREAD_NAME_PREFIX
argument_list|,
name|TEST_MOCK_TRANSPORT_THREAD_PREFIX
argument_list|)
control|)
block|{
if|if
condition|(
name|threadName
operator|.
name|contains
argument_list|(
name|s
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|assertTransportThread
specifier|public
specifier|static
name|boolean
name|assertTransportThread
parameter_list|()
block|{
specifier|final
name|Thread
name|t
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
assert|assert
name|isTransportThread
argument_list|(
name|t
argument_list|)
operator|:
literal|"Expected transport thread but got ["
operator|+
name|t
operator|+
literal|"]"
assert|;
return|return
literal|true
return|;
block|}
DECL|method|assertNotTransportThread
specifier|public
specifier|static
name|boolean
name|assertNotTransportThread
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
specifier|final
name|Thread
name|t
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
assert|assert
name|isTransportThread
argument_list|(
name|t
argument_list|)
operator|==
literal|false
operator|:
literal|"Expected current thread ["
operator|+
name|t
operator|+
literal|"] to not be a transport thread. Reason: ["
operator|+
name|reason
operator|+
literal|"]"
assert|;
return|return
literal|true
return|;
block|}
block|}
end_enum

end_unit

