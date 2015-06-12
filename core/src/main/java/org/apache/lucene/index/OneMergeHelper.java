begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Allows pkg private access  */
end_comment

begin_class
DECL|class|OneMergeHelper
specifier|public
class|class
name|OneMergeHelper
block|{
DECL|method|OneMergeHelper
specifier|private
name|OneMergeHelper
parameter_list|()
block|{}
DECL|method|getSegmentName
specifier|public
specifier|static
name|String
name|getSegmentName
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
block|{
return|return
name|merge
operator|.
name|info
operator|!=
literal|null
condition|?
name|merge
operator|.
name|info
operator|.
name|info
operator|.
name|name
else|:
literal|"_na_"
return|;
block|}
block|}
end_class

end_unit

