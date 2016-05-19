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
name|action
operator|.
name|IndicesRequest
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
name|search
operator|.
name|SearchRequest
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
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
operator|.
name|randomSimpleString
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
name|arrayWithSize
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|sameInstance
import|;
end_import

begin_class
DECL|class|UpdateByQueryRequestTests
specifier|public
class|class
name|UpdateByQueryRequestTests
extends|extends
name|ESTestCase
block|{
DECL|method|testUpdateByQueryRequestImplementsCompositeIndicesRequestWithDummies
specifier|public
name|void
name|testUpdateByQueryRequestImplementsCompositeIndicesRequestWithDummies
parameter_list|()
block|{
name|int
name|numIndices
init|=
name|between
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|String
index|[]
name|indices
init|=
operator|new
name|String
index|[
name|numIndices
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numIndices
condition|;
name|i
operator|++
control|)
block|{
name|indices
index|[
name|i
index|]
operator|=
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|30
argument_list|)
expr_stmt|;
block|}
name|UpdateByQueryRequest
name|request
init|=
operator|new
name|UpdateByQueryRequest
argument_list|(
operator|new
name|SearchRequest
argument_list|(
name|indices
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|IndicesRequest
argument_list|>
name|subRequests
init|=
name|request
operator|.
name|subRequests
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|subRequests
argument_list|,
name|hasSize
argument_list|(
name|numIndices
operator|+
literal|1
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
name|numIndices
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|subRequests
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|indices
argument_list|()
argument_list|,
name|arrayWithSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|indices
index|[
name|i
index|]
argument_list|,
name|subRequests
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|indices
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|subRequests
operator|.
name|get
argument_list|(
name|numIndices
argument_list|)
argument_list|,
name|sameInstance
argument_list|(
name|request
operator|.
name|getSearchRequest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

