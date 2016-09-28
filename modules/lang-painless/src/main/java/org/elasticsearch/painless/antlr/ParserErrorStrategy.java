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
name|DefaultErrorStrategy
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
name|InputMismatchException
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
name|NoViableAltException
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
name|Parser
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
name|RecognitionException
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
name|elasticsearch
operator|.
name|painless
operator|.
name|Location
import|;
end_import

begin_comment
comment|/**  * An error strategy that will override the default error behavior to fail on the first parser error.  */
end_comment

begin_class
DECL|class|ParserErrorStrategy
specifier|final
class|class
name|ParserErrorStrategy
extends|extends
name|DefaultErrorStrategy
block|{
DECL|field|sourceName
specifier|final
name|String
name|sourceName
decl_stmt|;
DECL|method|ParserErrorStrategy
name|ParserErrorStrategy
parameter_list|(
name|String
name|sourceName
parameter_list|)
block|{
name|this
operator|.
name|sourceName
operator|=
name|sourceName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|recover
specifier|public
name|void
name|recover
parameter_list|(
specifier|final
name|Parser
name|recognizer
parameter_list|,
specifier|final
name|RecognitionException
name|re
parameter_list|)
block|{
specifier|final
name|Token
name|token
init|=
name|re
operator|.
name|getOffendingToken
argument_list|()
decl_stmt|;
name|String
name|message
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
name|message
operator|=
literal|"no parse token found."
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|re
operator|instanceof
name|InputMismatchException
condition|)
block|{
name|message
operator|=
literal|"unexpected token ["
operator|+
name|getTokenErrorDisplay
argument_list|(
name|token
argument_list|)
operator|+
literal|"]"
operator|+
literal|" was expecting one of ["
operator|+
name|re
operator|.
name|getExpectedTokens
argument_list|()
operator|.
name|toString
argument_list|(
name|recognizer
operator|.
name|getVocabulary
argument_list|()
argument_list|)
operator|+
literal|"]."
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|re
operator|instanceof
name|NoViableAltException
condition|)
block|{
if|if
condition|(
name|token
operator|.
name|getType
argument_list|()
operator|==
name|PainlessParser
operator|.
name|EOF
condition|)
block|{
name|message
operator|=
literal|"unexpected end of script."
expr_stmt|;
block|}
else|else
block|{
name|message
operator|=
literal|"invalid sequence of tokens near ["
operator|+
name|getTokenErrorDisplay
argument_list|(
name|token
argument_list|)
operator|+
literal|"]."
expr_stmt|;
block|}
block|}
else|else
block|{
name|message
operator|=
literal|"unexpected token near ["
operator|+
name|getTokenErrorDisplay
argument_list|(
name|token
argument_list|)
operator|+
literal|"]."
expr_stmt|;
block|}
name|Location
name|location
init|=
operator|new
name|Location
argument_list|(
name|sourceName
argument_list|,
name|token
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|token
operator|.
name|getStartIndex
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
name|location
operator|.
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
name|message
argument_list|,
name|re
argument_list|)
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|recoverInline
specifier|public
name|Token
name|recoverInline
parameter_list|(
specifier|final
name|Parser
name|recognizer
parameter_list|)
throws|throws
name|RecognitionException
block|{
specifier|final
name|Token
name|token
init|=
name|recognizer
operator|.
name|getCurrentToken
argument_list|()
decl_stmt|;
specifier|final
name|String
name|message
init|=
literal|"unexpected token ["
operator|+
name|getTokenErrorDisplay
argument_list|(
name|token
argument_list|)
operator|+
literal|"]"
operator|+
literal|" was expecting one of ["
operator|+
name|recognizer
operator|.
name|getExpectedTokens
argument_list|()
operator|.
name|toString
argument_list|(
name|recognizer
operator|.
name|getVocabulary
argument_list|()
argument_list|)
operator|+
literal|"]."
decl_stmt|;
name|Location
name|location
init|=
operator|new
name|Location
argument_list|(
name|sourceName
argument_list|,
name|token
operator|.
name|getStartIndex
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
name|location
operator|.
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
name|message
argument_list|)
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
specifier|final
name|Parser
name|recognizer
parameter_list|)
block|{     }
block|}
end_class

end_unit
