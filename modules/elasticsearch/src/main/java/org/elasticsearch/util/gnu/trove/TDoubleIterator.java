begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.gnu.trove
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gnu
operator|.
name|trove
package|;
end_package

begin_comment
comment|//////////////////////////////////////////////////
end_comment

begin_comment
comment|// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
end_comment

begin_comment
comment|//////////////////////////////////////////////////
end_comment

begin_comment
comment|/**  * Iterator for double collections.  *  * @author Eric D. Friedman  * @version $Id: PIterator.template,v 1.1 2006/11/10 23:28:00 robeden Exp $  */
end_comment

begin_class
DECL|class|TDoubleIterator
specifier|public
class|class
name|TDoubleIterator
extends|extends
name|TPrimitiveIterator
block|{
comment|/**      * the collection on which the iterator operates      */
DECL|field|_hash
specifier|private
specifier|final
name|TDoubleHash
name|_hash
decl_stmt|;
comment|/**      * Creates a TDoubleIterator for the elements in the specified collection.      */
DECL|method|TDoubleIterator
specifier|public
name|TDoubleIterator
parameter_list|(
name|TDoubleHash
name|hash
parameter_list|)
block|{
name|super
argument_list|(
name|hash
argument_list|)
expr_stmt|;
name|this
operator|.
name|_hash
operator|=
name|hash
expr_stmt|;
block|}
comment|/**      * Advances the iterator to the next element in the underlying collection      * and returns it.      *      * @return the next double in the collection      * @throws NoSuchElementException if the iterator is already exhausted      */
DECL|method|next
specifier|public
name|double
name|next
parameter_list|()
block|{
name|moveToNextIndex
argument_list|()
expr_stmt|;
return|return
name|_hash
operator|.
name|_set
index|[
name|_index
index|]
return|;
block|}
block|}
end_class

begin_comment
comment|// TDoubleIterator
end_comment

end_unit

