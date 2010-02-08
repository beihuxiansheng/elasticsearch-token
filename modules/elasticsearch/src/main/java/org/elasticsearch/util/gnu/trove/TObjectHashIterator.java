begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.gnu.trove
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gnu
operator|.
name|trove
package|;
end_package

begin_comment
comment|/**  * Created: Wed Nov 28 21:30:53 2001  *  * @author Eric D. Friedman  * @version $Id: TObjectHashIterator.java,v 1.2 2006/11/10 23:27:56 robeden Exp $  */
end_comment

begin_class
DECL|class|TObjectHashIterator
class|class
name|TObjectHashIterator
parameter_list|<
name|E
parameter_list|>
extends|extends
name|THashIterator
argument_list|<
name|E
argument_list|>
block|{
DECL|field|_objectHash
specifier|protected
specifier|final
name|TObjectHash
argument_list|<
name|E
argument_list|>
name|_objectHash
decl_stmt|;
DECL|method|TObjectHashIterator
specifier|public
name|TObjectHashIterator
parameter_list|(
name|TObjectHash
argument_list|<
name|E
argument_list|>
name|hash
parameter_list|)
block|{
name|super
argument_list|(
name|hash
argument_list|)
expr_stmt|;
name|_objectHash
operator|=
name|hash
expr_stmt|;
block|}
DECL|method|objectAtIndex
specifier|protected
name|E
name|objectAtIndex
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
operator|(
name|E
operator|)
name|_objectHash
operator|.
name|_set
index|[
name|index
index|]
return|;
block|}
block|}
end_class

begin_comment
comment|// TObjectHashIterator
end_comment

end_unit

