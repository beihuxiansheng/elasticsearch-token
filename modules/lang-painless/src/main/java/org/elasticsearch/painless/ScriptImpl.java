begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
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
name|LeafReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ExecutableScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|SearchScript
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
name|lookup
operator|.
name|LeafSearchLookup
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
name|lookup
operator|.
name|SearchLookup
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|function
operator|.
name|DoubleSupplier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * ScriptImpl can be used as either an {@link ExecutableScript} or a {@link SearchScript}  * to run a previously compiled Painless script.  */
end_comment

begin_class
DECL|class|ScriptImpl
specifier|final
class|class
name|ScriptImpl
extends|extends
name|SearchScript
block|{
comment|/**      * The Painless script that can be run.      */
DECL|field|script
specifier|private
specifier|final
name|GenericElasticsearchScript
name|script
decl_stmt|;
comment|/**      * A map that can be used to access input parameters at run-time.      */
DECL|field|variables
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|variables
decl_stmt|;
comment|/**      * Looks up the {@code _score} from {@link #scorer} if {@code _score} is used, otherwise returns {@code 0.0}.      */
DECL|field|scoreLookup
specifier|private
specifier|final
name|DoubleSupplier
name|scoreLookup
decl_stmt|;
comment|/**      * Looks up the {@code ctx} from the {@link #variables} if {@code ctx} is used, otherwise return {@code null}.      */
DECL|field|ctxLookup
specifier|private
specifier|final
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|ctxLookup
decl_stmt|;
comment|/**      * Current _value for aggregation      * @see #setNextAggregationValue(Object)      */
DECL|field|aggregationValue
specifier|private
name|Object
name|aggregationValue
decl_stmt|;
comment|/**      * Creates a ScriptImpl for the a previously compiled Painless script.      * @param script The previously compiled Painless script.      * @param vars The initial variables to run the script with.      * @param lookup The lookup to allow search fields to be available if this is run as a search script.      */
DECL|method|ScriptImpl
name|ScriptImpl
parameter_list|(
name|GenericElasticsearchScript
name|script
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|,
name|SearchLookup
name|lookup
parameter_list|,
name|LeafReaderContext
name|leafContext
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|lookup
argument_list|,
name|leafContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
name|this
operator|.
name|variables
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
name|vars
operator|!=
literal|null
condition|)
block|{
name|variables
operator|.
name|putAll
argument_list|(
name|vars
argument_list|)
expr_stmt|;
block|}
name|LeafSearchLookup
name|leafLookup
init|=
name|getLeafLookup
argument_list|()
decl_stmt|;
if|if
condition|(
name|leafLookup
operator|!=
literal|null
condition|)
block|{
name|variables
operator|.
name|putAll
argument_list|(
name|leafLookup
operator|.
name|asMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|scoreLookup
operator|=
name|script
operator|.
name|needs_score
argument_list|()
condition|?
name|this
operator|::
name|getScore
else|:
parameter_list|()
lambda|->
literal|0.0
expr_stmt|;
name|ctxLookup
operator|=
name|script
operator|.
name|needsCtx
argument_list|()
condition|?
name|variables
lambda|->
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|variables
operator|.
name|get
argument_list|(
literal|"ctx"
argument_list|)
else|:
name|variables
lambda|->
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getParams
parameter_list|()
block|{
return|return
name|variables
return|;
block|}
annotation|@
name|Override
DECL|method|setNextVar
specifier|public
name|void
name|setNextVar
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Object
name|value
parameter_list|)
block|{
name|variables
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextAggregationValue
specifier|public
name|void
name|setNextAggregationValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|aggregationValue
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|Object
name|run
parameter_list|()
block|{
return|return
name|script
operator|.
name|execute
argument_list|(
name|variables
argument_list|,
name|scoreLookup
operator|.
name|getAsDouble
argument_list|()
argument_list|,
name|getDoc
argument_list|()
argument_list|,
name|aggregationValue
argument_list|,
name|ctxLookup
operator|.
name|apply
argument_list|(
name|variables
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|runAsDouble
specifier|public
name|double
name|runAsDouble
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|run
argument_list|()
operator|)
operator|.
name|doubleValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|runAsLong
specifier|public
name|long
name|runAsLong
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|run
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

