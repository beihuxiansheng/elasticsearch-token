begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.builder
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|builder
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
name|builder
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A builder for search highlighting.  *  * @author kimchy (shay.banon)  * @see SearchSourceBuilder#highlight()  */
end_comment

begin_class
DECL|class|SearchSourceHighlightBuilder
specifier|public
class|class
name|SearchSourceHighlightBuilder
implements|implements
name|ToXContent
block|{
DECL|field|fields
specifier|private
name|List
argument_list|<
name|Field
argument_list|>
name|fields
decl_stmt|;
DECL|field|tagsSchema
specifier|private
name|String
name|tagsSchema
decl_stmt|;
DECL|field|preTags
specifier|private
name|String
index|[]
name|preTags
decl_stmt|;
DECL|field|postTags
specifier|private
name|String
index|[]
name|postTags
decl_stmt|;
DECL|field|order
specifier|private
name|String
name|order
decl_stmt|;
comment|/**      * Adds a field to be highlighted with default fragment size of 100 characters, and      * default number of fragments of 5.      *      * @param name The field to highlight      */
DECL|method|field
specifier|public
name|SearchSourceHighlightBuilder
name|field
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
block|}
name|fields
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a field to be highlighted with a provided fragment size (in characters), and      * default number of fragments of 5.      *      * @param name         The field to highlight      * @param fragmentSize The size of a fragment in characters      */
DECL|method|field
specifier|public
name|SearchSourceHighlightBuilder
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|fragmentSize
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
block|}
name|fields
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|name
argument_list|)
operator|.
name|fragmentSize
argument_list|(
name|fragmentSize
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a field to be highlighted with a provided fragment size (in characters), and      * a provided (maximum) number of fragments.      *      * @param name              The field to highlight      * @param fragmentSize      The size of a fragment in characters      * @param numberOfFragments The (maximum) number of fragments      */
DECL|method|field
specifier|public
name|SearchSourceHighlightBuilder
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|fragmentSize
parameter_list|,
name|int
name|numberOfFragments
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
block|}
name|fields
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|name
argument_list|)
operator|.
name|fragmentSize
argument_list|(
name|fragmentSize
argument_list|)
operator|.
name|numOfFragments
argument_list|(
name|numberOfFragments
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set a tag scheme that encapsulates a built in pre and post tags. The allows schemes      * are<tt>styled</tt> and<tt>default</tt>.      *      * @param schemaName The tag scheme name      */
DECL|method|tagsSchema
specifier|public
name|SearchSourceHighlightBuilder
name|tagsSchema
parameter_list|(
name|String
name|schemaName
parameter_list|)
block|{
name|this
operator|.
name|tagsSchema
operator|=
name|schemaName
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Explicitly set the pre tags that will be used for highlighting.      */
DECL|method|preTags
specifier|public
name|SearchSourceHighlightBuilder
name|preTags
parameter_list|(
name|String
modifier|...
name|preTags
parameter_list|)
block|{
name|this
operator|.
name|preTags
operator|=
name|preTags
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Explicitly set the post tags that will be used for highlighting.      */
DECL|method|postTags
specifier|public
name|SearchSourceHighlightBuilder
name|postTags
parameter_list|(
name|String
modifier|...
name|postTags
parameter_list|)
block|{
name|this
operator|.
name|postTags
operator|=
name|postTags
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The order of fragments per field. By default, ordered by the order in the      * highlighted text. Can be<tt>score</tt>, which then it will be ordered      * by score of the fragments.      */
DECL|method|order
specifier|public
name|SearchSourceHighlightBuilder
name|order
parameter_list|(
name|String
name|order
parameter_list|)
block|{
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|toXContent
annotation|@
name|Override
specifier|public
name|void
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
name|builder
operator|.
name|startObject
argument_list|(
literal|"highlight"
argument_list|)
expr_stmt|;
if|if
condition|(
name|tagsSchema
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"tags_schema"
argument_list|,
name|tagsSchema
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|preTags
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|array
argument_list|(
literal|"pre_tags"
argument_list|,
name|preTags
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|postTags
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|array
argument_list|(
literal|"post_tags"
argument_list|,
name|postTags
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|order
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"order"
argument_list|,
name|order
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"fields"
argument_list|)
expr_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|fragmentSize
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"fragment_size"
argument_list|,
name|field
operator|.
name|fragmentSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|field
operator|.
name|numOfFragments
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"number_of_fragments"
argument_list|,
name|field
operator|.
name|numOfFragments
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|class|Field
specifier|private
specifier|static
class|class
name|Field
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|fragmentSize
specifier|private
name|int
name|fragmentSize
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|numOfFragments
specifier|private
name|int
name|numOfFragments
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|Field
specifier|private
name|Field
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
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|fragmentSize
specifier|public
name|int
name|fragmentSize
parameter_list|()
block|{
return|return
name|fragmentSize
return|;
block|}
DECL|method|fragmentSize
specifier|public
name|Field
name|fragmentSize
parameter_list|(
name|int
name|fragmentSize
parameter_list|)
block|{
name|this
operator|.
name|fragmentSize
operator|=
name|fragmentSize
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|numOfFragments
specifier|public
name|int
name|numOfFragments
parameter_list|()
block|{
return|return
name|numOfFragments
return|;
block|}
DECL|method|numOfFragments
specifier|public
name|Field
name|numOfFragments
parameter_list|(
name|int
name|numOfFragments
parameter_list|)
block|{
name|this
operator|.
name|numOfFragments
operator|=
name|numOfFragments
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

