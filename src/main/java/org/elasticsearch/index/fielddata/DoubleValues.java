begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|elasticsearch
operator|.
name|ElasticSearchIllegalStateException
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
name|ordinals
operator|.
name|Ordinals
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
name|ordinals
operator|.
name|Ordinals
operator|.
name|Docs
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|DoubleValues
specifier|public
specifier|abstract
class|class
name|DoubleValues
block|{
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|DoubleValues
name|EMPTY
init|=
operator|new
name|Empty
argument_list|()
decl_stmt|;
DECL|field|multiValued
specifier|private
specifier|final
name|boolean
name|multiValued
decl_stmt|;
DECL|field|iter
specifier|protected
specifier|final
name|Iter
operator|.
name|Single
name|iter
init|=
operator|new
name|Iter
operator|.
name|Single
argument_list|()
decl_stmt|;
DECL|method|DoubleValues
specifier|protected
name|DoubleValues
parameter_list|(
name|boolean
name|multiValued
parameter_list|)
block|{
name|this
operator|.
name|multiValued
operator|=
name|multiValued
expr_stmt|;
block|}
comment|/**      * Is one of the documents in this field data values is multi valued?      */
DECL|method|isMultiValued
specifier|public
specifier|final
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
name|multiValued
return|;
block|}
comment|/**      * Is there a value for this doc?      */
DECL|method|hasValue
specifier|public
specifier|abstract
name|boolean
name|hasValue
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
DECL|method|getValue
specifier|public
specifier|abstract
name|double
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
DECL|method|getValueMissing
specifier|public
name|double
name|getValueMissing
parameter_list|(
name|int
name|docId
parameter_list|,
name|double
name|missingValue
parameter_list|)
block|{
if|if
condition|(
name|hasValue
argument_list|(
name|docId
argument_list|)
condition|)
block|{
return|return
name|getValue
argument_list|(
name|docId
argument_list|)
return|;
block|}
return|return
name|missingValue
return|;
block|}
DECL|method|getIter
specifier|public
name|Iter
name|getIter
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
assert|assert
operator|!
name|isMultiValued
argument_list|()
assert|;
if|if
condition|(
name|hasValue
argument_list|(
name|docId
argument_list|)
condition|)
block|{
return|return
name|iter
operator|.
name|reset
argument_list|(
name|getValue
argument_list|(
name|docId
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Iter
operator|.
name|Empty
operator|.
name|INSTANCE
return|;
block|}
block|}
DECL|class|Dense
specifier|public
specifier|static
specifier|abstract
class|class
name|Dense
extends|extends
name|DoubleValues
block|{
DECL|method|Dense
specifier|protected
name|Dense
parameter_list|(
name|boolean
name|multiValued
parameter_list|)
block|{
name|super
argument_list|(
name|multiValued
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasValue
specifier|public
specifier|final
name|boolean
name|hasValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
DECL|method|getValueMissing
specifier|public
specifier|final
name|double
name|getValueMissing
parameter_list|(
name|int
name|docId
parameter_list|,
name|double
name|missingValue
parameter_list|)
block|{
assert|assert
name|hasValue
argument_list|(
name|docId
argument_list|)
assert|;
assert|assert
operator|!
name|isMultiValued
argument_list|()
assert|;
return|return
name|getValue
argument_list|(
name|docId
argument_list|)
return|;
block|}
DECL|method|getIter
specifier|public
specifier|final
name|Iter
name|getIter
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
assert|assert
name|hasValue
argument_list|(
name|docId
argument_list|)
assert|;
assert|assert
operator|!
name|isMultiValued
argument_list|()
assert|;
return|return
name|iter
operator|.
name|reset
argument_list|(
name|getValue
argument_list|(
name|docId
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|WithOrdinals
specifier|public
specifier|static
specifier|abstract
class|class
name|WithOrdinals
extends|extends
name|DoubleValues
block|{
DECL|field|ordinals
specifier|protected
specifier|final
name|Docs
name|ordinals
decl_stmt|;
DECL|field|iter
specifier|private
specifier|final
name|Iter
operator|.
name|Multi
name|iter
decl_stmt|;
DECL|method|WithOrdinals
specifier|protected
name|WithOrdinals
parameter_list|(
name|Ordinals
operator|.
name|Docs
name|ordinals
parameter_list|)
block|{
name|super
argument_list|(
name|ordinals
operator|.
name|isMultiValued
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|ordinals
operator|=
name|ordinals
expr_stmt|;
name|iter
operator|=
operator|new
name|Iter
operator|.
name|Multi
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|ordinals
specifier|public
name|Docs
name|ordinals
parameter_list|()
block|{
return|return
name|ordinals
return|;
block|}
annotation|@
name|Override
DECL|method|hasValue
specifier|public
specifier|final
name|boolean
name|hasValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|ordinals
operator|.
name|getOrd
argument_list|(
name|docId
argument_list|)
operator|!=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
specifier|final
name|double
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|getValueByOrd
argument_list|(
name|ordinals
operator|.
name|getOrd
argument_list|(
name|docId
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueMissing
specifier|public
specifier|final
name|double
name|getValueMissing
parameter_list|(
name|int
name|docId
parameter_list|,
name|double
name|missingValue
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|ordinals
operator|.
name|getOrd
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|==
literal|0
condition|)
block|{
return|return
name|missingValue
return|;
block|}
else|else
block|{
return|return
name|getValueByOrd
argument_list|(
name|ord
argument_list|)
return|;
block|}
block|}
DECL|method|getValueByOrd
specifier|public
specifier|abstract
name|double
name|getValueByOrd
parameter_list|(
name|int
name|ord
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|getIter
specifier|public
specifier|final
name|Iter
name|getIter
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|iter
operator|.
name|reset
argument_list|(
name|ordinals
operator|.
name|getIter
argument_list|(
name|docId
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|interface|Iter
specifier|public
specifier|static
interface|interface
name|Iter
block|{
DECL|method|hasNext
name|boolean
name|hasNext
parameter_list|()
function_decl|;
DECL|method|next
name|double
name|next
parameter_list|()
function_decl|;
DECL|class|Empty
specifier|public
specifier|static
class|class
name|Empty
implements|implements
name|Iter
block|{
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|Empty
name|INSTANCE
init|=
operator|new
name|Empty
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|double
name|next
parameter_list|()
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|()
throw|;
block|}
block|}
DECL|class|Single
specifier|static
class|class
name|Single
implements|implements
name|Iter
block|{
DECL|field|value
specifier|public
name|double
name|value
decl_stmt|;
DECL|field|done
specifier|public
name|boolean
name|done
decl_stmt|;
DECL|method|reset
specifier|public
name|Single
name|reset
parameter_list|(
name|double
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|done
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|!
name|done
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|double
name|next
parameter_list|()
block|{
assert|assert
operator|!
name|done
assert|;
name|done
operator|=
literal|true
expr_stmt|;
return|return
name|value
return|;
block|}
block|}
DECL|class|Multi
specifier|static
class|class
name|Multi
implements|implements
name|Iter
block|{
DECL|field|ordsIter
specifier|private
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ordinals
operator|.
name|Ordinals
operator|.
name|Docs
operator|.
name|Iter
name|ordsIter
decl_stmt|;
DECL|field|ord
specifier|private
name|int
name|ord
decl_stmt|;
DECL|field|values
specifier|private
name|WithOrdinals
name|values
decl_stmt|;
DECL|method|Multi
specifier|public
name|Multi
parameter_list|(
name|WithOrdinals
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|Multi
name|reset
parameter_list|(
name|Ordinals
operator|.
name|Docs
operator|.
name|Iter
name|ordsIter
parameter_list|)
block|{
name|this
operator|.
name|ordsIter
operator|=
name|ordsIter
expr_stmt|;
name|this
operator|.
name|ord
operator|=
name|ordsIter
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|ord
operator|!=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|double
name|next
parameter_list|()
block|{
name|double
name|value
init|=
name|values
operator|.
name|getValueByOrd
argument_list|(
name|ord
argument_list|)
decl_stmt|;
name|ord
operator|=
name|ordsIter
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|value
return|;
block|}
block|}
block|}
DECL|class|Empty
specifier|static
class|class
name|Empty
extends|extends
name|DoubleValues
block|{
DECL|method|Empty
specifier|public
name|Empty
parameter_list|()
block|{
name|super
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasValue
specifier|public
name|boolean
name|hasValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|double
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Can't retrieve a value from an empty DoubleValues"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getIter
specifier|public
name|Iter
name|getIter
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|Iter
operator|.
name|Empty
operator|.
name|INSTANCE
return|;
block|}
block|}
DECL|class|Filtered
specifier|public
specifier|static
class|class
name|Filtered
extends|extends
name|DoubleValues
block|{
DECL|field|delegate
specifier|protected
specifier|final
name|DoubleValues
name|delegate
decl_stmt|;
DECL|method|Filtered
specifier|public
name|Filtered
parameter_list|(
name|DoubleValues
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
operator|.
name|isMultiValued
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
DECL|method|hasValue
specifier|public
name|boolean
name|hasValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|hasValue
argument_list|(
name|docId
argument_list|)
return|;
block|}
DECL|method|getValue
specifier|public
name|double
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getValue
argument_list|(
name|docId
argument_list|)
return|;
block|}
DECL|method|getIter
specifier|public
name|Iter
name|getIter
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getIter
argument_list|(
name|docId
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

