begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESBackcompatTestCase
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
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|SharedSignificantTermsTestMethods
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
name|ExecutionException
import|;
end_import

begin_class
DECL|class|SignificantTermsBackwardCompatibilityIT
specifier|public
class|class
name|SignificantTermsBackwardCompatibilityIT
extends|extends
name|ESBackcompatTestCase
block|{
comment|/**      * Test for streaming significant terms buckets to old es versions.      */
DECL|method|testAggregateAndCheckFromSeveralShards
specifier|public
name|void
name|testAggregateAndCheckFromSeveralShards
parameter_list|()
throws|throws
name|IOException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|SharedSignificantTermsTestMethods
operator|.
name|aggregateAndCheckFromSeveralShards
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

