begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.significant.heuristics
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|significant
operator|.
name|heuristics
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|ParseField
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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
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
name|query
operator|.
name|QueryParseContext
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
name|query
operator|.
name|QueryShardException
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

begin_class
DECL|class|NXYSignificanceHeuristic
specifier|public
specifier|abstract
class|class
name|NXYSignificanceHeuristic
extends|extends
name|SignificanceHeuristic
block|{
DECL|field|BACKGROUND_IS_SUPERSET
specifier|protected
specifier|static
specifier|final
name|ParseField
name|BACKGROUND_IS_SUPERSET
init|=
operator|new
name|ParseField
argument_list|(
literal|"background_is_superset"
argument_list|)
decl_stmt|;
DECL|field|INCLUDE_NEGATIVES_FIELD
specifier|protected
specifier|static
specifier|final
name|ParseField
name|INCLUDE_NEGATIVES_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"include_negatives"
argument_list|)
decl_stmt|;
DECL|field|SCORE_ERROR_MESSAGE
specifier|protected
specifier|static
specifier|final
name|String
name|SCORE_ERROR_MESSAGE
init|=
literal|", does your background filter not include all documents in the bucket? If so and it is intentional, set \""
operator|+
name|BACKGROUND_IS_SUPERSET
operator|.
name|getPreferredName
argument_list|()
operator|+
literal|"\": false"
decl_stmt|;
DECL|field|backgroundIsSuperset
specifier|protected
specifier|final
name|boolean
name|backgroundIsSuperset
decl_stmt|;
comment|/**      * Some heuristics do not differentiate between terms that are descriptive for subset or for      * the background without the subset. We might want to filter out the terms that are appear much less often      * in the subset than in the background without the subset.      */
DECL|field|includeNegatives
specifier|protected
specifier|final
name|boolean
name|includeNegatives
decl_stmt|;
DECL|method|NXYSignificanceHeuristic
specifier|protected
name|NXYSignificanceHeuristic
parameter_list|(
name|boolean
name|includeNegatives
parameter_list|,
name|boolean
name|backgroundIsSuperset
parameter_list|)
block|{
name|this
operator|.
name|includeNegatives
operator|=
name|includeNegatives
expr_stmt|;
name|this
operator|.
name|backgroundIsSuperset
operator|=
name|backgroundIsSuperset
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|NXYSignificanceHeuristic
specifier|protected
name|NXYSignificanceHeuristic
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|includeNegatives
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|backgroundIsSuperset
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
name|out
operator|.
name|writeBoolean
argument_list|(
name|includeNegatives
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|backgroundIsSuperset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NXYSignificanceHeuristic
operator|)
name|other
operator|)
operator|.
name|includeNegatives
operator|==
name|includeNegatives
operator|&&
operator|(
operator|(
name|NXYSignificanceHeuristic
operator|)
name|other
operator|)
operator|.
name|backgroundIsSuperset
operator|==
name|backgroundIsSuperset
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
operator|(
name|includeNegatives
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|backgroundIsSuperset
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|Frequencies
specifier|protected
specifier|static
class|class
name|Frequencies
block|{
DECL|field|N00
DECL|field|N01
DECL|field|N10
DECL|field|N11
DECL|field|N0_
DECL|field|N1_
DECL|field|N_0
DECL|field|N_1
DECL|field|N
name|double
name|N00
decl_stmt|,
name|N01
decl_stmt|,
name|N10
decl_stmt|,
name|N11
decl_stmt|,
name|N0_
decl_stmt|,
name|N1_
decl_stmt|,
name|N_0
decl_stmt|,
name|N_1
decl_stmt|,
name|N
decl_stmt|;
block|}
DECL|method|computeNxys
specifier|protected
name|Frequencies
name|computeNxys
parameter_list|(
name|long
name|subsetFreq
parameter_list|,
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetFreq
parameter_list|,
name|long
name|supersetSize
parameter_list|,
name|String
name|scoreFunctionName
parameter_list|)
block|{
name|checkFrequencies
argument_list|(
name|subsetFreq
argument_list|,
name|subsetSize
argument_list|,
name|supersetFreq
argument_list|,
name|supersetSize
argument_list|,
name|scoreFunctionName
argument_list|)
expr_stmt|;
name|Frequencies
name|frequencies
init|=
operator|new
name|Frequencies
argument_list|()
decl_stmt|;
if|if
condition|(
name|backgroundIsSuperset
condition|)
block|{
comment|//documents not in class and do not contain term
name|frequencies
operator|.
name|N00
operator|=
name|supersetSize
operator|-
name|supersetFreq
operator|-
operator|(
name|subsetSize
operator|-
name|subsetFreq
operator|)
expr_stmt|;
comment|//documents in class and do not contain term
name|frequencies
operator|.
name|N01
operator|=
operator|(
name|subsetSize
operator|-
name|subsetFreq
operator|)
expr_stmt|;
comment|// documents not in class and do contain term
name|frequencies
operator|.
name|N10
operator|=
name|supersetFreq
operator|-
name|subsetFreq
expr_stmt|;
comment|// documents in class and do contain term
name|frequencies
operator|.
name|N11
operator|=
name|subsetFreq
expr_stmt|;
comment|//documents that do not contain term
name|frequencies
operator|.
name|N0_
operator|=
name|supersetSize
operator|-
name|supersetFreq
expr_stmt|;
comment|//documents that contain term
name|frequencies
operator|.
name|N1_
operator|=
name|supersetFreq
expr_stmt|;
comment|//documents that are not in class
name|frequencies
operator|.
name|N_0
operator|=
name|supersetSize
operator|-
name|subsetSize
expr_stmt|;
comment|//documents that are in class
name|frequencies
operator|.
name|N_1
operator|=
name|subsetSize
expr_stmt|;
comment|//all docs
name|frequencies
operator|.
name|N
operator|=
name|supersetSize
expr_stmt|;
block|}
else|else
block|{
comment|//documents not in class and do not contain term
name|frequencies
operator|.
name|N00
operator|=
name|supersetSize
operator|-
name|supersetFreq
expr_stmt|;
comment|//documents in class and do not contain term
name|frequencies
operator|.
name|N01
operator|=
name|subsetSize
operator|-
name|subsetFreq
expr_stmt|;
comment|// documents not in class and do contain term
name|frequencies
operator|.
name|N10
operator|=
name|supersetFreq
expr_stmt|;
comment|// documents in class and do contain term
name|frequencies
operator|.
name|N11
operator|=
name|subsetFreq
expr_stmt|;
comment|//documents that do not contain term
name|frequencies
operator|.
name|N0_
operator|=
name|supersetSize
operator|-
name|supersetFreq
operator|+
name|subsetSize
operator|-
name|subsetFreq
expr_stmt|;
comment|//documents that contain term
name|frequencies
operator|.
name|N1_
operator|=
name|supersetFreq
operator|+
name|subsetFreq
expr_stmt|;
comment|//documents that are not in class
name|frequencies
operator|.
name|N_0
operator|=
name|supersetSize
expr_stmt|;
comment|//documents that are in class
name|frequencies
operator|.
name|N_1
operator|=
name|subsetSize
expr_stmt|;
comment|//all docs
name|frequencies
operator|.
name|N
operator|=
name|supersetSize
operator|+
name|subsetSize
expr_stmt|;
block|}
return|return
name|frequencies
return|;
block|}
DECL|method|checkFrequencies
specifier|protected
name|void
name|checkFrequencies
parameter_list|(
name|long
name|subsetFreq
parameter_list|,
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetFreq
parameter_list|,
name|long
name|supersetSize
parameter_list|,
name|String
name|scoreFunctionName
parameter_list|)
block|{
name|checkFrequencyValidity
argument_list|(
name|subsetFreq
argument_list|,
name|subsetSize
argument_list|,
name|supersetFreq
argument_list|,
name|supersetSize
argument_list|,
name|scoreFunctionName
argument_list|)
expr_stmt|;
if|if
condition|(
name|backgroundIsSuperset
condition|)
block|{
if|if
condition|(
name|subsetFreq
operator|>
name|supersetFreq
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"subsetFreq> supersetFreq"
operator|+
name|SCORE_ERROR_MESSAGE
argument_list|)
throw|;
block|}
if|if
condition|(
name|subsetSize
operator|>
name|supersetSize
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"subsetSize> supersetSize"
operator|+
name|SCORE_ERROR_MESSAGE
argument_list|)
throw|;
block|}
if|if
condition|(
name|supersetFreq
operator|-
name|subsetFreq
operator|>
name|supersetSize
operator|-
name|subsetSize
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"supersetFreq - subsetFreq> supersetSize - subsetSize"
operator|+
name|SCORE_ERROR_MESSAGE
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|build
specifier|protected
name|void
name|build
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
name|INCLUDE_NEGATIVES_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|includeNegatives
argument_list|)
operator|.
name|field
argument_list|(
name|BACKGROUND_IS_SUPERSET
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|backgroundIsSuperset
argument_list|)
expr_stmt|;
block|}
DECL|class|NXYParser
specifier|public
specifier|abstract
specifier|static
class|class
name|NXYParser
implements|implements
name|SignificanceHeuristicParser
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
name|SignificanceHeuristic
name|parse
parameter_list|(
name|QueryParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryShardException
block|{
name|XContentParser
name|parser
init|=
name|context
operator|.
name|parser
argument_list|()
decl_stmt|;
name|String
name|givenName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|boolean
name|includeNegatives
init|=
literal|false
decl_stmt|;
name|boolean
name|backgroundIsSuperset
init|=
literal|true
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|token
operator|.
name|equals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|)
condition|)
block|{
if|if
condition|(
name|INCLUDE_NEGATIVES_FIELD
operator|.
name|match
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
condition|)
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|includeNegatives
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|BACKGROUND_IS_SUPERSET
operator|.
name|match
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
condition|)
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|backgroundIsSuperset
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse [{}] significance heuristic. unknown field [{}]"
argument_list|,
name|givenName
argument_list|,
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
throw|;
block|}
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
return|return
name|newHeuristic
argument_list|(
name|includeNegatives
argument_list|,
name|backgroundIsSuperset
argument_list|)
return|;
block|}
DECL|method|newHeuristic
specifier|protected
specifier|abstract
name|SignificanceHeuristic
name|newHeuristic
parameter_list|(
name|boolean
name|includeNegatives
parameter_list|,
name|boolean
name|backgroundIsSuperset
parameter_list|)
function_decl|;
block|}
DECL|class|NXYBuilder
specifier|protected
specifier|abstract
specifier|static
class|class
name|NXYBuilder
implements|implements
name|SignificanceHeuristicBuilder
block|{
DECL|field|includeNegatives
specifier|protected
name|boolean
name|includeNegatives
init|=
literal|true
decl_stmt|;
DECL|field|backgroundIsSuperset
specifier|protected
name|boolean
name|backgroundIsSuperset
init|=
literal|true
decl_stmt|;
DECL|method|NXYBuilder
specifier|public
name|NXYBuilder
parameter_list|(
name|boolean
name|includeNegatives
parameter_list|,
name|boolean
name|backgroundIsSuperset
parameter_list|)
block|{
name|this
operator|.
name|includeNegatives
operator|=
name|includeNegatives
expr_stmt|;
name|this
operator|.
name|backgroundIsSuperset
operator|=
name|backgroundIsSuperset
expr_stmt|;
block|}
DECL|method|build
specifier|protected
name|void
name|build
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
name|INCLUDE_NEGATIVES_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|includeNegatives
argument_list|)
operator|.
name|field
argument_list|(
name|BACKGROUND_IS_SUPERSET
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|backgroundIsSuperset
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

