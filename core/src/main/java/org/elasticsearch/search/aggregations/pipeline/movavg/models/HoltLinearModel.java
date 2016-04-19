begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.pipeline.movavg.models
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|pipeline
operator|.
name|movavg
operator|.
name|models
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
name|Nullable
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
name|ParseField
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
name|ParseFieldMatcher
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
name|search
operator|.
name|aggregations
operator|.
name|pipeline
operator|.
name|movavg
operator|.
name|MovAvgPipelineAggregatorBuilder
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
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Calculate a doubly exponential weighted moving average  */
end_comment

begin_class
DECL|class|HoltLinearModel
specifier|public
class|class
name|HoltLinearModel
extends|extends
name|MovAvgModel
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"holt"
decl_stmt|;
DECL|field|NAME_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|NAME_FIELD
init|=
operator|new
name|ParseField
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_ALPHA
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_ALPHA
init|=
literal|0.3
decl_stmt|;
DECL|field|DEFAULT_BETA
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_BETA
init|=
literal|0.1
decl_stmt|;
comment|/**      * Controls smoothing of data.  Also known as "level" value.      * Alpha = 1 retains no memory of past values      * (e.g. random walk), while alpha = 0 retains infinite memory of past values (e.g.      * mean of the series).      */
DECL|field|alpha
specifier|private
specifier|final
name|double
name|alpha
decl_stmt|;
comment|/**      * Controls smoothing of trend.      * Beta = 1 retains no memory of past values      * (e.g. random walk), while alpha = 0 retains infinite memory of past values (e.g.      * mean of the series).      */
DECL|field|beta
specifier|private
specifier|final
name|double
name|beta
decl_stmt|;
DECL|method|HoltLinearModel
specifier|public
name|HoltLinearModel
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_ALPHA
argument_list|,
name|DEFAULT_BETA
argument_list|)
expr_stmt|;
block|}
DECL|method|HoltLinearModel
specifier|public
name|HoltLinearModel
parameter_list|(
name|double
name|alpha
parameter_list|,
name|double
name|beta
parameter_list|)
block|{
name|this
operator|.
name|alpha
operator|=
name|alpha
expr_stmt|;
name|this
operator|.
name|beta
operator|=
name|beta
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|HoltLinearModel
specifier|public
name|HoltLinearModel
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|alpha
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|beta
operator|=
name|in
operator|.
name|readDouble
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
name|writeDouble
argument_list|(
name|alpha
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|beta
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|canBeMinimized
specifier|public
name|boolean
name|canBeMinimized
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|neighboringModel
specifier|public
name|MovAvgModel
name|neighboringModel
parameter_list|()
block|{
name|double
name|newValue
init|=
name|Math
operator|.
name|random
argument_list|()
decl_stmt|;
switch|switch
condition|(
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|random
argument_list|()
operator|*
literal|2
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
operator|new
name|HoltLinearModel
argument_list|(
name|newValue
argument_list|,
name|this
operator|.
name|beta
argument_list|)
return|;
case|case
literal|1
case|:
return|return
operator|new
name|HoltLinearModel
argument_list|(
name|this
operator|.
name|alpha
argument_list|,
name|newValue
argument_list|)
return|;
default|default:
assert|assert
operator|(
literal|false
operator|)
operator|:
literal|"Random value fell outside of range [0-1]"
assert|;
return|return
operator|new
name|HoltLinearModel
argument_list|(
name|newValue
argument_list|,
name|this
operator|.
name|beta
argument_list|)
return|;
comment|// This should never technically happen...
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|MovAvgModel
name|clone
parameter_list|()
block|{
return|return
operator|new
name|HoltLinearModel
argument_list|(
name|this
operator|.
name|alpha
argument_list|,
name|this
operator|.
name|beta
argument_list|)
return|;
block|}
comment|/**      * Predicts the next `n` values in the series, using the smoothing model to generate new values.      * Unlike the other moving averages, Holt-Linear has forecasting/prediction built into the algorithm.      * Prediction is more than simply adding the next prediction to the window and repeating.  Holt-Linear      * will extrapolate into the future by applying the trend information to the smoothed data.      *      * @param values            Collection of numerics to movingAvg, usually windowed      * @param numPredictions    Number of newly generated predictions to return      * @param<T>               Type of numeric      * @return                  Returns an array of doubles, since most smoothing methods operate on floating points      */
annotation|@
name|Override
DECL|method|doPredict
specifier|protected
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
name|double
index|[]
name|doPredict
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
return|return
name|next
argument_list|(
name|values
argument_list|,
name|numPredictions
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
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
block|{
return|return
name|next
argument_list|(
name|values
argument_list|,
literal|1
argument_list|)
index|[
literal|0
index|]
return|;
block|}
comment|/**      * Calculate a Holt-Linear (doubly exponential weighted) moving average      *      * @param values Collection of values to calculate avg for      * @param numForecasts number of forecasts into the future to return      *      * @param<T>    Type T extending Number      * @return       Returns a Double containing the moving avg for the window      */
DECL|method|next
specifier|public
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
name|double
index|[]
name|next
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|values
parameter_list|,
name|int
name|numForecasts
parameter_list|)
block|{
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
name|numForecasts
argument_list|)
return|;
block|}
comment|// Smoothed value
name|double
name|s
init|=
literal|0
decl_stmt|;
name|double
name|last_s
init|=
literal|0
decl_stmt|;
comment|// Trend value
name|double
name|b
init|=
literal|0
decl_stmt|;
name|double
name|last_b
init|=
literal|0
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|T
name|last
decl_stmt|;
for|for
control|(
name|T
name|v
range|:
name|values
control|)
block|{
name|last
operator|=
name|v
expr_stmt|;
if|if
condition|(
name|counter
operator|==
literal|1
condition|)
block|{
name|s
operator|=
name|v
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
name|b
operator|=
name|v
operator|.
name|doubleValue
argument_list|()
operator|-
name|last
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
name|alpha
operator|*
name|v
operator|.
name|doubleValue
argument_list|()
operator|+
operator|(
literal|1.0d
operator|-
name|alpha
operator|)
operator|*
operator|(
name|last_s
operator|+
name|last_b
operator|)
expr_stmt|;
name|b
operator|=
name|beta
operator|*
operator|(
name|s
operator|-
name|last_s
operator|)
operator|+
operator|(
literal|1
operator|-
name|beta
operator|)
operator|*
name|last_b
expr_stmt|;
block|}
name|counter
operator|+=
literal|1
expr_stmt|;
name|last_s
operator|=
name|s
expr_stmt|;
name|last_b
operator|=
name|b
expr_stmt|;
block|}
name|double
index|[]
name|forecastValues
init|=
operator|new
name|double
index|[
name|numForecasts
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numForecasts
condition|;
name|i
operator|++
control|)
block|{
name|forecastValues
index|[
name|i
index|]
operator|=
name|s
operator|+
operator|(
name|i
operator|*
name|b
operator|)
expr_stmt|;
block|}
return|return
name|forecastValues
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
name|field
argument_list|(
name|MovAvgPipelineAggregatorBuilder
operator|.
name|MODEL
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|NAME_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|MovAvgPipelineAggregatorBuilder
operator|.
name|SETTINGS
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"alpha"
argument_list|,
name|alpha
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"beta"
argument_list|,
name|beta
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
DECL|field|PARSER
specifier|public
specifier|static
specifier|final
name|AbstractModelParser
name|PARSER
init|=
operator|new
name|AbstractModelParser
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MovAvgModel
name|parse
parameter_list|(
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|settings
parameter_list|,
name|String
name|pipelineName
parameter_list|,
name|int
name|windowSize
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|)
throws|throws
name|ParseException
block|{
name|double
name|alpha
init|=
name|parseDoubleParam
argument_list|(
name|settings
argument_list|,
literal|"alpha"
argument_list|,
name|DEFAULT_ALPHA
argument_list|)
decl_stmt|;
name|double
name|beta
init|=
name|parseDoubleParam
argument_list|(
name|settings
argument_list|,
literal|"beta"
argument_list|,
name|DEFAULT_BETA
argument_list|)
decl_stmt|;
name|checkUnrecognizedParams
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
operator|new
name|HoltLinearModel
argument_list|(
name|alpha
argument_list|,
name|beta
argument_list|)
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|alpha
argument_list|,
name|beta
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|HoltLinearModel
name|other
init|=
operator|(
name|HoltLinearModel
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|alpha
argument_list|,
name|other
operator|.
name|alpha
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|beta
argument_list|,
name|other
operator|.
name|beta
argument_list|)
return|;
block|}
DECL|class|HoltLinearModelBuilder
specifier|public
specifier|static
class|class
name|HoltLinearModelBuilder
implements|implements
name|MovAvgModelBuilder
block|{
DECL|field|alpha
specifier|private
name|double
name|alpha
init|=
name|DEFAULT_ALPHA
decl_stmt|;
DECL|field|beta
specifier|private
name|double
name|beta
init|=
name|DEFAULT_BETA
decl_stmt|;
comment|/**          * Alpha controls the smoothing of the data.  Alpha = 1 retains no memory of past values          * (e.g. a random walk), while alpha = 0 retains infinite memory of past values (e.g.          * the series mean).  Useful values are somewhere in between.  Defaults to 0.5.          *          * @param alpha A double between 0-1 inclusive, controls data smoothing          *          * @return The builder to continue chaining          */
DECL|method|alpha
specifier|public
name|HoltLinearModelBuilder
name|alpha
parameter_list|(
name|double
name|alpha
parameter_list|)
block|{
name|this
operator|.
name|alpha
operator|=
name|alpha
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Equivalent to<code>alpha</code>, but controls the smoothing of the trend instead of the data          *          * @param beta a double between 0-1 inclusive, controls trend smoothing          *          * @return The builder to continue chaining          */
DECL|method|beta
specifier|public
name|HoltLinearModelBuilder
name|beta
parameter_list|(
name|double
name|beta
parameter_list|)
block|{
name|this
operator|.
name|beta
operator|=
name|beta
expr_stmt|;
return|return
name|this
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
name|field
argument_list|(
name|MovAvgPipelineAggregatorBuilder
operator|.
name|MODEL
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|NAME_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|MovAvgPipelineAggregatorBuilder
operator|.
name|SETTINGS
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"alpha"
argument_list|,
name|alpha
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"beta"
argument_list|,
name|beta
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
annotation|@
name|Override
DECL|method|build
specifier|public
name|MovAvgModel
name|build
parameter_list|()
block|{
return|return
operator|new
name|HoltLinearModel
argument_list|(
name|alpha
argument_list|,
name|beta
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

