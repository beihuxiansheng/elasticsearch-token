begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
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
name|store
operator|.
name|BaseDirectoryWrapper
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
name|FSDirectory
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
name|FileSwitchDirectory
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
name|FilterDirectory
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
name|RAMDirectory
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|notNullValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|nullValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|sameInstance
import|;
end_import

begin_class
DECL|class|DirectoryUtilsTests
specifier|public
class|class
name|DirectoryUtilsTests
extends|extends
name|ESTestCase
block|{
DECL|method|testGetLeave
specifier|public
name|void
name|testGetLeave
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|file
init|=
name|createTempDir
argument_list|()
decl_stmt|;
specifier|final
name|int
name|iters
init|=
name|scaledRandomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
block|{
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|FSDirectory
name|directory
init|=
name|DirectoryUtils
operator|.
name|getLeaf
argument_list|(
operator|new
name|FilterDirectory
argument_list|(
name|dir
argument_list|)
block|{}
argument_list|,
name|FSDirectory
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|directory
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|directory
argument_list|,
name|sameInstance
argument_list|(
name|DirectoryUtils
operator|.
name|getLeafDirectory
argument_list|(
name|dir
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|{
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|FSDirectory
name|directory
init|=
name|DirectoryUtils
operator|.
name|getLeaf
argument_list|(
name|dir
argument_list|,
name|FSDirectory
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|directory
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|directory
argument_list|,
name|sameInstance
argument_list|(
name|DirectoryUtils
operator|.
name|getLeafDirectory
argument_list|(
name|dir
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|stringSet
init|=
name|Collections
operator|.
name|emptySet
argument_list|()
decl_stmt|;
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|FSDirectory
name|directory
init|=
name|DirectoryUtils
operator|.
name|getLeaf
argument_list|(
operator|new
name|FileSwitchDirectory
argument_list|(
name|stringSet
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|,
name|FSDirectory
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|directory
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|directory
argument_list|,
name|sameInstance
argument_list|(
name|DirectoryUtils
operator|.
name|getLeafDirectory
argument_list|(
name|dir
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|stringSet
init|=
name|Collections
operator|.
name|emptySet
argument_list|()
decl_stmt|;
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|FSDirectory
name|directory
init|=
name|DirectoryUtils
operator|.
name|getLeaf
argument_list|(
operator|new
name|FilterDirectory
argument_list|(
operator|new
name|FileSwitchDirectory
argument_list|(
name|stringSet
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
block|{}
argument_list|,
name|FSDirectory
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|directory
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|directory
argument_list|,
name|sameInstance
argument_list|(
name|DirectoryUtils
operator|.
name|getLeafDirectory
argument_list|(
name|dir
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|stringSet
init|=
name|Collections
operator|.
name|emptySet
argument_list|()
decl_stmt|;
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|RAMDirectory
name|directory
init|=
name|DirectoryUtils
operator|.
name|getLeaf
argument_list|(
operator|new
name|FilterDirectory
argument_list|(
operator|new
name|FileSwitchDirectory
argument_list|(
name|stringSet
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
block|{}
argument_list|,
name|RAMDirectory
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|directory
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

