begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.optimize
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|optimize
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|broadcast
operator|.
name|BroadcastOperationRequest
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A request to optimize one or more indices. In order to optimize on all the indices, pass an empty array or  *<tt>null</tt> for the indices.  *<p/>  *<p>{@link #waitForMerge(boolean)} allows to control if the call will block until the optimize completes and  * defaults to<tt>true</tt>.  *<p/>  *<p>{@link #maxNumSegments(int)} allows to control the number of segments to optimize down to. By default, will  * cause the optimize process to optimize down to half the configured number of segments.  *  * @see org.elasticsearch.client.Requests#optimizeRequest(String...)  * @see org.elasticsearch.client.IndicesAdminClient#optimize(OptimizeRequest)  * @see OptimizeResponse  */
end_comment

begin_class
DECL|class|OptimizeRequest
specifier|public
class|class
name|OptimizeRequest
extends|extends
name|BroadcastOperationRequest
argument_list|<
name|OptimizeRequest
argument_list|>
block|{
DECL|class|Defaults
specifier|public
specifier|static
specifier|final
class|class
name|Defaults
block|{
DECL|field|WAIT_FOR_MERGE
specifier|public
specifier|static
specifier|final
name|boolean
name|WAIT_FOR_MERGE
init|=
literal|true
decl_stmt|;
DECL|field|MAX_NUM_SEGMENTS
specifier|public
specifier|static
specifier|final
name|int
name|MAX_NUM_SEGMENTS
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|ONLY_EXPUNGE_DELETES
specifier|public
specifier|static
specifier|final
name|boolean
name|ONLY_EXPUNGE_DELETES
init|=
literal|false
decl_stmt|;
DECL|field|FLUSH
specifier|public
specifier|static
specifier|final
name|boolean
name|FLUSH
init|=
literal|true
decl_stmt|;
block|}
DECL|field|waitForMerge
specifier|private
name|boolean
name|waitForMerge
init|=
name|Defaults
operator|.
name|WAIT_FOR_MERGE
decl_stmt|;
DECL|field|maxNumSegments
specifier|private
name|int
name|maxNumSegments
init|=
name|Defaults
operator|.
name|MAX_NUM_SEGMENTS
decl_stmt|;
DECL|field|onlyExpungeDeletes
specifier|private
name|boolean
name|onlyExpungeDeletes
init|=
name|Defaults
operator|.
name|ONLY_EXPUNGE_DELETES
decl_stmt|;
DECL|field|flush
specifier|private
name|boolean
name|flush
init|=
name|Defaults
operator|.
name|FLUSH
decl_stmt|;
comment|/**      * Constructs an optimization request over one or more indices.      *      * @param indices The indices to optimize, no indices passed means all indices will be optimized.      */
DECL|method|OptimizeRequest
specifier|public
name|OptimizeRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|super
argument_list|(
name|indices
argument_list|)
expr_stmt|;
block|}
DECL|method|OptimizeRequest
specifier|public
name|OptimizeRequest
parameter_list|()
block|{      }
comment|/**      * Should the call block until the optimize completes. Defaults to<tt>true</tt>.      */
DECL|method|waitForMerge
specifier|public
name|boolean
name|waitForMerge
parameter_list|()
block|{
return|return
name|waitForMerge
return|;
block|}
comment|/**      * Should the call block until the optimize completes. Defaults to<tt>true</tt>.      */
DECL|method|waitForMerge
specifier|public
name|OptimizeRequest
name|waitForMerge
parameter_list|(
name|boolean
name|waitForMerge
parameter_list|)
block|{
name|this
operator|.
name|waitForMerge
operator|=
name|waitForMerge
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Will optimize the index down to<= maxNumSegments. By default, will cause the optimize      * process to optimize down to half the configured number of segments.      */
DECL|method|maxNumSegments
specifier|public
name|int
name|maxNumSegments
parameter_list|()
block|{
return|return
name|maxNumSegments
return|;
block|}
comment|/**      * Will optimize the index down to<= maxNumSegments. By default, will cause the optimize      * process to optimize down to half the configured number of segments.      */
DECL|method|maxNumSegments
specifier|public
name|OptimizeRequest
name|maxNumSegments
parameter_list|(
name|int
name|maxNumSegments
parameter_list|)
block|{
name|this
operator|.
name|maxNumSegments
operator|=
name|maxNumSegments
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the optimization only expunge deletes from the index, without full optimization.      * Defaults to full optimization (<tt>false</tt>).      */
DECL|method|onlyExpungeDeletes
specifier|public
name|boolean
name|onlyExpungeDeletes
parameter_list|()
block|{
return|return
name|onlyExpungeDeletes
return|;
block|}
comment|/**      * Should the optimization only expunge deletes from the index, without full optimization.      * Defaults to full optimization (<tt>false</tt>).      */
DECL|method|onlyExpungeDeletes
specifier|public
name|OptimizeRequest
name|onlyExpungeDeletes
parameter_list|(
name|boolean
name|onlyExpungeDeletes
parameter_list|)
block|{
name|this
operator|.
name|onlyExpungeDeletes
operator|=
name|onlyExpungeDeletes
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should flush be performed after the optimization. Defaults to<tt>true</tt>.      */
DECL|method|flush
specifier|public
name|boolean
name|flush
parameter_list|()
block|{
return|return
name|flush
return|;
block|}
comment|/**      * Should flush be performed after the optimization. Defaults to<tt>true</tt>.      */
DECL|method|flush
specifier|public
name|OptimizeRequest
name|flush
parameter_list|(
name|boolean
name|flush
parameter_list|)
block|{
name|this
operator|.
name|flush
operator|=
name|flush
expr_stmt|;
return|return
name|this
return|;
block|}
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
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|waitForMerge
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|maxNumSegments
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|onlyExpungeDeletes
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|flush
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrBefore
argument_list|(
name|Version
operator|.
name|V_0_90_3
argument_list|)
condition|)
block|{
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
comment|// old refresh flag
block|}
block|}
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|waitForMerge
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|maxNumSegments
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|onlyExpungeDeletes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|flush
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrBefore
argument_list|(
name|Version
operator|.
name|V_0_90_3
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// old refresh flag
block|}
block|}
block|}
end_class

end_unit

