begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|search
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
name|search
operator|.
name|DocIdSetIterator
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
name|lucene
operator|.
name|docset
operator|.
name|DocIdSets
import|;
end_import

begin_comment
comment|/**  * Extension of {@link DocIdSetIterator} that allows to know if iteration is  * implemented efficiently.  */
end_comment

begin_class
DECL|class|XDocIdSetIterator
specifier|public
specifier|abstract
class|class
name|XDocIdSetIterator
extends|extends
name|DocIdSetIterator
block|{
comment|/**      * Return<tt>true</tt> if this iterator cannot both      * {@link DocIdSetIterator#nextDoc} and {@link DocIdSetIterator#advance}      * in sub-linear time.      *      * Do not call this method directly, use {@link DocIdSets#isBroken}.      */
DECL|method|isBroken
specifier|public
specifier|abstract
name|boolean
name|isBroken
parameter_list|()
function_decl|;
block|}
end_class

end_unit

