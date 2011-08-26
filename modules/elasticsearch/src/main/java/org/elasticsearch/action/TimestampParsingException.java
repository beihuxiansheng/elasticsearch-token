begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
import|;
end_import

begin_comment
comment|/**  * @author paikan (benjamin.deveze)  */
end_comment

begin_class
DECL|class|TimestampParsingException
specifier|public
class|class
name|TimestampParsingException
extends|extends
name|ElasticSearchException
block|{
DECL|field|timestamp
specifier|private
specifier|final
name|String
name|timestamp
decl_stmt|;
DECL|method|TimestampParsingException
specifier|public
name|TimestampParsingException
parameter_list|(
name|String
name|timestamp
parameter_list|)
block|{
name|super
argument_list|(
literal|"failed to parse timestamp ["
operator|+
name|timestamp
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
block|}
DECL|method|timestamp
specifier|public
name|String
name|timestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
block|}
end_class

end_unit

