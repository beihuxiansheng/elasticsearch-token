begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.network
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|network
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|NetworkStats
specifier|public
class|class
name|NetworkStats
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|field|timestamp
name|long
name|timestamp
decl_stmt|;
DECL|field|tcp
name|Tcp
name|tcp
init|=
literal|null
decl_stmt|;
DECL|method|NetworkStats
name|NetworkStats
parameter_list|()
block|{      }
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|NETWORK
specifier|static
specifier|final
name|XContentBuilderString
name|NETWORK
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"network"
argument_list|)
decl_stmt|;
DECL|field|TCP
specifier|static
specifier|final
name|XContentBuilderString
name|TCP
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"tcp"
argument_list|)
decl_stmt|;
DECL|field|ACTIVE_OPENS
specifier|static
specifier|final
name|XContentBuilderString
name|ACTIVE_OPENS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"active_opens"
argument_list|)
decl_stmt|;
DECL|field|PASSIVE_OPENS
specifier|static
specifier|final
name|XContentBuilderString
name|PASSIVE_OPENS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"passive_opens"
argument_list|)
decl_stmt|;
DECL|field|CURR_ESTAB
specifier|static
specifier|final
name|XContentBuilderString
name|CURR_ESTAB
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"curr_estab"
argument_list|)
decl_stmt|;
DECL|field|IN_SEGS
specifier|static
specifier|final
name|XContentBuilderString
name|IN_SEGS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"in_segs"
argument_list|)
decl_stmt|;
DECL|field|OUT_SEGS
specifier|static
specifier|final
name|XContentBuilderString
name|OUT_SEGS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"out_segs"
argument_list|)
decl_stmt|;
DECL|field|RETRANS_SEGS
specifier|static
specifier|final
name|XContentBuilderString
name|RETRANS_SEGS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"retrans_segs"
argument_list|)
decl_stmt|;
DECL|field|ESTAB_RESETS
specifier|static
specifier|final
name|XContentBuilderString
name|ESTAB_RESETS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"estab_resets"
argument_list|)
decl_stmt|;
DECL|field|ATTEMPT_FAILS
specifier|static
specifier|final
name|XContentBuilderString
name|ATTEMPT_FAILS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"attempt_fails"
argument_list|)
decl_stmt|;
DECL|field|IN_ERRS
specifier|static
specifier|final
name|XContentBuilderString
name|IN_ERRS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"in_errs"
argument_list|)
decl_stmt|;
DECL|field|OUT_RSTS
specifier|static
specifier|final
name|XContentBuilderString
name|OUT_RSTS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"out_rsts"
argument_list|)
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
name|NETWORK
argument_list|)
expr_stmt|;
if|if
condition|(
name|tcp
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|TCP
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ACTIVE_OPENS
argument_list|,
name|tcp
operator|.
name|getActiveOpens
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PASSIVE_OPENS
argument_list|,
name|tcp
operator|.
name|getPassiveOpens
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|CURR_ESTAB
argument_list|,
name|tcp
operator|.
name|getCurrEstab
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|IN_SEGS
argument_list|,
name|tcp
operator|.
name|getInSegs
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|OUT_SEGS
argument_list|,
name|tcp
operator|.
name|getOutSegs
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|RETRANS_SEGS
argument_list|,
name|tcp
operator|.
name|getRetransSegs
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ESTAB_RESETS
argument_list|,
name|tcp
operator|.
name|getEstabResets
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ATTEMPT_FAILS
argument_list|,
name|tcp
operator|.
name|getAttemptFails
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|IN_ERRS
argument_list|,
name|tcp
operator|.
name|getInErrs
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|OUT_RSTS
argument_list|,
name|tcp
operator|.
name|getOutRsts
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|readNetworkStats
specifier|public
specifier|static
name|NetworkStats
name|readNetworkStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|NetworkStats
name|stats
init|=
operator|new
name|NetworkStats
argument_list|()
decl_stmt|;
name|stats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|stats
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
name|timestamp
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|tcp
operator|=
name|Tcp
operator|.
name|readNetworkTcp
argument_list|(
name|in
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
name|writeVLong
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
if|if
condition|(
name|tcp
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tcp
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|timestamp
specifier|public
name|long
name|timestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
DECL|method|getTimestamp
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
argument_list|()
return|;
block|}
DECL|method|tcp
specifier|public
name|Tcp
name|tcp
parameter_list|()
block|{
return|return
name|tcp
return|;
block|}
DECL|method|getTcp
specifier|public
name|Tcp
name|getTcp
parameter_list|()
block|{
return|return
name|tcp
argument_list|()
return|;
block|}
DECL|class|Tcp
specifier|public
specifier|static
class|class
name|Tcp
implements|implements
name|Streamable
block|{
DECL|field|activeOpens
name|long
name|activeOpens
decl_stmt|;
DECL|field|passiveOpens
name|long
name|passiveOpens
decl_stmt|;
DECL|field|attemptFails
name|long
name|attemptFails
decl_stmt|;
DECL|field|estabResets
name|long
name|estabResets
decl_stmt|;
DECL|field|currEstab
name|long
name|currEstab
decl_stmt|;
DECL|field|inSegs
name|long
name|inSegs
decl_stmt|;
DECL|field|outSegs
name|long
name|outSegs
decl_stmt|;
DECL|field|retransSegs
name|long
name|retransSegs
decl_stmt|;
DECL|field|inErrs
name|long
name|inErrs
decl_stmt|;
DECL|field|outRsts
name|long
name|outRsts
decl_stmt|;
DECL|method|readNetworkTcp
specifier|public
specifier|static
name|Tcp
name|readNetworkTcp
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Tcp
name|tcp
init|=
operator|new
name|Tcp
argument_list|()
decl_stmt|;
name|tcp
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|tcp
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
name|activeOpens
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|passiveOpens
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|attemptFails
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|estabResets
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|currEstab
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|inSegs
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|outSegs
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|retransSegs
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|inErrs
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|outRsts
operator|=
name|in
operator|.
name|readLong
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
name|activeOpens
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|passiveOpens
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|attemptFails
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|estabResets
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|currEstab
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|inSegs
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|outSegs
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|retransSegs
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|inErrs
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|outRsts
argument_list|)
expr_stmt|;
block|}
DECL|method|activeOpens
specifier|public
name|long
name|activeOpens
parameter_list|()
block|{
return|return
name|this
operator|.
name|activeOpens
return|;
block|}
DECL|method|getActiveOpens
specifier|public
name|long
name|getActiveOpens
parameter_list|()
block|{
return|return
name|activeOpens
argument_list|()
return|;
block|}
DECL|method|passiveOpens
specifier|public
name|long
name|passiveOpens
parameter_list|()
block|{
return|return
name|passiveOpens
return|;
block|}
DECL|method|getPassiveOpens
specifier|public
name|long
name|getPassiveOpens
parameter_list|()
block|{
return|return
name|passiveOpens
argument_list|()
return|;
block|}
DECL|method|attemptFails
specifier|public
name|long
name|attemptFails
parameter_list|()
block|{
return|return
name|attemptFails
return|;
block|}
DECL|method|getAttemptFails
specifier|public
name|long
name|getAttemptFails
parameter_list|()
block|{
return|return
name|attemptFails
argument_list|()
return|;
block|}
DECL|method|estabResets
specifier|public
name|long
name|estabResets
parameter_list|()
block|{
return|return
name|estabResets
return|;
block|}
DECL|method|getEstabResets
specifier|public
name|long
name|getEstabResets
parameter_list|()
block|{
return|return
name|estabResets
argument_list|()
return|;
block|}
DECL|method|currEstab
specifier|public
name|long
name|currEstab
parameter_list|()
block|{
return|return
name|currEstab
return|;
block|}
DECL|method|getCurrEstab
specifier|public
name|long
name|getCurrEstab
parameter_list|()
block|{
return|return
name|currEstab
argument_list|()
return|;
block|}
DECL|method|inSegs
specifier|public
name|long
name|inSegs
parameter_list|()
block|{
return|return
name|inSegs
return|;
block|}
DECL|method|getInSegs
specifier|public
name|long
name|getInSegs
parameter_list|()
block|{
return|return
name|inSegs
argument_list|()
return|;
block|}
DECL|method|outSegs
specifier|public
name|long
name|outSegs
parameter_list|()
block|{
return|return
name|outSegs
return|;
block|}
DECL|method|getOutSegs
specifier|public
name|long
name|getOutSegs
parameter_list|()
block|{
return|return
name|outSegs
argument_list|()
return|;
block|}
DECL|method|retransSegs
specifier|public
name|long
name|retransSegs
parameter_list|()
block|{
return|return
name|retransSegs
return|;
block|}
DECL|method|getRetransSegs
specifier|public
name|long
name|getRetransSegs
parameter_list|()
block|{
return|return
name|retransSegs
argument_list|()
return|;
block|}
DECL|method|inErrs
specifier|public
name|long
name|inErrs
parameter_list|()
block|{
return|return
name|inErrs
return|;
block|}
DECL|method|getInErrs
specifier|public
name|long
name|getInErrs
parameter_list|()
block|{
return|return
name|inErrs
argument_list|()
return|;
block|}
DECL|method|outRsts
specifier|public
name|long
name|outRsts
parameter_list|()
block|{
return|return
name|outRsts
return|;
block|}
DECL|method|getOutRsts
specifier|public
name|long
name|getOutRsts
parameter_list|()
block|{
return|return
name|outRsts
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

