begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
operator|.
name|SortOrder
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
name|termQuery
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertHitCount
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
name|hasSize
import|;
end_import

begin_class
DECL|class|UpdateByQueryBasicTests
specifier|public
class|class
name|UpdateByQueryBasicTests
extends|extends
name|ReindexTestCase
block|{
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSize
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// Reindex all the docs
name|assertThat
argument_list|(
name|updateByQuery
argument_list|()
operator|.
name|source
argument_list|(
literal|"test"
argument_list|)
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|updated
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now none of them
name|assertThat
argument_list|(
name|updateByQuery
argument_list|()
operator|.
name|source
argument_list|(
literal|"test"
argument_list|)
operator|.
name|filter
argument_list|(
name|termQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"no_match"
argument_list|)
argument_list|)
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|updated
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now half of them
name|assertThat
argument_list|(
name|updateByQuery
argument_list|()
operator|.
name|source
argument_list|(
literal|"test"
argument_list|)
operator|.
name|filter
argument_list|(
name|termQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|updated
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// Limit with size
name|UpdateByQueryRequestBuilder
name|request
init|=
name|updateByQuery
argument_list|()
operator|.
name|source
argument_list|(
literal|"test"
argument_list|)
operator|.
name|size
argument_list|(
literal|3
argument_list|)
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|request
operator|.
name|source
argument_list|()
operator|.
name|addSort
argument_list|(
literal|"foo.keyword"
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|request
operator|.
name|get
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|updated
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Only the first three documents are updated because of sort
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testWorkers
specifier|public
name|void
name|testWorkers
parameter_list|()
throws|throws
name|Exception
block|{
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSize
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// Reindex all the docs
name|assertThat
argument_list|(
name|updateByQuery
argument_list|()
operator|.
name|source
argument_list|(
literal|"test"
argument_list|)
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
operator|.
name|setSlices
argument_list|(
literal|5
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|updated
argument_list|(
literal|4
argument_list|)
operator|.
name|slices
argument_list|(
name|hasSize
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now none of them
name|assertThat
argument_list|(
name|updateByQuery
argument_list|()
operator|.
name|source
argument_list|(
literal|"test"
argument_list|)
operator|.
name|filter
argument_list|(
name|termQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"no_match"
argument_list|)
argument_list|)
operator|.
name|setSlices
argument_list|(
literal|5
argument_list|)
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|updated
argument_list|(
literal|0
argument_list|)
operator|.
name|slices
argument_list|(
name|hasSize
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now half of them
name|assertThat
argument_list|(
name|updateByQuery
argument_list|()
operator|.
name|source
argument_list|(
literal|"test"
argument_list|)
operator|.
name|filter
argument_list|(
name|termQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
operator|.
name|setSlices
argument_list|(
literal|5
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|updated
argument_list|(
literal|2
argument_list|)
operator|.
name|slices
argument_list|(
name|hasSize
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

