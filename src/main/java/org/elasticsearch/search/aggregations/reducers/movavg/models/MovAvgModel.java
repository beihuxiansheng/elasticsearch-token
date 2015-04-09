begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.reducers.movavg.models
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|reducers
operator|.
name|movavg
operator|.
name|models
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|EvictingQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_class
DECL|class|MovAvgModel
specifier|public
specifier|abstract
class|class
name|MovAvgModel
block|{
comment|/**      * Returns the next value in the series, according to the underlying smoothing model      *      * @param values    Collection of numerics to movingAvg, usually windowed      * @param<T>       Type of numeric      * @return          Returns a double, since most smoothing methods operate on floating points      */
DECL|method|next
specifier|public
specifier|abstract
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
name|double
name|next
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|values
parameter_list|)
function_decl|;
comment|/**      * Predicts the next `n` values in the series, using the smoothing model to generate new values.      * Default prediction mode is to simply continuing calling<code>next()</code> and adding the      * predicted value back into the windowed buffer.      *      * @param values            Collection of numerics to movingAvg, usually windowed      * @param numPredictions    Number of newly generated predictions to return      * @param<T>               Type of numeric      * @return                  Returns an array of doubles, since most smoothing methods operate on floating points      */
DECL|method|predict
specifier|public
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
name|double
index|[]
name|predict
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|values
parameter_list|,
name|int
name|numPredictions
parameter_list|)
block|{
name|double
index|[]
name|predictions
init|=
operator|new
name|double
index|[
name|numPredictions
index|]
decl_stmt|;
comment|// If there are no values, we can't do anything.  Return an array of NaNs.
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|emptyPredictions
argument_list|(
name|numPredictions
argument_list|)
return|;
block|}
comment|// special case for one prediction, avoids allocation
if|if
condition|(
name|numPredictions
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"numPredictions may not be less than 1."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|numPredictions
operator|==
literal|1
condition|)
block|{
name|predictions
index|[
literal|0
index|]
operator|=
name|next
argument_list|(
name|values
argument_list|)
expr_stmt|;
return|return
name|predictions
return|;
block|}
comment|// nocommit
comment|// I don't like that it creates a new queue here
comment|// The alternative to this is to just use `values` directly, but that would "consume" values
comment|// and potentially change state elsewhere.  Maybe ok?
name|Collection
argument_list|<
name|Number
argument_list|>
name|predictionBuffer
init|=
name|EvictingQueue
operator|.
name|create
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|predictionBuffer
operator|.
name|addAll
argument_list|(
name|values
argument_list|)
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
name|numPredictions
condition|;
name|i
operator|++
control|)
block|{
name|predictions
index|[
name|i
index|]
operator|=
name|next
argument_list|(
name|predictionBuffer
argument_list|)
expr_stmt|;
comment|// Add the last value to the buffer, so we can keep predicting
name|predictionBuffer
operator|.
name|add
argument_list|(
name|predictions
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|predictions
return|;
block|}
DECL|method|emptyPredictions
specifier|protected
name|double
index|[]
name|emptyPredictions
parameter_list|(
name|int
name|numPredictions
parameter_list|)
block|{
name|double
index|[]
name|predictions
init|=
operator|new
name|double
index|[
name|numPredictions
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|predictions
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
expr_stmt|;
return|return
name|predictions
return|;
block|}
comment|/**      * Write the model to the output stream      *      * @param out   Output stream      * @throws IOException      */
DECL|method|writeTo
specifier|public
specifier|abstract
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

