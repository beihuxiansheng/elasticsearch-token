begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.collect
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|PeekingIterator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|UnmodifiableIterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_enum
DECL|enum|Iterators2
specifier|public
enum|enum
name|Iterators2
block|{     ;
comment|/** Remove duplicated elements from an iterator over sorted content. */
DECL|method|deduplicateSorted
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterator
argument_list|<
name|T
argument_list|>
name|deduplicateSorted
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|iterator
parameter_list|,
specifier|final
name|Comparator
argument_list|<
name|?
super|super
name|T
argument_list|>
name|comparator
parameter_list|)
block|{
comment|// TODO: infer type once JI-9019884 is fixed
specifier|final
name|PeekingIterator
argument_list|<
name|T
argument_list|>
name|it
init|=
name|Iterators
operator|.
expr|<
name|T
operator|>
name|peekingIterator
argument_list|(
name|iterator
argument_list|)
decl_stmt|;
return|return
operator|new
name|UnmodifiableIterator
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|it
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|T
name|next
parameter_list|()
block|{
specifier|final
name|T
name|ret
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
name|comparator
operator|.
name|compare
argument_list|(
name|ret
argument_list|,
name|it
operator|.
name|peek
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
block|{
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
assert|assert
operator|!
name|it
operator|.
name|hasNext
argument_list|()
operator|||
name|comparator
operator|.
name|compare
argument_list|(
name|ret
argument_list|,
name|it
operator|.
name|peek
argument_list|()
argument_list|)
operator|<
literal|0
operator|:
literal|"iterator is not sorted: "
operator|+
name|ret
operator|+
literal|"> "
operator|+
name|it
operator|.
name|peek
argument_list|()
assert|;
return|return
name|ret
return|;
block|}
block|}
return|;
block|}
comment|/** Return a merged view over several iterators, optionally deduplicating equivalent entries. */
DECL|method|mergeSorted
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterator
argument_list|<
name|T
argument_list|>
name|mergeSorted
parameter_list|(
name|Iterable
argument_list|<
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
argument_list|>
name|iterators
parameter_list|,
name|Comparator
argument_list|<
name|?
super|super
name|T
argument_list|>
name|comparator
parameter_list|,
name|boolean
name|deduplicate
parameter_list|)
block|{
name|Iterator
argument_list|<
name|T
argument_list|>
name|it
init|=
name|Iterators
operator|.
name|mergeSorted
argument_list|(
name|iterators
argument_list|,
name|comparator
argument_list|)
decl_stmt|;
if|if
condition|(
name|deduplicate
condition|)
block|{
name|it
operator|=
name|deduplicateSorted
argument_list|(
name|it
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
block|}
return|return
name|it
return|;
block|}
block|}
end_enum

end_unit

