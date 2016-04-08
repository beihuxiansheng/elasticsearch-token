begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.search.function
package|package
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
name|search
operator|.
name|Explanation
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
name|io
operator|.
name|stream
operator|.
name|Writeable
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
name|Locale
import|;
end_import

begin_enum
DECL|enum|CombineFunction
specifier|public
enum|enum
name|CombineFunction
implements|implements
name|Writeable
argument_list|<
name|CombineFunction
argument_list|>
block|{
DECL|enum constant|MULTIPLY
name|MULTIPLY
block|{
annotation|@
name|Override
specifier|public
name|float
name|combine
parameter_list|(
name|double
name|queryScore
parameter_list|,
name|double
name|funcScore
parameter_list|,
name|double
name|maxBoost
parameter_list|)
block|{
return|return
name|toFloat
argument_list|(
name|queryScore
operator|*
name|Math
operator|.
name|min
argument_list|(
name|funcScore
argument_list|,
name|maxBoost
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Explanation
name|queryExpl
parameter_list|,
name|Explanation
name|funcExpl
parameter_list|,
name|float
name|maxBoost
parameter_list|)
block|{
name|Explanation
name|boostExpl
init|=
name|Explanation
operator|.
name|match
argument_list|(
name|maxBoost
argument_list|,
literal|"maxBoost"
argument_list|)
decl_stmt|;
name|Explanation
name|minExpl
init|=
name|Explanation
operator|.
name|match
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|funcExpl
operator|.
name|getValue
argument_list|()
argument_list|,
name|maxBoost
argument_list|)
argument_list|,
literal|"min of:"
argument_list|,
name|funcExpl
argument_list|,
name|boostExpl
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|queryExpl
operator|.
name|getValue
argument_list|()
operator|*
name|minExpl
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"function score, product of:"
argument_list|,
name|queryExpl
argument_list|,
name|minExpl
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|REPLACE
name|REPLACE
block|{
annotation|@
name|Override
specifier|public
name|float
name|combine
parameter_list|(
name|double
name|queryScore
parameter_list|,
name|double
name|funcScore
parameter_list|,
name|double
name|maxBoost
parameter_list|)
block|{
return|return
name|toFloat
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|funcScore
argument_list|,
name|maxBoost
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Explanation
name|queryExpl
parameter_list|,
name|Explanation
name|funcExpl
parameter_list|,
name|float
name|maxBoost
parameter_list|)
block|{
name|Explanation
name|boostExpl
init|=
name|Explanation
operator|.
name|match
argument_list|(
name|maxBoost
argument_list|,
literal|"maxBoost"
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|funcExpl
operator|.
name|getValue
argument_list|()
argument_list|,
name|maxBoost
argument_list|)
argument_list|,
literal|"min of:"
argument_list|,
name|funcExpl
argument_list|,
name|boostExpl
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|SUM
name|SUM
block|{
annotation|@
name|Override
specifier|public
name|float
name|combine
parameter_list|(
name|double
name|queryScore
parameter_list|,
name|double
name|funcScore
parameter_list|,
name|double
name|maxBoost
parameter_list|)
block|{
return|return
name|toFloat
argument_list|(
name|queryScore
operator|+
name|Math
operator|.
name|min
argument_list|(
name|funcScore
argument_list|,
name|maxBoost
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Explanation
name|queryExpl
parameter_list|,
name|Explanation
name|funcExpl
parameter_list|,
name|float
name|maxBoost
parameter_list|)
block|{
name|Explanation
name|minExpl
init|=
name|Explanation
operator|.
name|match
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|funcExpl
operator|.
name|getValue
argument_list|()
argument_list|,
name|maxBoost
argument_list|)
argument_list|,
literal|"min of:"
argument_list|,
name|funcExpl
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|maxBoost
argument_list|,
literal|"maxBoost"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|funcExpl
operator|.
name|getValue
argument_list|()
argument_list|,
name|maxBoost
argument_list|)
operator|+
name|queryExpl
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"sum of"
argument_list|,
name|queryExpl
argument_list|,
name|minExpl
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|AVG
name|AVG
block|{
annotation|@
name|Override
specifier|public
name|float
name|combine
parameter_list|(
name|double
name|queryScore
parameter_list|,
name|double
name|funcScore
parameter_list|,
name|double
name|maxBoost
parameter_list|)
block|{
return|return
name|toFloat
argument_list|(
operator|(
name|Math
operator|.
name|min
argument_list|(
name|funcScore
argument_list|,
name|maxBoost
argument_list|)
operator|+
name|queryScore
operator|)
operator|/
literal|2.0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Explanation
name|queryExpl
parameter_list|,
name|Explanation
name|funcExpl
parameter_list|,
name|float
name|maxBoost
parameter_list|)
block|{
name|Explanation
name|minExpl
init|=
name|Explanation
operator|.
name|match
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|funcExpl
operator|.
name|getValue
argument_list|()
argument_list|,
name|maxBoost
argument_list|)
argument_list|,
literal|"min of:"
argument_list|,
name|funcExpl
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|maxBoost
argument_list|,
literal|"maxBoost"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|toFloat
argument_list|(
operator|(
name|Math
operator|.
name|min
argument_list|(
name|funcExpl
operator|.
name|getValue
argument_list|()
argument_list|,
name|maxBoost
argument_list|)
operator|+
name|queryExpl
operator|.
name|getValue
argument_list|()
operator|)
operator|/
literal|2.0
argument_list|)
argument_list|,
literal|"avg of"
argument_list|,
name|queryExpl
argument_list|,
name|minExpl
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|MIN
name|MIN
block|{
annotation|@
name|Override
specifier|public
name|float
name|combine
parameter_list|(
name|double
name|queryScore
parameter_list|,
name|double
name|funcScore
parameter_list|,
name|double
name|maxBoost
parameter_list|)
block|{
return|return
name|toFloat
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|queryScore
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|funcScore
argument_list|,
name|maxBoost
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Explanation
name|queryExpl
parameter_list|,
name|Explanation
name|funcExpl
parameter_list|,
name|float
name|maxBoost
parameter_list|)
block|{
name|Explanation
name|innerMinExpl
init|=
name|Explanation
operator|.
name|match
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|funcExpl
operator|.
name|getValue
argument_list|()
argument_list|,
name|maxBoost
argument_list|)
argument_list|,
literal|"min of:"
argument_list|,
name|funcExpl
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|maxBoost
argument_list|,
literal|"maxBoost"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|funcExpl
operator|.
name|getValue
argument_list|()
argument_list|,
name|maxBoost
argument_list|)
argument_list|,
name|queryExpl
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
literal|"min of"
argument_list|,
name|queryExpl
argument_list|,
name|innerMinExpl
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|MAX
name|MAX
block|{
annotation|@
name|Override
specifier|public
name|float
name|combine
parameter_list|(
name|double
name|queryScore
parameter_list|,
name|double
name|funcScore
parameter_list|,
name|double
name|maxBoost
parameter_list|)
block|{
return|return
name|toFloat
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|queryScore
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|funcScore
argument_list|,
name|maxBoost
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Explanation
name|queryExpl
parameter_list|,
name|Explanation
name|funcExpl
parameter_list|,
name|float
name|maxBoost
parameter_list|)
block|{
name|Explanation
name|innerMinExpl
init|=
name|Explanation
operator|.
name|match
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|funcExpl
operator|.
name|getValue
argument_list|()
argument_list|,
name|maxBoost
argument_list|)
argument_list|,
literal|"min of:"
argument_list|,
name|funcExpl
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|maxBoost
argument_list|,
literal|"maxBoost"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|funcExpl
operator|.
name|getValue
argument_list|()
argument_list|,
name|maxBoost
argument_list|)
argument_list|,
name|queryExpl
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
literal|"max of:"
argument_list|,
name|queryExpl
argument_list|,
name|innerMinExpl
argument_list|)
return|;
block|}
block|}
block|;
DECL|method|combine
specifier|public
specifier|abstract
name|float
name|combine
parameter_list|(
name|double
name|queryScore
parameter_list|,
name|double
name|funcScore
parameter_list|,
name|double
name|maxBoost
parameter_list|)
function_decl|;
DECL|method|toFloat
specifier|public
specifier|static
name|float
name|toFloat
parameter_list|(
name|double
name|input
parameter_list|)
block|{
assert|assert
name|deviation
argument_list|(
name|input
argument_list|)
operator|<=
literal|0.001
operator|:
literal|"input "
operator|+
name|input
operator|+
literal|" out of float scope for function score deviation: "
operator|+
name|deviation
argument_list|(
name|input
argument_list|)
assert|;
return|return
operator|(
name|float
operator|)
name|input
return|;
block|}
DECL|method|deviation
specifier|private
specifier|static
name|double
name|deviation
parameter_list|(
name|double
name|input
parameter_list|)
block|{
comment|// only with assert!
name|float
name|floatVersion
init|=
operator|(
name|float
operator|)
name|input
decl_stmt|;
return|return
name|Double
operator|.
name|compare
argument_list|(
name|floatVersion
argument_list|,
name|input
argument_list|)
operator|==
literal|0
operator|||
name|input
operator|==
literal|0.0d
condition|?
literal|0
else|:
literal|1.d
operator|-
operator|(
name|floatVersion
operator|)
operator|/
name|input
return|;
block|}
DECL|method|explain
specifier|public
specifier|abstract
name|Explanation
name|explain
parameter_list|(
name|Explanation
name|queryExpl
parameter_list|,
name|Explanation
name|funcExpl
parameter_list|,
name|float
name|maxBoost
parameter_list|)
function_decl|;
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
name|writeVInt
argument_list|(
name|this
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|readFromStream
specifier|public
specifier|static
name|CombineFunction
name|readFromStream
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ordinal
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|ordinal
operator|<
literal|0
operator|||
name|ordinal
operator|>=
name|values
argument_list|()
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown CombineFunction ordinal ["
operator|+
name|ordinal
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|values
argument_list|()
index|[
name|ordinal
index|]
return|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|CombineFunction
name|fromString
parameter_list|(
name|String
name|combineFunction
parameter_list|)
block|{
return|return
name|valueOf
argument_list|(
name|combineFunction
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

