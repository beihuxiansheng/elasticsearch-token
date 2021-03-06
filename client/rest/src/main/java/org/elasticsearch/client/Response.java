begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
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
name|Header
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpHost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|RequestLine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|StatusLine
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Holds an elasticsearch response. It wraps the {@link HttpResponse} returned and associates it with  * its corresponding {@link RequestLine} and {@link HttpHost}.  */
end_comment

begin_class
DECL|class|Response
specifier|public
class|class
name|Response
block|{
DECL|field|requestLine
specifier|private
specifier|final
name|RequestLine
name|requestLine
decl_stmt|;
DECL|field|host
specifier|private
specifier|final
name|HttpHost
name|host
decl_stmt|;
DECL|field|response
specifier|private
specifier|final
name|HttpResponse
name|response
decl_stmt|;
DECL|method|Response
name|Response
parameter_list|(
name|RequestLine
name|requestLine
parameter_list|,
name|HttpHost
name|host
parameter_list|,
name|HttpResponse
name|response
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|requestLine
argument_list|,
literal|"requestLine cannot be null"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|host
argument_list|,
literal|"node cannot be null"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|response
argument_list|,
literal|"response cannot be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestLine
operator|=
name|requestLine
expr_stmt|;
name|this
operator|.
name|host
operator|=
name|host
expr_stmt|;
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
block|}
comment|/**      * Returns the request line that generated this response      */
DECL|method|getRequestLine
specifier|public
name|RequestLine
name|getRequestLine
parameter_list|()
block|{
return|return
name|requestLine
return|;
block|}
comment|/**      * Returns the node that returned this response      */
DECL|method|getHost
specifier|public
name|HttpHost
name|getHost
parameter_list|()
block|{
return|return
name|host
return|;
block|}
comment|/**      * Returns the status line of the current response      */
DECL|method|getStatusLine
specifier|public
name|StatusLine
name|getStatusLine
parameter_list|()
block|{
return|return
name|response
operator|.
name|getStatusLine
argument_list|()
return|;
block|}
comment|/**      * Returns all the response headers      */
DECL|method|getHeaders
specifier|public
name|Header
index|[]
name|getHeaders
parameter_list|()
block|{
return|return
name|response
operator|.
name|getAllHeaders
argument_list|()
return|;
block|}
comment|/**      * Returns the value of the first header with a specified name of this message.      * If there is more than one matching header in the message the first element is returned.      * If there is no matching header in the message<code>null</code> is returned.      */
DECL|method|getHeader
specifier|public
name|String
name|getHeader
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Header
name|header
init|=
name|response
operator|.
name|getFirstHeader
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|header
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|header
operator|.
name|getValue
argument_list|()
return|;
block|}
comment|/**      * Returns the response body available, null otherwise      * @see HttpEntity      */
DECL|method|getEntity
specifier|public
name|HttpEntity
name|getEntity
parameter_list|()
block|{
return|return
name|response
operator|.
name|getEntity
argument_list|()
return|;
block|}
DECL|method|getHttpResponse
name|HttpResponse
name|getHttpResponse
parameter_list|()
block|{
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Response{"
operator|+
literal|"requestLine="
operator|+
name|requestLine
operator|+
literal|", host="
operator|+
name|host
operator|+
literal|", response="
operator|+
name|response
operator|.
name|getStatusLine
argument_list|()
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

