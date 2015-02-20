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
DECL|class|PercentageScore
specifier|public
class|class
name|PercentageScore
extends|extends
name|SignificanceHeuristic
block|{
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|PercentageScore
name|INSTANCE
init|=
operator|new
name|PercentageScore
argument_list|()
decl_stmt|;
DECL|field|NAMES
specifier|protected
specifier|static
specifier|final
name|String
index|[]
name|NAMES
init|=
block|{
literal|"percentage"
block|}
decl_stmt|;
DECL|method|PercentageScore
specifier|private
name|PercentageScore
parameter_list|()
block|{}
empty_stmt|;
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
name|readFrom
argument_list|(
name|in
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
name|NAMES
index|[
literal|0
index|]
return|;
block|}
block|}
decl_stmt|;
DECL|method|readFrom
specifier|public
specifier|static
name|SignificanceHeuristic
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|INSTANCE
return|;
block|}
comment|/**      * Indicates the significance of a term in a sample by determining what percentage      * of all occurrences of a term are found in the sample.       */
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
literal|"PercentageScore"
argument_list|)
expr_stmt|;
if|if
condition|(
name|supersetFreq
operator|==
literal|0
condition|)
block|{
comment|// avoid a divide by zero issue
return|return
literal|0
return|;
block|}
return|return
operator|(
name|double
operator|)
name|subsetFreq
operator|/
operator|(
name|double
operator|)
name|supersetFreq
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
block|}
DECL|class|PercentageScoreParser
specifier|public
specifier|static
class|class
name|PercentageScoreParser
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
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
comment|// move to the closing bracket
if|if
condition|(
operator|!
name|parser
operator|.
name|nextToken
argument_list|()
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
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"expected }, got "
operator|+
name|parser
operator|.
name|currentName
argument_list|()
operator|+
literal|" instead in percentage score"
argument_list|)
throw|;
block|}
return|return
operator|new
name|PercentageScore
argument_list|()
return|;
block|}
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
name|NAMES
return|;
block|}
block|}
DECL|class|PercentageScoreBuilder
specifier|public
specifier|static
class|class
name|PercentageScoreBuilder
implements|implements
name|SignificanceHeuristicBuilder
block|{
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
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
