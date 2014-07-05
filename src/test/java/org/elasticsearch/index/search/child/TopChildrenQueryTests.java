begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.child
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|child
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
name|QueryUtils
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
name|util
operator|.
name|IOUtils
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
name|lease
operator|.
name|Releasable
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
name|lease
operator|.
name|Releasables
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
name|fielddata
operator|.
name|plain
operator|.
name|ParentChildIndexFieldData
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
name|internal
operator|.
name|ParentFieldMapper
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
name|search
operator|.
name|nested
operator|.
name|NonNestedDocsFilter
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
name|internal
operator|.
name|SearchContext
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
name|ElasticsearchLuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|child
operator|.
name|ChildrenConstantScoreQueryTests
operator|.
name|createSearchContext
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TopChildrenQueryTests
specifier|public
class|class
name|TopChildrenQueryTests
extends|extends
name|ElasticsearchLuceneTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|before
specifier|public
specifier|static
name|void
name|before
parameter_list|()
throws|throws
name|IOException
block|{
name|forceDefaultCodec
argument_list|()
expr_stmt|;
name|SearchContext
operator|.
name|setCurrent
argument_list|(
name|createSearchContext
argument_list|(
literal|"test"
argument_list|,
literal|"parent"
argument_list|,
literal|"child"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|after
specifier|public
specifier|static
name|void
name|after
parameter_list|()
throws|throws
name|IOException
block|{
name|SearchContext
name|current
init|=
name|SearchContext
operator|.
name|current
argument_list|()
decl_stmt|;
name|SearchContext
operator|.
name|removeCurrent
argument_list|()
expr_stmt|;
name|Releasables
operator|.
name|close
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasicQuerySanities
specifier|public
name|void
name|testBasicQuerySanities
parameter_list|()
block|{
name|Query
name|childQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
decl_stmt|;
name|ScoreType
name|scoreType
init|=
name|ScoreType
operator|.
name|values
argument_list|()
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|ScoreType
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
name|ParentFieldMapper
name|parentFieldMapper
init|=
name|SearchContext
operator|.
name|current
argument_list|()
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
literal|"child"
argument_list|)
operator|.
name|parentFieldMapper
argument_list|()
decl_stmt|;
name|ParentChildIndexFieldData
name|parentChildIndexFieldData
init|=
name|SearchContext
operator|.
name|current
argument_list|()
operator|.
name|fieldData
argument_list|()
operator|.
name|getForField
argument_list|(
name|parentFieldMapper
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TopChildrenQuery
argument_list|(
name|parentChildIndexFieldData
argument_list|,
name|childQuery
argument_list|,
literal|"child"
argument_list|,
literal|"parent"
argument_list|,
name|scoreType
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|SearchContext
operator|.
name|current
argument_list|()
operator|.
name|cacheRecycler
argument_list|()
argument_list|,
name|NonNestedDocsFilter
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

