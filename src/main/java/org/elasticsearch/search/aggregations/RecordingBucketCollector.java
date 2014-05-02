begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalStateException
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
name|lease
operator|.
name|Releasable
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

begin_comment
comment|/**  * Abstraction for implementations that record a "collect" stream for subsequent play-back  */
end_comment

begin_class
DECL|class|RecordingBucketCollector
specifier|public
specifier|abstract
class|class
name|RecordingBucketCollector
extends|extends
name|BucketCollector
implements|implements
name|Releasable
block|{
comment|/**      * Replay a previously executed set of calls to the {@link #collect(int, long)} method      * @param collector the object which will be called to handle the playback      * @throws IOException      */
DECL|method|replayCollection
specifier|public
specifier|abstract
name|void
name|replayCollection
parameter_list|(
name|BucketCollector
name|collector
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|gatherAnalysis
specifier|public
name|void
name|gatherAnalysis
parameter_list|(
name|BucketAnalysisCollector
name|analysisCollector
parameter_list|,
name|long
name|bucketOrdinal
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"gatherAnalysis not supported"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

