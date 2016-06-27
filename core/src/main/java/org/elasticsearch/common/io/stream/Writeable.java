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
comment|/**  * Implementers can be written to a {@linkplain StreamOutput} and read from a {@linkplain StreamInput}. This allows them to be "thrown  * across the wire" using Elasticsearch's internal protocol. If the implementer also implements equals and hashCode then a copy made by  * serializing and deserializing must be equal and have the same hashCode. It isn't required that such a copy be entirely unchanged.  *  * Prefer implementing this interface over implementing {@link Streamable} where possible. Lots of code depends on {@linkplain Streamable}  * so this isn't always possible.  */
end_comment

begin_interface
DECL|interface|Writeable
specifier|public
interface|interface
name|Writeable
block|{
comment|/**      * Write this into the {@linkplain StreamOutput}.      */
DECL|method|writeTo
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Reference to a method that can read some object from a stream. By convention this is a constructor that takes      * {@linkplain StreamInput} as an argument for most classes and a static method for things like enums. Returning null from one of these      * is always wrong - for that we use methods like {@link StreamInput#readOptionalWriteable(Reader)}.      */
annotation|@
name|FunctionalInterface
DECL|interface|Reader
interface|interface
name|Reader
parameter_list|<
name|R
parameter_list|>
block|{
comment|/**          * Read R from a stream.          */
DECL|method|read
name|R
name|read
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
block|}
end_interface

end_unit

