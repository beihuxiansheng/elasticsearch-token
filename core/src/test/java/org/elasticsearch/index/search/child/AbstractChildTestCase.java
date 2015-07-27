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
name|search
operator|.
name|DocIdSetIterator
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
name|Filter
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
name|IndexSearcher
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
name|ScoreDoc
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
name|TopDocs
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
name|join
operator|.
name|BitDocIdSetFilter
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
name|BitDocIdSet
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
name|BitSet
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
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|put
operator|.
name|PutMappingRequest
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
name|common
operator|.
name|xcontent
operator|.
name|XContentHelper
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
name|xcontent
operator|.
name|XContentParser
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
name|mapper
operator|.
name|internal
operator|.
name|UidFieldMapper
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
name|QueryShardContext
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
name|ESSingleNodeTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|StringDescription
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
DECL|class|AbstractChildTestCase
specifier|public
specifier|abstract
class|class
name|AbstractChildTestCase
extends|extends
name|ESSingleNodeTestCase
block|{
comment|/**      * The name of the field within the child type that stores a score to use in test queries.      *<p />      * Its type is {@code double}.      */
DECL|field|CHILD_SCORE_NAME
specifier|protected
specifier|static
name|String
name|CHILD_SCORE_NAME
init|=
literal|"childScore"
decl_stmt|;
DECL|method|createSearchContext
specifier|static
name|SearchContext
name|createSearchContext
parameter_list|(
name|String
name|indexName
parameter_list|,
name|String
name|parentType
parameter_list|,
name|String
name|childType
parameter_list|)
throws|throws
name|IOException
block|{
name|Settings
name|settings
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
name|V_1_6_0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexService
name|indexService
init|=
name|createIndex
argument_list|(
name|indexName
argument_list|,
name|settings
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
comment|// Parent/child parsers require that the parent and child type to be presented in mapping
comment|// Sometimes we want a nested object field in the parent type that triggers nonNestedDocsFilter to be used
name|mapperService
operator|.
name|merge
argument_list|(
name|parentType
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|PutMappingRequest
operator|.
name|buildFromSimplifiedDef
argument_list|(
name|parentType
argument_list|,
literal|"nested_field"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"type=nested"
else|:
literal|"type=object"
argument_list|)
operator|.
name|string
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
name|childType
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|PutMappingRequest
operator|.
name|buildFromSimplifiedDef
argument_list|(
name|childType
argument_list|,
literal|"_parent"
argument_list|,
literal|"type="
operator|+
name|parentType
argument_list|,
name|CHILD_SCORE_NAME
argument_list|,
literal|"type=double,doc_values=false"
argument_list|)
operator|.
name|string
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|createSearchContext
argument_list|(
name|indexService
argument_list|)
return|;
block|}
DECL|method|assertBitSet
specifier|static
name|void
name|assertBitSet
parameter_list|(
name|BitSet
name|actual
parameter_list|,
name|BitSet
name|expected
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|assertBitSet
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|actual
argument_list|)
argument_list|,
operator|new
name|BitDocIdSet
argument_list|(
name|expected
argument_list|)
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|assertBitSet
specifier|static
name|void
name|assertBitSet
parameter_list|(
name|BitDocIdSet
name|actual
parameter_list|,
name|BitDocIdSet
name|expected
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|equals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
condition|)
block|{
name|Description
name|description
init|=
operator|new
name|StringDescription
argument_list|()
decl_stmt|;
name|description
operator|.
name|appendText
argument_list|(
name|reason
argument_list|(
name|actual
argument_list|,
name|expected
argument_list|,
name|searcher
argument_list|)
argument_list|)
expr_stmt|;
name|description
operator|.
name|appendText
argument_list|(
literal|"\nExpected: "
argument_list|)
expr_stmt|;
name|description
operator|.
name|appendValue
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|description
operator|.
name|appendText
argument_list|(
literal|"\n     got: "
argument_list|)
expr_stmt|;
name|description
operator|.
name|appendValue
argument_list|(
name|actual
argument_list|)
expr_stmt|;
name|description
operator|.
name|appendText
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|java
operator|.
name|lang
operator|.
name|AssertionError
argument_list|(
name|description
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|equals
specifier|static
name|boolean
name|equals
parameter_list|(
name|BitDocIdSet
name|expected
parameter_list|,
name|BitDocIdSet
name|actual
parameter_list|)
block|{
if|if
condition|(
name|actual
operator|==
literal|null
operator|&&
name|expected
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|actual
operator|==
literal|null
operator|||
name|expected
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BitSet
name|actualBits
init|=
name|actual
operator|.
name|bits
argument_list|()
decl_stmt|;
name|BitSet
name|expectedBits
init|=
name|expected
operator|.
name|bits
argument_list|()
decl_stmt|;
if|if
condition|(
name|actualBits
operator|.
name|length
argument_list|()
operator|!=
name|expectedBits
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedBits
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|expectedBits
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|!=
name|actualBits
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|reason
specifier|static
name|String
name|reason
parameter_list|(
name|BitDocIdSet
name|actual
parameter_list|,
name|BitDocIdSet
name|expected
parameter_list|,
name|IndexSearcher
name|indexSearcher
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"expected cardinality:"
argument_list|)
operator|.
name|append
argument_list|(
name|expected
operator|.
name|bits
argument_list|()
operator|.
name|cardinality
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|DocIdSetIterator
name|iterator
init|=
name|expected
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|iterator
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"Expected doc["
argument_list|)
operator|.
name|append
argument_list|(
name|doc
argument_list|)
operator|.
name|append
argument_list|(
literal|"] with id value "
argument_list|)
operator|.
name|append
argument_list|(
name|indexSearcher
operator|.
name|doc
argument_list|(
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"actual cardinality: "
argument_list|)
operator|.
name|append
argument_list|(
name|actual
operator|.
name|bits
argument_list|()
operator|.
name|cardinality
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|actual
operator|.
name|iterator
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|iterator
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"Actual doc["
argument_list|)
operator|.
name|append
argument_list|(
name|doc
argument_list|)
operator|.
name|append
argument_list|(
literal|"] with id value "
argument_list|)
operator|.
name|append
argument_list|(
name|indexSearcher
operator|.
name|doc
argument_list|(
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|assertTopDocs
specifier|static
name|void
name|assertTopDocs
parameter_list|(
name|TopDocs
name|actual
parameter_list|,
name|TopDocs
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"actual.totalHits != expected.totalHits"
argument_list|,
name|actual
operator|.
name|totalHits
argument_list|,
name|equalTo
argument_list|(
name|expected
operator|.
name|totalHits
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"actual.getMaxScore() != expected.getMaxScore()"
argument_list|,
name|actual
operator|.
name|getMaxScore
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expected
operator|.
name|getMaxScore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"actual.scoreDocs.length != expected.scoreDocs.length"
argument_list|,
name|actual
operator|.
name|scoreDocs
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
name|actual
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|actual
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ScoreDoc
name|actualHit
init|=
name|actual
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|ScoreDoc
name|expectedHit
init|=
name|expected
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|assertThat
argument_list|(
literal|"actualHit.doc != expectedHit.doc"
argument_list|,
name|actualHit
operator|.
name|doc
argument_list|,
name|equalTo
argument_list|(
name|expectedHit
operator|.
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"actualHit.score != expectedHit.score"
argument_list|,
name|actualHit
operator|.
name|score
argument_list|,
name|equalTo
argument_list|(
name|expectedHit
operator|.
name|score
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|wrapWithBitSetFilter
specifier|static
name|BitDocIdSetFilter
name|wrapWithBitSetFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
name|SearchContext
operator|.
name|current
argument_list|()
operator|.
name|bitsetFilterCache
argument_list|()
operator|.
name|getBitDocIdSetFilter
argument_list|(
name|filter
argument_list|)
return|;
block|}
DECL|method|parseQuery
specifier|static
name|Query
name|parseQuery
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryShardContext
name|context
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
name|SearchContext
operator|.
name|current
argument_list|()
operator|.
name|queryParserService
argument_list|()
argument_list|)
decl_stmt|;
name|XContentParser
name|parser
init|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|queryBuilder
operator|.
name|buildAsBytes
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|reset
argument_list|(
name|parser
argument_list|)
expr_stmt|;
return|return
name|context
operator|.
name|parseContext
argument_list|()
operator|.
name|parseInnerQuery
argument_list|()
return|;
block|}
block|}
end_class

end_unit

