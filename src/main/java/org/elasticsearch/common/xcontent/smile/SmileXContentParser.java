begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.smile
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|smile
package|;
end_package

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonParser
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
name|xcontent
operator|.
name|XContentType
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
name|xcontent
operator|.
name|json
operator|.
name|JsonXContentParser
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SmileXContentParser
specifier|public
class|class
name|SmileXContentParser
extends|extends
name|JsonXContentParser
block|{
DECL|method|SmileXContentParser
specifier|public
name|SmileXContentParser
parameter_list|(
name|JsonParser
name|parser
parameter_list|)
block|{
name|super
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|contentType
specifier|public
name|XContentType
name|contentType
parameter_list|()
block|{
return|return
name|XContentType
operator|.
name|SMILE
return|;
block|}
block|}
end_class

end_unit

