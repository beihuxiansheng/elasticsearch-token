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
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|ActionRequestValidationException
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
name|BroadcastOperationRequest
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
name|mapper
operator|.
name|AllFieldMapper
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

begin_comment
comment|/**  * Terms request represent a request to get terms in one or more indices of specific fields and their  * document frequencies (in how many document each term exists).  *  *<p>By default, the "_all" field will be used to extract terms and frequencies.  *  *<p>This is very handy to implement things like tag clouds and auto complete (using {@link #prefix(String)} or  * {@link #regexp(String)}).  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TermsRequest
specifier|public
class|class
name|TermsRequest
extends|extends
name|BroadcastOperationRequest
block|{
comment|/**      * The type of sorting for terms.      */
DECL|enum|SortType
specifier|public
specifier|static
enum|enum
name|SortType
block|{
comment|/**          * Sort based on the term (lex).          */
DECL|enum constant|TERM
name|TERM
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
comment|/**          * Sort based on the term document frequency.          */
DECL|enum constant|FREQ
name|FREQ
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|;
DECL|field|value
specifier|private
name|byte
name|value
decl_stmt|;
DECL|method|SortType
name|SortType
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**          * The unique byte value of the sort type.          */
DECL|method|value
specifier|public
name|byte
name|value
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**          * Parses the sort type from its {@link #value()}.          */
DECL|method|fromValue
specifier|public
specifier|static
name|SortType
name|fromValue
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
switch|switch
condition|(
name|value
condition|)
block|{
case|case
literal|0
case|:
return|return
name|TERM
return|;
case|case
literal|1
case|:
return|return
name|FREQ
return|;
default|default:
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No value for ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/**          * Parses the sort type from a string. Can either be "term" or "freq". If<tt>null</tt>          * is passed, will return the defaultSort provided.          *          * @param value       The string value to parse. Can be either "term" or "freq"          * @param defaultSort The sort type to return in case value is<tt>null</tt>          * @return The sort type parsed          */
DECL|method|fromString
specifier|public
specifier|static
name|SortType
name|fromString
parameter_list|(
name|String
name|value
parameter_list|,
name|SortType
name|defaultSort
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|defaultSort
return|;
block|}
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|"term"
argument_list|)
condition|)
block|{
return|return
name|TERM
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|"freq"
argument_list|)
condition|)
block|{
return|return
name|FREQ
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Illegal sort type ["
operator|+
name|value
operator|+
literal|"], must be one of [term,freq]"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|DEFAULT_FIELDS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|DEFAULT_FIELDS
init|=
operator|new
name|String
index|[]
block|{
name|AllFieldMapper
operator|.
name|NAME
block|}
decl_stmt|;
DECL|field|fields
specifier|private
name|String
index|[]
name|fields
init|=
name|DEFAULT_FIELDS
decl_stmt|;
DECL|field|from
specifier|private
name|String
name|from
decl_stmt|;
DECL|field|fromInclusive
specifier|private
name|boolean
name|fromInclusive
init|=
literal|true
decl_stmt|;
DECL|field|to
specifier|private
name|String
name|to
decl_stmt|;
DECL|field|toInclusive
specifier|private
name|boolean
name|toInclusive
init|=
literal|true
decl_stmt|;
DECL|field|prefix
specifier|private
name|String
name|prefix
decl_stmt|;
DECL|field|regexp
specifier|private
name|String
name|regexp
decl_stmt|;
DECL|field|minFreq
specifier|private
name|int
name|minFreq
init|=
literal|1
decl_stmt|;
DECL|field|maxFreq
specifier|private
name|int
name|maxFreq
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
init|=
literal|10
decl_stmt|;
DECL|field|convert
specifier|private
name|boolean
name|convert
init|=
literal|true
decl_stmt|;
DECL|field|sortType
specifier|private
name|SortType
name|sortType
init|=
name|SortType
operator|.
name|TERM
decl_stmt|;
DECL|field|exact
specifier|private
name|boolean
name|exact
init|=
literal|false
decl_stmt|;
DECL|method|TermsRequest
name|TermsRequest
parameter_list|()
block|{     }
comment|/**      * Constructs a new terms requests with the provided indices. Don't pass anything for it to run      * over all the indices.      */
DECL|method|TermsRequest
specifier|public
name|TermsRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|super
argument_list|(
name|indices
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|validate
annotation|@
name|Override
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
name|super
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|fields
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|fields
operator|=
name|DEFAULT_FIELDS
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
comment|/**      * The fields within each document which terms will be iterated over and returned with the      * document frequencies.      */
DECL|method|fields
specifier|public
name|String
index|[]
name|fields
parameter_list|()
block|{
return|return
name|this
operator|.
name|fields
return|;
block|}
comment|/**      * The fields within each document which terms will be iterated over and returned with the      * document frequencies. By default will use the "_all" field.      */
DECL|method|fields
specifier|public
name|TermsRequest
name|fields
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The lower bound term from which the iteration will start.  Defaults to start from the      * first.      */
DECL|method|from
specifier|public
name|String
name|from
parameter_list|()
block|{
return|return
name|from
return|;
block|}
comment|/**      * The lower bound term from which the iteration will start.  Defaults to start from the      * first.      */
DECL|method|from
specifier|public
name|TermsRequest
name|from
parameter_list|(
name|Object
name|from
parameter_list|)
block|{
if|if
condition|(
name|from
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|from
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|from
operator|=
name|from
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Greater than (like setting from with fromIInclusive set to<tt>false</tt>).      */
DECL|method|gt
specifier|public
name|TermsRequest
name|gt
parameter_list|(
name|Object
name|from
parameter_list|)
block|{
name|from
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|fromInclusive
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Greater/equal than  (like setting from with fromInclusive set to<tt>true</tt>).      */
DECL|method|gte
specifier|public
name|TermsRequest
name|gte
parameter_list|(
name|Object
name|from
parameter_list|)
block|{
name|from
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|fromInclusive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Lower then (like setting to with toInclusive set to<tt>false</tt>)      */
DECL|method|lt
specifier|public
name|TermsRequest
name|lt
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
name|to
argument_list|(
name|to
argument_list|)
expr_stmt|;
name|toInclusive
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Lower/equal then (like setting to with toInclusive set to<tt>false</tt>)      */
DECL|method|lte
specifier|public
name|TermsRequest
name|lte
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
name|to
argument_list|(
name|to
argument_list|)
expr_stmt|;
name|toInclusive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the first from (if set using {@link #from(Object)} be inclusive or not. Defaults      * to<tt>false</tt> (not inclusive / exclusive).      */
DECL|method|fromInclusive
specifier|public
name|boolean
name|fromInclusive
parameter_list|()
block|{
return|return
name|fromInclusive
return|;
block|}
comment|/**      * Should the first from (if set using {@link #from(Object)} be inclusive or not. Defaults      * to<tt>false</tt> (not inclusive / exclusive).      */
DECL|method|fromInclusive
specifier|public
name|TermsRequest
name|fromInclusive
parameter_list|(
name|boolean
name|fromInclusive
parameter_list|)
block|{
name|this
operator|.
name|fromInclusive
operator|=
name|fromInclusive
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The upper bound term to which the iteration will end. Defaults to unbound (<tt>null</tt>).      */
DECL|method|to
specifier|public
name|String
name|to
parameter_list|()
block|{
return|return
name|to
return|;
block|}
comment|/**      * The upper bound term to which the iteration will end. Defaults to unbound (<tt>null</tt>).      */
DECL|method|to
specifier|public
name|TermsRequest
name|to
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
if|if
condition|(
name|to
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|to
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|to
operator|=
name|to
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Should the last to (if set using {@link #to(Object)} be inclusive or not. Defaults to      *<tt>true</tt>.      */
DECL|method|toInclusive
specifier|public
name|boolean
name|toInclusive
parameter_list|()
block|{
return|return
name|toInclusive
return|;
block|}
comment|/**      * Should the last to (if set using {@link #to(Object)} be inclusive or not. Defaults to      *<tt>true</tt>.      */
DECL|method|toInclusive
specifier|public
name|TermsRequest
name|toInclusive
parameter_list|(
name|boolean
name|toInclusive
parameter_list|)
block|{
name|this
operator|.
name|toInclusive
operator|=
name|toInclusive
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional prefix from which the terms iteration will start (in lex order).      */
DECL|method|prefix
specifier|public
name|String
name|prefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
comment|/**      * An optional prefix from which the terms iteration will start (in lex order).      */
DECL|method|prefix
specifier|public
name|TermsRequest
name|prefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional regular expression to filter out terms (only the ones that match the regexp      * will return).      */
DECL|method|regexp
specifier|public
name|String
name|regexp
parameter_list|()
block|{
return|return
name|regexp
return|;
block|}
comment|/**      * An optional regular expression to filter out terms (only the ones that match the regexp      * will return).      */
DECL|method|regexp
specifier|public
name|void
name|regexp
parameter_list|(
name|String
name|regexp
parameter_list|)
block|{
name|this
operator|.
name|regexp
operator|=
name|regexp
expr_stmt|;
block|}
comment|/**      * An optional minimum document frequency to filter out terms.      */
DECL|method|minFreq
specifier|public
name|int
name|minFreq
parameter_list|()
block|{
return|return
name|minFreq
return|;
block|}
comment|/**      * An optional minimum document frequency to filter out terms.      */
DECL|method|minFreq
specifier|public
name|TermsRequest
name|minFreq
parameter_list|(
name|int
name|minFreq
parameter_list|)
block|{
name|this
operator|.
name|minFreq
operator|=
name|minFreq
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional maximum document frequency to filter out terms.      */
DECL|method|maxFreq
specifier|public
name|int
name|maxFreq
parameter_list|()
block|{
return|return
name|maxFreq
return|;
block|}
comment|/**      * An optional maximum document frequency to filter out terms.      */
DECL|method|maxFreq
specifier|public
name|TermsRequest
name|maxFreq
parameter_list|(
name|int
name|maxFreq
parameter_list|)
block|{
name|this
operator|.
name|maxFreq
operator|=
name|maxFreq
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The number of term / doc freq pairs to return per field. Defaults to<tt>10</tt>.      */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**      * The number of term / doc freq pairs to return per field. Defaults to<tt>10</tt>.      */
DECL|method|size
specifier|public
name|TermsRequest
name|size
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The type of sorting for term / doc freq. Can either sort on term (lex) or doc frequency. Defaults to      * {@link TermsRequest.SortType#TERM}.      */
DECL|method|sortType
specifier|public
name|SortType
name|sortType
parameter_list|()
block|{
return|return
name|sortType
return|;
block|}
comment|/**      * The type of sorting for term / doc freq. Can either sort on term (lex) or doc frequency. Defaults to      * {@link TermsRequest.SortType#TERM}.      */
DECL|method|sortType
specifier|public
name|TermsRequest
name|sortType
parameter_list|(
name|SortType
name|sortType
parameter_list|)
block|{
name|this
operator|.
name|sortType
operator|=
name|sortType
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the string representation of the sort type.      */
DECL|method|sortType
specifier|public
name|TermsRequest
name|sortType
parameter_list|(
name|String
name|sortType
parameter_list|)
block|{
return|return
name|sortType
argument_list|(
name|SortType
operator|.
name|fromString
argument_list|(
name|sortType
argument_list|,
name|this
operator|.
name|sortType
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Should the doc frequencies be exact frequencies. Exact frequencies takes into account deletes that      * have not been merged and cleaned (optimized). Note, when this is set to<tt>true</tt> this operation      * might be an expensive operation. Defaults to<tt>false</tt>.      */
DECL|method|exact
specifier|public
name|boolean
name|exact
parameter_list|()
block|{
return|return
name|exact
return|;
block|}
comment|/**      * Should the doc frequencies be exact frequencies. Exact frequencies takes into account deletes that      * have not been merged and cleaned (optimized). Note, when this is set to<tt>true</tt> this operation      * might be an expensive operation. Defaults to<tt>false</tt>.      */
DECL|method|exact
specifier|public
name|TermsRequest
name|exact
parameter_list|(
name|boolean
name|exact
parameter_list|)
block|{
name|this
operator|.
name|exact
operator|=
name|exact
expr_stmt|;
return|return
name|this
return|;
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
name|writeVInt
argument_list|(
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|from
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|from
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|to
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|to
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|fromInclusive
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|toInclusive
argument_list|)
expr_stmt|;
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|regexp
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|regexp
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|convert
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|sortType
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|minFreq
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|maxFreq
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|exact
argument_list|)
expr_stmt|;
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
name|fields
operator|=
operator|new
name|String
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fields
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|from
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|to
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
name|fromInclusive
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|toInclusive
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|prefix
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|regexp
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
name|size
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|convert
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|sortType
operator|=
name|TermsRequest
operator|.
name|SortType
operator|.
name|fromValue
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|minFreq
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|maxFreq
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|exact
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

