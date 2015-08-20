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
name|StreamOutput
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
name|SearchParseException
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
name|Arrays
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

begin_class
DECL|class|MovAvgModel
specifier|public
specifier|abstract
class|class
name|MovAvgModel
block|{
comment|/**      * Should this model be fit to the data via a cost minimizing algorithm by default?      *      * @return      */
DECL|method|minimizeByDefault
specifier|public
name|boolean
name|minimizeByDefault
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Returns if the model can be cost minimized.  Not all models have parameters      * which can be tuned / optimized.      *      * @return      */
DECL|method|canBeMinimized
specifier|public
specifier|abstract
name|boolean
name|canBeMinimized
parameter_list|()
function_decl|;
comment|/**      * Generates a "neighboring" model, where one of the tunable parameters has been      * randomly mutated within the allowed range.  Used for minimization      *      * @return      */
DECL|method|neighboringModel
specifier|public
specifier|abstract
name|MovAvgModel
name|neighboringModel
parameter_list|()
function_decl|;
comment|/**      * Checks to see this model can produce a new value, without actually running the algo.      * This can be used for models that have certain preconditions that need to be met in order      * to short-circuit execution      *      * @param valuesAvailable Number of values in the current window of values      * @return                Returns `true` if calling next() will produce a value, `false` otherwise      */
DECL|method|hasValue
specifier|public
name|boolean
name|hasValue
parameter_list|(
name|int
name|valuesAvailable
parameter_list|)
block|{
comment|// Default implementation can always provide a next() value
return|return
name|valuesAvailable
operator|>
literal|0
return|;
block|}
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
comment|/**      * Predicts the next `n` values in the series.      *      * @param values            Collection of numerics to movingAvg, usually windowed      * @param numPredictions    Number of newly generated predictions to return      * @param<T>               Type of numeric      * @return                  Returns an array of doubles, since most smoothing methods operate on floating points      */
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
assert|assert
operator|(
name|numPredictions
operator|>=
literal|1
operator|)
assert|;
comment|// If there are no values, we can't do anything.  Return an array of NaNs.
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|emptyPredictions
argument_list|(
name|numPredictions
argument_list|)
return|;
block|}
return|return
name|doPredict
argument_list|(
name|values
argument_list|,
name|numPredictions
argument_list|)
return|;
block|}
comment|/**      * Calls to the model-specific implementation which actually generates the predictions      *      * @param values            Collection of numerics to movingAvg, usually windowed      * @param numPredictions    Number of newly generated predictions to return      * @param<T>               Type of numeric      * @return                  Returns an array of doubles, since most smoothing methods operate on floating points      */
DECL|method|doPredict
specifier|protected
specifier|abstract
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
function_decl|;
comment|/**      * Returns an empty set of predictions, filled with NaNs      * @param numPredictions Number of empty predictions to generate      * @return      */
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
comment|/**      * Clone the model, returning an exact copy      *      * @return      */
DECL|method|clone
specifier|public
specifier|abstract
name|MovAvgModel
name|clone
parameter_list|()
function_decl|;
comment|/**      * Abstract class which also provides some concrete parsing functionality.      */
DECL|class|AbstractModelParser
specifier|public
specifier|abstract
specifier|static
class|class
name|AbstractModelParser
block|{
comment|/**          * Returns the name of the model          *          * @return The model's name          */
DECL|method|getName
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**          * Parse a settings hash that is specific to this model          *          * @param settings           Map of settings, extracted from the request          * @param pipelineName       Name of the parent pipeline agg          * @param windowSize         Size of the window for this moving avg          * @param parseFieldMatcher  Matcher for field names          * @return                   A fully built moving average model          */
DECL|method|parse
specifier|public
specifier|abstract
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
function_decl|;
comment|/**          * Extracts a 0-1 inclusive double from the settings map, otherwise throws an exception          *          * @param settings      Map of settings provided to this model          * @param name          Name of parameter we are attempting to extract          * @param defaultValue  Default value to be used if value does not exist in map          *          * @throws ParseException          *          * @return Double value extracted from settings map          */
DECL|method|parseDoubleParam
specifier|protected
name|double
name|parseDoubleParam
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
name|name
parameter_list|,
name|double
name|defaultValue
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|settings
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
name|Object
name|value
init|=
name|settings
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
name|double
name|v
init|=
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|>=
literal|0
operator|&&
name|v
operator|<=
literal|1
condition|)
block|{
name|settings
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|v
return|;
block|}
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Parameter ["
operator|+
name|name
operator|+
literal|"] must be between 0-1 inclusive.  Provided"
operator|+
literal|"value was ["
operator|+
name|v
operator|+
literal|"]"
argument_list|,
literal|0
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Parameter ["
operator|+
name|name
operator|+
literal|"] must be a double, type `"
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"` provided instead"
argument_list|,
literal|0
argument_list|)
throw|;
block|}
comment|/**          * Extracts an integer from the settings map, otherwise throws an exception          *          * @param settings      Map of settings provided to this model          * @param name          Name of parameter we are attempting to extract          * @param defaultValue  Default value to be used if value does not exist in map          *          * @throws ParseException          *          * @return Integer value extracted from settings map          */
DECL|method|parseIntegerParam
specifier|protected
name|int
name|parseIntegerParam
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
name|name
parameter_list|,
name|int
name|defaultValue
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|settings
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
name|Object
name|value
init|=
name|settings
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
name|settings
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
return|;
block|}
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Parameter ["
operator|+
name|name
operator|+
literal|"] must be an integer, type `"
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"` provided instead"
argument_list|,
literal|0
argument_list|)
throw|;
block|}
comment|/**          * Extracts a boolean from the settings map, otherwise throws an exception          *          * @param settings      Map of settings provided to this model          * @param name          Name of parameter we are attempting to extract          * @param defaultValue  Default value to be used if value does not exist in map          *          * @throws SearchParseException          *          * @return Boolean value extracted from settings map          */
DECL|method|parseBoolParam
specifier|protected
name|boolean
name|parseBoolParam
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
name|name
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|settings
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
name|Object
name|value
init|=
name|settings
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Boolean
condition|)
block|{
name|settings
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
operator|(
name|Boolean
operator|)
name|value
return|;
block|}
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Parameter ["
operator|+
name|name
operator|+
literal|"] must be a boolean, type `"
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"` provided instead"
argument_list|,
literal|0
argument_list|)
throw|;
block|}
DECL|method|checkUnrecognizedParams
specifier|protected
name|void
name|checkUnrecognizedParams
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
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|settings
operator|!=
literal|null
operator|&&
name|settings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Unrecognized parameter(s): ["
operator|+
name|settings
operator|.
name|keySet
argument_list|()
operator|+
literal|"]"
argument_list|,
literal|0
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit
