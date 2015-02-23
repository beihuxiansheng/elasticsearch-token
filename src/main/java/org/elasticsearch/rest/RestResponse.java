begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|bytes
operator|.
name|BytesReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RestResponse
specifier|public
specifier|abstract
class|class
name|RestResponse
implements|implements
name|HasRestHeaders
block|{
DECL|field|customHeaders
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|customHeaders
decl_stmt|;
comment|/**      * The response content type.      */
DECL|method|contentType
specifier|public
specifier|abstract
name|String
name|contentType
parameter_list|()
function_decl|;
comment|/**      * Can the content byte[] be used only with this thread (<tt>false</tt>), or by any thread (<tt>true</tt>).      */
DECL|method|contentThreadSafe
specifier|public
specifier|abstract
name|boolean
name|contentThreadSafe
parameter_list|()
function_decl|;
comment|/**      * The response content. Note, if the content is {@link org.elasticsearch.common.lease.Releasable} it      * should automatically be released when done by the channel sending it.      */
DECL|method|content
specifier|public
specifier|abstract
name|BytesReference
name|content
parameter_list|()
function_decl|;
comment|/**      * The rest status code.      */
DECL|method|status
specifier|public
specifier|abstract
name|RestStatus
name|status
parameter_list|()
function_decl|;
DECL|method|addHeaders
specifier|public
name|void
name|addHeaders
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|headers
parameter_list|)
block|{
if|if
condition|(
name|customHeaders
operator|==
literal|null
condition|)
block|{
name|customHeaders
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|headers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|headers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
name|customHeaders
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|customHeaders
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
name|values
operator|.
name|addAll
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Add a custom header.      */
DECL|method|addHeader
specifier|public
name|void
name|addHeader
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|customHeaders
operator|==
literal|null
condition|)
block|{
name|customHeaders
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|header
init|=
name|customHeaders
operator|.
name|get
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
name|header
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|customHeaders
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|header
argument_list|)
expr_stmt|;
block|}
name|header
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns custom headers that have been added, or null if none have been set.      */
annotation|@
name|Override
annotation|@
name|Nullable
DECL|method|getHeaders
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getHeaders
parameter_list|()
block|{
return|return
name|customHeaders
return|;
block|}
block|}
end_class

end_unit

