begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_comment
comment|/**  * Redirect to another web page  */
end_comment

begin_class
DECL|class|HttpRedirectRestResponse
specifier|public
class|class
name|HttpRedirectRestResponse
extends|extends
name|StringRestResponse
block|{
DECL|method|HttpRedirectRestResponse
specifier|public
name|HttpRedirectRestResponse
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|super
argument_list|(
name|RestStatus
operator|.
name|MOVED_PERMANENTLY
argument_list|,
literal|"<head><meta http-equiv=\"refresh\" content=\"0; URL="
operator|+
name|url
operator|+
literal|"\"></head>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|contentType
specifier|public
name|String
name|contentType
parameter_list|()
block|{
return|return
literal|"text/html"
return|;
block|}
block|}
end_class

end_unit

