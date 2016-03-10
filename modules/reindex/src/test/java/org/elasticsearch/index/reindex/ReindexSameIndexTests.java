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
name|ActionRequestValidationException
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
name|index
operator|.
name|IndexRequest
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
name|action
operator|.
name|support
operator|.
name|AutoCreateIndex
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
name|ClusterName
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
name|ClusterState
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
name|AliasMetaData
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
name|cluster
operator|.
name|metadata
operator|.
name|IndexNameExpressionResolver
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
name|MetaData
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
name|test
operator|.
name|ESTestCase
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
comment|/**  * Tests that indexing from an index back into itself fails the request.  */
end_comment

begin_class
DECL|class|ReindexSameIndexTests
specifier|public
class|class
name|ReindexSameIndexTests
extends|extends
name|ESTestCase
block|{
DECL|field|STATE
specifier|private
specifier|static
specifier|final
name|ClusterState
name|STATE
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
operator|new
name|ClusterName
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|metaData
argument_list|(
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|index
argument_list|(
literal|"target"
argument_list|,
literal|"target_alias"
argument_list|,
literal|"target_multi"
argument_list|)
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
name|index
argument_list|(
literal|"target2"
argument_list|,
literal|"target_multi"
argument_list|)
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
name|index
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
name|index
argument_list|(
literal|"bar"
argument_list|)
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
name|index
argument_list|(
literal|"baz"
argument_list|)
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
name|index
argument_list|(
literal|"source"
argument_list|,
literal|"source_multi"
argument_list|)
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
name|index
argument_list|(
literal|"source2"
argument_list|,
literal|"source_multi"
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|INDEX_NAME_EXPRESSION_RESOLVER
specifier|private
specifier|static
specifier|final
name|IndexNameExpressionResolver
name|INDEX_NAME_EXPRESSION_RESOLVER
init|=
operator|new
name|IndexNameExpressionResolver
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
DECL|field|AUTO_CREATE_INDEX
specifier|private
specifier|static
specifier|final
name|AutoCreateIndex
name|AUTO_CREATE_INDEX
init|=
operator|new
name|AutoCreateIndex
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|INDEX_NAME_EXPRESSION_RESOLVER
argument_list|)
decl_stmt|;
DECL|method|testObviousCases
specifier|public
name|void
name|testObviousCases
parameter_list|()
throws|throws
name|Exception
block|{
name|fails
argument_list|(
literal|"target"
argument_list|,
literal|"target"
argument_list|)
expr_stmt|;
name|fails
argument_list|(
literal|"target"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"target"
argument_list|,
literal|"baz"
argument_list|)
expr_stmt|;
name|fails
argument_list|(
literal|"target"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"target"
argument_list|,
literal|"baz"
argument_list|,
literal|"target"
argument_list|)
expr_stmt|;
name|succeeds
argument_list|(
literal|"target"
argument_list|,
literal|"source"
argument_list|)
expr_stmt|;
name|succeeds
argument_list|(
literal|"target"
argument_list|,
literal|"source"
argument_list|,
literal|"source2"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAliasesContainTarget
specifier|public
name|void
name|testAliasesContainTarget
parameter_list|()
throws|throws
name|Exception
block|{
name|fails
argument_list|(
literal|"target"
argument_list|,
literal|"target_alias"
argument_list|)
expr_stmt|;
name|fails
argument_list|(
literal|"target_alias"
argument_list|,
literal|"target"
argument_list|)
expr_stmt|;
name|fails
argument_list|(
literal|"target"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"target_alias"
argument_list|,
literal|"baz"
argument_list|)
expr_stmt|;
name|fails
argument_list|(
literal|"target_alias"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"target_alias"
argument_list|,
literal|"baz"
argument_list|)
expr_stmt|;
name|fails
argument_list|(
literal|"target_alias"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"target"
argument_list|,
literal|"baz"
argument_list|)
expr_stmt|;
name|fails
argument_list|(
literal|"target"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"target_alias"
argument_list|,
literal|"target_alias"
argument_list|)
expr_stmt|;
name|fails
argument_list|(
literal|"target"
argument_list|,
literal|"target_multi"
argument_list|)
expr_stmt|;
name|fails
argument_list|(
literal|"target"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"target_multi"
argument_list|,
literal|"baz"
argument_list|)
expr_stmt|;
name|succeeds
argument_list|(
literal|"target"
argument_list|,
literal|"source_multi"
argument_list|)
expr_stmt|;
name|succeeds
argument_list|(
literal|"target"
argument_list|,
literal|"source"
argument_list|,
literal|"source2"
argument_list|,
literal|"source_multi"
argument_list|)
expr_stmt|;
block|}
DECL|method|testTargetIsAlias
specifier|public
name|void
name|testTargetIsAlias
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|succeeds
argument_list|(
literal|"target_multi"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected failure"
argument_list|)
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
literal|"Alias [target_multi] has more than one indices associated with it [["
argument_list|)
argument_list|)
expr_stmt|;
comment|// The index names can come in either order
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"target"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"target2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fails
specifier|private
name|void
name|fails
parameter_list|(
name|String
name|target
parameter_list|,
name|String
modifier|...
name|sources
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|succeeds
argument_list|(
name|target
argument_list|,
name|sources
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ActionRequestValidationException
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
literal|"reindex cannot write into an index its reading from [target]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|succeeds
specifier|private
name|void
name|succeeds
parameter_list|(
name|String
name|target
parameter_list|,
name|String
modifier|...
name|sources
parameter_list|)
throws|throws
name|Exception
block|{
name|TransportReindexAction
operator|.
name|validateAgainstAliases
argument_list|(
operator|new
name|SearchRequest
argument_list|(
name|sources
argument_list|)
argument_list|,
operator|new
name|IndexRequest
argument_list|(
name|target
argument_list|)
argument_list|,
name|INDEX_NAME_EXPRESSION_RESOLVER
argument_list|,
name|AUTO_CREATE_INDEX
argument_list|,
name|STATE
argument_list|)
expr_stmt|;
block|}
DECL|method|index
specifier|private
specifier|static
name|IndexMetaData
name|index
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|aliases
parameter_list|)
block|{
name|IndexMetaData
operator|.
name|Builder
name|builder
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|name
argument_list|)
operator|.
name|settings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.version.created"
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|id
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|alias
range|:
name|aliases
control|)
block|{
name|builder
operator|.
name|putAlias
argument_list|(
name|AliasMetaData
operator|.
name|builder
argument_list|(
name|alias
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit
