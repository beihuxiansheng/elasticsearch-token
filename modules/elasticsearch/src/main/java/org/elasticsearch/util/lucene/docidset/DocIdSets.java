begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.lucene.docidset
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|lucene
operator|.
name|docidset
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
name|index
operator|.
name|IndexReader
import|;
end_import

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
name|DocIdSet
import|;
end_import

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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|OpenBitSetDISI
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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|DocIdSets
specifier|public
class|class
name|DocIdSets
block|{
comment|/**      * Returns a cacheable version of the doc id set (might be the same instance provided as a parameter).      */
DECL|method|cacheable
specifier|public
specifier|static
name|DocIdSet
name|cacheable
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|DocIdSet
name|docIdSet
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|docIdSet
operator|.
name|isCacheable
argument_list|()
condition|)
block|{
return|return
name|docIdSet
return|;
block|}
else|else
block|{
specifier|final
name|DocIdSetIterator
name|it
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// null is allowed to be returned by iterator(),
comment|// in this case we wrap with the empty set,
comment|// which is cacheable.
return|return
operator|(
name|it
operator|==
literal|null
operator|)
condition|?
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
else|:
operator|new
name|OpenBitSetDISI
argument_list|(
name|it
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|DocIdSets
specifier|private
name|DocIdSets
parameter_list|()
block|{      }
block|}
end_class

end_unit

