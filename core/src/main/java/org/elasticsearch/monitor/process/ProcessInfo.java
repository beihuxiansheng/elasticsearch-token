begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.process
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|process
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|ToXContent
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|ProcessInfo
specifier|public
class|class
name|ProcessInfo
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|field|refreshInterval
name|long
name|refreshInterval
decl_stmt|;
DECL|field|id
specifier|private
name|long
name|id
decl_stmt|;
DECL|field|mlockall
specifier|private
name|boolean
name|mlockall
decl_stmt|;
DECL|method|ProcessInfo
name|ProcessInfo
parameter_list|()
block|{     }
DECL|method|ProcessInfo
specifier|public
name|ProcessInfo
parameter_list|(
name|long
name|id
parameter_list|,
name|boolean
name|mlockall
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|mlockall
operator|=
name|mlockall
expr_stmt|;
block|}
DECL|method|refreshInterval
specifier|public
name|long
name|refreshInterval
parameter_list|()
block|{
return|return
name|this
operator|.
name|refreshInterval
return|;
block|}
DECL|method|getRefreshInterval
specifier|public
name|long
name|getRefreshInterval
parameter_list|()
block|{
return|return
name|this
operator|.
name|refreshInterval
return|;
block|}
comment|/**      * The process id.      */
DECL|method|getId
specifier|public
name|long
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|isMlockall
specifier|public
name|boolean
name|isMlockall
parameter_list|()
block|{
return|return
name|mlockall
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|PROCESS
specifier|static
specifier|final
name|String
name|PROCESS
init|=
literal|"process"
decl_stmt|;
DECL|field|REFRESH_INTERVAL
specifier|static
specifier|final
name|String
name|REFRESH_INTERVAL
init|=
literal|"refresh_interval"
decl_stmt|;
DECL|field|REFRESH_INTERVAL_IN_MILLIS
specifier|static
specifier|final
name|String
name|REFRESH_INTERVAL_IN_MILLIS
init|=
literal|"refresh_interval_in_millis"
decl_stmt|;
DECL|field|ID
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"id"
decl_stmt|;
DECL|field|MLOCKALL
specifier|static
specifier|final
name|String
name|MLOCKALL
init|=
literal|"mlockall"
decl_stmt|;
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
argument_list|(
name|Fields
operator|.
name|PROCESS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|timeValueField
argument_list|(
name|Fields
operator|.
name|REFRESH_INTERVAL_IN_MILLIS
argument_list|,
name|Fields
operator|.
name|REFRESH_INTERVAL
argument_list|,
name|refreshInterval
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ID
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MLOCKALL
argument_list|,
name|mlockall
argument_list|)
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
DECL|method|readProcessInfo
specifier|public
specifier|static
name|ProcessInfo
name|readProcessInfo
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ProcessInfo
name|info
init|=
operator|new
name|ProcessInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|info
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
name|refreshInterval
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|id
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|mlockall
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
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
name|writeLong
argument_list|(
name|refreshInterval
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|mlockall
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

