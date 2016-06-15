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
comment|/**  * Implementers can be written to a {@linkplain StreamOutput} and read from a {@linkplain StreamInput}. This allows them to be "thrown  * across the wire" using Elasticsearch's internal protocol. If the implementer also implements equals and hashCode then a copy made by  * serializing and deserializing must be equal and have the same hashCode. It isn't required that such a copy be entirely unchanged. For  * example, {@link org.elasticsearch.common.unit.TimeValue} converts the time to nanoseconds for serialization.  * {@linkplain org.elasticsearch.common.unit.TimeValue} actually implements {@linkplain Writeable} not {@linkplain Streamable} but it has  * the same contract.  *  * Prefer implementing {@link Writeable} over implementing this interface where possible. Lots of code depends on this interface so this  * isn't always possible.  *  * Implementers of this interface almost always declare a no arg constructor that is exclusively used for creating "empty" objects on which  * you then call {@link #readFrom(StreamInput)}. Because {@linkplain #readFrom(StreamInput)} isn't part of the constructor the fields  * on implementers cannot be final. It is these reasons that this interface has fallen out of favor compared to {@linkplain Writeable}.  */
end_comment

begin_interface
DECL|interface|Streamable
specifier|public
interface|interface
name|Streamable
block|{
comment|/**      * Set this object's fields from a {@linkplain StreamInput}.      */
DECL|method|readFrom
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Write this object's fields to a {@linkplain StreamOutput}.      */
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
block|}
end_interface

end_unit

