begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
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
name|Analyzer
import|;
end_import

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
name|TokenStream
import|;
end_import

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
name|Tokenizer
import|;
end_import

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
name|tokenattributes
operator|.
name|CharTermAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

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
name|IndexableField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|analysis
operator|.
name|AnalyzerScope
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
name|analysis
operator|.
name|NamedAnalyzer
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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|DocumentFieldMapperTests
specifier|public
class|class
name|DocumentFieldMapperTests
extends|extends
name|LuceneTestCase
block|{
DECL|class|FakeAnalyzer
specifier|private
specifier|static
class|class
name|FakeAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|output
specifier|private
specifier|final
name|String
name|output
decl_stmt|;
DECL|method|FakeAnalyzer
name|FakeAnalyzer
parameter_list|(
name|String
name|output
parameter_list|)
block|{
name|this
operator|.
name|output
operator|=
name|output
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|Tokenizer
argument_list|()
block|{
name|boolean
name|incremented
init|=
literal|false
decl_stmt|;
name|CharTermAttribute
name|term
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|incremented
condition|)
block|{
return|return
literal|false
return|;
block|}
name|term
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
operator|.
name|append
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|incremented
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|)
return|;
block|}
block|}
DECL|class|FakeFieldType
specifier|static
class|class
name|FakeFieldType
extends|extends
name|TermBasedFieldType
block|{
DECL|method|FakeFieldType
name|FakeFieldType
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|FakeFieldType
name|FakeFieldType
parameter_list|(
name|FakeFieldType
name|other
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|MappedFieldType
name|clone
parameter_list|()
block|{
return|return
operator|new
name|FakeFieldType
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|typeName
specifier|public
name|String
name|typeName
parameter_list|()
block|{
return|return
literal|"fake"
return|;
block|}
block|}
DECL|class|FakeFieldMapper
specifier|static
class|class
name|FakeFieldMapper
extends|extends
name|FieldMapper
block|{
DECL|field|SETTINGS
specifier|private
specifier|static
specifier|final
name|Settings
name|SETTINGS
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|method|FakeFieldMapper
name|FakeFieldMapper
parameter_list|(
name|String
name|simpleName
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|)
block|{
name|super
argument_list|(
name|simpleName
argument_list|,
name|fieldType
operator|.
name|clone
argument_list|()
argument_list|,
name|fieldType
operator|.
name|clone
argument_list|()
argument_list|,
name|SETTINGS
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseCreateField
specifier|protected
name|void
name|parseCreateField
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|List
argument_list|<
name|IndexableField
argument_list|>
name|fields
parameter_list|)
throws|throws
name|IOException
block|{         }
annotation|@
name|Override
DECL|method|contentType
specifier|protected
name|String
name|contentType
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|testAnalyzers
specifier|public
name|void
name|testAnalyzers
parameter_list|()
throws|throws
name|IOException
block|{
name|FakeFieldType
name|fieldType1
init|=
operator|new
name|FakeFieldType
argument_list|()
decl_stmt|;
name|fieldType1
operator|.
name|setName
argument_list|(
literal|"field1"
argument_list|)
expr_stmt|;
name|fieldType1
operator|.
name|setIndexAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"foo"
argument_list|,
name|AnalyzerScope
operator|.
name|INDEX
argument_list|,
operator|new
name|FakeAnalyzer
argument_list|(
literal|"index"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fieldType1
operator|.
name|setSearchAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"bar"
argument_list|,
name|AnalyzerScope
operator|.
name|INDEX
argument_list|,
operator|new
name|FakeAnalyzer
argument_list|(
literal|"search"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fieldType1
operator|.
name|setSearchQuoteAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"baz"
argument_list|,
name|AnalyzerScope
operator|.
name|INDEX
argument_list|,
operator|new
name|FakeAnalyzer
argument_list|(
literal|"search_quote"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|FieldMapper
name|fieldMapper1
init|=
operator|new
name|FakeFieldMapper
argument_list|(
literal|"field1"
argument_list|,
name|fieldType1
argument_list|)
decl_stmt|;
name|FakeFieldType
name|fieldType2
init|=
operator|new
name|FakeFieldType
argument_list|()
decl_stmt|;
name|fieldType2
operator|.
name|setName
argument_list|(
literal|"field2"
argument_list|)
expr_stmt|;
name|FieldMapper
name|fieldMapper2
init|=
operator|new
name|FakeFieldMapper
argument_list|(
literal|"field2"
argument_list|,
name|fieldType2
argument_list|)
decl_stmt|;
name|Analyzer
name|defaultIndex
init|=
operator|new
name|FakeAnalyzer
argument_list|(
literal|"default_index"
argument_list|)
decl_stmt|;
name|Analyzer
name|defaultSearch
init|=
operator|new
name|FakeAnalyzer
argument_list|(
literal|"default_search"
argument_list|)
decl_stmt|;
name|Analyzer
name|defaultSearchQuote
init|=
operator|new
name|FakeAnalyzer
argument_list|(
literal|"default_search_quote"
argument_list|)
decl_stmt|;
name|DocumentFieldMappers
name|documentFieldMappers
init|=
operator|new
name|DocumentFieldMappers
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|fieldMapper1
argument_list|,
name|fieldMapper2
argument_list|)
argument_list|,
name|defaultIndex
argument_list|,
name|defaultSearch
argument_list|,
name|defaultSearchQuote
argument_list|)
decl_stmt|;
name|assertAnalyzes
argument_list|(
name|documentFieldMappers
operator|.
name|indexAnalyzer
argument_list|()
argument_list|,
literal|"field1"
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
name|assertAnalyzes
argument_list|(
name|documentFieldMappers
operator|.
name|searchAnalyzer
argument_list|()
argument_list|,
literal|"field1"
argument_list|,
literal|"search"
argument_list|)
expr_stmt|;
name|assertAnalyzes
argument_list|(
name|documentFieldMappers
operator|.
name|searchQuoteAnalyzer
argument_list|()
argument_list|,
literal|"field1"
argument_list|,
literal|"search_quote"
argument_list|)
expr_stmt|;
name|assertAnalyzes
argument_list|(
name|documentFieldMappers
operator|.
name|indexAnalyzer
argument_list|()
argument_list|,
literal|"field2"
argument_list|,
literal|"default_index"
argument_list|)
expr_stmt|;
name|assertAnalyzes
argument_list|(
name|documentFieldMappers
operator|.
name|searchAnalyzer
argument_list|()
argument_list|,
literal|"field2"
argument_list|,
literal|"default_search"
argument_list|)
expr_stmt|;
name|assertAnalyzes
argument_list|(
name|documentFieldMappers
operator|.
name|searchQuoteAnalyzer
argument_list|()
argument_list|,
literal|"field2"
argument_list|,
literal|"default_search_quote"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzes
specifier|private
name|void
name|assertAnalyzes
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|output
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|TokenStream
name|tok
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
init|)
block|{
name|CharTermAttribute
name|term
init|=
name|tok
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tok
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|output
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

