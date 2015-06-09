begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
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
name|Term
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
name|index
operator|.
name|TermContext
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
name|BooleanClause
operator|.
name|Occur
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
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|search
operator|.
name|Queries
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
name|MappedFieldType
import|;
end_import

begin_comment
comment|/**  * Extended version of {@link CommonTermsQuery} that allows to pass in a  *<tt>minimumNumberShouldMatch</tt> specification that uses the actual num of high frequent terms  * to calculate the minimum matching terms.  */
end_comment

begin_class
DECL|class|ExtendedCommonTermsQuery
specifier|public
class|class
name|ExtendedCommonTermsQuery
extends|extends
name|CommonTermsQuery
block|{
DECL|field|fieldType
specifier|private
specifier|final
name|MappedFieldType
name|fieldType
decl_stmt|;
DECL|method|ExtendedCommonTermsQuery
specifier|public
name|ExtendedCommonTermsQuery
parameter_list|(
name|Occur
name|highFreqOccur
parameter_list|,
name|Occur
name|lowFreqOccur
parameter_list|,
name|float
name|maxTermFrequency
parameter_list|,
name|boolean
name|disableCoord
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|)
block|{
name|super
argument_list|(
name|highFreqOccur
argument_list|,
name|lowFreqOccur
argument_list|,
name|maxTermFrequency
argument_list|,
name|disableCoord
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldType
operator|=
name|fieldType
expr_stmt|;
block|}
DECL|field|lowFreqMinNumShouldMatchSpec
specifier|private
name|String
name|lowFreqMinNumShouldMatchSpec
decl_stmt|;
DECL|field|highFreqMinNumShouldMatchSpec
specifier|private
name|String
name|highFreqMinNumShouldMatchSpec
decl_stmt|;
annotation|@
name|Override
DECL|method|calcLowFreqMinimumNumberShouldMatch
specifier|protected
name|int
name|calcLowFreqMinimumNumberShouldMatch
parameter_list|(
name|int
name|numOptional
parameter_list|)
block|{
return|return
name|calcMinimumNumberShouldMatch
argument_list|(
name|lowFreqMinNumShouldMatchSpec
argument_list|,
name|numOptional
argument_list|)
return|;
block|}
DECL|method|calcMinimumNumberShouldMatch
specifier|protected
name|int
name|calcMinimumNumberShouldMatch
parameter_list|(
name|String
name|spec
parameter_list|,
name|int
name|numOptional
parameter_list|)
block|{
if|if
condition|(
name|spec
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|Queries
operator|.
name|calculateMinShouldMatch
argument_list|(
name|numOptional
argument_list|,
name|spec
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|calcHighFreqMinimumNumberShouldMatch
specifier|protected
name|int
name|calcHighFreqMinimumNumberShouldMatch
parameter_list|(
name|int
name|numOptional
parameter_list|)
block|{
return|return
name|calcMinimumNumberShouldMatch
argument_list|(
name|highFreqMinNumShouldMatchSpec
argument_list|,
name|numOptional
argument_list|)
return|;
block|}
DECL|method|setHighFreqMinimumNumberShouldMatch
specifier|public
name|void
name|setHighFreqMinimumNumberShouldMatch
parameter_list|(
name|String
name|spec
parameter_list|)
block|{
name|this
operator|.
name|highFreqMinNumShouldMatchSpec
operator|=
name|spec
expr_stmt|;
block|}
DECL|method|getHighFreqMinimumNumberShouldMatchSpec
specifier|public
name|String
name|getHighFreqMinimumNumberShouldMatchSpec
parameter_list|()
block|{
return|return
name|highFreqMinNumShouldMatchSpec
return|;
block|}
DECL|method|setLowFreqMinimumNumberShouldMatch
specifier|public
name|void
name|setLowFreqMinimumNumberShouldMatch
parameter_list|(
name|String
name|spec
parameter_list|)
block|{
name|this
operator|.
name|lowFreqMinNumShouldMatchSpec
operator|=
name|spec
expr_stmt|;
block|}
DECL|method|getLowFreqMinimumNumberShouldMatchSpec
specifier|public
name|String
name|getLowFreqMinimumNumberShouldMatchSpec
parameter_list|()
block|{
return|return
name|lowFreqMinNumShouldMatchSpec
return|;
block|}
annotation|@
name|Override
DECL|method|newTermQuery
specifier|protected
name|Query
name|newTermQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|TermContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|newTermQuery
argument_list|(
name|term
argument_list|,
name|context
argument_list|)
return|;
block|}
specifier|final
name|Query
name|query
init|=
name|fieldType
operator|.
name|queryStringTermQuery
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|newTermQuery
argument_list|(
name|term
argument_list|,
name|context
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|query
return|;
block|}
block|}
block|}
end_class

end_unit

