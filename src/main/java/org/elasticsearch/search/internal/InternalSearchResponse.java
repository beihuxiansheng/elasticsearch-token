begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
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
name|common
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|ToXContent
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
name|search
operator|.
name|SearchHits
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
name|Aggregations
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
name|InternalAggregations
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
name|facet
operator|.
name|Facets
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
name|facet
operator|.
name|InternalFacets
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
name|suggest
operator|.
name|Suggest
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|InternalSearchHits
operator|.
name|readSearchHits
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|InternalSearchResponse
specifier|public
class|class
name|InternalSearchResponse
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|method|empty
specifier|public
specifier|static
name|InternalSearchResponse
name|empty
parameter_list|()
block|{
return|return
operator|new
name|InternalSearchResponse
argument_list|(
name|InternalSearchHits
operator|.
name|empty
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|field|hits
specifier|private
name|InternalSearchHits
name|hits
decl_stmt|;
DECL|field|facets
specifier|private
name|InternalFacets
name|facets
decl_stmt|;
DECL|field|aggregations
specifier|private
name|InternalAggregations
name|aggregations
decl_stmt|;
DECL|field|suggest
specifier|private
name|Suggest
name|suggest
decl_stmt|;
DECL|field|timedOut
specifier|private
name|boolean
name|timedOut
decl_stmt|;
DECL|method|InternalSearchResponse
specifier|private
name|InternalSearchResponse
parameter_list|()
block|{     }
DECL|method|InternalSearchResponse
specifier|public
name|InternalSearchResponse
parameter_list|(
name|InternalSearchHits
name|hits
parameter_list|,
name|InternalFacets
name|facets
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|,
name|Suggest
name|suggest
parameter_list|,
name|boolean
name|timedOut
parameter_list|)
block|{
name|this
operator|.
name|hits
operator|=
name|hits
expr_stmt|;
name|this
operator|.
name|facets
operator|=
name|facets
expr_stmt|;
name|this
operator|.
name|aggregations
operator|=
name|aggregations
expr_stmt|;
name|this
operator|.
name|suggest
operator|=
name|suggest
expr_stmt|;
name|this
operator|.
name|timedOut
operator|=
name|timedOut
expr_stmt|;
block|}
DECL|method|timedOut
specifier|public
name|boolean
name|timedOut
parameter_list|()
block|{
return|return
name|this
operator|.
name|timedOut
return|;
block|}
DECL|method|hits
specifier|public
name|SearchHits
name|hits
parameter_list|()
block|{
return|return
name|hits
return|;
block|}
DECL|method|facets
specifier|public
name|Facets
name|facets
parameter_list|()
block|{
return|return
name|facets
return|;
block|}
DECL|method|aggregations
specifier|public
name|Aggregations
name|aggregations
parameter_list|()
block|{
return|return
name|aggregations
return|;
block|}
DECL|method|suggest
specifier|public
name|Suggest
name|suggest
parameter_list|()
block|{
return|return
name|suggest
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|hits
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|facets
operator|!=
literal|null
condition|)
block|{
name|facets
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|aggregations
operator|!=
literal|null
condition|)
block|{
name|aggregations
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|suggest
operator|!=
literal|null
condition|)
block|{
name|suggest
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|readInternalSearchResponse
specifier|public
specifier|static
name|InternalSearchResponse
name|readInternalSearchResponse
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalSearchResponse
name|response
init|=
operator|new
name|InternalSearchResponse
argument_list|()
decl_stmt|;
name|response
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
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
name|hits
operator|=
name|readSearchHits
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|facets
operator|=
name|InternalFacets
operator|.
name|readFacets
argument_list|(
name|in
argument_list|)
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
name|aggregations
operator|=
name|InternalAggregations
operator|.
name|readAggregations
argument_list|(
name|in
argument_list|)
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
name|suggest
operator|=
name|Suggest
operator|.
name|readSuggest
argument_list|(
name|Suggest
operator|.
name|Fields
operator|.
name|SUGGEST
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
name|timedOut
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
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
name|hits
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|facets
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
name|facets
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|aggregations
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
name|aggregations
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|suggest
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
name|suggest
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|timedOut
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

