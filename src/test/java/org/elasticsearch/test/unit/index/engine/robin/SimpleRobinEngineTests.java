begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.index.engine.robin
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|index
operator|.
name|engine
operator|.
name|robin
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
operator|.
name|AnalysisService
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
name|engine
operator|.
name|Engine
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
name|engine
operator|.
name|robin
operator|.
name|RobinEngine
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
name|indexing
operator|.
name|ShardIndexingService
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
name|settings
operator|.
name|IndexSettingsService
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
name|similarity
operator|.
name|SimilarityService
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
name|store
operator|.
name|Store
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
name|test
operator|.
name|unit
operator|.
name|index
operator|.
name|engine
operator|.
name|AbstractSimpleEngineTests
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SimpleRobinEngineTests
specifier|public
class|class
name|SimpleRobinEngineTests
extends|extends
name|AbstractSimpleEngineTests
block|{
DECL|method|createEngine
specifier|protected
name|Engine
name|createEngine
parameter_list|(
name|Store
name|store
parameter_list|,
name|Translog
name|translog
parameter_list|)
block|{
return|return
operator|new
name|RobinEngine
argument_list|(
name|shardId
argument_list|,
name|EMPTY_SETTINGS
argument_list|,
name|threadPool
argument_list|,
operator|new
name|IndexSettingsService
argument_list|(
name|shardId
operator|.
name|index
argument_list|()
argument_list|,
name|EMPTY_SETTINGS
argument_list|)
argument_list|,
operator|new
name|ShardIndexingService
argument_list|(
name|shardId
argument_list|,
name|EMPTY_SETTINGS
argument_list|)
argument_list|,
literal|null
argument_list|,
name|store
argument_list|,
name|createSnapshotDeletionPolicy
argument_list|()
argument_list|,
name|translog
argument_list|,
name|createMergePolicy
argument_list|()
argument_list|,
name|createMergeScheduler
argument_list|()
argument_list|,
operator|new
name|AnalysisService
argument_list|(
name|shardId
operator|.
name|index
argument_list|()
argument_list|)
argument_list|,
operator|new
name|SimilarityService
argument_list|(
name|shardId
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

