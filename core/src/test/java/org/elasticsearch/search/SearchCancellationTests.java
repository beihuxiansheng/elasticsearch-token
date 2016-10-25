begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search
package|package
name|org
operator|.
name|elasticsearch
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|StringField
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
name|IndexReader
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
name|RandomIndexWriter
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
name|LeafCollector
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
name|TotalHitCountCollector
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
name|store
operator|.
name|Directory
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
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
name|query
operator|.
name|CancellableCollector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|TaskCancelledException
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_class
DECL|class|SearchCancellationTests
specifier|public
class|class
name|SearchCancellationTests
extends|extends
name|ESTestCase
block|{
DECL|field|dir
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
specifier|static
name|IndexReader
name|reader
decl_stmt|;
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|indexRandomDocuments
argument_list|(
name|w
argument_list|,
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|flush
argument_list|()
expr_stmt|;
name|indexRandomDocuments
argument_list|(
name|w
argument_list|,
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
name|w
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|indexRandomDocuments
specifier|private
specifier|static
name|void
name|indexRandomDocuments
parameter_list|(
name|RandomIndexWriter
name|w
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|numHoles
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numHoles
condition|;
operator|++
name|j
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
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
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testLowLevelCancellableCollector
specifier|public
name|void
name|testLowLevelCancellableCollector
parameter_list|()
throws|throws
name|IOException
block|{
name|TotalHitCountCollector
name|collector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|AtomicBoolean
name|cancelled
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|CancellableCollector
name|cancellableCollector
init|=
operator|new
name|CancellableCollector
argument_list|(
name|cancelled
operator|::
name|get
argument_list|,
literal|true
argument_list|,
name|collector
argument_list|)
decl_stmt|;
specifier|final
name|LeafCollector
name|leafCollector
init|=
name|cancellableCollector
operator|.
name|getLeafCollector
argument_list|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|leafCollector
operator|.
name|collect
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cancelled
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|TaskCancelledException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|leafCollector
operator|.
name|collect
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCancellableCollector
specifier|public
name|void
name|testCancellableCollector
parameter_list|()
throws|throws
name|IOException
block|{
name|TotalHitCountCollector
name|collector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|AtomicBoolean
name|cancelled
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|CancellableCollector
name|cancellableCollector
init|=
operator|new
name|CancellableCollector
argument_list|(
name|cancelled
operator|::
name|get
argument_list|,
literal|false
argument_list|,
name|collector
argument_list|)
decl_stmt|;
specifier|final
name|LeafCollector
name|leafCollector
init|=
name|cancellableCollector
operator|.
name|getLeafCollector
argument_list|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|leafCollector
operator|.
name|collect
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cancelled
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|leafCollector
operator|.
name|collect
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|TaskCancelledException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|cancellableCollector
operator|.
name|getLeafCollector
argument_list|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

