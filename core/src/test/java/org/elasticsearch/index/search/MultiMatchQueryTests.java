begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
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
name|*
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
name|compress
operator|.
name|CompressedXContent
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
name|Index
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
name|index
operator|.
name|engine
operator|.
name|Engine
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
name|MapperService
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
name|IndexQueryParserService
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
name|MultiMatchQueryBuilder
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
name|QueryShardContext
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
name|org
operator|.
name|junit
operator|.
name|Before
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|multiMatchQuery
import|;
end_import

begin_class
DECL|class|MultiMatchQueryTests
specifier|public
class|class
name|MultiMatchQueryTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|field|queryParser
specifier|private
name|IndexQueryParserService
name|queryParser
decl_stmt|;
DECL|field|indexService
specifier|private
name|IndexService
name|indexService
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexService
name|indexService
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|MapperService
name|mapperService
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|String
name|mapping
init|=
literal|"{\n"
operator|+
literal|"    \"person\":{\n"
operator|+
literal|"        \"properties\":{\n"
operator|+
literal|"            \"name\":{\n"
operator|+
literal|"                  \"properties\":{\n"
operator|+
literal|"                        \"first\": {\n"
operator|+
literal|"                            \"type\":\"string\"\n"
operator|+
literal|"                        },"
operator|+
literal|"                        \"last\": {\n"
operator|+
literal|"                            \"type\":\"string\"\n"
operator|+
literal|"                        }"
operator|+
literal|"                   }"
operator|+
literal|"            }\n"
operator|+
literal|"        }\n"
operator|+
literal|"    }\n"
operator|+
literal|"}"
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
literal|"person"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexService
operator|=
name|indexService
expr_stmt|;
name|queryParser
operator|=
name|indexService
operator|.
name|queryParserService
argument_list|()
expr_stmt|;
block|}
DECL|method|testCrossFieldMultiMatchQuery
specifier|public
name|void
name|testCrossFieldMultiMatchQuery
parameter_list|()
throws|throws
name|IOException
block|{
name|QueryShardContext
name|queryShardContext
init|=
operator|new
name|QueryShardContext
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|queryParser
argument_list|)
decl_stmt|;
name|queryShardContext
operator|.
name|setAllowUnmappedFields
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Query
name|parsedQuery
init|=
name|multiMatchQuery
argument_list|(
literal|"banon"
argument_list|)
operator|.
name|field
argument_list|(
literal|"name.first"
argument_list|,
literal|2
argument_list|)
operator|.
name|field
argument_list|(
literal|"name.last"
argument_list|,
literal|3
argument_list|)
operator|.
name|field
argument_list|(
literal|"foobar"
argument_list|)
operator|.
name|type
argument_list|(
name|MultiMatchQueryBuilder
operator|.
name|Type
operator|.
name|CROSS_FIELDS
argument_list|)
operator|.
name|toQuery
argument_list|(
name|queryShardContext
argument_list|)
decl_stmt|;
try|try
init|(
name|Engine
operator|.
name|Searcher
name|searcher
init|=
name|indexService
operator|.
name|getShard
argument_list|(
literal|0
argument_list|)
operator|.
name|acquireSearcher
argument_list|(
literal|"test"
argument_list|)
init|)
block|{
name|Query
name|rewrittenQuery
init|=
name|searcher
operator|.
name|searcher
argument_list|()
operator|.
name|rewrite
argument_list|(
name|parsedQuery
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|expected
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|expected
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foobar"
argument_list|,
literal|"banon"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|Query
name|tq1
init|=
operator|new
name|BoostQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"name.first"
argument_list|,
literal|"banon"
argument_list|)
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|Query
name|tq2
init|=
operator|new
name|BoostQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"name.last"
argument_list|,
literal|"banon"
argument_list|)
argument_list|)
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|expected
operator|.
name|add
argument_list|(
operator|new
name|DisjunctionMaxQuery
argument_list|(
name|Arrays
operator|.
expr|<
name|Query
operator|>
name|asList
argument_list|(
name|tq1
argument_list|,
name|tq2
argument_list|)
argument_list|,
literal|0f
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|build
argument_list|()
argument_list|,
name|rewrittenQuery
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
