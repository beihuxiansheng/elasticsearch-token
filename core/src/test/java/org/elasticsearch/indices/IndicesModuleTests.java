begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
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
name|analysis
operator|.
name|hunspell
operator|.
name|Dictionary
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
name|*
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
name|io
operator|.
name|InputStream
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
DECL|class|IndicesModuleTests
specifier|public
class|class
name|IndicesModuleTests
extends|extends
name|ModuleTestCase
block|{
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
DECL|method|testRegisterQueryParser
specifier|public
name|void
name|testRegisterQueryParser
parameter_list|()
block|{
name|IndicesModule
name|module
init|=
operator|new
name|IndicesModule
argument_list|()
decl_stmt|;
name|module
operator|.
name|registerQueryParser
argument_list|(
name|FakeQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertSetMultiBinding
argument_list|(
name|module
argument_list|,
name|QueryParser
operator|.
name|class
argument_list|,
name|FakeQueryParser
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
name|IndicesModule
name|module
init|=
operator|new
name|IndicesModule
argument_list|()
decl_stmt|;
try|try
block|{
name|module
operator|.
name|registerQueryParser
argument_list|(
name|TermQueryParser
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
literal|"Can't register the same [query_parser] more than once for ["
operator|+
name|TermQueryParser
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRegisterHunspellDictionary
specifier|public
name|void
name|testRegisterHunspellDictionary
parameter_list|()
throws|throws
name|Exception
block|{
name|IndicesModule
name|module
init|=
operator|new
name|IndicesModule
argument_list|()
decl_stmt|;
name|InputStream
name|aff
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/indices/analyze/conf_dir/hunspell/en_US/en_US.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dic
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/indices/analyze/conf_dir/hunspell/en_US/en_US.dic"
argument_list|)
decl_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|Dictionary
argument_list|(
name|aff
argument_list|,
name|dic
argument_list|)
decl_stmt|;
name|module
operator|.
name|registerHunspellDictionary
argument_list|(
literal|"foo"
argument_list|,
name|dictionary
argument_list|)
expr_stmt|;
name|assertMapInstanceBinding
argument_list|(
name|module
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Dictionary
operator|.
name|class
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"foo"
argument_list|,
name|dictionary
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegisterHunspellDictionaryDuplicate
specifier|public
name|void
name|testRegisterHunspellDictionaryDuplicate
parameter_list|()
block|{
name|IndicesModule
name|module
init|=
operator|new
name|IndicesModule
argument_list|()
decl_stmt|;
try|try
block|{
name|module
operator|.
name|registerQueryParser
argument_list|(
name|TermQueryParser
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
literal|"Can't register the same [query_parser] more than once for ["
operator|+
name|TermQueryParser
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

