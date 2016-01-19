begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
package|;
end_package

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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|ModuleTestCase
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
name|NamedWriteableRegistry
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
name|query
operator|.
name|QueryBuilder
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
name|QueryParseContext
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
name|QueryParser
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
name|TermQueryParser
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
name|highlight
operator|.
name|CustomHighlighter
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
name|highlight
operator|.
name|Highlighter
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
name|highlight
operator|.
name|PlainHighlighter
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
name|suggest
operator|.
name|CustomSuggester
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
name|suggest
operator|.
name|Suggester
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
name|suggest
operator|.
name|phrase
operator|.
name|PhraseSuggester
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
name|containsString
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|SearchModuleTests
specifier|public
class|class
name|SearchModuleTests
extends|extends
name|ModuleTestCase
block|{
DECL|method|testDoubleRegister
specifier|public
name|void
name|testDoubleRegister
parameter_list|()
block|{
name|SearchModule
name|module
init|=
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|module
operator|.
name|registerHighlighter
argument_list|(
literal|"fvh"
argument_list|,
name|PlainHighlighter
operator|.
name|class
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
literal|"Can't register the same [highlighter] more than once for [fvh]"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|module
operator|.
name|registerSuggester
argument_list|(
literal|"term"
argument_list|,
name|PhraseSuggester
operator|.
name|class
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
literal|"Can't register the same [suggester] more than once for [term]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRegisterSuggester
specifier|public
name|void
name|testRegisterSuggester
parameter_list|()
block|{
name|SearchModule
name|module
init|=
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|()
argument_list|)
decl_stmt|;
name|module
operator|.
name|registerSuggester
argument_list|(
literal|"custom"
argument_list|,
name|CustomSuggester
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|module
operator|.
name|registerSuggester
argument_list|(
literal|"custom"
argument_list|,
name|CustomSuggester
operator|.
name|class
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
literal|"Can't register the same [suggester] more than once for [custom]"
argument_list|)
expr_stmt|;
block|}
name|assertMapMultiBinding
argument_list|(
name|module
argument_list|,
name|Suggester
operator|.
name|class
argument_list|,
name|CustomSuggester
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegisterHighlighter
specifier|public
name|void
name|testRegisterHighlighter
parameter_list|()
block|{
name|SearchModule
name|module
init|=
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|()
argument_list|)
decl_stmt|;
name|module
operator|.
name|registerHighlighter
argument_list|(
literal|"custom"
argument_list|,
name|CustomHighlighter
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|module
operator|.
name|registerHighlighter
argument_list|(
literal|"custom"
argument_list|,
name|CustomHighlighter
operator|.
name|class
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
literal|"Can't register the same [highlighter] more than once for [custom]"
argument_list|)
expr_stmt|;
block|}
name|assertMapMultiBinding
argument_list|(
name|module
argument_list|,
name|Highlighter
operator|.
name|class
argument_list|,
name|CustomHighlighter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegisterQueryParserDuplicate
specifier|public
name|void
name|testRegisterQueryParserDuplicate
parameter_list|()
block|{
name|SearchModule
name|module
init|=
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|()
argument_list|)
decl_stmt|;
name|module
operator|.
name|registerQueryParser
argument_list|(
name|TermQueryParser
operator|::
operator|new
argument_list|)
expr_stmt|;
try|try
block|{
name|module
operator|.
name|buildQueryParserRegistry
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"already registered for name [term] while trying to register [org.elasticsearch.index."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FakeQueryParser
specifier|static
class|class
name|FakeQueryParser
implements|implements
name|QueryParser
block|{
annotation|@
name|Override
DECL|method|names
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"fake-query-parser"
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|QueryBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getBuilderPrototype
specifier|public
name|QueryBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

