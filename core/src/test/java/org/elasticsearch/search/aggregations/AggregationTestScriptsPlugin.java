begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
package|;
end_package

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
name|ScriptDocValues
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
name|MockScriptPlugin
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
name|Script
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
name|Function
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
operator|.
name|ScriptType
import|;
end_import

begin_comment
comment|/**  * This class contains various mocked scripts that are used in aggregations integration tests.  */
end_comment

begin_class
DECL|class|AggregationTestScriptsPlugin
specifier|public
class|class
name|AggregationTestScriptsPlugin
extends|extends
name|MockScriptPlugin
block|{
comment|// Equivalent to:
comment|//
comment|// List values = doc['values'].values;
comment|// double[] res = new double[values.size()];
comment|// for (int i = 0; i< res.length; i++) {
comment|//      res[i] = values.get(i) - dec;
comment|// };
comment|// return res;
DECL|field|DECREMENT_ALL_VALUES
specifier|public
specifier|static
specifier|final
name|Script
name|DECREMENT_ALL_VALUES
init|=
operator|new
name|Script
argument_list|(
literal|"decrement all values"
argument_list|,
name|ScriptType
operator|.
name|INLINE
argument_list|,
name|NAME
argument_list|,
name|singletonMap
argument_list|(
literal|"dec"
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|pluginScripts
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|>
name|pluginScripts
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|>
name|scripts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"20 - _value"
argument_list|,
name|vars
lambda|->
literal|20.0d
operator|-
operator|(
name|double
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"_value"
argument_list|)
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"_value - 1"
argument_list|,
name|vars
lambda|->
operator|(
name|double
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"_value"
argument_list|)
operator|-
literal|1
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"_value + 1"
argument_list|,
name|vars
lambda|->
operator|(
name|double
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"_value"
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"_value * -1"
argument_list|,
name|vars
lambda|->
operator|(
name|double
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"_value"
argument_list|)
operator|*
operator|-
literal|1
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"_value - dec"
argument_list|,
name|vars
lambda|->
block|{
name|double
name|value
init|=
operator|(
name|double
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"_value"
argument_list|)
decl_stmt|;
name|int
name|dec
init|=
operator|(
name|int
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"dec"
argument_list|)
decl_stmt|;
return|return
name|value
operator|-
name|dec
return|;
block|}
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"_value + inc"
argument_list|,
name|vars
lambda|->
block|{
name|double
name|value
init|=
operator|(
name|double
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"_value"
argument_list|)
decl_stmt|;
name|int
name|inc
init|=
operator|(
name|int
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"inc"
argument_list|)
decl_stmt|;
return|return
name|value
operator|+
name|inc
return|;
block|}
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"doc['value'].value"
argument_list|,
name|vars
lambda|->
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|doc
init|=
operator|(
name|Map
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
decl_stmt|;
return|return
name|doc
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"doc['value'].value - dec"
argument_list|,
name|vars
lambda|->
block|{
name|int
name|dec
init|=
operator|(
name|int
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"dec"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|doc
init|=
operator|(
name|Map
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
decl_stmt|;
name|ScriptDocValues
operator|.
name|Longs
name|value
init|=
operator|(
name|ScriptDocValues
operator|.
name|Longs
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
return|return
name|value
operator|.
name|getValue
argument_list|()
operator|-
name|dec
return|;
block|}
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"doc['value'].value + inc"
argument_list|,
name|vars
lambda|->
block|{
name|int
name|inc
init|=
operator|(
name|int
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"inc"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|doc
init|=
operator|(
name|Map
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
decl_stmt|;
name|ScriptDocValues
operator|.
name|Longs
name|value
init|=
operator|(
name|ScriptDocValues
operator|.
name|Longs
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
return|return
name|value
operator|.
name|getValue
argument_list|()
operator|+
name|inc
return|;
block|}
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"doc['values'].values"
argument_list|,
name|vars
lambda|->
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|doc
init|=
operator|(
name|Map
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
decl_stmt|;
return|return
name|doc
operator|.
name|get
argument_list|(
literal|"values"
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
name|DECREMENT_ALL_VALUES
operator|.
name|getScript
argument_list|()
argument_list|,
name|vars
lambda|->
block|{
name|int
name|dec
init|=
operator|(
name|int
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"dec"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|doc
init|=
operator|(
name|Map
operator|)
name|vars
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
decl_stmt|;
name|ScriptDocValues
operator|.
name|Longs
name|values
init|=
operator|(
name|ScriptDocValues
operator|.
name|Longs
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"values"
argument_list|)
decl_stmt|;
name|double
index|[]
name|res
init|=
operator|new
name|double
index|[
name|values
operator|.
name|size
argument_list|()
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
name|res
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|-
name|dec
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"[ doc['value'].value, doc['value'].value - dec ]"
argument_list|,
name|vars
lambda|->
block|{
name|Long
name|a
init|=
operator|(
operator|(
name|ScriptDocValues
operator|.
name|Longs
operator|)
name|scripts
operator|.
name|get
argument_list|(
literal|"doc['value'].value"
argument_list|)
operator|.
name|apply
argument_list|(
name|vars
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Long
name|b
init|=
operator|(
name|Long
operator|)
name|scripts
operator|.
name|get
argument_list|(
literal|"doc['value'].value - dec"
argument_list|)
operator|.
name|apply
argument_list|(
name|vars
argument_list|)
decl_stmt|;
return|return
operator|new
name|Long
index|[]
block|{
name|a
operator|,
name|b
block|}
empty_stmt|;
block|}
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
literal|"[ doc['value'].value, doc['value'].value + inc ]"
argument_list|,
name|vars
lambda|->
block|{
name|Long
name|a
init|=
operator|(
operator|(
name|ScriptDocValues
operator|.
name|Longs
operator|)
name|scripts
operator|.
name|get
argument_list|(
literal|"doc['value'].value"
argument_list|)
operator|.
name|apply
argument_list|(
name|vars
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Long
name|b
init|=
operator|(
name|Long
operator|)
name|scripts
operator|.
name|get
argument_list|(
literal|"doc['value'].value + inc"
argument_list|)
operator|.
name|apply
argument_list|(
name|vars
argument_list|)
decl_stmt|;
return|return
operator|new
name|Long
index|[]
block|{
name|a
operator|,
name|b
block|}
empty_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
name|scripts
return|;
block|}
block|}
end_class

end_unit

