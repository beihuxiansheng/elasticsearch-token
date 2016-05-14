begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.completion
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
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
name|index
operator|.
name|query
operator|.
name|RegexpFlag
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
DECL|class|RegexOptionsTests
specifier|public
class|class
name|RegexOptionsTests
extends|extends
name|WritableTestCase
argument_list|<
name|RegexOptions
argument_list|>
block|{
DECL|method|randomRegexOptions
specifier|public
specifier|static
name|RegexOptions
name|randomRegexOptions
parameter_list|()
block|{
specifier|final
name|RegexOptions
operator|.
name|Builder
name|builder
init|=
name|RegexOptions
operator|.
name|builder
argument_list|()
decl_stmt|;
name|maybeSet
argument_list|(
name|builder
operator|::
name|setMaxDeterminizedStates
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|RegexpFlag
name|regexpFlag
range|:
name|RegexpFlag
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"|"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|regexpFlag
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|maybeSet
argument_list|(
name|builder
operator|::
name|setFlags
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createTestModel
specifier|protected
name|RegexOptions
name|createTestModel
parameter_list|()
block|{
return|return
name|randomRegexOptions
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createMutation
specifier|protected
name|RegexOptions
name|createMutation
parameter_list|(
name|RegexOptions
name|original
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|RegexOptions
operator|.
name|Builder
name|builder
init|=
name|RegexOptions
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setMaxDeterminizedStates
argument_list|(
name|randomValueOtherThan
argument_list|(
name|original
operator|.
name|getMaxDeterminizedStates
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|protected
name|RegexOptions
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|RegexOptions
argument_list|(
name|in
argument_list|)
return|;
block|}
DECL|method|testIllegalArgument
specifier|public
name|void
name|testIllegalArgument
parameter_list|()
block|{
specifier|final
name|RegexOptions
operator|.
name|Builder
name|builder
init|=
name|RegexOptions
operator|.
name|builder
argument_list|()
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|setMaxDeterminizedStates
argument_list|(
operator|-
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"max determinized state must be positive"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"maxDeterminizedStates must not be negative"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

