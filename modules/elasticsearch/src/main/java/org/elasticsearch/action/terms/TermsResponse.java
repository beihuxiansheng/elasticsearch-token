begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.terms
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|terms
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
name|elasticsearch
operator|.
name|action
operator|.
name|ShardOperationFailedException
import|;
end_import

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
name|broadcast
operator|.
name|BroadcastOperationResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|terms
operator|.
name|FieldTermsFreq
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * The response of terms request. Includes a list of {@link FieldTermsFreq} which include  * the field and all its term / doc freq pair.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TermsResponse
specifier|public
class|class
name|TermsResponse
extends|extends
name|BroadcastOperationResponse
implements|implements
name|Iterable
argument_list|<
name|FieldTermsFreq
argument_list|>
block|{
DECL|field|numDocs
specifier|private
name|long
name|numDocs
decl_stmt|;
DECL|field|maxDoc
specifier|private
name|long
name|maxDoc
decl_stmt|;
DECL|field|numDeletedDocs
specifier|private
name|long
name|numDeletedDocs
decl_stmt|;
DECL|field|fieldsTermsFreq
specifier|private
name|FieldTermsFreq
index|[]
name|fieldsTermsFreq
decl_stmt|;
DECL|field|fieldsTermsFreqMap
specifier|private
specifier|transient
name|Map
argument_list|<
name|String
argument_list|,
name|FieldTermsFreq
argument_list|>
name|fieldsTermsFreqMap
decl_stmt|;
DECL|method|TermsResponse
name|TermsResponse
parameter_list|()
block|{     }
DECL|method|TermsResponse
name|TermsResponse
parameter_list|(
name|int
name|totalShards
parameter_list|,
name|int
name|successfulShards
parameter_list|,
name|int
name|failedShards
parameter_list|,
name|List
argument_list|<
name|ShardOperationFailedException
argument_list|>
name|shardFailures
parameter_list|,
name|FieldTermsFreq
index|[]
name|fieldsTermsFreq
parameter_list|,
name|long
name|numDocs
parameter_list|,
name|long
name|maxDoc
parameter_list|,
name|long
name|numDeletedDocs
parameter_list|)
block|{
name|super
argument_list|(
name|totalShards
argument_list|,
name|successfulShards
argument_list|,
name|failedShards
argument_list|,
name|shardFailures
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldsTermsFreq
operator|=
name|fieldsTermsFreq
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|this
operator|.
name|numDeletedDocs
operator|=
name|numDeletedDocs
expr_stmt|;
block|}
comment|/**      * The total number of documents.      */
DECL|method|numDocs
specifier|public
name|long
name|numDocs
parameter_list|()
block|{
return|return
name|this
operator|.
name|numDocs
return|;
block|}
comment|/**      * The total number of documents.      */
DECL|method|getNumDocs
specifier|public
name|long
name|getNumDocs
parameter_list|()
block|{
return|return
name|numDocs
return|;
block|}
comment|/**      * The total maximum number of documents (including deletions).      */
DECL|method|maxDoc
specifier|public
name|long
name|maxDoc
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxDoc
return|;
block|}
comment|/**      * The total maximum number of documents (including deletions).      */
DECL|method|getMaxDoc
specifier|public
name|long
name|getMaxDoc
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
comment|/**      * The number of deleted docs.      */
DECL|method|deletedDocs
specifier|public
name|long
name|deletedDocs
parameter_list|()
block|{
return|return
name|this
operator|.
name|numDeletedDocs
return|;
block|}
comment|/**      * The number of deleted docs.      */
DECL|method|getNumDeletedDocs
specifier|public
name|long
name|getNumDeletedDocs
parameter_list|()
block|{
return|return
name|numDeletedDocs
return|;
block|}
comment|/**      * Iterates over the {@link FieldTermsFreq}.      */
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|FieldTermsFreq
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Iterators
operator|.
name|forArray
argument_list|(
name|fieldsTermsFreq
argument_list|)
return|;
block|}
comment|/**      * The {@link FieldTermsFreq} for the specified field name,<tt>null</tt> if      * there is none.      *      * @param fieldName The field name to return the field terms freq for      * @return The field terms freq      */
DECL|method|field
specifier|public
name|FieldTermsFreq
name|field
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|fieldsAsMap
argument_list|()
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
comment|/**      * All the {@link FieldTermsFreq}.      */
DECL|method|fields
specifier|public
name|FieldTermsFreq
index|[]
name|fields
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldsTermsFreq
return|;
block|}
DECL|method|getFields
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|FieldTermsFreq
argument_list|>
name|getFields
parameter_list|()
block|{
return|return
name|fieldsAsMap
argument_list|()
return|;
block|}
comment|/**      * The pair of field name to {@link FieldTermsFreq} as map for simpler usage.      */
DECL|method|fieldsAsMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|FieldTermsFreq
argument_list|>
name|fieldsAsMap
parameter_list|()
block|{
if|if
condition|(
name|fieldsTermsFreqMap
operator|!=
literal|null
condition|)
block|{
return|return
name|fieldsTermsFreqMap
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|FieldTermsFreq
argument_list|>
name|fieldsTermsFreqMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldTermsFreq
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldTermsFreq
name|fieldTermsFreq
range|:
name|fieldsTermsFreq
control|)
block|{
name|fieldsTermsFreqMap
operator|.
name|put
argument_list|(
name|fieldTermsFreq
operator|.
name|fieldName
argument_list|()
argument_list|,
name|fieldTermsFreq
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|fieldsTermsFreqMap
operator|=
name|fieldsTermsFreqMap
expr_stmt|;
return|return
name|fieldsTermsFreqMap
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|numDocs
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|maxDoc
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|numDeletedDocs
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|fieldsTermsFreq
operator|=
operator|new
name|FieldTermsFreq
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldsTermsFreq
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fieldsTermsFreq
index|[
name|i
index|]
operator|=
name|readFieldTermsFreq
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeTo
annotation|@
name|Override
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|numDeletedDocs
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|fieldsTermsFreq
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldTermsFreq
name|fieldTermsFreq
range|:
name|fieldsTermsFreq
control|)
block|{
name|fieldTermsFreq
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

