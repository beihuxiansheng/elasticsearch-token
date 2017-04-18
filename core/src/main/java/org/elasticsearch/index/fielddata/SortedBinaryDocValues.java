begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

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
comment|/**  * A list of per-document binary values, sorted  * according to {@link BytesRef#compareTo(BytesRef)}.  * There might be dups however.  */
end_comment

begin_comment
comment|// TODO: Should it expose a count (current approach) or return null when there are no more values?
end_comment

begin_class
DECL|class|SortedBinaryDocValues
specifier|public
specifier|abstract
class|class
name|SortedBinaryDocValues
block|{
comment|/**      * Advance this instance to the given document id      * @return true if there is a value for this document      */
DECL|method|advanceExact
specifier|public
specifier|abstract
name|boolean
name|advanceExact
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**       * Retrieves the number of values for the current document.  This must always      * be greater than zero.      * It is illegal to call this method after {@link #advanceExact(int)}      * returned {@code false}.      */
DECL|method|docValueCount
specifier|public
specifier|abstract
name|int
name|docValueCount
parameter_list|()
function_decl|;
comment|/**       * Iterates to the next value in the current document. Do not call this more than      * {@link #docValueCount} times for the document.      * Note that the returned {@link BytesRef} might be reused across invocations.      */
DECL|method|nextValue
specifier|public
specifier|abstract
name|BytesRef
name|nextValue
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

