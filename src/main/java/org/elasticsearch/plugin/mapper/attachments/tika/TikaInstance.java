begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.mapper.attachments.tika
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|mapper
operator|.
name|attachments
operator|.
name|tika
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|Tika
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TikaInstance
specifier|public
class|class
name|TikaInstance
block|{
DECL|field|tika
specifier|private
specifier|static
specifier|final
name|Tika
name|tika
init|=
operator|new
name|Tika
argument_list|()
decl_stmt|;
DECL|method|tika
specifier|public
specifier|static
name|Tika
name|tika
parameter_list|()
block|{
return|return
name|tika
return|;
block|}
block|}
end_class

end_unit

