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
name|get
operator|.
name|GetResponse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|DocumentRequest
operator|.
name|OpType
operator|.
name|CREATE
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
name|VersionType
operator|.
name|EXTERNAL
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
name|VersionType
operator|.
name|INTERNAL
import|;
end_import

begin_class
DECL|class|ReindexVersioningTests
specifier|public
class|class
name|ReindexVersioningTests
extends|extends
name|ReindexTestCase
block|{
DECL|field|SOURCE_VERSION
specifier|private
specifier|static
specifier|final
name|int
name|SOURCE_VERSION
init|=
literal|4
decl_stmt|;
DECL|field|OLDER_VERSION
specifier|private
specifier|static
specifier|final
name|int
name|OLDER_VERSION
init|=
literal|1
decl_stmt|;
DECL|field|NEWER_VERSION
specifier|private
specifier|static
specifier|final
name|int
name|NEWER_VERSION
init|=
literal|10
decl_stmt|;
DECL|method|testExternalVersioningCreatesWhenAbsentAndSetsVersion
specifier|public
name|void
name|testExternalVersioningCreatesWhenAbsentAndSetsVersion
parameter_list|()
throws|throws
name|Exception
block|{
name|setupSourceAbsent
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reindexExternal
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|created
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertDest
argument_list|(
literal|"source"
argument_list|,
name|SOURCE_VERSION
argument_list|)
expr_stmt|;
block|}
DECL|method|testExternalVersioningUpdatesOnOlderAndSetsVersion
specifier|public
name|void
name|testExternalVersioningUpdatesOnOlderAndSetsVersion
parameter_list|()
throws|throws
name|Exception
block|{
name|setupDestOlder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reindexExternal
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|updated
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertDest
argument_list|(
literal|"source"
argument_list|,
name|SOURCE_VERSION
argument_list|)
expr_stmt|;
block|}
DECL|method|testExternalVersioningVersionConflictsOnNewer
specifier|public
name|void
name|testExternalVersioningVersionConflictsOnNewer
parameter_list|()
throws|throws
name|Exception
block|{
name|setupDestNewer
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reindexExternal
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|versionConflicts
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertDest
argument_list|(
literal|"dest"
argument_list|,
name|NEWER_VERSION
argument_list|)
expr_stmt|;
block|}
DECL|method|testInternalVersioningCreatesWhenAbsent
specifier|public
name|void
name|testInternalVersioningCreatesWhenAbsent
parameter_list|()
throws|throws
name|Exception
block|{
name|setupSourceAbsent
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reindexInternal
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|created
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertDest
argument_list|(
literal|"source"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testInternalVersioningUpdatesOnOlder
specifier|public
name|void
name|testInternalVersioningUpdatesOnOlder
parameter_list|()
throws|throws
name|Exception
block|{
name|setupDestOlder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reindexInternal
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|updated
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertDest
argument_list|(
literal|"source"
argument_list|,
name|OLDER_VERSION
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testInternalVersioningUpdatesOnNewer
specifier|public
name|void
name|testInternalVersioningUpdatesOnNewer
parameter_list|()
throws|throws
name|Exception
block|{
name|setupDestNewer
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reindexInternal
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|updated
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertDest
argument_list|(
literal|"source"
argument_list|,
name|NEWER_VERSION
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateCreatesWhenAbsent
specifier|public
name|void
name|testCreateCreatesWhenAbsent
parameter_list|()
throws|throws
name|Exception
block|{
name|setupSourceAbsent
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reindexCreate
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|created
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertDest
argument_list|(
literal|"source"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateVersionConflictsOnOlder
specifier|public
name|void
name|testCreateVersionConflictsOnOlder
parameter_list|()
throws|throws
name|Exception
block|{
name|setupDestOlder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reindexCreate
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|versionConflicts
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertDest
argument_list|(
literal|"dest"
argument_list|,
name|OLDER_VERSION
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateVersionConflictsOnNewer
specifier|public
name|void
name|testCreateVersionConflictsOnNewer
parameter_list|()
throws|throws
name|Exception
block|{
name|setupDestNewer
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reindexCreate
argument_list|()
argument_list|,
name|matcher
argument_list|()
operator|.
name|versionConflicts
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertDest
argument_list|(
literal|"dest"
argument_list|,
name|NEWER_VERSION
argument_list|)
expr_stmt|;
block|}
comment|/**      * Perform a reindex with EXTERNAL versioning which has "refresh" semantics.      */
DECL|method|reindexExternal
specifier|private
name|BulkIndexByScrollResponse
name|reindexExternal
parameter_list|()
block|{
name|ReindexRequestBuilder
name|reindex
init|=
name|reindex
argument_list|()
operator|.
name|source
argument_list|(
literal|"source"
argument_list|)
operator|.
name|destination
argument_list|(
literal|"dest"
argument_list|)
operator|.
name|abortOnVersionConflict
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|reindex
operator|.
name|destination
argument_list|()
operator|.
name|setVersionType
argument_list|(
name|EXTERNAL
argument_list|)
expr_stmt|;
return|return
name|reindex
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * Perform a reindex with INTERNAL versioning which has "overwrite" semantics.      */
DECL|method|reindexInternal
specifier|private
name|BulkIndexByScrollResponse
name|reindexInternal
parameter_list|()
block|{
name|ReindexRequestBuilder
name|reindex
init|=
name|reindex
argument_list|()
operator|.
name|source
argument_list|(
literal|"source"
argument_list|)
operator|.
name|destination
argument_list|(
literal|"dest"
argument_list|)
operator|.
name|abortOnVersionConflict
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|reindex
operator|.
name|destination
argument_list|()
operator|.
name|setVersionType
argument_list|(
name|INTERNAL
argument_list|)
expr_stmt|;
return|return
name|reindex
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * Perform a reindex with CREATE OpType which has "create" semantics.      */
DECL|method|reindexCreate
specifier|private
name|BulkIndexByScrollResponse
name|reindexCreate
parameter_list|()
block|{
name|ReindexRequestBuilder
name|reindex
init|=
name|reindex
argument_list|()
operator|.
name|source
argument_list|(
literal|"source"
argument_list|)
operator|.
name|destination
argument_list|(
literal|"dest"
argument_list|)
operator|.
name|abortOnVersionConflict
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|reindex
operator|.
name|destination
argument_list|()
operator|.
name|setOpType
argument_list|(
name|CREATE
argument_list|)
expr_stmt|;
return|return
name|reindex
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|setupSourceAbsent
specifier|private
name|void
name|setupSourceAbsent
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
literal|"source"
argument_list|,
literal|"test"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|setVersionType
argument_list|(
name|EXTERNAL
argument_list|)
operator|.
name|setVersion
argument_list|(
name|SOURCE_VERSION
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"source"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SOURCE_VERSION
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"source"
argument_list|,
literal|"test"
argument_list|,
literal|"test"
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
DECL|method|setupDest
specifier|private
name|void
name|setupDest
parameter_list|(
name|int
name|version
parameter_list|)
throws|throws
name|Exception
block|{
name|setupSourceAbsent
argument_list|()
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"dest"
argument_list|,
literal|"test"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|setVersionType
argument_list|(
name|EXTERNAL
argument_list|)
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"dest"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|version
argument_list|,
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"dest"
argument_list|,
literal|"test"
argument_list|,
literal|"test"
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
DECL|method|setupDestOlder
specifier|private
name|void
name|setupDestOlder
parameter_list|()
throws|throws
name|Exception
block|{
name|setupDest
argument_list|(
name|OLDER_VERSION
argument_list|)
expr_stmt|;
block|}
DECL|method|setupDestNewer
specifier|private
name|void
name|setupDestNewer
parameter_list|()
throws|throws
name|Exception
block|{
name|setupDest
argument_list|(
name|NEWER_VERSION
argument_list|)
expr_stmt|;
block|}
DECL|method|assertDest
specifier|private
name|void
name|assertDest
parameter_list|(
name|String
name|fooValue
parameter_list|,
name|int
name|version
parameter_list|)
block|{
name|GetResponse
name|get
init|=
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"dest"
argument_list|,
literal|"test"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|fooValue
argument_list|,
name|get
operator|.
name|getSource
argument_list|()
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|version
argument_list|,
name|get
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

