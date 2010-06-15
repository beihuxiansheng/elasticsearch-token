begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_comment
comment|/*  * Written by Cliff Click and released to the public domain, as explained at  * http://creativecommons.org/licenses/publicdomain  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util.concurrent.highscalelib
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|highscalelib
package|;
end_package

begin_comment
comment|/**  * A simple high-performance counter.  Merely renames the extended {@link  * org.cliffc.high_scale_lib.ConcurrentAutoTable} class to be more obvious.  * {@link org.cliffc.high_scale_lib.ConcurrentAutoTable} already has a decent  * counting API.  *  * @author Cliff Click  * @since 1.5  */
end_comment

begin_class
DECL|class|Counter
specifier|public
class|class
name|Counter
extends|extends
name|ConcurrentAutoTable
block|{
comment|// Add the given value to current counter value.  Concurrent updates will
comment|// not be lost, but addAndGet or getAndAdd are not implemented because but
comment|// the total counter value is not atomically updated.
comment|//public void add( long x );
comment|//public void decrement();
comment|//public void increment();
comment|// Current value of the counter.  Since other threads are updating furiously
comment|// the value is only approximate, but it includes all counts made by the
comment|// current thread.  Requires a pass over all the striped counters.
comment|//public long get();
comment|//public int  intValue();
comment|//public long longValue();
comment|// A cheaper 'get'.  Updated only once/millisecond, but fast as a simple
comment|// load instruction when not updating.
comment|//public long estimate_get( );
block|}
end_class

end_unit

