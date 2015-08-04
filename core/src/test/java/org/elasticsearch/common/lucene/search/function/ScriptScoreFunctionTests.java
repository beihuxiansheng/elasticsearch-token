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
name|index
operator|.
name|LeafReaderContext
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
name|AbstractFloatSearchScript
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
name|LeafSearchScript
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
name|ScriptException
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
name|SearchScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|ScriptScoreFunctionTests
specifier|public
class|class
name|ScriptScoreFunctionTests
extends|extends
name|ESTestCase
block|{
comment|/**      * Tests https://github.com/elasticsearch/elasticsearch/issues/2426      */
annotation|@
name|Test
DECL|method|testScriptScoresReturnsNaN
specifier|public
name|void
name|testScriptScoresReturnsNaN
parameter_list|()
throws|throws
name|IOException
block|{
name|ScoreFunction
name|scoreFunction
init|=
operator|new
name|ScriptScoreFunction
argument_list|(
operator|new
name|Script
argument_list|(
literal|"Float.NaN"
argument_list|)
argument_list|,
operator|new
name|FloatValueScript
argument_list|(
name|Float
operator|.
name|NaN
argument_list|)
argument_list|)
decl_stmt|;
name|LeafScoreFunction
name|leafScoreFunction
init|=
name|scoreFunction
operator|.
name|getLeafScoreFunction
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|leafScoreFunction
operator|.
name|score
argument_list|(
name|randomInt
argument_list|()
argument_list|,
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown an exception about the script_score returning NaN"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ScriptException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"message contains error about script_score returning NaN: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NaN"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FloatValueScript
specifier|static
class|class
name|FloatValueScript
implements|implements
name|SearchScript
block|{
DECL|field|value
specifier|private
specifier|final
name|float
name|value
decl_stmt|;
DECL|method|FloatValueScript
name|FloatValueScript
parameter_list|(
name|float
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafSearchScript
specifier|public
name|LeafSearchScript
name|getLeafSearchScript
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AbstractFloatSearchScript
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|runAsFloat
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
comment|// nothing here
block|}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

