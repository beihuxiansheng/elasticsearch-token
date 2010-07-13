begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_comment
comment|// LUCENE TRACK
end_comment

begin_class
DECL|class|ShardFieldDocSortedHitQueue
specifier|public
class|class
name|ShardFieldDocSortedHitQueue
extends|extends
name|FieldDocSortedHitQueue
block|{
DECL|method|ShardFieldDocSortedHitQueue
specifier|public
name|ShardFieldDocSortedHitQueue
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|setFields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
DECL|method|setFields
annotation|@
name|Override
specifier|public
name|void
name|setFields
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|)
block|{
name|super
operator|.
name|setFields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

