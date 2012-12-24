begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.child
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|child
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
import|;
end_import

begin_comment
comment|/**  * Defines how scores from child documents are mapped into the parent document.  */
end_comment

begin_enum
DECL|enum|ScoreType
specifier|public
enum|enum
name|ScoreType
block|{
comment|/**      * Only the highest score of all matching child documents is mapped into the parent.      */
DECL|enum constant|MAX
name|MAX
block|,
comment|/**      * The average score based on all matching child documents are mapped into the parent.      */
DECL|enum constant|AVG
name|AVG
block|,
comment|/**      * The matching children scores is summed up and mapped into the parent.      */
DECL|enum constant|SUM
name|SUM
block|;
DECL|method|fromString
specifier|public
specifier|static
name|ScoreType
name|fromString
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
literal|"max"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|MAX
return|;
block|}
elseif|else
if|if
condition|(
literal|"avg"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|AVG
return|;
block|}
elseif|else
if|if
condition|(
literal|"sum"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|SUM
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No score type for child query ["
operator|+
name|type
operator|+
literal|"] found"
argument_list|)
throw|;
block|}
block|}
end_enum

end_unit

