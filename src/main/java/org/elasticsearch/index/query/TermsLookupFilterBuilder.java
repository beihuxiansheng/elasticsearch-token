begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|xcontent
operator|.
name|XContentBuilder
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
comment|/**  * A filer for a field based on several terms matching on any of them.  */
end_comment

begin_class
DECL|class|TermsLookupFilterBuilder
specifier|public
class|class
name|TermsLookupFilterBuilder
extends|extends
name|BaseFilterBuilder
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|lookupIndex
specifier|private
name|String
name|lookupIndex
decl_stmt|;
DECL|field|lookupType
specifier|private
name|String
name|lookupType
decl_stmt|;
DECL|field|lookupId
specifier|private
name|String
name|lookupId
decl_stmt|;
DECL|field|lookupRouting
specifier|private
name|String
name|lookupRouting
decl_stmt|;
DECL|field|lookupPath
specifier|private
name|String
name|lookupPath
decl_stmt|;
DECL|field|lookupCache
specifier|private
name|Boolean
name|lookupCache
decl_stmt|;
DECL|field|cache
specifier|private
name|Boolean
name|cache
decl_stmt|;
DECL|field|cacheKey
specifier|private
name|String
name|cacheKey
decl_stmt|;
DECL|field|filterName
specifier|private
name|String
name|filterName
decl_stmt|;
DECL|method|TermsLookupFilterBuilder
specifier|public
name|TermsLookupFilterBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * Sets the filter name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|filterName
specifier|public
name|TermsLookupFilterBuilder
name|filterName
parameter_list|(
name|String
name|filterName
parameter_list|)
block|{
name|this
operator|.
name|filterName
operator|=
name|filterName
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the index name to lookup the terms from.      */
DECL|method|lookupIndex
specifier|public
name|TermsLookupFilterBuilder
name|lookupIndex
parameter_list|(
name|String
name|lookupIndex
parameter_list|)
block|{
name|this
operator|.
name|lookupIndex
operator|=
name|lookupIndex
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the index type to lookup the terms from.      */
DECL|method|lookupType
specifier|public
name|TermsLookupFilterBuilder
name|lookupType
parameter_list|(
name|String
name|lookupType
parameter_list|)
block|{
name|this
operator|.
name|lookupType
operator|=
name|lookupType
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the doc id to lookup the terms from.      */
DECL|method|lookupId
specifier|public
name|TermsLookupFilterBuilder
name|lookupId
parameter_list|(
name|String
name|lookupId
parameter_list|)
block|{
name|this
operator|.
name|lookupId
operator|=
name|lookupId
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the path within the document to lookup the terms from.      */
DECL|method|lookupPath
specifier|public
name|TermsLookupFilterBuilder
name|lookupPath
parameter_list|(
name|String
name|lookupPath
parameter_list|)
block|{
name|this
operator|.
name|lookupPath
operator|=
name|lookupPath
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|lookupRouting
specifier|public
name|TermsLookupFilterBuilder
name|lookupRouting
parameter_list|(
name|String
name|lookupRouting
parameter_list|)
block|{
name|this
operator|.
name|lookupRouting
operator|=
name|lookupRouting
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|lookupCache
specifier|public
name|TermsLookupFilterBuilder
name|lookupCache
parameter_list|(
name|boolean
name|lookupCache
parameter_list|)
block|{
name|this
operator|.
name|lookupCache
operator|=
name|lookupCache
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|cache
specifier|public
name|TermsLookupFilterBuilder
name|cache
parameter_list|(
name|boolean
name|cache
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|cacheKey
specifier|public
name|TermsLookupFilterBuilder
name|cacheKey
parameter_list|(
name|String
name|cacheKey
parameter_list|)
block|{
name|this
operator|.
name|cacheKey
operator|=
name|cacheKey
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|public
name|void
name|doXContent
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
name|builder
operator|.
name|startObject
argument_list|(
name|TermsFilterParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|lookupIndex
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
name|lookupIndex
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|lookupType
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"id"
argument_list|,
name|lookupId
argument_list|)
expr_stmt|;
if|if
condition|(
name|lookupRouting
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"routing"
argument_list|,
name|lookupRouting
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lookupCache
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"cache"
argument_list|,
name|lookupCache
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
literal|"path"
argument_list|,
name|lookupPath
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|filterName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_name"
argument_list|,
name|filterName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_cache"
argument_list|,
name|cache
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cacheKey
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_cache_key"
argument_list|,
name|cacheKey
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

