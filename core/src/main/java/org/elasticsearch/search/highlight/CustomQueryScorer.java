begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.highlight
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|highlight
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
name|index
operator|.
name|IndexReader
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|highlight
operator|.
name|QueryScorer
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
name|search
operator|.
name|highlight
operator|.
name|WeightedSpanTerm
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
name|search
operator|.
name|highlight
operator|.
name|WeightedSpanTermExtractor
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
name|lucene
operator|.
name|search
operator|.
name|function
operator|.
name|FiltersFunctionScoreQuery
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
name|lucene
operator|.
name|search
operator|.
name|function
operator|.
name|FunctionScoreQuery
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
name|Map
import|;
end_import

begin_class
DECL|class|CustomQueryScorer
specifier|public
specifier|final
class|class
name|CustomQueryScorer
extends|extends
name|QueryScorer
block|{
DECL|method|CustomQueryScorer
specifier|public
name|CustomQueryScorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|defaultField
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|,
name|reader
argument_list|,
name|field
argument_list|,
name|defaultField
argument_list|)
expr_stmt|;
block|}
DECL|method|CustomQueryScorer
specifier|public
name|CustomQueryScorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|,
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
DECL|method|CustomQueryScorer
specifier|public
name|CustomQueryScorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|defaultField
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|,
name|field
argument_list|,
name|defaultField
argument_list|)
expr_stmt|;
block|}
DECL|method|CustomQueryScorer
specifier|public
name|CustomQueryScorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
DECL|method|CustomQueryScorer
specifier|public
name|CustomQueryScorer
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
DECL|method|CustomQueryScorer
specifier|public
name|CustomQueryScorer
parameter_list|(
name|WeightedSpanTerm
index|[]
name|weightedTerms
parameter_list|)
block|{
name|super
argument_list|(
name|weightedTerms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newTermExtractor
specifier|protected
name|WeightedSpanTermExtractor
name|newTermExtractor
parameter_list|(
name|String
name|defaultField
parameter_list|)
block|{
return|return
name|defaultField
operator|==
literal|null
condition|?
operator|new
name|CustomWeightedSpanTermExtractor
argument_list|()
else|:
operator|new
name|CustomWeightedSpanTermExtractor
argument_list|(
name|defaultField
argument_list|)
return|;
block|}
DECL|class|CustomWeightedSpanTermExtractor
specifier|private
specifier|static
class|class
name|CustomWeightedSpanTermExtractor
extends|extends
name|WeightedSpanTermExtractor
block|{
DECL|method|CustomWeightedSpanTermExtractor
specifier|public
name|CustomWeightedSpanTermExtractor
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|CustomWeightedSpanTermExtractor
specifier|public
name|CustomWeightedSpanTermExtractor
parameter_list|(
name|String
name|defaultField
parameter_list|)
block|{
name|super
argument_list|(
name|defaultField
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractUnknownQuery
specifier|protected
name|void
name|extractUnknownQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|WeightedSpanTerm
argument_list|>
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|query
operator|instanceof
name|FunctionScoreQuery
condition|)
block|{
name|query
operator|=
operator|(
operator|(
name|FunctionScoreQuery
operator|)
name|query
operator|)
operator|.
name|getSubQuery
argument_list|()
expr_stmt|;
name|extract
argument_list|(
name|query
argument_list|,
name|query
operator|.
name|getBoost
argument_list|()
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|FiltersFunctionScoreQuery
condition|)
block|{
name|query
operator|=
operator|(
operator|(
name|FiltersFunctionScoreQuery
operator|)
name|query
operator|)
operator|.
name|getSubQuery
argument_list|()
expr_stmt|;
name|extract
argument_list|(
name|query
argument_list|,
name|query
operator|.
name|getBoost
argument_list|()
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|extractWeightedTerms
argument_list|(
name|terms
argument_list|,
name|query
argument_list|,
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

