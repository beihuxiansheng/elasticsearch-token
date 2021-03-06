begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|MockSynonymAnalyzer
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
name|standard
operator|.
name|StandardAnalyzer
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
name|Term
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
name|search
operator|.
name|BooleanClause
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|PrefixQuery
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|SynonymQuery
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
name|search
operator|.
name|TermQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanNearQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanOrQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanTermQuery
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
name|IndexSettings
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
name|mapper
operator|.
name|MappedFieldType
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
name|mapper
operator|.
name|MockFieldMapper
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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
DECL|class|SimpleQueryParserTests
specifier|public
class|class
name|SimpleQueryParserTests
extends|extends
name|ESTestCase
block|{
DECL|class|MockSimpleQueryParser
specifier|private
specifier|static
class|class
name|MockSimpleQueryParser
extends|extends
name|SimpleQueryParser
block|{
DECL|method|MockSimpleQueryParser
name|MockSimpleQueryParser
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|weights
parameter_list|,
name|int
name|flags
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|analyzer
argument_list|,
name|weights
argument_list|,
name|flags
argument_list|,
name|settings
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newTermQuery
specifier|protected
name|Query
name|newTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
return|return
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
return|;
block|}
block|}
DECL|method|testAnalyzeWildcard
specifier|public
name|void
name|testAnalyzeWildcard
parameter_list|()
block|{
name|SimpleQueryParser
operator|.
name|Settings
name|settings
init|=
operator|new
name|SimpleQueryParser
operator|.
name|Settings
argument_list|()
decl_stmt|;
name|settings
operator|.
name|analyzeWildcard
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|weights
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|weights
operator|.
name|put
argument_list|(
literal|"field1"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|SimpleQueryParser
name|parser
init|=
operator|new
name|MockSimpleQueryParser
argument_list|(
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
name|weights
argument_list|,
operator|-
literal|1
argument_list|,
name|settings
argument_list|)
decl_stmt|;
for|for
control|(
name|Operator
name|op
range|:
name|Operator
operator|.
name|values
argument_list|()
control|)
block|{
name|BooleanClause
operator|.
name|Occur
name|defaultOp
init|=
name|op
operator|.
name|toBooleanClauseOccur
argument_list|()
decl_stmt|;
name|parser
operator|.
name|setDefaultOperator
argument_list|(
name|defaultOp
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"first foo-bar-foobar* last"
argument_list|)
decl_stmt|;
name|Query
name|expectedQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"first"
argument_list|)
argument_list|)
argument_list|,
name|defaultOp
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
name|defaultOp
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|defaultOp
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
argument_list|,
name|defaultOp
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|defaultOp
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"last"
argument_list|)
argument_list|)
argument_list|,
name|defaultOp
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|equalTo
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAnalyzerWildcardWithSynonyms
specifier|public
name|void
name|testAnalyzerWildcardWithSynonyms
parameter_list|()
block|{
name|SimpleQueryParser
operator|.
name|Settings
name|settings
init|=
operator|new
name|SimpleQueryParser
operator|.
name|Settings
argument_list|()
decl_stmt|;
name|settings
operator|.
name|analyzeWildcard
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|weights
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|weights
operator|.
name|put
argument_list|(
literal|"field1"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|SimpleQueryParser
name|parser
init|=
operator|new
name|MockSimpleQueryParser
argument_list|(
operator|new
name|MockRepeatAnalyzer
argument_list|()
argument_list|,
name|weights
argument_list|,
operator|-
literal|1
argument_list|,
name|settings
argument_list|)
decl_stmt|;
for|for
control|(
name|Operator
name|op
range|:
name|Operator
operator|.
name|values
argument_list|()
control|)
block|{
name|BooleanClause
operator|.
name|Occur
name|defaultOp
init|=
name|op
operator|.
name|toBooleanClauseOccur
argument_list|()
decl_stmt|;
name|parser
operator|.
name|setDefaultOperator
argument_list|(
name|defaultOp
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"first foo-bar-foobar* last"
argument_list|)
decl_stmt|;
name|Query
name|expectedQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|SynonymQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"first"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"first"
argument_list|)
argument_list|)
argument_list|,
name|defaultOp
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|SynonymQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
name|defaultOp
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|SynonymQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|defaultOp
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|defaultOp
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|defaultOp
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|SynonymQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"last"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"last"
argument_list|)
argument_list|)
argument_list|,
name|defaultOp
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|equalTo
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAnalyzerWithGraph
specifier|public
name|void
name|testAnalyzerWithGraph
parameter_list|()
block|{
name|SimpleQueryParser
operator|.
name|Settings
name|settings
init|=
operator|new
name|SimpleQueryParser
operator|.
name|Settings
argument_list|()
decl_stmt|;
name|settings
operator|.
name|analyzeWildcard
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|weights
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|weights
operator|.
name|put
argument_list|(
literal|"field1"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|SimpleQueryParser
name|parser
init|=
operator|new
name|MockSimpleQueryParser
argument_list|(
operator|new
name|MockSynonymAnalyzer
argument_list|()
argument_list|,
name|weights
argument_list|,
operator|-
literal|1
argument_list|,
name|settings
argument_list|)
decl_stmt|;
for|for
control|(
name|Operator
name|op
range|:
name|Operator
operator|.
name|values
argument_list|()
control|)
block|{
name|BooleanClause
operator|.
name|Occur
name|defaultOp
init|=
name|op
operator|.
name|toBooleanClauseOccur
argument_list|()
decl_stmt|;
name|parser
operator|.
name|setDefaultOperator
argument_list|(
name|defaultOp
argument_list|)
expr_stmt|;
comment|// non-phrase won't detect multi-word synonym because of whitespace splitting
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"guinea pig"
argument_list|)
decl_stmt|;
name|Query
name|expectedQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"guinea"
argument_list|)
argument_list|)
argument_list|,
name|defaultOp
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"pig"
argument_list|)
argument_list|)
argument_list|,
name|defaultOp
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|equalTo
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
comment|// phrase will pick it up
name|query
operator|=
name|parser
operator|.
name|parse
argument_list|(
literal|"\"guinea pig\""
argument_list|)
expr_stmt|;
name|SpanTermQuery
name|span1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"guinea"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|span2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"pig"
argument_list|)
argument_list|)
decl_stmt|;
name|expectedQuery
operator|=
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|span1
block|,
name|span2
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
argument_list|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"cavy"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|equalTo
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
comment|// phrase with slop
name|query
operator|=
name|parser
operator|.
name|parse
argument_list|(
literal|"big \"tiny guinea pig\"~2"
argument_list|)
expr_stmt|;
name|expectedQuery
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"big"
argument_list|)
argument_list|)
argument_list|,
name|defaultOp
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"tiny"
argument_list|)
argument_list|)
block|,
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|span1
block|,
name|span2
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
argument_list|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"cavy"
argument_list|)
argument_list|)
argument_list|)
block|}
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
argument_list|,
name|defaultOp
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|equalTo
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testQuoteFieldSuffix
specifier|public
name|void
name|testQuoteFieldSuffix
parameter_list|()
block|{
name|SimpleQueryParser
operator|.
name|Settings
name|sqpSettings
init|=
operator|new
name|SimpleQueryParser
operator|.
name|Settings
argument_list|()
decl_stmt|;
name|sqpSettings
operator|.
name|quoteFieldSuffix
argument_list|(
literal|".quote"
argument_list|)
expr_stmt|;
name|Settings
name|indexSettings
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
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_INDEX_UUID
argument_list|,
literal|"some_uuid"
argument_list|)
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
name|IndexMetaData
name|indexState
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
literal|"index"
argument_list|)
operator|.
name|settings
argument_list|(
name|indexSettings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexSettings
name|settings
init|=
operator|new
name|IndexSettings
argument_list|(
name|indexState
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|QueryShardContext
name|mockShardContext
init|=
operator|new
name|QueryShardContext
argument_list|(
literal|0
argument_list|,
name|settings
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|xContentRegistry
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|System
operator|::
name|currentTimeMillis
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|MappedFieldType
name|fieldMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|MockFieldMapper
operator|.
name|FakeFieldType
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|SimpleQueryParser
name|parser
init|=
operator|new
name|SimpleQueryParser
argument_list|(
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"foo"
argument_list|,
literal|1f
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
name|sqpSettings
argument_list|,
name|mockShardContext
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo.quote"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
literal|"\"bar\""
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now check what happens if foo.quote does not exist
name|mockShardContext
operator|=
operator|new
name|QueryShardContext
argument_list|(
literal|0
argument_list|,
name|settings
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|xContentRegistry
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|System
operator|::
name|currentTimeMillis
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|MappedFieldType
name|fieldMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"foo.quote"
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|MockFieldMapper
operator|.
name|FakeFieldType
argument_list|()
return|;
block|}
block|}
expr_stmt|;
name|parser
operator|=
operator|new
name|SimpleQueryParser
argument_list|(
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"foo"
argument_list|,
literal|1f
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
name|sqpSettings
argument_list|,
name|mockShardContext
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
literal|"\"bar\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

