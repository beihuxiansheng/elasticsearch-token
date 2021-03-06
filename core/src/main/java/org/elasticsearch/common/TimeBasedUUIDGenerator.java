begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Base64
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/** These are essentially flake ids (http://boundary.com/blog/2012/01/12/flake-a-decentralized-k-ordered-unique-id-generator-in-erlang) but  *  we use 6 (not 8) bytes for timestamp, and use 3 (not 2) bytes for sequence number. */
end_comment

begin_class
DECL|class|TimeBasedUUIDGenerator
class|class
name|TimeBasedUUIDGenerator
implements|implements
name|UUIDGenerator
block|{
comment|// We only use bottom 3 bytes for the sequence number.  Paranoia: init with random int so that if JVM/OS/machine goes down, clock slips
comment|// backwards, and JVM comes back up, we are less likely to be on the same sequenceNumber at the same time:
DECL|field|sequenceNumber
specifier|private
specifier|final
name|AtomicInteger
name|sequenceNumber
init|=
operator|new
name|AtomicInteger
argument_list|(
name|SecureRandomHolder
operator|.
name|INSTANCE
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
comment|// Used to ensure clock moves forward:
DECL|field|lastTimestamp
specifier|private
name|long
name|lastTimestamp
decl_stmt|;
DECL|field|SECURE_MUNGED_ADDRESS
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|SECURE_MUNGED_ADDRESS
init|=
name|MacAddressProvider
operator|.
name|getSecureMungedAddress
argument_list|()
decl_stmt|;
static|static
block|{
assert|assert
name|SECURE_MUNGED_ADDRESS
operator|.
name|length
operator|==
literal|6
assert|;
block|}
comment|/** Puts the lower numberOfLongBytes from l into the array, starting index pos. */
DECL|method|putLong
specifier|private
specifier|static
name|void
name|putLong
parameter_list|(
name|byte
index|[]
name|array
parameter_list|,
name|long
name|l
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|numberOfLongBytes
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numberOfLongBytes
condition|;
operator|++
name|i
control|)
block|{
name|array
index|[
name|pos
operator|+
name|numberOfLongBytes
operator|-
name|i
operator|-
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|l
operator|>>>
operator|(
name|i
operator|*
literal|8
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getBase64UUID
specifier|public
name|String
name|getBase64UUID
parameter_list|()
block|{
specifier|final
name|int
name|sequenceId
init|=
name|sequenceNumber
operator|.
name|incrementAndGet
argument_list|()
operator|&
literal|0xffffff
decl_stmt|;
name|long
name|timestamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// Don't let timestamp go backwards, at least "on our watch" (while this JVM is running).  We are still vulnerable if we are
comment|// shut down, clock goes backwards, and we restart... for this we randomize the sequenceNumber on init to decrease chance of
comment|// collision:
name|timestamp
operator|=
name|Math
operator|.
name|max
argument_list|(
name|lastTimestamp
argument_list|,
name|timestamp
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequenceId
operator|==
literal|0
condition|)
block|{
comment|// Always force the clock to increment whenever sequence number is 0, in case we have a long time-slip backwards:
name|timestamp
operator|++
expr_stmt|;
block|}
name|lastTimestamp
operator|=
name|timestamp
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|uuidBytes
init|=
operator|new
name|byte
index|[
literal|15
index|]
decl_stmt|;
comment|// Only use lower 6 bytes of the timestamp (this will suffice beyond the year 10000):
name|putLong
argument_list|(
name|uuidBytes
argument_list|,
name|timestamp
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
expr_stmt|;
comment|// MAC address adds 6 bytes:
name|System
operator|.
name|arraycopy
argument_list|(
name|SECURE_MUNGED_ADDRESS
argument_list|,
literal|0
argument_list|,
name|uuidBytes
argument_list|,
literal|6
argument_list|,
name|SECURE_MUNGED_ADDRESS
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Sequence number adds 3 bytes:
name|putLong
argument_list|(
name|uuidBytes
argument_list|,
name|sequenceId
argument_list|,
literal|12
argument_list|,
literal|3
argument_list|)
expr_stmt|;
assert|assert
literal|9
operator|+
name|SECURE_MUNGED_ADDRESS
operator|.
name|length
operator|==
name|uuidBytes
operator|.
name|length
assert|;
return|return
name|Base64
operator|.
name|getUrlEncoder
argument_list|()
operator|.
name|withoutPadding
argument_list|()
operator|.
name|encodeToString
argument_list|(
name|uuidBytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

