begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.termvectors
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvectors
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
name|ActionRequestBuilder
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
name|ElasticsearchClient
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
name|xcontent
operator|.
name|XContentBuilder
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
name|VersionType
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

begin_comment
comment|/**  * The builder class for a term vector request.  * Returns the term vector (doc frequency, positions, offsets) for a document.  *<p>  * Note, the {@code index}, {@code type} and {@code id} are  * required.  */
end_comment

begin_class
DECL|class|TermVectorsRequestBuilder
specifier|public
class|class
name|TermVectorsRequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|TermVectorsRequest
argument_list|,
name|TermVectorsResponse
argument_list|,
name|TermVectorsRequestBuilder
argument_list|>
block|{
DECL|method|TermVectorsRequestBuilder
specifier|public
name|TermVectorsRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|TermVectorsAction
name|action
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
name|action
argument_list|,
operator|new
name|TermVectorsRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new term vector request builder for a document that will be fetch      * from the provided index. Use {@code index}, {@code type} and      * {@code id} to specify the document to load.      */
DECL|method|TermVectorsRequestBuilder
specifier|public
name|TermVectorsRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|TermVectorsAction
name|action
parameter_list|,
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
name|action
argument_list|,
operator|new
name|TermVectorsRequest
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the index where the document is located.      */
DECL|method|setIndex
specifier|public
name|TermVectorsRequestBuilder
name|setIndex
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|request
operator|.
name|index
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the type of the document.      */
DECL|method|setType
specifier|public
name|TermVectorsRequestBuilder
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|request
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the id of the document.      */
DECL|method|setId
specifier|public
name|TermVectorsRequestBuilder
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|request
operator|.
name|id
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the artificial document from which to generate term vectors.      */
DECL|method|setDoc
specifier|public
name|TermVectorsRequestBuilder
name|setDoc
parameter_list|(
name|XContentBuilder
name|xContent
parameter_list|)
block|{
name|request
operator|.
name|doc
argument_list|(
name|xContent
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the routing. Required if routing isn't id based.      */
DECL|method|setRouting
specifier|public
name|TermVectorsRequestBuilder
name|setRouting
parameter_list|(
name|String
name|routing
parameter_list|)
block|{
name|request
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the parent id of this document. Will simply set the routing to this value, as it is only      * used for routing with delete requests.      */
DECL|method|setParent
specifier|public
name|TermVectorsRequestBuilder
name|setParent
parameter_list|(
name|String
name|parent
parameter_list|)
block|{
name|request
operator|.
name|parent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the preference to execute the search. Defaults to randomize across shards. Can be set to      *<tt>_local</tt> to prefer local shards,<tt>_primary</tt> to execute only on primary shards, or      * a custom value, which guarantees that the same order will be used across different requests.      */
DECL|method|setPreference
specifier|public
name|TermVectorsRequestBuilder
name|setPreference
parameter_list|(
name|String
name|preference
parameter_list|)
block|{
name|request
operator|.
name|preference
argument_list|(
name|preference
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets whether to return the start and stop offsets for each term if they were stored or      * skip offsets.      */
DECL|method|setOffsets
specifier|public
name|TermVectorsRequestBuilder
name|setOffsets
parameter_list|(
name|boolean
name|offsets
parameter_list|)
block|{
name|request
operator|.
name|offsets
argument_list|(
name|offsets
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets whether to return the positions for each term if stored or skip.      */
DECL|method|setPositions
specifier|public
name|TermVectorsRequestBuilder
name|setPositions
parameter_list|(
name|boolean
name|positions
parameter_list|)
block|{
name|request
operator|.
name|positions
argument_list|(
name|positions
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets whether to return the payloads for each term or skip.      */
DECL|method|setPayloads
specifier|public
name|TermVectorsRequestBuilder
name|setPayloads
parameter_list|(
name|boolean
name|payloads
parameter_list|)
block|{
name|request
operator|.
name|payloads
argument_list|(
name|payloads
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets whether to return the term statistics for each term in the shard or skip.      */
DECL|method|setTermStatistics
specifier|public
name|TermVectorsRequestBuilder
name|setTermStatistics
parameter_list|(
name|boolean
name|termStatistics
parameter_list|)
block|{
name|request
operator|.
name|termStatistics
argument_list|(
name|termStatistics
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets whether to return the field statistics for each term in the shard or skip.      */
DECL|method|setFieldStatistics
specifier|public
name|TermVectorsRequestBuilder
name|setFieldStatistics
parameter_list|(
name|boolean
name|fieldStatistics
parameter_list|)
block|{
name|request
operator|.
name|fieldStatistics
argument_list|(
name|fieldStatistics
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets whether to return only term vectors for special selected fields. Returns the term      * vectors for all fields if selectedFields == null      */
DECL|method|setSelectedFields
specifier|public
name|TermVectorsRequestBuilder
name|setSelectedFields
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|request
operator|.
name|selectedFields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets whether term vectors are generated real-time.      */
DECL|method|setRealtime
specifier|public
name|TermVectorsRequestBuilder
name|setRealtime
parameter_list|(
name|boolean
name|realtime
parameter_list|)
block|{
name|request
operator|.
name|realtime
argument_list|(
name|realtime
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/*      * Sets the version, which will cause the get operation to only be performed if a matching      * version exists and no changes happened on the doc since then.      */
DECL|method|setVersion
specifier|public
name|TermVectorsRequestBuilder
name|setVersion
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|request
operator|.
name|version
argument_list|(
name|version
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/*      * Sets the versioning type. Defaults to {@link org.elasticsearch.index.VersionType#INTERNAL}.      */
DECL|method|setVersionType
specifier|public
name|TermVectorsRequestBuilder
name|setVersionType
parameter_list|(
name|VersionType
name|versionType
parameter_list|)
block|{
name|request
operator|.
name|versionType
argument_list|(
name|versionType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the analyzer used at each field when generating term vectors.      */
DECL|method|setPerFieldAnalyzer
specifier|public
name|TermVectorsRequestBuilder
name|setPerFieldAnalyzer
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|perFieldAnalyzer
parameter_list|)
block|{
name|request
operator|.
name|perFieldAnalyzer
argument_list|(
name|perFieldAnalyzer
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the settings for filtering out terms.      */
DECL|method|setFilterSettings
specifier|public
name|TermVectorsRequestBuilder
name|setFilterSettings
parameter_list|(
name|TermVectorsRequest
operator|.
name|FilterSettings
name|filterSettings
parameter_list|)
block|{
name|request
operator|.
name|filterSettings
argument_list|(
name|filterSettings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

