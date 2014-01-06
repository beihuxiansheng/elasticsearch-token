begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elasticsearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.client.http
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|client
operator|.
name|http
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpEntityEnclosingRequestBase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * Allows to send GET requests providing a body (not supported out of the box)  */
end_comment

begin_class
DECL|class|HttpGetWithEntity
specifier|public
class|class
name|HttpGetWithEntity
extends|extends
name|HttpEntityEnclosingRequestBase
block|{
DECL|field|METHOD_NAME
specifier|public
specifier|final
specifier|static
name|String
name|METHOD_NAME
init|=
literal|"GET"
decl_stmt|;
DECL|method|HttpGetWithEntity
specifier|public
name|HttpGetWithEntity
parameter_list|(
specifier|final
name|URI
name|uri
parameter_list|)
block|{
name|setURI
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMethod
specifier|public
name|String
name|getMethod
parameter_list|()
block|{
return|return
name|METHOD_NAME
return|;
block|}
block|}
end_class

end_unit

