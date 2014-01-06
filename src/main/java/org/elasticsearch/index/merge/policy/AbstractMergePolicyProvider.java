begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.merge.policy
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|merge
operator|.
name|policy
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
name|MergePolicy
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
name|index
operator|.
name|shard
operator|.
name|AbstractIndexShardComponent
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
name|store
operator|.
name|Store
import|;
end_import

begin_class
DECL|class|AbstractMergePolicyProvider
specifier|public
specifier|abstract
class|class
name|AbstractMergePolicyProvider
parameter_list|<
name|MP
extends|extends
name|MergePolicy
parameter_list|>
extends|extends
name|AbstractIndexShardComponent
implements|implements
name|MergePolicyProvider
argument_list|<
name|MP
argument_list|>
block|{
DECL|field|INDEX_COMPOUND_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_COMPOUND_FORMAT
init|=
literal|"index.compound_format"
decl_stmt|;
DECL|field|noCFSRatio
specifier|protected
specifier|volatile
name|double
name|noCFSRatio
decl_stmt|;
DECL|method|AbstractMergePolicyProvider
specifier|protected
name|AbstractMergePolicyProvider
parameter_list|(
name|Store
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|store
operator|.
name|shardId
argument_list|()
argument_list|,
name|store
operator|.
name|indexSettings
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|noCFSRatio
operator|=
name|parseNoCFSRatio
argument_list|(
name|indexSettings
operator|.
name|get
argument_list|(
name|INDEX_COMPOUND_FORMAT
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|store
operator|.
name|suggestUseCompoundFile
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|parseNoCFSRatio
specifier|public
specifier|static
name|double
name|parseNoCFSRatio
parameter_list|(
name|String
name|noCFSRatio
parameter_list|)
block|{
name|noCFSRatio
operator|=
name|noCFSRatio
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|noCFSRatio
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
condition|)
block|{
return|return
literal|1.0d
return|;
block|}
elseif|else
if|if
condition|(
name|noCFSRatio
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"false"
argument_list|)
condition|)
block|{
return|return
literal|0.0
return|;
block|}
else|else
block|{
try|try
block|{
name|double
name|value
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|noCFSRatio
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
argument_list|<
literal|0.0
operator|||
name|value
argument_list|>
literal|1.0
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"NoCFSRatio must be in the interval [0..1] but was: ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|value
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Expected a boolean or a value in the interval [0..1] but was: ["
operator|+
name|noCFSRatio
operator|+
literal|"]"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|formatNoCFSRatio
specifier|public
specifier|static
name|String
name|formatNoCFSRatio
parameter_list|(
name|double
name|ratio
parameter_list|)
block|{
if|if
condition|(
name|ratio
operator|==
literal|1.0
condition|)
block|{
return|return
name|Boolean
operator|.
name|TRUE
operator|.
name|toString
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|ratio
operator|==
literal|0.0
condition|)
block|{
return|return
name|Boolean
operator|.
name|FALSE
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|ratio
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

