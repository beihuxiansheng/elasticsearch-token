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
name|util
operator|.
name|IntArrayRef
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
name|util
operator|.
name|LongArrayRef
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_interface
DECL|interface|IntValues
specifier|public
interface|interface
name|IntValues
block|{
DECL|field|EMPTY
specifier|static
specifier|final
name|IntValues
name|EMPTY
init|=
operator|new
name|Empty
argument_list|()
decl_stmt|;
comment|/**      * Is one of the documents in this field data values is multi valued?      */
DECL|method|isMultiValued
name|boolean
name|isMultiValued
parameter_list|()
function_decl|;
comment|/**      * Is there a value for this doc?      */
DECL|method|hasValue
name|boolean
name|hasValue
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
DECL|method|getValue
name|int
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
DECL|method|getValueMissing
name|int
name|getValueMissing
parameter_list|(
name|int
name|docId
parameter_list|,
name|int
name|missingValue
parameter_list|)
function_decl|;
DECL|method|getValues
name|IntArrayRef
name|getValues
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
DECL|method|getIter
name|Iter
name|getIter
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
DECL|method|forEachValueInDoc
name|void
name|forEachValueInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|ValueInDocProc
name|proc
parameter_list|)
function_decl|;
DECL|interface|ValueInDocProc
specifier|static
interface|interface
name|ValueInDocProc
block|{
DECL|method|onValue
name|void
name|onValue
parameter_list|(
name|int
name|docId
parameter_list|,
name|int
name|value
parameter_list|)
function_decl|;
DECL|method|onMissing
name|void
name|onMissing
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
block|}
DECL|interface|Iter
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
name|int
name|next
parameter_list|()
function_decl|;
DECL|class|Empty
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
name|int
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
name|int
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
name|int
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
name|int
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
block|}
DECL|class|Empty
specifier|static
class|class
name|Empty
implements|implements
name|IntValues
block|{
annotation|@
name|Override
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
literal|false
return|;
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
name|int
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
literal|"Can't retrieve a value from an empty IntValues"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getValueMissing
specifier|public
name|int
name|getValueMissing
parameter_list|(
name|int
name|docId
parameter_list|,
name|int
name|missingValue
parameter_list|)
block|{
return|return
name|missingValue
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|IntArrayRef
name|getValues
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|IntArrayRef
operator|.
name|EMPTY
return|;
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
annotation|@
name|Override
DECL|method|forEachValueInDoc
specifier|public
name|void
name|forEachValueInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|ValueInDocProc
name|proc
parameter_list|)
block|{
name|proc
operator|.
name|onMissing
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|LongBased
specifier|public
specifier|static
class|class
name|LongBased
implements|implements
name|IntValues
block|{
DECL|field|values
specifier|private
specifier|final
name|LongValues
name|values
decl_stmt|;
DECL|field|arrayScratch
specifier|private
specifier|final
name|IntArrayRef
name|arrayScratch
init|=
operator|new
name|IntArrayRef
argument_list|(
operator|new
name|int
index|[
literal|1
index|]
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|field|iter
specifier|private
specifier|final
name|ValueIter
name|iter
init|=
operator|new
name|ValueIter
argument_list|()
decl_stmt|;
DECL|field|proc
specifier|private
specifier|final
name|Proc
name|proc
init|=
operator|new
name|Proc
argument_list|()
decl_stmt|;
DECL|method|LongBased
specifier|public
name|LongBased
parameter_list|(
name|LongValues
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
annotation|@
name|Override
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
name|values
operator|.
name|isMultiValued
argument_list|()
return|;
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
name|values
operator|.
name|hasValue
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|int
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|values
operator|.
name|getValue
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueMissing
specifier|public
name|int
name|getValueMissing
parameter_list|(
name|int
name|docId
parameter_list|,
name|int
name|missingValue
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|values
operator|.
name|getValueMissing
argument_list|(
name|docId
argument_list|,
name|missingValue
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|IntArrayRef
name|getValues
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|LongArrayRef
name|arrayRef
init|=
name|values
operator|.
name|getValues
argument_list|(
name|docId
argument_list|)
decl_stmt|;
name|int
name|size
init|=
name|arrayRef
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
name|IntArrayRef
operator|.
name|EMPTY
return|;
block|}
name|arrayScratch
operator|.
name|reset
argument_list|(
name|size
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|arrayRef
operator|.
name|start
init|;
name|i
operator|<
name|arrayRef
operator|.
name|end
condition|;
name|i
operator|++
control|)
block|{
name|arrayScratch
operator|.
name|values
index|[
name|arrayScratch
operator|.
name|end
operator|++
index|]
operator|=
operator|(
name|int
operator|)
name|arrayRef
operator|.
name|values
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|arrayScratch
return|;
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
name|iter
operator|.
name|reset
argument_list|(
name|values
operator|.
name|getIter
argument_list|(
name|docId
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|forEachValueInDoc
specifier|public
name|void
name|forEachValueInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|ValueInDocProc
name|proc
parameter_list|)
block|{
name|values
operator|.
name|forEachValueInDoc
argument_list|(
name|docId
argument_list|,
name|this
operator|.
name|proc
operator|.
name|reset
argument_list|(
name|proc
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|ValueIter
specifier|static
class|class
name|ValueIter
implements|implements
name|Iter
block|{
DECL|field|iter
specifier|private
name|LongValues
operator|.
name|Iter
name|iter
decl_stmt|;
DECL|method|reset
specifier|public
name|ValueIter
name|reset
parameter_list|(
name|LongValues
operator|.
name|Iter
name|iter
parameter_list|)
block|{
name|this
operator|.
name|iter
operator|=
name|iter
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
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|int
name|next
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|iter
operator|.
name|next
argument_list|()
return|;
block|}
block|}
DECL|class|Proc
specifier|static
class|class
name|Proc
implements|implements
name|LongValues
operator|.
name|ValueInDocProc
block|{
DECL|field|proc
specifier|private
name|ValueInDocProc
name|proc
decl_stmt|;
DECL|method|reset
specifier|public
name|Proc
name|reset
parameter_list|(
name|ValueInDocProc
name|proc
parameter_list|)
block|{
name|this
operator|.
name|proc
operator|=
name|proc
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|onValue
specifier|public
name|void
name|onValue
parameter_list|(
name|int
name|docId
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|proc
operator|.
name|onValue
argument_list|(
name|docId
argument_list|,
operator|(
name|int
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMissing
specifier|public
name|void
name|onMissing
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|proc
operator|.
name|onMissing
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_interface

end_unit

