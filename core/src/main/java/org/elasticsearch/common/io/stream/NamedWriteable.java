begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.io.stream
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
package|;
end_package

begin_comment
comment|/**  * A {@link Writeable} object identified by its name.  * To be used for arbitrary serializable objects (e.g. queries); when reading them, their name tells  * which specific object needs to be created.  */
end_comment

begin_interface
DECL|interface|NamedWriteable
specifier|public
interface|interface
name|NamedWriteable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Writeable
argument_list|<
name|T
argument_list|>
block|{
comment|/**      * Returns the name of the writeable object      */
DECL|method|getWriteableName
name|String
name|getWriteableName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

