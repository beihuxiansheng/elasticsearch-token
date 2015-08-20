begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.percolator.stats
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|percolator
operator|.
name|stats
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
name|unit
operator|.
name|ByteSizeValue
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
name|unit
operator|.
name|TimeValue
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
comment|/**  * Exposes percolator related statistics.  */
end_comment

begin_class
DECL|class|PercolateStats
specifier|public
class|class
name|PercolateStats
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|field|percolateCount
specifier|private
name|long
name|percolateCount
decl_stmt|;
DECL|field|percolateTimeInMillis
specifier|private
name|long
name|percolateTimeInMillis
decl_stmt|;
DECL|field|current
specifier|private
name|long
name|current
decl_stmt|;
DECL|field|memorySizeInBytes
specifier|private
name|long
name|memorySizeInBytes
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|numQueries
specifier|private
name|long
name|numQueries
decl_stmt|;
comment|/**      * Noop constructor for serialazation purposes.      */
DECL|method|PercolateStats
specifier|public
name|PercolateStats
parameter_list|()
block|{     }
DECL|method|PercolateStats
name|PercolateStats
parameter_list|(
name|long
name|percolateCount
parameter_list|,
name|long
name|percolateTimeInMillis
parameter_list|,
name|long
name|current
parameter_list|,
name|long
name|memorySizeInBytes
parameter_list|,
name|long
name|numQueries
parameter_list|)
block|{
name|this
operator|.
name|percolateCount
operator|=
name|percolateCount
expr_stmt|;
name|this
operator|.
name|percolateTimeInMillis
operator|=
name|percolateTimeInMillis
expr_stmt|;
name|this
operator|.
name|current
operator|=
name|current
expr_stmt|;
name|this
operator|.
name|memorySizeInBytes
operator|=
name|memorySizeInBytes
expr_stmt|;
name|this
operator|.
name|numQueries
operator|=
name|numQueries
expr_stmt|;
block|}
comment|/**      * @return The number of times the percolate api has been invoked.      */
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|percolateCount
return|;
block|}
comment|/**      * @return The total amount of time spend in the percolate api      */
DECL|method|getTimeInMillis
specifier|public
name|long
name|getTimeInMillis
parameter_list|()
block|{
return|return
name|percolateTimeInMillis
return|;
block|}
comment|/**      * @return The total amount of time spend in the percolate api      */
DECL|method|getTime
specifier|public
name|TimeValue
name|getTime
parameter_list|()
block|{
return|return
operator|new
name|TimeValue
argument_list|(
name|getTimeInMillis
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @return The total amount of active percolate api invocations.      */
DECL|method|getCurrent
specifier|public
name|long
name|getCurrent
parameter_list|()
block|{
return|return
name|current
return|;
block|}
comment|/**      * @return The total number of loaded percolate queries.      */
DECL|method|getNumQueries
specifier|public
name|long
name|getNumQueries
parameter_list|()
block|{
return|return
name|numQueries
return|;
block|}
comment|/**      * @return Temporarily returns<code>-1</code>, but this used to return the total size the loaded queries take in      * memory, but this is disabled now because the size estimation was too expensive cpu wise. This will be enabled      * again when a cheaper size estimation can be found.      */
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
return|return
name|memorySizeInBytes
return|;
block|}
comment|/**      * @return The total size the loaded queries take in memory.      */
DECL|method|getMemorySize
specifier|public
name|ByteSizeValue
name|getMemorySize
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|memorySizeInBytes
argument_list|)
return|;
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
name|PERCOLATE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL
argument_list|,
name|percolateCount
argument_list|)
expr_stmt|;
name|builder
operator|.
name|timeValueField
argument_list|(
name|Fields
operator|.
name|TIME_IN_MILLIS
argument_list|,
name|Fields
operator|.
name|TIME
argument_list|,
name|percolateTimeInMillis
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|CURRENT
argument_list|,
name|current
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MEMORY_SIZE_IN_BYTES
argument_list|,
name|memorySizeInBytes
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MEMORY_SIZE
argument_list|,
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|QUERIES
argument_list|,
name|getNumQueries
argument_list|()
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
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|PercolateStats
name|percolate
parameter_list|)
block|{
if|if
condition|(
name|percolate
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|percolateCount
operator|+=
name|percolate
operator|.
name|getCount
argument_list|()
expr_stmt|;
name|percolateTimeInMillis
operator|+=
name|percolate
operator|.
name|getTimeInMillis
argument_list|()
expr_stmt|;
name|current
operator|+=
name|percolate
operator|.
name|getCurrent
argument_list|()
expr_stmt|;
name|numQueries
operator|+=
name|percolate
operator|.
name|getNumQueries
argument_list|()
expr_stmt|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|PERCOLATE
specifier|static
specifier|final
name|XContentBuilderString
name|PERCOLATE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"percolate"
argument_list|)
decl_stmt|;
DECL|field|TOTAL
specifier|static
specifier|final
name|XContentBuilderString
name|TOTAL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"total"
argument_list|)
decl_stmt|;
DECL|field|TIME
specifier|static
specifier|final
name|XContentBuilderString
name|TIME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"time"
argument_list|)
decl_stmt|;
DECL|field|TIME_IN_MILLIS
specifier|static
specifier|final
name|XContentBuilderString
name|TIME_IN_MILLIS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"time_in_millis"
argument_list|)
decl_stmt|;
DECL|field|CURRENT
specifier|static
specifier|final
name|XContentBuilderString
name|CURRENT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"current"
argument_list|)
decl_stmt|;
DECL|field|MEMORY_SIZE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|MEMORY_SIZE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"memory_size_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|MEMORY_SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|MEMORY_SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"memory_size"
argument_list|)
decl_stmt|;
DECL|field|QUERIES
specifier|static
specifier|final
name|XContentBuilderString
name|QUERIES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"queries"
argument_list|)
decl_stmt|;
block|}
DECL|method|readPercolateStats
specifier|public
specifier|static
name|PercolateStats
name|readPercolateStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|PercolateStats
name|stats
init|=
operator|new
name|PercolateStats
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
name|percolateCount
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|percolateTimeInMillis
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|current
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|numQueries
operator|=
name|in
operator|.
name|readVLong
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
name|writeVLong
argument_list|(
name|percolateCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|percolateTimeInMillis
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|numQueries
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
