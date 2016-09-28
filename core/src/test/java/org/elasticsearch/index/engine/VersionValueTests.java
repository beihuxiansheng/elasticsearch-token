begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
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
name|RamUsageTester
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
name|translog
operator|.
name|Translog
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
name|translog
operator|.
name|TranslogTests
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

begin_class
DECL|class|VersionValueTests
specifier|public
class|class
name|VersionValueTests
extends|extends
name|ESTestCase
block|{
DECL|method|testRamBytesUsed
specifier|public
name|void
name|testRamBytesUsed
parameter_list|()
block|{
name|VersionValue
name|versionValue
init|=
operator|new
name|VersionValue
argument_list|(
name|randomLong
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|RamUsageTester
operator|.
name|sizeOf
argument_list|(
name|versionValue
argument_list|)
argument_list|,
name|versionValue
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeleteRamBytesUsed
specifier|public
name|void
name|testDeleteRamBytesUsed
parameter_list|()
block|{
name|DeleteVersionValue
name|versionValue
init|=
operator|new
name|DeleteVersionValue
argument_list|(
name|randomLong
argument_list|()
argument_list|,
name|randomLong
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|RamUsageTester
operator|.
name|sizeOf
argument_list|(
name|versionValue
argument_list|)
argument_list|,
name|versionValue
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
