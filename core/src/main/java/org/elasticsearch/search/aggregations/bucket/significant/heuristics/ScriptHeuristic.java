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
name|logging
operator|.
name|ESLoggerFactory
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
name|QueryShardException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ExecutableScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
operator|.
name|ScriptField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptContext
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
name|InternalAggregation
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
name|support
operator|.
name|XContentParseContext
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
name|internal
operator|.
name|SearchContext
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_class
DECL|class|ScriptHeuristic
specifier|public
class|class
name|ScriptHeuristic
extends|extends
name|SignificanceHeuristic
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"script_heuristic"
decl_stmt|;
DECL|field|subsetSizeHolder
specifier|private
specifier|final
name|LongAccessor
name|subsetSizeHolder
decl_stmt|;
DECL|field|supersetSizeHolder
specifier|private
specifier|final
name|LongAccessor
name|supersetSizeHolder
decl_stmt|;
DECL|field|subsetDfHolder
specifier|private
specifier|final
name|LongAccessor
name|subsetDfHolder
decl_stmt|;
DECL|field|supersetDfHolder
specifier|private
specifier|final
name|LongAccessor
name|supersetDfHolder
decl_stmt|;
DECL|field|script
specifier|private
specifier|final
name|Script
name|script
decl_stmt|;
DECL|field|executableScript
name|ExecutableScript
name|executableScript
init|=
literal|null
decl_stmt|;
DECL|method|ScriptHeuristic
specifier|public
name|ScriptHeuristic
parameter_list|(
name|Script
name|script
parameter_list|)
block|{
name|subsetSizeHolder
operator|=
operator|new
name|LongAccessor
argument_list|()
expr_stmt|;
name|supersetSizeHolder
operator|=
operator|new
name|LongAccessor
argument_list|()
expr_stmt|;
name|subsetDfHolder
operator|=
operator|new
name|LongAccessor
argument_list|()
expr_stmt|;
name|supersetDfHolder
operator|=
operator|new
name|LongAccessor
argument_list|()
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|ScriptHeuristic
specifier|public
name|ScriptHeuristic
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|Script
argument_list|(
name|in
argument_list|)
argument_list|)
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
name|script
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|(
name|InternalAggregation
operator|.
name|ReduceContext
name|context
parameter_list|)
block|{
name|initialize
argument_list|(
name|context
operator|.
name|scriptService
argument_list|()
operator|.
name|executable
argument_list|(
name|script
argument_list|,
name|ScriptContext
operator|.
name|Standard
operator|.
name|AGGS
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
name|initialize
argument_list|(
name|context
operator|.
name|getQueryShardContext
argument_list|()
operator|.
name|getExecutableScript
argument_list|(
name|script
argument_list|,
name|ScriptContext
operator|.
name|Standard
operator|.
name|AGGS
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|(
name|ExecutableScript
name|executableScript
parameter_list|)
block|{
name|this
operator|.
name|executableScript
operator|=
name|executableScript
expr_stmt|;
name|this
operator|.
name|executableScript
operator|.
name|setNextVar
argument_list|(
literal|"_subset_freq"
argument_list|,
name|subsetDfHolder
argument_list|)
expr_stmt|;
name|this
operator|.
name|executableScript
operator|.
name|setNextVar
argument_list|(
literal|"_subset_size"
argument_list|,
name|subsetSizeHolder
argument_list|)
expr_stmt|;
name|this
operator|.
name|executableScript
operator|.
name|setNextVar
argument_list|(
literal|"_superset_freq"
argument_list|,
name|supersetDfHolder
argument_list|)
expr_stmt|;
name|this
operator|.
name|executableScript
operator|.
name|setNextVar
argument_list|(
literal|"_superset_size"
argument_list|,
name|supersetSizeHolder
argument_list|)
expr_stmt|;
block|}
comment|/**      * Calculates score with a script      *      * @param subsetFreq   The frequency of the term in the selected sample      * @param subsetSize   The size of the selected sample (typically number of docs)      * @param supersetFreq The frequency of the term in the superset from which the sample was taken      * @param supersetSize The size of the superset from which the sample was taken  (typically number of docs)      * @return a "significance" score      */
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
if|if
condition|(
name|executableScript
operator|==
literal|null
condition|)
block|{
comment|//In tests, wehn calling assertSearchResponse(..) the response is streamed one additional time with an arbitrary version, see assertVersionSerializable(..).
comment|// Now, for version before 1.5.0 the score is computed after streaming the response but for scripts the script does not exists yet.
comment|// assertSearchResponse() might therefore fail although there is no problem.
comment|// This should be replaced by an exception in 2.0.
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"script heuristic"
argument_list|)
operator|.
name|warn
argument_list|(
literal|"cannot compute score - script has not been initialized yet."
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
name|subsetSizeHolder
operator|.
name|value
operator|=
name|subsetSize
expr_stmt|;
name|supersetSizeHolder
operator|.
name|value
operator|=
name|supersetSize
expr_stmt|;
name|subsetDfHolder
operator|.
name|value
operator|=
name|subsetFreq
expr_stmt|;
name|supersetDfHolder
operator|.
name|value
operator|=
name|supersetFreq
expr_stmt|;
return|return
operator|(
operator|(
name|Number
operator|)
name|executableScript
operator|.
name|run
argument_list|()
operator|)
operator|.
name|doubleValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
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
name|builderParams
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|ScriptField
operator|.
name|SCRIPT
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|script
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|builderParams
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
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
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|script
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ScriptHeuristic
name|other
init|=
operator|(
name|ScriptHeuristic
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|script
argument_list|,
name|other
operator|.
name|script
argument_list|)
return|;
block|}
DECL|method|parse
specifier|public
specifier|static
name|SignificanceHeuristic
name|parse
parameter_list|(
name|XContentParseContext
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
name|getParser
argument_list|()
decl_stmt|;
name|String
name|heuristicName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|Script
name|script
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|.
name|equals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|)
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|context
operator|.
name|matchField
argument_list|(
name|currentFieldName
argument_list|,
name|ScriptField
operator|.
name|SCRIPT
argument_list|)
condition|)
block|{
name|script
operator|=
name|Script
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
argument_list|,
name|context
operator|.
name|getDefaultScriptLanguage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse [{}] significance heuristic. unknown object [{}]"
argument_list|,
name|heuristicName
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|script
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse [{}] significance heuristic. no script found in script_heuristic"
argument_list|,
name|heuristicName
argument_list|)
throw|;
block|}
return|return
operator|new
name|ScriptHeuristic
argument_list|(
name|script
argument_list|)
return|;
block|}
DECL|class|LongAccessor
specifier|public
specifier|final
class|class
name|LongAccessor
extends|extends
name|Number
block|{
DECL|field|value
specifier|public
name|long
name|value
decl_stmt|;
annotation|@
name|Override
DECL|method|intValue
specifier|public
name|int
name|intValue
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|longValue
specifier|public
name|long
name|longValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|floatValue
specifier|public
name|float
name|floatValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|doubleValue
specifier|public
name|double
name|doubleValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

