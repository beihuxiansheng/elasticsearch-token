begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.io
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
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
name|util
operator|.
name|LuceneTestCase
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
name|FileSystem
import|;
end_import

begin_comment
comment|/**  * Exposes some package private stuff in PathUtils for framework purposes only!  */
end_comment

begin_class
DECL|class|PathUtilsForTesting
specifier|public
class|class
name|PathUtilsForTesting
block|{
comment|/** Sets a new default filesystem for testing */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
comment|// https://github.com/elastic/elasticsearch/issues/15845
DECL|method|setup
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
block|{
name|installMock
argument_list|(
name|LuceneTestCase
operator|.
name|getBaseTempDirForTestClass
argument_list|()
operator|.
name|getFileSystem
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Installs a mock filesystem for testing */
DECL|method|installMock
specifier|public
specifier|static
name|void
name|installMock
parameter_list|(
name|FileSystem
name|mock
parameter_list|)
block|{
name|PathUtils
operator|.
name|DEFAULT
operator|=
name|mock
expr_stmt|;
block|}
comment|/** Resets filesystem back to the real system default */
DECL|method|teardown
specifier|public
specifier|static
name|void
name|teardown
parameter_list|()
block|{
name|PathUtils
operator|.
name|DEFAULT
operator|=
name|PathUtils
operator|.
name|ACTUAL_DEFAULT
expr_stmt|;
block|}
block|}
end_class

end_unit

