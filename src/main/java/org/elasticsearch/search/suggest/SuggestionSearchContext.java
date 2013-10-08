begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
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
name|analysis
operator|.
name|Analyzer
import|;
end_import

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
name|ElasticsearchIllegalArgumentException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
comment|/**  */
end_comment

begin_class
DECL|class|SuggestionSearchContext
specifier|public
class|class
name|SuggestionSearchContext
block|{
DECL|field|suggestions
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SuggestionContext
argument_list|>
name|suggestions
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|SuggestionContext
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
DECL|method|addSuggestion
specifier|public
name|void
name|addSuggestion
parameter_list|(
name|String
name|name
parameter_list|,
name|SuggestionContext
name|suggestion
parameter_list|)
block|{
name|suggestions
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|suggestion
argument_list|)
expr_stmt|;
block|}
DECL|method|suggestions
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|SuggestionContext
argument_list|>
name|suggestions
parameter_list|()
block|{
return|return
name|suggestions
return|;
block|}
DECL|class|SuggestionContext
specifier|public
specifier|static
class|class
name|SuggestionContext
block|{
DECL|field|text
specifier|private
name|BytesRef
name|text
decl_stmt|;
DECL|field|suggester
specifier|private
specifier|final
name|Suggester
name|suggester
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
init|=
literal|5
decl_stmt|;
DECL|field|shardSize
specifier|private
name|int
name|shardSize
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|shardId
specifier|private
name|int
name|shardId
decl_stmt|;
DECL|field|index
specifier|private
name|String
name|index
decl_stmt|;
DECL|method|getText
specifier|public
name|BytesRef
name|getText
parameter_list|()
block|{
return|return
name|text
return|;
block|}
DECL|method|setText
specifier|public
name|void
name|setText
parameter_list|(
name|BytesRef
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
DECL|method|SuggestionContext
specifier|public
name|SuggestionContext
parameter_list|(
name|Suggester
name|suggester
parameter_list|)
block|{
name|this
operator|.
name|suggester
operator|=
name|suggester
expr_stmt|;
block|}
DECL|method|getSuggester
specifier|public
name|Suggester
argument_list|<
name|SuggestionContext
argument_list|>
name|getSuggester
parameter_list|()
block|{
return|return
name|this
operator|.
name|suggester
return|;
block|}
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
DECL|method|setAnalyzer
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|setSize
specifier|public
name|void
name|setSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Size must be positive but was: "
operator|+
name|size
argument_list|)
throw|;
block|}
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
DECL|method|getShardSize
specifier|public
name|Integer
name|getShardSize
parameter_list|()
block|{
return|return
name|shardSize
return|;
block|}
DECL|method|setShardSize
specifier|public
name|void
name|setShardSize
parameter_list|(
name|int
name|shardSize
parameter_list|)
block|{
if|if
condition|(
name|shardSize
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"ShardSize must be positive but was: "
operator|+
name|shardSize
argument_list|)
throw|;
block|}
name|this
operator|.
name|shardSize
operator|=
name|shardSize
expr_stmt|;
block|}
DECL|method|setShard
specifier|public
name|void
name|setShard
parameter_list|(
name|int
name|shardId
parameter_list|)
block|{
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
block|}
DECL|method|setIndex
specifier|public
name|void
name|setIndex
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
DECL|method|getIndex
specifier|public
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|getShard
specifier|public
name|int
name|getShard
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
block|}
block|}
end_class

end_unit

