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
name|QueryParsingException
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
DECL|class|GND
specifier|public
class|class
name|GND
extends|extends
name|NXYSignificanceHeuristic
block|{
DECL|field|NAMES_FIELD
specifier|protected
specifier|static
specifier|final
name|ParseField
name|NAMES_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"gnd"
argument_list|)
decl_stmt|;
DECL|method|GND
specifier|public
name|GND
parameter_list|(
name|boolean
name|backgroundIsSuperset
parameter_list|)
block|{
name|super
argument_list|(
literal|true
argument_list|,
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
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|GND
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
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
name|NAMES_FIELD
operator|.
name|getPreferredName
argument_list|()
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|super
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|field|STREAM
specifier|public
specifier|static
specifier|final
name|SignificanceHeuristicStreams
operator|.
name|Stream
name|STREAM
init|=
operator|new
name|SignificanceHeuristicStreams
operator|.
name|Stream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SignificanceHeuristic
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|GND
argument_list|(
name|in
operator|.
name|readBoolean
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|NAMES_FIELD
operator|.
name|getPreferredName
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Calculates Google Normalized Distance, as described in "The Google Similarity Distance", Cilibrasi and Vitanyi, 2007      * link: http://arxiv.org/pdf/cs/0412098v3.pdf      */
annotation|@
name|Override
DECL|method|getScore
specifier|public
name|double
name|getScore
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
parameter_list|)
block|{
name|Frequencies
name|frequencies
init|=
name|computeNxys
argument_list|(
name|subsetFreq
argument_list|,
name|subsetSize
argument_list|,
name|supersetFreq
argument_list|,
name|supersetSize
argument_list|,
literal|"GND"
argument_list|)
decl_stmt|;
name|double
name|fx
init|=
name|frequencies
operator|.
name|N1_
decl_stmt|;
name|double
name|fy
init|=
name|frequencies
operator|.
name|N_1
decl_stmt|;
name|double
name|fxy
init|=
name|frequencies
operator|.
name|N11
decl_stmt|;
name|double
name|N
init|=
name|frequencies
operator|.
name|N
decl_stmt|;
if|if
condition|(
name|fxy
operator|==
literal|0
condition|)
block|{
comment|// no co-occurrence
return|return
literal|0.0
return|;
block|}
if|if
condition|(
operator|(
name|fx
operator|==
name|fy
operator|)
operator|&&
operator|(
name|fx
operator|==
name|fxy
operator|)
condition|)
block|{
comment|// perfect co-occurrence
return|return
literal|1.0
return|;
block|}
name|double
name|score
init|=
operator|(
name|Math
operator|.
name|max
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|fx
argument_list|)
argument_list|,
name|Math
operator|.
name|log
argument_list|(
name|fy
argument_list|)
argument_list|)
operator|-
name|Math
operator|.
name|log
argument_list|(
name|fxy
argument_list|)
operator|)
operator|/
operator|(
name|Math
operator|.
name|log
argument_list|(
name|N
argument_list|)
operator|-
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|fx
argument_list|)
argument_list|,
name|Math
operator|.
name|log
argument_list|(
name|fy
argument_list|)
argument_list|)
operator|)
decl_stmt|;
comment|//we must invert the order of terms because GND scores relevant terms low
name|score
operator|=
name|Math
operator|.
name|exp
argument_list|(
operator|-
literal|1.0d
operator|*
name|score
argument_list|)
expr_stmt|;
return|return
name|score
return|;
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
name|writeString
argument_list|(
name|STREAM
operator|.
name|getName
argument_list|()
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
DECL|class|GNDParser
specifier|public
specifier|static
class|class
name|GNDParser
extends|extends
name|NXYParser
block|{
annotation|@
name|Override
DECL|method|getNames
specifier|public
name|String
index|[]
name|getNames
parameter_list|()
block|{
return|return
name|NAMES_FIELD
operator|.
name|getAllNamesIncludedDeprecated
argument_list|()
return|;
block|}
DECL|method|newHeuristic
specifier|protected
name|SignificanceHeuristic
name|newHeuristic
parameter_list|(
name|boolean
name|includeNegatives
parameter_list|,
name|boolean
name|backgroundIsSuperset
parameter_list|)
block|{
return|return
operator|new
name|GND
argument_list|(
name|backgroundIsSuperset
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|SignificanceHeuristic
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|String
name|givenName
init|=
name|parser
operator|.
name|currentName
argument_list|()
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
name|BACKGROUND_IS_SUPERSET
operator|.
name|match
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|,
name|ParseField
operator|.
name|EMPTY_FLAGS
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
literal|"Field "
operator|+
name|parser
operator|.
name|currentName
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" unknown for "
operator|+
name|givenName
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
literal|true
argument_list|,
name|backgroundIsSuperset
argument_list|)
return|;
block|}
block|}
DECL|class|GNDBuilder
specifier|public
specifier|static
class|class
name|GNDBuilder
extends|extends
name|NXYBuilder
block|{
DECL|method|GNDBuilder
specifier|public
name|GNDBuilder
parameter_list|(
name|boolean
name|backgroundIsSuperset
parameter_list|)
block|{
name|super
argument_list|(
literal|true
argument_list|,
name|backgroundIsSuperset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|void
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|STREAM
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
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
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

