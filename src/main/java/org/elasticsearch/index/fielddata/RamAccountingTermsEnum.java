begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
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
name|index
operator|.
name|FilteredTermsEnum
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
name|index
operator|.
name|TermsEnum
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
name|BytesRef
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
name|fielddata
operator|.
name|plain
operator|.
name|AbstractIndexFieldData
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
name|breaker
operator|.
name|CircuitBreaker
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
comment|/**  * {@link TermsEnum} that takes a MemoryCircuitBreaker, increasing the breaker  * every time {@code .next(...)} is called. Proxies all methods to the original  * TermsEnum otherwise.  */
end_comment

begin_class
DECL|class|RamAccountingTermsEnum
specifier|public
specifier|final
class|class
name|RamAccountingTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
comment|// Flush every 5mb
DECL|field|FLUSH_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|long
name|FLUSH_BUFFER_SIZE
init|=
literal|1024
operator|*
literal|1024
operator|*
literal|5
decl_stmt|;
DECL|field|breaker
specifier|private
specifier|final
name|CircuitBreaker
name|breaker
decl_stmt|;
DECL|field|termsEnum
specifier|private
specifier|final
name|TermsEnum
name|termsEnum
decl_stmt|;
DECL|field|estimator
specifier|private
specifier|final
name|AbstractIndexFieldData
operator|.
name|PerValueEstimator
name|estimator
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|totalBytes
specifier|private
name|long
name|totalBytes
decl_stmt|;
DECL|field|flushBuffer
specifier|private
name|long
name|flushBuffer
decl_stmt|;
DECL|method|RamAccountingTermsEnum
specifier|public
name|RamAccountingTermsEnum
parameter_list|(
name|TermsEnum
name|termsEnum
parameter_list|,
name|CircuitBreaker
name|breaker
parameter_list|,
name|AbstractIndexFieldData
operator|.
name|PerValueEstimator
name|estimator
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|termsEnum
argument_list|)
expr_stmt|;
name|this
operator|.
name|breaker
operator|=
name|breaker
expr_stmt|;
name|this
operator|.
name|termsEnum
operator|=
name|termsEnum
expr_stmt|;
name|this
operator|.
name|estimator
operator|=
name|estimator
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|totalBytes
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|flushBuffer
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * Always accept the term.      */
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
comment|/**      * Flush the {@code flushBuffer} to the breaker, incrementing the total      * bytes and resetting the buffer.      */
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
block|{
name|breaker
operator|.
name|addEstimateBytesAndMaybeBreak
argument_list|(
name|this
operator|.
name|flushBuffer
argument_list|,
name|this
operator|.
name|fieldName
argument_list|)
expr_stmt|;
name|this
operator|.
name|totalBytes
operator|+=
name|this
operator|.
name|flushBuffer
expr_stmt|;
name|this
operator|.
name|flushBuffer
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * Proxy to the original next() call, but estimates the overhead of      * loading the next term.      */
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
operator|&&
name|this
operator|.
name|flushBuffer
operator|!=
literal|0
condition|)
block|{
comment|// We have reached the end of the termsEnum, flush the buffer
name|flush
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|flushBuffer
operator|+=
name|estimator
operator|.
name|bytesPerValue
argument_list|(
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|flushBuffer
operator|>=
name|FLUSH_BUFFER_SIZE
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|term
return|;
block|}
comment|/**      * @return the total number of bytes that have been aggregated      */
DECL|method|getTotalBytes
specifier|public
name|long
name|getTotalBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalBytes
return|;
block|}
block|}
end_class

end_unit

