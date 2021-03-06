begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless.antlr
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|antlr
package|;
end_package

begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|v4
operator|.
name|runtime
operator|.
name|CharStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|v4
operator|.
name|runtime
operator|.
name|Lexer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|v4
operator|.
name|runtime
operator|.
name|Token
import|;
end_import

begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|v4
operator|.
name|runtime
operator|.
name|TokenFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|v4
operator|.
name|runtime
operator|.
name|TokenSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|v4
operator|.
name|runtime
operator|.
name|misc
operator|.
name|Pair
import|;
end_import

begin_comment
comment|/**  * Token factory that preserves that last non-whitespace token so you can do token level lookbehind in the lexer.  */
end_comment

begin_class
DECL|class|StashingTokenFactory
specifier|public
class|class
name|StashingTokenFactory
parameter_list|<
name|T
extends|extends
name|Token
parameter_list|>
implements|implements
name|TokenFactory
argument_list|<
name|T
argument_list|>
block|{
DECL|field|delegate
specifier|private
specifier|final
name|TokenFactory
argument_list|<
name|T
argument_list|>
name|delegate
decl_stmt|;
DECL|field|lastToken
specifier|private
name|T
name|lastToken
decl_stmt|;
DECL|method|StashingTokenFactory
specifier|public
name|StashingTokenFactory
parameter_list|(
name|TokenFactory
argument_list|<
name|T
argument_list|>
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
DECL|method|getLastToken
specifier|public
name|T
name|getLastToken
parameter_list|()
block|{
return|return
name|lastToken
return|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|T
name|create
parameter_list|(
name|Pair
argument_list|<
name|TokenSource
argument_list|,
name|CharStream
argument_list|>
name|source
parameter_list|,
name|int
name|type
parameter_list|,
name|String
name|text
parameter_list|,
name|int
name|channel
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|stop
parameter_list|,
name|int
name|line
parameter_list|,
name|int
name|charPositionInLine
parameter_list|)
block|{
return|return
name|maybeStash
argument_list|(
name|delegate
operator|.
name|create
argument_list|(
name|source
argument_list|,
name|type
argument_list|,
name|text
argument_list|,
name|channel
argument_list|,
name|start
argument_list|,
name|stop
argument_list|,
name|line
argument_list|,
name|charPositionInLine
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|T
name|create
parameter_list|(
name|int
name|type
parameter_list|,
name|String
name|text
parameter_list|)
block|{
return|return
name|maybeStash
argument_list|(
name|delegate
operator|.
name|create
argument_list|(
name|type
argument_list|,
name|text
argument_list|)
argument_list|)
return|;
block|}
DECL|method|maybeStash
specifier|private
name|T
name|maybeStash
parameter_list|(
name|T
name|token
parameter_list|)
block|{
if|if
condition|(
name|token
operator|.
name|getChannel
argument_list|()
operator|==
name|Lexer
operator|.
name|DEFAULT_TOKEN_CHANNEL
condition|)
block|{
name|lastToken
operator|=
name|token
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
block|}
end_class

end_unit

