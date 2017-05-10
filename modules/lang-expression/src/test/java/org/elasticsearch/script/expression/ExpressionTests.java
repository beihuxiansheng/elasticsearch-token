begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.expression
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|expression
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
name|settings
operator|.
name|Settings
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
name|IndexService
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
name|CompiledScript
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
name|ScriptType
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
name|search
operator|.
name|lookup
operator|.
name|SearchLookup
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
name|ESSingleNodeTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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

begin_class
DECL|class|ExpressionTests
specifier|public
class|class
name|ExpressionTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|field|service
name|ExpressionScriptEngine
name|service
decl_stmt|;
DECL|field|lookup
name|SearchLookup
name|lookup
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|IndexService
name|index
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|"type"
argument_list|,
literal|"d"
argument_list|,
literal|"type=double"
argument_list|)
decl_stmt|;
name|service
operator|=
operator|new
name|ExpressionScriptEngine
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|lookup
operator|=
operator|new
name|SearchLookup
argument_list|(
name|index
operator|.
name|mapperService
argument_list|()
argument_list|,
name|index
operator|.
name|fieldData
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|compile
specifier|private
name|SearchScript
name|compile
parameter_list|(
name|String
name|expression
parameter_list|)
block|{
name|Object
name|compiled
init|=
name|service
operator|.
name|compile
argument_list|(
literal|null
argument_list|,
name|expression
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|service
operator|.
name|search
argument_list|(
operator|new
name|CompiledScript
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"randomName"
argument_list|,
literal|"expression"
argument_list|,
name|compiled
argument_list|)
argument_list|,
name|lookup
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|)
return|;
block|}
DECL|method|testNeedsScores
specifier|public
name|void
name|testNeedsScores
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|compile
argument_list|(
literal|"1.2"
argument_list|)
operator|.
name|needsScores
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|compile
argument_list|(
literal|"doc['d'].value"
argument_list|)
operator|.
name|needsScores
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|compile
argument_list|(
literal|"1/_score"
argument_list|)
operator|.
name|needsScores
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|compile
argument_list|(
literal|"doc['d'].value * _score"
argument_list|)
operator|.
name|needsScores
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCompileError
specifier|public
name|void
name|testCompileError
parameter_list|()
block|{
name|ScriptException
name|e
init|=
name|expectThrows
argument_list|(
name|ScriptException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|compile
argument_list|(
literal|"doc['d'].value * *@#)(@$*@#$ + 4"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|ParseException
argument_list|)
expr_stmt|;
block|}
DECL|method|testLinkError
specifier|public
name|void
name|testLinkError
parameter_list|()
block|{
name|ScriptException
name|e
init|=
name|expectThrows
argument_list|(
name|ScriptException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|compile
argument_list|(
literal|"doc['e'].value * 5"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|ParseException
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

