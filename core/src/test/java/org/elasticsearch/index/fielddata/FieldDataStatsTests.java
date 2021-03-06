begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|FieldMemoryStats
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
name|FieldMemoryStatsTests
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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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

begin_class
DECL|class|FieldDataStatsTests
specifier|public
class|class
name|FieldDataStatsTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSerialize
specifier|public
name|void
name|testSerialize
parameter_list|()
throws|throws
name|IOException
block|{
name|FieldMemoryStats
name|map
init|=
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
name|FieldMemoryStatsTests
operator|.
name|randomFieldMemoryStats
argument_list|()
decl_stmt|;
name|FieldDataStats
name|stats
init|=
operator|new
name|FieldDataStats
argument_list|(
name|randomNonNegativeLong
argument_list|()
argument_list|,
name|randomNonNegativeLong
argument_list|()
argument_list|,
name|map
operator|==
literal|null
condition|?
literal|null
else|:
name|map
argument_list|)
decl_stmt|;
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|stats
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|FieldDataStats
name|read
init|=
operator|new
name|FieldDataStats
argument_list|()
decl_stmt|;
name|StreamInput
name|input
init|=
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
decl_stmt|;
name|read
operator|.
name|readFrom
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|input
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|evictions
argument_list|,
name|read
operator|.
name|evictions
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|memorySize
argument_list|,
name|read
operator|.
name|memorySize
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|getFields
argument_list|()
argument_list|,
name|read
operator|.
name|getFields
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

