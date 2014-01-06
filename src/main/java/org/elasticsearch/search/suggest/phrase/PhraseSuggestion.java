begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.phrase
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|phrase
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|text
operator|.
name|Text
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
name|XContentBuilderString
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|Suggest
operator|.
name|Suggestion
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
comment|/**  * Suggestion entry returned from the {@link PhraseSuggester}.  */
end_comment

begin_class
DECL|class|PhraseSuggestion
specifier|public
class|class
name|PhraseSuggestion
extends|extends
name|Suggest
operator|.
name|Suggestion
argument_list|<
name|PhraseSuggestion
operator|.
name|Entry
argument_list|>
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|int
name|TYPE
init|=
literal|3
decl_stmt|;
DECL|method|PhraseSuggestion
specifier|public
name|PhraseSuggestion
parameter_list|()
block|{     }
DECL|method|PhraseSuggestion
specifier|public
name|PhraseSuggestion
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|newEntry
specifier|protected
name|Entry
name|newEntry
parameter_list|()
block|{
return|return
operator|new
name|Entry
argument_list|()
return|;
block|}
DECL|class|Entry
specifier|public
specifier|static
class|class
name|Entry
extends|extends
name|Suggestion
operator|.
name|Entry
argument_list|<
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
block|{
DECL|class|Fields
specifier|static
class|class
name|Fields
block|{
DECL|field|CUTOFF_SCORE
specifier|static
specifier|final
name|XContentBuilderString
name|CUTOFF_SCORE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"cutoff_score"
argument_list|)
decl_stmt|;
block|}
DECL|field|cutoffScore
specifier|protected
name|double
name|cutoffScore
init|=
name|Double
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|method|Entry
specifier|public
name|Entry
parameter_list|(
name|Text
name|text
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|double
name|cutoffScore
parameter_list|)
block|{
name|super
argument_list|(
name|text
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|cutoffScore
operator|=
name|cutoffScore
expr_stmt|;
block|}
DECL|method|Entry
specifier|public
name|Entry
parameter_list|()
block|{         }
comment|/**          * @return cutoff score for suggestions.  input term score * confidence for phrase suggest, 0 otherwise          */
DECL|method|getCutoffScore
specifier|public
name|double
name|getCutoffScore
parameter_list|()
block|{
return|return
name|cutoffScore
return|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|protected
name|void
name|merge
parameter_list|(
name|Suggestion
operator|.
name|Entry
argument_list|<
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
name|other
parameter_list|)
block|{
name|super
operator|.
name|merge
argument_list|(
name|other
argument_list|)
expr_stmt|;
comment|// If the cluster contains both pre 0.90.4 and post 0.90.4 nodes then we'll see Suggestion.Entry
comment|// objects being merged with PhraseSuggestion.Entry objects.  We merge Suggestion.Entry objects
comment|// by assuming they had a low cutoff score rather than a high one as that is the more common scenario
comment|// and the simplest one for us to implement.
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|PhraseSuggestion
operator|.
name|Entry
operator|)
condition|)
block|{
return|return;
block|}
name|PhraseSuggestion
operator|.
name|Entry
name|otherSuggestionEntry
init|=
operator|(
name|PhraseSuggestion
operator|.
name|Entry
operator|)
name|other
decl_stmt|;
name|this
operator|.
name|cutoffScore
operator|=
name|Math
operator|.
name|max
argument_list|(
name|this
operator|.
name|cutoffScore
argument_list|,
name|otherSuggestionEntry
operator|.
name|cutoffScore
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addOption
specifier|public
name|void
name|addOption
parameter_list|(
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
name|option
parameter_list|)
block|{
if|if
condition|(
name|option
operator|.
name|getScore
argument_list|()
operator|>
name|this
operator|.
name|cutoffScore
condition|)
block|{
name|this
operator|.
name|options
operator|.
name|add
argument_list|(
name|option
argument_list|)
expr_stmt|;
block|}
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
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// If the other side is older than 0.90.4 then it shouldn't be sending suggestions of this type but just in case
comment|// we're going to assume that they are regular suggestions so we won't read anything.
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_0_90_4
argument_list|)
condition|)
block|{
return|return;
block|}
name|cutoffScore
operator|=
name|in
operator|.
name|readDouble
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// If the other side of the message is older than 0.90.4 it'll interpret these suggestions as regular suggestions
comment|// so we have to pretend to be one which we can do by just calling the superclass writeTo and doing nothing else
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_0_90_4
argument_list|)
condition|)
block|{
return|return;
block|}
name|out
operator|.
name|writeDouble
argument_list|(
name|cutoffScore
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

