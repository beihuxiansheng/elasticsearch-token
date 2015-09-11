begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.snapshots
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|IndicesOptions
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
name|regex
operator|.
name|Regex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|IndexNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|List
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

begin_comment
comment|/**  * Snapshot utilities  */
end_comment

begin_class
DECL|class|SnapshotUtils
specifier|public
class|class
name|SnapshotUtils
block|{
comment|/**      * Filters out list of available indices based on the list of selected indices.      *      * @param availableIndices list of available indices      * @param selectedIndices  list of selected indices      * @param indicesOptions    ignore indices flag      * @return filtered out indices      */
DECL|method|filterIndices
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|filterIndices
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|availableIndices
parameter_list|,
name|String
index|[]
name|selectedIndices
parameter_list|,
name|IndicesOptions
name|indicesOptions
parameter_list|)
block|{
if|if
condition|(
name|selectedIndices
operator|==
literal|null
operator|||
name|selectedIndices
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|availableIndices
return|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|selectedIndices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|indexOrPattern
init|=
name|selectedIndices
index|[
name|i
index|]
decl_stmt|;
name|boolean
name|add
init|=
literal|true
decl_stmt|;
if|if
condition|(
operator|!
name|indexOrPattern
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|availableIndices
operator|.
name|contains
argument_list|(
name|indexOrPattern
argument_list|)
condition|)
block|{
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|indexOrPattern
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|indexOrPattern
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'+'
condition|)
block|{
name|add
operator|=
literal|true
expr_stmt|;
name|indexOrPattern
operator|=
name|indexOrPattern
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// if its the first, add empty set
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|result
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|indexOrPattern
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'-'
condition|)
block|{
comment|// if its the first, fill it with all the indices...
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|result
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|availableIndices
argument_list|)
expr_stmt|;
block|}
name|add
operator|=
literal|false
expr_stmt|;
name|indexOrPattern
operator|=
name|indexOrPattern
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indexOrPattern
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|Regex
operator|.
name|isSimpleMatchPattern
argument_list|(
name|indexOrPattern
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|availableIndices
operator|.
name|contains
argument_list|(
name|indexOrPattern
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|indicesOptions
operator|.
name|ignoreUnavailable
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IndexNotFoundException
argument_list|(
name|indexOrPattern
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
comment|// add all the previous ones...
name|result
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|availableIndices
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|add
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|indexOrPattern
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|remove
argument_list|(
name|indexOrPattern
argument_list|)
expr_stmt|;
block|}
block|}
block|}
continue|continue;
block|}
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
comment|// add all the previous ones...
name|result
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|availableIndices
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|availableIndices
control|)
block|{
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|indexOrPattern
argument_list|,
name|index
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|add
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|remove
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|found
operator|&&
operator|!
name|indicesOptions
operator|.
name|allowNoIndices
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IndexNotFoundException
argument_list|(
name|indexOrPattern
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|selectedIndices
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|result
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

