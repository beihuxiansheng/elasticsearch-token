begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util.set
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|set
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_class
DECL|class|Sets
specifier|public
specifier|final
class|class
name|Sets
block|{
DECL|method|Sets
specifier|private
name|Sets
parameter_list|()
block|{     }
DECL|method|newHashSet
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|HashSet
argument_list|<
name|T
argument_list|>
name|newHashSet
parameter_list|(
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
name|HashSet
argument_list|<
name|T
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
DECL|method|newHashSet
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|HashSet
argument_list|<
name|T
argument_list|>
name|newHashSet
parameter_list|(
name|Iterable
argument_list|<
name|T
argument_list|>
name|iterable
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
return|return
name|iterable
operator|instanceof
name|Collection
condition|?
operator|new
name|HashSet
argument_list|<>
argument_list|(
operator|(
name|Collection
operator|)
name|iterable
argument_list|)
else|:
name|newHashSet
argument_list|(
name|iterable
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
DECL|method|newHashSet
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|HashSet
argument_list|<
name|T
argument_list|>
name|newHashSet
parameter_list|(
name|T
modifier|...
name|elements
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|elements
argument_list|)
expr_stmt|;
name|HashSet
argument_list|<
name|T
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|elements
operator|.
name|length
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|set
argument_list|,
name|elements
argument_list|)
expr_stmt|;
return|return
name|set
return|;
block|}
comment|/**      * Create a new HashSet copying the original set with elements added. Useful      * for initializing constants without static blocks.      */
DECL|method|newHashSetCopyWith
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|HashSet
argument_list|<
name|T
argument_list|>
name|newHashSetCopyWith
parameter_list|(
name|Set
argument_list|<
name|T
argument_list|>
name|original
parameter_list|,
name|T
modifier|...
name|elements
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|original
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|elements
argument_list|)
expr_stmt|;
name|HashSet
argument_list|<
name|T
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|original
operator|.
name|size
argument_list|()
operator|+
name|elements
operator|.
name|length
argument_list|)
decl_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|original
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|set
argument_list|,
name|elements
argument_list|)
expr_stmt|;
return|return
name|set
return|;
block|}
DECL|method|newConcurrentHashSet
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Set
argument_list|<
name|T
argument_list|>
name|newConcurrentHashSet
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
argument_list|)
return|;
block|}
DECL|method|haveEmptyIntersection
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|boolean
name|haveEmptyIntersection
parameter_list|(
name|Set
argument_list|<
name|T
argument_list|>
name|left
parameter_list|,
name|Set
argument_list|<
name|T
argument_list|>
name|right
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|left
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|right
argument_list|)
expr_stmt|;
return|return
operator|!
name|left
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|k
lambda|->
name|right
operator|.
name|contains
argument_list|(
name|k
argument_list|)
argument_list|)
return|;
block|}
DECL|method|difference
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Set
argument_list|<
name|T
argument_list|>
name|difference
parameter_list|(
name|Set
argument_list|<
name|T
argument_list|>
name|left
parameter_list|,
name|Set
argument_list|<
name|T
argument_list|>
name|right
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|left
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|right
argument_list|)
expr_stmt|;
return|return
name|left
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|k
lambda|->
operator|!
name|right
operator|.
name|contains
argument_list|(
name|k
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|union
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Set
argument_list|<
name|T
argument_list|>
name|union
parameter_list|(
name|Set
argument_list|<
name|T
argument_list|>
name|left
parameter_list|,
name|Set
argument_list|<
name|T
argument_list|>
name|right
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|left
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|right
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|T
argument_list|>
name|union
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|left
argument_list|)
decl_stmt|;
name|union
operator|.
name|addAll
argument_list|(
name|right
argument_list|)
expr_stmt|;
return|return
name|union
return|;
block|}
block|}
end_class

end_unit

