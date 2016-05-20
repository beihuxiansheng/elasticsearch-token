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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Exception thrown when an elasticsearch node responds to a request with a status code that indicates an error.  * Note that the response body gets passed in as a string and read eagerly, which means that the ElasticsearchResponse object  * is expected to be closed and available only to read metadata like status line, request line, response headers.  */
end_comment

begin_class
DECL|class|ElasticsearchResponseException
specifier|public
class|class
name|ElasticsearchResponseException
extends|extends
name|IOException
block|{
DECL|field|elasticsearchResponse
specifier|private
name|ElasticsearchResponse
name|elasticsearchResponse
decl_stmt|;
DECL|field|responseBody
specifier|private
specifier|final
name|String
name|responseBody
decl_stmt|;
DECL|method|ElasticsearchResponseException
specifier|public
name|ElasticsearchResponseException
parameter_list|(
name|ElasticsearchResponse
name|elasticsearchResponse
parameter_list|,
name|String
name|responseBody
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|buildMessage
argument_list|(
name|elasticsearchResponse
argument_list|,
name|responseBody
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|elasticsearchResponse
operator|=
name|elasticsearchResponse
expr_stmt|;
name|this
operator|.
name|responseBody
operator|=
name|responseBody
expr_stmt|;
block|}
DECL|method|buildMessage
specifier|private
specifier|static
name|String
name|buildMessage
parameter_list|(
name|ElasticsearchResponse
name|response
parameter_list|,
name|String
name|responseBody
parameter_list|)
block|{
name|String
name|message
init|=
name|response
operator|.
name|getRequestLine
argument_list|()
operator|.
name|getMethod
argument_list|()
operator|+
literal|" "
operator|+
name|response
operator|.
name|getHost
argument_list|()
operator|+
name|response
operator|.
name|getRequestLine
argument_list|()
operator|.
name|getUri
argument_list|()
operator|+
literal|": "
operator|+
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|responseBody
operator|!=
literal|null
condition|)
block|{
name|message
operator|+=
literal|"\n"
operator|+
name|responseBody
expr_stmt|;
block|}
return|return
name|message
return|;
block|}
comment|/**      * Returns the {@link ElasticsearchResponse} that caused this exception to be thrown.      * Expected to be used only to read metadata like status line, request line, response headers. The response body should      * be retrieved using {@link #getResponseBody()}      */
DECL|method|getElasticsearchResponse
specifier|public
name|ElasticsearchResponse
name|getElasticsearchResponse
parameter_list|()
block|{
return|return
name|elasticsearchResponse
return|;
block|}
comment|/**      * Returns the response body as a string or null if there wasn't any.      * The body is eagerly consumed when an ElasticsearchResponseException gets created, and its corresponding ElasticsearchResponse      * gets closed straightaway so this method is the only way to get back the response body that was returned.      */
DECL|method|getResponseBody
specifier|public
name|String
name|getResponseBody
parameter_list|()
block|{
return|return
name|responseBody
return|;
block|}
block|}
end_class

end_unit

