begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.ingest.transport.simulate
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|simulate
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentBuilderString
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_class
DECL|class|SimulateVerboseDocumentResult
specifier|public
class|class
name|SimulateVerboseDocumentResult
extends|extends
name|SimulateDocumentResult
block|{
DECL|field|STREAM_ID
specifier|public
specifier|static
specifier|final
name|int
name|STREAM_ID
init|=
literal|1
decl_stmt|;
DECL|field|processorResults
specifier|private
name|List
argument_list|<
name|SimulateProcessorResult
argument_list|>
name|processorResults
decl_stmt|;
DECL|method|SimulateVerboseDocumentResult
specifier|public
name|SimulateVerboseDocumentResult
parameter_list|()
block|{      }
DECL|method|SimulateVerboseDocumentResult
specifier|public
name|SimulateVerboseDocumentResult
parameter_list|(
name|List
argument_list|<
name|SimulateProcessorResult
argument_list|>
name|processorResults
parameter_list|)
block|{
name|this
operator|.
name|processorResults
operator|=
name|processorResults
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStreamId
specifier|public
name|int
name|getStreamId
parameter_list|()
block|{
return|return
name|STREAM_ID
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|streamId
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|streamId
operator|!=
name|STREAM_ID
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"stream_id ["
operator|+
name|streamId
operator|+
literal|"] does not match "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" [stream_id="
operator|+
name|STREAM_ID
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|processorResults
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|SimulateProcessorResult
name|processorResult
init|=
operator|new
name|SimulateProcessorResult
argument_list|()
decl_stmt|;
name|processorResult
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|processorResults
operator|.
name|add
argument_list|(
name|processorResult
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|STREAM_ID
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|processorResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SimulateProcessorResult
name|result
range|:
name|processorResults
control|)
block|{
name|result
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|PROCESSOR_RESULTS
argument_list|)
expr_stmt|;
for|for
control|(
name|SimulateProcessorResult
name|processorResult
range|:
name|processorResults
control|)
block|{
name|processorResult
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|SimulateVerboseDocumentResult
name|that
init|=
operator|(
name|SimulateVerboseDocumentResult
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|processorResults
argument_list|,
name|that
operator|.
name|processorResults
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|processorResults
argument_list|)
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|PROCESSOR_RESULTS
specifier|static
specifier|final
name|XContentBuilderString
name|PROCESSOR_RESULTS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"processor_results"
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

