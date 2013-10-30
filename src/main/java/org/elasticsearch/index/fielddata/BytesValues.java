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
comment|/**  * A state-full lightweight per document set of<code>byte[]</code> values.  *  * To iterate over values in a document use the following pattern:  *<pre>  *   BytesValues values = ..;  *   final int numValues = values.setDocId(docId);  *   for (int i = 0; i< numValues; i++) {  *       BytesRef value = values.nextValue();  *       // process value  *   }  *</pre>  */
end_comment

begin_class
DECL|class|BytesValues
specifier|public
specifier|abstract
class|class
name|BytesValues
block|{
comment|/**      * An empty {@link BytesValues instance}      */
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|BytesValues
name|EMPTY
init|=
operator|new
name|Empty
argument_list|()
decl_stmt|;
DECL|field|multiValued
specifier|private
name|boolean
name|multiValued
decl_stmt|;
DECL|field|scratch
specifier|protected
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|docId
specifier|protected
name|int
name|docId
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Creates a new {@link BytesValues} instance      * @param multiValued<code>true</code> iff this instance is multivalued. Otherwise<code>false</code>.      */
DECL|method|BytesValues
specifier|protected
name|BytesValues
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
comment|/**      * Converts the current shared {@link BytesRef} to a stable instance. Note,      * this calls makes the bytes safe for *reads*, not writes (into the same BytesRef). For example,      * it makes it safe to be placed in a map.      */
DECL|method|copyShared
specifier|public
name|BytesRef
name|copyShared
parameter_list|()
block|{
return|return
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|scratch
argument_list|)
return|;
block|}
comment|/**      * Sets iteration to the specified docID and returns the number of      * values for this document ID,      * @param docId document ID      *      * @see #nextValue()      */
DECL|method|setDocument
specifier|public
specifier|abstract
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
comment|/**      * Returns the next value for the current docID set to {@link #setDocument(int)}.      * This method should only be called<tt>N</tt> times where<tt>N</tt> is the number      * returned from {@link #setDocument(int)}. If called more than<tt>N</tt> times the behavior      * is undefined.      *      * Note: the returned {@link BytesRef} might be shared across invocations.      *      * @return the next value for the current docID set to {@link #setDocument(int)}.      */
DECL|method|nextValue
specifier|public
specifier|abstract
name|BytesRef
name|nextValue
parameter_list|()
function_decl|;
comment|/**      * Returns the hash value of the previously returned shared {@link BytesRef} instances.      *      * @return the hash value of the previously returned shared {@link BytesRef} instances.      */
DECL|method|currentValueHash
specifier|public
name|int
name|currentValueHash
parameter_list|()
block|{
return|return
name|scratch
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**      * Ordinal based {@link BytesValues}.      */
DECL|class|WithOrdinals
specifier|public
specifier|static
specifier|abstract
class|class
name|WithOrdinals
extends|extends
name|BytesValues
block|{
DECL|field|ordinals
specifier|protected
specifier|final
name|Docs
name|ordinals
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
block|}
comment|/**          * Returns the associated ordinals instance.          * @return the associated ordinals instance.          */
DECL|method|ordinals
specifier|public
name|Ordinals
operator|.
name|Docs
name|ordinals
parameter_list|()
block|{
return|return
name|ordinals
return|;
block|}
comment|/**          * Returns the value for the given ordinal.          * @param ord the ordinal to lookup.          * @return a shared {@link BytesRef} instance holding the value associated          *         with the given ordinal or<code>null</code> if ordinal is<tt>0</tt>          */
DECL|method|getValueByOrd
specifier|public
specifier|abstract
name|BytesRef
name|getValueByOrd
parameter_list|(
name|long
name|ord
parameter_list|)
function_decl|;
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
name|int
name|length
init|=
name|ordinals
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|ordinals
operator|.
name|getOrd
argument_list|(
name|docId
argument_list|)
operator|!=
name|Ordinals
operator|.
name|MISSING_ORDINAL
operator|)
operator|==
name|length
operator|>
literal|0
operator|:
literal|"Doc: ["
operator|+
name|docId
operator|+
literal|"] hasValue: ["
operator|+
operator|(
name|ordinals
operator|.
name|getOrd
argument_list|(
name|docId
argument_list|)
operator|!=
name|Ordinals
operator|.
name|MISSING_ORDINAL
operator|)
operator|+
literal|"] but length is ["
operator|+
name|length
operator|+
literal|"]"
assert|;
return|return
name|length
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
assert|assert
name|docId
operator|!=
operator|-
literal|1
assert|;
return|return
name|getValueByOrd
argument_list|(
name|ordinals
operator|.
name|nextOrd
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * An empty {@link BytesValues} implementation      */
DECL|class|Empty
specifier|private
specifier|final
specifier|static
class|class
name|Empty
extends|extends
name|BytesValues
block|{
DECL|method|Empty
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
DECL|method|setDocument
specifier|public
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
literal|0
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
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Empty BytesValues has no next value"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|currentValueHash
specifier|public
name|int
name|currentValueHash
parameter_list|()
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Empty BytesValues has no hash for the current Value"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

