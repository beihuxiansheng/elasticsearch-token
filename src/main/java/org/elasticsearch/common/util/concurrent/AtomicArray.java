begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util.concurrent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
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
name|ImmutableList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchGenerationException
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
name|List
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
name|atomic
operator|.
name|AtomicReferenceArray
import|;
end_import

begin_comment
comment|/**  * A list backed by an {@link AtomicReferenceArray} with potential null values, easily allowing  * to get the concrete values as a list using {@link #asList()}.  */
end_comment

begin_class
DECL|class|AtomicArray
specifier|public
class|class
name|AtomicArray
parameter_list|<
name|E
parameter_list|>
block|{
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|AtomicArray
name|EMPTY
init|=
operator|new
name|AtomicArray
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|empty
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|E
name|empty
parameter_list|()
block|{
return|return
operator|(
name|E
operator|)
name|EMPTY
return|;
block|}
DECL|field|array
specifier|private
specifier|final
name|AtomicReferenceArray
argument_list|<
name|E
argument_list|>
name|array
decl_stmt|;
DECL|field|nonNullList
specifier|private
specifier|volatile
name|List
argument_list|<
name|Entry
argument_list|<
name|E
argument_list|>
argument_list|>
name|nonNullList
decl_stmt|;
DECL|method|AtomicArray
specifier|public
name|AtomicArray
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|array
operator|=
operator|new
name|AtomicReferenceArray
argument_list|<
name|E
argument_list|>
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**      * The size of the expected results, including potential null values.      */
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|array
operator|.
name|length
argument_list|()
return|;
block|}
comment|/**      * Sets the element at position {@code i} to the given value.      *      * @param i     the index      * @param value the new value      */
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|int
name|i
parameter_list|,
name|E
name|value
parameter_list|)
block|{
name|array
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|nonNullList
operator|!=
literal|null
condition|)
block|{
comment|// read first, lighter, and most times it will be null...
name|nonNullList
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**      * Gets the current value at position {@code i}.      *      * @param i the index      * @return the current value      */
DECL|method|get
specifier|public
name|E
name|get
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|array
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
comment|/**      * Returns the it as a non null list, with an Entry wrapping each value allowing to      * retain its index.      */
DECL|method|asList
specifier|public
name|List
argument_list|<
name|Entry
argument_list|<
name|E
argument_list|>
argument_list|>
name|asList
parameter_list|()
block|{
if|if
condition|(
name|nonNullList
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|array
operator|==
literal|null
operator|||
name|array
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|nonNullList
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|Entry
argument_list|<
name|E
argument_list|>
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Entry
argument_list|<
name|E
argument_list|>
argument_list|>
argument_list|(
name|array
operator|.
name|length
argument_list|()
argument_list|)
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
name|array
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|E
name|e
init|=
name|array
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|Entry
argument_list|<
name|E
argument_list|>
argument_list|(
name|i
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|nonNullList
operator|=
name|list
expr_stmt|;
block|}
block|}
return|return
name|nonNullList
return|;
block|}
comment|/**      * Copies the content of the underlying atomic array to a normal one.      */
DECL|method|toArray
specifier|public
name|E
index|[]
name|toArray
parameter_list|(
name|E
index|[]
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|length
operator|!=
name|array
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchGenerationException
argument_list|(
literal|"AtomicArrays can only be copied to arrays of the same size"
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|array
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|array
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
comment|/**      * An entry within the array.      */
DECL|class|Entry
specifier|public
specifier|static
class|class
name|Entry
parameter_list|<
name|E
parameter_list|>
block|{
comment|/**          * The original index of the value within the array.          */
DECL|field|index
specifier|public
specifier|final
name|int
name|index
decl_stmt|;
comment|/**          * The value.          */
DECL|field|value
specifier|public
specifier|final
name|E
name|value
decl_stmt|;
DECL|method|Entry
specifier|public
name|Entry
parameter_list|(
name|int
name|index
parameter_list|,
name|E
name|value
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

