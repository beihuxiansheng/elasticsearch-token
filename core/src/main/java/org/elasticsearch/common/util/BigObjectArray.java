begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ArrayUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|RamUsageEstimator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|util
operator|.
name|BigArrays
operator|.
name|OBJECT_PAGE_SIZE
import|;
end_import

begin_comment
comment|/**  * Int array abstraction able to support more than 2B values. This implementation slices data into fixed-sized blocks of  * configurable length.  */
end_comment

begin_class
DECL|class|BigObjectArray
specifier|final
class|class
name|BigObjectArray
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractBigArray
implements|implements
name|ObjectArray
argument_list|<
name|T
argument_list|>
block|{
DECL|field|pages
specifier|private
name|Object
index|[]
index|[]
name|pages
decl_stmt|;
comment|/** Constructor. */
DECL|method|BigObjectArray
specifier|public
name|BigObjectArray
parameter_list|(
name|long
name|size
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|)
block|{
name|super
argument_list|(
name|OBJECT_PAGE_SIZE
argument_list|,
name|bigArrays
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|pages
operator|=
operator|new
name|Object
index|[
name|numPages
argument_list|(
name|size
argument_list|)
index|]
index|[]
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
name|pages
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|pages
index|[
name|i
index|]
operator|=
name|newObjectPage
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
specifier|final
name|int
name|pageIndex
init|=
name|pageIndex
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|int
name|indexInPage
init|=
name|indexInPage
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
operator|(
name|T
operator|)
name|pages
index|[
name|pageIndex
index|]
index|[
name|indexInPage
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|T
name|set
parameter_list|(
name|long
name|index
parameter_list|,
name|T
name|value
parameter_list|)
block|{
specifier|final
name|int
name|pageIndex
init|=
name|pageIndex
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|int
name|indexInPage
init|=
name|indexInPage
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|Object
index|[]
name|page
init|=
name|pages
index|[
name|pageIndex
index|]
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|T
name|ret
init|=
operator|(
name|T
operator|)
name|page
index|[
name|indexInPage
index|]
decl_stmt|;
name|page
index|[
name|indexInPage
index|]
operator|=
name|value
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|numBytesPerElement
specifier|protected
name|int
name|numBytesPerElement
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
return|;
block|}
comment|/** Change the size of this array. Content between indexes<code>0</code> and<code>min(size(), newSize)</code> will be preserved. */
annotation|@
name|Override
DECL|method|resize
specifier|public
name|void
name|resize
parameter_list|(
name|long
name|newSize
parameter_list|)
block|{
specifier|final
name|int
name|numPages
init|=
name|numPages
argument_list|(
name|newSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|numPages
operator|>
name|pages
operator|.
name|length
condition|)
block|{
name|pages
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|pages
argument_list|,
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|numPages
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|numPages
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
operator|&&
name|pages
index|[
name|i
index|]
operator|==
literal|null
condition|;
operator|--
name|i
control|)
block|{
name|pages
index|[
name|i
index|]
operator|=
name|newObjectPage
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|numPages
init|;
name|i
operator|<
name|pages
operator|.
name|length
operator|&&
name|pages
index|[
name|i
index|]
operator|!=
literal|null
condition|;
operator|++
name|i
control|)
block|{
name|pages
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
name|releasePage
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|size
operator|=
name|newSize
expr_stmt|;
block|}
block|}
end_class

end_unit
