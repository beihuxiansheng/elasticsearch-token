begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.river.wikipedia.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|river
operator|.
name|wikipedia
operator|.
name|support
package|;
end_package

begin_comment
comment|/**  * A class abstracting Wiki infobox  *  * @author Delip Rao  */
end_comment

begin_class
DECL|class|InfoBox
specifier|public
class|class
name|InfoBox
block|{
DECL|field|infoBoxWikiText
name|String
name|infoBoxWikiText
init|=
literal|null
decl_stmt|;
DECL|method|InfoBox
name|InfoBox
parameter_list|(
name|String
name|infoBoxWikiText
parameter_list|)
block|{
name|this
operator|.
name|infoBoxWikiText
operator|=
name|infoBoxWikiText
expr_stmt|;
block|}
DECL|method|dumpRaw
specifier|public
name|String
name|dumpRaw
parameter_list|()
block|{
return|return
name|infoBoxWikiText
return|;
block|}
block|}
end_class

end_unit

