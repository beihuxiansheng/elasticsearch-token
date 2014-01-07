begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.benchmark.scripts.score.script
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|benchmark
operator|.
name|scripts
operator|.
name|score
operator|.
name|script
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
name|script
operator|.
name|AbstractSearchScript
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
name|NativeScriptFactory
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
name|IndexFieldTerm
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
name|IndexField
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
name|ArrayList
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
DECL|class|NativeNaiveTFIDFScoreScript
specifier|public
class|class
name|NativeNaiveTFIDFScoreScript
extends|extends
name|AbstractSearchScript
block|{
DECL|field|NATIVE_NAIVE_TFIDF_SCRIPT_SCORE
specifier|public
specifier|static
specifier|final
name|String
name|NATIVE_NAIVE_TFIDF_SCRIPT_SCORE
init|=
literal|"native_naive_tfidf_script_score"
decl_stmt|;
DECL|field|field
name|String
name|field
init|=
literal|null
decl_stmt|;
DECL|field|terms
name|String
index|[]
name|terms
init|=
literal|null
decl_stmt|;
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
implements|implements
name|NativeScriptFactory
block|{
annotation|@
name|Override
DECL|method|newScript
specifier|public
name|ExecutableScript
name|newScript
parameter_list|(
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
return|return
operator|new
name|NativeNaiveTFIDFScoreScript
argument_list|(
name|params
argument_list|)
return|;
block|}
block|}
DECL|method|NativeNaiveTFIDFScoreScript
specifier|private
name|NativeNaiveTFIDFScoreScript
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
name|params
operator|.
name|entrySet
argument_list|()
expr_stmt|;
name|terms
operator|=
operator|new
name|String
index|[
name|params
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|field
operator|=
name|params
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|Object
name|o
init|=
name|params
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|arrayList
init|=
operator|(
name|ArrayList
argument_list|<
name|String
argument_list|>
operator|)
name|o
decl_stmt|;
name|terms
operator|=
name|arrayList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|arrayList
operator|.
name|size
argument_list|()
index|]
argument_list|)
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
name|float
name|score
init|=
literal|0
decl_stmt|;
name|IndexField
name|indexField
init|=
name|indexLookup
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexFieldTerm
name|indexFieldTerm
init|=
name|indexField
operator|.
name|get
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|indexFieldTerm
operator|.
name|tf
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|score
operator|+=
name|indexFieldTerm
operator|.
name|tf
argument_list|()
operator|*
name|indexField
operator|.
name|docCount
argument_list|()
operator|/
name|indexFieldTerm
operator|.
name|df
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
block|}
return|return
name|score
return|;
block|}
block|}
end_class

end_unit

