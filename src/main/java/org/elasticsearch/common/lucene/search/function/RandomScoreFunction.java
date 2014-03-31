begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.search.function
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
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
name|AtomicReaderContext
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
name|search
operator|.
name|Explanation
import|;
end_import

begin_comment
comment|/**  * Pseudo randomly generate a score for each {@link #score}.  */
end_comment

begin_class
DECL|class|RandomScoreFunction
specifier|public
class|class
name|RandomScoreFunction
extends|extends
name|ScoreFunction
block|{
DECL|field|prng
specifier|private
specifier|final
name|PRNG
name|prng
decl_stmt|;
DECL|method|RandomScoreFunction
specifier|public
name|RandomScoreFunction
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|super
argument_list|(
name|CombineFunction
operator|.
name|MULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|prng
operator|=
operator|new
name|PRNG
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
comment|// intentionally does nothing
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|double
name|score
parameter_list|(
name|int
name|docId
parameter_list|,
name|float
name|subQueryScore
parameter_list|)
block|{
return|return
name|prng
operator|.
name|nextFloat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|explainScore
specifier|public
name|Explanation
name|explainScore
parameter_list|(
name|int
name|docId
parameter_list|,
name|Explanation
name|subQueryExpl
parameter_list|)
block|{
name|Explanation
name|exp
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|exp
operator|.
name|setDescription
argument_list|(
literal|"random score function (seed: "
operator|+
name|prng
operator|.
name|originalSeed
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|exp
operator|.
name|addDetail
argument_list|(
name|subQueryExpl
argument_list|)
expr_stmt|;
return|return
name|exp
return|;
block|}
comment|/**      * A non thread-safe PRNG      */
DECL|class|PRNG
specifier|static
class|class
name|PRNG
block|{
DECL|field|multiplier
specifier|private
specifier|static
specifier|final
name|long
name|multiplier
init|=
literal|0x5DEECE66DL
decl_stmt|;
DECL|field|addend
specifier|private
specifier|static
specifier|final
name|long
name|addend
init|=
literal|0xBL
decl_stmt|;
DECL|field|mask
specifier|private
specifier|static
specifier|final
name|long
name|mask
init|=
operator|(
literal|1L
operator|<<
literal|48
operator|)
operator|-
literal|1
decl_stmt|;
DECL|field|originalSeed
specifier|final
name|long
name|originalSeed
decl_stmt|;
DECL|field|seed
name|long
name|seed
decl_stmt|;
DECL|method|PRNG
name|PRNG
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|this
operator|.
name|originalSeed
operator|=
name|seed
expr_stmt|;
name|this
operator|.
name|seed
operator|=
operator|(
name|seed
operator|^
name|multiplier
operator|)
operator|&
name|mask
expr_stmt|;
block|}
DECL|method|nextFloat
specifier|public
name|float
name|nextFloat
parameter_list|()
block|{
name|seed
operator|=
operator|(
name|seed
operator|*
name|multiplier
operator|+
name|addend
operator|)
operator|&
name|mask
expr_stmt|;
return|return
name|seed
operator|/
call|(
name|float
call|)
argument_list|(
literal|1
operator|<<
literal|24
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

