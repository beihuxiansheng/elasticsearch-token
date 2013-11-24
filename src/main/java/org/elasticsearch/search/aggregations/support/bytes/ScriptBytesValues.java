begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.support.bytes
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
operator|.
name|bytes
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|BytesValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|SearchScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
operator|.
name|ScriptValues
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
import|;
end_import

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
name|Iterator
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ScriptBytesValues
specifier|public
class|class
name|ScriptBytesValues
extends|extends
name|BytesValues
implements|implements
name|ScriptValues
block|{
DECL|field|script
specifier|final
name|SearchScript
name|script
decl_stmt|;
DECL|field|iter
specifier|private
name|Iterator
argument_list|<
name|?
argument_list|>
name|iter
decl_stmt|;
DECL|field|value
specifier|private
name|Object
name|value
decl_stmt|;
DECL|field|scratch
specifier|private
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|ScriptBytesValues
specifier|public
name|ScriptBytesValues
parameter_list|(
name|SearchScript
name|script
parameter_list|)
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// assume multi-valued
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|script
specifier|public
name|SearchScript
name|script
parameter_list|()
block|{
return|return
name|script
return|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
name|script
operator|.
name|setNextDocId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|value
operator|=
name|script
operator|.
name|run
argument_list|()
expr_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|iter
operator|=
name|Iterators
operator|.
name|emptyIterator
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|isArray
argument_list|()
condition|)
block|{
specifier|final
name|int
name|length
init|=
name|Array
operator|.
name|getLength
argument_list|(
name|value
argument_list|)
decl_stmt|;
comment|// don't use Arrays.asList because the array may be an array of primitives?
name|iter
operator|=
operator|new
name|Iterator
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|i
operator|<
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|next
parameter_list|()
block|{
return|return
name|Array
operator|.
name|get
argument_list|(
name|value
argument_list|,
name|i
operator|++
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
expr_stmt|;
return|return
name|length
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|Collection
condition|)
block|{
specifier|final
name|Collection
argument_list|<
name|?
argument_list|>
name|coll
init|=
operator|(
name|Collection
argument_list|<
name|?
argument_list|>
operator|)
name|value
decl_stmt|;
name|iter
operator|=
name|coll
operator|.
name|iterator
argument_list|()
expr_stmt|;
return|return
name|coll
operator|.
name|size
argument_list|()
return|;
block|}
name|iter
operator|=
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|nextValue
specifier|public
name|BytesRef
name|nextValue
parameter_list|()
block|{
specifier|final
name|String
name|next
init|=
name|iter
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|scratch
operator|.
name|copyChars
argument_list|(
name|next
argument_list|)
expr_stmt|;
return|return
name|scratch
return|;
block|}
block|}
end_class

end_unit

