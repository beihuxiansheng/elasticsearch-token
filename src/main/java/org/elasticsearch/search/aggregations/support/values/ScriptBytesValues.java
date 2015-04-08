begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.support.values
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
operator|.
name|values
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
name|search
operator|.
name|Scorer
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
name|lucene
operator|.
name|ScorerAware
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
name|SortedBinaryDocValues
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
name|SortingBinaryDocValues
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
name|LeafSearchScript
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
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

begin_comment
comment|/**  * {@link SortedBinaryDocValues} implementation that reads values from a script.  */
end_comment

begin_class
DECL|class|ScriptBytesValues
specifier|public
class|class
name|ScriptBytesValues
extends|extends
name|SortingBinaryDocValues
implements|implements
name|ScorerAware
block|{
DECL|field|script
specifier|private
specifier|final
name|LeafSearchScript
name|script
decl_stmt|;
DECL|method|ScriptBytesValues
specifier|public
name|ScriptBytesValues
parameter_list|(
name|LeafSearchScript
name|script
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
block|}
DECL|method|set
specifier|private
name|void
name|set
parameter_list|(
name|int
name|i
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|i
index|]
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|values
index|[
name|i
index|]
operator|.
name|copyChars
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|script
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
specifier|final
name|Object
name|value
init|=
name|script
operator|.
name|run
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|count
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|count
operator|=
name|Array
operator|.
name|getLength
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|grow
argument_list|()
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
name|count
condition|;
operator|++
name|i
control|)
block|{
name|set
argument_list|(
name|i
argument_list|,
name|Array
operator|.
name|get
argument_list|(
name|value
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Collection
condition|)
block|{
specifier|final
name|Collection
argument_list|<
name|?
argument_list|>
name|coll
init|=
operator|(
name|Collection
argument_list|<
name|?
argument_list|>
operator|)
name|value
decl_stmt|;
name|count
operator|=
name|coll
operator|.
name|size
argument_list|()
expr_stmt|;
name|grow
argument_list|()
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Object
name|v
range|:
name|coll
control|)
block|{
name|set
argument_list|(
name|i
operator|++
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|count
operator|=
literal|1
expr_stmt|;
name|set
argument_list|(
literal|0
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|sort
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|script
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

