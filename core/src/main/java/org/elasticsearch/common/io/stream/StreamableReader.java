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
comment|/**  * Implementers can be read from {@linkplain StreamInput} by calling their {@link #readFrom(StreamInput)} method.  *  * It is common for implementers of this interface to declare a<code>public static final</code> instance of themselves named PROTOTYPE so  * users can call {@linkplain #readFrom(StreamInput)} on it. It is also fairly typical for readFrom to be implemented as a method that just  * calls a constructor that takes {@linkplain StreamInput} as a parameter. This allows the fields in the implementer to be  *<code>final</code>.  */
end_comment

begin_interface
DECL|interface|StreamableReader
specifier|public
interface|interface
name|StreamableReader
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**      * Reads an object of this type from the provided {@linkplain StreamInput}. The receiving instance remains unchanged.      */
DECL|method|readFrom
name|T
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

