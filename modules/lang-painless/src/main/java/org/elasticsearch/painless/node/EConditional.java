begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|node
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
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
name|Globals
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
name|Definition
operator|.
name|Type
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|AnalyzerCaster
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
name|Locals
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|Label
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
name|MethodWriter
import|;
end_import

begin_comment
comment|/**  * Respresents a conditional expression.  */
end_comment

begin_class
DECL|class|EConditional
specifier|public
specifier|final
class|class
name|EConditional
extends|extends
name|AExpression
block|{
DECL|field|condition
name|AExpression
name|condition
decl_stmt|;
DECL|field|left
name|AExpression
name|left
decl_stmt|;
DECL|field|right
name|AExpression
name|right
decl_stmt|;
DECL|method|EConditional
specifier|public
name|EConditional
parameter_list|(
name|Location
name|location
parameter_list|,
name|AExpression
name|condition
parameter_list|,
name|AExpression
name|left
parameter_list|,
name|AExpression
name|right
parameter_list|)
block|{
name|super
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|condition
operator|=
name|condition
expr_stmt|;
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|analyze
name|void
name|analyze
parameter_list|(
name|Locals
name|locals
parameter_list|)
block|{
name|condition
operator|.
name|expected
operator|=
name|Definition
operator|.
name|BOOLEAN_TYPE
expr_stmt|;
name|condition
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
name|condition
operator|=
name|condition
operator|.
name|cast
argument_list|(
name|locals
argument_list|)
expr_stmt|;
if|if
condition|(
name|condition
operator|.
name|constant
operator|!=
literal|null
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Extraneous conditional statement."
argument_list|)
argument_list|)
throw|;
block|}
name|left
operator|.
name|expected
operator|=
name|expected
expr_stmt|;
name|left
operator|.
name|explicit
operator|=
name|explicit
expr_stmt|;
name|left
operator|.
name|internal
operator|=
name|internal
expr_stmt|;
name|right
operator|.
name|expected
operator|=
name|expected
expr_stmt|;
name|right
operator|.
name|explicit
operator|=
name|explicit
expr_stmt|;
name|right
operator|.
name|internal
operator|=
name|internal
expr_stmt|;
name|actual
operator|=
name|expected
expr_stmt|;
name|left
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
name|right
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
if|if
condition|(
name|expected
operator|==
literal|null
condition|)
block|{
specifier|final
name|Type
name|promote
init|=
name|AnalyzerCaster
operator|.
name|promoteConditional
argument_list|(
name|left
operator|.
name|actual
argument_list|,
name|right
operator|.
name|actual
argument_list|,
name|left
operator|.
name|constant
argument_list|,
name|right
operator|.
name|constant
argument_list|)
decl_stmt|;
name|left
operator|.
name|expected
operator|=
name|promote
expr_stmt|;
name|right
operator|.
name|expected
operator|=
name|promote
expr_stmt|;
name|actual
operator|=
name|promote
expr_stmt|;
block|}
name|left
operator|=
name|left
operator|.
name|cast
argument_list|(
name|locals
argument_list|)
expr_stmt|;
name|right
operator|=
name|right
operator|.
name|cast
argument_list|(
name|locals
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
name|void
name|write
parameter_list|(
name|MethodWriter
name|writer
parameter_list|,
name|Globals
name|globals
parameter_list|)
block|{
name|writer
operator|.
name|writeDebugInfo
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|Label
name|localfals
init|=
operator|new
name|Label
argument_list|()
decl_stmt|;
name|Label
name|end
init|=
operator|new
name|Label
argument_list|()
decl_stmt|;
name|condition
operator|.
name|fals
operator|=
name|localfals
expr_stmt|;
name|left
operator|.
name|tru
operator|=
name|right
operator|.
name|tru
operator|=
name|tru
expr_stmt|;
name|left
operator|.
name|fals
operator|=
name|right
operator|.
name|fals
operator|=
name|fals
expr_stmt|;
name|condition
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|globals
argument_list|)
expr_stmt|;
name|left
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|globals
argument_list|)
expr_stmt|;
name|writer
operator|.
name|goTo
argument_list|(
name|end
argument_list|)
expr_stmt|;
name|writer
operator|.
name|mark
argument_list|(
name|localfals
argument_list|)
expr_stmt|;
name|right
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|globals
argument_list|)
expr_stmt|;
name|writer
operator|.
name|mark
argument_list|(
name|end
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

