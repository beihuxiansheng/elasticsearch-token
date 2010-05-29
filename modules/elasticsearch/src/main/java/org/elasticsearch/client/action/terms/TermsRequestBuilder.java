begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.action.terms
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
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
name|action
operator|.
name|ActionListener
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
name|ListenableActionFuture
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
name|PlainListenableActionFuture
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
name|terms
operator|.
name|TermsRequest
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
name|terms
operator|.
name|TermsResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|internal
operator|.
name|InternalClient
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TermsRequestBuilder
specifier|public
class|class
name|TermsRequestBuilder
block|{
DECL|field|client
specifier|private
specifier|final
name|InternalClient
name|client
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|TermsRequest
name|request
decl_stmt|;
DECL|method|TermsRequestBuilder
specifier|public
name|TermsRequestBuilder
parameter_list|(
name|InternalClient
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|request
operator|=
operator|new
name|TermsRequest
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sets the indices the terms will run against.      */
DECL|method|setIndices
specifier|public
name|TermsRequestBuilder
name|setIndices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|request
operator|.
name|indices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The fields within each document which terms will be iterated over and returned with the      * document frequencies. By default will use the "_all" field.      */
DECL|method|setFields
specifier|public
name|TermsRequestBuilder
name|setFields
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|request
operator|.
name|fields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The lower bound term from which the iteration will start.  Defaults to start from the      * first.      */
DECL|method|setFrom
specifier|public
name|TermsRequestBuilder
name|setFrom
parameter_list|(
name|Object
name|from
parameter_list|)
block|{
name|request
operator|.
name|from
argument_list|(
name|from
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Greater than (like setting from with fromIInclusive set to<tt>false</tt>).      */
DECL|method|setGreaterThan
specifier|public
name|TermsRequestBuilder
name|setGreaterThan
parameter_list|(
name|Object
name|from
parameter_list|)
block|{
name|request
operator|.
name|gt
argument_list|(
name|from
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Greater/equal than  (like setting from with fromInclusive set to<tt>true</tt>).      */
DECL|method|setGreaterEqualsThan
specifier|public
name|TermsRequestBuilder
name|setGreaterEqualsThan
parameter_list|(
name|Object
name|from
parameter_list|)
block|{
name|request
operator|.
name|gt
argument_list|(
name|from
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Lower then (like setting to with toInclusive set to<tt>false</tt>)      */
DECL|method|setLowerThan
specifier|public
name|TermsRequestBuilder
name|setLowerThan
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
name|request
operator|.
name|lt
argument_list|(
name|to
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Lower/equal then (like setting to with toInclusive set to<tt>false</tt>)      */
DECL|method|setLowerEqualThan
specifier|public
name|TermsRequestBuilder
name|setLowerEqualThan
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
name|request
operator|.
name|lte
argument_list|(
name|to
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the first from (if set using {@link #setFrom(Object)} be inclusive or not. Defaults      * to<tt>false</tt> (not inclusive / exclusive).      */
DECL|method|setFromInclusive
specifier|public
name|TermsRequestBuilder
name|setFromInclusive
parameter_list|(
name|boolean
name|fromInclusive
parameter_list|)
block|{
name|request
operator|.
name|fromInclusive
argument_list|(
name|fromInclusive
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The upper bound term to which the iteration will end. Defaults to unbound (<tt>null</tt>).      */
DECL|method|setTo
specifier|public
name|TermsRequestBuilder
name|setTo
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
name|request
operator|.
name|to
argument_list|(
name|to
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the last to (if set using {@link #setTo(Object)} be inclusive or not. Defaults to      *<tt>true</tt>.      */
DECL|method|setToInclusive
specifier|public
name|TermsRequestBuilder
name|setToInclusive
parameter_list|(
name|boolean
name|toInclusive
parameter_list|)
block|{
name|request
operator|.
name|toInclusive
argument_list|(
name|toInclusive
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional prefix from which the terms iteration will start (in lex order).      */
DECL|method|setPrefix
specifier|public
name|TermsRequestBuilder
name|setPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|request
operator|.
name|prefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional regular expression to filter out terms (only the ones that match the regexp      * will return).      */
DECL|method|setRegexp
specifier|public
name|TermsRequestBuilder
name|setRegexp
parameter_list|(
name|String
name|regexp
parameter_list|)
block|{
name|request
operator|.
name|regexp
argument_list|(
name|regexp
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional minimum document frequency to filter out terms.      */
DECL|method|setMinFreq
specifier|public
name|TermsRequestBuilder
name|setMinFreq
parameter_list|(
name|int
name|minFreq
parameter_list|)
block|{
name|request
operator|.
name|minFreq
argument_list|(
name|minFreq
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional maximum document frequency to filter out terms.      */
DECL|method|setMaxFreq
specifier|public
name|TermsRequestBuilder
name|setMaxFreq
parameter_list|(
name|int
name|maxFreq
parameter_list|)
block|{
name|request
operator|.
name|maxFreq
argument_list|(
name|maxFreq
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The number of term / doc freq pairs to return per field. Defaults to<tt>10</tt>.      */
DECL|method|setSize
specifier|public
name|TermsRequestBuilder
name|setSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|request
operator|.
name|size
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The type of sorting for term / doc freq. Can either sort on term (lex) or doc frequency. Defaults to      * {@link TermsRequest.SortType#TERM}.      */
DECL|method|setSortType
specifier|public
name|TermsRequestBuilder
name|setSortType
parameter_list|(
name|TermsRequest
operator|.
name|SortType
name|sortType
parameter_list|)
block|{
name|request
operator|.
name|sortType
argument_list|(
name|sortType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the string representation of the sort type.      */
DECL|method|setSortType
specifier|public
name|TermsRequestBuilder
name|setSortType
parameter_list|(
name|String
name|sortType
parameter_list|)
block|{
name|request
operator|.
name|sortType
argument_list|(
name|sortType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the doc frequencies be exact frequencies. Exact frequencies takes into account deletes that      * have not been merged and cleaned (optimized). Note, when this is set to<tt>true</tt> this operation      * might be an expensive operation. Defaults to<tt>false</tt>.      */
DECL|method|setExact
specifier|public
name|TermsRequestBuilder
name|setExact
parameter_list|(
name|boolean
name|exact
parameter_list|)
block|{
name|request
operator|.
name|exact
argument_list|(
name|exact
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Executes the operation asynchronously and returns a future.      */
DECL|method|execute
specifier|public
name|ListenableActionFuture
argument_list|<
name|TermsResponse
argument_list|>
name|execute
parameter_list|()
block|{
name|PlainListenableActionFuture
argument_list|<
name|TermsResponse
argument_list|>
name|future
init|=
operator|new
name|PlainListenableActionFuture
argument_list|<
name|TermsResponse
argument_list|>
argument_list|(
name|request
operator|.
name|listenerThreaded
argument_list|()
argument_list|,
name|client
operator|.
name|threadPool
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|terms
argument_list|(
name|request
argument_list|,
name|future
argument_list|)
expr_stmt|;
return|return
name|future
return|;
block|}
comment|/**      * Executes the operation asynchronously with the provided listener.      */
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|ActionListener
argument_list|<
name|TermsResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|client
operator|.
name|terms
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

