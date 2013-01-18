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
name|ElasticSearchIllegalStateException
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
name|HashedBytesRef
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_interface
DECL|interface|HashedBytesValues
specifier|public
interface|interface
name|HashedBytesValues
block|{
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
comment|/**      * Returns a bytes value for a docId. Note, the content of it might be shared across invocation.      */
DECL|method|getValue
name|HashedBytesRef
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
comment|/**      * Returns a bytes value for a docId. The content is guaranteed not to be shared.      */
DECL|method|getValueSafe
name|HashedBytesRef
name|getValueSafe
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
comment|/**      * Returns a bytes value iterator for a docId. Note, the content of it might be shared across invocation.      */
DECL|method|getIter
name|Iter
name|getIter
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
comment|/**      * Returns a bytes value iterator for a docId. The content is guaranteed not to be shared.      */
DECL|method|getIterSafe
name|Iter
name|getIterSafe
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
comment|/**      * Go over all the possible values in their BytesRef format for a specific doc.      */
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
comment|/**      * Go over all the possible values in their BytesRef format for a specific doc.      */
DECL|method|forEachSafeValueInDoc
name|void
name|forEachSafeValueInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|ValueInDocProc
name|proc
parameter_list|)
function_decl|;
DECL|interface|ValueInDocProc
specifier|public
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
name|HashedBytesRef
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
name|HashedBytesRef
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
name|HashedBytesRef
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
name|HashedBytesRef
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
name|HashedBytesRef
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
name|HashedBytesRef
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
comment|/**      * A {@link BytesValues} based implementation.      */
DECL|class|BytesBased
specifier|static
class|class
name|BytesBased
implements|implements
name|HashedBytesValues
block|{
DECL|field|values
specifier|private
specifier|final
name|BytesValues
name|values
decl_stmt|;
DECL|field|scratch
specifier|protected
specifier|final
name|HashedBytesRef
name|scratch
init|=
operator|new
name|HashedBytesRef
argument_list|(
operator|new
name|BytesRef
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|valueIter
specifier|private
specifier|final
name|ValueIter
name|valueIter
init|=
operator|new
name|ValueIter
argument_list|()
decl_stmt|;
DECL|field|safeValueIter
specifier|private
specifier|final
name|SafeValueIter
name|safeValueIter
init|=
operator|new
name|SafeValueIter
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
DECL|field|safeProc
specifier|private
specifier|final
name|SafeProc
name|safeProc
init|=
operator|new
name|SafeProc
argument_list|()
decl_stmt|;
DECL|method|BytesBased
specifier|public
name|BytesBased
parameter_list|(
name|BytesValues
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
name|HashedBytesRef
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|BytesRef
name|value
init|=
name|values
operator|.
name|getValue
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|scratch
operator|.
name|bytes
operator|=
name|value
expr_stmt|;
return|return
name|scratch
operator|.
name|resetHashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSafe
specifier|public
name|HashedBytesRef
name|getValueSafe
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|new
name|HashedBytesRef
argument_list|(
name|values
operator|.
name|getValueSafe
argument_list|(
name|docId
argument_list|)
argument_list|)
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
name|valueIter
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
DECL|method|getIterSafe
specifier|public
name|Iter
name|getIterSafe
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|safeValueIter
operator|.
name|reset
argument_list|(
name|values
operator|.
name|getIterSafe
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
specifier|final
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
annotation|@
name|Override
DECL|method|forEachSafeValueInDoc
specifier|public
name|void
name|forEachSafeValueInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
specifier|final
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
name|safeProc
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
DECL|field|scratch
specifier|private
specifier|final
name|HashedBytesRef
name|scratch
init|=
operator|new
name|HashedBytesRef
argument_list|(
operator|new
name|BytesRef
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|iter
specifier|private
name|BytesValues
operator|.
name|Iter
name|iter
decl_stmt|;
DECL|method|reset
specifier|public
name|ValueIter
name|reset
parameter_list|(
name|BytesValues
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
name|HashedBytesRef
name|next
parameter_list|()
block|{
name|scratch
operator|.
name|bytes
operator|=
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|scratch
operator|.
name|resetHashCode
argument_list|()
return|;
block|}
block|}
DECL|class|SafeValueIter
specifier|static
class|class
name|SafeValueIter
implements|implements
name|Iter
block|{
DECL|field|iter
specifier|private
name|BytesValues
operator|.
name|Iter
name|iter
decl_stmt|;
DECL|method|reset
specifier|public
name|SafeValueIter
name|reset
parameter_list|(
name|BytesValues
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
name|HashedBytesRef
name|next
parameter_list|()
block|{
return|return
operator|new
name|HashedBytesRef
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|Proc
specifier|static
class|class
name|Proc
implements|implements
name|BytesValues
operator|.
name|ValueInDocProc
block|{
DECL|field|scratch
specifier|private
specifier|final
name|HashedBytesRef
name|scratch
init|=
operator|new
name|HashedBytesRef
argument_list|()
decl_stmt|;
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
name|BytesRef
name|value
parameter_list|)
block|{
name|scratch
operator|.
name|bytes
operator|=
name|value
expr_stmt|;
name|proc
operator|.
name|onValue
argument_list|(
name|docId
argument_list|,
name|scratch
operator|.
name|resetHashCode
argument_list|()
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
DECL|class|SafeProc
specifier|static
class|class
name|SafeProc
implements|implements
name|BytesValues
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
name|SafeProc
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
name|BytesRef
name|value
parameter_list|)
block|{
name|proc
operator|.
name|onValue
argument_list|(
name|docId
argument_list|,
operator|new
name|HashedBytesRef
argument_list|(
name|value
argument_list|)
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
DECL|class|StringBased
specifier|static
class|class
name|StringBased
implements|implements
name|HashedBytesValues
block|{
DECL|field|values
specifier|private
specifier|final
name|StringValues
name|values
decl_stmt|;
DECL|field|scratch
specifier|protected
specifier|final
name|HashedBytesRef
name|scratch
init|=
operator|new
name|HashedBytesRef
argument_list|(
operator|new
name|BytesRef
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|valueIter
specifier|private
specifier|final
name|ValueIter
name|valueIter
init|=
operator|new
name|ValueIter
argument_list|()
decl_stmt|;
DECL|field|safeValueIter
specifier|private
specifier|final
name|SafeValueIter
name|safeValueIter
init|=
operator|new
name|SafeValueIter
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
DECL|field|safeProc
specifier|private
specifier|final
name|SafeProc
name|safeProc
init|=
operator|new
name|SafeProc
argument_list|()
decl_stmt|;
DECL|method|StringBased
specifier|public
name|StringBased
parameter_list|(
name|StringValues
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
name|HashedBytesRef
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|String
name|value
init|=
name|values
operator|.
name|getValue
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|scratch
operator|.
name|bytes
operator|.
name|copyChars
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|scratch
operator|.
name|resetHashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSafe
specifier|public
name|HashedBytesRef
name|getValueSafe
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|String
name|value
init|=
name|values
operator|.
name|getValue
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|HashedBytesRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|values
operator|.
name|getValue
argument_list|(
name|docId
argument_list|)
argument_list|)
argument_list|)
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
name|valueIter
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
DECL|method|getIterSafe
specifier|public
name|Iter
name|getIterSafe
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|safeValueIter
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
specifier|final
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
annotation|@
name|Override
DECL|method|forEachSafeValueInDoc
specifier|public
name|void
name|forEachSafeValueInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
specifier|final
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
name|safeProc
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
DECL|field|scratch
specifier|private
specifier|final
name|HashedBytesRef
name|scratch
init|=
operator|new
name|HashedBytesRef
argument_list|(
operator|new
name|BytesRef
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|iter
specifier|private
name|StringValues
operator|.
name|Iter
name|iter
decl_stmt|;
DECL|method|reset
specifier|public
name|ValueIter
name|reset
parameter_list|(
name|StringValues
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
name|HashedBytesRef
name|next
parameter_list|()
block|{
name|scratch
operator|.
name|bytes
operator|.
name|copyChars
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|scratch
operator|.
name|resetHashCode
argument_list|()
return|;
block|}
block|}
DECL|class|SafeValueIter
specifier|static
class|class
name|SafeValueIter
implements|implements
name|Iter
block|{
DECL|field|iter
specifier|private
name|StringValues
operator|.
name|Iter
name|iter
decl_stmt|;
DECL|method|reset
specifier|public
name|SafeValueIter
name|reset
parameter_list|(
name|StringValues
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
name|HashedBytesRef
name|next
parameter_list|()
block|{
return|return
operator|new
name|HashedBytesRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|Proc
specifier|static
class|class
name|Proc
implements|implements
name|StringValues
operator|.
name|ValueInDocProc
block|{
DECL|field|scratch
specifier|private
specifier|final
name|HashedBytesRef
name|scratch
init|=
operator|new
name|HashedBytesRef
argument_list|(
operator|new
name|BytesRef
argument_list|()
argument_list|)
decl_stmt|;
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
name|String
name|value
parameter_list|)
block|{
name|scratch
operator|.
name|bytes
operator|.
name|copyChars
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|proc
operator|.
name|onValue
argument_list|(
name|docId
argument_list|,
name|scratch
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
DECL|class|SafeProc
specifier|static
class|class
name|SafeProc
implements|implements
name|StringValues
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
name|SafeProc
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
name|String
name|value
parameter_list|)
block|{
name|proc
operator|.
name|onValue
argument_list|(
name|docId
argument_list|,
operator|new
name|HashedBytesRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
argument_list|)
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

