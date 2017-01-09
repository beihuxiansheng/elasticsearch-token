begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
package|;
end_package

begin_comment
comment|/**  * An interface allowing to transfer an object to "XContent" using an {@link XContentBuilder}.  * The difference between {@link ToXContent} and {@link ToXContentObject} is that the former may output a fragment that  * requires to start and end a new anonymous object externally, while the latter guarantees that what gets printed  * out is fully valid syntax without any external addition.  */
end_comment

begin_interface
DECL|interface|ToXContentObject
specifier|public
interface|interface
name|ToXContentObject
extends|extends
name|ToXContent
block|{
annotation|@
name|Override
DECL|method|isFragment
specifier|default
name|boolean
name|isFragment
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_interface

end_unit

